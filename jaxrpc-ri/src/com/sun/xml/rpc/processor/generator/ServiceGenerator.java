/*
 * $Id: ServiceGenerator.java,v 1.2 2006-04-13 01:28:56 ofung Exp $
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

package com.sun.xml.rpc.processor.generator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.ProcessorAction;
import com.sun.xml.rpc.processor.ProcessorConstants;
import com.sun.xml.rpc.processor.ProcessorOptions;
import com.sun.xml.rpc.processor.config.Configuration;
import com.sun.xml.rpc.processor.config.HandlerChainInfo;
import com.sun.xml.rpc.processor.config.HandlerInfo;
import com.sun.xml.rpc.processor.model.Model;
import com.sun.xml.rpc.processor.model.ModelProperties;
import com.sun.xml.rpc.processor.model.Port;
import com.sun.xml.rpc.processor.model.Service;
import com.sun.xml.rpc.processor.model.java.JavaInterface;
import com.sun.xml.rpc.processor.util.GeneratedFileInfo;
import com.sun.xml.rpc.processor.util.IndentingWriter;
import com.sun.xml.rpc.processor.util.ProcessorEnvironment;
import com.sun.xml.rpc.processor.util.StringUtils;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import com.sun.xml.rpc.util.localization.Localizable;

/**
 *
 * @author JAX-RPC Development Team
 */
public class ServiceGenerator implements ProcessorAction {
    private File sourceDir;
    private ProcessorEnvironment env;
    private Model model;
    private Set portClassNames;
    private String JAXRPCVersion;
    private String sourceVersion;
    private boolean donotOverride;

    public ServiceGenerator() {
        sourceDir = null;
        env = null;
        model = null;
    }

    public void perform(
        Model model,
        Configuration config,
        Properties properties) {
        ProcessorEnvironment env =
            (ProcessorEnvironment) config.getEnvironment();
        ServiceGenerator generator =
            new ServiceGenerator(env, model, properties);
        generator.doGeneration();
    }

    private ServiceGenerator(
        ProcessorEnvironment env,
        Model model,
        Properties properties) {
        this.env = env;
        this.model = model;
        String key = ProcessorOptions.SOURCE_DIRECTORY_PROPERTY;
        String dirPath = properties.getProperty(key);
        this.sourceDir = new File(dirPath);
        key = ProcessorOptions.DONOT_OVERRIDE_CLASSES;
        this.donotOverride =
            Boolean.valueOf(properties.getProperty(key)).booleanValue();
        this.JAXRPCVersion =
            properties.getProperty(ProcessorConstants.JAXRPC_VERSION);
        this.sourceVersion =
            properties.getProperty(ProcessorOptions.JAXRPC_SOURCE_VERSION);
    }

    private void doGeneration() {
        env.getNames().resetPrefixFactory();
        Service service = null;
        try {
            for (Iterator iter = model.getServices(); iter.hasNext();) {
                service = (Service) iter.next();
                generateService(service);
            }
        } catch (IOException e) {
            fail("generator.cant.write", service.getName().getLocalPart());
        } finally {
            sourceDir = null;
            env = null;
        }
    }

