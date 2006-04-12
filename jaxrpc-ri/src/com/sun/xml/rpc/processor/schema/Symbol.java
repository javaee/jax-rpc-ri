/*
 * $Id: Symbol.java,v 1.1 2006-04-12 20:35:04 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.schema;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author JAX-RPC Development Team
 */
public final class Symbol {
    
    public static final Symbol DEFAULT;
    public static final Symbol FIXED;
    
    public static final Symbol EXTENSION;
    public static final Symbol RESTRICTION;
    public static final Symbol SUBSTITUTION;
    
    public static final Symbol SKIP;
    public static final Symbol LAX;
    public static final Symbol STRICT;
    
    public static final Symbol KEY;
    public static final Symbol KEYREF;
    public static final Symbol UNIQUE;
    
    public static final Symbol ALL;
    public static final Symbol CHOICE;
    public static final Symbol SEQUENCE;
    
    public static final Symbol ATOMIC;
    public static final Symbol LIST;
    public static final Symbol UNION;
    
    public static Symbol named(String s) {
        return (Symbol) _symbolMap.get(s);
    }
    
    private static Map _symbolMap;
    
    static {
        _symbolMap = new HashMap();
        
        DEFAULT = new Symbol("default");
        FIXED = new Symbol("fixed");
        EXTENSION = new Symbol("extension");
        RESTRICTION = new Symbol("restriction");
        SUBSTITUTION = new Symbol("substitution");
        
        SKIP = new Symbol("skip");
        LAX = new Symbol("lax");
        STRICT = new Symbol("strict");
        
        KEY = new Symbol("key");
        KEYREF = new Symbol("keyref");
        UNIQUE = new Symbol("unique");
        
        ALL = new Symbol("all");
        CHOICE = new Symbol("choice");
        SEQUENCE = new Symbol("sequence");
        
        ATOMIC = new Symbol("atomic");
        LIST = new Symbol("list");
        UNION = new Symbol("union");
    }
    
    
    private Symbol(String s) {
        _name = s;
        _symbolMap.put(s, this);
    }
    
    public String getName() {
        return _name;
    }
    
    private String _name;
}
