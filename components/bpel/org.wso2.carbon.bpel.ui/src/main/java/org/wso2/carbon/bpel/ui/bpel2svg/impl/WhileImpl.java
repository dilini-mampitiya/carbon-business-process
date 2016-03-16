/*
 * Copyright (c) 2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.bpel.ui.bpel2svg.impl;

import org.wso2.carbon.bpel.ui.bpel2svg.ActivityInterface;
import org.wso2.carbon.bpel.ui.bpel2svg.BPEL2SVGFactory;
import org.apache.axiom.om.OMElement;
import org.wso2.carbon.bpel.ui.bpel2svg.WhileInterface;

/**
 * While tag UI implementation
 */
public class WhileImpl extends ForEachImpl implements WhileInterface {
	/**
     * Initializes a new instance of the WhileImpl class using the specified string i.e. the token
     * @param token
     */
    public WhileImpl(String token) {
        super(token);

       // Set Start and End Icons and their Sizes
        startIconPath = BPEL2SVGFactory.getInstance().getIconPath(this.getClass().getName());
        endIconPath = BPEL2SVGFactory.getInstance().getEndIconPath(this.getClass().getName());
    }
	/**
     * Initializes a new instance of the WhileImpl class using the specified omElement
     * @param omElement which matches the While tag
     */
    public WhileImpl(OMElement omElement) {
        super(omElement);

        // Set Start and End Icons and their Sizes
        startIconPath = BPEL2SVGFactory.getInstance().getIconPath(this.getClass().getName());
        endIconPath = BPEL2SVGFactory.getInstance().getEndIconPath(this.getClass().getName());
    }
	/**
     * Initializes a new instance of the WhileImpl class using the specified omElement
     * Constructor that is invoked when the omElement type matches an While Activity when processing the subActivities
     * of the process
     * @param omElement which matches the While tag
     * @param parent
     */
    public WhileImpl(OMElement omElement, ActivityInterface parent) {
        super(omElement);

        //Set the parent of the activity
        setParent(parent);

        // Set Start and End Icons and their Sizes
        startIconPath = BPEL2SVGFactory.getInstance().getIconPath(this.getClass().getName());
        endIconPath = BPEL2SVGFactory.getInstance().getEndIconPath(this.getClass().getName());
    }

    /**
     *
     * @return String with the end tag of While Activity
     */
    @Override
    public String getEndTag() {
        return BPEL2SVGFactory.WHILE_END_TAG;
    }

}
