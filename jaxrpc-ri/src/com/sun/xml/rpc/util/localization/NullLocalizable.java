/*
 * $Id: NullLocalizable.java,v 1.1 2006-04-12 20:34:16 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.util.localization;

/**
 * NullLocalizable
 *
 * @author JAX-RPC Development Team
 */

public class NullLocalizable implements Localizable {
	protected static NullLocalizable instance = null;

	public NullLocalizable(String key) {
		_key = key;
	}

	public String getKey() {
		return _key;
	}
	public Object[] getArguments() {
		return null;
	}
	public String getResourceBundleName() {
		return "";
	}

	private String _key;

	public static NullLocalizable instance() {
		if (instance == null) {
			instance = new NullLocalizable(null);
		}
		return instance;
	}
}