/*
 * $Id: LocalizableExceptionAdapter.java,v 1.1 2006-04-12 20:35:15 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.util.exception;

import java.io.PrintStream;
import java.io.PrintWriter;

import com.sun.xml.rpc.util.localization.Localizable;
import com.sun.xml.rpc.util.localization.Localizer;
import com.sun.xml.rpc.util.localization.NullLocalizable;

/**
 * LocalizableExceptionAdapter
 *
 * @author JAX-RPC Development Team
 */
public class LocalizableExceptionAdapter
	extends Exception
	implements Localizable {
	protected Localizable localizablePart;
	protected Throwable nestedException;

	public LocalizableExceptionAdapter(Throwable nestedException) {
		this.nestedException = nestedException;
		if (nestedException instanceof Localizable) {
			localizablePart = (Localizable) nestedException;
		} else {
			localizablePart = new NullLocalizable(nestedException.toString());
		}
	}

	public String getKey() {
		return localizablePart.getKey();
	}

	public Object[] getArguments() {
		return localizablePart.getArguments();
	}

	public String getResourceBundleName() {
		return localizablePart.getResourceBundleName();
	}

	public String toString() {
		// for debug purposes only
		return nestedException.toString();
	}

	public String getLocalizedMessage() {
		if (nestedException == localizablePart) {
			Localizer localizer = new Localizer();
			return localizer.localize(localizablePart);
		} else {
			return nestedException.getLocalizedMessage();
		}
	}

	public String getMessage() {
		return getLocalizedMessage();
	}

	public Throwable getNestedException() {
		return nestedException;
	}

	public void printStackTrace() {
		nestedException.printStackTrace();
	}

	public void printStackTrace(PrintStream s) {
		nestedException.printStackTrace(s);
	}

	public void printStackTrace(PrintWriter s) {
		nestedException.printStackTrace(s);
	}
}
