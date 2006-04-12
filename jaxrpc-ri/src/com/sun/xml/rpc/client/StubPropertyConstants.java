/*
 * $Id: StubPropertyConstants.java,v 1.1 2006-04-12 20:35:22 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.client;

import javax.xml.rpc.Stub;

/**
 * @author JAX-RPC Development Team
 */
public interface StubPropertyConstants {
    public static final String SERVICEIMPL_NAME = "serviceImplementationName";
    public static final String USERNAME_PROPERTY = Stub.USERNAME_PROPERTY;
    public static final String PASSWORD_PROPERTY = Stub.PASSWORD_PROPERTY;
    public static final String ENDPOINT_ADDRESS_PROPERTY =
        Stub.ENDPOINT_ADDRESS_PROPERTY;
    public static final String SESSION_MAINTAIN_PROPERTY =
        Stub.SESSION_MAINTAIN_PROPERTY;
    public static final String OPERATION_STYLE_PROPERTY =
        "com.sun.client.OperationStyleProperty";
    public static final String ENCODING_STYLE_PROPERTY =
        " com.sun.client.EncodingStyleProperty";
    public static final String HOSTNAME_VERIFICATION_PROPERTY =
        "com.sun.xml.rpc.client.http.HostnameVerificationProperty";
    public static final String HTTP_COOKIE_JAR =
        "com.sun.xml.rpc.client.http.CookieJar";
    public static final String SECURITY_CONTEXT =
        "com.sun.xml.rpc.security.context";
    public static final String HTTP_STATUS_CODE =
        "com.sun.xml.rpc.client.http.HTTPStatusCode";
    /*public static final String ATTACHMENT_CONTEXT =
        "com.sun.xml.rpc.attachment.AttachmentContext";*/
    public static final String REDIRECT_REQUEST_PROPERTY =
        "com.sun.xml.rpc.client.http.RedirectRequestProperty";
    public static final String SET_ATTACHMENT_PROPERTY =
        "com.sun.xml.rpc.attachment.SetAttachmentContext";
    public static final String GET_ATTACHMENT_PROPERTY =
        "com.sun.xml.rpc.attachment.GetAttachmentContext";
    
    // A string-valued property "none", "pessimistic" and "optimistic"
    // Used for Fast Infoset content negotiation
    public static final String CONTENT_NEGOTIATION_PROPERTY =
        "com.sun.xml.rpc.client.ContentNegotiation";
}
