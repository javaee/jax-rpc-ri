/*
 * $Id: Version.java,v 1.1 2006-04-12 20:32:06 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.util;

/**
 * This interface holds version information for the whole JAX-RPC RI.
 *
 * @author JAX-RPC Development Team
 */

public interface Version {

	/**
	 * JAX-RPC RI product name
	 */
	public static final String PRODUCT_NAME = "JAX-RPC Standard Implementation";

	/**
	 * JAX-RPC RI version number
	 */
	public static final String VERSION_NUMBER = "1.1.3";

	/**
	 * JAX-RPC RI build number
	 */
	public static final String BUILD_NUMBER = "R2";
}
