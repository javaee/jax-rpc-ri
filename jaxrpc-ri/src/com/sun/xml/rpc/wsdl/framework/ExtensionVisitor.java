/*
 * $Id: ExtensionVisitor.java,v 1.1 2006-04-12 20:32:57 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.wsdl.framework;

/**
 * A visitor working on extension entities.
 *
 * @author JAX-RPC Development Team
 */
public interface ExtensionVisitor {
    public void preVisit(Extension extension) throws Exception;
    public void postVisit(Extension extension) throws Exception;
}
