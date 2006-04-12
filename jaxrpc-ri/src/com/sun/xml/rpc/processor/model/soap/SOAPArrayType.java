/*
 * $Id: SOAPArrayType.java,v 1.1 2006-04-12 20:34:42 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.model.soap;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.model.java.JavaType;
import com.sun.xml.rpc.soap.SOAPVersion;

/**
 *
 * @author JAX-RPC Development Team
 */
public class SOAPArrayType extends SOAPType {
    
    public SOAPArrayType() {}
    
    public SOAPArrayType(QName name) {
        this(name, SOAPVersion.SOAP_11);
    }
    
    public SOAPArrayType(QName name, SOAPVersion version) {
        this(name, null, null, null, version);
    }
    
    public SOAPArrayType(QName name, QName elementName, SOAPType elementType,
        JavaType javaType) {
            
        this(name, elementName, elementType, javaType, SOAPVersion.SOAP_11);
    }
    
    public SOAPArrayType(QName name, QName elementName, SOAPType elementType,
        JavaType javaType, SOAPVersion version) {
            
        super(name, javaType, version);
        this.elementName = elementName;
        this.elementType = elementType;
    }
    
    public QName getElementName() {
        return elementName;
    }
    
    public void setElementName(QName name) {
        elementName = name;
    }
    
    public SOAPType getElementType() {
        return elementType;
    }
    
    public void setElementType(SOAPType type) {
        elementType = type;
    }
    
    public int getRank() {
        return rank;
    }
    
    public void setRank(int i) {
        rank = i;
    }
    
    public int[] getSize() {
        return size;
    }
    
    public void setSize(int[] a) {
        size = a;
    }
    
    public void accept(SOAPTypeVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    
    private QName elementName;
    private SOAPType elementType;
    private int rank;
    private int[] size;
}
