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

// @(#) 1.1 jsr109ri/src/java/com/ibm/webservices/ri/xml/jaxrpcmap1_1/javaWsdlMappingFactory.java, jsr109ri, jsr10911, b0240.03 9/30/02 11:33:36 [10/7/02 11:55:19]
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
* Provides convenience methods for creating Java beans for elements
* in this XML document
*/
public class javaWsdlMappingFactory extends Factory {
    public javaWsdlMappingFactory() {
        super();
    }

    /**
    * Create the Java bean javaWsdlMapping for the root element
    *   @param rootElementname The tag for the root element
    *   @return javaWsdlMapping The Java bean representing this element
    */
    public javaWsdlMapping createRoot(String rootElementname) {
        return (javaWsdlMapping) createRootDOMFromComplexType(
            "javaWsdlMapping",
            rootElementname);
    }

    /**
    * Create the Java bean javaWsdlMapping by loading the XML file
    *   @param filename The XML file name
    *   @return javaWsdlMapping The Java bean representing the root element
    */
    public javaWsdlMapping loadDocument(String filename) {
        return (javaWsdlMapping) loadDocument("javaWsdlMapping", filename);
    }

    /**
    * Create the Java bean portComponentRefType for the element
    *   @param elementName The tag for the element
    *   @return portComponentRefType The Java bean representing this element
    */
    public portComponentRefType createportComponentRefType(String elementName) {
        return (portComponentRefType) createDOMElementFromComplexType(
            "portComponentRefType",
            elementName);
    }

    /**
    * Create the Java bean serviceRefType for the element
    *   @param elementName The tag for the element
    *   @return serviceRefType The Java bean representing this element
    */
    public serviceRefType createserviceRefType(String elementName) {
        return (serviceRefType) createDOMElementFromComplexType(
            "serviceRefType",
            elementName);
    }

    /**
    * Create the Java bean serviceRef_handlerType for the element
    *   @param elementName The tag for the element
    *   @return serviceRef_handlerType The Java bean representing this element
    */
    public serviceRef_handlerType createserviceRef_handlerType(String elementName) {
        return (serviceRef_handlerType) createDOMElementFromComplexType(
            "serviceRef_handlerType",
            elementName);
    }

    /**
    * Create the Java bean deploymentExtensionType for the element
    *   @param elementName The tag for the element
    *   @return deploymentExtensionType The Java bean representing this element
    */
    public deploymentExtensionType createdeploymentExtensionType(String elementName) {
        return (deploymentExtensionType) createDOMElementFromComplexType(
            "deploymentExtensionType",
            elementName);
    }

    /**
    * Create the Java bean descriptionType for the element
    *   @param elementName The tag for the element
    *   @return descriptionType The Java bean representing this element
    */
    public descriptionType createdescriptionType(String elementName) {
        return (descriptionType) createDOMElementFromComplexType(
            "descriptionType",
            elementName);
    }

    /**
    * Create the Java bean deweyVersionType for the element
    *   @param elementName The tag for the element
    *   @return deweyVersionType The Java bean representing this element
    */
    public deweyVersionType createdeweyVersionType(String elementName) {
        return (deweyVersionType) createDOMElementFromSimpleType(
            "deweyVersionType",
            elementName);
    }

    /**
    * Create the Java bean displayNameType for the element
    *   @param elementName The tag for the element
    *   @return displayNameType The Java bean representing this element
    */
    public displayNameType createdisplayNameType(String elementName) {
        return (displayNameType) createDOMElementFromComplexType(
            "displayNameType",
            elementName);
    }

    /**
    * Create the Java bean ejbLinkType for the element
    *   @param elementName The tag for the element
    *   @return ejbLinkType The Java bean representing this element
    */
    public ejbLinkType createejbLinkType(String elementName) {
        return (ejbLinkType) createDOMElementFromComplexType(
            "ejbLinkType",
            elementName);
    }

    /**
    * Create the Java bean ejbLocalRefType for the element
    *   @param elementName The tag for the element
    *   @return ejbLocalRefType The Java bean representing this element
    */
    public ejbLocalRefType createejbLocalRefType(String elementName) {
        return (ejbLocalRefType) createDOMElementFromComplexType(
            "ejbLocalRefType",
            elementName);
    }

