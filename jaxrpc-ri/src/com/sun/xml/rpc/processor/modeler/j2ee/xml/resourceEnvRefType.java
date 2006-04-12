/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

// @(#) 1.1 jsr109ri/src/java/com/ibm/webservices/ri/xml/jaxrpcmap1_1/resourceEnvRefType.java, jsr109ri, jsr10911, b0240.03 9/30/02 11:34:09 [10/7/02 11:55:22]
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
* This class represents the complex type <resourceEnvRefType>
*/
public class resourceEnvRefType extends ComplexType {
    public resourceEnvRefType() {
    }

    public void setDescription(int index, descriptionType description) {
        setElementValue(index, "description", description);
    }

    public descriptionType getDescription(int index) {
        return (descriptionType) getElementValue(
            "description",
            "descriptionType",
            index);
    }

    public int getDescriptionCount() {
        return sizeOfElement("description");
    }

    public boolean removeDescription(int index) {
        return removeElement(index, "description");
    }

    public void setResourceEnvRefName(jndiNameType resourceEnvRefName) {
        setElementValue("resource-env-ref-name", resourceEnvRefName);
    }

    public jndiNameType getResourceEnvRefName() {
        return (jndiNameType) getElementValue(
            "resource-env-ref-name",
            "jndiNameType");
    }

    public boolean removeResourceEnvRefName() {
        return removeElement("resource-env-ref-name");
    }

    public void setResourceEnvRefType(fullyQualifiedClassType resourceEnvRefType) {
        setElementValue("resource-env-ref-type", resourceEnvRefType);
    }

    public fullyQualifiedClassType getResourceEnvRefType() {
        return (fullyQualifiedClassType) getElementValue(
            "resource-env-ref-type",
            "fullyQualifiedClassType");
    }

    public boolean removeResourceEnvRefType() {
        return removeElement("resource-env-ref-type");
    }

    public void setDeploymentExtension(
        int index,
        deploymentExtensionType deploymentExtension) {
        setElementValue(index, "deployment-extension", deploymentExtension);
    }

    public deploymentExtensionType getDeploymentExtension(int index) {
        return (deploymentExtensionType) getElementValue(
            "deployment-extension",
            "deploymentExtensionType",
            index);
    }

    public int getDeploymentExtensionCount() {
        return sizeOfElement("deployment-extension");
    }

    public boolean removeDeploymentExtension(int index) {
        return removeElement(index, "deployment-extension");
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
