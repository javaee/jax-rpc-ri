/*
 * $Id: LocalizableMessage.java,v 1.2 2006-04-13 01:33:56 ofung Exp $
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
