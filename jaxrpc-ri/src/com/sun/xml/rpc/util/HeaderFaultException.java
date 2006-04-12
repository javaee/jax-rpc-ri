/*
 * $Id: HeaderFaultException.java,v 1.1 2006-04-12 20:32:03 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/**
 * @author JAX-RPC Development Team
 */

package com.sun.xml.rpc.util;

public class HeaderFaultException extends Exception {
	private Object obj = null;

	public HeaderFaultException(String msg, Object obj) {
		super(msg);
		this.obj = obj;
	}

	public Object getObject() {
		return this.obj;
	}
}
