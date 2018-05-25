/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.xml.rpc.soap;

import com.sun.xml.rpc.soap.streaming.SOAP12NamespaceConstants;
import com.sun.xml.rpc.soap.streaming.SOAPNamespaceConstants;

class SOAPNamespaceConstantsImpl
	implements com.sun.xml.rpc.soap.SOAPNamespaceConstants {

	SOAPNamespaceConstantsImpl(SOAPVersion ver) {
		this.ver = ver;
		if (ver == SOAPVersion.SOAP_11)
			initSOAP11();
		else if (ver == SOAPVersion.SOAP_12)
			initSOAP12();
	}

	public SOAPVersion getSOAPVersion() {
		return this.ver;
	}

	public String getEnvelope() {
		return ENVELOPE;
	}

	public String getEncoding() {
		return ENCODING;
	}

	// SOAP 1.2
	public String getSOAPRpc() {
		return SOAP_RPC;
	}

	public String getXSD() {
		return XSD;
	}

	public String getXSI() {
		return XSI;
	}

	public String getTransportHTTP() {
		return TRANSPORT_HTTP;
	}

	public String getActorNext() {
		return ACTOR_NEXT;
	}

	public String getTagEnvelope() {
		return TAG_ENVELOPE;
	}

	public String getTagHeader() {
		return TAG_HEADER;
	}

	public String getTagBody() {
		return TAG_BODY;
	}

	public String getAttrActor() {
		return ATTR_ACTOR;
	}

	public String getTagResult() {
		return TAG_RESULT;
	}

	public String getAttrMustUnderstand() {
		return ATTR_MUST_UNDERSTAND;
	}

	public String getAttrMisunderstood() {
		return ATTR_MISUNDERSTOOD;
	}

	public String getSOAPUpgrade() {
		return SOAP_UPGRADE;
	}

	public String getAttrEncodingStyle() {
		return ATTR_ENCODING_STYLE;
	}

	private void initSOAP11() {
		ENVELOPE = SOAPNamespaceConstants.ENVELOPE;
		ENCODING = SOAPNamespaceConstants.ENCODING;
		XSD = SOAPNamespaceConstants.XSD;
		XSI = SOAPNamespaceConstants.XSI;
		TRANSPORT_HTTP = SOAPNamespaceConstants.TRANSPORT_HTTP;
		ACTOR_NEXT = SOAPNamespaceConstants.ACTOR_NEXT;
		TAG_ENVELOPE = SOAPNamespaceConstants.TAG_ENVELOPE;
		TAG_HEADER = SOAPNamespaceConstants.TAG_HEADER;
		TAG_BODY = SOAPNamespaceConstants.TAG_BODY;
		ATTR_ACTOR = SOAPNamespaceConstants.ATTR_ACTOR;
		ATTR_MUST_UNDERSTAND = SOAPNamespaceConstants.ATTR_MUST_UNDERSTAND;
		ATTR_ENCODING_STYLE = SOAPNamespaceConstants.ATTR_ENCODING_STYLE;

		// SOAP 1.2
		SOAP_RPC = null;
		TAG_RESULT = null;
		ATTR_MISUNDERSTOOD = null;
		SOAP_UPGRADE = null;
	}

	private void initSOAP12() {
		ENVELOPE = SOAP12NamespaceConstants.ENVELOPE;
		ENCODING = SOAP12NamespaceConstants.ENCODING;
		XSD = SOAP12NamespaceConstants.XSD;
		XSI = SOAP12NamespaceConstants.XSI;
		TRANSPORT_HTTP = SOAP12NamespaceConstants.TRANSPORT_HTTP;
		ACTOR_NEXT = SOAP12NamespaceConstants.ACTOR_NEXT;
		TAG_ENVELOPE = SOAP12NamespaceConstants.TAG_ENVELOPE;
		TAG_HEADER = SOAP12NamespaceConstants.TAG_HEADER;
		TAG_BODY = SOAP12NamespaceConstants.TAG_BODY;
		ATTR_ACTOR = SOAP12NamespaceConstants.ATTR_ACTOR;
		ATTR_MUST_UNDERSTAND = SOAP12NamespaceConstants.ATTR_MUST_UNDERSTAND;
		ATTR_ENCODING_STYLE = SOAP12NamespaceConstants.ATTR_ENCODING_STYLE;

		// SOAP 1.2
		SOAP_RPC = SOAP12NamespaceConstants.SOAP_RPC;
		TAG_RESULT = SOAP12NamespaceConstants.TAG_RESULT;
		ATTR_MISUNDERSTOOD = SOAP12NamespaceConstants.ATTR_MISUNDERSTOOD;
		SOAP_UPGRADE = SOAP12NamespaceConstants.SOAP_UPGRADE;
	}

	private String ENVELOPE;
	private String ENCODING;

	// SOAP 1.2
	private String SOAP_RPC;

	private String XSD;
	private String XSI;

	private String TRANSPORT_HTTP;

	private String ACTOR_NEXT;

	private String TAG_ENVELOPE;
	private String TAG_HEADER;
	private String TAG_BODY;
	private String ATTR_ACTOR;

	// SOAP 1.2
	private String TAG_RESULT;
	private String ATTR_MISUNDERSTOOD;

	private String ATTR_MUST_UNDERSTAND;
	private String ATTR_ENCODING_STYLE;
	private String SOAP_UPGRADE;

	private SOAPVersion ver;
}
