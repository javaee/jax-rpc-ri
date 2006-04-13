/*
 * $Id: CombinedIterator.java,v 1.2 2006-04-13 01:33:42 ofung Exp $
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