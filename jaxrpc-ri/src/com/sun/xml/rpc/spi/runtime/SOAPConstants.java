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
