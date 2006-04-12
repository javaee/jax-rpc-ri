/*
 * $Id: EndpointCompileTool.java,v 1.1 2006-04-12 20:34:16 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.tools.wsdeploy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.Processor;
import com.sun.xml.rpc.processor.ProcessorNotificationListener;
import com.sun.xml.rpc.processor.config.Configuration;
import com.sun.xml.rpc.processor.config.ModelFileModelInfo;
import com.sun.xml.rpc.processor.config.RmiInterfaceInfo;
import com.sun.xml.rpc.processor.config.RmiModelInfo;
import com.sun.xml.rpc.processor.model.ModelProperties;
import com.sun.xml.rpc.processor.model.Port;
import com.sun.xml.rpc.processor.model.Service;
import com.sun.xml.rpc.processor.model.java.JavaInterface;
import com.sun.xml.rpc.processor.util.GeneratedFileInfo;
import com.sun.xml.rpc.processor.util.XMLModelWriter;
import com.sun.xml.rpc.tools.wscompile.ActionConstants;
import com.sun.xml.rpc.tools.wscompile.CompileTool;
import com.sun.xml.rpc.util.VersionUtil;
import com.sun.xml.rpc.util.localization.Localizable;

/**
 *
 * @author JAX-RPC Development Team
 */
public class EndpointCompileTool extends CompileTool {
    
    public EndpointCompileTool(OutputStream out,
        String program,
        WebServicesInfo wsi,
        ArrayList list,
        File dir,
        String target,
        Properties props,
        String classpath,
        ProcessorNotificationListener l) {
            
        super(out, program);
        webServicesInfo = wsi;
        targetDirectory = dir;
        additionalClasspath = classpath;
        listener = l;
        vector = list;
        endpointInfo = (EndpointInfo)vector.get(0);
        useModel = endpointInfo.getModel() != null;
        this.targetVersion = target;
        this.verbose = new Boolean(props.getProperty("verbose")).booleanValue();
        this.keepGenerated =
            new Boolean(props.getProperty("keepGenerated")).booleanValue();
        this.donotOverride = true;
        resetOptionsForTargetVersion();
    }
    
    public EndpointCompileTool(OutputStream out,
        String program,
        EndpointInfo ei,
        WebServicesInfo wsi,
        File dir,
        String target,
        Properties props,
        String classpath,
        ProcessorNotificationListener l) {
            
        super(out, program);
        endpointInfo = ei;
        webServicesInfo = wsi;
        targetDirectory = dir;
        additionalClasspath = classpath;
        listener = l;
        useModel = endpointInfo.getModel() != null;
        this.targetVersion = target;
        this.verbose = new Boolean(props.getProperty("verbose")).booleanValue();
        this.keepGenerated =
            new Boolean(props.getProperty("keepGenerated")).booleanValue();
        this.donotOverride = true;
        resetOptionsForTargetVersion();
    }
    
    /* This has been to ensure that data specified in jaxrpc-ri.xml
     * the value for interface and implementation is valid
     */
    protected boolean classExists(String className, String message) {
        try {
            Class c =
                Class.forName(className, true, environment.getClassLoader());
            if (c != null) {
                return true;
            }
        } catch (ClassNotFoundException ce) {
            onError(getMessage("wscompile.fileNotFound", message));
        }
        return false;
    }
    
    protected void beforeHook() {
        
        // set things up properly
        String targetPath = targetDirectory.getAbsolutePath();
        nonclassDestDir = new File(targetPath + FS + "WEB-INF");
        userClasspath = targetPath + FS + "WEB-INF" + FS + "classes";
        destDir = new File(userClasspath);
        
        // Add all jar files under WEB-INF/lib to the user classpath
        if (new File(targetPath + FS + "WEB-INF" + FS + "lib").exists()) {
            File[] fs =
                new File(targetPath + FS + "WEB-INF" + FS + "lib").listFiles();
            for (int counter = 0; counter < fs.length; ++counter) {
                userClasspath += PS + fs[counter];
            }
        }
        
        if (additionalClasspath != null && additionalClasspath.length() > 0) {
            userClasspath += PS + additionalClasspath;
        }
        
        if (!useModel) {
            modelFile = new File(makeModelFileName());
        } else if (targetVersion != null) {
            onWarning(getMessage(
                "wscompile.warning.ignoringTargetVersionForModel",
                endpointInfo.getModel(),
                targetVersion));
            targetVersion = null;
        }
        
        serializerInfix = "_" + endpointInfo.getName() + "_";
        compilerDebug = false;
        compilerOptimize = true;
        super.beforeHook();
        
    }
    
