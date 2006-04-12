/*
 * $Id: SOAPType.java,v 1.1 2006-04-12 20:34:44 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.model.soap;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.java.JavaType;
import com.sun.xml.rpc.soap.SOAPVersion;

/**
 *
 * @author JAX-RPC Development Team
 */
public abstract class SOAPType extends AbstractType {
    
    protected SOAPType() {
        setVersion(SOAPVersion.SOAP_11.toString());
    }
    
    protected SOAPType(QName name) {
        this(name, SOAPVersion.SOAP_11);
    }
    
    protected SOAPType(QName name, SOAPVersion version) {
        this(name, null, version);
    }
    
    protected SOAPType(QName name, JavaType javaType) {
        this(name, javaType, SOAPVersion.SOAP_11);
    }
    
    protected SOAPType(QName name, JavaType javaType, SOAPVersion version) {
        super(name, javaType, version != null ? version.toString() : null);
    }
    
    public boolean isNillable() {
        return true;
    }
    
    public boolean isReferenceable() {
        return true;
    }
    
    public boolean isSOAPType() {
        return true;
    }
    
    public abstract void accept(SOAPTypeVisitor visitor) throws Exception;
}
