/*
 * $Id: Debug.java,v 1.1 2006-04-12 20:32:06 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.util;

/**
 * @author JAX-RPC Development Team
 */

public final class Debug {

	protected static final boolean _enabled = true;

	public static void println(String s) {
		if (_enabled)
			System.out.println(s);
	}

	public static boolean enabled() {
		return _enabled;
	}
}
