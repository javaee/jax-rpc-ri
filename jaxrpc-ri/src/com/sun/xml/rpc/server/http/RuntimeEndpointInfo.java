/*
 * $Id: RuntimeEndpointInfo.java,v 1.1 2006-04-12 20:33:41 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.server.http;

import javax.xml.namespace.QName;

/**
 * @author JAX-RPC Development Team
 */
public class RuntimeEndpointInfo
    implements com.sun.xml.rpc.spi.runtime.RuntimeEndpointInfo {

    public RuntimeEndpointInfo() {
    }

    public Class getRemoteInterface() {
        return remoteInterface;
    }

    public void setRemoteInterface(Class klass) {
        remoteInterface = klass;
    }

    public Class getImplementationClass() {
        return implementationClass;
    }

    public void setImplementationClass(Class klass) {
        implementationClass = klass;
    }

    public Class getTieClass() {
        return tieClass;
    }

    public void setTieClass(Class klass) {
        tieClass = klass;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception e) {
        exception = e;
    }

    public String getName() {
        return name;
    }

    public void setName(String s) {
        name = s;
    }

    public String getModelFileName() {
        return modelFileName;
    }

    public void setModelFileName(String s) {
        modelFileName = s;
    }

    public String getWSDLFileName() {
        return wsdlFileName;
    }

    public void setWSDLFileName(String s) {
        wsdlFileName = s;
    }

    public boolean isDeployed() {
        return deployed;
    }

    public void setDeployed(boolean b) {
        deployed = b;
    }

    public QName getPortName() {
        return portName;
    }

    public void setPortName(QName n) {
        portName = n;
    }

    public QName getServiceName() {
        return serviceName;
    }

    public void setServiceName(QName n) {
        serviceName = n;
    }

    public String getUrlPattern() {
        return urlPattern;
    }

    public void setUrlPattern(String s) {
        urlPattern = s;
    }

    private Class remoteInterface;
    private Class implementationClass;
    private Class tieClass;
    private String name;
    private Exception exception;
    private QName portName;
    private QName serviceName;
    private String modelFileName;
    private String wsdlFileName;
    private boolean deployed;
    private String urlPattern;
}
