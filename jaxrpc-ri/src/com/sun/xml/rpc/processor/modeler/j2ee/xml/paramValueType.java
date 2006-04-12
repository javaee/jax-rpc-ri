/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

// @(#) 1.1 jsr109ri/src/java/com/ibm/webservices/ri/xml/jaxrpcmap1_1/paramValueType.java, jsr109ri, jsr10911, b0240.03 9/30/02 11:33:55 [10/7/02 11:55:21]
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
* This class represents the complex type <paramValueType>
*/
public class paramValueType extends ComplexType {
    public paramValueType() {
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

    public void setParamName(string paramName) {
        setElementValue("param-name", paramName);
    }

    public string getParamName() {
        return (string) getElementValue("param-name", "string");
    }

    public boolean removeParamName() {
        return removeElement("param-name");
    }

    public void setParamValue(xsdStringType paramValue) {
        setElementValue("param-value", paramValue);
    }

    public xsdStringType getParamValue() {
        return (xsdStringType) getElementValue("param-value", "xsdStringType");
    }

    public boolean removeParamValue() {
        return removeElement("param-value");
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
