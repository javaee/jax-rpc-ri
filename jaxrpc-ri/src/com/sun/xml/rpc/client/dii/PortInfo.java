/*
 * $Id: PortInfo.java,v 1.2 2006-04-13 01:26:49 ofung Exp $
 */

/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License).  You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the license at
 * https://glassfish.dev.java.net/public/CDDLv1.0.html.
 * See the License for the specific language governing
 * permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at https://glassfish.dev.java.net/public/CDDLv1.0.html.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * you own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
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
