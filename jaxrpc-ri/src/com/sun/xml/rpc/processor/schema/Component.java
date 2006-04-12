/*
 * $Id: Component.java,v 1.1 2006-04-12 20:35:06 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.schema;

/**
 *
 * @author JAX-RPC Development Team
 */
public abstract class Component {
    public abstract void accept(ComponentVisitor visitor) throws Exception;
}
