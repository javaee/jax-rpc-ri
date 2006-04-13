/*
 * $Id: RmiModeler.java,v 1.2 2006-04-13 01:31:19 ofung Exp $
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

package com.sun.xml.rpc.processor.modeler.rmi;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.ProcessorOptions;
import com.sun.xml.rpc.processor.config.ImportedDocumentInfo;
import com.sun.xml.rpc.processor.config.NamespaceMappingInfo;
import com.sun.xml.rpc.processor.config.NamespaceMappingRegistryInfo;
import com.sun.xml.rpc.processor.config.RmiInterfaceInfo;
import com.sun.xml.rpc.processor.config.RmiModelInfo;
import com.sun.xml.rpc.processor.config.TypeMappingRegistryInfo;
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
import com.sun.xml.rpc.processor.model.java.JavaInterface;
import com.sun.xml.rpc.processor.model.java.JavaMethod;
import com.sun.xml.rpc.processor.model.java.JavaParameter;
import com.sun.xml.rpc.processor.model.java.JavaStructureMember;
import com.sun.xml.rpc.processor.model.java.JavaStructureType;
import com.sun.xml.rpc.processor.model.literal.LiteralAttributeMember;
import com.sun.xml.rpc.processor.model.literal.LiteralElementMember;
import com.sun.xml.rpc.processor.model.literal.LiteralSequenceType;
import com.sun.xml.rpc.processor.model.literal.LiteralStructuredType;
import com.sun.xml.rpc.processor.model.literal.LiteralType;
import com.sun.xml.rpc.processor.model.soap.RPCRequestOrderedStructureType;
import com.sun.xml.rpc.processor.model.soap.RPCResponseStructureType;
import com.sun.xml.rpc.processor.model.soap.SOAPStructureMember;
import com.sun.xml.rpc.processor.model.soap.SOAPStructureType;
import com.sun.xml.rpc.processor.model.soap.SOAPType;
import com.sun.xml.rpc.processor.modeler.Modeler;
import com.sun.xml.rpc.processor.modeler.ModelerException;
import com.sun.xml.rpc.processor.util.ProcessorEnvironment;
import com.sun.xml.rpc.processor.util.StringUtils;
import com.sun.xml.rpc.soap.SOAPVersion;
import com.sun.xml.rpc.util.ClassNameInfo;
import com.sun.xml.rpc.util.VersionUtil;
import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import com.sun.xml.rpc.util.VersionUtil;
import com.sun.xml.rpc.wsdl.document.soap.SOAPStyle;
import com.sun.xml.rpc.wsdl.document.soap.SOAPUse;

/**
 *
 * @author JAX-RPC Development Team
 */
public class RmiModeler implements RmiConstants, Modeler {

    private final String modelName;
    private final String typeUri;
    private final String wsdlUri;
    private final RmiModelInfo modelInfo;
    private ProcessorEnvironment env;
    private Map structMap;
    private TypeMappingRegistryInfo typeMappingRegistry;
    private Map messageMap;
    private NamespaceMappingRegistryInfo namespaceMappingRegistry;

    // the model being build
    private Model model;
    // set of interfaces excluded from being used as a Value Type
    private static final Set excludedInterfaces = new HashSet();

    private Class remoteExceptionClass = null;
    private Class defHolder = null;
    private Class defRemote = null;

    private RmiTypeModeler rmiTypeModeler_11;
    private RmiTypeModeler rmiTypeModeler_12;
    private LiteralTypeModeler literalTypeModeler;
    private ExceptionModelerBase exceptionModeler_11;
    private ExceptionModelerBase exceptionModeler_12;
    private ExceptionModelerBase literalExceptionModeler;

    private boolean useDocLiteral = false;
    private boolean useWSIBasicProfile = false;
    private boolean useRPCLiteral = false;
    private boolean generateOneWayOperations = false;
    private boolean strictCompliance = false;
    private Properties options;
    private String sourceVersion;

    public RmiModeler(RmiModelInfo rmiModelInfo, Properties options) {
        modelInfo = rmiModelInfo;
        this.options = options;
        modelName = rmiModelInfo.getName();
        typeUri = rmiModelInfo.getTypeNamespaceURI();
        wsdlUri = rmiModelInfo.getTargetNamespaceURI();
        env =
            (ProcessorEnvironment) rmiModelInfo
                .getConfiguration()
                .getEnvironment();
        try {
            remoteExceptionClass =
                RmiUtils.getClassForName(
                    REMOTE_EXCEPTION_CLASSNAME,
                    env.getClassLoader());
            defHolder =
                RmiUtils.getClassForName(
                    HOLDER_CLASSNAME,
                    env.getClassLoader());
            defRemote =
                RmiUtils.getClassForName(
                    REMOTE_CLASSNAME,
                    env.getClassLoader());
        } catch (ClassNotFoundException e) {
            String className = REMOTE_CLASSNAME;
            if (remoteExceptionClass == null)
                className = REMOTE_EXCEPTION_CLASSNAME;
            else if (defHolder == null)
                className = HOLDER_CLASSNAME;

            throw new ModelerException("rmimodeler.class.not.found", className);
        }
        typeMappingRegistry = rmiModelInfo.getTypeMappingRegistry();
        namespaceMappingRegistry = rmiModelInfo.getNamespaceMappingRegistry();
        useDocLiteral =
            Boolean
                .valueOf(
                    options.getProperty(
                        ProcessorOptions.USE_DOCUMENT_LITERAL_ENCODING))
                .booleanValue();
        useWSIBasicProfile =
            Boolean
                .valueOf(
                    options.getProperty(
                        ProcessorOptions.USE_WSI_BASIC_PROFILE))
                .booleanValue();
        useRPCLiteral =
            Boolean
                .valueOf(
                    options.getProperty(
                        ProcessorOptions.USE_RPC_LITERAL_ENCODING))
                .booleanValue();
        generateOneWayOperations =
            Boolean
                .valueOf(
                    options.getProperty(
                        ProcessorOptions.GENERATE_ONE_WAY_OPERATIONS))
                .booleanValue();
        strictCompliance =
            Boolean
                .valueOf(
                    options.getProperty(ProcessorOptions.STRICT_COMPLIANCE))
                .booleanValue();
        /* checking here what encoding was specified */
        if (useDocLiteral && useRPCLiteral) {
            throw new ModelerException(
                "rmimodeler.invalid.encoding",
                new LocalizableExceptionAdapter(
                    new Exception("Both -f:docliteral and -f:rpcliteral specified.")));
        }
        rmiTypeModeler_11 = new RmiTypeModeler(this, env, SOAPVersion.SOAP_11);
        rmiTypeModeler_12 = new RmiTypeModeler(this, env, SOAPVersion.SOAP_12);
        exceptionModeler_11 = getExceptionModeler(rmiTypeModeler_11);
        exceptionModeler_12 = new ExceptionModeler(this, rmiTypeModeler_12);
        literalTypeModeler = new LiteralTypeModeler(this, env);
        literalExceptionModeler = getLiteralExceptionModeler(literalTypeModeler);
        sourceVersion = options.getProperty(ProcessorOptions.JAXRPC_SOURCE_VERSION);
    }

