/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2002 International Business Machines Corp. 2002. All rights reserved.
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
