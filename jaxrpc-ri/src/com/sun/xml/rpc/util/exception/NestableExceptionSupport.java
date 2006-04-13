/*
 * $Id: NestableExceptionSupport.java,v 1.2 2006-04-13 01:33:55 ofung Exp $
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

/**
 * NestableExceptionSupport
 *
 * @author JAX-RPC Development Team
 */

public class NestableExceptionSupport {
	protected Throwable cause = null;

	public NestableExceptionSupport() {
	}

	public NestableExceptionSupport(Throwable cause) {
		this.cause = cause;
	}

	public void printStackTrace() {
		//super.printStackTrace();
		if (cause != null) {
			System.err.println("\nCAUSE:\n");
			cause.printStackTrace();
		}
	}

	public void printStackTrace(PrintStream s) {
		//super.printStackTrace(s);
		if (cause != null) {
			s.println("\nCAUSE:\n");
			cause.printStackTrace(s);
		}
	}

	public void printStackTrace(PrintWriter s) {
		//super.printStackTrace(s);
		if (cause != null) {
			s.println("\nCAUSE:\n");
			cause.printStackTrace(s);
		}
	}

	public Throwable getCause() {
		return cause;
	}

	public void setCause(Throwable cause) {
		this.cause = cause;
	}
}