    private void generateService(Service service) throws IOException {
        try {
            JavaInterface intf = (JavaInterface) service.getJavaInterface();
            String className = env.getNames().interfaceImplClassName(intf);
            String serializerRegistryName =
                env.getNames().serializerRegistryClassName(intf);
            int location = serializerRegistryName.lastIndexOf(".");
            if (service
                .getName()
                .getLocalPart()
                .equals(serializerRegistryName.substring(0, location))) {
                /* by using location + 1 we skip the package name 
                   but get the rest of the naming structure */
                serializerRegistryName =
                    serializerRegistryName.substring(
                        location + 1,
                        serializerRegistryName.length());
            }
            if ((donotOverride && GeneratorUtil.classExists(env, className))) {
                log("Class " + className + " exists. Not overriding.");
                return;
            }
            log("creating service: " + className);
            String interfaceName = env.getNames().customJavaTypeClassName(intf);
            File classFile =
                env.getNames().sourceFileForClass(
                    className,
                    className,
                    sourceDir,
                    env);

            /* adding the file name and its type */
            GeneratedFileInfo fi = new GeneratedFileInfo();
            fi.setFile(classFile);
            fi.setType(GeneratorConstants.FILE_TYPE_SERVICE_IMPL);
            env.addGeneratedFile(fi);

            portClassNames = new HashSet();

            IndentingWriter out =
                new IndentingWriter(
                    new OutputStreamWriter(new FileOutputStream(classFile)));
            GeneratorBase.writePackage(out, className, JAXRPCVersion, sourceVersion);
            writeImports(out);
            out.pln();
            writeClassDecl(out, className, interfaceName);
            writeStaticMembers(out, service);
            out.pln();
            writeConstructor(out, className, service, serializerRegistryName);
            out.pln();
            writeGenericGetPortMethods(out, service);
            out.pln();
            writeIndividualGetPorts(out, service.getPorts());
            out.pOln("}");
            out.close();

        } catch (Exception e) {
            throw new GeneratorException(
                "generator.nestedGeneratorError",
                new LocalizableExceptionAdapter(e));
        } finally {
            portClassNames = null;
        }
    }

    private void writeImports(IndentingWriter p) throws IOException {
        p.pln("import com.sun.xml.rpc.encoding.*;");
        p.pln("import com.sun.xml.rpc.client.ServiceExceptionImpl;");
        p.pln("import com.sun.xml.rpc.util.exception.*;");
        p.pln("import com.sun.xml.rpc.soap.SOAPVersion;");
        p.pln("import com.sun.xml.rpc.client.HandlerChainImpl;");
        p.pln("import javax.xml.rpc.*;");
        p.pln("import javax.xml.rpc.encoding.*;");
        p.pln("import javax.xml.rpc.handler.HandlerChain;");
        p.pln("import javax.xml.rpc.handler.HandlerInfo;");
        p.pln("import javax.xml.namespace.QName;");
    }

    private void writeClassDecl(
        IndentingWriter p,
        String className,
        String interfaceName)
        throws IOException {
        p.plnI(
            "public class "
                + Names.stripQualifier(className)
                + " extends com.sun.xml.rpc.client.BasicService"
                + " implements "
                + Names.stripQualifier(interfaceName)
                + " {");
    }

    private void writeStaticMembers(IndentingWriter p, Service service)
        throws IOException {
        p.p("private static final QName serviceName = ");
        GeneratorUtil.writeNewQName(p, service.getName());
        p.pln(";");
        Iterator ports = service.getPorts();
        Port port;
        String portClass;
        while (ports.hasNext()) {
            port = (Port) ports.next();
            portClass = port.getJavaInterface().getName();
            QName portName = port.getName();
            p.p(
                "private static final QName "
                    + env.getNames().getQNameName(portName)
                    + " = ");
            GeneratorUtil.writeNewQName(p, portName);
            p.pln(";");
            QName portWsdlName =
                (QName) port.getProperty(
                    ModelProperties.PROPERTY_WSDL_PORT_NAME);
            if (!portWsdlName.equals(portName)) {
                p.p(
                    "private static final QName "
                        + env.getNames().getQNameName(portWsdlName)
                        + " = ");
                GeneratorUtil.writeNewQName(p, portWsdlName);
                p.pln(";");
            }
            if (!portClassNames.contains(portClass)) {
                p.pln(
                    "private static final Class "
                        + StringUtils.decapitalize(
                            Names.stripQualifier(portClass))
                        + "_PortClass = "
                        + portClass
                        + ".class;");
                portClassNames.add(portClass);
            }
        }
    }

