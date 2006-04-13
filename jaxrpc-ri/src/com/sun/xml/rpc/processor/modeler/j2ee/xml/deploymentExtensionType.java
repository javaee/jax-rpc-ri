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

// @(#) 1.1 jsr109ri/src/java/com/ibm/webservices/ri/xml/jaxrpcmap1_1/deploymentExtensionType.java, jsr109ri, jsr10911, b0240.03 9/30/02 11:33:06 [10/7/02 11:55:17]
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
* This class represents the complex type <deploymentExtensionType>
*/
public class deploymentExtensionType extends ComplexType {
    public deploymentExtensionType() {
    }

    public void setExtensionElement(
        int index,
        extensibleType extensionElement) {
        setElementValue(index, "extension-element", extensionElement);
    }

    public extensibleType getExtensionElement(int index) {
        return (extensibleType) getElementValue(
            "extension-element",
            "extensibleType",
            index);
    }

    public int getExtensionElementCount() {
        return sizeOfElement("extension-element");
    }

    public boolean removeExtensionElement(int index) {
        return removeElement(index, "extension-element");
    }

    public void setNamespace(String namespace) {
        setAttributeValue("namespace", namespace);
    }

    public String getNamespace() {
        return getAttributeValue("namespace");
    }

    public boolean removeNamespace() {
        return removeAttribute("namespace");
    }

    public void setMustUnderstand(boolean mustUnderstand) {
        setAttributeValue("mustUnderstand", mustUnderstand);
    }

    public boolean getMustUnderstand() {
        return getAttributeBooleanValue("mustUnderstand");
    }

    public boolean removeMustUnderstand() {
        return removeAttribute("mustUnderstand");
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
