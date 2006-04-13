/*
 * $Id: AttributeDecl.java,v 1.2 2006-04-13 01:32:27 ofung Exp $
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
