/*
 * $Id: ToolBase.java,v 1.2.2.2 2008-02-20 17:48:38 venkatajetti Exp $
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

package com.sun.xml.rpc.util;

import java.io.OutputStream;
import java.io.PrintStream;

import java.util.logging.Level;
import java.util.logging.Logger;

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
        // CR-6660308, Merge from JavaCAPS RTS for backward compatibility
        if (out == null) {
            logger = Logger.getLogger(ToolBase.class.getName());
        }

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
        // CR-6660308, Merge from JavaCAPS RTS for backward compatibility
        if (out != null) {
            PrintStream outstream =
                out instanceof PrintStream
                    ? (PrintStream) out
                    : new PrintStream(out, true);
            t.printStackTrace(outstream);
            outstream.flush();
        } else if (logger != null) {
            logger.log(Level.SEVERE, "ToolBase Error occured: ", t);
        }

	}

	protected void report(String msg) {
        // CR-6660308, Merge from JavaCAPS RTS for backward compatibility
        if (out != null) {
            PrintStream outstream =
                out instanceof PrintStream
                    ? (PrintStream) out
                    : new PrintStream(out, true);
            outstream.println(msg);
            outstream.flush();
        } else if (logger != null) {
            logger.log(Level.INFO, msg);
        }

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

   private static Logger logger = null;
}
