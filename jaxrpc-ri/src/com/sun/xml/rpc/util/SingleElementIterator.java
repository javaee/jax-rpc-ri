/*
 * $Id: SingleElementIterator.java,v 1.2 2006-04-13 01:33:50 ofung Exp $
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
