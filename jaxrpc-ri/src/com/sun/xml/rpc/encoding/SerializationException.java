/*
 * $Id: SerializationException.java,v 1.1 2006-04-12 20:33:09 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.encoding;

import com.sun.xml.rpc.util.exception.*;
import com.sun.xml.rpc.util.localization.*;

/**
 * SerializationException represents an exception that occurred while 
 * serializing a Java value as XML.
 *
 * @see com.sun.xml.rpc.util.exception.JAXRPCExceptionBase
 *
 * @author JAX-RPC Development Team
 */
public class SerializationException extends JAXRPCExceptionBase {

    public SerializationException(String key) {
        super(key);
    }

    public SerializationException(String key, String arg) {
        super(key, arg);
    }

    public SerializationException(String key, Object[] args) {
        super(key, args);
    }

    public SerializationException(String key, Localizable arg) {
        super(key, arg);
    }

    public SerializationException(Localizable arg) {
        super("nestedSerializationError", arg);
    }

    public String getResourceBundleName() {
        return "com.sun.xml.rpc.resources.encoding";
    }

}
