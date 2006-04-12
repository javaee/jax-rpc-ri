/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.xml.rpc.spi.runtime;

import javax.xml.namespace.QName;

/**
 * This class is implemented by 
 * com.sun.xml.rpc.server.http.RuntimeEndpointInfo
 */
public interface RuntimeEndpointInfo {
    public void setRemoteInterface(Class klass);
    public void setImplementationClass(Class klass);
    public void setTieClass(Class klass);
    public void setName(String s);
    public void setDeployed(boolean b);
    public void setPortName(QName n);
    public void setServiceName(QName n);
    public void setUrlPattern(String s);
    public Class getTieClass();
    public Class getRemoteInterface();
    public Class getImplementationClass();
}
