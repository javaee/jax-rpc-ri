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

// @(#) 1.1 jsr109ri/src/java/com/ibm/webservices/ri/xml/jaxrpcmap1_1/messageDestinationRefType.java, jsr109ri, jsr10911, b0240.03 9/30/02 11:33:47 [10/7/02 11:55:20]
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
* This class represents the complex type <messageDestinationRefType>
*/
public class messageDestinationRefType extends ComplexType {
    public messageDestinationRefType() {
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

    public void setMessageDestinationRefName(jndiNameType messageDestinationRefName) {
        setElementValue(
            "message-destination-ref-name",
            messageDestinationRefName);
    }

    public jndiNameType getMessageDestinationRefName() {
        return (jndiNameType) getElementValue(
            "message-destination-ref-name",
            "jndiNameType");
    }

    public boolean removeMessageDestinationRefName() {
        return removeElement("message-destination-ref-name");
    }

    public void setMessageDestinationType(messageDestinationTypeType messageDestinationType) {
        setElementValue("message-destination-type", messageDestinationType);
    }

    public messageDestinationTypeType getMessageDestinationType() {
        return (messageDestinationTypeType) getElementValue(
            "message-destination-type",
            "messageDestinationTypeType");
    }

    public boolean removeMessageDestinationType() {
        return removeElement("message-destination-type");
    }

    public void setMessageDestinationUsage(messageDestinationUsageType messageDestinationUsage) {
        setElementValue("message-destination-usage", messageDestinationUsage);
    }

    public messageDestinationUsageType getMessageDestinationUsage() {
        return (messageDestinationUsageType) getElementValue(
            "message-destination-usage",
            "messageDestinationUsageType");
    }

    public boolean removeMessageDestinationUsage() {
        return removeElement("message-destination-usage");
    }

    public void setMessageDestinationLink(messageDestinationLinkType messageDestinationLink) {
        setElementValue("message-destination-link", messageDestinationLink);
    }

    public messageDestinationLinkType getMessageDestinationLink() {
        return (messageDestinationLinkType) getElementValue(
            "message-destination-link",
            "messageDestinationLinkType");
    }

    public boolean removeMessageDestinationLink() {
        return removeElement("message-destination-link");
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
