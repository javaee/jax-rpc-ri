/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.xml.rpc.spi.model;

import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

/**
 * This class is implemented by 
 * com.sun.xml.rpc.processor.model.Service
 */
public interface Service extends ModelObject {
    public Iterator getPorts();
    public QName getName();
    public List getPortsList();

    /**
     * TODO: better way to derive the generated service * implementation class
     * PE uses service.getJavaInterface() + _Impl  Should we provide a method
     * for it?
     */
    public JavaInterface getJavaIntf();
}
