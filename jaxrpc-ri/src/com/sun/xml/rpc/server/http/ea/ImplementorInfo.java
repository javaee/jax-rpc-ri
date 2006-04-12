/*
 * $Id: ImplementorInfo.java,v 1.1 2006-04-12 20:35:27 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.server.http.ea;

import java.rmi.Remote;

import javax.servlet.ServletContext;

import com.sun.xml.rpc.server.Tie;
import com.sun.xml.rpc.server.http.Implementor;

/**
 * ImplementorInfo contains basic information about the implementor of a port.
 *
 * @author JAX-RPC Development Team
 */
public class ImplementorInfo {

    public ImplementorInfo(Class tieClass, Class servantClass) {
        _tieClass = tieClass;
        _servantClass = servantClass;
    }

    public Class getTieClass() {
        return _tieClass;
    }
    public Class getServantClass() {
        return _servantClass;
    }

    public Implementor createImplementor(ServletContext context)
        throws IllegalAccessException, InstantiationException {
        Tie tie = (Tie) _tieClass.newInstance();
        Remote servant = (Remote) _servantClass.newInstance();
        tie.setTarget(servant);
        return new Implementor(context, tie);
    }

    private Class _tieClass;
    private Class _servantClass;
}
