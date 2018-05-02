/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2018 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.sun.xml.rpc.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * <p> A Map that keeps track of the order in which entries are made. The
 * <code>values()</code> method returns an unmodifiable List of the values
 * in the order in which they were added. A new method,
 * <code>keys()</code> has been added. It returns an unmodifiable List of the
 * keys in the order in which they were added. </p>
 *
 * @author JAX-RPC Development Team
 */
public class StructMap implements Map {
	protected HashMap map = new HashMap();
	protected ArrayList keys = new ArrayList();
	protected ArrayList values = new ArrayList();

	public int size() {
		return map.size();
	}
	public boolean isEmpty() {
		return map.isEmpty();
	}
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}
	public Object get(Object key) {
		return map.get(key);
	}
	public Object put(Object key, Object value) {
		keys.add(key);
		values.add(value);
		return map.put(key, value);
	}
	public Object remove(Object key) {
		Object value = map.get(key);
		keys.remove(key);
		values.remove(value);
		return map.remove(key);
	}
	public void putAll(Map t) {
		if (!(t instanceof StructMap))
			throw new IllegalArgumentException("Cannot putAll members of anything other than a StructMap");
		StructMap that = (StructMap) t;
		for (int i = 0; i < that.keys.size(); ++i) {
			put(that.keys.get(i), that.values.get(i));
		}
	}
	public void clear() {
		keys.clear();
		values.clear();
		map.clear();
	}
	public Set keySet() {
		return map.keySet();
	}
	public Collection values() {
		return Collections.unmodifiableList(values);
	}
	public Set entrySet() {
		return map.entrySet();
	}
	public boolean equals(Object o) {
		return map.equals(o);
	}
	public int hashCode() {
		return map.hashCode() ^ keys.hashCode() ^ values.hashCode();
	}

	// new
	public Collection keys() {
		return Collections.unmodifiableList(keys);
	}
	public void set(int index, Object key, Object value) {
		keys.set(index, key);
		values.set(index, value);
		map.put(key, value);
	}
	public void set(int index, Object value) {
		Object key = keys.get(index);
		values.set(index, value);
		map.put(key, value);
	}
}