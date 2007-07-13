/*
 * $Id: JaxRpcObjectFactoryImpl.java,v 1.3 2007-07-13 23:36:38 ofung Exp $ 
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
package com.sun.xml.rpc.util;

import java.io.OutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import com.sun.xml.rpc.client.ClientTransportFactory;
import com.sun.xml.rpc.client.http.HttpClientTransportFactory;
import com.sun.xml.rpc.client.local.LocalClientTransportFactory;
import com.sun.xml.rpc.processor.config.Configuration;
import com.sun.xml.rpc.processor.config.HandlerInfo;
import com.sun.xml.rpc.processor.config.ModelFileModelInfo;
import com.sun.xml.rpc.processor.config.NamespaceMappingInfo;
import com.sun.xml.rpc.processor.config.NamespaceMappingRegistryInfo;
import com.sun.xml.rpc.processor.config.NoMetadataModelInfo;
import com.sun.xml.rpc.processor.config.parser.ModelInfoPlugin;
import com.sun.xml.rpc.processor.generator.Names;
import com.sun.xml.rpc.processor.util.XMLModelFileFilter;
import com.sun.xml.rpc.server.http.Implementor;
import com.sun.xml.rpc.server.http.ImplementorCache;
import com.sun.xml.rpc.server.http.JAXRPCServletDelegate;
import com.sun.xml.rpc.server.http.RuntimeEndpointInfo;
import com.sun.xml.rpc.soap.message.SOAPMessageContext;
import com.sun.xml.rpc.tools.plugin.ToolPluginConstants;
import com.sun.xml.rpc.tools.plugin.ToolPluginFactory;
import com.sun.xml.rpc.tools.wscompile.CompileTool;
import com.sun.xml.rpc.wsdl.parser.WSDLParser;
import com.sun.xml.rpc.wsdl.parser.WSDLUtil;

/**
 * Singleton factory class to instantiate concrete objects.
 *
 * @author JAX-RPC Development Team
 */
public class JaxRpcObjectFactoryImpl
    extends com.sun.xml.rpc.spi.JaxRpcObjectFactory {

    public JaxRpcObjectFactoryImpl() {
    }

    public com
        .sun
        .xml
        .rpc
        .spi
        .tools
        .ModelFileModelInfo createModelFileModelInfo() {
        return new ModelFileModelInfo();
    }

    public com.sun.xml.rpc.spi.tools.J2EEModelInfo createJ2EEModelInfo(
        java.net.URL mappingFile)
        throws Exception {

        ModelInfoPlugin plugin =
            (ModelInfoPlugin) ToolPluginFactory.getInstance().getPlugin(
                ToolPluginConstants.J2EE_PLUGIN);
        return (com.sun.xml.rpc.spi.tools.J2EEModelInfo) plugin.createModelInfo(
            mappingFile);
    }

    public com
        .sun
        .xml
        .rpc
        .spi
        .runtime
        .ClientTransportFactory createClientTransportFactory(
            int type,
            java.io.OutputStream outputStream) {
        ClientTransportFactory clientFactory = null;
        switch (type) {
            case com.sun.xml.rpc.spi.runtime.ClientTransportFactoryTypes.HTTP :
                return new HttpClientTransportFactory(outputStream);

            case com
                .sun
                .xml
                .rpc
                .spi
                .runtime
                .ClientTransportFactoryTypes
                .LOCAL :
                return new LocalClientTransportFactory(null, outputStream);
        }
        return clientFactory;
    }

    public com.sun.xml.rpc.spi.tools.CompileTool createCompileTool(
        OutputStream outputStream,
        String str) {
        return new CompileTool(outputStream, str);
    }

    public com.sun.xml.rpc.spi.runtime.Implementor createImplementor(
        ServletContext servletContext,
        com.sun.xml.rpc.spi.runtime.Tie tie) {
        return new Implementor(servletContext, tie);
    }

    public com.sun.xml.rpc.spi.runtime.ImplementorCache createImplementorCache(
        ServletConfig servletConfig) {
        return new ImplementorCache(servletConfig);
    }

    public com.sun.xml.rpc.spi.tools.Configuration createConfiguration(
        com.sun.xml.rpc.spi.tools.ProcessorEnvironment processorEnvironment) {
        return new Configuration(processorEnvironment);
    }

    public com.sun.xml.rpc.spi.tools.HandlerInfo createHandlerInfo() {
        return new HandlerInfo();
    }

    public com.sun.xml.rpc.spi.tools.Names createNames() {
        return new Names();
    }

    public com
        .sun
        .xml
        .rpc
        .spi
        .tools
        .NamespaceMappingInfo createNamespaceMappingInfo(
            String namespaceURI,
            String javaPackageName) {
        return new NamespaceMappingInfo(namespaceURI, javaPackageName);
    }

    public com
        .sun
        .xml
        .rpc
        .spi
        .tools
        .NamespaceMappingRegistryInfo createNamespaceMappingRegistryInfo() {
        return new NamespaceMappingRegistryInfo();
    }

    public com
        .sun
        .xml
        .rpc
        .spi
        .tools
        .NoMetadataModelInfo createNoMetadataModelInfo() {
        return new NoMetadataModelInfo();
    }

    public com
        .sun
        .xml
        .rpc
        .spi
        .runtime
        .RuntimeEndpointInfo createRuntimeEndpointInfo() {
        return new RuntimeEndpointInfo();
    }

    public com
        .sun
        .xml
        .rpc
        .spi
        .runtime
        .SOAPMessageContext createSOAPMessageContext() {
        return new SOAPMessageContext();
    }

    public com
        .sun
        .xml
        .rpc
        .spi
        .runtime
        .ServletDelegate createServletDelegate() {
        return new JAXRPCServletDelegate();
    }

    public com
        .sun
        .xml
        .rpc
        .spi
        .tools
        .XMLModelFileFilter createXMLModelFileFilter() {
        return new XMLModelFileFilter();
    }

    public com.sun.xml.rpc.spi.tools.WSDLUtil createWSDLUtil() {
        return new WSDLUtil();
    }

    public com.sun.xml.rpc.spi.tools.WSDLParser createWSDLParser() {
        return new WSDLParser();
    }
}
