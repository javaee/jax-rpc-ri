/*
 * $Id: SOAPHeaderBlockInfo.java,v 1.1 2006-04-12 20:35:02 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.soap.message;

import javax.xml.namespace.QName;

/**
 * @author JAX-RPC Development Team
 */
public class SOAPHeaderBlockInfo extends SOAPBlockInfo {

	public SOAPHeaderBlockInfo(
		QName name,
		String actor,
		boolean mustUnderstand) {
		super(name);
		_actor = actor;
		_mustUnderstand = mustUnderstand;
	}

	private String _actor;
	private boolean _mustUnderstand;
}
