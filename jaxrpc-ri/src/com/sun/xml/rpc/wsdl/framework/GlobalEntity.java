/*
 * $Id: GlobalEntity.java,v 1.1 2006-04-12 20:33:01 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.wsdl.framework;

/**
 * An entity that can be defined in a target namespace.
 *
 * @author JAX-RPC Development Team
 */
public abstract class GlobalEntity extends Entity implements GloballyKnown {

    public GlobalEntity(Defining defining) {
        _defining = defining;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public abstract Kind getKind();

    public Defining getDefining() {
        return _defining;
    }

    private Defining _defining;
    private String _name;
}
