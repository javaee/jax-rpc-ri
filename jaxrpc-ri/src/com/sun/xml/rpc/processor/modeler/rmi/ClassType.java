/*
 * $Id: ClassType.java,v 1.1 2006-04-12 20:33:04 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.modeler.rmi;

import com.sun.xml.rpc.util.ClassNameInfo;

/**
 * @author JAX-RPC Development Team
 */
public final class ClassType extends RmiType {
    String className;

    ClassType(String typeSig, String className) {
        super(TC_CLASS, typeSig);
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public String typeString(boolean abbrev) {
        String tmp = className;
        if (abbrev)
            tmp = ClassNameInfo.getName(tmp);
        return tmp;
    }

    public boolean isNillable() {
        return true;
    }
}
