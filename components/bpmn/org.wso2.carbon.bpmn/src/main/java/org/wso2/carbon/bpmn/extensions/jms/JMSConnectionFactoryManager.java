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

import java.util.*;

/**
 * Created by dilini on 12/11/15.
 */
public class JMSConnectionFactoryManager {
    private static JMSConnectionFactoryManager connectionFactoryManager = null;

    private final Map<String, JMSConnectionFactory> connectionFactories = new HashMap<>();

    public JMSConnectionFactoryManager(){
    }

    public static synchronized JMSConnectionFactoryManager getInstance(){
        if(connectionFactoryManager == null){
            connectionFactoryManager = new JMSConnectionFactoryManager();
        }
        return connectionFactoryManager;
    }

    public void initializeConnectionFactories(HashMap<String, Hashtable<String, String>> parameterList){
        Set<String> keys = parameterList.keySet();
        Iterator<String> keyIterator = keys.iterator();
        String key;
        while (keyIterator.hasNext()){
            key = keyIterator.next();
            connectionFactories.put(key, new JMSConnectionFactory(parameterList.get(key)));
        }
    }

    public JMSConnectionFactory getConnectionFactory(String jmsProviderID){
        return connectionFactories.get(jmsProviderID);
    }

    public void stop(){
        for(JMSConnectionFactory jcf : connectionFactories.values()){
            jcf.stop();
        }
    }
}
