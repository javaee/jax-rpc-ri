/*
 * $Id: CachedStack.java,v 1.2 2006-04-13 01:33:41 ofung Exp $
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
