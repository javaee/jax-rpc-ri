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

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.streaming.Attributes;
import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLReaderException;
import com.sun.xml.rpc.streaming.XMLReaderFactory;
import com.sun.xml.rpc.util.xml.XmlUtil;
import com.sun.xml.rpc.processor.config.HandlerChainInfo;
import com.sun.xml.rpc.processor.config.HandlerInfo;

/**
 *
 * @author JAX-RPC Development Team
 */
public class DeploymentDescriptorParser {
    
    public DeploymentDescriptorParser() {
    }
    
    public WebServicesInfo parse(InputStream is) {
        try {
            XMLReader reader =
                XMLReaderFactory.newInstance().createXMLReader(is);
            reader.next();
            return parseWebServices(reader);
        } catch (XMLReaderException e) {
            throw new DeploymentException("deployment.parser.xmlReader", e);
        }
    }
    
    protected WebServicesInfo parseWebServices(XMLReader reader) {
        if (!reader.getName().equals(Constants.QNAME_WEB_SERVICES)) {
            failWithFullName("deployment.parser.invalidElement", reader);
        }
        
        WebServicesInfo wsInfo = new WebServicesInfo();
        
        String version =
            getMandatoryNonEmptyAttribute(reader, Constants.ATTR_VERSION);
        if (!version.equals(Constants.ATTRVALUE_VERSION_1_0)) {
            failWithLocalName("deployment.parser.invalidVersionNumber",
                reader, version);
        }
        wsInfo.setTargetNamespaceBase(getNonEmptyAttribute(reader,
            Constants.ATTR_TARGET_NAMESPACE_BASE));
        wsInfo.setTypeNamespaceBase(getNonEmptyAttribute(reader,
            Constants.ATTR_TYPE_NAMESPACE_BASE));
        wsInfo.setUrlPatternBase(getNonEmptyAttribute(reader,
            Constants.ATTR_URL_PATTERN_BASE));
        
        boolean gotEndpointMapping = false;
        while(reader.nextElementContent() != XMLReader.END) {
            if (reader.getName().equals(Constants.QNAME_ENDPOINT)) {
                if (gotEndpointMapping) {
                    failWithLocalName("deployment.parser.invalidElement",
                        reader);
                } else {
                    wsInfo.add(parseEndpoint(reader));
                }
            } else if (reader.getName().equals(
                Constants.QNAME_ENDPOINT_MAPPING)) {
                    
                wsInfo.add(parseEndpointMapping(reader));
                gotEndpointMapping = true;
            } else if (reader.getName().equals(
                Constants.QNAME_ENDPOINT_CLIENT_MAPPING)) {
                    
                /* this support has been removed as there were no immediate means of
                 * generating ServiceImpl without generating the other artifacts.
                 */
                //System.out.println(Constants.QNAME_ENDPOINT_CLIENT_MAPPING );
                //wsInfo.add(parseEndpointClient(reader));
            } else {
                failWithLocalName("deployment.parser.invalidElement", reader);
            }
        }
        
        reader.close();
        
        // now do some validation
        Map endpoints = wsInfo.getEndpoints();
        Map endpointMappings = wsInfo.getEndpointMappings();
        Map endpointClients = wsInfo.getEndpointClients();
        
        boolean gotModels = true;
        boolean gotMappings = true;
        for (Iterator iter = endpoints.values().iterator(); iter.hasNext();) {
            EndpointInfo endpoint = (EndpointInfo) iter.next();
            if (endpoint.getModel() == null) {
                gotModels = false;
            }
            if (endpointMappings.get(endpoint.getName()) == null) {
                gotMappings = false;
            }
        }
        
        if (!gotModels) {
            if (wsInfo.getTargetNamespaceBase() == null) {
                throw new DeploymentException(
                    "deployment.parser.missing.attribute.no.line",
                    new Object[] {
                        Constants.QNAME_WEB_SERVICES.getLocalPart(),
                        Constants.ATTR_TARGET_NAMESPACE_BASE });
            }
            if (wsInfo.getTypeNamespaceBase() == null) {
                throw new DeploymentException(
                    "deployment.parser.missing.attribute.no.line",
                    new Object[] {
                        Constants.QNAME_WEB_SERVICES.getLocalPart(),
                        Constants.ATTR_TYPE_NAMESPACE_BASE });
            }
        }
        if (!gotMappings) {
            if (wsInfo.getUrlPatternBase() == null) {
                throw new DeploymentException(
                    "deployment.parser.missing.attribute.no.line",
                    new Object[] {
                        Constants.QNAME_WEB_SERVICES.getLocalPart(),
                        Constants.ATTR_URL_PATTERN_BASE });
            }
        }
        
        for (Iterator iter = endpointClients.values().iterator();
            iter.hasNext();) {
                
            EndpointClientInfo endpointClient =
                (EndpointClientInfo) iter.next();
            if (endpointClient.getModel() == null) {
                throw new DeploymentException(
                    "deployment.parser.missing.attribute.no.line",
                    new Object[] {
                        Constants.QNAME_ENDPOINT_CLIENT_MAPPING.getLocalPart(),
                        Constants.QNAME_ENDPOINT_CLIENT_MAPPING });
            }
            
            if (endpointClient.getService() == null) {
                throw new DeploymentException(
                    "deployment.parser.missing.attribute.no.line",
                    new Object[] { Constants.ATTR_SERVICE });
            }
        }
        return wsInfo;
    }
    
