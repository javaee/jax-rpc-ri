/*
 * $Id: LocalClientTransportFactory.java,v 1.1 2006-04-12 20:34:38 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.client.local;

import java.io.OutputStream;

import com.sun.xml.rpc.client.ClientTransport;
import com.sun.xml.rpc.client.ClientTransportFactory;
import com.sun.xml.rpc.soap.message.Handler;

/**
 * @author JAX-RPC Development Team
 */
public class LocalClientTransportFactory implements ClientTransportFactory {

    //this class is used primarily for debugging purposes
    public LocalClientTransportFactory(Handler handler) {
        _handler = handler;
        _logStream = null;
    }

    public LocalClientTransportFactory(
        Handler handler,
        OutputStream logStream) {
        _handler = handler;
        _logStream = logStream;
    }

    public ClientTransport create() {
        return new LocalClientTransport(_handler, _logStream);
    }

    private Handler _handler;
    private OutputStream _logStream;
}
