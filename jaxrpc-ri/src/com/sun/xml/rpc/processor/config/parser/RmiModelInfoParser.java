/*
 * $Id: RmiModelInfoParser.java,v 1.2 2006-04-13 01:28:38 ofung Exp $
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

import com.sun.xml.rpc.processor.config.ConfigurationException;
import com.sun.xml.rpc.processor.config.ImportedDocumentInfo;
import com.sun.xml.rpc.processor.config.ModelInfo;
import com.sun.xml.rpc.processor.config.RmiInterfaceInfo;
import com.sun.xml.rpc.processor.config.RmiModelInfo;
import com.sun.xml.rpc.processor.config.TypeMappingRegistryInfo;
import com.sun.xml.rpc.processor.util.ProcessorEnvironment;
import com.sun.xml.rpc.soap.SOAPVersion;
import com.sun.xml.rpc.streaming.XMLReader;

/**
 *
 * @author JAX-RPC Development Team
 */
public class RmiModelInfoParser extends ModelInfoParser {
    
    public RmiModelInfoParser(ProcessorEnvironment env) {
        super(env);
    }
    
    public ModelInfo parse(XMLReader reader) {
        RmiModelInfo modelInfo = new RmiModelInfo();
        String name = ParserUtil.getMandatoryNonEmptyAttribute(reader,
            Constants.ATTR_NAME);
        modelInfo.setName(name);
        String targetNamespaceURI = ParserUtil.getMandatoryNonEmptyAttribute(
            reader, Constants.ATTR_TARGET_NAMESPACE);
        modelInfo.setTargetNamespaceURI(targetNamespaceURI);
        String typeNamespaceURI = ParserUtil.getMandatoryNonEmptyAttribute(
            reader, Constants.ATTR_TYPE_NAMESPACE);
        modelInfo.setTypeNamespaceURI(typeNamespaceURI);
        String packageName = ParserUtil.getMandatoryNonEmptyAttribute(
            reader, Constants.ATTR_PACKAGE_NAME);
        modelInfo.setJavaPackageName(packageName);
        
        boolean gotTypeMappingRegistry = false;
        boolean gotHandlerChains = false;
        boolean gotNamespaceMappingRegistry = false;
        while (reader.nextElementContent() != XMLReader.END) {
            if (reader.getName().equals(Constants.QNAME_INTERFACE)) {
                if (gotTypeMappingRegistry) {
                    ParserUtil.failWithLocalName("configuration.invalidElement",
                        reader);
                } else {
                    modelInfo.add(parseInterfaceInfo(reader));
                }
            } else if (reader.getName().equals(
                Constants.QNAME_TYPE_MAPPING_REGISTRY)) {
                    
                if (gotTypeMappingRegistry) {
                    ParserUtil.failWithLocalName("configuration.invalidElement",
                        reader);
                } else {
                    modelInfo.setTypeMappingRegistry(
                        parseTypeMappingRegistryInfo(reader));
                    gotTypeMappingRegistry = true;
                }
            } else if (reader.getName().equals(
                Constants.QNAME_HANDLER_CHAINS)) {
                    
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
        
        // do some additional validation
        TypeMappingRegistryInfo registryInfo =
            modelInfo.getTypeMappingRegistry();
        if (registryInfo != null) {
            String tns = modelInfo.getTargetNamespaceURI();
            if (tns != null) {
                ImportedDocumentInfo docInfo =
                    registryInfo.getImportedDocument(tns);
                if (docInfo != null) {
                    throw new ConfigurationException(
                        "configuration.invalidImport.targetNamespace", tns);
                }
            }
            String ttns = modelInfo.getTypeNamespaceURI();
            if (ttns != null) {
                ImportedDocumentInfo docInfo = registryInfo.getImportedDocument(ttns);
                if (docInfo != null) {
                    throw new ConfigurationException(
                        "configuration.invalidImport.targetTypeNamespace",
                        ttns);
                }
            }
        }
        return modelInfo;
    }
    
    private RmiInterfaceInfo parseInterfaceInfo(XMLReader reader) {
        RmiInterfaceInfo interfaceInfo = new RmiInterfaceInfo();
        String name = ParserUtil.getMandatoryNonEmptyAttribute(reader,
            Constants.ATTR_NAME);
        interfaceInfo.setName(name);
        String servantName = ParserUtil.getAttribute(reader,
            Constants.ATTR_SERVANT_NAME);
        interfaceInfo.setServantName(servantName);
        String soapAction = ParserUtil.getAttribute(reader,
            Constants.ATTR_SOAP_ACTION);
        interfaceInfo.setSOAPAction(soapAction);
        String soapActionBase = ParserUtil.getAttribute(reader,
            Constants.ATTR_SOAP_ACTION_BASE);
        interfaceInfo.setSOAPActionBase(soapActionBase);
        
        // TODO put this back in when doing SOAP 1.2
        //String soapVersion = ParserUtil.getAttribute(reader, Constants.ATTR_SOAP_VERSION);
        //if (soapVersion == null || soapVersion.equals(Constants.ATTRVALUE_SOAP_1_1)) {
        interfaceInfo.setSOAPVersion(SOAPVersion.SOAP_11);
        //}  else if (soapVersion.equals(Constants.ATTRVALUE_SOAP_1_2)) {
        //    interfaceInfo.setSOAPVersion(SOAPVersion.SOAP_12);
        //} else {
        //    throw new ConfigurationException("configuration.invalid.soap.version", soapVersion);
        //}
        
        boolean gotHandlerChains = false;
        while (reader.nextElementContent() != XMLReader.END) {
            if (reader.getName().equals(Constants.QNAME_HANDLER_CHAINS)) {
                if (gotHandlerChains) {
                    ParserUtil.failWithLocalName("configuration.invalidElement", 
                        reader);
                } else {
                    HandlerChainInfoData data =
                        parseHandlerChainInfoData(reader);
                    interfaceInfo.setClientHandlerChainInfo(
                        data.getClientHandlerChainInfo());
                    interfaceInfo.setServerHandlerChainInfo(
                        data.getServerHandlerChainInfo());
                    gotHandlerChains = true;
                }
            } else {
                ParserUtil.failWithLocalName("configuration.invalidElement",
                    reader);
            }
        }
        return interfaceInfo;
    }
}
