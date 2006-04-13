/*
 * $Id: ElementDecl.java,v 1.2 2006-04-13 01:32:30 ofung Exp $
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
