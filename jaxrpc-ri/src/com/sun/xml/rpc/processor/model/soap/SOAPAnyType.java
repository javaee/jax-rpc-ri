/*
 * $Id: SOAPAnyType.java,v 1.1 2006-04-12 20:34:41 kohlert Exp $
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
public class SOAPAnyType extends SOAPType {
    
    public SOAPAnyType() {}
    
    public SOAPAnyType(QName name) {
        super(name);
    }
    
    public SOAPAnyType(QName name, JavaType javaType) {
        this(name, javaType, SOAPVersion.SOAP_11);
    }
    
    public SOAPAnyType(QName name, JavaType javaType, SOAPVersion version) {
        super(name, javaType, version);
    }
    
    public void accept(SOAPTypeVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}
