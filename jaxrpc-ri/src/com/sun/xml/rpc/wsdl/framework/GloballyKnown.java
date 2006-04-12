/*
 * $Id: GloballyKnown.java,v 1.1 2006-04-12 20:32:59 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.wsdl.framework;

/**
 * An interface implemented by entities which can be defined in a target namespace.
 *
 * @author JAX-RPC Development Team
 */
public interface GloballyKnown extends Elemental {
    public String getName();
    public Kind getKind();
    public Defining getDefining();
}
