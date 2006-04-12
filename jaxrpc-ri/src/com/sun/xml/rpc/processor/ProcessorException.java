/*
 * $Id: ProcessorException.java,v 1.1 2006-04-12 20:34:54 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor;

import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.localization.Localizable;

/**
 * ProcessorException represents an exception that occurred while processing
 * a web service.
 *
 * @see com.sun.xml.rpc.util.exception.JAXRPCExceptionBase
 *
 * @author JAX-RPC Development Team
 */
public class ProcessorException extends JAXRPCExceptionBase {
    
    public ProcessorException(String key) {
        super(key);
    }

    public ProcessorException(String key, String arg) {
        super(key, arg);
    }

    public ProcessorException(String key, Object[] args) {
        super(key, args);
    }

    public ProcessorException(String key, Localizable arg) {
        super(key, arg);
    }

    public String getResourceBundleName() {
        return "com.sun.xml.rpc.resources.processor";
    }
}
