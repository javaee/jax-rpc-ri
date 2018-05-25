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

package com.sun.xml.rpc.processor.modeler.nometadata;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import javax.xml.namespace.QName;

import org.xml.sax.InputSource;

import com.sun.xml.rpc.processor.ProcessorOptions;

import com.sun.xml.rpc.processor.config.NamespaceMappingInfo;
import com.sun.xml.rpc.processor.config.NamespaceMappingRegistryInfo;
import com.sun.xml.rpc.processor.config.NoMetadataModelInfo;

import com.sun.xml.rpc.processor.model.Block;
import com.sun.xml.rpc.processor.model.Fault;
import com.sun.xml.rpc.processor.model.Model;
import com.sun.xml.rpc.processor.model.ModelException;
import com.sun.xml.rpc.processor.model.ModelObject;
import com.sun.xml.rpc.processor.model.ModelProperties;
import com.sun.xml.rpc.processor.model.Operation;
import com.sun.xml.rpc.processor.model.Parameter;
import com.sun.xml.rpc.processor.model.Port;
import com.sun.xml.rpc.processor.model.Request;
import com.sun.xml.rpc.processor.model.Response;
import com.sun.xml.rpc.processor.model.Service;

import com.sun.xml.rpc.processor.model.java.JavaArrayType;
import com.sun.xml.rpc.processor.model.java.JavaException;
import com.sun.xml.rpc.processor.model.java.JavaInterface;
import com.sun.xml.rpc.processor.model.java.JavaMethod;
import com.sun.xml.rpc.processor.model.java.JavaParameter;
import com.sun.xml.rpc.processor.model.java.JavaStructureMember;
import com.sun.xml.rpc.processor.model.java.JavaStructureType;
import com.sun.xml.rpc.processor.model.java.JavaType;

import com.sun.xml.rpc.processor.model.soap.RPCRequestUnorderedStructureType;
import com.sun.xml.rpc.processor.model.soap.RPCResponseStructureType;
import com.sun.xml.rpc.processor.model.soap.SOAPOrderedStructureType;
import com.sun.xml.rpc.processor.model.soap.SOAPStructureMember;
import com.sun.xml.rpc.processor.model.soap.SOAPStructureType;
import com.sun.xml.rpc.processor.model.soap.SOAPType;

import com.sun.xml.rpc.processor.modeler.JavaSimpleTypeCreator;
import com.sun.xml.rpc.processor.modeler.Modeler;
import com.sun.xml.rpc.processor.modeler.ModelerException;
import com.sun.xml.rpc.processor.modeler.rmi.RmiUtils;
import com.sun.xml.rpc.processor.modeler.wsdl.SchemaAnalyzer;

import com.sun.xml.rpc.processor.util.ClassNameCollector;
import com.sun.xml.rpc.processor.util.ProcessorEnvironment;
import com.sun.xml.rpc.processor.util.StringUtils;

import com.sun.xml.rpc.util.localization.LocalizableMessageFactory;
import com.sun.xml.rpc.util.xml.XmlUtil;

import com.sun.xml.rpc.wsdl.document.Binding;
import com.sun.xml.rpc.wsdl.document.BindingOperation;
import com.sun.xml.rpc.wsdl.document.BindingFault;
import com.sun.xml.rpc.wsdl.document.Documentation;
import com.sun.xml.rpc.wsdl.document.MessagePart;
import com.sun.xml.rpc.wsdl.document.OperationStyle;
import com.sun.xml.rpc.wsdl.document.PortType;
import com.sun.xml.rpc.wsdl.document.WSDLConstants;
import com.sun.xml.rpc.wsdl.document.WSDLDocument;

import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;
import com.sun.xml.rpc.wsdl.document.schema.SchemaKinds;

import com.sun.xml.rpc.wsdl.document.soap.SOAPAddress;
import com.sun.xml.rpc.wsdl.document.soap.SOAPBinding;
import com.sun.xml.rpc.wsdl.document.soap.SOAPBody;
import com.sun.xml.rpc.wsdl.document.soap.SOAPConstants;
import com.sun.xml.rpc.wsdl.document.soap.SOAPFault;
import com.sun.xml.rpc.wsdl.document.soap.SOAPHeader;
import com.sun.xml.rpc.wsdl.document.soap.SOAPOperation;
import com.sun.xml.rpc.wsdl.document.soap.SOAPStyle;

import com.sun.xml.rpc.wsdl.framework.Entity;
import com.sun.xml.rpc.wsdl.framework.Extensible;
import com.sun.xml.rpc.wsdl.framework.Extension;
import com.sun.xml.rpc.wsdl.framework.GloballyKnown;
import com.sun.xml.rpc.wsdl.framework.NoSuchEntityException;
import com.sun.xml.rpc.wsdl.framework.ParseException;
import com.sun.xml.rpc.wsdl.framework.ParserListener;
import com.sun.xml.rpc.wsdl.framework.ValidationException;

import com.sun.xml.rpc.wsdl.parser.WSDLParser;
import com.sun.xml.rpc.wsdl.parser.SOAPEntityReferenceValidator;

/**
 *
 * @author JAX-RPC Development Team
 */
public class NoMetadataModeler implements Modeler {
    
    public NoMetadataModeler(NoMetadataModelInfo modelInfo,
        Properties options) {
            
        this.modelInfo = modelInfo;
        env = (ProcessorEnvironment)
            modelInfo.getConfiguration().getEnvironment();
        this.options = options;
        namespaceMappingRegistry = modelInfo.getNamespaceMappingRegistry();
        messageFactory =
            new LocalizableMessageFactory("com.sun.xml.rpc.resources.modeler");
    }
    
    public ProcessorEnvironment getProcessorEnvironment() {
        return env;
    }
    
    public NamespaceMappingRegistryInfo getNamespaceMappingRegistryInfo() {
        return namespaceMappingRegistry;
    }
    
    public Model getModel() {
        return model;
    }
    
    public Model buildModel() {
        try {
            WSDLParser parser = new WSDLParser();
            InputSource inputSource = new InputSource(modelInfo.getLocation());
            parser.addParserListener(new ParserListener() {
                public void ignoringExtension(QName name, QName parent) {
                    if (parent.equals(WSDLConstants.QNAME_TYPES)) {
                        
                        // check for schema element with wrong namespace URI
                        if (name.getLocalPart().equals("schema") &&
                            !name.getNamespaceURI().equals("")) {
                                
                            warn("wsdlmodeler.warning.ignoringUnrecognizedSchemaExtension",
                                name.getNamespaceURI());
                        }
                    }
                }
                public void doneParsingEntity(QName element, Entity entity) {}
            });
            
            // Added useWSIBasicProfile parameter to WSDLParser.parse() that it
            // can generate warnings when f:wsi is set to true
            // This is done to validate WSDL for wsi compliant
            boolean useWSIBasicProfile = Boolean.valueOf(options.getProperty(
                ProcessorOptions.USE_WSI_BASIC_PROFILE)).booleanValue();
            WSDLDocument document = parser.parse(inputSource,
                useWSIBasicProfile);
            document.validateLocally();
            
            boolean validateWSDL =
                Boolean.valueOf(options.getProperty(
                    ProcessorOptions.VALIDATE_WSDL_PROPERTY)).booleanValue();
            if (validateWSDL) {
                document.validate(new SOAPEntityReferenceValidator());
            }
            
            Model model = internalBuildModel(document);
            ClassNameCollector collector = new ClassNameCollector();
            collector.process(model);
            if (collector.getConflictingClassNames().isEmpty()) {
                return model;
            } else {
                
                // give up
                StringBuffer conflictList = new StringBuffer();
                boolean first = true;
                for (Iterator iter =
                    collector.getConflictingClassNames().iterator();
                    iter.hasNext();) {
                        
                    if (!first) {
                        conflictList.append(", ");
                    } else {
                        first = false;
                    }
                    conflictList.append((String) iter.next());
                }
                throw new ModelerException(
                    "wsdlmodeler.unsolvableNamingConflicts",
                    conflictList.toString());
            }
        } catch (ModelException e) {
            throw new ModelerException(e);
        } catch (ParseException e) {
            throw new ModelerException(e);
        } catch (ValidationException e) {
            throw new ModelerException(e);
        } finally {
            analyzer = null;
        }
    }
    
