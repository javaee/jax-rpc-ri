/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.xml.rpc.spi.runtime;

import java.rmi.Remote;

/**
 * This class is implemented by 
 * com.sun.xml.rpc.server.Tie
 */
public interface Tie extends Handler {
    public void setTarget(Remote target);
}
