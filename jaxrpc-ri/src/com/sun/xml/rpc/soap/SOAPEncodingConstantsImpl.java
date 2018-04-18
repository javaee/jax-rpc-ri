/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

/*
 * $Id: SOAPEncodingConstantsImpl.java,v 1.3 2007-07-13 23:36:25 ofung Exp $
 */

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.sun.xml.rpc.soap;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.encoding.soap.SOAP12Constants;
import com.sun.xml.rpc.encoding.soap.SOAPConstants;

/**
 * @author JAX-RPC Development Team
 */

class SOAPEncodingConstantsImpl
	extends SOAPWSDLConstantsImpl
	implements SOAPEncodingConstants {

	SOAPEncodingConstantsImpl(SOAPVersion ver) {
		super(ver);
		this.ver = ver;
		if (ver == SOAPVersion.SOAP_11)
			initSOAP11();
		else if (ver == SOAPVersion.SOAP_12)
			initSOAP12();
	}

	public String getURIEnvelope() {
		return URI_ENVELOPE;
	}

	public String getURIEncoding() {
		return URI_ENCODING;
	}

	public String getURIHttp() {
		return URI_HTTP;
	}

	public QName getQNameEncodingArray() {
		return QNAME_ENCODING_ARRAY;
	}

	public QName getQNameEncodingArraytype() {
		return QNAME_ENCODING_ARRAYTYPE;
	}

	public QName getQNameEncodingItemtype() {
		return QNAME_ENCODING_ITEMTYPE;
	}

	public QName getQNameEncodingArraysize() {
		return QNAME_ENCODING_ARRAYSIZE;
	}

	public QName getQNameEncodingBase64() {
		return QNAME_ENCODING_BASE64;
	}

	public QName getQNameEnvelopeEncodingStyle() {
		return QNAME_ENVELOPE_ENCODINGSTYLE;
	}

	public QName getQNameSOAPFault() {
		return QNAME_SOAP_FAULT;
	}

	public QName getFaultCodeClient() {
		return FAULT_CODE_CLIENT;
	}

	public QName getFaultCodeMustUnderstand() {
		return FAULT_CODE_MUST_UNDERSTAND;
	}

	public QName getFaultCodeServer() {
		return FAULT_CODE_SERVER;
	}

	public QName getFaultCodeVersionMismatch() {
		return FAULT_CODE_VERSION_MISMATCH;
	}

	public QName getFaultCodeDataEncodingUnknown() {
		return FAULT_CODE_DATA_ENCODING_UNKNOWN;
	}

	public QName getFaultCodeProcedureNotPresent() {
		return FAULT_CODE_PROCEDURE_NOT_PRESENT;
	}

	public QName getFaultCodeBadArguments() {
		return FAULT_CODE_BAD_ARGUMENTS;
	}

	// SOAP 1.2
	public QName getQNameSOAPRpc() {
		return QNAME_SOAP_RPC;
	}

	public QName getQNameSOAPResult() {
		return QNAME_SOAP_RESULT;
	}

	public QName getFaultCodeMisunderstood() {
		return FAULT_CODE_MISUNDERSTOOD;
	}

	/** SOAP Version */
	public SOAPVersion getSOAPVersion() {
		return this.ver;
	}

	private void initSOAP11() {
		URI_ENVELOPE = SOAPConstants.URI_ENVELOPE;
		URI_ENCODING = SOAPConstants.URI_ENCODING;
		URI_HTTP = SOAPConstants.URI_HTTP;

		QNAME_ENCODING_ARRAY = SOAPConstants.QNAME_ENCODING_ARRAY;
		QNAME_ENCODING_ARRAYTYPE = SOAPConstants.QNAME_ENCODING_ARRAYTYPE;
		QNAME_ENCODING_BASE64 = SOAPConstants.QNAME_ENCODING_BASE64;
		QNAME_ENVELOPE_ENCODINGSTYLE =
			SOAPConstants.QNAME_ENVELOPE_ENCODINGSTYLE;

		QNAME_SOAP_FAULT = SOAPConstants.QNAME_SOAP_FAULT;
		FAULT_CODE_CLIENT = SOAPConstants.FAULT_CODE_CLIENT;
		FAULT_CODE_MUST_UNDERSTAND = SOAPConstants.FAULT_CODE_MUST_UNDERSTAND;
		FAULT_CODE_SERVER = SOAPConstants.FAULT_CODE_SERVER;
		FAULT_CODE_VERSION_MISMATCH = SOAPConstants.FAULT_CODE_VERSION_MISMATCH;
		FAULT_CODE_DATA_ENCODING_UNKNOWN =
			SOAPConstants.FAULT_CODE_DATA_ENCODING_UNKNOWN;
		FAULT_CODE_PROCEDURE_NOT_PRESENT =
			SOAPConstants.FAULT_CODE_PROCEDURE_NOT_PRESENT;
		FAULT_CODE_BAD_ARGUMENTS = SOAPConstants.FAULT_CODE_BAD_ARGUMENTS;

		QNAME_SOAP_RPC = null;
		QNAME_SOAP_RESULT = null;
		FAULT_CODE_MISUNDERSTOOD = null;
	}

	private void initSOAP12() {
		URI_ENVELOPE = SOAP12Constants.URI_ENVELOPE;
		URI_ENCODING = SOAP12Constants.URI_ENCODING;
		URI_HTTP = SOAP12Constants.URI_HTTP;

		QNAME_ENCODING_ARRAY = SOAP12Constants.QNAME_ENCODING_ARRAY;
		QNAME_ENCODING_ARRAYTYPE = SOAP12Constants.QNAME_ENCODING_ARRAYTYPE;
		QNAME_ENCODING_ITEMTYPE = SOAP12Constants.QNAME_ENCODING_ITEMTYPE;
		QNAME_ENCODING_ARRAYSIZE = SOAP12Constants.QNAME_ENCODING_ARRAYSIZE;
		QNAME_ENCODING_BASE64 = SOAP12Constants.QNAME_ENCODING_BASE64;
		QNAME_ENVELOPE_ENCODINGSTYLE =
			SOAP12Constants.QNAME_ENVELOPE_ENCODINGSTYLE;

		QNAME_SOAP_FAULT = SOAP12Constants.QNAME_SOAP_FAULT;
		FAULT_CODE_CLIENT = SOAP12Constants.FAULT_CODE_CLIENT;
		FAULT_CODE_MUST_UNDERSTAND = SOAP12Constants.FAULT_CODE_MUST_UNDERSTAND;
		FAULT_CODE_SERVER = SOAP12Constants.FAULT_CODE_SERVER;
		FAULT_CODE_VERSION_MISMATCH =
			SOAP12Constants.FAULT_CODE_VERSION_MISMATCH;
		FAULT_CODE_DATA_ENCODING_UNKNOWN =
			SOAP12Constants.FAULT_CODE_DATA_ENCODING_UNKNOWN;
		FAULT_CODE_PROCEDURE_NOT_PRESENT =
			SOAP12Constants.FAULT_CODE_PROCEDURE_NOT_PRESENT;
		FAULT_CODE_BAD_ARGUMENTS = SOAP12Constants.FAULT_CODE_BAD_ARGUMENTS;

		// SOAP 1.2
		QNAME_SOAP_RPC = SOAP12Constants.QNAME_SOAP_RPC;
		QNAME_SOAP_RESULT = SOAP12Constants.QNAME_SOAP_RESULT;
		FAULT_CODE_MISUNDERSTOOD = SOAP12Constants.FAULT_CODE_MISUNDERSTOOD;
	}

	private SOAPVersion ver;
	private String URI_ENVELOPE;
	private String URI_ENCODING;
	private String URI_HTTP;

	private QName QNAME_ENCODING_ARRAY;
	private QName QNAME_ENCODING_ARRAYTYPE;
	private QName QNAME_ENCODING_ITEMTYPE;
	private QName QNAME_ENCODING_ARRAYSIZE;
	private QName QNAME_ENCODING_BASE64;
	private QName QNAME_ENVELOPE_ENCODINGSTYLE;

	private QName QNAME_SOAP_FAULT;
	private QName FAULT_CODE_CLIENT;
	private QName FAULT_CODE_MUST_UNDERSTAND;
	private QName FAULT_CODE_SERVER;
	private QName FAULT_CODE_VERSION_MISMATCH;
	private QName FAULT_CODE_DATA_ENCODING_UNKNOWN;
	private QName FAULT_CODE_PROCEDURE_NOT_PRESENT;
	private QName FAULT_CODE_BAD_ARGUMENTS;

	// SOAP 1.2
	private QName QNAME_SOAP_RPC;
	private QName QNAME_SOAP_RESULT;
	private QName FAULT_CODE_MISUNDERSTOOD;
}
