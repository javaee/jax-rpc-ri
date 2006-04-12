/*
 * $Id: LongStack.java,v 1.1 2006-04-12 20:32:03 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
