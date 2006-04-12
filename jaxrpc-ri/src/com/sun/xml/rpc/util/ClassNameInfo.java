/*
 * $Id: ClassNameInfo.java,v 1.1 2006-04-12 20:32:07 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.util;

/**
 * @author JAX-RPC Development Team
 */

public final class ClassNameInfo {

	public static String getName(String className) {
		String qual = getQualifier(className);
		return qual != null
			? className.substring(qual.length() + 1)
			: className;
	}

	public static String getQualifier(String className) {
		int idot = className.indexOf(' ');
		if (idot <= 0)
			idot = className.length();
		else
			idot -= 1; // back up over previous dot
		int index = className.lastIndexOf('.', idot - 1);
		return (index < 0) ? null : className.substring(0, index);
	}

	public static String replaceInnerClassSym(String name) {
		return name.replace('$', '_');
	}
}
