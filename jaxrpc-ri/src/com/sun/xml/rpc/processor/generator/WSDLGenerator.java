/*
 * $Id: WSDLGenerator.java,v 1.1 2006-04-12 20:33:41 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.generator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.ProcessorAction;
import com.sun.xml.rpc.processor.ProcessorOptions;
import com.sun.xml.rpc.processor.config.Configuration;
import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.Block;
import com.sun.xml.rpc.processor.model.Fault;
import com.sun.xml.rpc.processor.model.Message;
import com.sun.xml.rpc.processor.model.Model;
import com.sun.xml.rpc.processor.model.ModelProperties;
import com.sun.xml.rpc.processor.model.Operation;
import com.sun.xml.rpc.processor.model.Parameter;
import com.sun.xml.rpc.processor.model.Port;
import com.sun.xml.rpc.processor.model.Request;
import com.sun.xml.rpc.processor.model.Response;
import com.sun.xml.rpc.processor.model.Service;
import com.sun.xml.rpc.processor.model.java.JavaException;
import com.sun.xml.rpc.processor.util.GeneratedFileInfo;
import com.sun.xml.rpc.processor.util.ProcessorEnvironment;
import com.sun.xml.rpc.soap.SOAPConstantsFactory;
import com.sun.xml.rpc.soap.SOAPVersion;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import com.sun.xml.rpc.wsdl.document.Binding;
import com.sun.xml.rpc.wsdl.document.BindingFault;
import com.sun.xml.rpc.wsdl.document.BindingInput;
import com.sun.xml.rpc.wsdl.document.BindingOperation;
import com.sun.xml.rpc.wsdl.document.BindingOutput;
import com.sun.xml.rpc.wsdl.document.Definitions;
import com.sun.xml.rpc.wsdl.document.Input;
import com.sun.xml.rpc.wsdl.document.MessagePart;
import com.sun.xml.rpc.wsdl.document.OperationStyle;
import com.sun.xml.rpc.wsdl.document.Output;
import com.sun.xml.rpc.wsdl.document.PortType;
import com.sun.xml.rpc.wsdl.document.WSDLDocument;
import com.sun.xml.rpc.wsdl.document.schema.SchemaKinds;
import com.sun.xml.rpc.wsdl.document.soap.SOAP12Constants;
import com.sun.xml.rpc.wsdl.document.soap.SOAPAddress;
import com.sun.xml.rpc.wsdl.document.soap.SOAPBinding;
import com.sun.xml.rpc.wsdl.document.soap.SOAPBody;
import com.sun.xml.rpc.wsdl.document.soap.SOAPConstants;
import com.sun.xml.rpc.wsdl.document.soap.SOAPFault;
import com.sun.xml.rpc.wsdl.document.soap.SOAPOperation;
import com.sun.xml.rpc.wsdl.document.soap.SOAPStyle;
import com.sun.xml.rpc.wsdl.document.soap.SOAPUse;
import com.sun.xml.rpc.wsdl.framework.DuplicateEntityException;
import com.sun.xml.rpc.wsdl.parser.Constants;
import com.sun.xml.rpc.wsdl.parser.WSDLWriter;

/**
 *
 * @author JAX-RPC Development Team
 */
public class WSDLGenerator implements Constants, ProcessorAction {

    private com.sun.xml.rpc.soap.SOAPWSDLConstants soapWSDLConstants = null;
    private SOAPVersion soapVer = SOAPVersion.SOAP_11;

    private File destDir;
    private ProcessorEnvironment env;
    private Model model;
    private Properties options = null;

    public WSDLGenerator() {
        this(SOAPVersion.SOAP_11);
    }

    public WSDLGenerator(SOAPVersion ver) {
        init(ver);
        destDir = null;
        env = null;
        model = null;
    }

    private void init(SOAPVersion ver) {
        soapWSDLConstants = SOAPConstantsFactory.getSOAPWSDLConstants(ver);
        this.soapVer = ver;
    }

