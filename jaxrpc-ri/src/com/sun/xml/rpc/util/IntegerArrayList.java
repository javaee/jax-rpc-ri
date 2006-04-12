/*
 * $Id: IntegerArrayList.java,v 1.1 2006-04-12 20:32:07 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.util;

/**
 * @author JAX-RPC Development Team
 */
public final class IntegerArrayList {
	private int values[] = null;
	private int length = 0;

	public IntegerArrayList() {
		this(8);
	}
	
	public IntegerArrayList(int size) {
		values = new int[size];
	}

	public boolean add(int value) {
		resize();
		values[length++] = value;

		return true;
	}

	public int get(int index) {
		return values[index];
	}

	public void clear() {
		values = new int[length];
		length = 0;
	}

	public int[] toArray() {
		int[] array = new int[length];
		System.arraycopy(values, 0, array, 0, length);
		return array;
	}

	public int size() {
		return length;
	}
	
	private void resize() {
		if (length >= values.length) {
			int newValues[] = new int[values.length * 2];
			System.arraycopy(values, 0, newValues, 0, values.length);
			values = newValues;
		}
	}
}
