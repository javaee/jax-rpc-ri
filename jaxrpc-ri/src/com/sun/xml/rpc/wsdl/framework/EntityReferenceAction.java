/*
 * $Id: EntityReferenceAction.java,v 1.1 2006-04-12 20:32:57 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.wsdl.framework;

import javax.xml.namespace.QName;

/**
 * An action operating on an entity reference composed of a kind and a QName.
 *
 * @author JAX-RPC Development Team
 */
public interface EntityReferenceAction {
    public void perform(Kind kind, QName name);
}
