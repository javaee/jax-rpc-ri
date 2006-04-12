/*
 * $Id: PortInfo.java,v 1.1 2006-04-12 20:33:57 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/**
 * @author JAX-RPC Development Team
 */
package com.sun.xml.rpc.client.dii;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;

public class PortInfo {
    Map operationMap;
    String targetEndpoint;
    String defaultNamespace;
    QName name;
    QName portTypeName;

    //stores port information from the examined
    //wsdl - stores a map of operations found in the
    //wsdl
    public PortInfo(QName name) {
        init();
        this.name = name;
    }

    protected void init() {
        operationMap = new HashMap();
        targetEndpoint = "";
        defaultNamespace = "";
    }

    public QName getName() {
        return name;
    }

    public OperationInfo createOperationForName(String operationName) {
        OperationInfo operation =
            (OperationInfo) operationMap.get(operationName);
        if (operation == null) {
            operation = new OperationInfo(operationName);
            operation.setNamespace(defaultNamespace);
            operationMap.put(operationName, operation);
        }
        return operation;
    }

    public void setPortTypeName(QName typeName) {
        portTypeName = typeName;
    }

    public QName getPortTypeName() {
        return portTypeName;
    }

    public void setDefaultNamespace(String namespace) {
        defaultNamespace = namespace;
    }

    public boolean isOperationKnown(String operationName) {
        return operationMap.get(operationName) != null;
    }

    public String getTargetEndpoint() {
        return targetEndpoint;
    }

    public void setTargetEndpoint(String target) {
        targetEndpoint = target;
    }

    public Iterator getOperations() {
        return operationMap.values().iterator();
    }

    public int getOperationCount() {
        return operationMap.values().size();
    }
}
