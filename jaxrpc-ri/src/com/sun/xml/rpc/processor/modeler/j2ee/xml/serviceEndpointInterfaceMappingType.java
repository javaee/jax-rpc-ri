/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