    /**
    * Create the Java bean ejbRefNameType for the element
    *   @param elementName The tag for the element
    *   @return ejbRefNameType The Java bean representing this element
    */
    public ejbRefNameType createejbRefNameType(String elementName) {
        return (ejbRefNameType) createDOMElementFromComplexType(
            "ejbRefNameType",
            elementName);
    }

    /**
    * Create the Java bean ejbRefTypeType for the element
    *   @param elementName The tag for the element
    *   @return ejbRefTypeType The Java bean representing this element
    */
    public ejbRefTypeType createejbRefTypeType(String elementName) {
        return (ejbRefTypeType) createDOMElementFromComplexType(
            "ejbRefTypeType",
            elementName);
    }

    /**
    * Create the Java bean ejbRefType for the element
    *   @param elementName The tag for the element
    *   @return ejbRefType The Java bean representing this element
    */
    public ejbRefType createejbRefType(String elementName) {
        return (ejbRefType) createDOMElementFromComplexType(
            "ejbRefType",
            elementName);
    }

    /**
    * Create the Java bean emptyType for the element
    *   @param elementName The tag for the element
    *   @return emptyType The Java bean representing this element
    */
    public emptyType createemptyType(String elementName) {
        return (emptyType) createDOMElementFromComplexType(
            "emptyType",
            elementName);
    }

    /**
    * Create the Java bean envEntryTypeValuesType for the element
    *   @param elementName The tag for the element
    *   @return envEntryTypeValuesType The Java bean representing this element
    */
    public envEntryTypeValuesType createenvEntryTypeValuesType(String elementName) {
        return (envEntryTypeValuesType) createDOMElementFromComplexType(
            "envEntryTypeValuesType",
            elementName);
    }

    /**
    * Create the Java bean envEntryType for the element
    *   @param elementName The tag for the element
    *   @return envEntryType The Java bean representing this element
    */
    public envEntryType createenvEntryType(String elementName) {
        return (envEntryType) createDOMElementFromComplexType(
            "envEntryType",
            elementName);
    }

    /**
    * Create the Java bean extensibleType for the element
    *   @param elementName The tag for the element
    *   @return extensibleType The Java bean representing this element
    */
    public extensibleType createextensibleType(String elementName) {
        return (extensibleType) createDOMElementFromComplexType(
            "extensibleType",
            elementName);
    }

    /**
    * Create the Java bean fullyQualifiedClassType for the element
    *   @param elementName The tag for the element
    *   @return fullyQualifiedClassType The Java bean representing this element
    */
    public fullyQualifiedClassType createfullyQualifiedClassType(String elementName) {
        return (fullyQualifiedClassType) createDOMElementFromComplexType(
            "fullyQualifiedClassType",
            elementName);
    }

    /**
    * Create the Java bean genericBooleanType for the element
    *   @param elementName The tag for the element
    *   @return genericBooleanType The Java bean representing this element
    */
    public genericBooleanType creategenericBooleanType(String elementName) {
        return (genericBooleanType) createDOMElementFromComplexType(
            "genericBooleanType",
            elementName);
    }

    /**
    * Create the Java bean homeType for the element
    *   @param elementName The tag for the element
    *   @return homeType The Java bean representing this element
    */
    public homeType createhomeType(String elementName) {
        return (homeType) createDOMElementFromComplexType(
            "homeType",
            elementName);
    }

    /**
    * Create the Java bean iconType for the element
    *   @param elementName The tag for the element
    *   @return iconType The Java bean representing this element
    */
    public iconType createiconType(String elementName) {
        return (iconType) createDOMElementFromComplexType(
            "iconType",
            elementName);
    }

    /**
    * Create the Java bean javaIdentifierType for the element
    *   @param elementName The tag for the element
    *   @return javaIdentifierType The Java bean representing this element
    */
    public javaIdentifierType createjavaIdentifierType(String elementName) {
        return (javaIdentifierType) createDOMElementFromComplexType(
            "javaIdentifierType",
            elementName);
    }

    /**
    * Create the Java bean jndiNameType for the element
    *   @param elementName The tag for the element
    *   @return jndiNameType The Java bean representing this element
    */
    public jndiNameType createjndiNameType(String elementName) {
        return (jndiNameType) createDOMElementFromComplexType(
            "jndiNameType",
            elementName);
    }

