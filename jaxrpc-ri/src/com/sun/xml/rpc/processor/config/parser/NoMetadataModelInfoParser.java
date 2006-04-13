/*
 * $Id: NoMetadataModelInfoParser.java,v 1.2 2006-04-13 01:28:36 ofung Exp $
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

package com.sun.xml.rpc.processor.config.parser;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.config.ModelInfo;
import com.sun.xml.rpc.processor.config.NoMetadataModelInfo;
import com.sun.xml.rpc.processor.util.ProcessorEnvironment;
import com.sun.xml.rpc.streaming.XMLReader;

/**
 *
 * @author JAX-RPC Development Team
 */
public class NoMetadataModelInfoParser extends ModelInfoParser {
    
    public NoMetadataModelInfoParser(ProcessorEnvironment env) {
        super(env);
    }
    
    public ModelInfo parse(XMLReader reader) {
        NoMetadataModelInfo modelInfo = new NoMetadataModelInfo();
        String location = ParserUtil.getMandatoryNonEmptyAttribute(reader,
            Constants.ATTR_LOCATION);
        modelInfo.setLocation(location);
        String interfaceName = ParserUtil.getMandatoryNonEmptyAttribute(reader,
            Constants.ATTR_INTERFACE_NAME);
        modelInfo.setInterfaceName(interfaceName);
        String servantName = ParserUtil.getNonEmptyAttribute(reader,
            Constants.ATTR_SERVANT_NAME);
        modelInfo.setServantName(servantName);
        String serviceInterfaceName = ParserUtil.getNonEmptyAttribute(reader,
            Constants.ATTR_SERVICE_INTERFACE_NAME);
        modelInfo.setServiceInterfaceName(serviceInterfaceName);
        QName serviceName = ParserUtil.getQNameAttribute(reader,
            Constants.ATTR_SERVICE_NAME);
        modelInfo.setServiceName(serviceName);
        QName portName = ParserUtil.getQNameAttribute(reader,
            Constants.ATTR_PORT_NAME);
        modelInfo.setPortName(portName);
        
        boolean gotHandlerChains = false;
        boolean gotNamespaceMappingRegistry = false;
        while (reader.nextElementContent() != XMLReader.END) {
            if (reader.getName().equals(Constants.QNAME_HANDLER_CHAINS)) {
                if (gotHandlerChains) {
                    ParserUtil.failWithLocalName("configuration.invalidElement",
                        reader);
                } else {
                    HandlerChainInfoData data =
                        parseHandlerChainInfoData(reader);
                    modelInfo.setClientHandlerChainInfo(
                        data.getClientHandlerChainInfo());
                    modelInfo.setServerHandlerChainInfo(
                        data.getServerHandlerChainInfo());
                    gotHandlerChains = true;
                }
            } else if (reader.getName().equals(
                Constants.QNAME_NAMESPACE_MAPPING_REGISTRY)) {
                    
                if (gotNamespaceMappingRegistry) {
                    ParserUtil.failWithLocalName("configuration.invalidElement",
                        reader);
                } else {
                    modelInfo.setNamespaceMappingRegistry(
                        parseNamespaceMappingRegistryInfo(reader));
                    gotNamespaceMappingRegistry = true;
                }
            } else {
                ParserUtil.failWithLocalName("configuration.invalidElement",
                    reader);
            }
        }
        return modelInfo;
    }
}