    private Model internalBuildModel(WSDLDocument document) {
        QName modelName = new QName(
            document.getDefinitions().getTargetNamespaceURI(),
            document.getDefinitions().getName() == null ? "model" :
                document.getDefinitions().getName());
        Model model = new Model(modelName);
        model.setProperty(ModelProperties.PROPERTY_MODELER_NAME,
            this.getClass().getName());
        javaTypes = new JavaSimpleTypeCreator();
        javaExceptions = new HashMap();
        analyzer = new SchemaAnalyzer(document, modelInfo, options,
            new HashSet(), javaTypes);
        
        // grab target namespace
        model.setTargetNamespaceURI(
            document.getDefinitions().getTargetNamespaceURI());
        
        setDocumentationIfPresent(model,
            document.getDefinitions().getDocumentation());
        
        boolean hasServices = document.getDefinitions().services().hasNext();
        if (hasServices) {
            boolean gotOne = false;
            for (Iterator iter =
                document.getDefinitions().services(); iter.hasNext();) {
                    
                com.sun.xml.rpc.wsdl.document.Service service =
                    (com.sun.xml.rpc.wsdl.document.Service) iter.next();
                if (gotOne) {
                    throw new ModelerException(
                        "nometadatamodeler.error.moreThanOneServiceDefinition");
                }
                processService(service, model, document);
                gotOne = true;
            }
        } else {
            // emit an error if there are no service definitions
            throw new ModelerException(
                "nometadatamodeler.error.noServiceDefinitionsFound");
        }
        return model;
    }
    
    protected void processService(
        com.sun.xml.rpc.wsdl.document.Service wsdlService,
        Model model, WSDLDocument document) {
            
        QName serviceQName = getQNameOf(wsdlService);
        if (modelInfo.getServiceName() != null &&
            !modelInfo.getServiceName().equals(serviceQName)) {
                
            throw new ModelerException(
                "nometadatamodeler.error.incorrectServiceName", 
                serviceQName.toString());
        }
        
        String serviceInterface = null;
        if (modelInfo.getServiceInterfaceName() != null) {
            serviceInterface = modelInfo.getServiceInterfaceName();
        } else {
            serviceInterface = "";
            String javaPackageName = getJavaPackageName(serviceQName);
            if (javaPackageName == null &&
                modelInfo.getJavaPackageName() != null &&
                !modelInfo.getJavaPackageName().equals("")) {
                    
                javaPackageName = modelInfo.getJavaPackageName();
            }
            if (javaPackageName == null) {
                throw new ModelerException(
                    "nometadatamodeler.error.cannotMapNamespace",
                    serviceQName.getNamespaceURI());
            }
            serviceInterface = javaPackageName + ".";
            serviceInterface += env.getNames().validJavaClassName(wsdlService.getName());
        }
        
        Service service = new Service(serviceQName,
            new JavaInterface(serviceInterface, serviceInterface+"Impl"));
        
        setDocumentationIfPresent(service, wsdlService.getDocumentation());
        
        boolean gotOne = false;
        for (Iterator iter = wsdlService.ports(); iter.hasNext();) {
            if (gotOne) {
                throw new ModelerException(
                    "nometadatamodeler.error.moreThanOnePortDefinition",
                    wsdlService.getName());
            }
            com.sun.xml.rpc.wsdl.document.Port port =
                (com.sun.xml.rpc.wsdl.document.Port) iter.next();
            boolean processed = processPort(port, service, document);
            if (!processed) {
                throw new ModelerException(
                    "nometadatamodeler.error.failedToProcessPort",
                    port.getName());
            }
            gotOne = true;
        }
        
        if (!gotOne) {
            throw new ModelerException(
                "nometadatamodeler.error.noPortsInService",
                wsdlService.getName());
        }
        model.addService(service);
        verifyServiceInterface(service);
    }
    
    protected boolean processPort(com.sun.xml.rpc.wsdl.document.Port wsdlPort,
        Service service, WSDLDocument document) {
            
        try {
            QName portQName = getQNameOf(wsdlPort);
            
            if (modelInfo.getPortName() != null &&
                !modelInfo.getPortName().equals(portQName)) {
                    
                throw new ModelerException("nometadatamodeler.error.incorrectPortName", portQName.toString());
            }
            
            Port port = new Port(portQName);
            setDocumentationIfPresent(port, wsdlPort.getDocumentation());
            
            SOAPAddress soapAddress = 
                (SOAPAddress) getExtensionOfType(wsdlPort, SOAPAddress.class);
            if (soapAddress == null) {
                warn("wsdlmodeler.warning.ignoringNonSOAPPort.noAddress",
                    wsdlPort.getName());
                return false;
            }
            
            port.setAddress(soapAddress.getLocation());
            Binding binding = wsdlPort.resolveBinding(document);
            PortType portType = binding.resolvePortType(document);
            
            // find out the SOAP binding extension, if any
            SOAPBinding soapBinding =
                (SOAPBinding) getExtensionOfType(binding, SOAPBinding.class);
            
            if (soapBinding == null) {
                warn("wsdlmodeler.warning.ignoringNonSOAPPort",
                    wsdlPort.getName());
                return false;
            }
            
            if (soapBinding.getTransport() == null ||
                !soapBinding.getTransport().equals(
                    SOAPConstants.URI_SOAP_TRANSPORT_HTTP)) {
                        
                warn("wsdlmodeler.warning.ignoringSOAPBinding.nonHTTPTransport",
                    wsdlPort.getName());
                return false;
            }
            
            boolean hasOverloadedOperations = false;
            Set operationNames = new HashSet();
            for (Iterator iter = portType.operations(); iter.hasNext();) {
                com.sun.xml.rpc.wsdl.document.Operation operation =
                    (com.sun.xml.rpc.wsdl.document.Operation) iter.next();
                if (operationNames.contains(operation.getName())) {
                    hasOverloadedOperations = true;
                    break;
                }
                operationNames.add(operation.getName());
            }
            
            if (hasOverloadedOperations) {
                throw new ModelerException(
                    "nometadatamodeler.error.overloadedOperationsFound",
                    wsdlPort.getName());
            }
            
            port.setProperty(ModelProperties.PROPERTY_WSDL_PORT_NAME,
                getQNameOf(wsdlPort));
            port.setProperty(ModelProperties.PROPERTY_WSDL_PORT_TYPE_NAME,
                getQNameOf(portType));
            port.setProperty(ModelProperties.PROPERTY_WSDL_BINDING_NAME,
                getQNameOf(binding));
            
            boolean hasOperations = false;
            for (Iterator iter = binding.operations(); iter.hasNext();) {
                BindingOperation bindingOperation =
                    (BindingOperation) iter.next();
                com.sun.xml.rpc.wsdl.document.Operation portTypeOperation =
                    null;
                Set operations =
                    portType.getOperationsNamed(bindingOperation.getName());
                if (operations.size() == 0) {
                    
                    // the WSDL document is invalid
                    throw new ModelerException(
                        "wsdlmodeler.invalid.bindingOperation.notInPortType",
                        new Object[] { bindingOperation.getName(),
                            binding.getName() });
                } else if (operations.size() == 1) {
                    portTypeOperation =
                        (com.sun.xml.rpc.wsdl.document.Operation)
                            operations.iterator().next();
                } else {
                    boolean found = false;
                    String expectedInputName =
                        bindingOperation.getInput().getName();
                    String expectedOutputName =
                        bindingOperation.getOutput().getName();
                    
                    for (Iterator iter2 = operations.iterator();
                        iter2.hasNext();) {
                            
                        com.sun.xml.rpc.wsdl.document.Operation
                            candidateOperation =
                                (com.sun.xml.rpc.wsdl.document.Operation)
                                    iter2.next();
                        
                        if (expectedInputName == null) {
                            
                            // the WSDL document is invalid
                            throw new ModelerException(
                                "wsdlmodeler.invalid.bindingOperation.missingInputName",
                                new Object[] { bindingOperation.getName(),
                                    binding.getName() });
                        }
                        if (expectedOutputName == null) {
                            
                            // the WSDL document is invalid
                            throw new ModelerException(
                                "wsdlmodeler.invalid.bindingOperation.missingOutputName",
                                new Object[] { bindingOperation.getName(),
                                    binding.getName() });
                        }
                        if (expectedInputName.equals(
                            candidateOperation.getInput().getName()) &&
                            expectedOutputName.equals(
                                candidateOperation.getOutput().getName())) {
                                    
                            if (found) {
                                
                                // the WSDL document is invalid
                                throw new ModelerException(
                                    "wsdlmodeler.invalid.bindingOperation.multipleMatchingOperations",
                                    new Object[] { bindingOperation.getName(),
                                        binding.getName() });
                            } else {
                                
                                // got it!
                                found = true;
                                portTypeOperation = candidateOperation;
                            }
                        }
                    }
                    
                    if (!found) {
                        
                        // the WSDL document is invalid
                        throw new ModelerException(
                            "wsdlmodeler.invalid.bindingOperation.notFound",
                            new Object[] { bindingOperation.getName(),
                                binding.getName() });
                    }
                }
                
                Operation operation = processSOAPOperation(
                    new ProcessSOAPOperationInfo(
                        port,
                        wsdlPort,
                        portTypeOperation,
                        bindingOperation,
                        soapBinding,
                        document));
                if (operation != null) {
                    port.addOperation(operation);
                    hasOperations = true;
                }
            }
            
            if (!hasOperations) {
                
                // emit a warning if there are no operations
                warn("wsdlmodeler.warning.noOperationsInPort",
                    wsdlPort.getName());
            }
            
            // now deal with the configured handlers
            port.setClientHandlerChainInfo(
                modelInfo.getClientHandlerChainInfo());
            port.setServerHandlerChainInfo(
                modelInfo.getServerHandlerChainInfo());
            
            service.addPort(port);
            createJavaInterfaceForPort(port);
            verifyJavaInterface(port);
            
            // generate stub and tie class names
            String stubClassName = env.getNames().stubFor(port, null);
            String tieClassName = env.getNames().tieFor(port, null);
            
            port.setProperty(ModelProperties.PROPERTY_STUB_CLASS_NAME,
                stubClassName);
            port.setProperty(ModelProperties.PROPERTY_TIE_CLASS_NAME,
                tieClassName);
            
            return true;
            
        } catch (NoSuchEntityException e) {
            
            // should not happen
            return false;
        }
    }
    
