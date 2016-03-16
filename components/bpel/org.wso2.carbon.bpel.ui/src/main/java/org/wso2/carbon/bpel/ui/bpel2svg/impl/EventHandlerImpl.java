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

import org.apache.axiom.om.OMElement;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;
import org.wso2.carbon.bpel.ui.bpel2svg.*;

import java.util.Iterator;

/**
 * EventHandler tag UI implementation
 */
public class EventHandlerImpl extends ActivityImpl implements EventHandlerInterface {
    /**
     * Initializes a new instance of the EventHandlerImpl class using the specified string i.e. the token
     * @param token
     */
    public EventHandlerImpl(String token) {
        super(token);

        //Assigns the name of the activity to be displayed when drawing the process
        if (name == null) {
            name = "EVENTHANDLER";
        }
        displayName = "Event Handlers";

        // Set Start and End Icons and Sizes
        startIconPath = BPEL2SVGFactory.getInstance().getIconPath(this.getClass().getName());
        endIconPath = BPEL2SVGFactory.getInstance().getEndIconPath(this.getClass().getName());
        // Set Layout
        setVerticalChildLayout(false);
    }
    /**
     * Initializes a new instance of the EventHandlerImpl class using the specified omElement
     * @param omElement which matches the EventHandler tag
     */
    public EventHandlerImpl(OMElement omElement) {
        super(omElement);
        //Assigns the name of the activity to be displayed when drawing the process
        if (name == null) {
            name = "EVENTHANDLER";
        }
        displayName = "Event Handlers";

        // Set Start and End Icons and Sizes
        startIconPath = BPEL2SVGFactory.getInstance().getIconPath(this.getClass().getName());
        endIconPath = BPEL2SVGFactory.getInstance().getEndIconPath(this.getClass().getName());
        // Set Layout
        setVerticalChildLayout(false);
    }

    /**
     * Initializes a new instance of the EventHandlerImpl class using the specified omElement
     * Constructor that is invoked when the omElement type matches an EventHandler Activity when processing the subActivities
     * of the process
     * @param omElement which matches the EventHandler tag
     * @param parent
     */
    public EventHandlerImpl(OMElement omElement, ActivityInterface parent) {
        super(omElement);
        //Set the parent of the activity
        setParent(parent);
        //Assigns the name of the activity to be displayed when drawing the process
        if (name == null) {
            name = "EVENTHANDLER";
        }
        displayName = "Event Handlers";

        // Set Start and End Icons and Sizes
        startIconPath = BPEL2SVGFactory.getInstance().getIconPath(this.getClass().getName());
        endIconPath = BPEL2SVGFactory.getInstance().getEndIconPath(this.getClass().getName());
        // Set Layout
        setVerticalChildLayout(false);
    }

    /**
     *
     * @return String with name of the activity
     */
    @Override
    public String getId() {
        return getName(); // + "-EventHandler";
    }

    /**
     *
     * @return- String with the end tag of Else Activity
     */
    @Override
    public String getEndTag() {
        return BPEL2SVGFactory.EVENTHANDLER_END_TAG;
    }

    /**
     * At the start: width=0, height=0
     * @return dimensions of the composite activity i.e. the final width and height after doing calculations by iterating
     *         through the dimensions of the subActivities
     */
    @Override
    public SVGDimension getDimensions() {
        if (dimensions == null) {
            int width = 0;
            int height = 0;
            //Set the dimensions at the start to (0,0)
            dimensions = new SVGDimension(width, height);

            SVGDimension subActivityDim = null;
            ActivityInterface activity = null;
            //Iterates through the subActivites inside the composite activity
            Iterator<ActivityInterface> itr = getSubActivities().iterator();
            while (itr.hasNext()) {
                activity = itr.next();
                //Gets the dimensions of each subActivity separately
                subActivityDim = activity.getDimensions();
                //Checks whether the height of the subActivity is greater than zero
                if (subActivityDim.getHeight() > height) {
                    height += subActivityDim.getHeight();
                }
                //width of the subActivities is added to the width of the composite activity
                width += subActivityDim.getWidth();
            }
            /*After iterating through all the subActivities and altering the dimensions of the composite activity
              to get more spacing , Xspacing and Yspacing is added to the height and the width of the composite activity
            */
            height += getYSpacing();
            width += getXSpacing();
            //Set the Calculated dimensions for the SVG height and width
            dimensions.setWidth(width);
            dimensions.setHeight(height);
        }

        return dimensions;
    }
    /**
     * Sets the layout of the process drawn
     * @param startXLeft x-coordinate of the activity
     * @param startYTop  y-coordinate of the activity
     */
    @Override
    public void layout(int startXLeft, int startYTop) {
        if (layoutManager.isVerticalLayout()) {
            layoutVertical(startXLeft, startYTop);
        } else {
            layoutHorizontal(startXLeft, startYTop);
        }
    }

