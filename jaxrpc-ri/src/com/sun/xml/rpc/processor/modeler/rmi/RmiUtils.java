/*
 * $Id: RmiUtils.java,v 1.3 2007-07-13 23:36:17 ofung Exp $
 */

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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

package com.sun.xml.rpc.processor.modeler.rmi;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author JAX-RPC Development Team
 */
public class RmiUtils implements RmiConstants {

    public static String getTypeSig(String typeName) {
        String sig = "";
        int idx;
        if ((idx = typeName.lastIndexOf(BRACKETS)) > 0) {
            return SIG_ARRAY + getTypeSig(typeName.substring(0, idx));
        }
        if (typeName.equals(BOOLEAN_CLASSNAME))
            return SIG_BOOLEAN;
        if (typeName.equals(BYTE_CLASSNAME))
            return SIG_BYTE;
        if (typeName.equals(CHAR_CLASSNAME))
            return SIG_CHAR;
        if (typeName.equals(SHORT_CLASSNAME))
            return SIG_SHORT;
        if (typeName.equals(INT_CLASSNAME))
            return SIG_INT;
        if (typeName.equals(LONG_CLASSNAME))
            return SIG_LONG;
        if (typeName.equals(FLOAT_CLASSNAME))
            return SIG_FLOAT;
        if (typeName.equals(DOUBLE_CLASSNAME))
            return SIG_DOUBLE;
        if (typeName.equals(VOID_CLASSNAME))
            return SIG_VOID;
        return SIG_CLASS + typeName.replace('.', SIGC_PACKAGE) + SIG_ENDCLASS;
    }

    public static String getRealName(String name, ClassLoader loader)
        throws ClassNotFoundException {
            
        String tmpName = name;
        if ((name.lastIndexOf(BRACKETS)) > 0)
            tmpName = getTypeSig(name).replace(SIGC_PACKAGE, '.');
        tmpName = getLoadableClassName(tmpName, loader);
        return tmpName;
    }

    public static Class getClassForName(String name, ClassLoader classLoader)
        throws ClassNotFoundException {
            
        Class tmpClass = (Class) typeClassMap.get(name);
        if (tmpClass != null)
            return tmpClass;
        String tmpName = name;
        if ((name.lastIndexOf(BRACKETS)) > 0)
            tmpName = getTypeSig(name).replace(SIGC_PACKAGE, '.');
        tmpName = getLoadableClassName(tmpName, classLoader);
        tmpClass = Class.forName(tmpName, true, classLoader);
        return tmpClass;
    }

    public static String getLoadableClassName(
        String className,
        ClassLoader classLoader)
        throws ClassNotFoundException {
            
        try {
            Class seiClass = Class.forName(className, true, classLoader);
        } catch (ClassNotFoundException e) {
            int idx = className.lastIndexOf(DOTC);
            if (idx > -1) {
                String tmp = className.substring(0, idx) + SIG_INNERCLASS;
                tmp += className.substring(idx + 1);
                return getLoadableClassName(tmp, classLoader);
            }
            throw e;
        }
        return className;
    }

    private static final Map typeClassMap = new HashMap();

    static {
        typeClassMap.put(BOOLEAN_CLASSNAME, boolean.class);
        typeClassMap.put(BYTE_CLASSNAME, byte.class);
        typeClassMap.put(CHAR_CLASSNAME, char.class);
        typeClassMap.put(DOUBLE_CLASSNAME, double.class);
        typeClassMap.put(FLOAT_CLASSNAME, float.class);
        typeClassMap.put(INT_CLASSNAME, int.class);
        typeClassMap.put(LONG_CLASSNAME, long.class);
        typeClassMap.put(SHORT_CLASSNAME, short.class);
        typeClassMap.put(VOID_CLASSNAME, void.class);
    }
}
