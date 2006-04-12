/*
 * $Id: Validatable.java,v 1.1 2006-04-12 20:33:15 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.encoding;

/**
 *
 * @author JAX-RPC Development Team
 */
public interface Validatable {

    public void validate(Object instance);
}