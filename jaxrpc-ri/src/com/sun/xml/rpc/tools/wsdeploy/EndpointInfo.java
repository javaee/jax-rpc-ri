/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
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

package com.sun.xml.rpc.tools.wsdeploy;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.config.HandlerChainInfo;

/**
 *
 * @author JAX-RPC Development Team
 */
public class EndpointInfo {
    
    public EndpointInfo() {
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String s) {
        name = s;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public void setDisplayName(String s) {
        displayName = s;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String s) {
        description = s;
    }
    
    public String getInterface() {
        return interfaceClass;
    }
    
    public void setInterface(String s) {
        interfaceClass = s;
    }
    
    public String getImplementation() {
        return implementationClass;
    }
    
    public void setImplementation(String s) {
        implementationClass = s;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String s) {
        model = s;
    }
    
    public HandlerChainInfo getClientHandlerChainInfo() {
        return clientHandlerChainInfo;
    }
    
    public void setClientHandlerChainInfo(HandlerChainInfo i) {
        clientHandlerChainInfo = i;
    }
    
    public HandlerChainInfo getServerHandlerChainInfo() {
        return serverHandlerChainInfo;
    }
    
    public void setServerHandlerChainInfo(HandlerChainInfo i) {
        serverHandlerChainInfo = i;
    }
    
    public boolean isRuntimeDeployed() {
        return runtimeDeployed;
    }
    
    public void setRuntimeDeployed(boolean b) {
        runtimeDeployed = b;
    }
    
    public String getRuntimeModel() {
        return runtimeModel;
    }
    
    public void setRuntimeModel(String s) {
        runtimeModel = s;
    }
    
    public String getRuntimeWSDL() {
        return runtimeWSDL;
    }
    
    public void setRuntimeWSDL(String s) {
        runtimeWSDL = s;
    }
    
    public String getRuntimeTie() {
        return runtimeTie;
    }
    
    public void setRuntimeTie(String s) {
        runtimeTie = s;
    }
    
    public QName getRuntimeServiceName() {
        return runtimeServiceName;
    }
    
    public void setRuntimeServiceName(QName n) {
        runtimeServiceName = n;
    }
    
    public QName getRuntimePortName() {
        return runtimePortName;
    }
    
    public void setRuntimePortName(QName n) {
        runtimePortName = n;
    }
    
    public String getRuntimeUrlPattern() {
        return runtimeUrlPattern;
    }
    
    public void setRuntimeUrlPattern(String s) {
        runtimeUrlPattern = s;
    }
    
    private String name;
    private String displayName;
    private String description;
    private String interfaceClass;
    private String implementationClass;
    private String model;
    private HandlerChainInfo clientHandlerChainInfo;
    private HandlerChainInfo serverHandlerChainInfo;
    
    // runtime information - apologies for the funny names
    private boolean runtimeDeployed;
    private String runtimeModel;
    private String runtimeWSDL;
    private String runtimeTie;
    private QName runtimeServiceName;
    private QName runtimePortName;
    private String runtimeUrlPattern;
}
