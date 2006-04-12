/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.xml.rpc.spi.model;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.spi.tools.HandlerChainInfo;

/**
 * This class is implemented by 
 * com.sun.xml.rpc.processor.model.Port
 */
public interface Port extends ModelObject {
    public QName getName();
    public HandlerChainInfo getServerHCI();
}
