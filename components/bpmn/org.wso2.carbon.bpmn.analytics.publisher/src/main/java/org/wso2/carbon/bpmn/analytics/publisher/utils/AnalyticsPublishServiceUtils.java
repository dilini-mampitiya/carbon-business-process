/**
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.carbon.bpmn.analytics.publisher.utils;

import org.activiti.engine.HistoryService;
import org.activiti.engine.history.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.bpmn.analytics.publisher.AnalyticsPublisherConstants;
import org.wso2.carbon.bpmn.analytics.publisher.models.BPMNProcessInstance;
import org.wso2.carbon.bpmn.analytics.publisher.models.BPMNTaskInstance;

import org.wso2.carbon.bpmn.core.BPMNServerHolder;
import org.wso2.carbon.bpmn.core.mgt.model.BPMNVariable;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.context.RegistryType;
import org.wso2.carbon.registry.api.Registry;
import org.wso2.carbon.registry.api.RegistryException;
import org.wso2.carbon.registry.api.Resource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * AnalyticsPublishServiceUtils is used by the AnalyticsPublisher to fetch the BPMN process instances and the task instances
 */
public class AnalyticsPublishServiceUtils {
    private static final Log log = LogFactory.getLog(AnalyticsPublishServiceUtils.class);

    /**
     * Get completed process instances which were finished after the given date and time
     *
     * @return BPMNProcessInstance array if the historic process instance list is not null
     */
    public BPMNProcessInstance[] getCompletedProcessInstances() {
        HistoryService historyService =
                BPMNServerHolder.getInstance().getEngine().getHistoryService();
        HistoricProcessInstanceQuery instanceQuery =
                historyService.createHistoricProcessInstanceQuery();
        List<HistoricProcessInstance> historicProcessInstanceList = null;
        String time = readPublishTimeFromRegistry(AnalyticsPublisherConstants.PROCESS_RESOURCE_PATH,
                AnalyticsPublisherConstants.LAST_PROCESS_INSTANCE_PUBLISH_TIME);
        if (time == null) {
            if (instanceQuery.finished().list().size() != 0) {
                // if the stored time is null in the registry file then send all completed process instances.
                historicProcessInstanceList =
                        instanceQuery.finished().orderByProcessInstanceStartTime().asc().list();
            }
        } else {
            Date timeInDateFormat = DateConverter.convertStringToDate(time);
            int listSize = instanceQuery.finished().startedAfter(timeInDateFormat).list().size();
            if (listSize != 0) {
                /*When using the startedAfter() method it returns the finished objects according to the time stored of
                  last completed instance. But if the list length is one then it always return the same object in the
                  list twice from the last updated time which stored in the carbon registry.
                  (avoid to return same object repeatedly if the list has only one object)*/
                if (listSize == 1) {
                    return null;
                }
                //send the process instances which were finished after the given date/time in registry
                historicProcessInstanceList =
                        instanceQuery.finished().startedAfter(timeInDateFormat)
                                .orderByProcessInstanceStartTime().asc().listPage(1, listSize);
            }
        }
        if (historicProcessInstanceList != null) {
            if (log.isDebugEnabled()) {
                log.debug("Write the published time of the last BPMN process instance to the carbon registry..." + historicProcessInstanceList.toString());
            }
            /*write the last published time to the registry because lets say as an example when a new process is completed,
              then the attributes belong to that process instance should be published to the DAS and if we are not stored
              the last published time, then all the completed processes which were previously published are also re-published
              to the DAS.*/
            writePublishTimeToRegistry(historicProcessInstanceList);
            //return ProcessInstances set as BPMNProcessInstance array
            return getBPMNProcessInstances(historicProcessInstanceList);
        }
        return null;
    }

    /**
     * Get completed task instances which were finished after the given date and time
     *
     * @return BPMNTaskInstance array if the historic task instance list is not null
     */
    public BPMNTaskInstance[] getCompletedTaskInstances() {
        HistoryService historyService =
                BPMNServerHolder.getInstance().getEngine().getHistoryService();
        HistoricTaskInstanceQuery instanceQuery = historyService.createHistoricTaskInstanceQuery();
        List<HistoricTaskInstance> historicTaskInstanceList = null;
        String time = readPublishTimeFromRegistry(AnalyticsPublisherConstants.TASK_RESOURCE_PATH,
                AnalyticsPublisherConstants.LAST_TASK_INSTANCE_END_TIME);
        if (time == null) {
            if (instanceQuery.finished().list().size() != 0) {
                historicTaskInstanceList =
                        instanceQuery.finished().orderByHistoricTaskInstanceEndTime().asc().list();
            }
        } else {
            Date dateFormat = DateConverter.convertStringToDate(time);
            int listSize = instanceQuery.finished().taskCompletedAfter(dateFormat).list().size();
            if (listSize != 0) {
                /*When using the startedAfter() method it returns the finished objects according to the time stored of
                  last completed instance. But if the list length is one then it always return the same object in the
                  list twice from the last updated time which stored in the carbon registry.
                  (avoid to return same object repeatedly if the list has only one object)*/
                if (listSize == 1) {
                    return null;
                }
                historicTaskInstanceList = instanceQuery.finished().taskCompletedAfter(dateFormat)
                        .orderByHistoricTaskInstanceEndTime().asc()
                        .listPage(1, listSize);
            }
        }
        if (historicTaskInstanceList != null) {
            if (log.isDebugEnabled()) {
                log.debug("Write BPMN task instance to the carbon registry..." + historicTaskInstanceList.toString());
            }
            writeTaskEndTimeToRegistry(historicTaskInstanceList);
            return getBPMNTaskInstances(historicTaskInstanceList);
        }
        return null;
    }

