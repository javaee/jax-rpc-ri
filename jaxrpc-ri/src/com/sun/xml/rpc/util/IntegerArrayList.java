/*
 * $Id: IntegerArrayList.java,v 1.2 2006-04-13 01:33:46 ofung Exp $
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
