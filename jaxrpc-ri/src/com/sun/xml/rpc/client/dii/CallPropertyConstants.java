/*
 * $Id: CallPropertyConstants.java,v 1.1 2006-04-12 20:33:56 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.client.dii;

import javax.xml.rpc.Call;

import com.sun.xml.rpc.client.StubPropertyConstants;

/**
 * @author JAX-RPC Development Team
 */
public interface CallPropertyConstants {
    public static final String USERNAME_PROPERTY = Call.USERNAME_PROPERTY;
    public static final String PASSWORD_PROPERTY = Call.PASSWORD_PROPERTY;
    public static final String ENDPOINT_ADDRESS_PROPERTY =
        "javax.xml.rpc.endpoint";
    public static final String OPERATION_STYLE_PROPERTY =
        Call.OPERATION_STYLE_PROPERTY;
    public static final String SOAPACTION_USE_PROPERTY =
        Call.SOAPACTION_USE_PROPERTY;
    public static final String SOAPACTION_URI_PROPERTY =
        Call.SOAPACTION_URI_PROPERTY;
    public static final String SESSION_MAINTAIN_PROPERTY =
        Call.SESSION_MAINTAIN_PROPERTY;
    public static final String ENCODING_STYLE_PROPERTY =
        Call.ENCODINGSTYLE_URI_PROPERTY;
    public static final String HTTP_COOKIE_JAR =
        StubPropertyConstants.HTTP_COOKIE_JAR;
    public static final String RPC_LITERAL_RESPONSE_QNAME =
        "com.sun.xml.rpc.client.responseQName";
    public static final String HOSTNAME_VERIFICATION_PROPERTY =
        StubPropertyConstants.HOSTNAME_VERIFICATION_PROPERTY;
    public static final String REDIRECT_REQUEST_PROPERTY =
        StubPropertyConstants.REDIRECT_REQUEST_PROPERTY;
    public static final String SECURITY_CONTEXT =
        StubPropertyConstants.SECURITY_CONTEXT;
    public static final String HTTP_STATUS_CODE =
        StubPropertyConstants.HTTP_STATUS_CODE;
    /*public static final String ATTACHMENT_CONTEXT =
        StubPropertyConstants.ATTACHMENT_CONTEXT;*/
    public static final String SET_ATTACHMENT_PROPERTY =
        "com.sun.xml.rpc.attachment.SetAttachmentContext";
    public static final String GET_ATTACHMENT_PROPERTY =
        "com.sun.xml.rpc.attachment.GetAttachmentContext";
    
    // A string-valued property "none", "pessimistic" and "optimistic"
    // Used for Fast Infoset content negotiation
    public static final String CONTENT_NEGOTIATION_PROPERTY =
        StubPropertyConstants.CONTENT_NEGOTIATION_PROPERTY;
}