    protected Operation processSOAPOperation(ProcessSOAPOperationInfo info) {
        
        Operation operation = new Operation(new QName(
            null, info.bindingOperation.getName()));
        
        setDocumentationIfPresent(operation,
            info.portTypeOperation.getDocumentation());
        
        //fix for bug 4851104
        if (info.portTypeOperation.getStyle() !=
            OperationStyle.REQUEST_RESPONSE &&
            info.portTypeOperation.getStyle() != OperationStyle.ONE_WAY) {
                
            // we only support request-response right now
            warn("wsdlmodeler.warning.ignoringOperation.notRequestResponse",
                info.portTypeOperation.getName());
            return null;
        }
        
        SOAPStyle soapStyle = info.soapBinding.getStyle();
        
        // find out the SOAP operation extension, if any
        SOAPOperation soapOperation = (SOAPOperation)
            getExtensionOfType(info.bindingOperation, SOAPOperation.class);
        
        if (soapOperation != null) {
            if (soapOperation.getStyle() != null) {
                soapStyle = soapOperation.getStyle();
            }
            if (soapOperation.getSOAPAction() != null) {
                operation.setSOAPAction(soapOperation.getSOAPAction());
            }
        }
        
        operation.setStyle(soapStyle);
        
        String uniqueOperationName = info.portTypeOperation.getName();
        
        info.operation = operation;
        info.uniqueOperationName = uniqueOperationName;
        
        if (soapStyle == SOAPStyle.RPC) {
            
            // rpc style
            return processSOAPOperationRPCStyle(info);
        } else {
            
            // document style
            throw new ModelerException(
                "nometadatamodeler.error.documentStyleOperation",
                operation.getName().getLocalPart());
        }
    }
    
