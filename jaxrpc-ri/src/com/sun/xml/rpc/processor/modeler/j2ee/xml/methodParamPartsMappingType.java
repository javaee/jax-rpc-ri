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

// @(#) 1.1 jsr109ri/src/java/com/ibm/webservices/ri/xml/jaxrpcmap1_1/methodParamPartsMappingType.java, jsr109ri, jsr10911, b0240.03 9/30/02 11:33:52 [10/7/02 11:55:20]
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
* This class represents the complex type <methodParamPartsMappingType>
*/
public class methodParamPartsMappingType extends ComplexType {
    public methodParamPartsMappingType() {
    }

    public void setParamPosition(xsdNonNegativeIntegerType paramPosition) {
        setElementValue("param-position", paramPosition);
    }

    public xsdNonNegativeIntegerType getParamPosition() {
        return (xsdNonNegativeIntegerType) getElementValue(
            "param-position",
            "xsdNonNegativeIntegerType");
    }

    public boolean removeParamPosition() {
        return removeElement("param-position");
    }

    public void setParamType(javaTypeType paramType) {
        setElementValue("param-type", paramType);
    }

    public javaTypeType getParamType() {
        return (javaTypeType) getElementValue("param-type", "javaTypeType");
    }

    public boolean removeParamType() {
        return removeElement("param-type");
    }

    public void setWsdlMessageMapping(wsdlMessageMappingType wsdlMessageMapping) {
        setElementValue("wsdl-message-mapping", wsdlMessageMapping);
    }

    public wsdlMessageMappingType getWsdlMessageMapping() {
        return (wsdlMessageMappingType) getElementValue(
            "wsdl-message-mapping",
            "wsdlMessageMappingType");
    }

    public boolean removeWsdlMessageMapping() {
        return removeElement("wsdl-message-mapping");
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