    /**
     * Convert historic process instances to BPMN process instances
     *
     * @param historicProcessInstanceList List of historic process instances
     * @return BPMNProcessInstance array
     */
    private BPMNProcessInstance[] getBPMNProcessInstances(
            List<HistoricProcessInstance> historicProcessInstanceList) {
        BPMNProcessInstance bpmnProcessInstance;
        List<BPMNProcessInstance> bpmnProcessInstances = new ArrayList<>();
        for (HistoricProcessInstance instance : historicProcessInstanceList) {
            bpmnProcessInstance = new BPMNProcessInstance();
            bpmnProcessInstance.setProcessDefinitionId(instance.getProcessDefinitionId());
            bpmnProcessInstance.setTenantId(instance.getTenantId());
            bpmnProcessInstance.setName(instance.getName());
            bpmnProcessInstance.setInstanceId(instance.getId());
            bpmnProcessInstance.setBusinessKey(instance.getBusinessKey());
            bpmnProcessInstance.setStartTime(instance.getStartTime());
            bpmnProcessInstance.setEndTime(instance.getEndTime());
            bpmnProcessInstance.setDuration(instance.getDurationInMillis());
            bpmnProcessInstance.setStartUserId(instance.getStartUserId());
            bpmnProcessInstance.setStartActivityId(instance.getStartActivityId());
            bpmnProcessInstance.setVariables(formatVariables(instance.getProcessVariables()));
            bpmnProcessInstances.add(bpmnProcessInstance);
        }
        return bpmnProcessInstances.toArray(new BPMNProcessInstance[bpmnProcessInstances.size()]);
    }

    /**
     * convert historic task instances to BPMN task instances
     *
     * @param historicTaskInstanceList List of historic task instances
     * @return BPMNTaskInstance array
     */
    private BPMNTaskInstance[] getBPMNTaskInstances(
            List<HistoricTaskInstance> historicTaskInstanceList) {
        BPMNTaskInstance bpmnTaskInstance;
        List<BPMNTaskInstance> bpmnTaskInstances = new ArrayList<>();
        for (HistoricTaskInstance taskInstance : historicTaskInstanceList) {
            bpmnTaskInstance = new BPMNTaskInstance();
            bpmnTaskInstance.setTaskDefinitionKey(taskInstance.getTaskDefinitionKey());
            bpmnTaskInstance.setTaskInstanceId(taskInstance.getId());
            bpmnTaskInstance.setAssignee(taskInstance.getAssignee());
            bpmnTaskInstance.setStartTime(taskInstance.getStartTime());
            bpmnTaskInstance.setEndTime(taskInstance.getEndTime());
            bpmnTaskInstance.setTaskName(taskInstance.getName());
            bpmnTaskInstance.setDurationInMills(taskInstance.getDurationInMillis());
            bpmnTaskInstance.setCreateTime(taskInstance.getCreateTime());
            bpmnTaskInstance.setOwner(taskInstance.getOwner());
            bpmnTaskInstance.setProcessInstanceId(taskInstance.getProcessInstanceId());
            bpmnTaskInstances.add(bpmnTaskInstance);
        }
        return bpmnTaskInstances.toArray(new BPMNTaskInstance[bpmnTaskInstances.size()]);
    }

    /**
     * Format the process instance variables as BPMNVariable objects
     *
     * @param processVariables variables which belongs to a given process instance as key, value pairs
     * @return BPMNVariable objects array
     */
    private BPMNVariable[] formatVariables(Map<String, Object> processVariables) {
        if (processVariables == null) {
            return null;
        }
        BPMNVariable[] vars = new BPMNVariable[processVariables.size()];
        int currentVar = 0;
        for (Map.Entry entry : processVariables.entrySet()) {
            vars[currentVar] = new BPMNVariable(entry.getKey().toString(),
                    processVariables.get(entry.getKey().toString())
                            .toString());
            currentVar++;
        }
        return vars;
    }

