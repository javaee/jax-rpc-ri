/*
 * $Id: SOAPAttributeMember.java,v 1.1 2006-04-12 20:34:43 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.xml.rpc.processor.model.soap;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.model.java.JavaStructureMember;

/**
 * @author JAX-RPC Development Team
 *
 */
public class SOAPAttributeMember {

    public SOAPAttributeMember() {}
    
    public SOAPAttributeMember(QName name, SOAPType type) {
        this(name, type, null);
    }
    
    public SOAPAttributeMember(QName name, SOAPType type,
        JavaStructureMember javaStructureMember) {
            
        _name = name;
        _type = type;
        _javaStructureMember = javaStructureMember;
    }
    
    public QName getName() {
        return _name;
    }
    
    public void setName(QName n) {
        _name = n;
    }
    
    public SOAPType getType() {
        return _type;
    }
    
    public void setType(SOAPType t) {
        _type = t;
    }
    
    public JavaStructureMember getJavaStructureMember() {
        return _javaStructureMember;
    }
    
    public void setJavaStructureMember(
        JavaStructureMember javaStructureMember) {
            
        _javaStructureMember = javaStructureMember;
    }
    
    public boolean isRequired() {
        return _required;
    }
    
    public void setRequired(boolean b) {
        _required = b;
    }
    
    public boolean isInherited() {
        return isInherited;
    }
    
    public void setInherited(boolean b) {
        isInherited = b;
    }
    
    private QName _name;
    private SOAPType _type;
    private JavaStructureMember _javaStructureMember;
    private boolean _required;
    private boolean isInherited = false;
}

