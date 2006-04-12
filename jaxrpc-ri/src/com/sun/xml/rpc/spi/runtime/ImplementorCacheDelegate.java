/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.xml.rpc.spi.runtime;

/**
 * This class allows the implementation of ImplementorCache be
 * overridden.
 */
public abstract class ImplementorCacheDelegate {

    public ImplementorCacheDelegate() {
    }

    public Implementor getImplementorFor(RuntimeEndpointInfo targetEndpoint) {
        //no op
        return null;
    }

    public void releaseImplementor(
        RuntimeEndpointInfo targetEndpoint,
        Implementor implementor) {
        //no op
    }

    public void destroy() {
        //no op
    }
}
