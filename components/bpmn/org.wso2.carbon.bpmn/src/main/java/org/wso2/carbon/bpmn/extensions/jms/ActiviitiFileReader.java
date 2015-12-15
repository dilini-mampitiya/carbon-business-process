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

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.bpmn.core.BPMNConstants;
import org.wso2.carbon.utils.CarbonUtils;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

/**
 * Created by dilini on 12/9/15.
 */
public class ActiviitiFileReader {

    private static final Log log = LogFactory.getLog(ActiviitiFileReader.class);

    /**
     *
     * @param jmsProviderID
     * @return
     */
    public static Hashtable<String, String> readJMSProviderInformation(String jmsProviderID){
        Hashtable<String, String> beanPropertyList = new Hashtable<>();

        try {
            String carbonConfigDirPath = CarbonUtils.getCarbonConfigDirPath();
            String activitiConfigPath = carbonConfigDirPath + File.separator + BPMNConstants.ACTIVITI_CONFIGURATION_FILE_NAME;
            File configFile = new File(activitiConfigPath);
            String configContent = FileUtils.readFileToString(configFile);
            OMElement configElement = AXIOMUtil.stringToOM(configContent);

            Iterator beans = configElement.getChildrenWithName(new QName("http://www.springframework.org/schema/beans", "bean"));
            while (beans.hasNext()) {
                OMElement bean = (OMElement) beans.next();
                String beanId = bean.getAttributeValue(new QName(null, "id"));
                if (beanId.equals(jmsProviderID)) {
                    Iterator beanProps = bean.getChildrenWithName(new QName("http://www.springframework.org/schema/beans", "property"));
                    while (beanProps.hasNext()) {
                        OMElement beanProp = (OMElement) beanProps.next();
                        switch (beanProp.getAttributeValue(new QName(null, "name"))){
                            case BPMNConstants.JMS_INITIAL_CONTEXT_FACTORY:
                                String initContextFactory = beanProp.getAttributeValue(new QName(null, "value"));
                                beanPropertyList.put(JMSConstants.NAMING_FACTORY_INITIAL, initContextFactory);
                                break;
                            case BPMNConstants.JMS_PROVIDER_URL:
                                String provURL = beanProp.getAttributeValue(new QName(null, "value"));
                                beanPropertyList.put(JMSConstants.JMS_PROVIDER_URL, provURL);
                                break;
                        }
                    }
                }
            }
        }catch (IOException e){
            log.error(e.getMessage());
        } catch (XMLStreamException e) {
            log.error(e.getMessage());
        }
        beanPropertyList.put(JMSConstants.JMS_CONNECTION_FACTORY_JNDI_NAME, "ConnectionFactory");
        return beanPropertyList;
    }

    public static List<String> readJMSProviderIDList(){
        List <String> providerIDList = new ArrayList<>();

        try {
            String carbonConfigDirPath = CarbonUtils.getCarbonConfigDirPath();
            String activitiConfigPath = carbonConfigDirPath + File.separator + BPMNConstants.ACTIVITI_CONFIGURATION_FILE_NAME;
            File configFile = new File(activitiConfigPath);
            String configContent = FileUtils.readFileToString(configFile);
            OMElement configElement = AXIOMUtil.stringToOM(configContent);

            Iterator beans = configElement.getChildrenWithName(new QName("http://www.springframework.org/schema/beans", "bean"));
            while (beans.hasNext()) {
                OMElement bean = (OMElement) beans.next();
                String beanId = bean.getAttributeValue(new QName(null, "id"));
                providerIDList.add(beanId);
            }
        } catch (XMLStreamException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return providerIDList;
    }
}