    protected void withModelHook() {
        
        /* local EndpointInfo variable */
        EndpointInfo ei = null;
        
        if (endpointInfo.getClientHandlerChainInfo() != null
            || endpointInfo.getServerHandlerChainInfo() != null) {
                
            // we need to replace the handler chain descriptions in the model
            Iterator services = processor.getModel().getServices();
            if (services.hasNext()) {
                Service service = (Service)services.next();
                endpointInfo.setRuntimeServiceName(service.getName());
                Iterator ports = service.getPorts();
                Port port = null;
                QName qName = null;
                while (ports.hasNext()) {
                    
                    /* here we loop through all the available ports
                     * for a specified service
                     */
                    port = (Port)ports.next();
                    if (endpointInfo.getModel() == null) {
                        if (((port.getJavaInterface()).getName()).equals(
                            endpointInfo.getInterface())) {
                                
                            port.setClientHandlerChainInfo(
                                endpointInfo.getClientHandlerChainInfo());
                            port.setServerHandlerChainInfo(
                                endpointInfo.getServerHandlerChainInfo());
                        }
                    } else {
                        for (int counter = 0; counter < vector.size();
                            counter++) {
                                
                            ei = (EndpointInfo) vector.get(counter);
                            
                            if (((port.getJavaInterface()).getName()).equals(
                                ei.getInterface())) {
                                    
                                ei.setRuntimeServiceName(service.getName());
                                port.setClientHandlerChainInfo(
                                    ei.getClientHandlerChainInfo());
                                port.setServerHandlerChainInfo(
                                    ei.getServerHandlerChainInfo());
                                counter = vector.size();
                            }
                        }
                    }
                }
            }
        }
    }
    
