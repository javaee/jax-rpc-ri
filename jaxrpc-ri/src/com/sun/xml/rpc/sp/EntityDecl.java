/*
 * $Id: EntityDecl.java,v 1.1 2006-04-12 20:34:21 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.sp;

/**
 * Base class for entity declarations as used by the parser.
 *
 * @author David Brownell
 * @author JAX-RPC RI Development Team
 */
class EntityDecl {
    String name; // <!ENTITY name ... >

    boolean isFromInternalSubset;
    boolean isPE;
}
