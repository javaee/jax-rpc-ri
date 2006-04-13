/*
 * $Id: LongStack.java,v 1.2 2006-04-13 01:33:48 ofung Exp $
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

/**
 * @author JAX-RPC Development Team
 */
public final class LongStack {
	private long[] values = null;
	private int topOfStack = 0;

	public LongStack() {
		this(32);
	}

	public LongStack(int size) {
		values = new long[size];
	}

	public void push(long newValue) {
		resize();
		values[topOfStack] = newValue;
		++topOfStack;
	}

	public long pop() {
		--topOfStack;
		return values[topOfStack];
	}

	public long peek() {
		return values[topOfStack - 1];
	}

	private void resize() {
		if (topOfStack >= values.length) {
			long[] newValues = new long[values.length * 2];
			System.arraycopy(values, 0, newValues, 0, values.length);
			values = newValues;
		}
	}
}
