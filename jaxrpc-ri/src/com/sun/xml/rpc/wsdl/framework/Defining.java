/*
 * $Id: Defining.java,v 1.1 2006-04-12 20:33:00 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.wsdl.framework;

/**
 * An interface implemented by entities that define target namespaces.
 *
 * @author JAX-RPC Development Team
 */
public interface Defining extends Elemental {
    public String getTargetNamespaceURI();
}
