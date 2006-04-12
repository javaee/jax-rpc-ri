/*
 * $Id: Configuration.java,v 1.1 2006-04-12 20:34:50 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package com.sun.xml.rpc.processor.config;

import com.sun.xml.rpc.processor.util.ProcessorEnvironment;

/**
 *
 * @author JAX-RPC Development Team
 */
public class Configuration implements com.sun.xml.rpc.spi.tools.Configuration {

    public Configuration(com.sun.xml.rpc.spi.tools.ProcessorEnvironment env) {
        _env = (ProcessorEnvironment)env;
    }

    public com.sun.xml.rpc.spi.tools.ModelInfo getModelInfo() {
        return _modelInfo;
    }

    public void setModelInfo(com.sun.xml.rpc.spi.tools.ModelInfo i) {
        _modelInfo = (ModelInfo)i;
        _modelInfo.setParent(this);
    }
    
    public com.sun.xml.rpc.spi.tools.ProcessorEnvironment getEnvironment() {
        return _env;
    }
    
    private ProcessorEnvironment _env;
    private ModelInfo _modelInfo;
}
