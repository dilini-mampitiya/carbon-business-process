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

package org.wso2.carbon.bpel.ui.bpel2svg;

import org.wso2.carbon.bpel.ui.bpel2svg.impl.AssignImpl;
import org.wso2.carbon.bpel.ui.bpel2svg.impl.CompensateImpl;
import org.wso2.carbon.bpel.ui.bpel2svg.impl.CompensateScopeImpl;
import org.wso2.carbon.bpel.ui.bpel2svg.impl.ElseIfImpl;
import org.wso2.carbon.bpel.ui.bpel2svg.impl.*;
import org.wso2.carbon.bpel.ui.bpel2svg.impl.FlowImpl;
import org.wso2.carbon.bpel.ui.bpel2svg.impl.ForEachImpl;
import org.wso2.carbon.bpel.ui.bpel2svg.impl.IfImpl;
import org.wso2.carbon.bpel.ui.bpel2svg.impl.InvokeImpl;
import org.wso2.carbon.bpel.ui.bpel2svg.impl.OnAlarmImpl;
import org.wso2.carbon.bpel.ui.bpel2svg.impl.OnEventImpl;
import org.wso2.carbon.bpel.ui.bpel2svg.impl.OnMessageImpl;
import org.wso2.carbon.bpel.ui.bpel2svg.impl.ProcessImpl;
import org.wso2.carbon.bpel.ui.bpel2svg.impl.ReThrowImpl;
import org.wso2.carbon.bpel.ui.bpel2svg.impl.ReceiveImpl;
import org.wso2.carbon.bpel.ui.bpel2svg.impl.RepeatUntilImpl;
import org.wso2.carbon.bpel.ui.bpel2svg.impl.ReplyImpl;
import org.wso2.carbon.bpel.ui.bpel2svg.impl.PickImpl;
import org.wso2.carbon.bpel.ui.bpel2svg.impl.WhileImpl;

/**
 * Manage the tag names, and the tag icon locations
 */
public class BPEL2SVGFactory {
    // Constants
    // START_TAGS
    public final static String ASSIGN_START_TAG = "assign";
    public final static String CATCH_START_TAG = "catch";
    public final static String CATCHALL_START_TAG = "catchAll";
    public final static String COMPENSATESCOPE_START_TAG = "compensateScope";
    public final static String COMPENSATE_START_TAG = "compensate";
    public final static String COMPENSATIONHANDLER_START_TAG = "compensationHandler";
    public final static String ELSE_START_TAG = "else";
    public final static String ELSEIF_START_TAG = "elseif";
    public final static String EVENTHANDLER_START_TAG = "eventHandlers";
    public final static String EXIT_START_TAG = "exit";
    public final static String FAULTHANDLER_START_TAG = "faultHandlers";
    public final static String FLOW_START_TAG = "flow";
    public final static String FOREACH_START_TAG = "forEach";
    public final static String IF_START_TAG = "if";
    public final static String INVOKE_START_TAG = "invoke";
    public final static String ONALARM_START_TAG = "onAlarm";
    public final static String ONEVENT_START_TAG = "onEvent";
    public final static String ONMESSAGE_START_TAG = "onMessage";
    public final static String PICK_START_TAG = "pick";
    public final static String PROCESS_START_TAG = "process";
    public final static String RECEIVE_START_TAG = "receive";
    public final static String REPEATUNTIL_START_TAG = "repeatUntil";
    public final static String REPLY_START_TAG = "reply";
    public final static String RETHROW_START_TAG = "rethrow";
    public final static String SCOPE_START_TAG = "scope";
    public final static String SEQUENCE_START_TAG = "sequence";
    public final static String SOURCE_START_TAG = "source";
    public final static String SOURCES_START_TAG = "sources";
    public final static String TARGET_START_TAG = "target";
    public final static String TARGETS_START_TAG = "targets";
    public final static String TERMINATIONHANDLER_START_TAG = "terminationHandler";
    public final static String THROW_START_TAG = "throw";
    public final static String WAIT_START_TAG = "wait";
    public final static String WHILE_START_TAG = "while";
    public final static String EMPTY_START_TAG = "empty";
    // END_TAGS
    public final static String ASSIGN_END_TAG = "/assign";
    public final static String CATCH_END_TAG = "/catch";
    public final static String CATCHALL_END_TAG = "/catchAll";
    public final static String COMPENSATESCOPE_END_TAG = "/compensateScope";
    public final static String COMPENSATE_END_TAG = "/compensate";
    public final static String COMPENSATIONHANDLER_END_TAG = "/compensationHandler";
    public final static String ELSE_END_TAG = "/else";
    public final static String ELSEIF_END_TAG = "/elseif";
    public final static String EVENTHANDLER_END_TAG = "/eventHandlers";
    public final static String EXIT_END_TAG = "/exit";
    public final static String FAULTHANDLER_END_TAG = "/faultHandlers";
    public final static String FLOW_END_TAG = "/flow";
    public final static String FOREACH_END_TAG = "/forEach";
    public final static String IF_END_TAG = "/if";
    public final static String INVOKE_END_TAG = "/invoke";
    public final static String ONMESSAGE_END_TAG = "/onMessage";
    public final static String ONALARM_END_TAG = "/onAlarm";
    public final static String ONEVENT_END_TAG = "/onEvent";
    public final static String PICK_END_TAG = "/pick";
    public final static String PROCESS_END_TAG = "/process";
    public final static String RECEIVE_END_TAG = "/receive";
    public final static String REPEATUNTIL_END_TAG = "/repeatUntil";
    public final static String REPLY_END_TAG = "/reply";
    public final static String RETHROW_END_TAG = "/rethrow";
    public final static String SCOPE_END_TAG = "/scope";
    public final static String SEQUENCE_END_TAG = "/sequence";
    public final static String SOURCE_END_TAG = "/source";
    public final static String SOURCES_END_TAG = "/sources";
    public final static String TARGET_END_TAG = "/target";
    public final static String TARGETS_END_TAG = "/targets";
    public final static String TERMINATIONHANDLER_END_TAG = "/terminationHandler";
    public final static String THROW_END_TAG = "/throw";
    public final static String WAIT_END_TAG = "/wait";
    public final static String WHILE_END_TAG = "/while";
    public final static String EMPTY_END_TAG = "/empty";

