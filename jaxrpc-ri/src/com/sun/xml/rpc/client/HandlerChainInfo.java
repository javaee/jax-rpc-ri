/*
 * $Id: HandlerChainInfo.java,v 1.1 2006-04-12 20:35:20 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.client;

import java.util.Iterator;
import java.util.List;

import javax.xml.rpc.handler.HandlerInfo;

/**
 * @author JAX-RPC Development Team
 */
public interface HandlerChainInfo extends List {

    public Iterator getHandlers();

    public List getHandlerList();

    public void addHandler(HandlerInfo info);

    public String[] getRoles();

    public void setRoles(String[] roles);

}
