/*
 * $Id: LocalizableMessageFactory.java,v 1.1 2006-04-12 20:34:17 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package com.sun.xml.rpc.util.localization;

/**
 * @author JAX-RPC Development Team
 */
public class LocalizableMessageFactory {

    protected String _bundlename;

    public LocalizableMessageFactory(String bundlename) {
        _bundlename = bundlename;
    }

    public Localizable getMessage(String key) {
        return getMessage(key, (Object[]) null);
    }

    public Localizable getMessage(String key, String arg) {
        return getMessage(key, new Object[] { arg });
    }

    public Localizable getMessage(String key, Localizable localizable) {
        return getMessage(key, new Object[] { localizable });
    }

    public Localizable getMessage(String key, Object[] args) {
        return new LocalizableMessage(_bundlename, key, args);
    }

}
