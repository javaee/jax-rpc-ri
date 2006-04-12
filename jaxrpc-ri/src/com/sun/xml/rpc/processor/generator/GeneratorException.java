/*
 * $Id: GeneratorException.java,v 1.1 2006-04-12 20:33:43 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.generator;

import com.sun.xml.rpc.processor.ProcessorException;
import com.sun.xml.rpc.util.localization.Localizable;

/**
 *
 * @author JAX-RPC Development Team
 */
public class GeneratorException extends ProcessorException {

    public GeneratorException(String key) {
        super(key);
    }

    public GeneratorException(String key, String arg) {
        super(key, arg);
    }

    public GeneratorException(String key, Object[] args) {
        super(key, args);
    }

    public GeneratorException(String key, Localizable arg) {
        super(key, arg);
    }

    public String getResourceBundleName() {
        return "com.sun.xml.rpc.resources.generator";
    }
}
