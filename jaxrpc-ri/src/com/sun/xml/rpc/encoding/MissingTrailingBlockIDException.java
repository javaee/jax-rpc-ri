/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.encoding;

import com.sun.xml.rpc.util.exception.*;
import com.sun.xml.rpc.util.localization.*;

/**
 * MissingTrailingBlockIDException represents an exception that occurred while
 * deserializing a Java value from poorly formed XML
 *
 * @see com.sun.xml.rpc.util.exception.JAXRPCExceptionBase
 *
 * @author JAX-RPC Development Team
 */
public class MissingTrailingBlockIDException extends JAXRPCExceptionBase {

    public MissingTrailingBlockIDException(String key) {
        super(key);
    }

    public MissingTrailingBlockIDException(String key, String arg) {
        super(key, arg);
    }

    public MissingTrailingBlockIDException(String key, Object[] args) {
        super(key, args);
    }

    public MissingTrailingBlockIDException(String key, Localizable arg) {
        super(key, arg);
    }

    public MissingTrailingBlockIDException(Localizable arg) {
        super("nestedDeserializationError", arg);
    }

    public String getResourceBundleName() {
        return "com.sun.xml.rpc.resources.encoding";
    }

}
