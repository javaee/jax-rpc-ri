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

// @(#) 1.1 jsr109ri/src/java/com/ibm/webservices/ri/xml/jaxrpcmap1_1/javaWsdlMappingType.java, jsr109ri, jsr10911, b0240.03 9/30/02 11:33:38 [10/7/02 11:55:19]
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
* This class represents the complex type <javaWsdlMappingType>
*/
public class javaWsdlMappingType extends ComplexType {
    public javaWsdlMappingType() {
    }

    public void setPackageMapping(
        int index,
        packageMappingType packageMapping) {
        setElementValue(index, "package-mapping", packageMapping);
    }

    public packageMappingType getPackageMapping(int index) {
        return (packageMappingType) getElementValue(
            "package-mapping",
            "packageMappingType",
            index);
    }

    public int getPackageMappingCount() {
        return sizeOfElement("package-mapping");
    }

    public boolean removePackageMapping(int index) {
        return removeElement(index, "package-mapping");
    }

    public void setJavaXmlTypeMapping(
        int index,
        javaXmlTypeMappingType javaXmlTypeMapping) {
        setElementValue(index, "java-xml-type-mapping", javaXmlTypeMapping);
    }

    public javaXmlTypeMappingType getJavaXmlTypeMapping(int index) {
        return (javaXmlTypeMappingType) getElementValue(
            "java-xml-type-mapping",
            "javaXmlTypeMappingType",
            index);
    }

    public int getJavaXmlTypeMappingCount() {
        return sizeOfElement("java-xml-type-mapping");
    }

    public boolean removeJavaXmlTypeMapping(int index) {
        return removeElement(index, "java-xml-type-mapping");
    }

    public void setExceptionMapping(
        int index,
        exceptionMappingType exceptionMapping) {
        setElementValue(index, "exception-mapping", exceptionMapping);
    }

    public exceptionMappingType getExceptionMapping(int index) {
        return (exceptionMappingType) getElementValue(
            "exception-mapping",
            "exceptionMappingType",
            index);
    }

    public int getExceptionMappingCount() {
        return sizeOfElement("exception-mapping");
    }

    public boolean removeExceptionMapping(int index) {
        return removeElement(index, "exception-mapping");
    }

    public void setServiceInterfaceMapping(
        int index,
        serviceInterfaceMappingType serviceInterfaceMapping) {
        setElementValue(
            index,
            "service-interface-mapping",
            serviceInterfaceMapping);
    }

    public serviceInterfaceMappingType getServiceInterfaceMapping(int index) {
        return (serviceInterfaceMappingType) getElementValue(
            "service-interface-mapping",
            "serviceInterfaceMappingType",
            index);
    }

    public int getServiceInterfaceMappingCount() {
        return sizeOfElement("service-interface-mapping");
    }

    public boolean removeServiceInterfaceMapping(int index) {
        return removeElement(index, "service-interface-mapping");
    }

    public void setServiceEndpointInterfaceMapping(
        int index,
        serviceEndpointInterfaceMappingType serviceEndpointInterfaceMapping) {
        setElementValue(
            index,
            "service-endpoint-interface-mapping",
            serviceEndpointInterfaceMapping);
    }

    public serviceEndpointInterfaceMappingType getServiceEndpointInterfaceMapping(int index) {
        return (serviceEndpointInterfaceMappingType) getElementValue(
            "service-endpoint-interface-mapping",
            "serviceEndpointInterfaceMappingType",
            index);
    }

    public int getServiceEndpointInterfaceMappingCount() {
        return sizeOfElement("service-endpoint-interface-mapping");
    }

    public boolean removeServiceEndpointInterfaceMapping(int index) {
        return removeElement(index, "service-endpoint-interface-mapping");
    }

    public void setVersion(deweyVersionType version) {
        setAttributeValue("version", version);
    }

    public deweyVersionType getVersion() {
        return (deweyVersionType) getAttributeValue(
            "version",
            "deweyVersionType");
    }

    public boolean removeVersion() {
        return removeAttribute("version");
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