    protected EndpointClientInfo parseEndpointClient(XMLReader reader) {
        EndpointClientInfo client = new EndpointClientInfo();
        client.setName(getMandatoryNonEmptyAttribute(reader,
            Constants.ATTR_NAME));
        client.setDisplayName(getAttribute(reader,
            Constants.ATTR_DISPLAY_NAME));
        client.setDescription(getAttribute(reader, Constants.ATTR_DESCRIPTION));
        client.setModel(getAttribute(reader, Constants.ATTR_MODEL));
        client.setService(getAttribute(reader, Constants.ATTR_SERVICE));
        return client;
    }
    
    protected EndpointInfo parseEndpoint(XMLReader reader) {
        EndpointInfo endpoint = new EndpointInfo();
        
        endpoint.setName(getMandatoryNonEmptyAttribute(reader,
            Constants.ATTR_NAME));
        endpoint.setDisplayName(getAttribute(reader,
            Constants.ATTR_DISPLAY_NAME));
        endpoint.setDescription(getAttribute(reader,
            Constants.ATTR_DESCRIPTION));
        endpoint.setInterface(getAttribute(reader, Constants.ATTR_INTERFACE));
        endpoint.setImplementation(getAttribute(reader,
            Constants.ATTR_IMPLEMENTATION));
        endpoint.setModel(getAttribute(reader, Constants.ATTR_MODEL));
        
        /* the following 2 ifs are specific to wsdl -> java model.
         * where the portName and the location of the wsdl is
         * specified for the endpoint, for better clarity of
         * relationship between ports and end points and for
         * accessibility of wsdl @ the endpoint
         */
        if (getAttribute(reader, Constants.ATTR_PORT) != null) {
            String portValue = getAttribute(reader, Constants.ATTR_PORT);
            StringTokenizer str = new StringTokenizer(portValue, "}");
            QName portQName =
                new QName(new StringTokenizer(str.nextToken(),"{").nextToken(),
                str.nextToken());
            endpoint.setRuntimePortName(portQName);
        }
        
        if( getAttribute(reader, Constants.ATTR_WSDL) != null ) {
            String wsdlValue = getAttribute(reader, Constants.ATTR_WSDL);
            endpoint.setRuntimeWSDL(wsdlValue);
        }
        
        if (reader.nextElementContent() == XMLReader.START) {
            if (reader.getName().equals(Constants.QNAME_HANDLER_CHAINS)) {
                while(reader.nextElementContent() != XMLReader.END) {
                    if (reader.getName().equals(Constants.QNAME_CHAIN)) {
                        String runatAttr =
                            getMandatoryNonEmptyAttribute(reader,
                                Constants.ATTR_RUN_AT);
                        HandlerChainInfo handlerChainInfo =
                            new HandlerChainInfo();
                        
                        String rolesAttr =
                            getAttribute(reader, Constants.ATTR_ROLES);
                        if (rolesAttr != null) {
                            List rolesList = XmlUtil.parseTokenList(rolesAttr);
                            for (Iterator iter = rolesList.iterator();
                                iter.hasNext();) {
                                    
                                handlerChainInfo.addRole((String) iter.next());
                            }
                        }
                        
                        while (reader.nextElementContent() != XMLReader.END) {
                            if (reader.getName().equals(
                                Constants.QNAME_HANDLER)) {
                                
                                HandlerInfo handlerInfo = new HandlerInfo();
                                
                                String className =
                                    getMandatoryNonEmptyAttribute(reader,
                                        Constants.ATTR_CLASS_NAME);
                                handlerInfo.setHandlerClassName(className);
                                String headers = getAttribute(reader,
                                    Constants.ATTR_HEADERS);
                                if (headers != null) {
                                    List headersList =
                                        XmlUtil.parseTokenList(headers);
                                    for (Iterator iter = headersList.iterator();
                                        iter.hasNext();) {
                                            
                                        String name = (String) iter.next();
                                        String prefix = XmlUtil.getPrefix(name);
                                        String localPart =
                                            XmlUtil.getLocalPart(name);
                                        if (prefix == null) {
                                            
                                            // use the default namespace
                                            prefix = "";
                                        }
                                        String uri = reader.getURI(prefix);
                                        if (uri == null) {
                                            failWithLocalName(
                                                "configuration.invalidAttributeValue",
                                                reader, Constants.ATTR_HEADERS);
                                        }
                                        handlerInfo.addHeaderName(new QName(
                                            uri, localPart));
                                    }
                                }
                                
                                Map properties = handlerInfo.getProperties();
                                while (reader.nextElementContent() !=
                                    XMLReader.END) {
                                        
                                    if (reader.getName().equals(
                                        Constants.QNAME_PROPERTY)) {
                                            
                                        String name =
                                            getMandatoryNonEmptyAttribute(
                                                reader, Constants.ATTR_NAME);
                                        String value =
                                            getMandatoryAttribute(
                                                reader, Constants.ATTR_VALUE);
                                        properties.put(name, value);
                                        ensureNoContent(reader);
                                    } else {
                                        failWithLocalName(
                                            "configuration.invalidElement",
                                            reader);
                                    }
                                }
                                
                                handlerChainInfo.add(handlerInfo);
                            } else {
                                failWithLocalName(
                                    "configuration.invalidElement", reader);
                            }
                        }
                        
                        if (runatAttr.equals(Constants.ATTRVALUE_CLIENT)) {
                            endpoint.setClientHandlerChainInfo(
                                handlerChainInfo);
                        } else if (runatAttr.equals(
                            Constants.ATTRVALUE_SERVER)) {
                                
                            endpoint.setServerHandlerChainInfo(
                                handlerChainInfo);
                        } else {
                            failWithLocalName(
                                "configuration.invalidAttributeValue",
                                reader, Constants.ATTR_RUN_AT);
                        }
                    } else {
                        failWithLocalName("deployment.parser.invalidElement",
                            reader);
                    }
                }
                
                ensureNoContent(reader);
            } else {
                failWithLocalName("deployment.parser.invalidElement", reader);
            }
        }
        
        return endpoint;
    }
    