    /**
     * Sets the x and y positions of the activities
     * At the start: startXLeft=0, startYTop=0
     * centreOfMyLayout- center of the the SVG
     * @param startXLeft x-coordinate
     * @param startYTop  y-coordinate
     *
     */
    public void layoutVertical(int startXLeft, int startYTop) {
        //Aligns the activities to the center of the layout
        int centreOfMyLayout = startXLeft + (dimensions.getWidth() / 2);
        //Positioning the startIcon
        int xLeft = centreOfMyLayout - (getStartIconWidth() / 2);
        int yTop = startYTop + (getYSpacing() / 2);
        //Positioning the endIcon
        int endXLeft = centreOfMyLayout - (getEndIconWidth() / 2);
        int endYTop = startYTop + dimensions.getHeight() - getEndIconHeight() - (getYSpacing() / 2);

        ActivityInterface activity = null;
        Iterator<ActivityInterface> itr = getSubActivities().iterator();
        //Adjusting the childXLeft and childYTop positions
        int childYTop = startYTop + (getYSpacing() / 2);
        int childXLeft = startXLeft + (getXSpacing() / 2);
        //Iterates through all the subActivities
        while (itr.hasNext()) {
            activity = itr.next();
            //Sets the xLeft and yTop position of the iterated activity
            activity.layout(childXLeft, childYTop);
            childXLeft += activity.getDimensions().getWidth();
        }

        //Sets the xLeft and yTop positions of the start icon
        setStartIconXLeft(xLeft);
        setStartIconYTop(yTop);
        //Sets the xLeft and yTop positions of the end icon
        setEndIconXLeft(endXLeft);
        setEndIconYTop(endYTop);
        //Sets the xLeft and yTop positions of the start icon text
        setStartIconTextXLeft(startXLeft + BOX_MARGIN);
        setStartIconTextYTop(startYTop + BOX_MARGIN + BPEL2SVGFactory.TEXT_ADJUST);
        //Sets the xLeft and yTop positions of the SVG  of the composite activity after setting the dimensions
        getDimensions().setXLeft(startXLeft);
        getDimensions().setYTop(startYTop);
    }
    /**
     * Sets the x and y positions of the activities
     * At the start: startXLeft=0, startYTop=0
     * @param startXLeft x-coordinate
     * @param startYTop  y-coordinate
     * centreOfMyLayout- center of the the SVG
     */
    private void layoutHorizontal(int startXLeft, int startYTop) {
        //Aligns the activities to the center of the layout
        int centreOfMyLayout = startYTop + (dimensions.getHeight() / 2);
        //Positioning the startIcon
        int xLeft = startXLeft + (getYSpacing() / 2);
        int yTop = centreOfMyLayout - (getStartIconHeight() / 2);
        //Positioning the endIcon
        int endXLeft = startXLeft + dimensions.getWidth() - getEndIconWidth() - (getYSpacing() / 2);
        int endYTop = centreOfMyLayout - (getEndIconHeight() / 2);

        ActivityInterface activity = null;
        Iterator<ActivityInterface> itr = getSubActivities().iterator();
        //Adjusting the childXLeft and childYTop positions
        int childXLeft = startXLeft + (getYSpacing() / 2);
        int childYTop = startYTop + (getXSpacing() / 2);
        //Iterates through all the subActivities
        while (itr.hasNext()) {
            activity = itr.next();
            //Sets the xLeft and yTop position of the iterated activity
            activity.layout(childXLeft, childYTop);
            childYTop += activity.getDimensions().getHeight();
        }

        //Sets the xLeft and yTop positions of the start icon
        setStartIconXLeft(xLeft);
        setStartIconYTop(yTop);
        //Sets the xLeft and yTop positions of the end icon
        setEndIconXLeft(endXLeft);
        setEndIconYTop(endYTop);
        //Sets the xLeft and yTop positions of the start icon text
        setStartIconTextXLeft(startXLeft + BOX_MARGIN);
        setStartIconTextYTop(startYTop + BOX_MARGIN + BPEL2SVGFactory.TEXT_ADJUST);
        //Sets the xLeft and yTop positions of the SVG of the composite activity after setting the dimensions
        getDimensions().setXLeft(startXLeft);
        getDimensions().setYTop(startYTop);
    }

