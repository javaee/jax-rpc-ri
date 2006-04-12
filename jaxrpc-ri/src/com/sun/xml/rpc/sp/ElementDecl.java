/*
 * $Id: ElementDecl.java,v 1.1 2006-04-12 20:34:23 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.sp;

/**
 * Represents all of the DTD information about an element.  That
 * includes:  <UL>
 *
 *	<LI> Element name
 *
 *	<LI> Content model ... either ANY, EMPTY, or a parenthesized
 *	regular expression matching the content model in the DTD
 *	(but with whitespace removed)
 *
 *	<LI> A hashtable mapping attribute names to the attribute
 *	metadata.
 *
 *	</UL>
 *
 * <P> This also records whether the element was declared in the
 * internal subset, for use in validating standalone declarations.
 *
 * @author David Brownell
 * @author JAX-RPC RI Development Team
 */
class ElementDecl {
    /** The element type name. */
    String name;

    /** The name of the element's ID attribute, if any */
    String id;

    // EMPTY
    // ANY
    // (#PCDATA) or (#PCDATA|name|...)
    // (name,(name|name|...)+,...) etc

    /** The compressed content model for the element */
    String contentType;

    // non-null only when validating; holds a data structure
    // representing (name,(name|name|...)+,...) style models
    ContentModel model;

    /** True for EMPTY and CHILDREN content models */
    boolean ignoreWhitespace;

    /** Used to validate standalone declarations */
    boolean isFromInternalSubset;

    SimpleHashtable attributes = new SimpleHashtable();

    ElementDecl(String s) {
        name = s;
    }
}
