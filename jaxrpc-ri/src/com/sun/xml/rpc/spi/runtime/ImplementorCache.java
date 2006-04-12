/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.xml.rpc.spi.runtime;

/**
 * This interface is implemented by 
 * com.sun.xml.rpc.server.http.ImplementorCache
 */
public interface ImplementorCache {
    public void setDelegate(ImplementorCacheDelegate delegate);
}
