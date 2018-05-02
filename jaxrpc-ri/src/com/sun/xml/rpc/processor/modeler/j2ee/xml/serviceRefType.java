/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
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

// @(#) 1.1 jsr109ri/src/java/com/ibm/webservices/ri/xml/jaxrpcmap1_1/serviceRefType.java, jsr109ri, jsr10911, b0240.03 9/30/02 11:34:32 [10/7/02 11:55:23]
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
* This class represents the complex type <serviceRefType>
*/
public class serviceRefType extends ComplexType {
    public serviceRefType() {
    }

    public void setServiceRefName(String serviceRefName) {
        setElementValue("service-ref-name", serviceRefName);
    }

    public String getServiceRefName() {
        return getElementValue("service-ref-name");
    }

    public boolean removeServiceRefName() {
        return removeElement("service-ref-name");
    }

    public void setServiceInterface(String serviceInterface) {
        setElementValue("service-interface", serviceInterface);
    }

    public String getServiceInterface() {
        return getElementValue("service-interface");
    }

    public boolean removeServiceInterface() {
        return removeElement("service-interface");
    }

    public void setWsdlFile(String wsdlFile) {
        setElementValue("wsdl-file", wsdlFile);
    }

    public String getWsdlFile() {
        return getElementValue("wsdl-file");
    }

    public boolean removeWsdlFile() {
        return removeElement("wsdl-file");
    }

    public void setJaxrpcMappingFile(String jaxrpcMappingFile) {
        setElementValue("jaxrpc-mapping-file", jaxrpcMappingFile);
    }

    public String getJaxrpcMappingFile() {
        return getElementValue("jaxrpc-mapping-file");
    }

    public boolean removeJaxrpcMappingFile() {
        return removeElement("jaxrpc-mapping-file");
    }

    public void setServiceQname(String serviceQname) {
        setElementValue("service-qname", serviceQname);
    }

    public String getServiceQname() {
        return getElementValue("service-qname");
    }

    public boolean removeServiceQname() {
        return removeElement("service-qname");
    }

    public void setPortComponentRef(
        int index,
        portComponentRefType portComponentRef) {
        setElementValue(index, "port-component-ref", portComponentRef);
    }

    public portComponentRefType getPortComponentRef(int index) {
        return (portComponentRefType) getElementValue(
            "port-component-ref",
            "portComponentRefType",
            index);
    }

    public int getPortComponentRefCount() {
        return sizeOfElement("port-component-ref");
    }

    public boolean removePortComponentRef(int index) {
        return removeElement(index, "port-component-ref");
    }

    public void setHandler(int index, serviceRef_handlerType handler) {
        setElementValue(index, "handler", handler);
    }

    public serviceRef_handlerType getHandler(int index) {
        return (serviceRef_handlerType) getElementValue(
            "handler",
            "serviceRef_handlerType",
            index);
    }

    public int getHandlerCount() {
        return sizeOfElement("handler");
    }

    public boolean removeHandler(int index) {
        return removeElement(index, "handler");
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