    /**
     * At the start: xLeft=0, yTop=0
     * Calculates the coordinates of the arrow which enters an activity
     * @return coordinates/entry point of the entry arrow for the activities
     * After Calculations(Vertical Layout): xLeft=Xleft of Icon + (width of icon)/2 , yTop= Ytop of the Icon
     */
    @Override
    public SVGCoordinates getEntryArrowCoords() {
        int xLeft = 0;
        int yTop = 0;
        if (layoutManager.isVerticalLayout()) {
            xLeft = getDimensions().getXLeft() + (getDimensions().getWidth() / 2);
            yTop = getDimensions().getYTop() + BOX_MARGIN;
        } else {
            xLeft = getDimensions().getXLeft() + BOX_MARGIN;
            yTop = getDimensions().getYTop() + (getDimensions().getHeight() / 2);

        }
        //Returns the calculated coordinate points of the entry arrow
        SVGCoordinates coords = new SVGCoordinates(xLeft, yTop);

        return coords;
    }
    /**
     * At the start: xLeft=0, yTop=0
     * Calculates the coordinates of the arrow which leaves an activity
     * @return coordinates/exit point of the exit arrow for the activities
     */
    @Override
    public SVGCoordinates getExitArrowCoords() {
        int xLeft = 0;
        int yTop = 0;
        if (layoutManager.isVerticalLayout()) {
            xLeft = getEndIconXLeft() + (getEndIconWidth() / 2);
            yTop = getEndIconYTop() + getEndIconHeight();
        } else {
            xLeft = getEndIconXLeft() + getEndIconWidth();
            yTop = getEndIconYTop() + (getEndIconHeight() / 2);

        }
        //Returns the calculated coordinate points of the exit arrow
        SVGCoordinates coords = new SVGCoordinates(xLeft, yTop);

        return coords;
    }

    /**
     *
     * @param doc SVG document which defines the components including shapes, gradients etc. of the activity
     * @return Element(represents an element in a XML/HTML document) which contains the components of the EventHandler composite activity
     */
    @Override
    public Element getSVGString(SVGDocument doc) {
        Element group = null;
        group = doc.createElementNS("http://www.w3.org/2000/svg", "g");
        //Get the id of the activity
        group.setAttributeNS(null, "id", getLayerId());
        //Get the scope/box of the EventHandler which surrounds all its subActivities
        group.appendChild(getBoxDefinition(doc));
        //Get the start icon of the activity
        group.appendChild(getImageDefinition(doc));
        //Get the start icon image text
        group.appendChild(getStartImageText(doc));
        // Process Sub Activities
        group.appendChild(getSubActivitiesSVGString(doc));
        //Get the end icon of the activity
        group.appendChild(getEndImageDefinition(doc));

        return group;
    }
    /**
     * Get the arrow coordinates of the activities
     * @return String which contains the arrow coordinates of the EventHandler activity and its subActivities
     */
    protected String getArrows() {
        StringBuffer svgSB = new StringBuffer();
        return svgSB.toString();
    }

    /**
     * Adds opacity to icons
     * @return true or false
     */
    @Override
    public boolean isAddOpacity() {
        return isAddCompositeActivityOpacity();
    }

    /**
     *
     * @return String with the opacity value
     */
    @Override
    public String getOpacity() {
        return getCompositeOpacity();
    }
}
