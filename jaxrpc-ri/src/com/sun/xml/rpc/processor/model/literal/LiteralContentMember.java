/*
 * $Id: LiteralContentMember.java,v 1.1 2006-04-12 20:32:44 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.model.literal;

import com.sun.xml.rpc.processor.model.java.JavaStructureMember;

/**
 *
 * @author JAX-RPC Development Team
 */
public class LiteralContentMember {
    
    public LiteralContentMember() {}
    
    public LiteralContentMember(LiteralType type) {
        this(type, null);
    }
    
    public LiteralContentMember( LiteralType type,
        JavaStructureMember javaStructureMember) {
            
        _type = type;
        _javaStructureMember = javaStructureMember;
    }
    
    public LiteralType getType() {
        return _type;
    }
    
    public void setType(LiteralType t) {
        _type = t;
    }
    
    public JavaStructureMember getJavaStructureMember() {
        return _javaStructureMember;
    }
    
    public void setJavaStructureMember(
        JavaStructureMember javaStructureMember) {
            
        _javaStructureMember = javaStructureMember;
    }
    
    private LiteralType _type;
    private JavaStructureMember _javaStructureMember;
}
