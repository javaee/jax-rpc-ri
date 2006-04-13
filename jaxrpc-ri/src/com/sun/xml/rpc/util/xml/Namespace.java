/*
 * $Id: Namespace.java,v 1.2 2006-04-13 01:34:00 ofung Exp $
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

package com.sun.xml.rpc.util.xml;

/**
 * @author JAX-RPC Development Team
 */
public final class Namespace {

	private Namespace(String prefix, String uri) {
		_prefix = prefix;
		_uri = uri;
	}

	public String getPrefix() {
		return _prefix;
	}

	public String getURI() {
		return _uri;
	}

	public boolean equals(Object obj) {
		if (obj == null)
			return false;

		if (!(obj instanceof Namespace))
			return false;

		Namespace namespace = (Namespace) obj;

		return this._prefix.equals(namespace._prefix)
			&& this._uri.equals(namespace._uri);
	}

	public int hashCode() {
		return _prefix.hashCode() ^ _uri.hashCode();
	}

	private String _prefix;
	private String _uri;

	////////

	public static Namespace getNamespace(String prefix, String uri) {
		// TODO - modify this to cache namespace objects, possibly with weak references to collect them

		if (prefix == null) {
			prefix = "";
		}
		if (uri == null) {
			uri = "";
		}

		return new Namespace(prefix, uri);
	}
}
