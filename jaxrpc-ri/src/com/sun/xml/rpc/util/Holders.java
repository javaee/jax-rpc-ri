/*
 * $Id: Holders.java,v 1.1 2006-04-12 20:32:05 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