    /**
    * Create the Java bean localHomeType for the element
    *   @param elementName The tag for the element
    *   @return localHomeType The Java bean representing this element
    */
    public localHomeType createlocalHomeType(String elementName) {
        return (localHomeType) createDOMElementFromComplexType(
            "localHomeType",
            elementName);
    }

    /**
    * Create the Java bean localType for the element
    *   @param elementName The tag for the element
    *   @return localType The Java bean representing this element
    */
    public localType createlocalType(String elementName) {
        return (localType) createDOMElementFromComplexType(
            "localType",
            elementName);
    }

    /**
    * Create the Java bean messageDestinationLinkType for the element
    *   @param elementName The tag for the element
    *   @return messageDestinationLinkType The Java bean representing this element
    */
    public messageDestinationLinkType createmessageDestinationLinkType(String elementName) {
        return (messageDestinationLinkType) createDOMElementFromComplexType(
            "messageDestinationLinkType",
            elementName);
    }

    /**
    * Create the Java bean messageDestinationRefType for the element
    *   @param elementName The tag for the element
    *   @return messageDestinationRefType The Java bean representing this element
    */
    public messageDestinationRefType createmessageDestinationRefType(String elementName) {
        return (messageDestinationRefType) createDOMElementFromComplexType(
            "messageDestinationRefType",
            elementName);
    }

    /**
    * Create the Java bean messageDestinationTypeType for the element
    *   @param elementName The tag for the element
    *   @return messageDestinationTypeType The Java bean representing this element
    */
    public messageDestinationTypeType createmessageDestinationTypeType(String elementName) {
        return (messageDestinationTypeType) createDOMElementFromComplexType(
            "messageDestinationTypeType",
            elementName);
    }

    /**
    * Create the Java bean messageDestinationUsageType for the element
    *   @param elementName The tag for the element
    *   @return messageDestinationUsageType The Java bean representing this element
    */
    public messageDestinationUsageType createmessageDestinationUsageType(String elementName) {
        return (messageDestinationUsageType) createDOMElementFromComplexType(
            "messageDestinationUsageType",
            elementName);
    }

    /**
    * Create the Java bean messageDestinationType for the element
    *   @param elementName The tag for the element
    *   @return messageDestinationType The Java bean representing this element
    */
    public messageDestinationType createmessageDestinationType(String elementName) {
        return (messageDestinationType) createDOMElementFromComplexType(
            "messageDestinationType",
            elementName);
    }

    /**
    * Create the Java bean paramValueType for the element
    *   @param elementName The tag for the element
    *   @return paramValueType The Java bean representing this element
    */
    public paramValueType createparamValueType(String elementName) {
        return (paramValueType) createDOMElementFromComplexType(
            "paramValueType",
            elementName);
    }

    /**
    * Create the Java bean pathType for the element
    *   @param elementName The tag for the element
    *   @return pathType The Java bean representing this element
    */
    public pathType createpathType(String elementName) {
        return (pathType) createDOMElementFromComplexType(
            "pathType",
            elementName);
    }

    /**
    * Create the Java bean remoteType for the element
    *   @param elementName The tag for the element
    *   @return remoteType The Java bean representing this element
    */
    public remoteType createremoteType(String elementName) {
        return (remoteType) createDOMElementFromComplexType(
            "remoteType",
            elementName);
    }

    /**
    * Create the Java bean resAuthType for the element
    *   @param elementName The tag for the element
    *   @return resAuthType The Java bean representing this element
    */
    public resAuthType createresAuthType(String elementName) {
        return (resAuthType) createDOMElementFromComplexType(
            "resAuthType",
            elementName);
    }

    /**
    * Create the Java bean resSharingScopeType for the element
    *   @param elementName The tag for the element
    *   @return resSharingScopeType The Java bean representing this element
    */
    public resSharingScopeType createresSharingScopeType(String elementName) {
        return (resSharingScopeType) createDOMElementFromComplexType(
            "resSharingScopeType",
            elementName);
    }

    /**
    * Create the Java bean resourceEnvRefType for the element
    *   @param elementName The tag for the element
    *   @return resourceEnvRefType The Java bean representing this element
    */
    public resourceEnvRefType createresourceEnvRefType(String elementName) {
        return (resourceEnvRefType) createDOMElementFromComplexType(
            "resourceEnvRefType",
            elementName);
    }

