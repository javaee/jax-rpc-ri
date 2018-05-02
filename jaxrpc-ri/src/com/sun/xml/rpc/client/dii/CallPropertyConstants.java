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
