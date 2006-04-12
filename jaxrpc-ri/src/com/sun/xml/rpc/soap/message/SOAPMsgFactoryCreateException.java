/*
 * $Id: SOAPMsgFactoryCreateException.java,v 1.1 2006-04-12 20:35:02 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.soap.message;

import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;

/**
 * @author JAX-RPC Development Team
 */
public class SOAPMsgFactoryCreateException extends JAXRPCExceptionBase {

	public SOAPMsgFactoryCreateException(String key, Object[] args) {
		super(key, args);
	}

	public String getResourceBundleName() {
		return "com.sun.xml.rpc.resources.soap";
	}
}
