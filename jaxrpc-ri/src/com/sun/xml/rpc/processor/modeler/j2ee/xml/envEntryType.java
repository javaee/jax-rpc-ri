/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

// @(#) 1.1 jsr109ri/src/java/com/ibm/webservices/ri/xml/jaxrpcmap1_1/envEntryType.java, jsr109ri, jsr10911, b0240.03 9/30/02 11:33:22 [10/7/02 11:55:18]
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
* This class represents the complex type <envEntryType>
*/
public class envEntryType extends ComplexType {
    public envEntryType() {
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

    public void setEnvEntryName(jndiNameType envEntryName) {
        setElementValue("env-entry-name", envEntryName);
    }

    public jndiNameType getEnvEntryName() {
        return (jndiNameType) getElementValue("env-entry-name", "jndiNameType");
    }

    public boolean removeEnvEntryName() {
        return removeElement("env-entry-name");
    }

    public void setEnvEntryType(envEntryTypeValuesType envEntryType) {
        setElementValue("env-entry-type", envEntryType);
    }

    public envEntryTypeValuesType getEnvEntryType() {
        return (envEntryTypeValuesType) getElementValue(
            "env-entry-type",
            "envEntryTypeValuesType");
    }

    public boolean removeEnvEntryType() {
        return removeElement("env-entry-type");
    }

    public void setEnvEntryValue(xsdStringType envEntryValue) {
        setElementValue("env-entry-value", envEntryValue);
    }

    public xsdStringType getEnvEntryValue() {
        return (xsdStringType) getElementValue(
            "env-entry-value",
            "xsdStringType");
    }

    public boolean removeEnvEntryValue() {
        return removeElement("env-entry-value");
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
