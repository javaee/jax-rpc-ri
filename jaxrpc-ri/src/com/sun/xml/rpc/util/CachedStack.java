/*
 * $Id: CachedStack.java,v 1.1 2006-04-12 20:32:05 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author JAX-RPC Development Team
 */
public abstract class CachedStack {
	protected List elements = new ArrayList();
	protected int topOfStack = -1;

	protected abstract Object createObject() throws Exception;

	public void push() throws Exception {
		++topOfStack;
		if (elements.size() == topOfStack) {
			elements.add(topOfStack, createObject());
		}
	}

	public void pop() {
		if (topOfStack < 0) {
			throw new ArrayIndexOutOfBoundsException(topOfStack);
		}
		--topOfStack;
	}

	public Object peek() {
		if (topOfStack == -1) {
			return null;
		}
		return elements.get(topOfStack);
	}

	public int depth() {
		return topOfStack + 1;
	}
}
