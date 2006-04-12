/*
 * $Id: Request.java,v 1.1 2006-04-12 20:33:05 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package com.sun.xml.rpc.processor.model;

/**
 *
 * @author JAX-RPC Development Team
 */
public class Request extends Message {
    
    public Request() {}
    
    public void accept(ModelVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}