    /**
    * Create the Java bean resourceRefType for the element
    *   @param elementName The tag for the element
    *   @return resourceRefType The Java bean representing this element
    */
    public resourceRefType createresourceRefType(String elementName) {
        return (resourceRefType) createDOMElementFromComplexType(
            "resourceRefType",
            elementName);
    }

    /**
    * Create the Java bean roleNameType for the element
    *   @param elementName The tag for the element
    *   @return roleNameType The Java bean representing this element
    */
    public roleNameType createroleNameType(String elementName) {
        return (roleNameType) createDOMElementFromComplexType(
            "roleNameType",
            elementName);
    }

    /**
    * Create the Java bean runAsType for the element
    *   @param elementName The tag for the element
    *   @return runAsType The Java bean representing this element
    */
    public runAsType createrunAsType(String elementName) {
        return (runAsType) createDOMElementFromComplexType(
            "runAsType",
            elementName);
    }

    /**
    * Create the Java bean securityRoleRefType for the element
    *   @param elementName The tag for the element
    *   @return securityRoleRefType The Java bean representing this element
    */
    public securityRoleRefType createsecurityRoleRefType(String elementName) {
        return (securityRoleRefType) createDOMElementFromComplexType(
            "securityRoleRefType",
            elementName);
    }

    /**
    * Create the Java bean securityRoleType for the element
    *   @param elementName The tag for the element
    *   @return securityRoleType The Java bean representing this element
    */
    public securityRoleType createsecurityRoleType(String elementName) {
        return (securityRoleType) createDOMElementFromComplexType(
            "securityRoleType",
            elementName);
    }

    /**
    * Create the Java bean string for the element
    *   @param elementName The tag for the element
    *   @return string The Java bean representing this element
    */
    public string createstring(String elementName) {
        return (string) createDOMElementFromComplexType("string", elementName);
    }

    /**
    * Create the Java bean trueFalseType for the element
    *   @param elementName The tag for the element
    *   @return trueFalseType The Java bean representing this element
    */
    public trueFalseType createtrueFalseType(String elementName) {
        return (trueFalseType) createDOMElementFromComplexType(
            "trueFalseType",
            elementName);
    }

    /**
    * Create the Java bean urlPatternType for the element
    *   @param elementName The tag for the element
    *   @return urlPatternType The Java bean representing this element
    */
    public urlPatternType createurlPatternType(String elementName) {
        return (urlPatternType) createDOMElementFromComplexType(
            "urlPatternType",
            elementName);
    }

    /**
    * Create the Java bean warPathType for the element
    *   @param elementName The tag for the element
    *   @return warPathType The Java bean representing this element
    */
    public warPathType createwarPathType(String elementName) {
        return (warPathType) createDOMElementFromComplexType(
            "warPathType",
            elementName);
    }

    /**
    * Create the Java bean xsdAnyURIType for the element
    *   @param elementName The tag for the element
    *   @return xsdAnyURIType The Java bean representing this element
    */
    public xsdAnyURIType createxsdAnyURIType(String elementName) {
        return (xsdAnyURIType) createDOMElementFromComplexType(
            "xsdAnyURIType",
            elementName);
    }

    /**
    * Create the Java bean xsdBooleanType for the element
    *   @param elementName The tag for the element
    *   @return xsdBooleanType The Java bean representing this element
    */
    public xsdBooleanType createxsdBooleanType(String elementName) {
        return (xsdBooleanType) createDOMElementFromComplexType(
            "xsdBooleanType",
            elementName);
    }

    /**
    * Create the Java bean xsdIntegerType for the element
    *   @param elementName The tag for the element
    *   @return xsdIntegerType The Java bean representing this element
    */
    public xsdIntegerType createxsdIntegerType(String elementName) {
        return (xsdIntegerType) createDOMElementFromComplexType(
            "xsdIntegerType",
            elementName);
    }

    /**
    * Create the Java bean xsdNMTOKENType for the element
    *   @param elementName The tag for the element
    *   @return xsdNMTOKENType The Java bean representing this element
    */
    public xsdNMTOKENType createxsdNMTOKENType(String elementName) {
        return (xsdNMTOKENType) createDOMElementFromComplexType(
            "xsdNMTOKENType",
            elementName);
    }

