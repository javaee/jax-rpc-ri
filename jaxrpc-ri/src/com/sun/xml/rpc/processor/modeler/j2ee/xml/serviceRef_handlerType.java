/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License).  You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the license at
 * https://glassfish.dev.java.net/public/CDDLv1.0.html.
 * See the License for the specific language governing
 * permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at https://glassfish.dev.java.net/public/CDDLv1.0.html.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * you own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

// @(#) 1.1 jsr109ri/src/java/com/ibm/webservices/ri/xml/jaxrpcmap1_1/serviceRef_handlerType.java, jsr109ri, jsr10911, b0240.03 9/30/02 11:34:38 [10/7/02 11:55:23]
/*************************************************************************
   Licensed Materials - Property of IBM
   5639-D57, 5630-A36, 5630-A37, 5724-D18 (C) COPYRIGHT International Business M
achines Corp. 2002
   All Rights Reserved
   US Government Users Restricted Rights - Use, duplication, or
   disclosure restricted by GSA ADP Schedule Contract  with
   IBM Corp.
**************************************************************************/
package com.sun.xml.rpc.processor.modeler.j2ee.xml;

/**
* This class represents the complex type <serviceRef_handlerType>
*/
public class serviceRef_handlerType extends ComplexType {
    public serviceRef_handlerType() {
    }

    public void setHandlerName(String handlerName) {
        setElementValue("handler-name", handlerName);
    }

    public String getHandlerName() {
        return getElementValue("handler-name");
    }

    public boolean removeHandlerName() {
        return removeElement("handler-name");
    }

    public void setHandlerClass(String handlerClass) {
        setElementValue("handler-class", handlerClass);
    }

    public String getHandlerClass() {
        return getElementValue("handler-class");
    }

    public boolean removeHandlerClass() {
        return removeElement("handler-class");
    }

    public void setInitParam(int index, String initParam) {
        setElementValue(index, "init-param", initParam);
    }

    public String getInitParam(int index) {
        return getElementValue("init-param", index);
    }

    public int getInitParamCount() {
        return sizeOfElement("init-param");
    }

    public boolean removeInitParam(int index) {
        return removeElement(index, "init-param");
    }

    public void setSoapHeader(int index, String soapHeader) {
        setElementValue(index, "soap-header", soapHeader);
    }

    public String getSoapHeader(int index) {
        return getElementValue("soap-header", index);
    }

    public int getSoapHeaderCount() {
        return sizeOfElement("soap-header");
    }

    public boolean removeSoapHeader(int index) {
        return removeElement(index, "soap-header");
    }

    public void setSoapRole(int index, String soapRole) {
        setElementValue(index, "soap-role", soapRole);
    }

    public String getSoapRole(int index) {
        return getElementValue("soap-role", index);
    }

    public int getSoapRoleCount() {
        return sizeOfElement("soap-role");
    }

    public boolean removeSoapRole(int index) {
        return removeElement(index, "soap-role");
    }

    public void setPortName(int index, String portName) {
        setElementValue(index, "port-name", portName);
    }

    public String getPortName(int index) {
        return getElementValue("port-name", index);
    }

    public int getPortNameCount() {
        return sizeOfElement("port-name");
    }

    public boolean removePortName(int index) {
        return removeElement(index, "port-name");
    }

    public void setId(String id) {
        setAttributeValue("id", id);
    }

    public String getId() {
        return getAttributeValue("id");
    }

    public boolean removeId() {
        return removeAttribute("id");
    }

}
