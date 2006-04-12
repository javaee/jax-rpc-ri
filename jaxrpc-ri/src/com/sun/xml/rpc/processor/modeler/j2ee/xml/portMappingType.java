/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

// @(#) 1.1 jsr109ri/src/java/com/ibm/webservices/ri/xml/jaxrpcmap1_1/portMappingType.java, jsr109ri, jsr10911, b0240.03 9/30/02 11:34:01 [10/7/02 11:55:21]
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
* This class represents the complex type <portMappingType>
*/
public class portMappingType extends ComplexType {
    public portMappingType() {
    }

    public void setPortName(string portName) {
        setElementValue("port-name", portName);
    }

    public string getPortName() {
        return (string) getElementValue("port-name", "string");
    }

    public boolean removePortName() {
        return removeElement("port-name");
    }

    public void setJavaPortName(string javaPortName) {
        setElementValue("java-port-name", javaPortName);
    }

    public string getJavaPortName() {
        return (string) getElementValue("java-port-name", "string");
    }

    public boolean removeJavaPortName() {
        return removeElement("java-port-name");
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
