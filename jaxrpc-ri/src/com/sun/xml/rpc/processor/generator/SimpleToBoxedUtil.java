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

package com.sun.xml.rpc.processor.generator;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author JAX-RPC Development Team
 */
public final class SimpleToBoxedUtil {

    public static String getBoxedExpressionOfType(String s, String c) {
        if (isPrimitive(c)) {
            StringBuffer sb = new StringBuffer();
            sb.append("new ");
            sb.append(getBoxedClassName(c));
            sb.append('(');
            sb.append(s);
            sb.append(')');
            return sb.toString();
        } else
            return s;
    }

    public static String getUnboxedExpressionOfType(String s, String c) {
        if (isPrimitive(c)) {
            StringBuffer sb = new StringBuffer();
            sb.append('(');
            sb.append(s);
            sb.append(").");
            sb.append(c);
            sb.append("Value()");
            return sb.toString();
        } else
            return s;
    }

    public static String convertExpressionFromTypeToType(
        String s,
        String from,
        String to)
        throws Exception {
        if (from.equals(to))
            return s;
        else {
            if (!isPrimitive(to) && isPrimitive(from))
                return getBoxedExpressionOfType(s, from);
            else if (isPrimitive(to) && isPrimitive(from))
                return getUnboxedExpressionOfType(s, to);
            else
                return s;
        }
    }

    public static String getBoxedClassName(String className) {
        if (isPrimitive(className)) {
            StringBuffer sb = new StringBuffer();
            if (className.equals(int.class.getName()))
                sb.append("java.lang.Integer");
            else if (className.equals(char.class.getName()))
                sb.append("java.lang.Character");
            else {
                sb.append(Character.toUpperCase(className.charAt(0)));
                sb.append(className.substring(1));
            }
            return sb.toString();
        } else
            return className;
    }

    public static boolean isPrimitive(String className) {
        return primitiveSet.contains(className);
    }

    static Set primitiveSet = null;

    static {
        primitiveSet = new HashSet();
        primitiveSet.add("boolean");
        primitiveSet.add("byte");
        primitiveSet.add("double");
        primitiveSet.add("float");
        primitiveSet.add("int");
        primitiveSet.add("long");
        primitiveSet.add("short");
    }
}
