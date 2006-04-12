/*
 * $Id: ArrayType.java,v 1.1 2006-04-12 20:33:05 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.modeler.rmi;

/**
 * @author JAX-RPC Development Team
 */
public final class ArrayType extends RmiType {
    RmiType elemType;

    ArrayType(String typeSig, RmiType elemType) {
        super(TC_ARRAY, typeSig);
        this.elemType = elemType;
    }

    public RmiType getElementType() {
        return elemType;
    }

    public int getArrayDimension() {
        return elemType.getArrayDimension() + 1;
    }

    public String typeString(boolean abbrev) {
        String tmp = getElementType().typeString(abbrev) + BRACKETS;
        return tmp;
    }

    public boolean isNillable() {
        return true;
    }
}
