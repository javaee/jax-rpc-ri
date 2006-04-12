/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.xml.rpc.spi.tools;

import java.util.Map;

import javax.xml.namespace.QName;

/**
 * This class is implemented by 
 * com.sun.xml.rpc.processor.config.HandlerInfo
 */
public interface HandlerInfo {
    public void setHandlerClassName(String s);
    public Map getProperties();
    public void addHeaderName(QName name);
}