    /**
    * Create the Java bean xsdNonNegativeIntegerType for the element
    *   @param elementName The tag for the element
    *   @return xsdNonNegativeIntegerType The Java bean representing this element
    */
    public xsdNonNegativeIntegerType createxsdNonNegativeIntegerType(String elementName) {
        return (xsdNonNegativeIntegerType) createDOMElementFromComplexType(
            "xsdNonNegativeIntegerType",
            elementName);
    }

    /**
    * Create the Java bean xsdPositiveIntegerType for the element
    *   @param elementName The tag for the element
    *   @return xsdPositiveIntegerType The Java bean representing this element
    */
    public xsdPositiveIntegerType createxsdPositiveIntegerType(String elementName) {
        return (xsdPositiveIntegerType) createDOMElementFromComplexType(
            "xsdPositiveIntegerType",
            elementName);
    }

    /**
    * Create the Java bean xsdQNameType for the element
    *   @param elementName The tag for the element
    *   @return xsdQNameType The Java bean representing this element
    */
    public xsdQNameType createxsdQNameType(String elementName) {
        return (xsdQNameType) createDOMElementFromComplexType(
            "xsdQNameType",
            elementName);
    }

    /**
    * Create the Java bean xsdStringType for the element
    *   @param elementName The tag for the element
    *   @return xsdStringType The Java bean representing this element
    */
    public xsdStringType createxsdStringType(String elementName) {
        return (xsdStringType) createDOMElementFromComplexType(
            "xsdStringType",
            elementName);
    }

    /**
    * Create the Java bean javaWsdlMapping for the element
    *   @param elementName The tag for the element
    *   @return javaWsdlMapping The Java bean representing this element
    */
    public javaWsdlMapping createjavaWsdlMapping(String elementName) {
        return (javaWsdlMapping) createDOMElementFromComplexType(
            "javaWsdlMapping",
            elementName);
    }

    /**
    * Create the Java bean constructorParameterOrderType for the element
    *   @param elementName The tag for the element
    *   @return constructorParameterOrderType The Java bean representing this element
    */
    public constructorParameterOrderType createconstructorParameterOrderType(String elementName) {
        return (constructorParameterOrderType) createDOMElementFromComplexType(
            "constructorParameterOrderType",
            elementName);
    }

    /**
    * Create the Java bean exceptionMappingType for the element
    *   @param elementName The tag for the element
    *   @return exceptionMappingType The Java bean representing this element
    */
    public exceptionMappingType createexceptionMappingType(String elementName) {
        return (exceptionMappingType) createDOMElementFromComplexType(
            "exceptionMappingType",
            elementName);
    }

    /**
    * Create the Java bean javaWsdlMappingType for the element
    *   @param elementName The tag for the element
    *   @return javaWsdlMappingType The Java bean representing this element
    */
    public javaWsdlMappingType createjavaWsdlMappingType(String elementName) {
        return (javaWsdlMappingType) createDOMElementFromComplexType(
            "javaWsdlMappingType",
            elementName);
    }

    /**
    * Create the Java bean javaXmlTypeMappingType for the element
    *   @param elementName The tag for the element
    *   @return javaXmlTypeMappingType The Java bean representing this element
    */
    public javaXmlTypeMappingType createjavaXmlTypeMappingType(String elementName) {
        return (javaXmlTypeMappingType) createDOMElementFromComplexType(
            "javaXmlTypeMappingType",
            elementName);
    }

    /**
    * Create the Java bean methodParamPartsMappingType for the element
    *   @param elementName The tag for the element
    *   @return methodParamPartsMappingType The Java bean representing this element
    */
    public methodParamPartsMappingType createmethodParamPartsMappingType(String elementName) {
        return (methodParamPartsMappingType) createDOMElementFromComplexType(
            "methodParamPartsMappingType",
            elementName);
    }

    /**
    * Create the Java bean packageMappingType for the element
    *   @param elementName The tag for the element
    *   @return packageMappingType The Java bean representing this element
    */
    public packageMappingType createpackageMappingType(String elementName) {
        return (packageMappingType) createDOMElementFromComplexType(
            "packageMappingType",
            elementName);
    }