    /**
     * @param modeler
     * @param rmiTypeModeler_11
     * @return
     */
    private ExceptionModelerBase getExceptionModeler(RmiTypeModeler rmiTypeModeler_11) {
        if (VersionUtil
            .isVersion101(
                options.getProperty(ProcessorOptions.JAXRPC_SOURCE_VERSION))) {
            return new ExceptionModeler101(this, rmiTypeModeler_11);
        } else if (
            VersionUtil.isVersion103(
                options.getProperty(ProcessorOptions.JAXRPC_SOURCE_VERSION))) {
            return new ExceptionModeler103(this, rmiTypeModeler_11);
        } else if (
            VersionUtil.isVersion11(
                options.getProperty(ProcessorOptions.JAXRPC_SOURCE_VERSION))) {
            // TODO: rename ExceptionHandler to ExceptionHandler11?
            return new ExceptionModeler(this, rmiTypeModeler_11);
        }
        // TODO: unknown version, return null or default to the latest version?
        // For now, lets default to the latest version(VersionUtil.JAXRPC_VERSION_DEFAULT).
        return new ExceptionModeler(this, rmiTypeModeler_11);
    }

    /**
     * @param modeler
     * @param rmiTypeModeler_11
     * @return
     */
    private ExceptionModelerBase getLiteralExceptionModeler(LiteralTypeModeler typeModeler) {
        String version = options.getProperty(ProcessorOptions.JAXRPC_SOURCE_VERSION);
        if (VersionUtil.isVersion11(version) ||
            VersionUtil.isVersion111(version)) {
            return new LiteralExceptionModeler111(this, typeModeler);
        } 
        // For now, lets default to the latest version(VersionUtil.JAXRPC_VERSION_DEFAULT).
        return new LiteralExceptionModeler(this, typeModeler);
    }

    public ProcessorEnvironment getProcessorEnvironment() {
        return env;
    }

    protected Properties getOptions() {
        return options;
    }

    public TypeMappingRegistryInfo getTypeMappingRegistryInfo() {
        return typeMappingRegistry;
    }

    public NamespaceMappingRegistryInfo getNamespaceMappingRegistryInfo() {
        return namespaceMappingRegistry;
    }

    public Class getDefHolder() {
        return defHolder;
    }

    public Model getModel() {
        return model;
    }

    public boolean isStrictCompliant() {
        return strictCompliance;
    }

    // should only be called after the model has been started to be built
    public String getServicePackage() {
        String servicePackage = "";
        if (model != null) {
            Iterator services = getModel().getServices();
            Service service = null;
            while (services != null && services.hasNext())
                service = (Service) services.next();
            if (service != null)
                servicePackage = service.getJavaInterface().getName();
            int idx = servicePackage.lastIndexOf(".");
            if (idx > 0) {
                servicePackage = servicePackage.substring(0, idx);
            } else {
                servicePackage = "";
            }
        }
        return servicePackage;
    }

    public LiteralElementMember modelTypeLiteral(
        QName elemName,
        String typeUri,
        RmiType type) {
            
        return literalTypeModeler.modelTypeLiteral(elemName, typeUri, type);
    }

    public LiteralSimpleTypeCreator getLieralTypes() {
        return literalTypeModeler.getLiteralTypes();
    }

    protected void addFaultParent(
        Fault fault,
        ExceptionModelerBase exceptionModeler) {
            
        try {
            Class javaClass =
                RmiUtils.getClassForName(
                    fault.getJavaException().getRealName(),
                    env.getClassLoader());
            Fault parentFault;
            javaClass = javaClass.getSuperclass();
            if (javaClass != null
                && !javaClass.getName().equals(EXCEPTION_CLASSNAME)) {
                parentFault =
                    exceptionModeler.modelException(
                        typeUri,
                        wsdlUri,
                        javaClass);
                parentFault.addSubfault(fault);
                parentFault.getJavaException().addSubclass(
                    fault.getJavaException());
                if (parentFault.getJavaException().getOwner()
                    instanceof SOAPStructureType) {
                    (
                        (SOAPStructureType) parentFault
                            .getJavaException()
                            .getOwner())
                            .addSubtype(
                        (SOAPStructureType) fault
                            .getJavaException()
                            .getOwner());
                } else if (
                    parentFault.getJavaException().getOwner()
                        instanceof LiteralStructuredType) {
                    (
                        (LiteralStructuredType) parentFault
                            .getJavaException()
                            .getOwner())
                            .addSubtype(
                        (LiteralStructuredType) fault
                            .getJavaException()
                            .getOwner());
                }

                Block block = parentFault.getBlock();
                block.setType(
                    (AbstractType) parentFault.getJavaException().getOwner());
                addFaultParent(parentFault, exceptionModeler);
                if (parentFault.getJavaException().getOwner()
                    instanceof SOAPStructureType) {
                    markInheritedMembers(
                        (SOAPStructureType) fault.getJavaException().getOwner(),
                        (SOAPStructureType) parentFault
                            .getJavaException()
                            .getOwner());
                } else if (
                    parentFault.getJavaException().getOwner()
                        instanceof LiteralStructuredType) {
                    markInheritedMembers(
                        (LiteralStructuredType) fault
                            .getJavaException()
                            .getOwner(),
                        (LiteralStructuredType) parentFault
                            .getJavaException()
                            .getOwner());
                }
            }
        } catch (ClassNotFoundException e) {
            throw new ModelerException(
                RMI_MODELER_NESTED_RMI_MODELER_ERROR,
                new LocalizableExceptionAdapter(e));
        }
    }

    public static void markInheritedMembers(
        SOAPStructureType type1,
        SOAPStructureType type2) {
            
        Iterator members1 = type1.getMembers();
        SOAPStructureMember member1, member2;
        Iterator members2;
        while (members1.hasNext()) {
            members2 = type2.getMembers();
            member1 = (SOAPStructureMember) members1.next();
            while (members2.hasNext()) {
                member2 = (SOAPStructureMember) members2.next();
                if (membersMatch(member1, member2)) {
                    member1.setInherited(true);
                    member1.getJavaStructureMember().setInherited(true);
                    break;
                }
            }
        }
    }

    public static boolean membersMatch(
        SOAPStructureMember member1,
        SOAPStructureMember member2) {
            
        return (
            member1.getName().equals(member2.getName())
                && member1.getType().equals(member2.getType()));
    }

    public static void markInheritedMembers(
        LiteralStructuredType type1,
        LiteralStructuredType type2) {
            
        Iterator members1 = type1.getElementMembers();
        LiteralElementMember member1, member2;
        Iterator members2;
        while (members1.hasNext()) {
            members2 = type2.getElementMembers();
            member1 = (LiteralElementMember) members1.next();
            while (members2.hasNext()) {
                member2 = (LiteralElementMember) members2.next();
                if (membersMatch(member1, member2)) {
                    member1.setInherited(true);
                    member1.getJavaStructureMember().setInherited(true);
                    break;
                }
            }
        }
        members1 = type1.getAttributeMembers();
        LiteralAttributeMember attr1, attr2;
        while (members1.hasNext()) {
            members2 = type2.getAttributeMembers();
            attr1 = (LiteralAttributeMember) members1.next();
            while (members2.hasNext()) {
                attr2 = (LiteralAttributeMember) members2.next();
                if (membersMatch(attr1, attr2)) {
                    attr1.setInherited(true);
                    attr1.getJavaStructureMember().setInherited(true);
                    break;
                }
            }
        }
    }

