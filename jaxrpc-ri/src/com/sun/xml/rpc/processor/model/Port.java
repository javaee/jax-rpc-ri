/*
 * $Id: Port.java,v 1.3 2007-07-13 23:36:04 ofung Exp $
 */

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
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