    protected Operation processSOAPOperationRPCStyle(
        ProcessSOAPOperationInfo info) {
            
        //fix for bug 4851104
        boolean isRequestResponse =
            info.portTypeOperation.getStyle() ==
                OperationStyle.REQUEST_RESPONSE;
        
        Request request = new Request();
        Response response = new Response();
        
        SOAPBody soapRequestBody = (SOAPBody) getExtensionOfType(
            info.bindingOperation.getInput(), SOAPBody.class);
        if (soapRequestBody == null) {
            
            // the WSDL document is invalid
            throw new ModelerException(
                "wsdlmodeler.invalid.bindingOperation.inputMissingSoapBody",
                new Object[] { info.bindingOperation.getName() });
        }
        SOAPBody soapResponseBody = null;
        
        //fix for bug 4851104
        if (isRequestResponse) {
            soapResponseBody = (SOAPBody) getExtensionOfType(
                info.bindingOperation.getOutput(), SOAPBody.class);
            if (soapResponseBody == null) {
                
                // the WSDL document is invalid
                throw new ModelerException(
                    "wsdlmodeler.invalid.bindingOperation.outputMissingSoapBody",
                    new Object[] { info.bindingOperation.getName() });
            }
        }
        
        if ((soapRequestBody.isLiteral() || !tokenListContains(
            soapRequestBody.getEncodingStyle(),
                SOAPConstants.NS_SOAP_ENCODING)) ||
            (soapResponseBody != null && (soapResponseBody.isLiteral() ||
                !tokenListContains(soapResponseBody.getEncodingStyle(),
                    SOAPConstants.NS_SOAP_ENCODING)))) {
                        
            throw new ModelerException(
                "nometadatamodeler.error.operationNotEncoded",
                info.portTypeOperation.getName());
        }
        
        String requestNamespaceURI = soapRequestBody.getNamespace();
        if (requestNamespaceURI == null) {
            
            /* the WSDL document is invalid
             * at least, that's my interpretation of section 3.5 of the
             * WSDL 1.1 spec!
             */
            throw new ModelerException(
                "wsdlmodeler.invalid.bindingOperation.inputSoapBody.missingNamespace",
                new Object[] { info.bindingOperation.getName() });
        }
        
        String responseNamespaceURI = null;
        
        //fix for bug 4851104
        if (isRequestResponse) {
            responseNamespaceURI = soapResponseBody.getNamespace();
            if (responseNamespaceURI == null) {
                
                /* the WSDL document is invalid
                 * at least, that's my interpretation of section 3.5 of the
                 * WSDL 1.1 spec!
                 */
                throw new ModelerException(
                    "wsdlmodeler.invalid.bindingOperation.outputSoapBody.missingNamespace",
                    new Object[] { info.bindingOperation.getName() });
            }
        }
        
        String structureNamePrefix = null;
        QName portTypeName = (QName) info.modelPort.getProperty(
            ModelProperties.PROPERTY_WSDL_PORT_TYPE_NAME);
        if (portTypeName != null) {
            structureNamePrefix = getNonQualifiedNameFor(portTypeName);
        } else {
            structureNamePrefix = getNonQualifiedNameFor(info.modelPort.getName());
        }
        structureNamePrefix += "_";
        
        QName requestBodyName =
            new QName(requestNamespaceURI, info.portTypeOperation.getName());
        SOAPStructureType requestBodyType =
            new RPCRequestUnorderedStructureType(requestBodyName);
        JavaStructureType requestBodyJavaType =
            new JavaStructureType(makePackageQualified(
                StringUtils.capitalize(structureNamePrefix + 
                    info.uniqueOperationName) + "_RequestStruct",
                    requestBodyName),
                false, requestBodyType);
        requestBodyType.setJavaType(requestBodyJavaType);
        
        Block requestBodyBlock = new Block(requestBodyName, requestBodyType);
        request.addBodyBlock(requestBodyBlock);
        
        //fix for bug 4851104
        SOAPStructureType responseBodyType = null;
        JavaStructureType responseBodyJavaType = null;
        Block responseBodyBlock = null;
        if (isRequestResponse) {
            QName responseBodyName = new QName(responseNamespaceURI,
                info.portTypeOperation.getName() + "Response");
            responseBodyType = new RPCResponseStructureType(responseBodyName);
            responseBodyJavaType = new JavaStructureType(makePackageQualified(
                StringUtils.capitalize(structureNamePrefix +
                    info.uniqueOperationName + "_ResponseStruct"),
                    responseBodyName),
                false, responseBodyType);
            responseBodyType.setJavaType(responseBodyJavaType);
            
            responseBodyBlock = new Block(responseBodyName, responseBodyType);
            response.addBodyBlock(responseBodyBlock);
        }
        
        if (soapRequestBody.getParts() != null) {
            
            // right now, we only support body parts
            // TODO - fix this to include the case of <soap:body parts="..."/>
            warn("wsdlmodeler.warning.ignoringOperation.cannotHandleBodyPartsAttribute",
                info.portTypeOperation.getName());
            return null;
        }
        
        com.sun.xml.rpc.wsdl.document.Message inputMessage =
            info.portTypeOperation.getInput().resolveMessage(info.document);
        
        com.sun.xml.rpc.wsdl.document.Message outputMessage = null;
        
        //fix for bug 4851104
        if (isRequestResponse) {
            outputMessage = info.portTypeOperation.getOutput().resolveMessage(
                info.document);
        }
        
        String parameterOrder = info.portTypeOperation.getParameterOrder();
        java.util.List parameterList = null;
        boolean buildParameterList = false;
        
        if (parameterOrder != null) {
            parameterList = XmlUtil.parseTokenList(parameterOrder);
        } else {
            parameterList = new ArrayList();
            buildParameterList = true;
        }
        
        Set partNames = new HashSet();
        Set inputParameterNames = new HashSet();
        Set outputParameterNames = new HashSet();
        String resultParameterName = null;
        
        for (Iterator iter = inputMessage.parts(); iter.hasNext();) {
            MessagePart part = (MessagePart) iter.next();
            if (part.getDescriptorKind() != SchemaKinds.XSD_TYPE) {
                throw new ModelerException(
                    "wsdlmodeler.invalid.message.partMustHaveTypeDescriptor",
                    new Object[] { inputMessage.getName(), part.getName() });
            }
            partNames.add(part.getName());
            inputParameterNames.add(part.getName());
            if (buildParameterList) {
                parameterList.add(part.getName());
            }
        }
        
        //fix for bug 4851104
        if (isRequestResponse) {
            for (Iterator iter = outputMessage.parts(); iter.hasNext();) {
                MessagePart part = (MessagePart) iter.next();
                if (part.getDescriptorKind() != SchemaKinds.XSD_TYPE) {
                    throw new ModelerException(
                        "wsdlmodeler.invalid.message.partMustHaveTypeDescriptor",
                        new Object[] { outputMessage.getName(),
                            part.getName() });
                }
                partNames.add(part.getName());
                if (buildParameterList && resultParameterName == null) {
                    
                    // pick the first output argument as the result
                    resultParameterName = part.getName();
                } else {
                    outputParameterNames.add(part.getName());
                    if (buildParameterList) {
                        if (!inputParameterNames.contains(part.getName())) {
                            parameterList.add(part.getName());
                        }
                    }
                }
            }
        }
        
        if (!buildParameterList) {
            
            // do some validation of the given parameter order
            for (Iterator iter = parameterList.iterator(); iter.hasNext();) {
                String name = (String) iter.next();
                if (!partNames.contains(name)) {
                    throw new ModelerException(
                        "wsdlmodeler.invalid.parameterorder.parameter",
                        new Object[] { name,
                            info.operation.getName().getLocalPart() });
                }
                partNames.remove(name);
            }
            
            // now we should be left with at most one part
            if (partNames.size() > 1) {
                throw new ModelerException(
                    "wsdlmodeler.invalid.parameterOrder.tooManyUnmentionedParts",
                    new Object[] { info.operation.getName().getLocalPart() });
            }
            if (partNames.size() == 1) {
                
                // This is a fix for bug: 4734459
                String partName = (String)partNames.iterator().next();
                if (outputParameterNames.contains(partName)) {
                    resultParameterName = partName;
                }
            }
        }
        
        if (resultParameterName == null) {
            
            /* this is ugly, but we need to save information about the return
             * type being void at this stage, so that when we later create a
             * Java interface for the port this operation belongs to, we'll do
             * the right thing.
             */
            info.operation.setProperty(OPERATION_HAS_VOID_RETURN_TYPE, "true");
        } else {
            
            // handle result parameter a bit specially
            MessagePart part = outputMessage.getPart(resultParameterName);
            SOAPType soapType =
                analyzer.schemaTypeToSOAPType(part.getDescriptor());
            SOAPStructureMember member = new SOAPStructureMember(
                new QName(null, part.getName()), soapType);
            JavaStructureMember javaMember = new JavaStructureMember(
                env.getNames().validJavaMemberName(
                    part.getName()), soapType.getJavaType(), member, false);
            javaMember.setReadMethod(
                env.getNames().getJavaMemberReadMethod(javaMember));
            javaMember.setWriteMethod(
                env.getNames().getJavaMemberWriteMethod(javaMember));
            member.setJavaStructureMember(javaMember);
            responseBodyType.add(member);
            responseBodyJavaType.add(javaMember);
            Parameter parameter = new Parameter(
                env.getNames().validJavaMemberName(part.getName()));
            parameter.setEmbedded(true);
            parameter.setType(soapType);
            parameter.setBlock(responseBodyBlock);
            response.addParameter(parameter);
            info.operation.setProperty(
                WSDL_RESULT_PARAMETER, parameter.getName());
        }
        
        /* create a definitive list of parameters to match what we'd like to get
         * in the java interface (which is generated much later)
         */
        List definitiveParameterList = new ArrayList();
        
        for (Iterator iter = parameterList.iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            boolean isInput = inputParameterNames.contains(name);
            boolean isOutput = outputParameterNames.contains(name);
            SOAPType soapType = null;
            Parameter inParameter = null;
            
            if (isInput && isOutput) {
                
                // make sure types match
                if (!inputMessage.getPart(name).getDescriptor().equals(
                    outputMessage.getPart(name).getDescriptor())) {
                        
                    throw new ModelerException(
                        "wsdlmodeler.invalid.parameter.differentTypes",
                        new Object[] { name,
                            info.operation.getName().getLocalPart() });
                }
            }
            
            if (isInput) {
                MessagePart part = inputMessage.getPart(name);
                soapType = analyzer.schemaTypeToSOAPType(part.getDescriptor());
                SOAPStructureMember member = new SOAPStructureMember(
                    new QName(null, part.getName()), soapType);
                JavaStructureMember javaMember = new JavaStructureMember(
                    env.getNames().validJavaMemberName(
                        part.getName()), soapType.getJavaType(), member, false);
                javaMember.setReadMethod(
                    env.getNames().getJavaMemberReadMethod(javaMember));
                javaMember.setWriteMethod(
                    env.getNames().getJavaMemberWriteMethod(javaMember));
                member.setJavaStructureMember(javaMember);
                requestBodyType.add(member);
                requestBodyJavaType.add(javaMember);
                inParameter = new Parameter(
                    env.getNames().validJavaMemberName(part.getName()));
                inParameter.setEmbedded(true);
                inParameter.setType(soapType);
                inParameter.setBlock(requestBodyBlock);
                request.addParameter(inParameter);
                definitiveParameterList.add(inParameter.getName());
            }
            if (isOutput) {
                
                // output parameter - disallowed
                throw new ModelerException(
                    "nometadatamodeler.outputParameterEncountered",
                    new Object[] { info.portTypeOperation.getName(), name });
            }
            
        }
        
        // faults with duplicate names
        Set duplicateNames = new HashSet();

        // fault names
        Set faultNames = new HashSet();
        
        /* look for fault messages with the same part name
         * handle faults
         */
        for (Iterator iter = info.bindingOperation.faults(); iter.hasNext(); ) {
            BindingFault bindingFault = (BindingFault) iter.next();
            
            com.sun.xml.rpc.wsdl.document.Fault portTypeFault = null;
            for (Iterator iter2 = info.portTypeOperation.faults();
                iter2.hasNext(); ) {
                    
                com.sun.xml.rpc.wsdl.document.Fault aFault =
                    (com.sun.xml.rpc.wsdl.document.Fault) iter2.next();
                
                if (aFault.getName().equals(bindingFault.getName())) {
                    if (portTypeFault != null) {
                        
                        // the WSDL document is invalid
                        throw new ModelerException(
                            "wsdlmodeler.invalid.bindingFault.notUnique",
                            new Object[] { bindingFault.getName(),
                                info.bindingOperation.getName() });
                    } else {
                        portTypeFault = aFault;
                    }
                }
            }
            
            if (portTypeFault == null) {
                
                // the WSDL document is invalid
                throw new ModelerException(
                    "wsdlmodeler.invalid.bindingFault.notFound",
                    new Object[] { bindingFault.getName(),
                        info.bindingOperation.getName() });
            }
            
            SOAPFault soapFault = 
                (SOAPFault) getExtensionOfType(bindingFault, SOAPFault.class);
            if (soapFault == null) {
                
                // the WSDL document is invalid
                throw new ModelerException(
                    "wsdlmodeler.invalid.bindingFault.outputMissingSoapFault",
                    new Object[] { bindingFault.getName(),
                        info.bindingOperation.getName() });
            }
            
            com.sun.xml.rpc.wsdl.document.Message faultMessage =
                portTypeFault.resolveMessage(info.document);
            Iterator iter2 = faultMessage.parts();
            if (!iter2.hasNext()) {
                
                // the WSDL document is invalid
                throw new ModelerException(
                    "wsdlmodeler.invalid.bindingFault.emptyMessage",
                    new Object[] { bindingFault.getName(),
                        faultMessage.getName() });
            }
            String faultNamespaceURI = soapFault.getNamespace();
            MessagePart faultPart = (MessagePart) iter2.next();
            QName faultQName =
                new QName(faultNamespaceURI, faultPart.getName());
            if (faultNames.contains(faultQName)) {
                duplicateNames.add(faultQName);
            } else {
                faultNames.add(faultQName);
            }
        }
        
        // handle faults
        for (Iterator iter = info.bindingOperation.faults(); iter.hasNext(); ) {
            BindingFault bindingFault = (BindingFault) iter.next();
            
            com.sun.xml.rpc.wsdl.document.Fault portTypeFault = null;
            for (Iterator iter2 = info.portTypeOperation.faults();
                iter2.hasNext(); ) {
                    
                com.sun.xml.rpc.wsdl.document.Fault aFault =
                    (com.sun.xml.rpc.wsdl.document.Fault) iter2.next();
                
                if (aFault.getName().equals(bindingFault.getName())) {
                    if (portTypeFault != null) {
                        
                        // the WSDL document is invalid
                        throw new ModelerException(
                            "wsdlmodeler.invalid.bindingFault.notUnique",
                            new Object[] { bindingFault.getName(),
                                info.bindingOperation.getName() });
                    } else {
                        portTypeFault = aFault;
                    }
                }
            }
            
            if (portTypeFault == null) {
                
                // the WSDL document is invalid
                throw new ModelerException(
                    "wsdlmodeler.invalid.bindingFault.notFound",
                    new Object[] { bindingFault.getName(),
                        info.bindingOperation.getName() });
                
            }
            
            /* changed this so that the message name is used to create the java
             * exception name later on
             * Fault fault = new Fault(portTypeFault.getName());
             */
            Fault fault = new Fault(portTypeFault.getMessage().getLocalPart());
            
            SOAPFault soapFault =
                (SOAPFault) getExtensionOfType(bindingFault, SOAPFault.class);
            if (soapFault == null) {
                
                // the WSDL document is invalid
                throw new ModelerException(
                    "wsdlmodeler.invalid.bindingFault.outputMissingSoapFault",
                    new Object[] { bindingFault.getName(),
                        info.bindingOperation.getName() });
            }
            
            if (soapFault.isLiteral() ||
                !tokenListContains(soapFault.getEncodingStyle(),
                    SOAPConstants.NS_SOAP_ENCODING)) {
                        
                // with rpc style, we only support encoded use
                warn("wsdlmodeler.warning.ignoringFault.notEncoded",
                    new Object[] { bindingFault.getName(),
                        info.bindingOperation.getName() });
                continue;
            }
            
            String faultNamespaceURI = soapFault.getNamespace();
            if (faultNamespaceURI == null) {
                
                // the WSDL document is invalid
                throw new ModelerException(
                    "wsdlmodeler.invalid.bindingFault.missingNamespace",
                    new Object[] { bindingFault.getName(),
                        info.bindingOperation.getName() });
            }
            
            com.sun.xml.rpc.wsdl.document.Message faultMessage =
                portTypeFault.resolveMessage(info.document);
            Iterator iter2 = faultMessage.parts();
            if (!iter2.hasNext()) {
                
                // the WSDL document is invalid
                throw new ModelerException(
                    "wsdlmodeler.invalid.bindingFault.emptyMessage",
                    new Object[] { bindingFault.getName(),
                        faultMessage.getName() });
            }
            MessagePart faultPart = (MessagePart) iter2.next();
            
            if (isIncorrectFaultPartName(faultPart.getName())) {
                throw new ModelerException(
                    "nometadatamodeler.error.incorrectFaultPartName",
                    new Object[] { info.portTypeOperation.getName(),
                        bindingFault.getName(), faultPart.getName() });
            }
            
            QName faultQName =
                new QName(faultNamespaceURI, faultPart.getName());
            
            // Don't include fault messages with non-unique part names
            if (duplicateNames.contains(faultQName)) {
                warn("wsdlmodeler.duplicate.fault.part.name",
                    new Object[] { bindingFault.getName(),
                        info.portTypeOperation.getName(), faultPart.getName()});
                continue;
            }
            if (iter2.hasNext()) {
                
                // the WSDL document is invalid
                throw new ModelerException(
                    "wsdlmodeler.invalid.bindingFault.messageHasMoreThanOnePart",
                    new Object[] { bindingFault.getName(),
                        faultMessage.getName() });
            }
            
            if (faultPart.getDescriptorKind() != SchemaKinds.XSD_TYPE) {
                throw new ModelerException(
                    "wsdlmodeler.invalid.message.partMustHaveTypeDescriptor",
                    new Object[] { faultMessage.getName(),
                        faultPart.getName() });
            }
            
            if (isIncorrectFaultPartType(faultPart.getDescriptor())) {
                throw new ModelerException(
                    "nometadatamodeler.error.incorrectFaultPartType",
                    new Object[] { info.portTypeOperation.getName(),
                        bindingFault.getName(),
                        faultPart.getName(),
                        faultPart.getDescriptor() });
            }
            
            SOAPType faultType =
                analyzer.schemaTypeToSOAPType(faultPart.getDescriptor());
            Block faultBlock = new Block(faultQName, faultType);
            fault.setBlock(faultBlock);
            response.addFaultBlock(faultBlock);
            info.operation.addFault(fault);
        }
        
        // handle headers (just ignore all of them)
        boolean explicitServiceContext = Boolean.valueOf(options.getProperty(
            ProcessorOptions.EXPLICIT_SERVICE_CONTEXT_PROPERTY)).booleanValue();
        for (Iterator iter = info.bindingOperation.getInput().extensions();
            iter.hasNext();) {
                
            Extension extension = (Extension) iter.next();
            if (extension instanceof SOAPHeader) {
                SOAPHeader header = (SOAPHeader) extension;
                warn("wsdlmodeler.warning.ignoringHeader",
                new Object[] { header.getPart(),
                    info.bindingOperation.getName() });
                continue;
            }
        }
        
        //fix for bug 4851104
        if (isRequestResponse) {
            for (Iterator iter = info.bindingOperation.getOutput().extensions();
                iter.hasNext();) {
                    
                Extension extension = (Extension) iter.next();
                if (extension instanceof SOAPHeader) {
                    SOAPHeader header = (SOAPHeader) extension;
                    warn("wsdlmodeler.warning.ignoringHeader",
                    new Object[] { header.getPart(),
                        info.bindingOperation.getName() });
                    continue;
                }
            }
        }
        
        // save the parameter order
        info.operation.setProperty(WSDL_PARAMETER_ORDER,
            definitiveParameterList);
        
        info.operation.setRequest(request);
        
        //fix for bug 4851104
        if (isRequestResponse) {
            info.operation.setResponse(response);
        }
        return info.operation;
    }
    