    public void perform(
        Model model,
        Configuration config,
        Properties properties) {
        this.perform(model, config, properties, SOAPVersion.SOAP_11);
    }

    public void perform(
        Model model,
        Configuration config,
        Properties properties,
        SOAPVersion ver) {
        ProcessorEnvironment env =
            (ProcessorEnvironment) config.getEnvironment();
        String key = ProcessorOptions.NONCLASS_DESTINATION_DIRECTORY_PROPERTY;
        String dirPath = properties.getProperty(key);
        File destDir = new File(dirPath);

        WSDLGenerator generator =
            new WSDLGenerator(env, properties, destDir, model, ver);

        generator.doGeneration();
    }

    private WSDLGenerator(
        ProcessorEnvironment env,
        Properties properties,
        File destDir,
        Model model) {
        this(env, properties, destDir, model, SOAPVersion.SOAP_11);
    }

    private WSDLGenerator(
        ProcessorEnvironment env,
        Properties properties,
        File destDir,
        Model model,
        SOAPVersion ver) {
        init(ver);
        this.env = env;
        this.model = model;
        this.destDir = destDir;
        this.options = properties;
    }

    private void doGeneration() {
        try {
            doGeneration(model);
        } catch (Exception e) {
            throw new GeneratorException(
                "generator.nestedGeneratorError",
                new LocalizableExceptionAdapter(e));
        }
    }

    private void doGeneration(Model model) throws Exception {

        String modelerName =
            (String) model.getProperty(ModelProperties.PROPERTY_MODELER_NAME);
        //TODO: checking modelername with the hardcoded WSDLModeler class name. Requres some better way to do it!
        if (modelerName != null
            && modelerName.equals(ModelProperties.WSDL_MODELER_NAME)) {
            //modelerName.equals("com.sun.xml.rpc.processor.modeler.wsdl.WSDLModeler")) {

            // do not generate a WSDL if the model was produced by the WSDL modeler
            // we should use the original WSDL document instead
            return;
        }

        File wsdlFile =
            new File(destDir, model.getName().getLocalPart() + ".wsdl");

        WSDLDocument document = generateDocument(model);

        try {
            WSDLWriter writer = new WSDLWriter();
            FileOutputStream fos = new FileOutputStream(wsdlFile);

            /* adding the file name and its type */
            GeneratedFileInfo fi = new GeneratedFileInfo();
            fi.setFile(wsdlFile);
            fi.setType(GeneratorConstants.FILE_TYPE_WSDL);
            env.addGeneratedFile(fi);

            writer.write(document, fos);
            fos.close();
        } catch (IOException e) {
            fail("generator.cant.write", wsdlFile.toString());
        }
    }

    private WSDLDocument generateDocument(Model model) throws Exception {
        WSDLDocument document = new WSDLDocument();
        Definitions definitions = new Definitions(document);
        definitions.setName(model.getName().getLocalPart());
        definitions.setTargetNamespaceURI(model.getTargetNamespaceURI());
        document.setDefinitions(definitions);

        // TODO - most of the following methods visit the model to collect
        //        the information they need; perhaps we can reorganize
        //        them a little bit and avoid at least some of the visits

        generateTypes(model, document);
        generateMessages(model, definitions);
        generatePortTypes(model, definitions);
        generateBindings(model, definitions);
        generateServices(model, definitions);

        return document;
    }

    private void generateTypes(Model model, WSDLDocument document)
        throws Exception {
        //WSDLTypeGenerator typeGenerator = new WSDLTypeGenerator(model, document);
        WSDLTypeGenerator typeGenerator =
            new WSDLTypeGenerator(model, document, options, this.soapVer);
        typeGenerator.run();
    }

    protected String getSOAPEncodingNamespace(Port port) {
        if (port.getSOAPVersion().equals(SOAPVersion.SOAP_12.toString()))
            return SOAP12Constants.NS_SOAP_ENCODING;
        else
            return SOAPConstants.NS_SOAP_ENCODING;
    }

