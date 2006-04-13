/*
 * $Id: Holders.java,v 1.2 2006-04-13 01:33:45 ofung Exp $
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

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import javax.xml.rpc.holders.Holder;

import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;

/**
* @author JAX-RPC Development Team
*/

public class Holders {
	private static final Map boxedTypes = new HashMap();

	static {
		boxedTypes.put(boolean.class, Boolean.class);
		boxedTypes.put(byte.class, Byte.class);
		boxedTypes.put(char.class, Character.class);
		boxedTypes.put(short.class, Short.class);
		boxedTypes.put(int.class, Integer.class);
		boxedTypes.put(long.class, Long.class);
		boxedTypes.put(float.class, Float.class);
		boxedTypes.put(double.class, Double.class);
	}

	public static Object getValue(Holder holder) {
		Class holderClass = holder.getClass();
		try {
			Field valueField = holderClass.getField("value");
			return valueField.get(holder);
		} catch (Exception e) {
			throw fieldExtractionException(e);
		}
	}

	public static void setValue(Holder holder, Object value) {
		Class holderClass = holder.getClass();
		try {
			Field valueField = holderClass.getField("value");
			valueField.set(holder, value);
		} catch (Exception e) {
			throw fieldExtractionException(e);
		}
	}

	public static Class stripHolderClass(Class aClass) {
		if (aClass == null || !Holder.class.isAssignableFrom(aClass)) {
			return aClass;
		}

		try {
			Field valueField = aClass.getField("value");
			Class valueClass = valueField.getType();

			return boxClassIfPrimitive(valueClass);
		} catch (Exception e) {
			throw fieldExtractionException(e);
		}
	}

	private static Class boxClassIfPrimitive(Class aClass) {
		Class boxedType = (Class) boxedTypes.get(aClass);

		if (boxedType != null) {
			return boxedType;
		} else {
			return aClass;
		}
	}

	private static HolderException fieldExtractionException(Exception e) {
		return new HolderException(
			"holder.valuefield.not.found",
			new LocalizableExceptionAdapter(e));
	}
}
