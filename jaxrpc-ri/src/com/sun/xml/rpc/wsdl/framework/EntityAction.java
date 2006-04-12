/*
 * $Id: EntityAction.java,v 1.1 2006-04-12 20:32:58 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.wsdl.framework;

/**
 * An action operating on an entity.
 *
 * @author JAX-RPC Development Team
 */
public interface EntityAction {
    public void perform(Entity entity);
}