    protected String getSOAPTransportHttpURI(Port port) {
        if (port.getSOAPVersion().equals(SOAPVersion.SOAP_12.toString()))
            return SOAP12Constants.URI_SOAP_TRANSPORT_HTTP;
        else
            return SOAPConstants.URI_SOAP_TRANSPORT_HTTP;
    }

    private void generateMessages(Model model, Definitions definitions)
        throws Exception {
        for (Iterator services = model.getServices(); services.hasNext();) {
            Service service = (Service) services.next();

            for (Iterator ports = service.getPorts(); ports.hasNext();) {
                Port port = (Port) ports.next();

                PortType wsdlPortType = new PortType(definitions);
                wsdlPortType.setName(getWSDLPortTypeName(port));

                for (Iterator operations = port.getOperations();
                    operations.hasNext();
                    ) {
                    Operation operation = (Operation) operations.next();

                    String localOperationName =
                        operation.getName().getLocalPart();

                    // NOTE - this code assumes that all parameters go into the body
                    // TODO - fix it
                    Request request = operation.getRequest();
                    com.sun.xml.rpc.wsdl.document.Message wsdlRequestMessage =
                        new com.sun.xml.rpc.wsdl.document.Message(definitions);
                    wsdlRequestMessage.setName(
                        getWSDLInputMessageName(operation));
                    fillInMessageParts(
                        request,
                        wsdlRequestMessage,
                        true,
                        operation.getStyle());
                    definitions.add(wsdlRequestMessage);

                    Response response = operation.getResponse();
                    if (response != null) {
                        com
                            .sun
                            .xml
                            .rpc
                            .wsdl
                            .document
                            .Message wsdlResponseMessage =
                            new com.sun.xml.rpc.wsdl.document.Message(
                                definitions);
                        wsdlResponseMessage.setName(
                            getWSDLOutputMessageName(operation));
                        fillInMessageParts(
                            response,
                            wsdlResponseMessage,
                            false,
                            operation.getStyle());
                        definitions.add(wsdlResponseMessage);
                    }

                    Set faultSet =
                        new TreeSet(new GeneratorUtil.FaultComparator());
                    faultSet.addAll(operation.getFaultsSet());
                    for (Iterator faults = faultSet.iterator();
                        faults.hasNext();
                        ) {
                        Fault fault = (Fault) faults.next();

                        com.sun.xml.rpc.wsdl.document.Message wsdlFaultMessage =
                            new com.sun.xml.rpc.wsdl.document.Message(
                                definitions);
                        wsdlFaultMessage.setName(
                            getWSDLFaultMessageName(fault));
                        MessagePart part = new MessagePart();
                        part.setName(fault.getBlock().getName().getLocalPart());
                        JavaException javaException = fault.getJavaException();
                        AbstractType type = fault.getBlock().getType();
                        if (type.isSOAPType()) {
                            part.setDescriptorKind(SchemaKinds.XSD_TYPE);
                            if (fault.getSubfaults() != null) {
                                QName ownerName =
                                    ((AbstractType) javaException.getOwner())
                                        .getName();
                                part.setDescriptor(ownerName);
                            } else {
                                part.setDescriptor(type.getName());
                            }
                        } else if (type.isLiteralType()) {
                            part.setDescriptorKind(SchemaKinds.XSD_ELEMENT);
                            part.setDescriptor(fault.getElementName());
                        }
                        wsdlFaultMessage.add(part);

                        try {
                            definitions.add(wsdlFaultMessage);
                        } catch (DuplicateEntityException e) {
                            // don't worry about it right now
                            // TODO - should we let this exception through?
                        }
                    }
                }
            }
        }
    }

