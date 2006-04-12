/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.xml.rpc.spi.runtime;

import javax.xml.namespace.QName;

/**
 * This class is implemented by 
 * com.sun.xml.rpc.encoding.soap.SOAPConstants
 */
public interface SOAPConstants
	extends com.sun.xml.rpc.spi.tools.SOAPConstants {

	public static final String URI_ENCODING = NS_SOAP_ENCODING;
	public final static QName FAULT_CODE_SERVER =
		new QName(URI_ENVELOPE, "Server");

	// FAULT_CODE_CLIENT  This is needed
	// during error processing on an HTTP POST.  
	static final QName FAULT_CODE_CLIENT =
		new QName(SOAPConstants.URI_ENVELOPE, "Client");

	//
	// Internal MessageContextProperties that should be exposed :
	//

	// Used to set http servlet response object so that TieBase
	// will correctly flush response code for one-way operations.
	static final String HTTP_SERVLET_RESPONSE =
		"com.sun.xml.rpc.server.http.HttpServletResponse";

	// Used to detect ONE_WAY_OPERATION so reply can be skipped
	// in HTTP POST post-invocation processing
	static final String ONE_WAY_OPERATION =
		"com.sun.xml.rpc.server.OneWayOperation";

	// Another internal property used in reply processing.
	static final String CLIENT_BAD_REQUEST =
		"com.sun.xml.rpc.server.http.ClientBadRequest";
}
