/*
 * $Id: ParseException.java,v 1.1 2006-04-12 20:32:56 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.wsdl.framework;

import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.localization.Localizable;

/**
 * An exception signalling a parsing error.
 *
 * @author JAX-RPC Development Team
 */
public class ParseException extends JAXRPCExceptionBase {

    public ParseException(String key) {
        super(key);
    }

    public ParseException(String key, String arg) {
        super(key, arg);
    }

    public ParseException(String key, Localizable localizable) {
        super(key, localizable);
    }

    public ParseException(String key, String arg, Localizable localizable) {
        this(key, new Object[] { arg, localizable });
    }

    public ParseException(String key, Object[] args) {
        super(key, args);
    }

    public String getResourceBundleName() {
        return "com.sun.xml.rpc.resources.wsdl";
    }
}
