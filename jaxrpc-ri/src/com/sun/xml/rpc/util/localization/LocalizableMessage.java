/*
 * $Id: LocalizableMessage.java,v 1.1 2006-04-12 20:34:16 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.util.localization;

/**
 * @author JAX-RPC Development Team
 */
public class LocalizableMessage implements Localizable {

	protected String _bundlename;
	protected String _key;
	protected Object[] _args;

	public LocalizableMessage(String bundlename, String key) {
		this(bundlename, key, (Object[]) null);
	}

	public LocalizableMessage(String bundlename, String key, String arg) {
		this(bundlename, key, new Object[] { arg });
	}

	protected LocalizableMessage(
		String bundlename,
		String key,
		Object[] args) {
		_bundlename = bundlename;
		_key = key;
		_args = args;
	}

	public String getKey() {
		return _key;
	}

	public Object[] getArguments() {
		return _args;
	}

	public String getResourceBundleName() {
		return _bundlename;
	}
}
