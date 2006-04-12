/*
 * $Id: SingleElementIterator.java,v 1.1 2006-04-12 20:32:05 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.lang.UnsupportedOperationException;
import java.lang.IllegalStateException;

/**
 * An Iterator on a single element collection.
 *
 * @author JAX-RPC Development Team
 */
public class SingleElementIterator implements Iterator {
	protected boolean hasNext = false;
	protected Object element;

	public SingleElementIterator() {
	}
	
	public SingleElementIterator(Object element) {
		this.element = element;
		hasNext = true;
	}
	
	public boolean hasNext() {
		return hasNext;
	}
	
	public Object next() throws NoSuchElementException {
		if (!hasNext) {
			throw new NoSuchElementException("No elements left in SingleElementIterator next()");
		}
		hasNext = false;
		return element;
	}
	
	public void remove()
		throws UnsupportedOperationException, IllegalStateException {
		throw new UnsupportedOperationException("SingleElementIterator does not support remove()");
	}
}
