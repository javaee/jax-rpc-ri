/*
 * $Id: ImplementorInfo.java,v 1.2 2006-04-13 01:32:13 ofung Exp $
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