    protected EndpointMappingInfo parseEndpointMapping(XMLReader reader) {
        EndpointMappingInfo endpointMapping = new EndpointMappingInfo();
        
        endpointMapping.setName(getMandatoryNonEmptyAttribute(reader,
            Constants.ATTR_ENDPOINT_NAME));
        endpointMapping.setUrlPattern(getMandatoryNonEmptyAttribute(reader,
            Constants.ATTR_URL_PATTERN));
        
        ensureNoContent(reader);
        return endpointMapping;
    }
    
    protected String getAttribute(XMLReader reader, String name) {
        Attributes attributes = reader.getAttributes();
        String value = attributes.getValue(name);
        if (value != null) {
            value = value.trim();
        }
        return value;
    }
    
    protected String getNonEmptyAttribute(XMLReader reader, String name) {
        String value = getAttribute(reader, name);
        if (value != null && value.equals("")) {
            failWithLocalName("deployment.parser.invalidAttributeValue",
                reader, name);
        }
        return value;
    }
    
    protected String getMandatoryAttribute(XMLReader reader, String name) {
        String value = getAttribute(reader, name);
        if (value == null) {
            failWithLocalName("deployment.parser.missing.attribute",
                reader, name);
        }
        return value;
    }
    
    protected String getMandatoryNonEmptyAttribute(XMLReader reader,
        String name) {
            
        String value = getAttribute(reader, name);
        if (value == null) {
            failWithLocalName("deployment.parser.missing.attribute",
                reader, name);
        } else if (value.equals("")) {
            failWithLocalName("deployment.parser.invalidAttributeValue",
                reader, name);
        }
        return value;
    }
    
    protected static void ensureNoContent(XMLReader reader) {
        if (reader.nextElementContent() != XMLReader.END) {
            fail("deployment.parser.unexpectedContent", reader);
        }
    }
    
    protected static void fail(String key, XMLReader reader) {
        throw new DeploymentException(key,
            Integer.toString(reader.getLineNumber()));
    }
    
    protected static void failWithFullName(String key, XMLReader reader) {
        throw new DeploymentException(key,
            new Object[] {
                Integer.toString(reader.getLineNumber()),
                reader.getName().toString() });
    }
    
    protected static void failWithLocalName(String key, XMLReader reader) {
        throw new DeploymentException(key,
            new Object[] {
                Integer.toString(reader.getLineNumber()),
                reader.getLocalName() });
    }
    
    protected static void failWithLocalName(String key,
        XMLReader reader, String arg) {
            
        throw new DeploymentException(key,
            new Object[] {
                Integer.toString(reader.getLineNumber()),
                reader.getLocalName(),
                arg });
    }
    
}
