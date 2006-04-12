/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

// @(#) 1.1 jsr109ri/src/java/com/ibm/webservices/ri/xml/jaxrpcmap1_1/portComponentRefType.java, jsr109ri, jsr10911, b0240.03 9/30/02 11:34:00 [10/7/02 11:55:21]
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
* This class represents the complex type <portComponentRefType>
*/
public class portComponentRefType extends ComplexType {
    public portComponentRefType() {
    }

    public void setServiceEndpointInterface(String serviceEndpointInterface) {
        setElementValue("service-endpoint-interface", serviceEndpointInterface);
    }

    public String getServiceEndpointInterface() {
        return getElementValue("service-endpoint-interface");
    }

    public boolean removeServiceEndpointInterface() {
        return removeElement("service-endpoint-interface");
    }

    public void setPortComponentLink(String portComponentLink) {
        setElementValue("port-component-link", portComponentLink);
    }

    public String getPortComponentLink() {
        return getElementValue("port-component-link");
    }

    public boolean removePortComponentLink() {
        return removeElement("port-component-link");
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
