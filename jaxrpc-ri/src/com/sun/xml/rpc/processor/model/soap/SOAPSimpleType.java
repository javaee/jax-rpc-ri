/*
 * $Id: SOAPSimpleType.java,v 1.1 2006-04-12 20:34:41 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.model.soap;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.model.java.JavaSimpleType;
import com.sun.xml.rpc.soap.SOAPVersion;

/**
 *
 * @author JAX-RPC Development Team
 */
public class SOAPSimpleType extends SOAPType {
    
    public SOAPSimpleType() {}
    
    public SOAPSimpleType(QName name) {
        this(name, null);
    }
    
    public SOAPSimpleType(QName name, JavaSimpleType javaType) {
        this(name, javaType, true);
    }
    
    public SOAPSimpleType(QName name, JavaSimpleType javaType,
        SOAPVersion version) {
        
        this(name, javaType, true, version);
    }
    
    public SOAPSimpleType(QName name, JavaSimpleType javaType,
        boolean referenceable) {
        
        this(name, javaType, referenceable, SOAPVersion.SOAP_11);
    }
    
    public SOAPSimpleType(QName name, JavaSimpleType javaType,
        boolean referenceable, SOAPVersion version) {
        
        super(name, javaType, version);
        this.referenceable = referenceable;
    }
    
    public QName getSchemaTypeRef() {
        return schemaTypeRef;
    }
    
    public void setSchemaTypeRef(QName n) {
        schemaTypeRef = n;
    }
    
    public boolean isReferenceable() {
        return referenceable;
    }
    
    public void setReferenceable(boolean b) {
        referenceable = b;
    }
    
    public void accept(SOAPTypeVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    
    private QName schemaTypeRef;
    private boolean referenceable;
}
