/**
 * $Id: SOAPConstantsFactory.java,v 1.1 2006-04-12 20:34:38 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
