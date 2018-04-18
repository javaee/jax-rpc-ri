/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
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

/*
 * $Id: RmiModelInfoParser.java,v 1.3 2007-07-13 23:36:01 ofung Exp $
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
