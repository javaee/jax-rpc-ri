/*
 * $Id: LocalizableExceptionAdapter.java,v 1.2 2006-04-13 01:33:54 ofung Exp $
 */

/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License).  You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the license at
 * https://glassfish.dev.java.net/public/CDDLv1.0.html.
 * See the License for the specific language governing
 * permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at https://glassfish.dev.java.net/public/CDDLv1.0.html.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * you own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
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