    public static boolean membersMatch(
        LiteralElementMember member1,
        LiteralElementMember member2) {
            
        return (
            member1.getName().equals(member2.getName())
                && member1.getType().equals(member2.getType()));
    }

    public static boolean membersMatch(
        LiteralAttributeMember member1,
        LiteralAttributeMember member2) {
            
        return (
            member1.getName().equals(member2.getName())
                && member1.getType().equals(member2.getType()));
    }

    public Model buildModel() {
        log("creating model: " + modelName);
        model = new Model(new QName(null, modelName));
        model.setProperty(
            ModelProperties.PROPERTY_MODELER_NAME,
            this.getClass().getName());
        model.setTargetNamespaceURI(wsdlUri);

        if (useDocLiteral || useWSIBasicProfile || useRPCLiteral) {
            return buildLiteralModel();
        } else {
            return buildEncodedModel();
        }
    }

    private Model buildEncodedModel() {
        try {
            if (typeMappingRegistry != null) {
                for (Iterator iter = typeMappingRegistry.getImportedDocuments();
                    iter.hasNext();
                    ) {
                    model.addImportedDocument(
                        (ImportedDocumentInfo) iter.next());
                }
                String typeSig;
                SOAPType extraType;
                RmiType type;
                for (Iterator iter = typeMappingRegistry.getExtraTypeNames();
                    iter.hasNext();
                    ) {
                    type =
                        RmiType.getRmiType(
                            RmiUtils.getClassForName(
                                (String) iter.next(),
                                env.getClassLoader()));
                    if (isException(env, type)) {
                        Fault fault =
                            exceptionModeler_11.modelException(
                                typeUri,
                                wsdlUri,
                                type.getTypeClass(env.getClassLoader()));
                        addFaultParent(fault, exceptionModeler_11);
                    } else {
                        extraType =
                            rmiTypeModeler_11.modelTypeSOAP(typeUri, type);
                        model.addExtraType(extraType);
                    }
                }
            }
            structMap = new HashMap();
            Service service;
            Iterator interfaces;
            String javaServiceName =
                StringUtils.capitalize(modelInfo.getName());
            log("creating service: " + javaServiceName);
            String serviceInterface;
            if (modelInfo.getJavaPackageName() != null
                && !modelInfo.getJavaPackageName().equals(EMPTY_STRING)) {
                serviceInterface =
                    modelInfo.getJavaPackageName() + DOT + javaServiceName;
            } else {
                serviceInterface = javaServiceName;
            }
            // take care of inner classes
            service =
                new Service(
                    new QName(wsdlUri, javaServiceName),
                    new JavaInterface(
                        serviceInterface,
                        serviceInterface + IMPL));
            model.addService(service);
            interfaces = modelInfo.getInterfaces();
            RmiInterfaceInfo interfaceInfo;
            while (interfaces.hasNext()) {
                interfaceInfo = (RmiInterfaceInfo) interfaces.next();
                service.addPort(modelPort(interfaceInfo));
            }
            rmiTypeModeler_11.modelSubclasses(typeUri);
            //            rmiTypeModeler_12.modelSubclasses(typeUri);
            messageMap = null;
        } catch (ModelerException e) {
            throw e;
        } catch (JAXRPCExceptionBase e) {
            throw new ModelerException(e);
        } catch (Exception e) {
            throw new ModelerException(new LocalizableExceptionAdapter(e));
        }
        structMap = null;
        return model;
    }

    private Model buildLiteralModel() {
        try {
            if (typeMappingRegistry != null) {
                for (Iterator iter = typeMappingRegistry.getImportedDocuments();
                    iter.hasNext();
                    ) {
                    model.addImportedDocument(
                        (ImportedDocumentInfo) iter.next());
                }
                String typeSig;
                LiteralElementMember member;
                RmiType type;
                for (Iterator iter = typeMappingRegistry.getExtraTypeNames();
                    iter.hasNext();
                    ) {
                    type =
                        RmiType.getRmiType(
                            RmiUtils.getClassForName(
                                (String) iter.next(),
                                env.getClassLoader()));
                    if (isException(env, type)) {
                        Fault fault =
                            literalExceptionModeler.modelException(
                                typeUri,
                                wsdlUri,
                                type.getTypeClass(env.getClassLoader()));
                        addFaultParent(fault, literalExceptionModeler);
                    } else {
                        QName elemName = new QName(type.getTypeSignature());
                        member =
                            literalTypeModeler.modelTypeLiteral(
                                elemName,
                                typeUri,
                                type);
                        model.addExtraType(member.getType());
                    }
                }
            }
            structMap = new HashMap();
            Service service;
            Iterator interfaces;
            String javaServiceName =
                StringUtils.capitalize(modelInfo.getName());
            log("creating service: " + javaServiceName);
            String serviceInterface;
            if (modelInfo.getJavaPackageName() != null
                && !modelInfo.getJavaPackageName().equals(EMPTY_STRING)) {
                serviceInterface =
                    modelInfo.getJavaPackageName() + DOT + javaServiceName;
            } else {
                serviceInterface = javaServiceName;
            }
            // take care of inner classes
            service =
                new Service(
                    new QName(wsdlUri, javaServiceName),
                    new JavaInterface(
                        serviceInterface,
                        serviceInterface + IMPL));
            model.addService(service);
            interfaces = modelInfo.getInterfaces();
            RmiInterfaceInfo interfaceInfo;
            while (interfaces.hasNext()) {
                interfaceInfo = (RmiInterfaceInfo) interfaces.next();
                service.addPort(modelPort(interfaceInfo));
            }
            literalTypeModeler.modelSubclasses(typeUri);
            if (useDocLiteral) {
                checkForDocLiteralNameClashes(model);
            }
            messageMap = null;
        } catch (ModelerException e) {
            throw e;
        } catch (JAXRPCExceptionBase e) {
            throw new ModelerException(e);
        } catch (Exception e) {
            throw new ModelerException(new LocalizableExceptionAdapter(e));
        }
        structMap = null;
        return model;
    }

    private void checkForDocLiteralNameClashes(Model model)
        throws ModelerException {
            
        Iterator services = model.getServices();
        Operation operation;
        while (services.hasNext()) {
            Iterator ports = ((Service) services.next()).getPorts();
            while (ports.hasNext()) {
                Iterator operations = ((Port) ports.next()).getOperations();
                while (operations.hasNext()) {
                    operation = (Operation) operations.next();
                    checkForMessageNameClash(operation.getRequest());
                    checkForMessageNameClash(operation.getResponse());
                }
            }
        }
    }

    private void checkForMessageNameClash(Message message)
        throws ModelerException {
            
        if (message != null) {
            Block block;
            Iterator bodyBlocks = message.getBodyBlocks();
            while (bodyBlocks.hasNext()) {
                block = (Block) bodyBlocks.next();
                if (literalTypeModeler
                    .nameClashes(block.getType().getName().getLocalPart()))
                    throw new ModelerException(
                        "rmimodeler.operation.name.clashes.with.type.name",
                        block.getType().getName().getLocalPart());

            }
        }
    }

