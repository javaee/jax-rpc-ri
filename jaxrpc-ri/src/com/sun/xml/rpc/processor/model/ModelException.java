/*
 * $Id: ModelException.java,v 1.1 2006-04-12 20:33:06 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.model;

import com.sun.xml.rpc.processor.ProcessorException;
import com.sun.xml.rpc.util.localization.Localizable;

/**
 * ModelException represents an exception that occurred while
 * visiting service model.
 *
 * @see com.sun.xml.rpc.util.exception.JAXRPCExceptionBase
 *
 * @author JAX-RPC Development Team
 */
public class ModelException extends ProcessorException {
    
    public ModelException(String key) {
        super(key);
    }
    
    public ModelException(String key, String arg) {
        super(key, arg);
    }
    
    public ModelException(String key, Object[] args) {
        super(key, args);
    }
    
    public ModelException(String key, Localizable arg) {
        super(key, arg);
    }
    
    public ModelException(Localizable arg) {
        super("model.nestedModelError", arg);
    }
    
    public String getResourceBundleName() {
        return "com.sun.xml.rpc.resources.model";
    }
}
