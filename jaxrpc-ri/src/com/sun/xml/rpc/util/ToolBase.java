/*
 * $Id: ToolBase.java,v 1.3 2007-07-13 23:36:38 ofung Exp $
 */

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
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

package com.sun.xml.rpc.util;

import java.io.OutputStream;
import java.io.PrintStream;

import com.sun.xml.rpc.util.localization.Localizable;
import com.sun.xml.rpc.util.localization.LocalizableMessageFactory;
import com.sun.xml.rpc.util.localization.Localizer;

/**
 * A base class for command-line tools.
 *
 * @author JAX-RPC Development Team
 */
public abstract class ToolBase {

	public ToolBase(OutputStream out, String program) {
		this.out = out;
		this.program = program;
		initialize();
	}

	protected void initialize() {
		messageFactory = new LocalizableMessageFactory(getResourceBundleName());
		localizer = new Localizer();
	}

	public boolean run(String[] args) {
		if (!parseArguments(args)) {
			return false;
		}

		try {
			run();
			return wasSuccessful();
		} catch (Exception e) {
			if (e instanceof Localizable) {
				report((Localizable) e);
			} else {
				report(getMessage(getGenericErrorMessage(), e.toString()));
			}
			printStackTrace(e);
			return false;
		}
	}

	public boolean wasSuccessful() {
		return true;
	}

	protected abstract boolean parseArguments(String[] args);
	protected abstract void run() throws Exception;
	protected abstract String getGenericErrorMessage();
	protected abstract String getResourceBundleName();

	public void printStackTrace(Throwable t) {
		PrintStream outstream =
			out instanceof PrintStream
				? (PrintStream) out
				: new PrintStream(out, true);
		t.printStackTrace(outstream);
		outstream.flush();
	}

	protected void report(String msg) {
		PrintStream outstream =
			out instanceof PrintStream
				? (PrintStream) out
				: new PrintStream(out, true);
		outstream.println(msg);
		outstream.flush();
	}

	protected void report(Localizable msg) {
		report(localizer.localize(msg));
	}

	public Localizable getMessage(String key) {
		return getMessage(key, (Object[]) null);
	}

	public Localizable getMessage(String key, String arg) {
		return messageFactory.getMessage(key, new Object[] { arg });
	}

	public Localizable getMessage(String key, String arg1, String arg2) {
		return messageFactory.getMessage(key, new Object[] { arg1, arg2 });
	}

	public Localizable getMessage(
		String key,
		String arg1,
		String arg2,
		String arg3) {
		return messageFactory.getMessage(
			key,
			new Object[] { arg1, arg2, arg3 });
	}

	public Localizable getMessage(String key, Localizable localizable) {
		return messageFactory.getMessage(key, new Object[] { localizable });
	}

	public Localizable getMessage(String key, Object[] args) {
		return messageFactory.getMessage(key, args);
	}

	protected OutputStream out;
	protected String program;
	protected Localizer localizer;
	protected LocalizableMessageFactory messageFactory;

	protected final static String TRUE = "true";
	protected final static String FALSE = "false";

}
