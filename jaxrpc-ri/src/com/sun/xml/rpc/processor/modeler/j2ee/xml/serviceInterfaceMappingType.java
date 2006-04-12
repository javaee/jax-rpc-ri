/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

// @(#) 1.1 jsr109ri/src/java/com/ibm/webservices/ri/xml/jaxrpcmap1_1/serviceInterfaceMappingType.java, jsr109ri, jsr10911, b0240.03 9/30/02 11:34:30 [10/7/02 11:55:23]
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
* This class represents the complex type <serviceInterfaceMappingType>
*/
public class serviceInterfaceMappingType extends ComplexType {
    public serviceInterfaceMappingType() {
    }

    public void setServiceInterface(fullyQualifiedClassType serviceInterface) {
        setElementValue("service-interface", serviceInterface);
    }

    public fullyQualifiedClassType getServiceInterface() {
        return (fullyQualifiedClassType) getElementValue(
            "service-interface",
            "fullyQualifiedClassType");
    }

    public boolean removeServiceInterface() {
        return removeElement("service-interface");
    }

    public void setWsdlServiceName(xsdQNameType wsdlServiceName) {
        setElementValue("wsdl-service-name", wsdlServiceName);
    }

    public xsdQNameType getWsdlServiceName() {
        return (xsdQNameType) getElementValue(
            "wsdl-service-name",
            "xsdQNameType");
    }

    public boolean removeWsdlServiceName() {
        return removeElement("wsdl-service-name");
    }

    public void setPortMapping(int index, portMappingType portMapping) {
        setElementValue(index, "port-mapping", portMapping);
    }

    public portMappingType getPortMapping(int index) {
        return (portMappingType) getElementValue(
            "port-mapping",
            "portMappingType",
            index);
    }

    public int getPortMappingCount() {
        return sizeOfElement("port-mapping");
    }

    public boolean removePortMapping(int index) {
        return removeElement(index, "port-mapping");
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
