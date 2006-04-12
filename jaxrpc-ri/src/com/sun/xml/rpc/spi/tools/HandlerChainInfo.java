/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.xml.rpc.spi.tools;

import java.util.List;

/**
 * This class is implemented by 
 * com.sun.xml.rpc.processor.config.HandlerChainInfo
 */
public interface HandlerChainInfo {
    public void setHandlersList(List l);
    public void addRole(String s);
}