    private void fillInMessageParts(
        Message message,
        com.sun.xml.rpc.wsdl.document.Message wsdlMessage,
        boolean isRequest,
        SOAPStyle style)
        throws Exception {
        if (message == null) {
            // one-way operation
            return;
        }
        if (style == SOAPStyle.RPC) {
            for (Iterator parameters = message.getParameters();
                parameters.hasNext();
                ) {
                Parameter parameter = (Parameter) parameters.next();
                MessagePart part = new MessagePart();
                part.setName(parameter.getName());
                AbstractType type = parameter.getType();
                if (type.getName() == null) {
                    // a void return type results in a dummy type in the model
                    continue;
                }
                if (type.isSOAPType() || style == SOAPStyle.RPC) {
                    part.setDescriptorKind(SchemaKinds.XSD_TYPE);
                    part.setDescriptor(type.getName());
                } else if (type.isLiteralType()) {
                    part.setDescriptorKind(SchemaKinds.XSD_ELEMENT);
                    part.setDescriptor(type.getName());
                }
                wsdlMessage.add(part);
            }
        } else {
            // body is literal
            Iterator iter = message.getBodyBlocks();
            if (iter.hasNext()) {
                Block bodyBlock = (Block) iter.next();
                MessagePart part = new MessagePart();
                part.setName(
                    isRequest
                        ? PART_NAME_LITERAL_REQUEST_WRAPPER
                        : PART_NAME_LITERAL_RESPONSE_WRAPPER);
                part.setDescriptorKind(SchemaKinds.XSD_ELEMENT);
                part.setDescriptor(bodyBlock.getName());
                wsdlMessage.add(part);
            }
        }
    }

    private void generatePortTypes(Model model, Definitions definitions)
        throws Exception {

        for (Iterator services = model.getServices(); services.hasNext();) {
            Service service = (Service) services.next();

            for (Iterator ports = service.getPorts(); ports.hasNext();) {
                Port port = (Port) ports.next();

                PortType wsdlPortType = new PortType(definitions);
                wsdlPortType.setName(getWSDLPortTypeName(port));

                for (Iterator operations = port.getOperations();
                    operations.hasNext();
                    ) {
                    Operation operation = (Operation) operations.next();

                    String localOperationName =
                        operation.getName().getLocalPart();

                    com.sun.xml.rpc.wsdl.document.Operation wsdlOperation =
                        new com.sun.xml.rpc.wsdl.document.Operation();
                    wsdlOperation.setName(localOperationName);
                    wsdlOperation.setStyle(OperationStyle.REQUEST_RESPONSE);

                    // fix for bug 4844538
                    if (operation.getStyle().equals(SOAPStyle.RPC)
                        && operation.getResponse() != null) {
                        // no paramOrder for one way
                        String paramOrder = "";
                        Iterator parameters =
                            operation.getRequest().getParameters();
                        Parameter parameter;
                        for (int i = 0; parameters.hasNext(); i++) {
                            if (i > 0)
                                paramOrder += " ";
                            parameter = (Parameter) parameters.next();
                            paramOrder += parameter.getName();
                        }
                        wsdlOperation.setParameterOrder(paramOrder);
                    }

                    Input input = new Input();
                    input.setMessage(
                        new QName(
                            model.getTargetNamespaceURI(),
                            getWSDLInputMessageName(operation)));
                    wsdlOperation.setInput(input);

                    if (getWSDLOutputMessageName(operation) != null) {
                        Output output = new Output();
                        output.setMessage(
                            new QName(
                                model.getTargetNamespaceURI(),
                                getWSDLOutputMessageName(operation)));
                        wsdlOperation.setOutput(output);
                    }

                    Set faultSet =
                        new TreeSet(new GeneratorUtil.FaultComparator());
                    faultSet.addAll(operation.getFaultsSet());
                    for (Iterator faults = faultSet.iterator();
                        faults.hasNext();
                        ) {
                        Fault fault = (Fault) faults.next();

                        com.sun.xml.rpc.wsdl.document.Fault wsdlFault =
                            new com.sun.xml.rpc.wsdl.document.Fault();
                        wsdlFault.setName(fault.getName());
                        wsdlFault.setMessage(
                            new QName(
                                model.getTargetNamespaceURI(),
                                getWSDLFaultMessageName(fault)));
                        wsdlOperation.addFault(wsdlFault);
                    }

                    wsdlPortType.add(wsdlOperation);
                }

                definitions.add(wsdlPortType);
            }
        }
    }

