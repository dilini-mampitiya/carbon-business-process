/*
 * Copyright (c) 2015. WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * /
 */

package org.wso2.carbon.bpmn.rest.service.base;

import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.wso2.carbon.bpmn.rest.common.utils.BPMNOSGIService;

public class BaseIdentityService {

    protected org.activiti.engine.IdentityService identityService = BPMNOSGIService.getIdentityService();

    protected Group getGroupFromRequest(String groupId) {
        Group group = identityService.createGroupQuery().groupId(groupId).singleResult();

        if (group == null) {
            throw new ActivitiObjectNotFoundException("Could not find a group with id '" + groupId + "'.", User.class);
        }
        return group;
    }

    protected User getUserFromRequest(String userId) {
        User user = identityService.createUserQuery().userId(userId).singleResult();

        if (user == null) {
            throw new ActivitiObjectNotFoundException("Could not find a user with id '" + userId + "'.", User.class);
        }
        return user;
    }

}
