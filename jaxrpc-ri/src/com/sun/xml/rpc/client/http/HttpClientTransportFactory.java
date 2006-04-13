/*
 * $Id: HttpClientTransportFactory.java,v 1.2 2006-04-13 01:26:57 ofung Exp $
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

package com.sun.xml.rpc.client.http;

import java.io.OutputStream;

import com.sun.xml.rpc.client.ClientTransport;
import com.sun.xml.rpc.client.ClientTransportFactory;

/**
 * @author JAX-RPC Development Team
 */
public class HttpClientTransportFactory implements ClientTransportFactory {

    public HttpClientTransportFactory() {
        this(null);
    }

    public HttpClientTransportFactory(OutputStream logStream) {
        _logStream = logStream;
    }

    public ClientTransport create() {
        return new HttpClientTransport(_logStream);
    }

    private OutputStream _logStream;
}