    /**
    * Create the Java bean parameterModeType for the element
    *   @param elementName The tag for the element
    *   @return parameterModeType The Java bean representing this element
    */
    public parameterModeType createparameterModeType(String elementName) {
        return (parameterModeType) createDOMElementFromComplexType(
            "parameterModeType",
            elementName);
    }

    /**
    * Create the Java bean portMappingType for the element
    *   @param elementName The tag for the element
    *   @return portMappingType The Java bean representing this element
    */
    public portMappingType createportMappingType(String elementName) {
        return (portMappingType) createDOMElementFromComplexType(
            "portMappingType",
            elementName);
    }

    /**
    * Create the Java bean qnameScopeType for the element
    *   @param elementName The tag for the element
    *   @return qnameScopeType The Java bean representing this element
    */
    public qnameScopeType createqnameScopeType(String elementName) {
        return (qnameScopeType) createDOMElementFromComplexType(
            "qnameScopeType",
            elementName);
    }

    /**
    * Create the Java bean serviceEndpointInterfaceMappingType for the element
    *   @param elementName The tag for the element
    *   @return serviceEndpointInterfaceMappingType The Java bean representing this element
    */
    public serviceEndpointInterfaceMappingType createserviceEndpointInterfaceMappingType(String elementName) {
        return (serviceEndpointInterfaceMappingType) createDOMElementFromComplexType(
            "serviceEndpointInterfaceMappingType",
            elementName);
    }

    /**
    * Create the Java bean serviceEndpointMethodMappingType for the element
    *   @param elementName The tag for the element
    *   @return serviceEndpointMethodMappingType The Java bean representing this element
    */
    public serviceEndpointMethodMappingType createserviceEndpointMethodMappingType(String elementName) {
        return (serviceEndpointMethodMappingType) createDOMElementFromComplexType(
            "serviceEndpointMethodMappingType",
            elementName);
    }

    /**
    * Create the Java bean serviceInterfaceMappingType for the element
    *   @param elementName The tag for the element
    *   @return serviceInterfaceMappingType The Java bean representing this element
    */
    public serviceInterfaceMappingType createserviceInterfaceMappingType(String elementName) {
        return (serviceInterfaceMappingType) createDOMElementFromComplexType(
            "serviceInterfaceMappingType",
            elementName);
    }

    /**
    * Create the Java bean variableMappingType for the element
    *   @param elementName The tag for the element
    *   @return variableMappingType The Java bean representing this element
    */
    public variableMappingType createvariableMappingType(String elementName) {
        return (variableMappingType) createDOMElementFromComplexType(
            "variableMappingType",
            elementName);
    }

    /**
    * Create the Java bean wsdlMessageMappingType for the element
    *   @param elementName The tag for the element
    *   @return wsdlMessageMappingType The Java bean representing this element
    */
    public wsdlMessageMappingType createwsdlMessageMappingType(String elementName) {
        return (wsdlMessageMappingType) createDOMElementFromComplexType(
            "wsdlMessageMappingType",
            elementName);
    }

    /**
    * Create the Java bean wsdlMessagePartNameType for the element
    *   @param elementName The tag for the element
    *   @return wsdlMessagePartNameType The Java bean representing this element
    */
    public wsdlMessagePartNameType createwsdlMessagePartNameType(String elementName) {
        return (wsdlMessagePartNameType) createDOMElementFromComplexType(
            "wsdlMessagePartNameType",
            elementName);
    }

    /**
    * Create the Java bean wsdlMessageType for the element
    *   @param elementName The tag for the element
    *   @return wsdlMessageType The Java bean representing this element
    */
    public wsdlMessageType createwsdlMessageType(String elementName) {
        return (wsdlMessageType) createDOMElementFromComplexType(
            "wsdlMessageType",
            elementName);
    }

    /**
    * Create the Java bean wsdlReturnValueMappingType for the element
    *   @param elementName The tag for the element
    *   @return wsdlReturnValueMappingType The Java bean representing this element
    */
    public wsdlReturnValueMappingType createwsdlReturnValueMappingType(String elementName) {
        return (wsdlReturnValueMappingType) createDOMElementFromComplexType(
            "wsdlReturnValueMappingType",
            elementName);
    }

}
