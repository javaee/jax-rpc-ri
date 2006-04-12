/*
 * $Id: Namespace.java,v 1.1 2006-04-12 20:35:27 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
