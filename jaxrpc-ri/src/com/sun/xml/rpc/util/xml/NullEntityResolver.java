/*
 * $Id: NullEntityResolver.java,v 1.1 2006-04-12 20:35:28 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.util.xml;

import java.io.StringReader;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * @author JAX-RPC Development Team
 */
public class NullEntityResolver implements EntityResolver {

	public NullEntityResolver() {
	}

	public InputSource resolveEntity(String publicId, String systemId) {
		// always resolve to an empty document
		return new InputSource(new StringReader(""));
	}
}
