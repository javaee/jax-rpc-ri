/*
 * $Id: SOAPEnumerationType.java,v 1.1 2006-04-12 20:34:42 kohlert Exp $
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
public class SOAPEnumerationType extends SOAPType {
    
    public SOAPEnumerationType() {}
    
    public SOAPEnumerationType(QName name, SOAPType baseType,
        JavaType javaType) {
            
        this(name, baseType, javaType, SOAPVersion.SOAP_11);
    }
    
    public SOAPEnumerationType(QName name, SOAPType baseType, JavaType javaType,
        SOAPVersion version) {
            
        super(name, javaType, version);
        this.baseType = baseType;
    }
    
    public SOAPType getBaseType() {
        return baseType;
    }
    
    public void setBaseType(SOAPType t) {
        baseType = t;
    }
    
    public void accept(SOAPTypeVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    
    private SOAPType baseType;
}
