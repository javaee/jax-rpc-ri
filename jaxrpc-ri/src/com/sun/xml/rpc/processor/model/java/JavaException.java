/*
 * $Id: JavaException.java,v 1.1 2006-04-12 20:34:13 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package com.sun.xml.rpc.processor.model.java;

/**
 *
 * @author JAX-RPC Development Team
 */
public class JavaException extends JavaStructureType {
    
    public JavaException() {}
    
    public JavaException(String name, boolean present, Object owner) {
        super(name, present, owner);
    }
}