    private void writeConstructor(
        IndentingWriter p,
        String className,
        Service service,
        String serializerRegistryName)
        throws IOException {
        p.plnI("public " + Names.stripQualifier(className) + "() {");
        p.plnI("super(serviceName, new QName[] {");
        p.pI(3);
        Iterator eachPort = service.getPorts();
        Port port;
        for (int i = 0; eachPort.hasNext(); i++) {
            port = (Port) eachPort.next();
            if (i > 0)
                p.pln(",");
            p.p(env.getNames().getQNameName(port.getName()));
        }
        p.pln();
        p.pOln("},");
        p.pO(2);
        p.pln("new " + serializerRegistryName + "().getRegistry());");

        p.pO();
        eachPort = service.getPorts();
        if (eachPort.hasNext()) {
            p.pln();
            while (eachPort.hasNext()) {
                port = (Port) eachPort.next();
                HandlerChainInfo portClientHandlers =
                    port.getClientHandlerChainInfo();
                Iterator eachHandler = portClientHandlers.getHandlers();
                if (eachHandler.hasNext()) {
                    p.plnI("{");
                    p.pln(
                        "java.util.List handlerInfos = new java.util.Vector();");
                    while (eachHandler.hasNext()) {
                        HandlerInfo currentHandler =
                            (HandlerInfo) eachHandler.next();
                        Map properties = currentHandler.getProperties();
                        String propertiesName = "props";
                        p.plnI("{");
                        p.pln(
                            "java.util.Map "
                                + propertiesName
                                + " = new java.util.HashMap();");
                        for (Iterator entries =
                            properties.entrySet().iterator();
                            entries.hasNext();
                            ) {
                            Map.Entry entry = (Map.Entry) entries.next();
                            p.pln(
                                propertiesName
                                    + ".put(\""
                                    + (String) entry.getKey()
                                    + "\", \""
                                    + (String) entry.getValue()
                                    + "\");");
                        }

                        Object[] headers =
                            currentHandler.getHeaderNames().toArray();

                        if (headers != null && headers.length > 0) {
                            p.plnI("javax.xml.namespace.QName[] headers = {");
                            for (int i = 0; i < headers.length; i++) {
                                QName hdr = (QName) headers[i];

                                p.pln(
                                    "new javax.xml.namespace.QName("
                                        + "\""
                                        + hdr.getNamespaceURI()
                                        + "\""
                                        + ", "
                                        + "\""
                                        + hdr.getLocalPart()
                                        + "\""
                                        + ")"
                                        + ((i != headers.length - 1) ? "," : ""));
                            }
                            p.pOln("};");
                        } else
                            p.pln("javax.xml.namespace.QName[] headers = null;");

                        p.pln(
                            "HandlerInfo handlerInfo = new HandlerInfo("
                                + currentHandler.getHandlerClassName()
                                + ".class"
                                + ", "
                                + propertiesName
                                + ", headers);");
                        p.pln("handlerInfos.add(handlerInfo);");
                        p.pOln("}");
                    }
                    p.pln(
                        "getHandlerRegistry().setHandlerChain("
                            + env.getNames().getQNameName(
                                (QName) port.getProperty(
                                    ModelProperties.PROPERTY_WSDL_PORT_NAME))
                            + ", handlerInfos);");

                    p.pOln("}");
                }
            }
        }
        p.pOln("}");
    }

