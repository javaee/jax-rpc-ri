/*
 * $Id: HandlerException.java,v 1.1 2006-04-12 20:35:21 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.client;

import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.localization.Localizable;

/**
 * @author JAX-RPC Development Team
 */
public class HandlerException extends JAXRPCExceptionBase {

    public HandlerException(String key) {
        super(key);
    }

    public HandlerException(String key, String arg) {
        super(key, arg);
    }

    public HandlerException(String key, Object[] args) {
        super(key, args);
    }

    public HandlerException(String key, Localizable arg) {
        super(key, arg);
    }

    public HandlerException(Localizable arg) {
        super("handler.nestedError", arg);
    }

    public String getResourceBundleName() {
        return "com.sun.xml.rpc.resources.handler";
    }
}
