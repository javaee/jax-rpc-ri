/*
 * $Id: HandlerChainInfoImpl.java,v 1.2 2006-04-13 01:26:33 ofung Exp $
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

package com.sun.xml.rpc.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.rpc.handler.HandlerInfo;

/**
 * @author JAX-RPC Development Team
 */
public class HandlerChainInfoImpl extends ArrayList {

    public HandlerChainInfoImpl() {
        this.handlers = new ArrayList();
        this.roles = null;
    }

    public HandlerChainInfoImpl(List list) {
        this.handlers = list;
        this.roles = null;
    }

    /* serialization */
    public List getHandlerList() {
        return handlers;
    }

    public Iterator getHandlers() {
        return handlers.iterator();
    }

    /* serialization */
    public void addHandler(HandlerInfo info) {
        handlers.add(info);
    }

    public String[] getRoles() {
        return roles;
    }

    /* serialization */
    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    private List handlers;
    private String[] roles;
}
