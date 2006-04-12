/*
 * $Id: SOAPEncodingConstants.java,v 1.1 2006-04-12 20:34:39 kohlert Exp $
 */
/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.soap;

import javax.xml.namespace.QName;

/**
 * @author JAX-RPC Development Team
 */

public interface SOAPEncodingConstants extends SOAPWSDLConstants {

	public String getURIEnvelope();
	public String getURIEncoding();
	public String getURIHttp();

	public QName getQNameEncodingArray();
	public QName getQNameEncodingArraytype();
	public QName getQNameEncodingItemtype();
	public QName getQNameEncodingArraysize();
	public QName getQNameEncodingBase64();
	public QName getQNameEnvelopeEncodingStyle();

	public QName getQNameSOAPFault();
	public QName getFaultCodeClient();
	public QName getFaultCodeMustUnderstand();
	public QName getFaultCodeServer();
	public QName getFaultCodeVersionMismatch();
	public QName getFaultCodeDataEncodingUnknown();
	public QName getFaultCodeProcedureNotPresent();
	public QName getFaultCodeBadArguments();

	// SOAP 1.2
	public QName getQNameSOAPRpc();
	public QName getQNameSOAPResult();
	public QName getFaultCodeMisunderstood();

	/** SOAP Version */
	public SOAPVersion getSOAPVersion();
}
