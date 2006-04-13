/*
 * $Id: Resources.java,v 1.2 2006-04-13 01:33:58 ofung Exp $
 */

/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License).  You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the license at
 * https://glassfish.dev.java.net/public/CDDLv1.0.html.
 * See the License for the specific language governing
 * permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at https://glassfish.dev.java.net/public/CDDLv1.0.html.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * you own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
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
