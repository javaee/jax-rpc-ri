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

// @(#) 1.2 jsr109ri/src/java/com/ibm/webservices/ri/xml/jaxrpcmap1_1/serviceEndpointMethodMappingType.java, jsr109ri, jsr10911, b0240.03 10/6/02 20:26:01 [10/7/02 11:55:22]
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
* This class represents the complex type <serviceEndpointMethodMappingType>
*/
public class serviceEndpointMethodMappingType extends ComplexType {
    public serviceEndpointMethodMappingType() {
    }

    public void setJavaMethodName(string javaMethodName) {
        setElementValue("java-method-name", javaMethodName);
    }

    public string getJavaMethodName() {
        return (string) getElementValue("java-method-name", "string");
    }

    public boolean removeJavaMethodName() {
        return removeElement("java-method-name");
    }

    public void setWsdlOperation(string wsdlOperation) {
        setElementValue("wsdl-operation", wsdlOperation);
    }

    public string getWsdlOperation() {
        return (string) getElementValue("wsdl-operation", "string");
    }

    public boolean removeWsdlOperation() {
        return removeElement("wsdl-operation");
    }

    public void setWrappedElement(emptyType wrappedElement) {
        setElementValue("wrapped-element", wrappedElement);
    }

    public emptyType getWrappedElement() {
        return (emptyType) getElementValue("wrapped-element", "emptyType");
    }

    public boolean removeWrappedElement() {
        return removeElement("wrapped-element");
    }

    public void setMethodParamPartsMapping(
        int index,
        methodParamPartsMappingType methodParamPartsMapping) {
        setElementValue(
            index,
            "method-param-parts-mapping",
            methodParamPartsMapping);
    }

    public methodParamPartsMappingType getMethodParamPartsMapping(int index) {
        return (methodParamPartsMappingType) getElementValue(
            "method-param-parts-mapping",
            "methodParamPartsMappingType",
            index);
    }

    public int getMethodParamPartsMappingCount() {
        return sizeOfElement("method-param-parts-mapping");
    }

    public boolean removeMethodParamPartsMapping(int index) {
        return removeElement(index, "method-param-parts-mapping");
    }

    public void setWsdlReturnValueMapping(wsdlReturnValueMappingType wsdlReturnValueMapping) {
        setElementValue("wsdl-return-value-mapping", wsdlReturnValueMapping);
    }

    public wsdlReturnValueMappingType getWsdlReturnValueMapping() {
        return (wsdlReturnValueMappingType) getElementValue(
            "wsdl-return-value-mapping",
            "wsdlReturnValueMappingType");
    }

    public boolean removeWsdlReturnValueMapping() {
        return removeElement("wsdl-return-value-mapping");
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