    private void generateBindings(Model model, Definitions definitions)
        throws Exception {
        for (Iterator services = model.getServices(); services.hasNext();) {
            Service service = (Service) services.next();

            for (Iterator ports = service.getPorts(); ports.hasNext();) {
                Port port = (Port) ports.next();

                // try to determine default style
                boolean isMixed = false;
                SOAPStyle defaultStyle = null;
                for (Iterator operations = port.getOperations();
                    operations.hasNext();
                    ) {
                    Operation operation = (Operation) operations.next();

                    if (operation.getStyle() == null) {
                        operation.setStyle(SOAPStyle.RPC);
                    }

                    if (defaultStyle == null) {
                        defaultStyle = operation.getStyle();
                    } else {
                        if (defaultStyle != operation.getStyle()) {
                            isMixed = true;
                        }
                    }
                }

                String localPortName = port.getName().getLocalPart();
                Binding wsdlBinding = new Binding(definitions);
                wsdlBinding.setName(getWSDLBindingName(port));
                wsdlBinding.setPortType(
                    new QName(
                        model.getTargetNamespaceURI(),
                        getWSDLPortTypeName(port)));
                SOAPBinding soapBinding = new SOAPBinding();
                if (defaultStyle != null && !isMixed) {
                    soapBinding.setStyle(defaultStyle);
                }
                soapBinding.setTransport(getSOAPTransportHttpURI(port));
                wsdlBinding.addExtension(soapBinding);

                for (Iterator operations = port.getOperations();
                    operations.hasNext();
                    ) {
                    Operation operation = (Operation) operations.next();

                    BindingOperation wsdlOperation = new BindingOperation();
                    wsdlOperation.setName(operation.getName().getLocalPart());
                    wsdlOperation.setStyle(OperationStyle.REQUEST_RESPONSE);
                    SOAPOperation soapOperation = new SOAPOperation();
                    soapOperation.setSOAPAction(operation.getSOAPAction());
                    if (!operation.getStyle().equals(defaultStyle)) {
                        soapOperation.setStyle(operation.getStyle());
                    }
                    wsdlOperation.addExtension(soapOperation);

                    Request request = operation.getRequest();
                    BindingInput input = new BindingInput();
                    SOAPBody soapBody = new SOAPBody();
                    soapBody.setUse(operation.getUse());
                    if (operation.getUse() == SOAPUse.ENCODED)
                        soapBody.setEncodingStyle(
                            getSOAPEncodingNamespace(port));
                    if (operation.getStyle() == SOAPStyle.RPC) {
                        soapBody.setNamespace(model.getTargetNamespaceURI());
                    }
                    input.addExtension(soapBody);
                    wsdlOperation.setInput(input);

                    Response response = operation.getResponse();
                    if (response != null) {
                        BindingOutput output = new BindingOutput();
                        soapBody = new SOAPBody();
                        soapBody.setUse(operation.getUse());
                        if (operation.getUse() == SOAPUse.ENCODED)
                            soapBody.setEncodingStyle(
                                getSOAPEncodingNamespace(port));
                        if (operation.getStyle() == SOAPStyle.RPC) {
                            soapBody.setNamespace(
                                model.getTargetNamespaceURI());
                        }
                        output.addExtension(soapBody);
                        wsdlOperation.setOutput(output);
                    }

                    Set faultSet =
                        new TreeSet(new GeneratorUtil.FaultComparator());
                    faultSet.addAll(operation.getFaultsSet());
                    for (Iterator faults = faultSet.iterator();
                        faults.hasNext();
                        ) {
                        Fault fault = (Fault) faults.next();
                        BindingFault bindingFault = new BindingFault();
                        bindingFault.setName(fault.getName());
                        SOAPFault soapFault = new SOAPFault();
                        soapFault.setName(fault.getName());
                        if (fault.getBlock().getType().isSOAPType()) {
                            soapFault.setUse(SOAPUse.ENCODED);
                            soapFault.setEncodingStyle(
                                getSOAPEncodingNamespace(port));
                            soapFault.setNamespace(
                                model.getTargetNamespaceURI());
                        } else {
                            soapFault.setUse(SOAPUse.LITERAL);
                        }
                        bindingFault.addExtension(soapFault);
                        wsdlOperation.addFault(bindingFault);
                    }

                    wsdlBinding.add(wsdlOperation);
                }

                definitions.add(wsdlBinding);
            }
        }
    }

