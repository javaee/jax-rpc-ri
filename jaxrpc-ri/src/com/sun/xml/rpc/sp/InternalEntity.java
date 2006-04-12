/*
 * $Id: InternalEntity.java,v 1.1 2006-04-12 20:34:20 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.sp;

/**
 *
 * @author JAX-RPC RI Development Team
 */
class InternalEntity extends EntityDecl {
    InternalEntity(String name, char value[]) {
        this.name = name;
        this.buf = value;
    }

    char buf[];
}
