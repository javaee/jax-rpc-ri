/*
 * $Id: DynamicInvocationException.java,v 1.1 2006-04-12 20:33:59 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.client.dii;

import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.localization.Localizable;

/**
 * DynamicInvocationException represents an exception that occurred while
 * using the DII interface
 *
 * @see com.sun.xml.rpc.util.exception.JAXRPCExceptionBase
 *
 * @author JAX-RPC Development Team
 */
public class DynamicInvocationException extends JAXRPCExceptionBase {

    public DynamicInvocationException(String key) {
        super(key);
    }

    public DynamicInvocationException(String key, String arg) {
        super(key, arg);
    }

    public DynamicInvocationException(String key, Object[] args) {
        super(key, args);
    }

    public DynamicInvocationException(String key, Localizable arg) {
        super(key, arg);
    }

    public DynamicInvocationException(Localizable arg) {
        super("dii.exception.nested", arg);
    }

    public String getResourceBundleName() {
        return "com.sun.xml.rpc.resources.dii";
    }

}