    protected void createJavaInterfaceForPort(Port port) {
        
        QName portTypeName = (QName) port.getProperty(
            ModelProperties.PROPERTY_WSDL_PORT_TYPE_NAME);
        QName bindingName = (QName) port.getProperty(
            ModelProperties.PROPERTY_WSDL_BINDING_NAME);
        String interfaceName = null;
        if (modelInfo.getInterfaceName() != null) {
            interfaceName = modelInfo.getInterfaceName();
        } else {
            if (portTypeName != null) {
                
                /* got portType information from WSDL, use it to name
                 * the interface
                 */
                interfaceName = makePackageQualified(
                    env.getNames().validJavaClassName(
                        getNonQualifiedNameFor(portTypeName)), portTypeName);
            } else {
                
                // somehow we only got the port name, so we use that
                interfaceName = makePackageQualified(
                    env.getNames().validJavaClassName(
                        getNonQualifiedNameFor(port.getName())),
                            port.getName());
            }
        }
        
        JavaInterface intf = new JavaInterface(interfaceName);
        
        Set methodNames = new HashSet();
        Set methodSignatures = new HashSet();
        
        for (Iterator iter = port.getOperations(); iter.hasNext();) {
            Operation operation = (Operation) iter.next();
            createJavaMethodForOperation(port, operation, intf,
                methodNames, methodSignatures);
        }
        port.setJavaInterface(intf);
    }
    
