/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
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
