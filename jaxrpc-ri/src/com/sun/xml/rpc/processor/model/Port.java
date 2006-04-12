/*
 * $Id: Port.java,v 1.1 2006-04-12 20:33:07 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.config.HandlerChainInfo;
import com.sun.xml.rpc.processor.model.java.JavaInterface;
import com.sun.xml.rpc.soap.SOAPVersion;


/**
 *
 * @author JAX-RPC Development Team
 */
public class Port extends ModelObject
    implements com.sun.xml.rpc.spi.model.Port {
    
    public Port() {}
    
    public Port(QName name) {
        _name = name;
    }
    
    public QName getName() {
        return _name;
    }
    
    public void setName(QName n) {
        _name = n;
    }
    
    public void addOperation(Operation operation) {
        _operations.add(operation);
        operationsByName.put(operation.getUniqueName(), operation);
    }
    
    public Iterator getOperations() {
        return _operations.iterator();
    }
    
    public Operation getOperationByUniqueName(String name) {
        if (operationsByName.size() != _operations.size()) {
            initializeOperationsByName();
        }
        return (Operation)operationsByName.get(name);
    }
    
    private void initializeOperationsByName() {
        operationsByName = new HashMap();
        if (_operations != null) {
            for (Iterator iter = _operations.iterator(); iter.hasNext();) {
                Operation operation = (Operation) iter.next();
                if (operation.getUniqueName() != null &&
                    operationsByName.containsKey(operation.getUniqueName())) {
                        
                    throw new ModelException("model.uniqueness");
                }
                operationsByName.put(operation.getUniqueName(), operation);
            }
        }
    }
    
    /* serialization */
    public List getOperationsList() {
        return _operations;
    }
    
    /* serialization */
    public void setOperationsList(List l) {
        _operations = l;
    }
    
    public JavaInterface getJavaInterface() {
        return _javaInterface;
    }
    
    public void setJavaInterface(JavaInterface i) {
        _javaInterface = i;
    }
    
    public String getAddress() {
        return _address;
    }
    
    public void setAddress(String s) {
        _address = s;
    }
    
    public HandlerChainInfo getClientHandlerChainInfo() {
        if (_clientHandlerChainInfo == null) {
            _clientHandlerChainInfo  = new HandlerChainInfo();
        }
        return _clientHandlerChainInfo;
    }
    
    public void setClientHandlerChainInfo(HandlerChainInfo i) {
        _clientHandlerChainInfo = i;
    }
    
    public com.sun.xml.rpc.spi.tools.HandlerChainInfo getServerHCI() {
        return getServerHandlerChainInfo();
    }
    
    public HandlerChainInfo getServerHandlerChainInfo() {
        if (_serverHandlerChainInfo == null) {
            _serverHandlerChainInfo  = new HandlerChainInfo();
        }
        return _serverHandlerChainInfo;
    }
    
    public void setServerHandlerChainInfo(HandlerChainInfo i) {
        _serverHandlerChainInfo = i;
    }
    
    public SOAPVersion getSOAPVersion() {
        return _soapVersion;
    }
    
    public void setSOAPVersion(SOAPVersion soapVersion) {
        _soapVersion = soapVersion;
    }
    
    public void accept(ModelVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    
    private QName _name;
    private List _operations = new ArrayList();
    private JavaInterface _javaInterface;
    private String _address;
    private Map operationsByName = new HashMap();
    private HandlerChainInfo _clientHandlerChainInfo;
    private HandlerChainInfo _serverHandlerChainInfo;
    private SOAPVersion _soapVersion = SOAPVersion.SOAP_11;
}
