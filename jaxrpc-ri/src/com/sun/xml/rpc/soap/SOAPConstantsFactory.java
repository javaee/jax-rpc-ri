/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2018 Sun Microsystems, Inc. All rights reserved.
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
