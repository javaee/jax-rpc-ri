/*
 * $Id: ImplementorCache.java,v 1.1 2006-04-12 20:33:40 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.server.http;

import javax.servlet.ServletConfig;

/**
 *
 * @author JAX-RPC Development Team
 */
public class ImplementorCache
    implements com.sun.xml.rpc.spi.runtime.ImplementorCache {

    public ImplementorCache(ServletConfig servletConfig) {
        delegate = new ImplementorCacheDelegateImpl(servletConfig);
    }

    public com.sun.xml.rpc.spi.runtime.Implementor getImplementorFor(
        RuntimeEndpointInfo targetEndpoint) {

        return delegate.getImplementorFor(targetEndpoint);
    }

    public void releaseImplementor(
        RuntimeEndpointInfo targetEndpoint,
        Implementor implementor) {
        delegate.releaseImplementor(targetEndpoint, implementor);
    }

    public void destroy() {
        delegate.destroy();
    }

    public void setDelegate(
        com
            .sun
            .xml
            .rpc
            .spi
            .runtime
            .ImplementorCacheDelegate implementorCacheDelegate) {
        delegate = implementorCacheDelegate;
    }

    com.sun.xml.rpc.spi.runtime.ImplementorCacheDelegate delegate;
}