    public final static String SINGLE_LINE_END_TAG = "/>";
    public final static int TEXT_ADJUST = 10;

    // Properties
    //Variable with the source of the images/icons
    public String iconSource = "images/bpel2svg";
    private static BPEL2SVGFactory instance = null;

    /**
     *
     * @return instance of a BPEL2SVGFactory
     */
    public static BPEL2SVGFactory getInstance() {
        if (instance == null) {
            instance = new BPEL2SVGFactory();
        }
        return instance;
    }

    public LayoutManager layoutManager = null;

    /**
     *
     * @return instance of LayoutManager
     */
    public LayoutManager getLayoutManager() {
        if (layoutManager == null) {
            layoutManager = new LayoutManager();
        }
        return layoutManager;
    }

    /**
     * Sets the layoutManager
     * @param layoutManager
     */
    public void setLayoutManager(LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    // Icon Extension for the activity icons
    private String iconExtension = ".png";

    //Getter and Setter of the icon extension of the activity icons

    /**
     * Gets the extension of the activity icon
     * @return String with the extension of the activity icon
     */
    public String getIconExtension() {
        return iconExtension;
    }

    /**
     * Sets the extension of the activity icon
     * @param iconExtension extension of the activity icon
     */
    public void setIconExtension(String iconExtension) {
        this.iconExtension = iconExtension;
    }

    /**
     * Gets the start icon path of each activity
     * @param activity String with the activity type/name
     * @return String with the start icon path relevant to each activity according to the activity type/name
     */
    public String getIconPath(String activity) {
        String iconPath = null;
        if (activity != null) {
            if (activity.equalsIgnoreCase(AssignImpl.class.getName())) {
                iconPath = BPEL2SVGIcons.ASSIGN_ICON;
            } else if (activity.equalsIgnoreCase(EmptyImpl.class.getName())) {
                iconPath = BPEL2SVGIcons.EMPTY_ICON;
            } else if (activity.equalsIgnoreCase(ElseIfImpl.class.getName())) {
                iconPath = BPEL2SVGIcons.ELSEIF_ICON;
            } else if (activity.equalsIgnoreCase(ElseImpl.class.getName())) {
                iconPath = BPEL2SVGIcons.ELSE_ICON;
            } else if (activity.equalsIgnoreCase(CompensateImpl.class.getName())) {
                iconPath = BPEL2SVGIcons.COMPENSATE_ICON;
            } else if (activity.equalsIgnoreCase(CompensateScopeImpl.class.getName())) {
                iconPath = BPEL2SVGIcons.COMPENSATESCOPE_ICON;
            } else if (activity.equalsIgnoreCase(ExitImpl.class.getName())) {
                iconPath = BPEL2SVGIcons.EXIT_ICON;
            } else if (activity.equalsIgnoreCase(FlowImpl.class.getName())) {
                iconPath = BPEL2SVGIcons.FLOW_ICON;
            } else if (activity.equalsIgnoreCase(ForEachImpl.class.getName())) {
                iconPath = BPEL2SVGIcons.FOREACH_ICON;
            } else if (activity.equalsIgnoreCase(IfImpl.class.getName())) {
                iconPath = BPEL2SVGIcons.IF_ICON;
            } else if (activity.equalsIgnoreCase(InvokeImpl.class.getName())) {
                iconPath = BPEL2SVGIcons.INVOKE_ICON;
            } else if (activity.equalsIgnoreCase(OnAlarmImpl.class.getName())) {
                iconPath = BPEL2SVGIcons.ONALARM_ICON;
            } else if (activity.equalsIgnoreCase(OnEventImpl.class.getName())) {
                iconPath = BPEL2SVGIcons.ONEVENT_ICON;
            } else if (activity.equalsIgnoreCase(OnMessageImpl.class.getName())) {
                iconPath = BPEL2SVGIcons.ONMESSAGE_ICON;
            } else if (activity.equalsIgnoreCase(PickImpl.class.getName())) {
                iconPath = BPEL2SVGIcons.PICK_ICON;
            } else if (activity.equalsIgnoreCase(ProcessImpl.class.getName())) {
                iconPath = BPEL2SVGIcons.START_ICON;
            } else if (activity.equalsIgnoreCase(ReceiveImpl.class.getName())) {
                iconPath = BPEL2SVGIcons.RECEIVE_ICON;
            } else if (activity.equalsIgnoreCase(RepeatUntilImpl.class.getName())) {
                iconPath = BPEL2SVGIcons.REPEATUNTIL_ICON;
            } else if (activity.equalsIgnoreCase(ReplyImpl.class.getName())) {
                iconPath = BPEL2SVGIcons.REPLY_ICON;
            } else if (activity.equalsIgnoreCase(ReThrowImpl.class.getName())) {
                iconPath = BPEL2SVGIcons.RETHROW_ICON;
            } else if (activity.equalsIgnoreCase(ScopeImpl.class.getName())) {
                iconPath = BPEL2SVGIcons.SCOPE_ICON;
            } else if (activity.equalsIgnoreCase(ThrowImpl.class.getName())) {
                iconPath = BPEL2SVGIcons.THROW_ICON;
            } else if (activity.equalsIgnoreCase(WaitImpl.class.getName())) {
                iconPath = BPEL2SVGIcons.WAIT_ICON;
            } else if (activity.equalsIgnoreCase(WhileImpl.class.getName())) {
                iconPath = BPEL2SVGIcons.WHILE_ICON;
            }
        }

        return iconPath;
    }

    /**
     * Gets the end icon path of each activity
     * @param activity String with the activity type/name
     * @return String with the end icon path relevant to each activity according to the activity type/name
     */
    public String getEndIconPath(String activity) {
        String iconPath = null;
        if (activity != null) {
            if (activity.equalsIgnoreCase(FlowImpl.class.getName())) {
                iconPath = BPEL2SVGIcons.FLOW_ICON;
            } else if (activity.equalsIgnoreCase(ForEachImpl.class.getName())) {
                iconPath = BPEL2SVGIcons.ENDFOREACH_ICON;
            } else if (activity.equalsIgnoreCase(IfImpl.class.getName())) {
                iconPath = BPEL2SVGIcons.ENDIF_ICON;
            } else if (activity.equalsIgnoreCase(PickImpl.class.getName())) {
                iconPath = BPEL2SVGIcons.PICK_ICON;
            } else if (activity.equalsIgnoreCase(ProcessImpl.class.getName())) {
                iconPath = BPEL2SVGIcons.END_ICON;
            } else if (activity.equalsIgnoreCase(RepeatUntilImpl.class.getName())) {
                iconPath = BPEL2SVGIcons.ENDREPEATUNTIL_ICON;
            } else if (activity.equalsIgnoreCase(ScopeImpl.class.getName())) {
                iconPath = BPEL2SVGIcons.SCOPE_ICON;
            } else if (activity.equalsIgnoreCase(WhileImpl.class.getName())) {
                iconPath = BPEL2SVGIcons.ENDWHILE_ICON;
            }
        }
        return iconPath;
    }
    //Getter and Setter of the image source of the activity icons

    /**
     * Gets the source of the activity icon
     * @return String with the source of the activity icon
     */
    public String getIconSource() {
        return iconSource;
    }

    /**
     * Sets the source of the activity icon
     * @param iconSource source of the activity icon
     */
    public void setIconSource(String iconSource) {
        this.iconSource = iconSource;
    }
}
