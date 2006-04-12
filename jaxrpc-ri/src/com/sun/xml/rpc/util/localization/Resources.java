/*
 * $Id: Resources.java,v 1.1 2006-04-12 20:34:16 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.util.localization;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author JAX-RPC Development Team
 */
public class Resources {

	private ResourceBundle _bundle;

	public Resources(String bundleName) throws MissingResourceException {
		_bundle = ResourceBundle.getBundle(bundleName);
	}

	public String getString(String key) {
		return getText(key, null);
	}

	public String getString(String key, String arg) {
		return getText(key, new String[] { arg });
	}

	public String getString(String key, String[] args) {
		return getText(key, args);
	}

	private String getText(String key, String[] args) {
		if (_bundle == null)
			return "";

		try {
			return MessageFormat.format(_bundle.getString(key), args);
		} catch (MissingResourceException e) {
			String msg = "Missing resource: key={0}";
			return MessageFormat.format(msg, new String[] { key });
		}
	}
}
