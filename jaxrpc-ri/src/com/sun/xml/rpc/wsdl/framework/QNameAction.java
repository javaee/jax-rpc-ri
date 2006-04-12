/*
 * $Id: QNameAction.java,v 1.1 2006-04-12 20:33:00 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.wsdl.framework;

import javax.xml.namespace.QName;

/**
 * An action operating on a QName.
 *
 * @author JAX-RPC Development Team
 */
public interface QNameAction {
    public void perform(QName name);
}
