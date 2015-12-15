/*
 * Copyright 2005-2015 WSO2, Inc. (http://wso2.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.bpmn.extensions.jms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dilini on 12/10/15.
 */
public class JMSConnectionFactory {
    private static final Log log = LogFactory.getLog(JMSConnectionFactory.class);

    /** the list of parameters from activiti.xml definition*/
    private Hashtable<String, String> parameters = new Hashtable<>();

    /** InitialContext reference*/
    private Context context = null;

    /** The JMS ConnectionFactory to create connection objects*/
    private ConnectionFactory connectionFactory = null;

    /** The shared JMS Connection for the JMS connection factory*/
    private Connection sharedConnection = null;

    private Session sharedSession = null;

    /** The shared JMS MessageProducer for the JMS connection factory*/
    private MessageProducer sharedProducer = null;

    /** The shared JMS Destination for the JMS connection factory*/
    private Destination sharedDestination = null;

    private int cacheLevel = JMSConstants.CACHE_CONNECTION;

    private int lastReturnedConnectionIndex = 0;

    private Map<Integer, Connection> sharedConnectionMap = new ConcurrentHashMap<>();

    private int maxSharedConnectionCount = 10;

    JMSUtils utils = new JMSUtils();

    /**
     *
     */
    public JMSConnectionFactory(Hashtable<String, String> parameters){
        this.parameters = parameters;

        digestCacheLevel();

        try {
            Properties properties = new Properties();
            properties.put(Context.INITIAL_CONTEXT_FACTORY, parameters.get(JMSConstants.JMS_CONNECTION_FACTORY_JNDI_NAME));
            properties.put(Context.PROVIDER_URL, parameters.get(JMSConstants.JMS_PROVIDER_URL));
            context = new InitialContext(parameters);

            connectionFactory = utils.lookup(context, ConnectionFactory.class,
                    parameters.get(JMSConstants.JMS_CONNECTION_FACTORY_JNDI_NAME));

            if(parameters.get(JMSConstants.PARAM_DESTINATION) != null){
                sharedDestination = utils.lookup(context, Destination.class,
                        parameters.get(JMSConstants.PARAM_DESTINATION));
            }

            log.info("JMS ConnectionFactory initialized for: " + parameters.get(JMSConstants.JMS_CONNECTION_FACTORY_JNDI_NAME));
        }catch (NamingException e){
            String errorMsg = "Cannot acquire JNDI context, JMS Connection factory : " +
                    parameters.get(JMSConstants.JMS_CONNECTION_FACTORY_JNDI_NAME) + " or default destination : " +
                    parameters.get(JMSConstants.PARAM_DESTINATION) +
                    " using : " + parameters;
            log.error(errorMsg);
        }
    }

    private void digestCacheLevel(){
        String value = parameters.get(JMSConstants.PARAM_CACHE_LEVEL);

        if("none".equalsIgnoreCase(value)){
            this.cacheLevel = JMSConstants.CACHE_NONE;
        }else if("connection".equalsIgnoreCase(value)){
            this.cacheLevel = JMSConstants.CACHE_CONNECTION;
        }else if("session".equalsIgnoreCase(value)){
            this.cacheLevel = JMSConstants.CACHE_SESSION;
        }else if("producer".equalsIgnoreCase(value)){
            this.cacheLevel = JMSConstants.CACHE_PRODUCER;
        }else if("consumer".equalsIgnoreCase(value)){
            this.cacheLevel = JMSConstants.CACHE_CONSUMER;
        }else if(value != null){
            String errorMsg = "Invalid cache level: " + value + " for JMS Connection Factory";
            log.error(errorMsg);
        }
    }

    /**
     *
     */
    public synchronized void stop(){
        if(sharedConnection != null){
            try {
                sharedConnection.close();
            } catch (JMSException e) {
                log.warn("Error while shutting down the connection factory", e);
            }
        }

        if(context != null){
            try {
                context.close();
            } catch (NamingException e) {
                log.warn("Error while closing the InitialContext", e);
            }
        }
    }

    /**
     *
     * @return
     */
    public Hashtable<String, String> getParameters(){
        return parameters;
    }

    /**
     *
     * @return
     */
    public Context getContext(){
        return context;
    }

    /**
     *
     * @return
     */
    public Destination getSharedDestination(){
        return sharedDestination;
    }

    /**
     *
     * @return
     */
    private synchronized Connection getSharedConnection(){
        Connection connection = sharedConnectionMap.get(lastReturnedConnectionIndex);

        if(connection == null){
            connection = createConnection();
            sharedConnectionMap.put(lastReturnedConnectionIndex++, connection);
        }
        if(lastReturnedConnectionIndex > maxSharedConnectionCount){
            lastReturnedConnectionIndex = 0;
        }

        return connection;
    }

