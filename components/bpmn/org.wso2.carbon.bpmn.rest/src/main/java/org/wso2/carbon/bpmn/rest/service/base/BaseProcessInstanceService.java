/**
 *  Copyright (c) 2015 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.wso2.carbon.bpmn.rest.service.base;

import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.impl.ProcessInstanceQueryProperty;
import org.activiti.engine.query.QueryProperty;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.IdentityLink;
import org.wso2.carbon.bpmn.rest.model.common.DataResponse;
import org.wso2.carbon.bpmn.rest.common.RestResponseFactory;
import org.wso2.carbon.bpmn.rest.common.exception.BPMNConflictException;
import org.wso2.carbon.bpmn.rest.common.exception.BPMNOSGIServiceException;
import org.wso2.carbon.bpmn.rest.common.utils.BPMNOSGIService;
import org.wso2.carbon.bpmn.rest.engine.variable.QueryVariable;
import org.wso2.carbon.bpmn.rest.model.runtime.ProcessInstancePaginateList;
import org.wso2.carbon.bpmn.rest.model.runtime.ProcessInstanceQueryRequest;
import org.wso2.carbon.bpmn.rest.model.runtime.ProcessInstanceResponse;

import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseProcessInstanceService {

    protected static final String DEFAULT_ENCODING = "UTF-8";
    protected static final List<String> allPropertiesList  = new ArrayList<>();
    protected static Map<String, QueryProperty> allowedSortProperties = new HashMap<>();


    static {
        allPropertiesList.add("id");
        allPropertiesList.add("processDefinitionKey");
        allPropertiesList.add("processDefinitionId");
        allPropertiesList.add("businessKey");
        allPropertiesList.add("involvedUser");
        allPropertiesList.add("suspended");
        allPropertiesList.add("superProcessInstanceId");
        allPropertiesList.add("subProcessInstanceId");
        allPropertiesList.add("excludeSubprocesses");
        allPropertiesList.add("includeProcessVariables");
        allPropertiesList.add("tenantId");
        allPropertiesList.add("tenantIdLike");
        allPropertiesList.add("withoutTenantId");
        allPropertiesList.add("sort");
        allPropertiesList.add("start");
        allPropertiesList.add("size");
        allPropertiesList.add("order");
    }

    static {
        allowedSortProperties.put("processDefinitionId", ProcessInstanceQueryProperty.PROCESS_DEFINITION_ID);
        allowedSortProperties.put("processDefinitionKey", ProcessInstanceQueryProperty.PROCESS_DEFINITION_KEY);
        allowedSortProperties.put("id", ProcessInstanceQueryProperty.PROCESS_INSTANCE_ID);
        allowedSortProperties.put("tenantId", ProcessInstanceQueryProperty.TENANT_ID);
    }

    protected Map<String, String> allRequestParams(UriInfo uriInfo){
        Map<String, String> allRequestParams = new HashMap<>();

        for (String property:allPropertiesList){
            String value= uriInfo.getQueryParameters().getFirst(property);

            if(value != null){
                allRequestParams.put(property, value);
            }
        }

        return allRequestParams;
    }

    protected ProcessInstance getProcessInstanceFromRequest(String processInstanceId) {
        RuntimeService runtimeService = BPMNOSGIService.getRumtimeService();
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().
                processInstanceId(processInstanceId).singleResult();
        if (processInstance == null) {
            throw new ActivitiObjectNotFoundException(" Could not find a process instance with id " +
                    processInstanceId + "'.", ProcessInstance.class);
        }
        return processInstance;
    }

    protected Execution getExecutionInstanceFromRequest(String processInstanceId) {
        RuntimeService runtimeService = BPMNOSGIService.getRumtimeService();
        Execution execution = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if (execution == null) {
            throw new ActivitiObjectNotFoundException("Could not find a process instance with id '" +
                    processInstanceId + "'.", ProcessInstance.class);
        }
        return execution;
    }


    protected ProcessInstanceResponse activateProcessInstance(ProcessInstance processInstance, UriInfo uriInfo) {
        if (!processInstance.isSuspended()) {
            throw new BPMNConflictException("Process instance with id '" +
                    processInstance.getId() + "' is already active.");
        }

        RuntimeService runtimeService = BPMNOSGIService.getRumtimeService();
        runtimeService.activateProcessInstanceById(processInstance.getId());

        ProcessInstanceResponse response = new RestResponseFactory().createProcessInstanceResponse(processInstance,
                uriInfo.getBaseUri().toString());

        // No need to re-fetch the instance, just alter the suspended state of the result-object
        response.setSuspended(false);
        return response;
    }

    protected ProcessInstanceResponse suspendProcessInstance(ProcessInstance processInstance, RestResponseFactory
            restResponseFactory, UriInfo uriInfo) {
        if (processInstance.isSuspended()) {
            throw new BPMNConflictException("Process instance with id '" +
                    processInstance.getId() + "' is already suspended.");
        }

        RuntimeService runtimeService = BPMNOSGIService.getRumtimeService();
        runtimeService.suspendProcessInstanceById(processInstance.getId());

        ProcessInstanceResponse response = restResponseFactory.createProcessInstanceResponse(processInstance, uriInfo
                .getBaseUri().toString());

        // No need to re-fetch the instance, just alter the suspended state of the result-object
        response.setSuspended(true);
        return response;
    }

    protected DataResponse getQueryResponse(ProcessInstanceQueryRequest queryRequest,
                                            Map<String, String> requestParams, UriInfo uriInfo) {

        RuntimeService runtimeService = BPMNOSGIService.getRumtimeService();
        ProcessInstanceQuery query = runtimeService.createProcessInstanceQuery();

        // Populate query based on request
        if (queryRequest.getProcessInstanceId() != null) {
            query.processInstanceId(queryRequest.getProcessInstanceId());
        }
        if (queryRequest.getProcessDefinitionKey() != null) {
            query.processDefinitionKey(queryRequest.getProcessDefinitionKey());
        }
        if (queryRequest.getProcessDefinitionId() != null) {
            query.processDefinitionId(queryRequest.getProcessDefinitionId());
        }
        if (queryRequest.getProcessBusinessKey() != null) {
            query.processInstanceBusinessKey(queryRequest.getProcessBusinessKey());
        }
        if (queryRequest.getInvolvedUser() != null) {
            query.involvedUser(queryRequest.getInvolvedUser());
        }
        if (queryRequest.getSuspended() != null) {
            if (queryRequest.getSuspended()) {
                query.suspended();
            } else {
                query.active();
            }
        }
        if (queryRequest.getSubProcessInstanceId() != null) {
            query.subProcessInstanceId(queryRequest.getSubProcessInstanceId());
        }
        if (queryRequest.getSuperProcessInstanceId() != null) {
            query.superProcessInstanceId(queryRequest.getSuperProcessInstanceId());
        }
        if (queryRequest.getExcludeSubprocesses() != null) {
            query.excludeSubprocesses(queryRequest.getExcludeSubprocesses());
        }
        if (queryRequest.getIncludeProcessVariables() != null) {
            if (queryRequest.getIncludeProcessVariables()) {
                query.includeProcessVariables();
            }
        }
        if (queryRequest.getVariables() != null) {
            addVariables(query, queryRequest.getVariables());
        }

        if(queryRequest.getTenantId() != null) {
            query.processInstanceTenantId(queryRequest.getTenantId());
        }

        if(queryRequest.getTenantIdLike() != null) {
            query.processInstanceTenantIdLike(queryRequest.getTenantIdLike());
        }

        if(Boolean.TRUE.equals(queryRequest.getWithoutTenantId())) {
            query.processInstanceWithoutTenantId();
        }

        return new ProcessInstancePaginateList(new RestResponseFactory(), uriInfo)
                .paginateList(requestParams, queryRequest, query, "id", allowedSortProperties);
    }

    protected void addVariables(ProcessInstanceQuery processInstanceQuery, List<QueryVariable> variables) {

        RestResponseFactory restResponseFactory = new RestResponseFactory();
        for (QueryVariable variable : variables) {
            if (variable.getVariableOperation() == null) {
                throw new ActivitiIllegalArgumentException("Variable operation is missing for variable: " + variable.getName());
            }
            if (variable.getValue() == null) {
                throw new ActivitiIllegalArgumentException("Variable value is missing for variable: " + variable.getName());
            }

            boolean nameLess = variable.getName() == null;

            Object actualValue = restResponseFactory.getVariableValue(variable);

            // A value-only query is only possible using equals-operator
            if (nameLess && variable.getVariableOperation() != QueryVariable.QueryVariableOperation.EQUALS) {
                throw new ActivitiIllegalArgumentException("Value-only query (without a variable-name) is only supported when using 'equals' operation.");
            }

            switch (variable.getVariableOperation()) {

                case EQUALS:
                    if (nameLess) {
                        processInstanceQuery.variableValueEquals(actualValue);
                    } else {
                        processInstanceQuery.variableValueEquals(variable.getName(), actualValue);
                    }
                    break;

                case EQUALS_IGNORE_CASE:
                    if (actualValue instanceof String) {
                        processInstanceQuery.variableValueEqualsIgnoreCase(variable.getName(), (String) actualValue);
                    } else {
                        throw new ActivitiIllegalArgumentException("Only string variable values are supported when ignoring casing, but was: "
                                + actualValue.getClass().getName());
                    }
                    break;

                case NOT_EQUALS:
                    processInstanceQuery.variableValueNotEquals(variable.getName(), actualValue);
                    break;

                case NOT_EQUALS_IGNORE_CASE:
                    if (actualValue instanceof String) {
                        processInstanceQuery.variableValueNotEqualsIgnoreCase(variable.getName(), (String) actualValue);
                    } else {
                        throw new ActivitiIllegalArgumentException("Only string variable values are supported when ignoring casing, but was: "
                                + actualValue.getClass().getName());
                    }
                    break;

                case LIKE:
                    if (actualValue instanceof String) {
                        processInstanceQuery.variableValueLike(variable.getName(), (String) actualValue);
                    } else {
                        throw new ActivitiIllegalArgumentException("Only string variable values are supported for like, but was: "
                                + actualValue.getClass().getName());
                    }
                    break;

                case GREATER_THAN:
                    processInstanceQuery.variableValueGreaterThan(variable.getName(), actualValue);
                    break;

                case GREATER_THAN_OR_EQUALS:
                    processInstanceQuery.variableValueGreaterThanOrEqual(variable.getName(), actualValue);
                    break;

                case LESS_THAN:
                    processInstanceQuery.variableValueLessThan(variable.getName(), actualValue);
                    break;

                case LESS_THAN_OR_EQUALS:
                    processInstanceQuery.variableValueLessThanOrEqual(variable.getName(), actualValue);
                    break;

                default:
                    throw new ActivitiIllegalArgumentException("Unsupported variable query operation: " + variable.getVariableOperation());
            }
        }
    }

    protected ProcessInstanceQueryRequest getQueryRequest(Map<String, String> allRequestParams){
        // Populate query based on request
        ProcessInstanceQueryRequest queryRequest = new ProcessInstanceQueryRequest();

        if (allRequestParams.containsKey("id")) {
            queryRequest.setProcessInstanceId(allRequestParams.get("id"));
        }

        if (allRequestParams.containsKey("processDefinitionKey")) {
            queryRequest.setProcessDefinitionKey(allRequestParams.get("processDefinitionKey"));
        }

        if (allRequestParams.containsKey("processDefinitionId")) {
            queryRequest.setProcessDefinitionId(allRequestParams.get("processDefinitionId"));
        }

        if (allRequestParams.containsKey("businessKey")) {
            queryRequest.setProcessBusinessKey(allRequestParams.get("businessKey"));
        }

        if (allRequestParams.containsKey("involvedUser")) {
            queryRequest.setInvolvedUser(allRequestParams.get("involvedUser"));
        }

        if (allRequestParams.containsKey("suspended")) {
            queryRequest.setSuspended(Boolean.valueOf(allRequestParams.get("suspended")));
        }

        if (allRequestParams.containsKey("superProcessInstanceId")) {
            queryRequest.setSuperProcessInstanceId(allRequestParams.get("superProcessInstanceId"));
        }

        if (allRequestParams.containsKey("subProcessInstanceId")) {
            queryRequest.setSubProcessInstanceId(allRequestParams.get("subProcessInstanceId"));
        }

        if (allRequestParams.containsKey("excludeSubprocesses")) {
            queryRequest.setExcludeSubprocesses(Boolean.valueOf(allRequestParams.get("excludeSubprocesses")));
        }

        if (allRequestParams.containsKey("includeProcessVariables")) {
            queryRequest.setIncludeProcessVariables(Boolean.valueOf(allRequestParams.get("includeProcessVariables")));
        }

        if (allRequestParams.containsKey("tenantId")) {
            queryRequest.setTenantId(allRequestParams.get("tenantId"));
        }

        if (allRequestParams.containsKey("tenantIdLike")) {
            queryRequest.setTenantIdLike(allRequestParams.get("tenantIdLike"));
        }

        if (allRequestParams.containsKey("withoutTenantId")) {
            if (Boolean.valueOf(allRequestParams.get("withoutTenantId"))) {
                queryRequest.setWithoutTenantId(Boolean.TRUE);
            }
        }

        return queryRequest;
    }

    protected void validateIdentityLinkArguments(String identityId, String type) {
        if (identityId == null) {
            throw new ActivitiIllegalArgumentException("IdentityId is required.");
        }
        if (type == null) {
            throw new ActivitiIllegalArgumentException("Type is required.");
        }
    }

    protected IdentityLink getIdentityLink(String identityId, String type, String processInstanceId) {
        RuntimeService runtimeService = BPMNOSGIService.getRumtimeService();
        // Perhaps it would be better to offer getting a single identity link from the API
        List<IdentityLink> allLinks = runtimeService.getIdentityLinksForProcessInstance(processInstanceId);
        for (IdentityLink link : allLinks) {
            if (identityId.equals(link.getUserId()) && link.getType().equals(type)) {
                return link;
            }
        }
        throw new ActivitiObjectNotFoundException("Could not find the requested identity link.", IdentityLink.class);
    }
}
