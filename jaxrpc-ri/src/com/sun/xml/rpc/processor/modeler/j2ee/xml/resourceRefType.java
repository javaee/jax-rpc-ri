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

// @(#) 1.1 jsr109ri/src/java/com/ibm/webservices/ri/xml/jaxrpcmap1_1/resourceRefType.java, jsr109ri, jsr10911, b0240.03 9/30/02 11:34:10 [10/7/02 11:55:22]
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
* This class represents the complex type <resourceRefType>
*/
public class resourceRefType extends ComplexType {
    public resourceRefType() {
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

    public void setResRefName(jndiNameType resRefName) {
        setElementValue("res-ref-name", resRefName);
    }

    public jndiNameType getResRefName() {
        return (jndiNameType) getElementValue("res-ref-name", "jndiNameType");
    }

    public boolean removeResRefName() {
        return removeElement("res-ref-name");
    }

    public void setResType(fullyQualifiedClassType resType) {
        setElementValue("res-type", resType);
    }

    public fullyQualifiedClassType getResType() {
        return (fullyQualifiedClassType) getElementValue(
            "res-type",
            "fullyQualifiedClassType");
    }

    public boolean removeResType() {
        return removeElement("res-type");
    }

    public void setResAuth(resAuthType resAuth) {
        setElementValue("res-auth", resAuth);
    }

    public resAuthType getResAuth() {
        return (resAuthType) getElementValue("res-auth", "resAuthType");
    }

    public boolean removeResAuth() {
        return removeElement("res-auth");
    }

    public void setResSharingScope(resSharingScopeType resSharingScope) {
        setElementValue("res-sharing-scope", resSharingScope);
    }

    public resSharingScopeType getResSharingScope() {
        return (resSharingScopeType) getElementValue(
            "res-sharing-scope",
            "resSharingScopeType");
    }

    public boolean removeResSharingScope() {
        return removeElement("res-sharing-scope");
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
