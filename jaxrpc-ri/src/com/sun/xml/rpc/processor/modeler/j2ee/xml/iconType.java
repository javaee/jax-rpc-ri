/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

// @(#) 1.1 jsr109ri/src/java/com/ibm/webservices/ri/xml/jaxrpcmap1_1/iconType.java, jsr109ri, jsr10911, b0240.03 9/30/02 11:33:32 [10/7/02 11:55:19]
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
* This class represents the complex type <iconType>
*/
public class iconType extends ComplexType {
    public iconType() {
    }

    public void setSmallIcon(pathType smallIcon) {
        setElementValue("small-icon", smallIcon);
    }

    public pathType getSmallIcon() {
        return (pathType) getElementValue("small-icon", "pathType");
    }

    public boolean removeSmallIcon() {
        return removeElement("small-icon");
    }

    public void setLargeIcon(pathType largeIcon) {
        setElementValue("large-icon", largeIcon);
    }

    public pathType getLargeIcon() {
        return (pathType) getElementValue("large-icon", "pathType");
    }

    public boolean removeLargeIcon() {
        return removeElement("large-icon");
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
