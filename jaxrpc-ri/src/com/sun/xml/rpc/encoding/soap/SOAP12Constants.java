/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


package com.sun.xml.rpc.encoding.soap;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.soap.streaming.SOAP12NamespaceConstants;

/**
 *
 * @author JAX-RPC Development Team
 */
public class SOAP12Constants implements com.sun.xml.rpc.wsdl.document.soap.SOAP12Constants {

    public static final String URI_ENVELOPE = SOAP12NamespaceConstants.ENVELOPE;
    public static final String URI_ENCODING = NS_SOAP_ENCODING;
    public static final String URI_HTTP = SOAP12NamespaceConstants.TRANSPORT_HTTP;
    public static final String URI_SOAP_RPC = SOAP12NamespaceConstants.SOAP_RPC;
    
    public static final QName QNAME_ENCODING_ARRAY = QNAME_TYPE_ARRAY;
    public static final QName QNAME_ENCODING_ARRAYTYPE = QNAME_ATTR_ARRAY_TYPE;
    public static final QName QNAME_ENCODING_ARRAYSIZE = QNAME_ATTR_ARRAY_SIZE;
    public static final QName QNAME_ENCODING_ITEMTYPE = QNAME_ATTR_ITEM_TYPE;
    public static final QName QNAME_ENCODING_BASE64 = QNAME_TYPE_BASE64;
    
    public static final QName QNAME_SOAP_RPC = new QName(URI_SOAP_RPC, "rpc");
    public static final QName QNAME_SOAP_RESULT = new QName(URI_SOAP_RPC, "result");
    public static final QName QNAME_ENVELOPE_ENCODINGSTYLE = new QName(URI_ENVELOPE, "encodingStyle");
    public static final QName QNAME_SOAP_FAULT             = new QName(URI_ENVELOPE, "Fault");
    public static final QName QNAME_MUSTUNDERSTAND         = new QName(URI_ENVELOPE, "mustUnderstand");
    
    public final static QName FAULT_CODE_CLIENT            = new QName(URI_ENVELOPE, "Sender");
    public final static QName FAULT_CODE_MUST_UNDERSTAND   = new QName(URI_ENVELOPE, "MustUnderstand");
    public final static QName FAULT_CODE_MISUNDERSTOOD   = new QName(URI_ENVELOPE, "Misunderstood");
    public final static QName FAULT_CODE_SERVER            = new QName(URI_ENVELOPE, "Receiver");
    public final static QName FAULT_CODE_VERSION_MISMATCH  = new QName(URI_ENVELOPE, "VersionMismatch");
    public final static QName FAULT_CODE_DATA_ENCODING_UNKNOWN = new QName(URI_ENVELOPE, "DataEncodingUnknown");
    public final static QName FAULT_CODE_PROCEDURE_NOT_PRESENT = new QName(URI_ENVELOPE, "ProcedureNotPresent");
    public final static QName FAULT_CODE_BAD_ARGUMENTS      = new QName(URI_ENVELOPE, "BadArguments");
}
