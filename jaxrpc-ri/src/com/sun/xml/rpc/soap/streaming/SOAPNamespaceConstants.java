/*
 * $Id: SOAPNamespaceConstants.java,v 1.1 2006-04-12 20:35:33 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.soap.streaming;

/**
 * @author JAX-RPC Development Team
 */
public class SOAPNamespaceConstants
	implements com.sun.xml.rpc.spi.tools.SOAPNamespaceConstants {
	//    public static final String ENVELOPE = "http://schemas.xmlsoap.org/soap/envelope/";
	public static final String ENCODING =
		"http://schemas.xmlsoap.org/soap/encoding/";
	public static final String XSD = "http://www.w3.org/2001/XMLSchema";
	public static final String XSI =
		"http://www.w3.org/2001/XMLSchema-instance";
	public static final String XMLNS = "http://www.w3.org/XML/1998/namespace";
	public static final String TRANSPORT_HTTP =
		"http://schemas.xmlsoap.org/soap/http/";
	public static final String ACTOR_NEXT =
		"http://schemas.xmlsoap.org/soap/actor/next";

	public static final String TAG_ENVELOPE = "Envelope";
	public static final String TAG_HEADER = "Header";
	public static final String TAG_BODY = "Body";

	public static final String ATTR_ACTOR = "actor";
	public static final String ATTR_MUST_UNDERSTAND = "mustUnderstand";
	public static final String ATTR_ENCODING_STYLE = "encodingStyle";
}
