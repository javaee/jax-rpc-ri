/*
 * $Id: StubPropertyConstants.java,v 1.2 2006-04-13 01:26:39 ofung Exp $
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