    private void createJavaMethodForOperation(Port port, Operation operation,
        JavaInterface intf, Set methodNames, Set methodSignatures) {
            
        String candidateName = env.getNames().validJavaMemberName(
            operation.getName().getLocalPart());
        JavaMethod method = new JavaMethod(candidateName);
        
        Request request = operation.getRequest();
        Iterator requestBodyBlocks = request.getBodyBlocks();
        Block requestBlock = (requestBodyBlocks.hasNext() ?
            (Block) request.getBodyBlocks().next() : null);
        
        //fix for bug 4851104
        Response response = operation.getResponse();
        Iterator responseBodyBlocks = null;
        Block responseBlock = null;
        if (response != null) {
            responseBodyBlocks = response.getBodyBlocks();
            responseBlock = (responseBodyBlocks.hasNext() ?
                (Block) response.getBodyBlocks().next() : null);
        }
        
        /* build a signature of the form
         * "opName%arg1type%arg2type%...%argntype so that we
         * detect overloading conflicts in the generated java
         * interface/classes
         */
        String signature = candidateName;
        
        List parameterOrder =
            (List) operation.getProperty(WSDL_PARAMETER_ORDER);
        if (parameterOrder == null) {
            for (Iterator iter = request.getParameters(); iter.hasNext();) {
                Parameter parameter = (Parameter) iter.next();
                
                if (parameter.getJavaParameter() != null) {
                    throw new ModelerException("wsdlmodeler.invalidOperation",
                        operation.getName().getLocalPart());
                }
                
                JavaType parameterType = parameter.getType().getJavaType();
                JavaParameter javaParameter = new JavaParameter(
                    env.getNames().validJavaMemberName(parameter.getName()),
                    parameterType, parameter,
                    parameter.getLinkedParameter() != null);
                method.addParameter(javaParameter);
                parameter.setJavaParameter(javaParameter);
                
                signature += "%" + parameterType.getName();
            }
            
            boolean operationHasVoidReturnType =
                operation.getProperty(OPERATION_HAS_VOID_RETURN_TYPE) != null;
            Parameter resultParameter = null;
            
            //fix for bug 4851104
            if(response != null) {
                for (Iterator iter = response.getParameters();
                    iter.hasNext();) {
                        
                    if (!operationHasVoidReturnType &&
                        resultParameter == null) {
                            
                        resultParameter = (Parameter) iter.next();
                        
                        if (resultParameter.getJavaParameter() != null) {
                            throw new ModelerException(
                                "wsdlmodeler.invalidOperation",
                                operation.getName().getLocalPart());
                        }
                        
                        if (resultParameter.getLinkedParameter() != null) {
                            
                            // result is an [inout] parameter
                            throw new ModelerException(
                                "wsdlmodeler.resultIsInOutParameter",
                                operation.getName().getLocalPart());
                        }
                        
                        if (resultParameter.getBlock() != responseBlock) {
                            
                            // result outside of the response body
                            throw new ModelerException(
                                "wsdlmodeler.invalidOperation",
                                operation.getName().getLocalPart());
                        }
                        
                        JavaType returnType =
                            resultParameter.getType().getJavaType();
                        method.setReturnType(returnType);
                        
                    } else {
                        
                        // ordinary out parameter, may be in/out
                        Parameter parameter = (Parameter) iter.next();
                        
                        if (parameter.getJavaParameter() != null) {
                            throw new ModelerException(
                                "wsdlmodeler.invalidOperation",
                                operation.getName().getLocalPart());
                        }
                        
                        JavaParameter javaParameter = null;
                        if (parameter.getLinkedParameter() != null) {
                            javaParameter = parameter.getLinkedParameter().
                                getJavaParameter();
                        }
                        JavaType parameterType =
                            parameter.getType().getJavaType();
                        parameterType.setHolder(true);
                        parameterType.setHolderPresent(false);
                        if (javaParameter == null) {
                            javaParameter = new JavaParameter(
                                env.getNames().validJavaMemberName(
                                    parameter.getName()),
                                parameterType,
                                parameter,
                                true);
                        }
                        parameter.setJavaParameter(javaParameter);
                        if (parameter.getLinkedParameter() == null) {
                            method.addParameter(javaParameter);
                        }
                    }
                }
            }
            
            //fix for bug 4851104
            if (response == null || operationHasVoidReturnType) {
                method.setReturnType(javaTypes.VOID_JAVATYPE);
            }
            
        } else {
            
            // parameter order is not null
            boolean operationHasVoidReturnType = operation.getProperty(OPERATION_HAS_VOID_RETURN_TYPE) != null;
            
            for (Iterator iter = parameterOrder.iterator(); iter.hasNext();) {
                String parameterName = (String) iter.next();
                Parameter requestParameter =
                    request.getParameterByName(parameterName);
                
                //fix for bug 4851104
                Parameter responseParameter = response != null ?
                    response.getParameterByName(parameterName) : null;
                
                if (requestParameter == null && responseParameter == null) {
                    
                    // should not happen
                    throw new ModelerException(
                        "wsdlmodeler.invalidState.modelingOperation",
                        operation.getName().getLocalPart());
                }
                
                if (requestParameter != null) {
                    Parameter linkedParameter =
                        requestParameter.getLinkedParameter();
                    if (responseParameter == null || linkedParameter == null) {
                        
                        // in parameter
                        JavaType parameterType =
                            requestParameter.getType().getJavaType();
                        JavaParameter javaParameter = new JavaParameter(
                            env.getNames().validJavaMemberName(
                                requestParameter.getName()),
                            parameterType,
                            requestParameter,
                            false);
                        method.addParameter(javaParameter);
                        requestParameter.setJavaParameter(javaParameter);
                        signature += "%" + parameterType.getName();
                    } else {
                        
                        // inout parameter
                        if (responseParameter != linkedParameter) {
                            // should not happen
                            throw new ModelerException(
                                "wsdlmodeler.invalidState.modelingOperation",
                                operation.getName().getLocalPart());
                        }
                        
                        JavaType parameterType =
                            responseParameter.getType().getJavaType();
                        JavaParameter javaParameter = new JavaParameter(
                            env.getNames().validJavaMemberName(
                                responseParameter.getName()),
                            parameterType,
                            responseParameter,
                            true);
                        parameterType.setHolder(true);
                        parameterType.setHolderPresent(false);
                        requestParameter.setJavaParameter(javaParameter);
                        responseParameter.setJavaParameter(javaParameter);
                        method.addParameter(javaParameter);
                        requestParameter.setJavaParameter(javaParameter);
                        responseParameter.setJavaParameter(javaParameter);
                        signature += "%" + parameterType.getName();
                    }
                } else if (responseParameter != null) {
                    
                    // out parameter
                    Parameter linkedParameter =
                        responseParameter.getLinkedParameter();
                    if (linkedParameter != null) {
                        
                        // should not happen
                        throw new ModelerException(
                            "wsdlmodeler.invalidState.modelingOperation",
                            operation.getName().getLocalPart());
                    }
                    
                    JavaType parameterType =
                        responseParameter.getType().getJavaType();
                    parameterType.setHolder(true);
                    parameterType.setHolderPresent(false);
                    JavaParameter javaParameter = new JavaParameter(
                        env.getNames().validJavaMemberName(
                            responseParameter.getName()),
                        parameterType,
                        responseParameter,
                        true);
                    responseParameter.setJavaParameter(javaParameter);
                    method.addParameter(javaParameter);
                    signature += "%" + parameterType.getName();
                }
            }
            
            // handle result parameter separately
            String resultParameterName =
                (String) operation.getProperty(WSDL_RESULT_PARAMETER);
            if (resultParameterName == null) {
                if (!operationHasVoidReturnType) {
                    
                    // should not happen
                    throw new ModelerException(
                        "wsdlmodeler.invalidState.modelingOperation",
                        operation.getName().getLocalPart());
                }
                method.setReturnType(javaTypes.VOID_JAVATYPE);
            } else {
                if (operationHasVoidReturnType) {
                    
                    // should not happen
                    throw new ModelerException(
                        "wsdlmodeler.invalidState.modelingOperation",
                        operation.getName().getLocalPart());
                }
                
                Parameter resultParameter =
                    response.getParameterByName(resultParameterName);
                JavaType returnType = resultParameter.getType().getJavaType();
                method.setReturnType(returnType);
            }
            
        }
        
        String operationName = candidateName;
        if (methodSignatures.contains(signature)) {
            throw new ModelerException(
                "nometadatamodeler.duplicateMethodSignature", operationName);
        }
        methodSignatures.add(signature);
        methodNames.add(method.getName());
        
        operation.setJavaMethod(method);
        intf.addMethod(method);
        
        for (Iterator iter = operation.getFaults();
            iter != null && iter.hasNext();) {
                
            Fault fault = (Fault) iter.next();
            createJavaException(fault, port, operationName);
        }
        JavaException javaException;
        Fault fault;
        for (Iterator iter = operation.getFaults(); iter.hasNext();) {
            fault = (Fault)iter.next();
            javaException = fault.getJavaException();
            method.addException(javaException.getName());
        }
        
    }
    
