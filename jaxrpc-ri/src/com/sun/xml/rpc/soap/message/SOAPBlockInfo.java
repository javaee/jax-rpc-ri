/*
 * $Id: SOAPBlockInfo.java,v 1.1 2006-04-12 20:35:02 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.soap.message;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.encoding.JAXRPCSerializer;

/**
 * @author JAX-RPC Development Team
 */
public class SOAPBlockInfo {

	public SOAPBlockInfo(QName name) {
		_name = name;
	}

	public QName getName() {
		return _name;
	}

	public Object getValue() {
		return _value;
	}

	public void setValue(Object value) {
		_value = value;
	}

	public JAXRPCSerializer getSerializer() {
		return _serializer;
	}

	public void setSerializer(JAXRPCSerializer s) {
		_serializer = s;
	}

	private QName _name;
	private Object _value;
	private JAXRPCSerializer _serializer;
}
