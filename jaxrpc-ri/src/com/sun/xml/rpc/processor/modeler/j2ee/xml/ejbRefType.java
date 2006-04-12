/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

// @(#) 1.1 jsr109ri/src/java/com/ibm/webservices/ri/xml/jaxrpcmap1_1/ejbRefType.java, jsr109ri, jsr10911, b0240.03 9/30/02 11:33:16 [10/7/02 11:55:17]
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
* This class represents the complex type <ejbRefType>
*/
public class ejbRefType extends ComplexType {
    public ejbRefType() {
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

    public void setEjbRefName(ejbRefNameType ejbRefName) {
        setElementValue("ejb-ref-name", ejbRefName);
    }

    public ejbRefNameType getEjbRefName() {
        return (ejbRefNameType) getElementValue(
            "ejb-ref-name",
            "ejbRefNameType");
    }

    public boolean removeEjbRefName() {
        return removeElement("ejb-ref-name");
    }

    public void setEjbRefType(ejbRefTypeType ejbRefType) {
        setElementValue("ejb-ref-type", ejbRefType);
    }

    public ejbRefTypeType getEjbRefType() {
        return (ejbRefTypeType) getElementValue(
            "ejb-ref-type",
            "ejbRefTypeType");
    }

    public boolean removeEjbRefType() {
        return removeElement("ejb-ref-type");
    }

    public void setHome(homeType home) {
        setElementValue("home", home);
    }

    public homeType getHome() {
        return (homeType) getElementValue("home", "homeType");
    }

    public boolean removeHome() {
        return removeElement("home");
    }

    public void setRemote(remoteType remote) {
        setElementValue("remote", remote);
    }

    public remoteType getRemote() {
        return (remoteType) getElementValue("remote", "remoteType");
    }

    public boolean removeRemote() {
        return removeElement("remote");
    }

    public void setEjbLink(ejbLinkType ejbLink) {
        setElementValue("ejb-link", ejbLink);
    }

    public ejbLinkType getEjbLink() {
        return (ejbLinkType) getElementValue("ejb-link", "ejbLinkType");
    }

    public boolean removeEjbLink() {
        return removeElement("ejb-link");
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
