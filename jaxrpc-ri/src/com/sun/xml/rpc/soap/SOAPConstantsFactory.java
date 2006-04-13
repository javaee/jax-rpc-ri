/**
 * $Id: SOAPConstantsFactory.java,v 1.2 2006-04-13 01:32:16 ofung Exp $
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

/**
 * @author JAX-RPC Development Team
 */

public class SOAPConstantsFactory {
	private static final SOAPConstantsFactory factory =
		new SOAPConstantsFactory();

	private SOAPConstantsFactory() {
		namespaceConstants11 =
			new SOAPNamespaceConstantsImpl(SOAPVersion.SOAP_11);
		wsdlConstants11 = new SOAPWSDLConstantsImpl(SOAPVersion.SOAP_11);
		encodingConstants11 =
			new SOAPEncodingConstantsImpl(SOAPVersion.SOAP_11);

		namespaceConstants12 =
			new SOAPNamespaceConstantsImpl(SOAPVersion.SOAP_12);
		wsdlConstants12 = new SOAPWSDLConstantsImpl(SOAPVersion.SOAP_12);
		encodingConstants12 =
			new SOAPEncodingConstantsImpl(SOAPVersion.SOAP_12);
	}

	public static SOAPNamespaceConstants getSOAPNamespaceConstants(SOAPVersion ver) {
		if (ver == SOAPVersion.SOAP_11)
			return namespaceConstants11;
		else if (ver == SOAPVersion.SOAP_12)
			return namespaceConstants12;
		return null;
	}

	public static SOAPWSDLConstants getSOAPWSDLConstants(SOAPVersion ver) {
		if (ver == SOAPVersion.SOAP_11)
			return wsdlConstants11;
		else if (ver == SOAPVersion.SOAP_12)
			return wsdlConstants12;
		return null;
	}

	public static SOAPEncodingConstants getSOAPEncodingConstants(SOAPVersion ver) {
		if (ver == SOAPVersion.SOAP_11)
			return encodingConstants11;
		else if (ver == SOAPVersion.SOAP_12)
			return encodingConstants12;
		return null;
	}

	private static SOAPNamespaceConstants namespaceConstants11;
	private static SOAPWSDLConstants wsdlConstants11;
	private static SOAPEncodingConstants encodingConstants11;

	private static SOAPNamespaceConstants namespaceConstants12;
	private static SOAPWSDLConstants wsdlConstants12;
	private static SOAPEncodingConstants encodingConstants12;
}
