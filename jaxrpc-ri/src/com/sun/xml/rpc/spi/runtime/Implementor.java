/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.xml.rpc.spi.runtime;

import javax.xml.rpc.ServiceException;;

/**
 * This interface is implemented by 
 * com.sun.xml.rpc.server.http.Implementor
 */
public interface Implementor {
    public void destroy();
    public void init() throws ServiceException;;
    public Tie getTie();
}
