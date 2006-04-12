/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.xml.rpc.spi.tools;

import com.sun.xml.rpc.spi.model.JavaInterface;
import com.sun.xml.rpc.spi.model.Port;

/**
 * This class is implemented by 
 * com.sun.xml.rpc.processor.generator.Names
 */
public interface Names {

    /**
     * Return stub class name for impl class name.
     */
    public String stubFor(Port port);
    public String interfaceImplClassName(JavaInterface intf);
}
