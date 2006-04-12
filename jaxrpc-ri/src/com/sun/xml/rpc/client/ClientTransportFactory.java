/*
 * $Id: ClientTransportFactory.java,v 1.1 2006-04-12 20:35:21 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.client;

/**
 * @author JAX-RPC Development Team
 */
public interface ClientTransportFactory
    extends com.sun.xml.rpc.spi.runtime.ClientTransportFactory {
    public ClientTransport create();
}