    private void writeGenericGetPortMethods(IndentingWriter p, Service service)
        throws IOException {
        Iterator ports = service.getPorts();
        Port port;
        String portClass;
        String portName;
        p.plnI(
            "public java.rmi.Remote getPort(javax.xml.namespace.QName portName, java.lang.Class serviceDefInterface) throws javax.xml.rpc.ServiceException {");
        p.plnI("try {");
        while (ports.hasNext()) {
            port = (Port) ports.next();
            portClass = port.getJavaInterface().getName();
            portName = Names.getPortName(port);
            p.plnI(
                "if (portName.equals("
                    + env.getNames().getQNameName(port.getName())
                    + ") &&");
            p.pln(
                "serviceDefInterface.equals("
                    + StringUtils.decapitalize(Names.stripQualifier(portClass))
                    + "_PortClass)) {");
            /* here we change the first character of the PortName
               to Capital Letter */
            portName = env.getNames().validJavaClassName(portName);
            p.pln("return get" + portName + "();");
            p.pOln("}");
        }
        p.pOlnI("} catch (Exception e) {");
        p.pln(
            "throw new ServiceExceptionImpl(new LocalizableExceptionAdapter(e));");
        p.pOln("}");
        p.pln("return super.getPort(portName, serviceDefInterface);");
        p.pOln("}");
        p.pln();

        ports = service.getPorts();
        p.plnI(
            "public java.rmi.Remote getPort(java.lang.Class serviceDefInterface) throws javax.xml.rpc.ServiceException {");
        p.plnI("try {");
        while (ports.hasNext()) {
            port = (Port) ports.next();
            portClass = port.getJavaInterface().getName();
            portName = Names.getPortName(port);
            /* here we change the first character of the PortName
               to Capital Letter */
            portName = env.getNames().validJavaClassName(portName);
            p.plnI(
                "if (serviceDefInterface.equals("
                    + StringUtils.decapitalize(Names.stripQualifier(portClass))
                    + "_PortClass)) {");
            p.pln("return get" + portName + "();");
            p.pOln("}");
        }
        p.pOlnI("} catch (Exception e) {");
        p.pln(
            "throw new ServiceExceptionImpl(new LocalizableExceptionAdapter(e));");
        p.pOln("}");
        p.pln("return super.getPort(serviceDefInterface);");
        p.pOln("}");

    }

    private void writeIndividualGetPorts(IndentingWriter p, Iterator ports)
        throws IOException {
        Port port;
        String portClass;
        while (ports.hasNext()) {
            port = (Port) ports.next();
            portClass = port.getJavaInterface().getName();
            String portName = Names.getPortName(port);
            QName portWsdlName =
                (QName) port.getProperty(
                    ModelProperties.PROPERTY_WSDL_PORT_NAME);
            /* here we change the first character of the PortName
               to Capital Letter */
            portName = env.getNames().validJavaClassName(portName);
            p.plnI("public " + portClass + " get" + portName + "() {");
            Set roles = port.getClientHandlerChainInfo().getRoles();
            p.p("java.lang.String[] roles = new java.lang.String[] {");

            boolean first = true;
            Iterator i = roles.iterator();
            while (i.hasNext()) {
                if (!first) {
                    p.p(", ");
                } else
                    first = false;

                p.p("\"" + i.next() + "\"");
            }

            p.pln("};");

            p.pln(
                "HandlerChainImpl handlerChain = new HandlerChainImpl(getHandlerRegistry().getHandlerChain("
                    + env.getNames().getQNameName(portWsdlName)
                    + "));");

            p.pln("handlerChain.setRoles(roles);");

            p.pln(
                env.getNames().stubFor(port)
                    + " stub = new "
                    + env.getNames().stubFor(port)
                    + "(handlerChain);");

            p.plnI("try {");
            p.pln("stub._initialize(super.internalTypeRegistry);");
            p.pOlnI("} catch (JAXRPCException e) {");
            p.pln("throw e;");
            p.pOlnI("} catch (Exception e) {");
            p.pln("throw new JAXRPCException(e.getMessage(), e);");
            p.pOln("}");
            p.pln("return stub;");
            p.pOln("}");
        }
    }

    private void log(String msg) {
        if (env.verbose()) {
            System.out.println(
                "["
                    + Names.stripQualifier(this.getClass().getName())
                    + ": "
                    + msg
                    + "]");
        }
    }

    protected void fail(String key) {
        throw new GeneratorException(key);
    }

    protected void fail(String key, String arg) {
        throw new GeneratorException(key, arg);
    }

    protected void fail(String key, String arg1, String arg2) {
        throw new GeneratorException(key, new Object[] { arg1, arg2 });
    }

    protected void fail(Localizable arg) {
        throw new GeneratorException("generator.nestedGeneratorError", arg);
    }

    protected void fail(Throwable arg) {
        throw new GeneratorException(
            "generator.nestedGeneratorError",
            new LocalizableExceptionAdapter(arg));
    }
}
