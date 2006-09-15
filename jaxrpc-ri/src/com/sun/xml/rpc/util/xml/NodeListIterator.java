/*
 * $Id: NodeListIterator.java,v 1.2 2006-04-13 01:34:00 ofung Exp $
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

package com.sun.xml.rpc.util.xml;

import java.util.Iterator;

import org.w3c.dom.NodeList;

/**
 * @author JAX-RPC Development Team
 */
public class NodeListIterator implements Iterator {

	protected NodeList _list;
	protected int _index;

	public NodeListIterator(NodeList list) {
		_list = list;
		_index = 0;
	}

	public boolean hasNext() {
		if (_list == null)
			return false;
		return _index < _list.getLength();
	}

	public Object next() {
		Object obj = _list.item(_index);
		if (obj != null)
			++_index;
		return obj;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}
}