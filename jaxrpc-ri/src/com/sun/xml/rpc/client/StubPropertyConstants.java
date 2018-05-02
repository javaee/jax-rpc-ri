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
