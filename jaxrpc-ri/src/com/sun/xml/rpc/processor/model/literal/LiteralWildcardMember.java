/*
 * $Id: LiteralWildcardMember.java,v 1.1 2006-04-12 20:32:45 kohlert Exp $
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
public class LiteralWildcardMember extends LiteralElementMember {
    
    public LiteralWildcardMember() {}
    
    public LiteralWildcardMember(LiteralType type) {
        this(type, null);
    }
    
    public LiteralWildcardMember(LiteralType type,
        JavaStructureMember javaStructureMember) {
            
        super(null, type, javaStructureMember);
    }
    
    public String getExcludedNamespaceName() {
        return _excludedNamespaceName;
    }
    
    public void setExcludedNamespaceName(String s) {
        _excludedNamespaceName = s;
    }
    
    public boolean isWildcard() {
        return true;
    }
    
    private String _excludedNamespaceName;
}
