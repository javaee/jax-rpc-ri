/*
 * $Id: WSDLParseException.java,v 1.1 2006-04-12 20:32:04 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.util;

import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.localization.Localizable;

/**
  * @author JAX-RPC Development Team
  */
public class WSDLParseException extends JAXRPCExceptionBase {

	public WSDLParseException(String key) {
		super(key);
	}

	public WSDLParseException(String key, String arg) {
		super(key, arg);
	}

	public WSDLParseException(String key, Localizable localizable) {
		super(key, localizable);
	}

	public WSDLParseException(String key, Object[] args) {
		super(key, args);
	}

	public String getResourceBundleName() {
		return "com.sun.xml.rpc.resources.util";
	}
}