    protected boolean createJavaException(Fault fault, Port port,
        String operationName) {
            
        String exceptionName = null;
        String propertyName =
            env.getNames().validJavaMemberName(fault.getName());
        SOAPType faultType = (SOAPType)fault.getBlock().getType();
        SOAPStructureType soapStruct;
        if (faultType instanceof SOAPStructureType) {
            
            // should not happen
            throw new ModelerException(
                "wsdlmodeler.invalidState.modelingOperation", operationName);
        } else {
            exceptionName = makePackageQualified(
                env.getNames().validJavaClassName(
                    fault.getName()), port.getName());
            soapStruct = new SOAPOrderedStructureType(new QName(
                faultType.getName().getNamespaceURI(), fault.getName()));
            QName memberName = new QName(
                fault.getBlock().getName().getNamespaceURI(),
            StringUtils.capitalize(faultType.getName().getLocalPart()));
            SOAPStructureMember soapMember =
                new SOAPStructureMember(memberName, faultType);
            JavaStructureMember javaMember =
                new JavaStructureMember(memberName.getLocalPart(),
            faultType.getJavaType(), soapMember);
            soapMember.setJavaStructureMember(javaMember);
            javaMember.setConstructorPos(0);
            javaMember.setReadMethod("get"+memberName.getLocalPart());
            javaMember.setInherited(soapMember.isInherited());
            soapMember.setJavaStructureMember(javaMember);
            soapStruct.add(soapMember);
        }
        
        JavaException existingJavaException =
            (JavaException) javaExceptions.get(exceptionName);
        if (existingJavaException != null) {
            if (existingJavaException.getName().equals(exceptionName)) {
                if (((SOAPType)
                    existingJavaException.getOwner()).getName().equals(
                        soapStruct.getName())) {
                            
                    // we have mapped this fault already
                    if (faultType instanceof SOAPStructureType) {
                        fault.getBlock().setType(
                            (SOAPType) existingJavaException.getOwner());
                    }
                    fault.setJavaException(existingJavaException);
                    createRelativeJavaExceptions(fault, port, operationName);
                    return false;
                }
            }
        }
        
        JavaException javaException = new JavaException(exceptionName,
            false, soapStruct);
        soapStruct.setJavaType(javaException);
        
        javaExceptions.put(javaException.getName(), javaException);
        
        Iterator members = soapStruct.getMembers();
        SOAPStructureMember member = null;
        JavaStructureMember javaMember;
        for (int i=0; members.hasNext(); i++) {
            member = (SOAPStructureMember)members.next();
            javaMember = member.getJavaStructureMember();
            javaMember.setConstructorPos(i);
            javaException.add(javaMember);
        }
        if (faultType instanceof SOAPStructureType) {
            fault.getBlock().setType(soapStruct);
        }
        fault.setJavaException(javaException);
        
        createRelativeJavaExceptions(fault, port, operationName);
        return true;
    }
    
    protected void createRelativeJavaExceptions(Fault fault, Port port,
        String operationName) {
            
        if (fault.getParentFault() != null &&
            fault.getParentFault().getJavaException() == null) {
                
            createJavaException(fault.getParentFault(), port, operationName);
            fault.getParentFault().getJavaException().addSubclass(
                fault.getJavaException());
            ((SOAPStructureType)
                fault.getParentFault().getJavaException().getOwner())
                    .addSubtype(
                        (SOAPStructureType)fault.getJavaException().getOwner());
        }
        Iterator subfaults = fault.getSubfaults();
        if (subfaults != null) {
            Fault subfault;
            while (subfaults.hasNext()) {
                subfault = (Fault) subfaults.next();
                if (subfault.getJavaException() == null) {
                    boolean didCreateNewException =
                        createJavaException(subfault, port, operationName);
                    fault.getJavaException().addSubclass(
                        subfault.getJavaException());
                    ((SOAPStructureType) fault.getJavaException().getOwner())
                        .addSubtype((SOAPStructureType) subfault
                            .getJavaException().getOwner());
                }
            }
        }
    }
    
    protected void verifyJavaInterface(Port port) {
        JavaInterface javaInterface = port.getJavaInterface();
        Class remoteInterface = getClassForNameOrFail(javaInterface.getName());
        if (!java.rmi.Remote.class.isAssignableFrom(remoteInterface)) {
            throw new ModelerException(
                "nometadatamodeler.error.notRemoteInterface",
                remoteInterface.getName());
        }
        Map javaTypeMap = new HashMap();
        for (Iterator iter = javaInterface.getMethods(); iter.hasNext();) {
            JavaMethod javaMethod = (JavaMethod) iter.next();
            Method[] methods = remoteInterface.getMethods();
            Method method = null;
            boolean found = false;
            for (int i = 0; i < methods.length; ++i) {
                if (methods[i].getName().equals(javaMethod.getName())) {
                    Class[] argTypes = methods[i].getParameterTypes();
                    if (argTypes.length != javaMethod.getParameterCount()) {
                        continue;
                    }
                    int index = 0;
                    for (Iterator iter2 = javaMethod.getParameters();
                        iter2.hasNext(); ++index) {
                            
                        JavaParameter param = (JavaParameter) iter2.next();
                        if (!param.getType().getName().equals(
                            getReadableClassName(argTypes[index]))) {
                                
                            break;
                        }
                    }
                    if (index < argTypes.length) {
                        
                        // not a match
                        continue;
                    }
                    Class returnType = methods[i].getReturnType();
                    if (!javaMethod.getReturnType().getName().equals(
                        getReadableClassName(returnType))) {
                            
                        continue;
                    }
                    found = true;
                    method = methods[i];
                    break;
                }
            }
            if (found) {
                
                // verify exceptions thrown by the method
                Class[] exceptionTypes = method.getExceptionTypes();
                for (Iterator iter2 = javaMethod.getExceptions();
                    iter2.hasNext();) {
                        
                    JavaException javaException = (JavaException) iter2.next();
                    boolean foundException = false;
                    for (int i = 0; i < exceptionTypes.length; ++i) {
                        if (javaException.getName().equals(
                            exceptionTypes[i].getName())) {
                                
                            foundException = true;
                            getConstructorForSignatureOrFail(
                                exceptionTypes[i],
                                new Class[] { String.class });
                            break;
                        }
                    }
                    if (!foundException) {
                        throw new ModelerException(
                            "nometadatamodeler.error.exceptionNotFound",
                            new Object[] { javaMethod.getName(),
                                javaException.getName() });
                    }
                }
                
                // save the argument and return type mappings
                Class[] argTypes = method.getParameterTypes();
                int index = 0;
                for (Iterator iter2 = javaMethod.getParameters();
                    iter2.hasNext(); ++index) {
                        
                    JavaParameter param = (JavaParameter) iter2.next();
                    javaTypeMap.put(param.getType(), argTypes[index]);
                }
                javaTypeMap.put(javaMethod.getReturnType(),
                    method.getReturnType());
            } else {
                throw new ModelerException(
                    "nometadatamodeler.error.methodNotFound",
                    new Object[] { javaMethod.getName(),
                        remoteInterface.getName() });
            }
        }
        
        // verify all types used
        for (Iterator iter = javaTypeMap.entrySet().iterator();
            iter.hasNext();) {
                
            Map.Entry entry = (Map.Entry) iter.next();
            verifyJavaType((JavaType) entry.getKey(), (Class) entry.getValue());
        }
    }
    
