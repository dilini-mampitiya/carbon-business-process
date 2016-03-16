package org.wso2.carbon.bpel.ui.bpel2svg;
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

public interface SVGInterface {
    /*
    * Return the SVG graph as a SVG string
    */
    public ProcessInterface getRootActivity();
    /**
     * Sets the root activity of the process i.e. Process Activity
     * @param rootActivity root activity of the process i.e. Process Activity
     */
    public void setRootActivity(ProcessInterface rootActivity);
    /*
    * Return the SVG graph as a SVG string
    */
    public String generateSVGString();
    /*
    * Return the image as a base64 encoded string of a PNG
    */
    public String toPNGBase64String();
    /*
    * Return the image as a byte array of a PNG
    */
    public byte[] toPNGBytes();

}
