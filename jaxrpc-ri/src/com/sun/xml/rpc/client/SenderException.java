/*
 * $Id: SenderException.java,v 1.1 2006-04-12 20:35:22 kohlert Exp $
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
public class SenderException extends JAXRPCExceptionBase {

    public SenderException(String key) {
        super(key);
    }

    public SenderException(String key, String arg) {
        super(key, arg);
    }

    public SenderException(String key, Object[] args) {
        super(key, args);
    }

    public SenderException(String key, Localizable arg) {
        super(key, arg);
    }

    public SenderException(Localizable arg) {
        super("sender.nestedError", arg);
    }

    public String getResourceBundleName() {
        return "com.sun.xml.rpc.resources.sender";
    }
}