    /**
     * Write last completed process instance end time to carbon registry
     *
     * @param historicProcessInstanceList List of historic process instances
     */
    private void writePublishTimeToRegistry(
            List<HistoricProcessInstance> historicProcessInstanceList) {
        if (log.isDebugEnabled()) {
            log.debug("Start writing last completed process instance publish time...");
        }
        Date lastProcessInstancePublishTime =
                historicProcessInstanceList.get(historicProcessInstanceList.size() - 1)
                        .getStartTime();
        try {
            PrivilegedCarbonContext context = PrivilegedCarbonContext.getThreadLocalCarbonContext();
            Registry registry = context.getRegistry(RegistryType.SYSTEM_GOVERNANCE);
            Resource resource;
            if (!registry.resourceExists(AnalyticsPublisherConstants.PROCESS_RESOURCE_PATH)) {
                resource = registry.newResource();
                resource.addProperty(AnalyticsPublisherConstants.LAST_PROCESS_INSTANCE_PUBLISH_TIME,
                        String.valueOf(lastProcessInstancePublishTime));
                registry.put(AnalyticsPublisherConstants.PROCESS_RESOURCE_PATH, resource);
            } else {
                resource = registry.get(AnalyticsPublisherConstants.PROCESS_RESOURCE_PATH);
                resource.setProperty(AnalyticsPublisherConstants.LAST_PROCESS_INSTANCE_PUBLISH_TIME,
                        String.valueOf(lastProcessInstancePublishTime));
                registry.put(AnalyticsPublisherConstants.PROCESS_RESOURCE_PATH, resource);
            }
            if (log.isDebugEnabled()) {
                log.debug("End of writing last completed process instance publish time...");
            }
        } catch (RegistryException e) {
            String errMsg = "Registry error while writing the process instance publish time.";
            log.error(errMsg, e);
        }
    }

    /**
     * Write last completed task instance end time to carbon registry
     *
     * @param historicTaskInstanceList List of historic task instances
     */
    private void writeTaskEndTimeToRegistry(List<HistoricTaskInstance> historicTaskInstanceList) {
        if (log.isDebugEnabled()) {
            log.debug("Start writing last completed task instance end time...");
        }
        Date lastTaskInstanceDate =
                historicTaskInstanceList.get(historicTaskInstanceList.size() - 1).getEndTime();
        try {
            PrivilegedCarbonContext privilegedContext =
                    PrivilegedCarbonContext.getThreadLocalCarbonContext();
            Registry registry = privilegedContext.getRegistry(RegistryType.SYSTEM_GOVERNANCE);
            Resource resource;
            if (!registry.resourceExists(AnalyticsPublisherConstants.TASK_RESOURCE_PATH)) {
                resource = registry.newResource();
                resource.addProperty(AnalyticsPublisherConstants.LAST_TASK_INSTANCE_END_TIME,
                        String.valueOf(lastTaskInstanceDate));
                registry.put(AnalyticsPublisherConstants.TASK_RESOURCE_PATH, resource);
            } else {
                resource = registry.get(AnalyticsPublisherConstants.TASK_RESOURCE_PATH);
                resource.setProperty(AnalyticsPublisherConstants.LAST_TASK_INSTANCE_END_TIME,
                        String.valueOf(lastTaskInstanceDate));
                registry.put(AnalyticsPublisherConstants.TASK_RESOURCE_PATH, resource);
            }
            if (log.isDebugEnabled()) {
                log.debug("End of writing last completed task instance end time...");
            }
        } catch (RegistryException e) {
            String errMsg = "Registry error while writing the task instance end time.";
            log.error(errMsg, e);
        }
    }

    /**
     * Read last completed process/task instance end time from carbon registry
     *
     * @param resourcePath resource path in the carbon registry
     * @param propertyPath property path in the resource
     * @return the end time of last completed process/task instance
     */

    private String readPublishTimeFromRegistry(String resourcePath, String propertyPath) {
        String time = null;
        try {
            PrivilegedCarbonContext carbonContext =
                    PrivilegedCarbonContext.getThreadLocalCarbonContext();
            Registry registry = carbonContext.getRegistry(RegistryType.SYSTEM_GOVERNANCE);
            if (registry != null) {
                if (registry.resourceExists(resourcePath)) {
                    if (log.isDebugEnabled()) {
                        log.debug("Process instance resource path exists..." + resourcePath);
                    }
                    Resource resource = registry.get(resourcePath);
                    time = resource.getProperty(propertyPath);
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Registry is null...");
                }
                throw new RegistryException("Registry is null");
            }
        } catch (RegistryException e) {
            String errMsg = "Registry error while reading the process instance publish time.";
            log.error(errMsg, e);
        }
        return time;
    }
}


