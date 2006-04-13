/*
 * $Id: ArrayType.java,v 1.2 2006-04-13 01:31:10 ofung Exp $
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