    public static boolean isException(ProcessorEnvironment env, RmiType type) {
        try {
            if (type.getTypeCode() != TC_CLASS)
                return false;
            Class typeClass = type.getTypeClass(env.getClassLoader());
            while (typeClass != null) {
                if (typeClass.getName().equals(EXCEPTION_CLASSNAME))
                    return true;
                typeClass = typeClass.getSuperclass();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private Port modelPort(RmiInterfaceInfo interfaceInfo) {
        Port port = null;
        try {
            String implClassName =
                RmiUtils.getRealName(
                    interfaceInfo.getName(),
                    env.getClassLoader());
            Class def =
                RmiUtils.getClassForName(implClassName, env.getClassLoader());
            validateEndpointClass(def);
            port = processInterface(def, implClassName, interfaceInfo);
        } catch (ClassNotFoundException e) {
            throw new ModelerException(
                RMI_MODELER_NESTED_RMI_MODELER_ERROR,
                new LocalizableExceptionAdapter(e));
        }
        return port;
    }

    private void validateEndpointClass(Class endpointClass)
        throws ModelerException {
        if (!endpointClass.isInterface()) {
            throw new ModelerException(
                "rmimodeler.service.endpoint.must.be.interface",
                endpointClass.getName());
        }
        if (!defRemote.isAssignableFrom(endpointClass)) {
            throw new ModelerException(
                "rmimodeler.must.implement.remote",
                endpointClass.getName());
        }
    }

    private Port processInterface(
        Class endpointClass,
        String intfName,
        RmiInterfaceInfo interfaceInfo) {

        messageMap = new HashMap();
        String servant = interfaceInfo.getServantName();
        log(
            "creating port: "
                + ClassNameInfo.replaceInnerClassSym(endpointClass.getName()));
        String portName =
            ClassNameInfo.getName(
                endpointClass.getName().replace(
                    SIGC_INNERCLASS,
                    SIGC_UNDERSCORE));
        String packageName = endpointClass.getPackage().getName();
        String namespace = wsdlUri;
        Port port = new Port(new QName(namespace, portName));
        port.setSOAPVersion(interfaceInfo.getSOAPVersion());
        JavaInterface javaInterface = new JavaInterface(intfName, servant);
        port.setJavaInterface(javaInterface);
        Class[] remoteInterfaces = endpointClass.getInterfaces();
        String interfaceName;
        for (int i = 0; i < remoteInterfaces.length; i++) {
            interfaceName =
                env.getNames().removeCharacter(
                    ' ',
                    remoteInterfaces[i].getName());
            if (!interfaceName.equals(javaInterface.getName()))
                javaInterface.addInterface(interfaceName);
        }

        Iterator methods =
            sortMethods(endpointClass, endpointClass.getMethods());
        Method method;
        while (methods.hasNext()) {
            method = (Method) methods.next();
            if (!verifyRemoteMethod(method)) {
                throw new ModelerException(
                    "rmimodeler.must.throw.remoteexception",
                    new Object[] { endpointClass.getName(), method.getName()});
            }
            port.addOperation(
                processMethod(interfaceInfo, endpointClass, method, namespace));
        }

        port.setClientHandlerChainInfo(
            interfaceInfo.getClientHandlerChainInfo());
        port.setServerHandlerChainInfo(
            interfaceInfo.getServerHandlerChainInfo());

        // generate stub and tie class names
        String stubClassName = env.getNames().stubFor(port, null);
        String tieClassName =
            env.getNames().tieFor(
                port,
                env.getNames().getSerializerNameInfix());

        port.setProperty(
            ModelProperties.PROPERTY_STUB_CLASS_NAME,
            stubClassName);
        port.setProperty(ModelProperties.PROPERTY_TIE_CLASS_NAME, tieClassName);

        // set WSDL-related properties
        port.setProperty(
            ModelProperties.PROPERTY_WSDL_PORT_NAME,
            getWSDLPortName(portName));
        port.setProperty(
            ModelProperties.PROPERTY_WSDL_PORT_TYPE_NAME,
            getWSDLPortTypeName(portName));
        port.setProperty(
            ModelProperties.PROPERTY_WSDL_BINDING_NAME,
            getWSDLBindingName(portName));

        messageMap = null;
        return port;
    }

    private Iterator sortMethods(Class endpointClass, Method[] methods) {
        //return unsorted array of methods to have 101 source compatibility.
        if (VersionUtil
            .isVersion101(
                options.getProperty(ProcessorOptions.JAXRPC_SOURCE_VERSION))) {
            return Arrays.asList(methods).iterator();
        }
        Set sortedMethods = new TreeSet(new MethodComparator());
        Set methodSigs = new HashSet();
        String sig;
        for (int i = 0; i < methods.length; i++) {
            sig = getMethodSig(methods[i]);
            if (!methodSigs.contains(sig)) {
                sortedMethods.add(methods[i]);
                methodSigs.add(sig);
            }
        }
        return sortedMethods.iterator();
    }

    private boolean verifyRemoteMethod(Method method) {
        Class[] exceptions = method.getExceptionTypes();
        boolean hasRemoteException = false;
        for (int i = 0; i < exceptions.length; i++) {
            if (java
                .rmi
                .RemoteException
                .class
                .isAssignableFrom(exceptions[i])) {
                return true;
            }
        }
        return false;
    }

    private Operation processMethod(
        RmiInterfaceInfo interfaceInfo,
        Class endpointClass,
        Method method,
        String namespaceURI) {
            
        if (useDocLiteral) {
            return processDocumentLiteralMethod(
                interfaceInfo,
                endpointClass,
                method,
                namespaceURI);
        } else if (useWSIBasicProfile || useRPCLiteral) {
            return processRpcLiteralMethod(
                interfaceInfo,
                endpointClass,
                method,
                namespaceURI);
        } else {
            return processRpcEncodedMethod(
                interfaceInfo,
                endpointClass,
                method,
                namespaceURI);
        }
    }

    private Operation processRpcEncodedMethod(
        RmiInterfaceInfo interfaceInfo,
        Class endpointClass,
        Method method,
        String namespaceURI) {

        String portName = ClassNameInfo.getName(endpointClass.getName());
        portName = portName.replace(SIGC_INNERCLASS, SIGC_UNDERSCORE);
        RmiType returnType = RmiType.getRmiType(method.getReturnType());
        RmiType paramTypes[] = getParameterTypes(method);
        String paramNames[] = nameParameters(paramTypes);
        Class[] exceptions = method.getExceptionTypes();
        String messageName = method.getName();
        String operationName = getOperationName(messageName);
        String methodName = method.getName().toString();
        log("creating operation: " + methodName);
        Operation operation = new Operation(new QName(operationName));

        operation.setSOAPAction(getSOAPAction(interfaceInfo, operationName));
        JavaMethod javaMethod = new JavaMethod(methodName);
        javaMethod.setDeclaringClass(method.getDeclaringClass().getName());
        operation.setJavaMethod(javaMethod);
        String packageName = endpointClass.getPackage().getName();
        String typeNamespace = getNamespaceURI(packageName);
        SOAPVersion soapVersion = interfaceInfo.getSOAPVersion();
        RmiTypeModeler rmiTypeModeler =
            soapVersion == SOAPVersion.SOAP_11
                ? rmiTypeModeler_11
                : rmiTypeModeler_12;
        ExceptionModelerBase exceptionModeler =
            soapVersion == SOAPVersion.SOAP_11
                ? exceptionModeler_11
                : exceptionModeler_12;
        if (typeNamespace == null)
            typeNamespace = typeUri;
        if (packageName.length() > 0) {
            packageName = packageName + DOT;
        }

        operation.setStyle(SOAPStyle.RPC);
        operation.setUse(SOAPUse.ENCODED);

        // create response
        SOAPStructureMember member = null;
        JavaStructureMember javaMember = null;
        JavaStructureType javaRespStructure = null;
        SOAPStructureType responseStruct = null;
        JavaParameter javaParameter = null;
        Block responseBlock = null;
        Response response = null;
        boolean hasHolders = false;
        for (int i = 0; i < paramTypes.length && !hasHolders; i++) {
            hasHolders =
                RmiTypeModeler.getHolderValueType(
                    env,
                    defHolder,
                    paramTypes[i])
                    != null;
        }

        // If the method returns void but declares its own exceptions,
        // then it is not a one-way method.
        if (!generateOneWayOperations
            || returnType.getTypeCode() != TC_VOID
            || hasHolders
            || exceptions.length > 1) {
            responseStruct =
                new RPCResponseStructureType(
                    new QName(
                        typeNamespace,
                        env.getNames().getResponseName(operationName)),
                    soapVersion);       
            javaRespStructure =
                new JavaStructureType(
                    getStructName(
                        packageName
                            + portName
                            + UNDERSCORE
                            + methodName
                            + RESPONSE_STRUCT),
                    false,
                    responseStruct);
            responseStruct.setJavaType(javaRespStructure);
            response = new Response();
            SOAPType resultType;
            responseBlock =
                new Block(
                    new QName(
                        namespaceURI,
                        env.getNames().getResponseName(operationName)));
            resultType = rmiTypeModeler.modelTypeSOAP(typeUri, returnType);
            if (returnType.getTypeCode() != TC_VOID) {
                if (soapVersion.equals(SOAPVersion.SOAP_12)) {
                    member =
                        new SOAPStructureMember(
                            new QName(namespaceURI, RESULT),
                            resultType);
                } else {
                    member =
                        new SOAPStructureMember(
                            new QName(null, RESULT),
                            resultType);
                }
                javaMember =
                    new JavaStructureMember(
                        member.getName().getLocalPart(),
                        member.getType().getJavaType(),
                        member,
                        false);
                env.getNames().setJavaStructureMemberMethodNames(javaMember);
                member.setJavaStructureMember(javaMember);
                javaRespStructure.add(javaMember);
                responseStruct.add(member);
            }
            response.addBodyBlock(responseBlock);
            Parameter resultParam = new Parameter(RESULT);
            resultParam.setEmbedded(true);
            resultParam.setType(resultType);
            resultParam.setBlock(responseBlock);
            responseBlock.setType(responseStruct);
            javaParameter =
                new JavaParameter(null, resultType.getJavaType(), resultParam);
            javaMethod.setReturnType(resultType.getJavaType());
            resultParam.setJavaParameter(javaParameter);
            response.addParameter(resultParam);
            response.setProperty(
                ModelProperties.PROPERTY_WSDL_MESSAGE_NAME,
                getWSDLOutputMessageName(
                    portName + UNDERSCORE + operationName));
            operation.setResponse(response);
        } else {
            javaMethod.setReturnType(
                rmiTypeModeler
                    .modelTypeSOAP(typeUri, returnType)
                    .getJavaType());
        }

        SOAPStructureType paramStruct =
            new RPCRequestOrderedStructureType(
                new QName(typeNamespace, operationName),
                soapVersion);        
         JavaStructureType javaStruct =
            new JavaStructureType(
                getStructName(
                    packageName
                        + portName
                        + UNDERSCORE
                        + paramStruct.getName().getLocalPart()
                        + REQUEST_STRUCT),
                false,
                paramStruct);
        paramStruct.setJavaType(javaStruct);
        //  create request
        Request request = new Request();
        // build up body block
        Parameter parameter;
        JavaStructureType javaStructure =
            (JavaStructureType) paramStruct.getJavaType();
        Block block = new Block(new QName(namespaceURI, operationName));
        SOAPType memberType;
        boolean isHolder;
        for (int i = 0; i < paramTypes.length; i++) {
            QName typeName = new QName(null, paramNames[i]);
            memberType = rmiTypeModeler.modelTypeSOAP(typeUri, paramTypes[i]);
            isHolder =
                RmiTypeModeler.getHolderValueType(
                    env,
                    defHolder,
                    paramTypes[i])
                    != null;
            member = new SOAPStructureMember(typeName, memberType);
            javaMember =
                new JavaStructureMember(
                    member.getName().getLocalPart(),
                    member.getType().getJavaType(),
                    member,
                    false);
            env.getNames().setJavaStructureMemberMethodNames(javaMember);
            member.setJavaStructureMember(javaMember);
            javaStructure.add(javaMember);
            paramStruct.add(member);
            parameter = new Parameter(paramNames[i]);
            if (isHolder) {
                javaRespStructure.add(javaMember);
                responseStruct.add(member);
                Parameter responseParam = new Parameter(paramNames[i]);
                responseParam.setEmbedded(true);
                javaParameter =
                    new JavaParameter(
                        paramNames[i],
                        member.getType().getJavaType(),
                        responseParam,
                        true);
                javaParameter.setHolderName(paramTypes[i].toString());
                responseParam.setJavaParameter(javaParameter);
                responseParam.setType(member.getType());
                responseParam.setBlock(responseBlock);
                parameter.setLinkedParameter(responseParam);
                responseParam.setLinkedParameter(parameter);
                response.addParameter(responseParam);
            }
            parameter.setEmbedded(true);
            javaParameter =
                new JavaParameter(
                    paramNames[i],
                    member.getType().getJavaType(),
                    parameter,
                    isHolder);
            if (isHolder)
                javaParameter.setHolderName(paramTypes[i].toString());
            parameter.setJavaParameter(javaParameter);
            parameter.setType(member.getType());
            parameter.setBlock(block);
            javaMethod.addParameter(javaParameter);
            request.addParameter(parameter);
        }
        block.setType(paramStruct);
        request.addBodyBlock(block);
        request.setProperty(
            ModelProperties.PROPERTY_WSDL_MESSAGE_NAME,
            getWSDLInputMessageName(portName + UNDERSCORE + operationName));
        operation.setRequest(request);

        //  create Fault Messages
        if (exceptions.length > 0) {
            for (int i = 0; i < exceptions.length; i++) {
                if (!isRemoteException(env,
                    exceptions[i].getName().toString())) {
                    javaMethod.addException(exceptions[i].getName().toString());
                    if (!exceptions[i]
                        .getName()
                        .toString()
                        .equals(EXCEPTION_CLASSNAME)) {
                        Fault fault =
                            exceptionModeler.modelException(
                                typeUri,
                                wsdlUri,
                                exceptions[i]);
                        fault.setElementName(fault.getBlock().getName());
                        response.addFaultBlock(fault.getBlock());
                        fault.setProperty(
                            ModelProperties.PROPERTY_WSDL_MESSAGE_NAME,
                            getWSDLFaultMessageName(fault.getName()));
                        operation.addFault(fault);
                        addFaultParent(fault, exceptionModeler);
                    }
                }
            }
        }

        return operation;
    }

    private Operation processDocumentLiteralMethod(
        RmiInterfaceInfo interfaceInfo,
        Class endpointClass,
        Method method,
        String namespaceURI) {

        String portName = ClassNameInfo.getName(endpointClass.getName());
        portName = portName.replace(SIGC_INNERCLASS, SIGC_UNDERSCORE);
        RmiType returnType = RmiType.getRmiType(method.getReturnType());
        RmiType paramTypes[] = getParameterTypes(method);
        String paramNames[] = nameParameters(paramTypes);
        Class[] exceptions = method.getExceptionTypes();
        String messageName = method.getName();
        String operationName = getOperationName(messageName);
        String methodName = method.getName().toString();
        log("creating operation: " + methodName);
        Operation operation =
            new Operation(new QName(namespaceURI, operationName));
        operation.setSOAPAction(getSOAPAction(interfaceInfo, operationName));
        JavaMethod javaMethod = new JavaMethod(methodName);
        javaMethod.setDeclaringClass(method.getDeclaringClass().getName());
        operation.setJavaMethod(javaMethod);
        String packageName = endpointClass.getPackage().getName();
        String typeNamespace = getNamespaceURI(packageName);
        if (typeNamespace == null)
            typeNamespace = typeUri;
        if (packageName.length() > 0) {
            packageName = packageName + DOT;
        }
        SOAPVersion soapVersion = interfaceInfo.getSOAPVersion();

        operation.setStyle(SOAPStyle.DOCUMENT);
        operation.setUse(SOAPUse.LITERAL);

        boolean ver111Above = (VersionUtil.compare(sourceVersion,
            VersionUtil.JAXRPC_VERSION_111) >= 0);
        LiteralStructuredType paramStruct = new LiteralSequenceType();
        if (ver111Above) {
           ((LiteralSequenceType)paramStruct).setUnwrapped(true);
        }
        paramStruct.setName(new QName(typeNamespace, operationName));
        // There is a complex-type for this in in the generated WSDL
        //paramStruct.setRequestResponseStruct(true);
        JavaStructureType javaStruct =
            new JavaStructureType(
                getStructName(
                    packageName
                        + portName
                        + UNDERSCORE
                        + paramStruct.getName().getLocalPart()
                        + REQUEST_STRUCT),
                false,
                paramStruct);
        paramStruct.setJavaType(javaStruct);
        //  create request
        Request request = new Request();
        // build up body block
        Parameter parameter;
        Block block =
            new Block(
                new QName(typeNamespace, paramStruct.getName().getLocalPart()));
        LiteralType memberType;
        LiteralElementMember member;
        JavaStructureMember javaMember;
        JavaParameter javaParameter;
        for (int i = 0; i < paramTypes.length; i++) {
            QName elemName = new QName(paramNames[i]);
            member =
                literalTypeModeler.modelTypeLiteral(
                    elemName,
                    typeUri,
                    paramTypes[i]);
            javaMember = member.getJavaStructureMember();
            javaMember.setName(paramNames[i]);
            env.getNames().setJavaStructureMemberMethodNames(javaMember);
            javaStruct.add(javaMember);
            paramStruct.add(member);
            parameter = new Parameter(paramNames[i]);
            parameter.setEmbedded(true);
            javaParameter =
                new JavaParameter(
                    paramNames[i],
                    member.getJavaStructureMember().getType(),
                    parameter,
                    false);
            parameter.setJavaParameter(javaParameter);
            parameter.setType(member.getType());
            parameter.setBlock(block);
            parameter.setProperty(
                ModelProperties.PROPERTY_PARAM_MESSAGE_PART_NAME,
                parameter.getName());
            javaMethod.addParameter(javaParameter);
            request.addParameter(parameter);
        }
        block.setType(paramStruct);
        request.addBodyBlock(block);
        request.setProperty(
            ModelProperties.PROPERTY_WSDL_MESSAGE_NAME,
            getWSDLInputMessageName(portName + UNDERSCORE + operationName));
        operation.setRequest(request);

        // handle response if not void
        QName elemName = new QName(RESULT);
        member =
            literalTypeModeler.modelTypeLiteral(elemName, typeUri, returnType);
        Response response = null;
        boolean genResponsePart = true;
        if (generateOneWayOperations && returnType.getTypeCode() == TC_VOID &&
            exceptions.length <= 1) {
            genResponsePart = false;
        }
        if (genResponsePart) {
            LiteralSequenceType responseStruct = new LiteralSequenceType();
            responseStruct.setName(
                new QName(
                    typeNamespace,
                    env.getNames().getResponseName(operationName)));
            // There is a complex-type for this in in the generated WSDL
            //responseStruct.setRequestResponseStruct(true);
            javaStruct =
                new JavaStructureType(
                    getStructName(
                        packageName
                            + portName
                            + UNDERSCORE
                            + methodName
                            + RESPONSE_STRUCT),
                    false,
                    responseStruct);
            responseStruct.setJavaType(javaStruct);
            response = new Response();
            // build up body block
            block =
                new Block(
                    new QName(
                        typeNamespace,
                        responseStruct.getName().getLocalPart()));
            if (returnType.getTypeCode() != TC_VOID) {
                javaMember = member.getJavaStructureMember();
                javaMember.setName(RESULT);
                javaStruct.add(javaMember);
                responseStruct.add(member);
                //            javaMethod.setReturnType(javaMember.getType());
                Parameter resultParam = new Parameter(RESULT);
                resultParam.setEmbedded(true);
                resultParam.setType(responseStruct);
                resultParam.setBlock(block);
                block.setType(responseStruct);
                response.addBodyBlock(block);
                javaParameter =
                    new JavaParameter(
                        null,
                        block.getType().getJavaType(),
                        resultParam);
                resultParam.setJavaParameter(javaParameter);
                resultParam.setProperty(
                    ModelProperties.PROPERTY_PARAM_MESSAGE_PART_NAME,
                    resultParam.getName());
                response.addParameter(resultParam);
            } else {
                if (ver111Above) {
                    block.setType(responseStruct);
                    response.addBodyBlock(block);
                }
            }
            response.setProperty(
                ModelProperties.PROPERTY_WSDL_MESSAGE_NAME,
                getWSDLOutputMessageName(
                    portName + UNDERSCORE + operationName));
            operation.setResponse(response);
        }

        javaMethod.setReturnType(member.getJavaStructureMember().getType());

        //  create Fault Messages
        if (exceptions.length > 0) {
            for (int i = 0; i < exceptions.length; i++) {
                if (!isRemoteException(env,
                    exceptions[i].getName().toString())) {
                    javaMethod.addException(exceptions[i].getName().toString());
                    if (!exceptions[i]
                        .getName()
                        .toString()
                        .equals(EXCEPTION_CLASSNAME)) {
                        Fault fault =
                            literalExceptionModeler.modelException(
                                typeUri,
                                wsdlUri,
                                exceptions[i]);
                        fault.setElementName(
                            new QName(
                                fault
                                    .getBlock()
                                    .getType()
                                    .getName()
                                    .getNamespaceURI(),
                                fault
                                    .getBlock()
                                    .getType()
                                    .getName()
                                    .getLocalPart()));
                         if (response == null) {
                            response = new Response();
                            operation.setResponse(response);
                        }
                        response.addFaultBlock(fault.getBlock());
                        fault.setProperty(
                            ModelProperties.PROPERTY_WSDL_MESSAGE_NAME,
                            getWSDLFaultMessageName(fault.getName()));
                        operation.addFault(fault);
                        addFaultParent(fault, literalExceptionModeler);
                    }
                }
            }
        }

        return operation;
    }

    private Operation processRpcLiteralMethod(
        RmiInterfaceInfo interfaceInfo,
        Class endpointClass,
        Method method,
        String namespaceURI) {

        String portName = ClassNameInfo.getName(endpointClass.getName());
        portName = portName.replace(SIGC_INNERCLASS, SIGC_UNDERSCORE);
        RmiType returnType = RmiType.getRmiType(method.getReturnType());
        RmiType paramTypes[] = getParameterTypes(method);
        String paramNames[] = nameParameters(paramTypes);
        Class[] exceptions = method.getExceptionTypes();
        String messageName = method.getName();
        String operationName = getOperationName(messageName);
        String methodName = method.getName().toString();
        log("creating operation: " + methodName);
        Operation operation =
            new Operation(new QName(namespaceURI, operationName));
        operation.setSOAPAction(getSOAPAction(interfaceInfo, operationName));
        JavaMethod javaMethod = new JavaMethod(methodName);
        javaMethod.setDeclaringClass(method.getDeclaringClass().getName());
        operation.setJavaMethod(javaMethod);
        String packageName = endpointClass.getPackage().getName();
        String typeNamespace = getNamespaceURI(packageName);
        if (typeNamespace == null)
            typeNamespace = typeUri;
        if (packageName.length() > 0) {
            packageName = packageName + DOT;
        }
        SOAPVersion soapVersion = interfaceInfo.getSOAPVersion();

        operation.setStyle(SOAPStyle.RPC);
        operation.setUse(SOAPUse.LITERAL);

        LiteralElementMember member;
        JavaStructureMember javaMember = null;
        JavaParameter javaParameter;
        JavaStructureType javaRespStructure = null;
        Block responseBlock = null;

        // handle response if not void
        String elemNamespaceURI = null;
        if (soapVersion.equals(SOAPVersion.SOAP_12))
            elemNamespaceURI = typeNamespace;
        QName elemName = new QName(elemNamespaceURI, RESULT);
        member =
            literalTypeModeler.modelTypeLiteral(
                elemName,
                typeUri,
                returnType,
                true,
                true);
        member.setRequired(true);
        Response response = null;
        boolean hasHolders = false;
        for (int i = 0; i < paramTypes.length && !hasHolders; i++) {
            hasHolders =
                LiteralTypeModeler.getHolderValueType(
                    env,
                    defHolder,
                    paramTypes[i])
                    != null;
        }

        LiteralSequenceType responseStruct = null;
        // If the method returns void but declares its own exceptions,
        // then it is not a one-way method.
        if (!generateOneWayOperations
            || returnType.getTypeCode() != TC_VOID
            || hasHolders
            || exceptions.length > 1) {
            //             responseStruct = new RPCResponseStructureType(new QName(typeNamespace,
            //                env.getNames().getResponseName(operationName)), soapVersion);
            responseStruct = new LiteralSequenceType();
            //            responseStruct.setName(member.getType().getName());
            responseStruct.setName(
                new QName(
                    elemNamespaceURI,
                    env.getNames().getResponseName(operationName)));
            responseStruct.setRpcWrapper(true);
            javaRespStructure =
                new JavaStructureType(
                    getStructName(
                        packageName
                            + portName
                            + UNDERSCORE
                            + methodName
                            + RESPONSE_STRUCT),
                    false,
                    responseStruct);
            responseStruct.setJavaType(javaRespStructure);
            response = new Response();
            // build up body block
            responseBlock =
                new Block(
                    new QName(
                        namespaceURI,
                        env.getNames().getResponseName(operationName)));
            javaMember = member.getJavaStructureMember();
            javaMember.setName(RESULT);
            if (returnType.getTypeCode() != TC_VOID) {
                javaRespStructure.add(javaMember);
                responseStruct.add(member);
            }
            Parameter resultParam = new Parameter(RESULT);
            response.addBodyBlock(responseBlock);
            resultParam.setEmbedded(true);
            resultParam.setType(member.getType());
            resultParam.setBlock(responseBlock);
            responseBlock.setType(responseStruct);
            javaParameter =
                new JavaParameter(
                    null,
                    responseStruct.getJavaType(),
                    resultParam);
            javaMethod.setReturnType(javaMember.getType());
            resultParam.setJavaParameter(javaParameter);
            response.addParameter(resultParam);

            response.setProperty(
                ModelProperties.PROPERTY_WSDL_MESSAGE_NAME,
                getWSDLOutputMessageName(
                    portName + UNDERSCORE + operationName));
            operation.setResponse(response);
        } else {
            javaMethod.setReturnType(member.getJavaStructureMember().getType());
        }

        // parameters        
        LiteralStructuredType paramStruct = new LiteralSequenceType();
        paramStruct.setName(new QName(typeNamespace, operationName));
        paramStruct.setRpcWrapper(true);
        JavaStructureType javaStruct =
            new JavaStructureType(
                getStructName(
                    packageName
                        + portName
                        + UNDERSCORE
                        + paramStruct.getName().getLocalPart()
                        + REQUEST_STRUCT),
                false,
                paramStruct);
        paramStruct.setJavaType(javaStruct);
        //  create request
        Request request = new Request();
        // build up body block
        Parameter parameter;
        Block requestBlock =
            new Block(
                new QName(namespaceURI, paramStruct.getName().getLocalPart()));
        LiteralType memberType;

        boolean isHolder;
        for (int i = 0; i < paramTypes.length; i++) {
            elemName = new QName(paramNames[i]);
            member =
                literalTypeModeler.modelTypeLiteral(
                    elemName,
                    typeUri,
                    paramTypes[i],
                    true,
                    true);
            member.setRequired(true);
            isHolder =
                LiteralTypeModeler.getHolderValueType(
                    env,
                    defHolder,
                    paramTypes[i])
                    != null;
            javaMember = member.getJavaStructureMember();
            parameter = new Parameter(paramNames[i]);
            if (isHolder) {
                javaRespStructure.add(javaMember);
                responseStruct.add(member);
                Parameter responseParam = new Parameter(paramNames[i]);
                responseParam.setEmbedded(true);
                javaParameter =
                    new JavaParameter(
                        paramNames[i],
                        member.getType().getJavaType(),
                        responseParam,
                        true);
                javaParameter.setHolderName(paramTypes[i].toString());
                responseParam.setJavaParameter(javaParameter);
                responseParam.setType(member.getType());
                responseParam.setBlock(responseBlock);
                parameter.setLinkedParameter(responseParam);
                responseParam.setLinkedParameter(parameter);
                response.addParameter(responseParam);
            }
            parameter.setEmbedded(true);
            javaStruct.add(javaMember);
            paramStruct.add(member);
            javaParameter =
                new JavaParameter(
                    paramNames[i],
                    member.getJavaStructureMember().getType(),
                    parameter,
                    isHolder);
            if (isHolder)
                javaParameter.setHolderName(paramTypes[i].toString());
            parameter.setJavaParameter(javaParameter);
            parameter.setType(member.getType());
            parameter.setBlock(requestBlock);
            javaMethod.addParameter(javaParameter);
            request.addParameter(parameter);
        }
        requestBlock.setType(paramStruct);
        request.addBodyBlock(requestBlock);
        request.setProperty(
            ModelProperties.PROPERTY_WSDL_MESSAGE_NAME,
            getWSDLInputMessageName(portName + UNDERSCORE + operationName));
        operation.setRequest(request);

        //  create Fault Messages
        if (exceptions.length > 0) {
            for (int i = 0; i < exceptions.length; i++) {
                if (!isRemoteException(env,
                    exceptions[i].getName().toString())) {
                    javaMethod.addException(exceptions[i].getName().toString());
                    if (!exceptions[i]
                        .getName()
                        .toString()
                        .equals(EXCEPTION_CLASSNAME)) {
                        Fault fault =
                            literalExceptionModeler.modelException(
                                typeUri,
                                wsdlUri,
                                exceptions[i]);
                        fault.setElementName(
                            new QName(
                                fault
                                    .getBlock()
                                    .getType()
                                    .getName()
                                    .getNamespaceURI(),
                                fault
                                    .getBlock()
                                    .getType()
                                    .getName()
                                    .getLocalPart()));
                        if (response == null) {
                            response = new Response();
                            operation.setResponse(response);
                        }
                        response.addFaultBlock(fault.getBlock());
                        fault.setProperty(
                            ModelProperties.PROPERTY_WSDL_MESSAGE_NAME,
                            getWSDLFaultMessageName(fault.getName()));
                        operation.addFault(fault);
                        addFaultParent(fault, literalExceptionModeler);
                    }
                }
            }
        }

        return operation;
    }

    private RmiType[] getParameterTypes(Method method) {
        Class[] args = method.getParameterTypes();
        RmiType[] types = new RmiType[args.length];
        for (int i = 0; i < args.length; i++) {
            types[i] = RmiType.getRmiType(args[i]);
        }
        return types;
    }

    private String getStructName(String name) {
        String tmp = name.toLowerCase();
        Integer count = (Integer) structMap.get(tmp);
        if (count != null) {
            count = new Integer(count.intValue() + 1);
            name = name + count;
        } else {
            count = new Integer(0);
        }
        structMap.put(tmp, count);
        return name;
    }

    public boolean isRemoteException(
        ProcessorEnvironment env,
        String exceptionName) {
            
        try {
            Class exceptionClass =
                RmiUtils.getClassForName(exceptionName, env.getClassLoader());
            return remoteExceptionClass.isAssignableFrom(exceptionClass);
        } catch (ClassNotFoundException e) {
            throw new ModelerException(
                RMI_MODELER_CLASS_NOT_FOUND,
                exceptionName);
        }
    }

    /**
    * Generate an array of names for parameters corresponding to the
    * given array of types for the parameters.  Each name in the returned
    * array is guaranteed to be unique.
    *
    * A representation of the type of a parameter is included in its
    * corresponding field name to enhance the readability of the generated
    * code.
    */
    private String[] nameParameters(RmiType[] types) {
        String[] names = new String[types.length];
        for (int i = 0; i < names.length; i++) {
            names[i] =
                generateNameFromType(types[i], env) + UNDERSCORE + (i + 1);
        }
        return names;
    }

    /**
      * Generate a readable string representing the given type suitable
      * for embedding within a Java identifier.
      */
    public String generateNameFromType(
        RmiType type,
        ProcessorEnvironment env) {
            
        int typeCode = type.getTypeCode();
        switch (typeCode) {
            case TC_BOOLEAN :
            case TC_BYTE :
            case TC_CHAR :
            case TC_SHORT :
            case TC_INT :
            case TC_LONG :
            case TC_FLOAT :
            case TC_DOUBLE :
                return type.toString();
            case TC_ARRAY :
                return ARRAY_OF
                    + generateNameFromType(type.getElementType(), env);
            case TC_CLASS :
                RmiType holderValueType =
                    RmiTypeModeler.getHolderValueType(env, defHolder, type);
                if (holderValueType != null) {
                    return generateNameFromType(holderValueType, env);
                }
                String tmp = ClassNameInfo.getName(type.getClassName());
                return ClassNameInfo.replaceInnerClassSym(tmp);
            default :
                throw new Error("unexpected type code: " + typeCode);
        }
    }

    public String getSOAPAction(
        RmiInterfaceInfo interfaceInfo,
        String operationName) {
            
        if (interfaceInfo.getSOAPAction() != null) {
            return interfaceInfo.getSOAPAction();
        }

        if (interfaceInfo.getSOAPActionBase() != null) {
            return interfaceInfo.getSOAPActionBase() + operationName;
        }

        return "";
    }

    public String getOperationName(String messageName) {
        String operationName = null;
        Integer cnt = (Integer) messageMap.get(messageName);
        if (cnt == null) {
            cnt = new Integer(0);
            operationName = messageName;
        }
        messageMap.put(messageName, new Integer(cnt.intValue() + 1));
        if (operationName == null) {
            operationName = messageName + (cnt.intValue() + 1);
        }
        return operationName;
    }

    public String getNamespaceURI(String javaPackageName) {
        if (namespaceMappingRegistry != null) {
            NamespaceMappingInfo i =
                namespaceMappingRegistry.getNamespaceMappingInfo(
                    javaPackageName);
            if (i != null)
                return i.getNamespaceURI();
        }
        return null;
    }

    private void log(String msg) {
        if (env.verbose()) {
            System.out.println("[" + msg + "]");
        }
    }

    // these methods added so that the RMI modeler can pick the names
    // it wants for the WSDL artifacts associated with ports and operations

    private QName getWSDLPortName(String portName) {
        return new QName(wsdlUri, portName + PORT);
    }

    private QName getWSDLBindingName(String portName) {
        return new QName(wsdlUri, portName + BINDING);
    }

    private QName getWSDLPortTypeName(String portName) {
        return new QName(wsdlUri, portName);
    }

    private QName getWSDLInputMessageName(String operationName) {
        return new QName(wsdlUri, operationName);
    }

    private QName getWSDLOutputMessageName(String operationName) {
        return new QName(wsdlUri, operationName + RESPONSE);
    }

    private QName getWSDLFaultMessageName(String faultName) {
        return new QName(wsdlUri, faultName);
    }

    public static String getMethodSig(Method method) {
        String sig = method.getName() + "(";
        Class[] params = method.getParameterTypes();
        for (int i = 0; i < params.length; i++) {
            if (i > 0)
                sig += ", ";
            sig += params[i].getName();
        }
        return sig + ")";
    }

    static {
        excludedInterfaces.add(SERIALIZABLE_CLASSNAME);
        excludedInterfaces.add(HOLDER_CLASSNAME);
        excludedInterfaces.add(REMOTE_CLASSNAME);
    }

    public static class MethodComparator implements Comparator {
        public MethodComparator() {
        }

        public int compare(Object o1, Object o2) {
            Method method1 = (Method) o1;
            Method method2 = (Method) o2;
            return sort(method1, method2);
        }

        protected int sort(Method method1, Method method2) {
            String sig1, sig2;
            sig1 = RmiModeler.getMethodSig(method1);
            sig2 = RmiModeler.getMethodSig(method2);
            Class class1 = method1.getDeclaringClass();
            Class class2 = method2.getDeclaringClass();
            if (class1.equals(class2))
                return sig1.compareTo(sig2);
            if (class1.isAssignableFrom(class2))
                return -1;
            return 1;
        }
    }
}
