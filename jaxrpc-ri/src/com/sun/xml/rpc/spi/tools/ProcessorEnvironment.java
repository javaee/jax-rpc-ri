/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.xml.rpc.spi.tools;

import java.util.Iterator;

/**
 * This class is implemented by 
 * com.sun.xml.rpc.processor.util.ProcessorEnvironment
 */
public interface ProcessorEnvironment {
    public Iterator getGeneratedFiles();
}
