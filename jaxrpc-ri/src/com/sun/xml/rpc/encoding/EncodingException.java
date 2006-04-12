/*
 * $Id: EncodingException.java,v 1.1 2006-04-12 20:33:15 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.encoding;

import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.localization.Localizable;

/**
 * EncodingException represents an exception that occurred while supporting
 * object serialization or deserialization.
 *
 * @see com.sun.xml.rpc.util.exception.JAXRPCExceptionBase
 *
 * @author JAX-RPC Development Team
 */
public class EncodingException extends JAXRPCExceptionBase {

    public EncodingException(String key) {
        super(key);
    }

    public EncodingException(String key, String arg) {
        super(key, arg);
    }

    public EncodingException(String key, Object[] args) {
        super(key, args);
    }

    public EncodingException(String key, Localizable arg) {
        super(key, arg);
    }

    public EncodingException(Localizable arg) {
        super("nestedEncodingError", arg);
    }

    public String getResourceBundleName() {
        return "com.sun.xml.rpc.resources.encoding";
    }

}
