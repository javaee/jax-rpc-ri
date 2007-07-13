/*
 * $Id: Symbol.java,v 1.3 2007-07-13 23:36:19 ofung Exp $
 */

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
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
