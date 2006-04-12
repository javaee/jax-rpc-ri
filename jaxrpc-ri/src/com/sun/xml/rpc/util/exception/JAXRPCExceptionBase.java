/*
 * $Id: JAXRPCExceptionBase.java,v 1.1 2006-04-12 20:35:15 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.util.exception;

import java.io.PrintStream;
import java.io.PrintWriter;

import javax.xml.rpc.JAXRPCException;

import com.sun.xml.rpc.util.localization.Localizable;
import com.sun.xml.rpc.util.localization.LocalizableSupport;
import com.sun.xml.rpc.util.localization.Localizer;

/**
 * JAXRPCExceptionBase
 *
 * @author JAX-RPC Development Team
 */
public abstract class JAXRPCExceptionBase
	extends JAXRPCException
	implements Localizable {
	protected LocalizableSupport localizablePart;
	protected NestableExceptionSupport nestablePart;

	public JAXRPCExceptionBase() {
		nestablePart = new NestableExceptionSupport();
	}

	public JAXRPCExceptionBase(String key) {
		this();
		localizablePart = new LocalizableSupport(key);
	}

	public JAXRPCExceptionBase(String key, String arg) {
		this();
		localizablePart = new LocalizableSupport(key, arg);
	}

	public JAXRPCExceptionBase(String key, Localizable localizable) {
		this(key, new Object[] { localizable });
	}

	protected JAXRPCExceptionBase(String key, Object[] args) {
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

	public String getKey() {
		return localizablePart.getKey();
	}

	public Object[] getArguments() {
		return localizablePart.getArguments();
	}

	public abstract String getResourceBundleName();

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
