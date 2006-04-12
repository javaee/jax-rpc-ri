/*
 * $Id: AttributeDecl.java,v 1.1 2006-04-12 20:34:20 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.sp;

/**
 * Encapsulate an attribute declaration.
 *
 * @author David Brownell
 * @author JAX-RPC RI Development Team
 */
class AttributeDecl {
    String name;

    String type;
    String values[]; // for notation, enumeration only

    String defaultValue;
    boolean isRequired;
    boolean isFixed;
    boolean isFromInternalSubset;

    final static String CDATA = "CDATA";

    final static String ID = "ID";
    final static String IDREF = "IDREF";
    final static String IDREFS = "IDREFS";
    final static String ENTITY = "ENTITY";
    final static String ENTITIES = "ENTITIES";
    final static String NMTOKEN = "NMTOKEN";
    final static String NMTOKENS = "NMTOKENS";

    final static String NOTATION = "NOTATION";

    final static String ENUMERATION = "ENUMERATION";

    AttributeDecl(String s) {
        name = s;
    }
}
