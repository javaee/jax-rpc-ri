/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

// @(#) 1.1 jsr109ri/src/java/com/ibm/webservices/ri/xml/jaxrpcmap1_1/packageMappingType.java, jsr109ri, jsr10911, b0240.03 9/30/02 11:33:54 [10/7/02 11:55:20]
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
* This class represents the complex type <packageMappingType>
*/
public class packageMappingType extends ComplexType {
    public packageMappingType() {
    }

    public void setPackageType(fullyQualifiedClassType packageType) {
        setElementValue("package-type", packageType);
    }

    public fullyQualifiedClassType getPackageType() {
        return (fullyQualifiedClassType) getElementValue(
            "package-type",
            "fullyQualifiedClassType");
    }

    public boolean removePackageType() {
        return removeElement("package-type");
    }

    public void setNamespaceURI(xsdAnyURIType namespaceURI) {
        setElementValue("namespaceURI", namespaceURI);
    }

    public xsdAnyURIType getNamespaceURI() {
        return (xsdAnyURIType) getElementValue("namespaceURI", "xsdAnyURIType");
    }

    public boolean removeNamespaceURI() {
        return removeElement("namespaceURI");
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