    protected static String getReadableClassName(Class klass) {
        if (klass.isArray()) {
            return getReadableClassName(klass.getComponentType()) + "[]";
        } else if (klass.isPrimitive()) {
            if (klass == Boolean.TYPE) {
                return "boolean";
            } else if (klass == Character.TYPE) {
                return "char";
            } else if (klass == Byte.TYPE) {
                return "byte";
            } else if (klass == Short.TYPE) {
                return "short";
            } else if (klass == Integer.TYPE) {
                return "int";
            } else if (klass == Long.TYPE) {
                return "long";
            } else if (klass == Float.TYPE) {
                return "float";
            } else if (klass == Double.TYPE) {
                return "double";
            } else if (klass == Void.TYPE) {
                return "void";
            } else {
                throw new IllegalArgumentException();
            }
        } else {
            return klass.getName();
        }
    }
    
    protected void verifyJavaType(JavaType javaType, Class klass) {
        if (javaType.isHolder()) {
            
            // holders are invalid
            throw new ModelerException(
                "nometadatamodeler.error.holderDetected");
        }
        if (javaType instanceof JavaArrayType) {
            JavaArrayType javaArrayType = (JavaArrayType) javaType;
            if (javaArrayType.getElementType() instanceof JavaArrayType) {
                
                // nested arrays are invalid
                throw new ModelerException(
                    "nometadatamodeler.error.nestedArrayDetected");
            }
        }
        if (javaType instanceof JavaStructureType) {
            
            // TODO - verify the value type
        }
    }
    
    protected void verifyServiceInterface(Service service) {
        JavaInterface theInterface = (JavaInterface)service.getJavaInterface();
        Class serviceInterface = getClassForNameOrNull(theInterface.getName());
        if (serviceInterface != null) {
            if (!javax.xml.rpc.Service.class.isAssignableFrom(
                serviceInterface)) {
                    
                throw new ModelerException(
                    "nometadatamodeler.error.notServiceInterface",
                    serviceInterface.getName());
            }
            for (Iterator iter = service.getPorts(); iter.hasNext();) {
                Port port = (Port) iter.next();
                String remoteInterfaceName = port.getJavaInterface().getName();
                String portName = env.getNames().getPortName(port);
                Class remoteInterface =
                    getClassForNameOrFail(remoteInterfaceName);
                Method getPort =
                    getMethodForNameAndSignatureAndReturnTypeOrFail(serviceInterface, "get" + portName, new Class[] {}, remoteInterface);
            }
        }
    }
    
    protected void warn(String key) {
        env.warn(messageFactory.getMessage(key));
    }
    
    protected void warn(String key, String arg) {
        env.warn(messageFactory.getMessage(key, arg));
    }
    
    protected void warn(String key, Object[] args) {
        env.warn(messageFactory.getMessage(key, args));
    }
    
    protected Class getClassForNameOrNull(String name) {
        try {
            return RmiUtils.getClassForName(name, env.getClassLoader());
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
    
    protected Class getClassForNameOrFail(String name) {
        try {
            return RmiUtils.getClassForName(name, env.getClassLoader());
        } catch (ClassNotFoundException e) {
            throw new ModelerException("nometadatamodeler.error.classNotFound",
                name);
        }
    }
    
    protected Method getMethodForNameAndSignatureAndReturnTypeOrFail(
        Class klass, String methodName, Class[] argTypes, Class returnType) {
            
        try {
            Method method = klass.getMethod(methodName, argTypes);
            if (method.getReturnType() != returnType) {
                throw new ModelerException(
                    "nometadatamodeler.error.methodNotFound",
                    new Object[] { methodName, klass.getName() });
            }
            return method;
        } catch (NoSuchMethodException e) {
            throw new ModelerException("nometadatamodeler.error.methodNotFound",
                new Object[] { methodName, klass.getName() });
        }
    }
    
    protected Constructor getConstructorForSignatureOrFail(Class klass, Class[] argTypes) {
        try {
            Constructor constructor = klass.getConstructor(argTypes);
            return constructor;
        } catch (NoSuchMethodException e) {
            throw new ModelerException(
                "nometadatamodeler.error.constructorNotFound",
                new Object[] { klass.getName() });
        }
    }
    
    protected String getJavaPackageName(QName name) {
        String packageName = null;
        if (modelInfo.getNamespaceMappingRegistry() != null) {
            NamespaceMappingInfo i =
                modelInfo.getNamespaceMappingRegistry().getNamespaceMappingInfo(
                    name);
            if (i != null) {
                return i.getJavaPackageName();
            }
        }
        return packageName;
    }
    
    protected String makePackageQualified(String s, QName name) {
        String javaPackageName = getJavaPackageName(name);
        if (javaPackageName != null) {
            return javaPackageName + "." + s;
        } else if (modelInfo.getJavaPackageName() != null &&
            !modelInfo.getJavaPackageName().equals("")) {
                
            return modelInfo.getJavaPackageName() + "." + s;
        } else {
            return s;
        }
    }
    
    protected QName makePackageQualified(QName name) {
        return new QName(name.getNamespaceURI(),
            makePackageQualified(name.getLocalPart(), name));
    }
    
    protected String getNonQualifiedNameFor(QName name) {
        return env.getNames().validJavaClassName(name.getLocalPart());
    }
    
    protected static boolean isIncorrectFaultPartName(String s) {
        return !s.equals("message");
    }
    
    protected static boolean isIncorrectFaultPartType(QName n) {
        return !n.equals(SchemaConstants.QNAME_TYPE_STRING);
    }
    
    /* TODO - pull these out so they can be shared by several modelers
     * (e.g., create a ModelerUtil class)
     */
    protected static Extension getExtensionOfType(Extensible extensible,
        Class type) {
            
        for (Iterator iter = extensible.extensions(); iter.hasNext();) {
            Extension extension = (Extension) iter.next();
            if (extension.getClass().equals(type)) {
                return extension;
            }
        }
        return null;
    }
    
    protected static void setDocumentationIfPresent(ModelObject obj,
        Documentation documentation) {
            
        if (documentation != null && documentation.getContent() != null) {
            obj.setProperty(WSDL_DOCUMENTATION, documentation.getContent());
        }
    }
    
    protected static QName getQNameOf(GloballyKnown entity) {
        return new QName(entity.getDefining().getTargetNamespaceURI(),
            entity.getName());
    }
    
    protected static boolean tokenListContains(String tokenList,
        String target) {
            
        if (tokenList == null) {
            return false;
        }
        
        StringTokenizer tokenizer = new StringTokenizer(tokenList, " ");
        while (tokenizer.hasMoreTokens()) {
            String s = tokenizer.nextToken();
            if (target.equals(s)) {
                return true;
            }
        }
        return false;
    }
    
    private Model model;
    private Properties options;
    private ProcessorEnvironment env;
    private NoMetadataModelInfo modelInfo;
    private NamespaceMappingRegistryInfo namespaceMappingRegistry;
    private SchemaAnalyzer analyzer;
    private LocalizableMessageFactory messageFactory;
    private JavaSimpleTypeCreator javaTypes;
    private Map javaExceptions;
    
    public class ProcessSOAPOperationInfo {
        
        public ProcessSOAPOperationInfo(Port modelPort,
            com.sun.xml.rpc.wsdl.document.Port port,
            com.sun.xml.rpc.wsdl.document.Operation portTypeOperation,
            BindingOperation bindingOperation,
            SOAPBinding soapBinding,
            WSDLDocument document) {
                
            this.modelPort = modelPort;
            this.port = port;
            this.portTypeOperation = portTypeOperation;
            this.bindingOperation = bindingOperation;
            this.soapBinding = soapBinding;
            this.document = document;
        }
        
        public Port modelPort;
        public com.sun.xml.rpc.wsdl.document.Port port;
        public com.sun.xml.rpc.wsdl.document.Operation portTypeOperation;
        public BindingOperation bindingOperation;
        public SOAPBinding soapBinding;
        public WSDLDocument document;
        
        // additional data
        public Operation operation;
        public String uniqueOperationName;
    }
    
    private static final String OPERATION_HAS_VOID_RETURN_TYPE =
        "com.sun.xml.rpc.processor.modeler.wsdl.operationHasVoidReturnType";
    private static final String WSDL_DOCUMENTATION =
        "com.sun.xml.rpc.processor.modeler.wsdl.documentation";
    private static final String WSDL_PARAMETER_ORDER =
        "com.sun.xml.rpc.processor.modeler.wsdl.parameterOrder";
    private static final String WSDL_RESULT_PARAMETER =
        "com.sun.xml.rpc.processor.modeler.wsdl.resultParameter";
    
}

