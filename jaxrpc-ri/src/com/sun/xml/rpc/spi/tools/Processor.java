/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.xml.rpc.spi.tools;

import com.sun.xml.rpc.spi.model.Model;

/**
 * This class is implemented by 
 * com.sun.xml.rpc.processor.Processor
 */
public interface Processor {
    public Model getModel();
}
