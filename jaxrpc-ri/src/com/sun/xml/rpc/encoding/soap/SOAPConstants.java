/*
 * $Id: SOAPConstants.java,v 1.1 2006-04-12 20:34:08 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
