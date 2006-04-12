/*
 * $Id: CallEx.java,v 1.1 2006-04-12 20:33:58 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/**
 * @author JAX-RPC Development Team
 */
package com.sun.xml.rpc.client.dii;

import javax.xml.namespace.QName;
import javax.xml.rpc.Call;

public interface CallEx extends Call {
    public QName getPortName();

    public void setPortName(QName name);
}
