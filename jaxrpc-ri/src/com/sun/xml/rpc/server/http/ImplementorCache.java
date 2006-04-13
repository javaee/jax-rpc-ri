/*
 * $Id: ImplementorCache.java,v 1.2 2006-04-13 01:32:04 ofung Exp $
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
