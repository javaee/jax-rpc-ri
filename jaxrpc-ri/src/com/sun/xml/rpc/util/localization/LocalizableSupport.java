/*
 * $Id: LocalizableSupport.java,v 1.1 2006-04-12 20:34:17 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.util.localization;

/**
 * @author JAX-RPC Development Team
 */
public class LocalizableSupport {
    protected String key;
    protected Object[] arguments;

    public LocalizableSupport(String key) {
        this(key, (Object[]) null);
    }

    public LocalizableSupport(String key, String argument) {
        this(key, new Object[] { argument });
    }

    public LocalizableSupport(String key, Localizable localizable) {
        this(key, new Object[] { localizable });
    }

    public LocalizableSupport(String key, Object[] arguments) {
        this.key = key;
        this.arguments = arguments;
    }

    public String getKey() {
        return key;
    }
    public Object[] getArguments() {
        return arguments;
    }

    //abstract public String getResourceBundleName();
}
