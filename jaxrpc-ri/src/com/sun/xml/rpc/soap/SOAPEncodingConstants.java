/*
 * $Id: SOAPEncodingConstants.java,v 1.2 2006-04-13 01:32:17 ofung Exp $
 */
/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License).  You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the license at
 * https://glassfish.dev.java.net/public/CDDLv1.0.html.
 * See the License for the specific language governing
 * permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at https://glassfish.dev.java.net/public/CDDLv1.0.html.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * you own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
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