    protected void afterHook() {
        
        // varialble to flag if end points will have similar interfacse
        boolean isCommonInterface = false;
        HashSet hSet = new HashSet();
        
        if (environment.getErrorCount() == 0) {
            endpointInfo.setRuntimeModel(useModel ?
                endpointInfo.getModel() : makeAppRelative(modelFile));
            if (endpointInfo.getRuntimeWSDL() == null) {
                endpointInfo.setRuntimeWSDL(
                    makeAppRelative(findGeneratedFileEndingWith(".wsdl")));
            }
            Iterator services = processor.getModel().getServices();
            String modelVersion = ((com.sun.xml.rpc.processor.model.Model)
                processor.getModel()).getSource();
            
            if (services.hasNext()) {
                Service service = (Service)services.next();
                endpointInfo.setRuntimeServiceName(service.getName());
                Iterator ports = service.getPorts();
                Port port = null;
                EndpointInfo endpoint = null;
                String name = null;
                
                if (endpointInfo.getModel() != null) {
                    
                    /* we first check wether this is a model file which has
                     * multiple endpoints with the same interface
                     */
                    for (int counter = 0; counter < vector.size(); counter++) {
                        endpoint = (EndpointInfo)vector.get(counter);
                        name = "";
                        for (int index = counter + 1; index < vector.size();
                            index++) {
                                
                            if (endpoint.getInterface().equals(((EndpointInfo)
                                vector.get(index)).getInterface())) {
                                    
                                hSet.add(endpoint);
                                hSet.add((EndpointInfo)vector.get(index));
                            }
                        }
                    }
                }
                
                while (ports.hasNext()) {
                    port = (Port)ports.next();
                    
                    // we need to find out the name of the port in WSDL
                    QName portName = (QName) port.getProperty(
                    ModelProperties.PROPERTY_WSDL_PORT_NAME);
                    if (portName == null) {
                        portName = port.getName();
                    }
                    
                    /* Catering for situations when endpoints in jaxrpx-ri.xml
                     * will not have model files associated to them
                     */
                    if (endpointInfo.getModel() == null) {
                        classExists(endpointInfo.getInterface(),
                            "Interface specified in jaxrpc-ri.xml for "
                            + endpointInfo.getName()
                            + " is incorrect");
                        classExists(endpointInfo.getImplementation(),
                            "Implementation specified in jaxrpc-ri.xml for "
                            + endpointInfo.getName()
                            + " is incorrect");
                        
                        if (((port.getJavaInterface()).getName()).equals(
                            endpointInfo.getInterface())) {
                                
                            endpointInfo.setRuntimePortName(portName);
                            JavaInterface intf = port.getJavaInterface();
                            endpointInfo.setRuntimeTie(
                                environment.getNames().tieFor(port));
                            endpointInfo.setRuntimeDeployed(true);
                        }
                    } else {
                        
                        /* situations when modelfile is infact associated with
                         * an endpointinfo
                         */
                        for (int counter = 0; counter < vector.size();
                            counter++) {
                                
                            endpointInfo = (EndpointInfo) vector.get(counter);
                            classExists(endpointInfo.getInterface(),
                                "Interface specified in jaxrpc-ri.xml for "
                                + endpointInfo.getName()
                                + " is incorrect");
                            classExists(endpointInfo.getImplementation(),
                                "Implementation specified in jaxrpc-ri.xml for "
                                + endpointInfo.getName()
                                + " is incorrect");
                            
                            if (vector.size() > 1) {
                                if (VersionUtil.isVersion103(modelVersion)
                                    || VersionUtil.isVersion101(modelVersion)) {
                                        
                                    if (((port.getJavaInterface()).getName())
                                        .equals(endpointInfo.getInterface())) {
                                            
                                        setEndpointInfo(endpointInfo,
                                            portName,
                                            service,
                                            port);
                                    }
                                } else {
                                    if (hSet.contains(endpointInfo)
                                        && (endpointInfo.getRuntimePortName()
                                        == null)) {
                                            
                                        onWarning(getMessage(
                                            "wscompile.warning.noportInfo",
                                            endpointInfo.getName()));
                                    } else if (hSet.contains(endpointInfo) &&
                                        portName.equals(endpointInfo.
                                            getRuntimePortName())) {
                                                
                                        setEndpointInfo(endpointInfo,
                                            portName,
                                            service,
                                            port);
                                    } else if (((port.getJavaInterface())
                                        .getName()).equals(
                                        endpointInfo.getInterface()) &&
                                        !(hSet.contains(endpointInfo))) {
                                            
                                        setEndpointInfo(endpointInfo,
                                            portName,
                                            service,
                                            port);
                                    }
                                }
                            } else {
                                if (((port.getJavaInterface()).getName())
                                    .equals(endpointInfo.getInterface())) {
                                        
                                    setEndpointInfo(endpointInfo,
                                        portName,
                                        service,
                                        port);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (delegate != null) {
            delegate.postRun();
        }
    }
    
    private void setEndpointInfo(EndpointInfo ei, QName portName,
        Service service, Port port) {
            
        ei.setRuntimeModel(useModel ?
            ei.getModel() : makeAppRelative(modelFile));
        if (ei.getRuntimeWSDL() == null) {
            ei.setRuntimeWSDL(makeAppRelative(
                findGeneratedFileEndingWith(".wsdl")));
        }
        ei.setRuntimeServiceName(service.getName());
        if (ei.getRuntimePortName() == null) {
            ei.setRuntimePortName(portName);
        }
        JavaInterface intf = port.getJavaInterface();
        ei.setRuntimeTie(environment.getNames().tieFor(port));
        ei.setRuntimeDeployed(true);
    }
    
    public com.sun.xml.rpc.spi.tools.Configuration createConfiguration()
        throws Exception {
            
        // create our own configuration
        Configuration config = new Configuration(environment);
        if (useModel) {
            ModelFileModelInfo modelInfo = new ModelFileModelInfo();
            modelInfo.setLocation(makeAbsolute(endpointInfo.getModel()));
            config.setModelInfo(modelInfo);
        } else {
            RmiModelInfo modelInfo = new RmiModelInfo();
            modelInfo.setName(endpointInfo.getName());
            modelInfo.setTargetNamespaceURI(makeTargetNamespaceURI());
            modelInfo.setTypeNamespaceURI(makeTypeNamespaceURI());
            modelInfo.setJavaPackageName(makeJavaPackageName());
            RmiInterfaceInfo interfaceInfo = new RmiInterfaceInfo();
            interfaceInfo.setName(endpointInfo.getInterface());
            interfaceInfo.setServantName(endpointInfo.getImplementation());
            interfaceInfo.setServerHandlerChainInfo(
            endpointInfo.getServerHandlerChainInfo());
            modelInfo.add(interfaceInfo);
            config.setModelInfo(modelInfo);
        }
        return config;
    }
    
    protected String makeTargetNamespaceURI() {
        String base = webServicesInfo.getTargetNamespaceBase();
        if (base.endsWith("/") || base.startsWith("urn:")) {
            return base + endpointInfo.getName();
        } else {
            return base + "/" + endpointInfo.getName();
        }
    }
    
    protected String makeTypeNamespaceURI() {
        String base = webServicesInfo.getTypeNamespaceBase();
        if (base.endsWith("/") || base.startsWith("urn:")) {
            return base + endpointInfo.getName();
        } else {
            return base + "/" + endpointInfo.getName();
        }
    }
    
    protected String makeModelFileName() {
        return targetDirectory.getAbsolutePath()
            + FS
            + "WEB-INF"
            + FS
            + endpointInfo.getName()
            + "_model.xml.gz";
    }
    
    protected String makeJavaPackageName() {
        return "jaxrpc.generated." +
            environment.getNames().validJavaPackageName(endpointInfo.getName());
    }
    
    protected String makeAbsolute(String s) {
        if (s == null) {
            return null;
        }
        return new File(targetDirectory.getAbsolutePath() + s)
            .getAbsolutePath();
    }
    
    protected String makeAppRelative(File f) {
        if (f == null) {
            return null;
        }
        String s = f.getAbsolutePath();
        String target = targetDirectory.getAbsolutePath();
        if (s.startsWith(target)) {
            return s.substring(target.length()).replace(FSCHAR, '/');
        } else {
            
            // TODO - isn't this an error?
            return null;
        }
    }
    
    protected File findGeneratedFileEndingWith(String s) {
        for (Iterator iter = environment.getGeneratedFiles();
            iter.hasNext();) {
                
            GeneratedFileInfo fileInfo = (GeneratedFileInfo) iter.next();
            File file = fileInfo.getFile();
            if (file.getAbsolutePath().endsWith(s)) {
                return file;
            }
        }
        return null;
    }
    
    protected void registerProcessorActions(Processor processor) {
        
        // completely override the actions in the base class
        if (!useModel) {
            try {
                processor.add(new XMLModelWriter(modelFile));
            } catch (FileNotFoundException e) {
                
                // should not happen
            }
        }
        processor.add(getAction(ActionConstants.ACTION_ENUMERATION_GENERATOR));
        processor.add(getAction(
            ActionConstants.ACTION_ENUMERATION_ENCODER_GENERATOR));
        processor.add(getAction(ActionConstants.ACTION_HOLDER_GENERATOR));
        processor.add(getAction(ActionConstants.ACTION_CUSTOM_CLASS_GENERATOR));
        processor.add(getAction(
            ActionConstants.ACTION_SOAP_OBJECT_SERIALIZER_GENERATOR));
        processor.add(getAction(
            ActionConstants.ACTION_INTERFACE_SERIALIZER_GENERATOR));
        processor.add(getAction(
            ActionConstants.ACTION_SOAP_OBJECT_BUILDER_GENERATOR));
        processor.add(getAction(
            ActionConstants.ACTION_LITERAL_OBJECT_SERIALIZER_GENERATOR));
        processor.add(getAction(
            ActionConstants.ACTION_CUSTOM_EXCEPTION_GENERATOR));
        processor.add(getAction(
            ActionConstants.ACTION_SOAP_FAULT_SERIALIZER_GENERATOR));
        processor.add(getAction(
            ActionConstants.ACTION_FAULT_EXCEPTION_BUILDER_GENERATOR));
        processor.add(getAction(
            ActionConstants.ACTION_SERIALIZER_REGISTRY_GENERATOR));
        processor.add(getAction(ActionConstants.ACTION_TIE_GENERATOR));
        processor.add(getAction(ActionConstants.ACTION_WSDL_GENERATOR));
        if (delegate != null) {
            delegate.postRegisterProcessorActions();
        }
    }
    
    /* Methods for localization of Messages */
    public void onError(Localizable msg) {
        if (delegate != null) {
            delegate.preOnError();
        }
        report(getMessage("wscompile.error", localizer.localize(msg)));
    }
    public void onWarning(Localizable msg) {
        report(getMessage("wscompile.warning", localizer.localize(msg)));
    }
    public void onInfo(Localizable msg) {
        report(getMessage("wscompile.info", localizer.localize(msg)));
    }
    
    protected EndpointInfo endpointInfo;
    protected WebServicesInfo webServicesInfo;
    protected File targetDirectory;
    protected boolean useModel;
    protected String additionalClasspath;
    protected Hashtable hashtable;
    protected ArrayList vector;
    protected boolean localUseWSIBasicProfile = false;
    
    private final static String PS = System.getProperty("path.separator");
    private final static char PSCHAR =
        System.getProperty("path.separator").charAt(0);
    private final static String FS = System.getProperty("file.separator");
    private final static char FSCHAR =
        System.getProperty("file.separator").charAt(0);
}
