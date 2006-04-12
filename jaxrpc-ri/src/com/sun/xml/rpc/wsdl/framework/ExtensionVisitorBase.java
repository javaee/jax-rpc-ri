/*
 * $Id: ExtensionVisitorBase.java,v 1.1 2006-04-12 20:32:58 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.wsdl.framework;

/**
 * A base class for extension visitors.
 *
 * @author JAX-RPC Development Team
 */
public class ExtensionVisitorBase implements ExtensionVisitor {
    public ExtensionVisitorBase() {
    }

    public void preVisit(Extension extension) throws Exception {
    }
    public void postVisit(Extension extension) throws Exception {
    }
}
