/*
 * $Id: ValidationException.java,v 1.1 2006-04-12 20:32:58 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.wsdl.framework;

import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.localization.Localizable;

/**
 * An exception signalling that validation of an entity failed.
 *
 * @author JAX-RPC Development Team
 */
public class ValidationException extends JAXRPCExceptionBase {

    public ValidationException(String key) {
        super(key);
    }

    public ValidationException(String key, String arg) {
        super(key, arg);
    }

    public ValidationException(String key, Localizable localizable) {
        super(key, localizable);
    }

    public ValidationException(String key, Object[] args) {
        super(key, args);
    }

    public String getResourceBundleName() {
        return "com.sun.xml.rpc.resources.wsdl";
    }
}
