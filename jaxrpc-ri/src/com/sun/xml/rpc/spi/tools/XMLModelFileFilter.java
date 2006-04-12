/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.xml.rpc.spi.tools;

import java.io.InputStream;
import java.net.URL;

/**
 * This class is implemented by 
 * com.sun.xml.rpc.processor.util.XMLModelFileFilter
 */
public interface XMLModelFileFilter {
    /**
     * Used by S1AS to detected whether the mapping file 
     * specified is a jaxrpc model file.
     */
    public boolean isModelFile(InputStream is);

    /**
     * Used by S1AS to detected whether the mapping file 
     * specified is a jaxrpc model file.
     */
    public boolean isModelFile(URL url);
}
