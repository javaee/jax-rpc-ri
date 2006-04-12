/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

// @(#) 1.1 jsr109ri/src/java/com/ibm/webservices/ri/xml/jaxrpcmap1_1/wsdlReturnValueMappingType.java, jsr109ri, jsr10911, b0240.03 9/30/02 11:34:52 [10/7/02 11:55:24]
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
* This class represents the complex type <wsdlReturnValueMappingType>
*/
public class wsdlReturnValueMappingType extends ComplexType {
    public wsdlReturnValueMappingType() {
    }

    public void setMethodReturnValue(fullyQualifiedClassType methodReturnValue) {
        setElementValue("method-return-value", methodReturnValue);
    }

    public fullyQualifiedClassType getMethodReturnValue() {
        return (fullyQualifiedClassType) getElementValue(
            "method-return-value",
            "fullyQualifiedClassType");
    }

    public boolean removeMethodReturnValue() {
        return removeElement("method-return-value");
    }

    public void setWsdlMessage(wsdlMessageType wsdlMessage) {
        setElementValue("wsdl-message", wsdlMessage);
    }

    public wsdlMessageType getWsdlMessage() {
        return (wsdlMessageType) getElementValue(
            "wsdl-message",
            "wsdlMessageType");
    }

    public boolean removeWsdlMessage() {
        return removeElement("wsdl-message");
    }

    public void setWsdlMessagePartName(wsdlMessagePartNameType wsdlMessagePartName) {
        setElementValue("wsdl-message-part-name", wsdlMessagePartName);
    }

    public wsdlMessagePartNameType getWsdlMessagePartName() {
        return (wsdlMessagePartNameType) getElementValue(
            "wsdl-message-part-name",
            "wsdlMessagePartNameType");
    }

    public boolean removeWsdlMessagePartName() {
        return removeElement("wsdl-message-part-name");
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
