/*
 * $Id: Service.java,v 1.2 2006-04-13 01:29:32 ofung Exp $
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

package com.sun.xml.rpc.processor.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.model.java.JavaInterface;

/**
 *
 * @author JAX-RPC Development Team
 */
public class Service extends ModelObject
    implements com.sun.xml.rpc.spi.model.Service {

    public Service() {}
    
    public Service(QName name, JavaInterface javaInterface) {
        this.name = name;
        this.javaInterface = javaInterface;
    }

    public QName getName() {
        return name;
    }
    
    public void setName(QName n) {
        name = n;
    }

    public void addPort(Port port) {
        if (portsByName.containsKey(port.getName())) {
            throw new ModelException("model.uniqueness");
        }
        ports.add(port);
        portsByName.put(port.getName(), port);
    }

    public Iterator getPorts() {
        return ports.iterator();
    }

    public Port getPortByName(QName n) {
        if (portsByName.size() != ports.size()) {
            initializePortsByName();
        }
        return (Port) portsByName.get(n);
    }

    /* serialization */
    public List getPortsList() {
        return ports;
    }
    
    /* serialization */
    public void setPortsList(List m) {
        ports = m;
//        initializePortsByName();
    }
    
    private void initializePortsByName() {
        portsByName = new HashMap();
        if (ports != null) {
            for (Iterator iter = ports.iterator(); iter.hasNext();) {
                Port port = (Port) iter.next();
                if (port.getName() != null &&
                    portsByName.containsKey(port.getName())) {
                        
                    throw new ModelException("model.uniqueness");
                }
                portsByName.put(port.getName(), port);
            }
        }
    }
    
    public com.sun.xml.rpc.spi.model.JavaInterface getJavaIntf() {
        return getJavaInterface();
    }
    
    public JavaInterface getJavaInterface() {
        return javaInterface;
    }
    
    public void setJavaInterface(JavaInterface i) {
        javaInterface = i;
    }

    public void accept(ModelVisitor visitor) throws Exception {
        visitor.visit(this);
    }

    private QName name;
    private List ports = new ArrayList();
    private Map portsByName = new HashMap();
    private JavaInterface javaInterface;
}
