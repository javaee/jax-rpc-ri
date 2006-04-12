/*
 * $Id: ModelerException.java,v 1.1 2006-04-12 20:34:24 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.modeler;

import com.sun.xml.rpc.processor.ProcessorException;
import com.sun.xml.rpc.util.localization.Localizable;

/**
 * ModelerException represents an exception that occurred while
 * visiting service model.
 *
 * @see com.sun.xml.rpc.util.exception.JAXRPCExceptionBase
 *
 * @author JAX-RPC Development Team
*/
public class ModelerException extends ProcessorException {
    
    public ModelerException(String key) {
        super(key);
    }
    
    public ModelerException(String key, String arg) {
        super(key, arg);
    }
    
    public ModelerException(String key, Object[] args) {
        super(key, args);
    }
    
    public ModelerException(String key, Localizable arg) {
        super(key, arg);
    }
    
    public ModelerException(Localizable arg) {
        super("modeler.nestedModelError", arg);
    }
    
    public String getResourceBundleName() {
        return "com.sun.xml.rpc.resources.modeler";
    }
    
}
