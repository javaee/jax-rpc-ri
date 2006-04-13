/*
 * $Id: RmiModelInfo.java,v 1.2 2006-04-13 01:28:29 ofung Exp $
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
 
package com.sun.xml.rpc.processor.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import com.sun.xml.rpc.processor.modeler.Modeler;
import com.sun.xml.rpc.processor.modeler.rmi.RmiModeler;

/**
 *
 * @author JAX-RPC Development Team
 */
public class RmiModelInfo extends ModelInfo {

    public RmiModelInfo() {
        interfaces = new ArrayList();
    }

    public String getTargetNamespaceURI() {
        return targetNamespaceURI;
    }

    public void setTargetNamespaceURI(String s) {
        targetNamespaceURI = s;
    }

    public String getTypeNamespaceURI() {
        return typeNamespaceURI;
    }

    public void setTypeNamespaceURI(String s) {
        typeNamespaceURI = s;
    }

    public String getJavaPackageName() {
        return javaPackageName;
    }

    public void setJavaPackageName(String s) {
        javaPackageName = s;
    }

    public void add(RmiInterfaceInfo i) {
        interfaces.add(i);
        i.setParent(this); 
    }

    public Iterator getInterfaces() {
        return interfaces.iterator();
    }

    protected Modeler getModeler(Properties options) {
        return new RmiModeler(this, options);
    }

    private String targetNamespaceURI;
    private String typeNamespaceURI;
    private List interfaces;
    private String javaPackageName;
    private HandlerChainInfo clientHandlerChainInfo;
    private HandlerChainInfo serverHandlerChainInfo;
}
