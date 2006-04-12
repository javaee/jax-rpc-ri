/*
 * $Id: ServiceExceptionImpl.java,v 1.1 2006-04-12 20:35:21 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.client;

import java.io.PrintStream;
import java.io.PrintWriter;

import javax.xml.rpc.ServiceException;

import com.sun.xml.rpc.util.exception.NestableExceptionSupport;
import com.sun.xml.rpc.util.localization.Localizable;
import com.sun.xml.rpc.util.localization.LocalizableSupport;
import com.sun.xml.rpc.util.localization.Localizer;

/**
 * @author JAX-RPC Development Team
 */
public class ServiceExceptionImpl
    extends ServiceException
    implements Localizable {
    protected LocalizableSupport localizablePart;
    protected NestableExceptionSupport nestablePart;

    public ServiceExceptionImpl() {
        nestablePart = new NestableExceptionSupport();
    }

    public ServiceExceptionImpl(String key) {
        this();
        localizablePart = new LocalizableSupport(key);
    }

    public ServiceExceptionImpl(String key, String arg) {
        this();
        localizablePart = new LocalizableSupport(key, arg);
    }

    public ServiceExceptionImpl(String key, Localizable localizable) {
        this(key, new Object[] { localizable });
    }

    public ServiceExceptionImpl(String key, Object[] args) {
        this();
        localizablePart = new LocalizableSupport(key, args);
        if (args != null && nestablePart.getCause() == null) {
            for (int i = 0; i < args.length; ++i) {
                if (args[i] instanceof Throwable) {
                    nestablePart.setCause((Throwable) args[i]);
                    break;
                }
            }
        }
    }

    public ServiceExceptionImpl(Localizable arg) {
        this("service.exception.nested", arg);
    }

    public String getResourceBundleName() {
        return "com.sun.xml.rpc.resources.dii";
    }

    public String getKey() {
        return localizablePart.getKey();
    }

    public Object[] getArguments() {
        return localizablePart.getArguments();
    }

    public String toString() {
        // for debug purposes only
        //return getClass().getName() + " (" + getKey() + ")";
        return getMessage();
    }

    public String getMessage() {
        Localizer localizer = new Localizer();
        return localizer.localize(this);
    }

    public Throwable getLinkedException() {
        return nestablePart.getCause();
    }

    public void printStackTrace() {
        super.printStackTrace();
        nestablePart.printStackTrace();
    }

    public void printStackTrace(PrintStream s) {
        super.printStackTrace(s);
        nestablePart.printStackTrace(s);
    }

    public void printStackTrace(PrintWriter s) {
        super.printStackTrace(s);
        nestablePart.printStackTrace(s);
    }
}
