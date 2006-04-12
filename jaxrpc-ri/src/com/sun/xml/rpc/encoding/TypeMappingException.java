/*
 * $Id: TypeMappingException.java,v 1.1 2006-04-12 20:33:12 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.encoding;

import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.localization.Localizable;

/**
 * TypeMappingException represents an exception that occurred in the type mapping framework.
 *
 * @see com.sun.xml.rpc.util.exception.JAXRPCExceptionBase
 *
 * @author JAX-RPC Development Team
 */
public class TypeMappingException extends JAXRPCExceptionBase {

    public TypeMappingException(String key) {
        super(key);
    }

    public TypeMappingException(String key, String arg) {
        super(key, arg);
    }

    public TypeMappingException(String key, Object[] args) {
        super(key, args);
    }

    public TypeMappingException(String key, Localizable arg) {
        super(key, arg);
    }

    public TypeMappingException(Localizable arg) {
        super("typemapping.nested.exception", arg);
    }

    public String getResourceBundleName() {
        return "com.sun.xml.rpc.resources.encoding";
    }

}
