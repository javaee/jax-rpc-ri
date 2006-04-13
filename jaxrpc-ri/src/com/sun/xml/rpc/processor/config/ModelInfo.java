/*
 * $Id: ModelInfo.java,v 1.2 2006-04-13 01:28:25 ofung Exp $
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

import java.util.Properties;

import com.sun.xml.rpc.processor.model.Model;
import com.sun.xml.rpc.processor.modeler.Modeler;

/**
 *
 * @author JAX-RPC Development Team
 */
public abstract class ModelInfo implements com.sun.xml.rpc.spi.tools.ModelInfo {

    protected ModelInfo() {} 

    public Configuration getParent() {
        return _parent;
    }

    public void setParent(Configuration c) {
        _parent = c;
    }

    public String getName() {
        return _name;
    }

    public void setName(String s) {
        _name = s;
    }

    public Configuration getConfiguration() {
        return _parent;
    }

    public TypeMappingRegistryInfo getTypeMappingRegistry() {
        return _typeMappingRegistryInfo;
    }

    public void setTypeMappingRegistry(TypeMappingRegistryInfo i) {
        _typeMappingRegistryInfo = i;
    }

    public HandlerChainInfo getClientHandlerChainInfo() {
        return _clientHandlerChainInfo;
    }

    public void setClientHandlerChainInfo(HandlerChainInfo i) {
        _clientHandlerChainInfo = i;
    }

    public HandlerChainInfo getServerHandlerChainInfo() {
        return _serverHandlerChainInfo;
    }

    public void setServerHandlerChainInfo(HandlerChainInfo i) {
        _serverHandlerChainInfo = i;
    }

    public NamespaceMappingRegistryInfo getNamespaceMappingRegistry() {
        return _namespaceMappingRegistryInfo;
    }

    public void setNamespaceMappingRegistry(
        com.sun.xml.rpc.spi.tools.NamespaceMappingRegistryInfo i) {
            
        _namespaceMappingRegistryInfo = (NamespaceMappingRegistryInfo) i;
    }

    public String getJavaPackageName() {
        return _javaPackageName;
    }

    public void setJavaPackageName(String s) {
        _javaPackageName = s;
    }

    public Model buildModel(Properties options){
        return getModeler(options).buildModel();
    }

    protected abstract Modeler getModeler(Properties options);

    private Configuration _parent;
    private String _name;
    private String _javaPackageName;
    private TypeMappingRegistryInfo _typeMappingRegistryInfo;
    private HandlerChainInfo _clientHandlerChainInfo;
    private HandlerChainInfo _serverHandlerChainInfo;
    private NamespaceMappingRegistryInfo _namespaceMappingRegistryInfo;
}
