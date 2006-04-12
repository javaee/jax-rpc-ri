/*
 * $Id: HandlerChainInfoImpl.java,v 1.1 2006-04-12 20:35:20 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
