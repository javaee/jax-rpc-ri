/*
 * $Id: CombinedIterator.java,v 1.1 2006-04-12 20:32:05 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/**
 * <p> Combines two iterators into one. </p>
 *
 * @author JAX-RPC Development Team
 */

package com.sun.xml.rpc.util;

import java.util.Iterator;

public class CombinedIterator implements Iterator {
	protected Iterator currentIterator;
	protected Iterator secondIterator;

	public CombinedIterator(Iterator firstIterator, Iterator secondIterator) {
		this.currentIterator = firstIterator;
		this.secondIterator = secondIterator;
	}

	public boolean hasNext() {
		if (!currentIterator.hasNext()) {
			currentIterator = secondIterator;
		}
		return currentIterator.hasNext();
	}

	public Object next() {
		if (!currentIterator.hasNext()) {
			currentIterator = secondIterator;
		}
		return currentIterator.next();
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}
}