/*
 * $Id: SOAPProtocolViolationException.java,v 1.1 2006-04-12 20:35:33 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.soap.streaming;

import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.localization.Localizable;

/**
 * @author JAX-RPC Development Team
 */
public class SOAPProtocolViolationException extends JAXRPCExceptionBase {
	public SOAPProtocolViolationException(String key) {
		super(key);
	}

	public SOAPProtocolViolationException(String key, String argument) {
		super(key, argument);
	}

	public SOAPProtocolViolationException(String key, Object[] arguments) {
		super(key, arguments);
	}

	public SOAPProtocolViolationException(String key, Localizable argument) {
		super(key, argument);
	}

	public String getResourceBundleName() {
		return "com.sun.xml.rpc.resources.soap";
	}
}