    private void generateServices(Model model, Definitions definitions)
        throws Exception {

        for (Iterator services = model.getServices(); services.hasNext();) {
            Service service = (Service) services.next();
            com.sun.xml.rpc.wsdl.document.Service wsdlService =
                new com.sun.xml.rpc.wsdl.document.Service(definitions);
            wsdlService.setName(service.getName().getLocalPart());
            for (Iterator ports = service.getPorts(); ports.hasNext();) {
                Port port = (Port) ports.next();
                String localPortName = port.getName().getLocalPart();
                com.sun.xml.rpc.wsdl.document.Port wsdlPort =
                    new com.sun.xml.rpc.wsdl.document.Port(definitions);
                wsdlPort.setName(getWSDLPortName(port));
                wsdlPort.setBinding(
                    new QName(
                        model.getTargetNamespaceURI(),
                        getWSDLBindingName(port)));
                SOAPAddress soapAddress = new SOAPAddress();
                soapAddress.setLocation(
                    port.getAddress() == null
                        ? "REPLACE_WITH_ACTUAL_URL"
                        : port.getAddress());
                wsdlPort.addExtension(soapAddress);
                wsdlService.add(wsdlPort);
            }

            definitions.add(wsdlService);
        }
    }

    private String getWSDLBaseName(Port port) {
        return port.getName().getLocalPart();
    }

    private String getWSDLPortName(Port port) {
        QName value =
            (QName) port.getProperty(ModelProperties.PROPERTY_WSDL_PORT_NAME);
        if (value != null) {
            return value.getLocalPart();
        } else {
            return getWSDLBaseName(port) + "Port";
        }
    }

    private String getWSDLBindingName(Port port) {
        QName value =
            (QName) port.getProperty(
                ModelProperties.PROPERTY_WSDL_BINDING_NAME);
        if (value != null) {
            return value.getLocalPart();
        } else {
            return getWSDLBaseName(port) + "Binding";
        }
    }

    private String getWSDLPortTypeName(Port port) {
        QName value =
            (QName) port.getProperty(
                ModelProperties.PROPERTY_WSDL_PORT_TYPE_NAME);
        if (value != null) {
            return value.getLocalPart();
        } else {
            return port.getName().getLocalPart();
        }
    }

    private String getWSDLInputMessageName(Operation operation) {
        QName value =
            (QName) operation.getRequest().getProperty(
                ModelProperties.PROPERTY_WSDL_MESSAGE_NAME);
        if (value != null) {
            return value.getLocalPart();
        } else {
            return operation.getName().getLocalPart();
        }
    }

    private String getWSDLOutputMessageName(Operation operation) {
        if (operation.getResponse() == null) {
            // one way operation
            return null;
        }
        QName value =
            (QName) operation.getResponse().getProperty(
                ModelProperties.PROPERTY_WSDL_MESSAGE_NAME);
        if (value != null) {
            return value.getLocalPart();
        } else {
            return operation.getName().getLocalPart() + "Response";
        }
    }

    private String getWSDLFaultMessageName(Fault fault) {
        QName value =
            (QName) fault.getProperty(
                ModelProperties.PROPERTY_WSDL_MESSAGE_NAME);
        if (value != null) {
            return value.getLocalPart();
        } else {
            return fault.getName();
        }
    }

    protected void fail(String key, String arg) {
        throw new GeneratorException(key, arg);
    }

    protected static final String PART_NAME_LITERAL_REQUEST_WRAPPER =
        "parameters";
    protected static final String PART_NAME_LITERAL_RESPONSE_WRAPPER = "result";
}