    /**
     *
     * @return
     */
    private synchronized Session getSharedSession(){
        if(sharedSession == null){
            sharedSession = createSession(getSharedConnection());
            if(log.isDebugEnabled()){
                log.debug("Creating a shared session from JMS Connection");
            }
        }
        return sharedSession;
    }

    /**
     *
     * @param destinationName
     * @param destinationType
     * @return
     */
    public Destination getDestination(String destinationName, String destinationType){
        try {
            return utils.lookupDestination(context, destinationName, destinationType);
        } catch (NamingException e) {
            log.error("Error looking up the JMS destination with name " + destinationName
                    + " of type " + destinationType, e);
        }

        return null;
    }

    public Boolean isQueue(){
        if(parameters.get(JMSConstants.PARAM_DESTINATION_TYPE) == null){
            return false;
        }else{
            if(JMSConstants.DESTINATION_TYPE_QUEUE.equalsIgnoreCase(parameters.get(JMSConstants.PARAM_DESTINATION_TYPE))){
                return true;
            }else if(JMSConstants.DESTINATION_TYPE_TOPIC.equalsIgnoreCase(parameters.get(JMSConstants.PARAM_DESTINATION_TYPE))){
                return false;
            }else{
                String invalidDestTypeErrorMsg = "Invalid " + JMSConstants.PARAM_DESTINATION_TYPE + ": " +
                        parameters.get(JMSConstants.PARAM_DESTINATION_TYPE);
                log.error(invalidDestTypeErrorMsg);
                return null;
            }
        }
    }

    /**
     *
     * @return
     */
    private Connection createConnection(){
        Connection connection = null;
        try{
            /*isQueue is set to true here.*/
            connection = JMSUtils.createConnection(connectionFactory, parameters.get(JMSConstants.PARAM_USERNAME), parameters.get(JMSConstants.PARAM_PASSWORD), isQueue());

            if(log.isDebugEnabled()) {
                log.debug("New JMS Connection was created from the JMS ConnectionFactory");
            }
        }catch (JMSException e){
            log.error("Error creating a connection from the JMS Connection Factory using properties: " + parameters, e);
        }
        return connection;
    }

    /**
     *
     * @param connection
     * @return
     */
    private Session createSession(Connection connection){
        Session session = null;
        try{
            session = JMSUtils.createSession(connection, false, Session.AUTO_ACKNOWLEDGE, true);
            if(log.isDebugEnabled()){
                log.debug("New JMS Session was created from the JMS Connection");
            }
        }catch (JMSException e){
            log.error("Error creating a session from the JMS Connection using properties: " + parameters, e);
        }
        return session;
    }

    /**
     *
     * @param session
     * @param destination
     * @return
     */
    private MessageProducer createProducer(Session session, Destination destination){
        MessageProducer producer = null;
        try{
            producer = JMSUtils.createProducer(session, true, destination);
            if(log.isDebugEnabled()){
                log.debug("New JMS MessageProducer was created");
            }
        }catch (JMSException e){
            log.error("Error creating a message producer");
    }
        return producer;
    }

    /**
     *
     * @return
     */
    public Connection getConnection(){
        if(cacheLevel > JMSConstants.CACHE_NONE){
            return getSharedConnection();
        }else{
            return createConnection();
        }
    }

    /**
     *
     * @param connection
     * @return
     */
    public Session getSession(Connection connection){
        if(cacheLevel > JMSConstants.CACHE_CONNECTION){
            return getSharedSession();
        }else{
            if(connection == null){
                return createSession(getConnection());
            }else{
                return createSession(connection);
            }
        }
    }

    /**
     *
     * @param connection
     * @param session
     * @param destination
     * @return
     */
    public MessageProducer getMessageProducer(Connection connection, Session session, Destination destination){
        if(cacheLevel > JMSConstants.CACHE_SESSION){
            return getSharedProducer();
        }else{
            if(session == null){
                return createProducer(getSession(connection), destination);
            }else{
                return createProducer(session, destination);
            }
        }
    }

    /**
     *
     * @return
     */
    private synchronized MessageProducer getSharedProducer(){
        if(sharedProducer == null){
            sharedProducer = createProducer(getSharedSession(), sharedDestination);
            if(log.isDebugEnabled()){
                log.debug("Created a shared JMS MessageProducer");
            }
        }

        return sharedProducer;
    }
}
