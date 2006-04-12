/*
 * $Id: Localizable.java,v 1.1 2006-04-12 20:34:16 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.util.localization;

/**
 * @author JAX-RPC Development Team
 */
public interface Localizable {

	public String getKey();
	public Object[] getArguments();
	public String getResourceBundleName();
}
