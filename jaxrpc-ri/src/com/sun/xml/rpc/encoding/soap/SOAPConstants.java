/*
 * $Id: SOAPConstants.java,v 1.2 2006-04-13 01:28:11 ofung Exp $
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


package com.sun.xml.rpc.encoding.soap;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.soap.streaming.SOAPNamespaceConstants;

/**
 *
 * @author JAX-RPC Development Team
 */
public class SOAPConstants implements com.sun.xml.rpc.wsdl.document.soap.SOAPConstants,
    com.sun.xml.rpc.spi.runtime.SOAPConstants {

    public static final String URI_HTTP = SOAPNamespaceConstants.TRANSPORT_HTTP;

    public static final QName QNAME_ENCODING_ARRAY = QNAME_TYPE_ARRAY;
    public static final QName QNAME_ENCODING_ARRAYTYPE = QNAME_ATTR_ARRAY_TYPE;
    public static final QName QNAME_ENCODING_BASE64 = QNAME_TYPE_BASE64;
    public static final QName QNAME_ENVELOPE_ENCODINGSTYLE = new QName(URI_ENVELOPE, "encodingStyle");

    public final static QName QNAME_SOAP_FAULT             = new QName(URI_ENVELOPE, "Fault");
    public final static QName FAULT_CODE_CLIENT            = new QName(URI_ENVELOPE, "Client");
    public final static QName FAULT_CODE_MUST_UNDERSTAND   = new QName(URI_ENVELOPE, "MustUnderstand");

    public final static QName FAULT_CODE_VERSION_MISMATCH  = new QName(URI_ENVELOPE, "VersionMismatch");
    public final static QName FAULT_CODE_DATA_ENCODING_UNKNOWN = new QName(URI_ENVELOPE, "DataEncodingUnknown");
    public final static QName FAULT_CODE_PROCEDURE_NOT_PRESENT = new QName(URI_ENVELOPE, "ProcedureNotPresent");
    public final static QName FAULT_CODE_BAD_ARGUMENTS      = new QName(URI_ENVELOPE, "BadArguments");
}
