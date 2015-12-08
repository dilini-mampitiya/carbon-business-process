package org.wso2.carbon.bpmn.extensions.jms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Properties;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.Context;
import javax.naming.NamingException;

/**
 * Created by dilini on 11/30/15.
 */
public class JMSInvoker {

    private static final Log log = LogFactory.getLog(JMSInvoker.class);

    private Context context = null;


    public JMSInvoker(){

    }

    public void sendMessage(String payload, Properties properties, String queueName){
        try {
            context = new InitialContext(properties);

            QueueConnectionFactory factory = (QueueConnectionFactory) context.lookup("QueueConnectionFactory");

            QueueConnection connection = factory.createQueueConnection();

            connection.start();

            QueueSession session = connection.createQueueSession(false, QueueSession.AUTO_ACKNOWLEDGE);

            Queue queue = (Queue) context.lookup(queueName);

            MessageProducer producer = session.createProducer(queue);

            TextMessage message = session.createTextMessage();
            message.setText(payload);

            producer.send(message);

            if (log.isTraceEnabled()) {
                log.trace("Sent a message to " + queueName + " - Input payload: " + payload);
            }

            if(connection != null){
                connection.close();
            }

        }catch (NamingException e){
            e.printStackTrace();
        }catch (JMSException e){
            e.printStackTrace();
        }
    }

    public void publishMessage(String payload, Properties properties, String topicName){
        try {
            context = new InitialContext(properties);

            TopicConnectionFactory factory = (TopicConnectionFactory) context.lookup("TopicConnectionFactory");

            TopicConnection connection = factory.createTopicConnection();

            connection.start();

            TopicSession session = connection.createTopicSession(false, TopicSession.AUTO_ACKNOWLEDGE);

            Topic topic = (Topic) context.lookup(topicName);

            TopicPublisher publisher = session.createPublisher(topic);

            TextMessage textMessage = session.createTextMessage();
            textMessage.setText(payload);

            publisher.publish(textMessage);

            if (log.isTraceEnabled()) {
                log.trace("Published a message to " + topicName + " - Input payload: " + payload);
            }

            if(connection != null){
                connection.close();
            }

        }catch (JMSException e){
            log.error(e.getMessage());
        }catch (NamingException e){
            log.error(e.getMessage());
        }
    }
}