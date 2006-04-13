/*
 * $Id: AttributesEx.java,v 1.2 2006-04-13 01:32:28 ofung Exp $
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

import org.xml.sax.Attributes;

/**
 * This interface extends the SAX Attributes interface to expose
 * information needed to support DOM Level 1 features used in document
 * editing, and detection of ID attributes which are declared for
 * an element.
 *
 * @author David Brownell
 * @author JAX-RPC RI Development Team
 */
public interface AttributesEx extends Attributes {
    /**
     * Returns true if the attribute was specified in the document.
     * <em> This method only relates to document editing; there is no
     * difference in semantics between explicitly specifying values
     * of attributes in a DTD vs another part of the document. </em>
     *
     * @param i the index of the attribute in the list.
     */
    public boolean isSpecified(int i);

    /**
     * Returns the default value of the specified attribute, or null
     * if no default value is known.  Default values may be explicitly
     * specified in documents; in fact, for standalone documents, they
     * must be so specified.  If <em>isSpecified</em> is false, the
     * value returned by this method will be what <em>getValue</em>
     * returns.
     *
     * @param i the index of the attribute in the list.
     */
    public String getDefault(int i);

    /**
     * Returns the name of the ID attribute for the associated element,
     * if one was declared.  If such an ID value was provided, this
     * name can be inferred from methods in the base class; but if none
     * was provided, this will be the only way this name can be determined.
     */
    public String getIdAttributeName();
}
