/*
 * $Id: HttpClientTransportFactory.java,v 1.1 2006-04-12 20:35:19 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
