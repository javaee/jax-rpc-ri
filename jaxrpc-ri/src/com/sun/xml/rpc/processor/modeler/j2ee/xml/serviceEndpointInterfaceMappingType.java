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

// @(#) 1.1 jsr109ri/src/java/com/ibm/webservices/ri/xml/jaxrpcmap1_1/serviceEndpointInterfaceMappingType.java, jsr109ri, jsr10911, b0240.03 9/30/02 11:34:18 [10/7/02 11:55:22]
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
* This class represents the complex type <serviceEndpointInterfaceMappingType>
*/
public class serviceEndpointInterfaceMappingType extends ComplexType {
    public serviceEndpointInterfaceMappingType() {
    }

    public void setServiceEndpointInterface(fullyQualifiedClassType serviceEndpointInterface) {
        setElementValue("service-endpoint-interface", serviceEndpointInterface);
    }

    public fullyQualifiedClassType getServiceEndpointInterface() {
        return (fullyQualifiedClassType) getElementValue(
            "service-endpoint-interface",
            "fullyQualifiedClassType");
    }

    public boolean removeServiceEndpointInterface() {
        return removeElement("service-endpoint-interface");
    }

    public void setWsdlPortType(xsdQNameType wsdlPortType) {
        setElementValue("wsdl-port-type", wsdlPortType);
    }

    public xsdQNameType getWsdlPortType() {
        return (xsdQNameType) getElementValue("wsdl-port-type", "xsdQNameType");
    }

    public boolean removeWsdlPortType() {
        return removeElement("wsdl-port-type");
    }

    public void setWsdlBinding(xsdQNameType wsdlBinding) {
        setElementValue("wsdl-binding", wsdlBinding);
    }

    public xsdQNameType getWsdlBinding() {
        return (xsdQNameType) getElementValue("wsdl-binding", "xsdQNameType");
    }

    public boolean removeWsdlBinding() {
        return removeElement("wsdl-binding");
    }

    public void setServiceEndpointMethodMapping(
        int index,
        serviceEndpointMethodMappingType serviceEndpointMethodMapping) {
        setElementValue(
            index,
            "service-endpoint-method-mapping",
            serviceEndpointMethodMapping);
    }

    public serviceEndpointMethodMappingType getServiceEndpointMethodMapping(int index) {
        return (serviceEndpointMethodMappingType) getElementValue(
            "service-endpoint-method-mapping",
            "serviceEndpointMethodMappingType",
            index);
    }

    public int getServiceEndpointMethodMappingCount() {
        return sizeOfElement("service-endpoint-method-mapping");
    }

    public boolean removeServiceEndpointMethodMapping(int index) {
        return removeElement(index, "service-endpoint-method-mapping");
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
