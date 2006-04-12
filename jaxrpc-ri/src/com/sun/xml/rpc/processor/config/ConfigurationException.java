/*
 * $Id: ConfigurationException.java,v 1.1 2006-04-12 20:34:49 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.config;

import com.sun.xml.rpc.processor.ProcessorException;
import com.sun.xml.rpc.util.localization.Localizable;

/**
 *
 * @author JAX-RPC Development Team
 */
public class ConfigurationException extends ProcessorException {

    public ConfigurationException(String key) {
        super(key);
    }

    public ConfigurationException(String key, String arg) {
        super(key, arg);
    }

    public ConfigurationException(String key, Object[] args) {
        super(key, args);
    }

    public ConfigurationException(String key, Localizable arg) {
        super(key, arg);
    }

    public String getResourceBundleName() {
        return "com.sun.xml.rpc.resources.configuration";
    }

}
