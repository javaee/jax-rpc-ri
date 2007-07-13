/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
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
