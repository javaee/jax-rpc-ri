/*
 * $Id: ServiceInterfaceGenerator.java,v 1.2.2.1 2008-02-14 11:54:48 venkatajetti Exp $
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
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.Properties;

import com.sun.xml.rpc.processor.ProcessorAction;
import com.sun.xml.rpc.processor.ProcessorConstants;
import com.sun.xml.rpc.processor.ProcessorOptions;
import com.sun.xml.rpc.processor.config.Configuration;
import com.sun.xml.rpc.processor.model.Model;
import com.sun.xml.rpc.processor.model.Port;
import com.sun.xml.rpc.processor.model.Service;
import com.sun.xml.rpc.processor.model.java.JavaInterface;
import com.sun.xml.rpc.processor.util.GeneratedFileInfo;
import com.sun.xml.rpc.processor.util.IndentingWriter;
import com.sun.xml.rpc.processor.util.ProcessorEnvironment;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import com.sun.xml.rpc.util.VersionUtil;

/**
 *
 * @author JAX-RPC Development Team
 */
public class ServiceInterfaceGenerator implements ProcessorAction {

    private File sourceDir;
    private ProcessorEnvironment env;
    private String JAXRPCVersion;
    private boolean donotOverride;
    private String sourceVersion;

    public ServiceInterfaceGenerator() {
    }

    public void perform(
        Model model,
        Configuration config,
        Properties options) {

        try {
            env = (ProcessorEnvironment) config.getEnvironment();
            env.getNames().resetPrefixFactory();
            String key = ProcessorOptions.SOURCE_DIRECTORY_PROPERTY;
            String dirPath = options.getProperty(key);
            key = ProcessorOptions.DONOT_OVERRIDE_CLASSES;
            donotOverride =
                Boolean.valueOf(options.getProperty(key)).booleanValue();
            sourceDir = new File(dirPath);
            JAXRPCVersion =
                options.getProperty(ProcessorConstants.JAXRPC_VERSION);
            key = ProcessorOptions.JAXRPC_SOURCE_VERSION;
            this.sourceVersion = options.getProperty(key);

            for (Iterator iter = model.getServices(); iter.hasNext();) {
                Service service = (Service) iter.next();
                process(service);
            }
        } finally {
            sourceDir = null;
            env = null;
        }
    }

    private void process(Service service) {
        try {
            JavaInterface intf = (JavaInterface) service.getJavaInterface();
            String className = env.getNames().customJavaTypeClassName(intf);
            if (donotOverride && GeneratorUtil.classExists(env, className)) {
                log("Class " + className + " exists. Not overriding.");
                return;
            }
            log("creating service interface: " + className);
            File classFile =
                env.getNames().sourceFileForClass(
                    className,
                    className,
                    sourceDir,
                    env);

            /* the implementation of the Service Generated is
               added in */
            GeneratedFileInfo fi = new GeneratedFileInfo();
            fi.setFile(classFile);
            fi.setType(GeneratorConstants.FILE_TYPE_SERVICE);
            env.addGeneratedFile(fi);

            IndentingWriter out =
                new IndentingWriter(
                    new OutputStreamWriter(new FileOutputStream(classFile)));
            GeneratorBase.writePackage(out, className, JAXRPCVersion, sourceVersion);
            out.pln("import javax.xml.rpc.*;");
            out.pln();
            out.plnI(
                "public interface "
                    + Names.stripQualifier(className)
                    + " extends javax.xml.rpc.Service {");
            Iterator ports = service.getPorts();
            Port port;
            String portClass;
            String portName;
            while (ports.hasNext()) {
                port = (Port) ports.next();
                portClass = port.getJavaInterface().getName();
                portName = Names.getPortName(port);
                /* here we change the first character of the PortName
                   to Capital Letter */
                portName = env.getNames().validJavaClassName(portName);
                String getPortMethodStr = "public "+portClass+" get"+portName+"()";
                if (VersionUtil.isVersion101(sourceVersion) ||
                    VersionUtil.isVersion103(sourceVersion)) {
                    getPortMethodStr += ";";
                } else {
                     // CR-6660366, Merge from JavaCAPS RTS for backward compatibility
                    getPortMethodStr += " throws javax.xml.rpc.ServiceException;";
                }
                out.pln(getPortMethodStr);
            }
            out.pOln("}");
            out.close();

        } catch (Exception e) {
            throw new GeneratorException(
                "generator.nestedGeneratorError",
                new LocalizableExceptionAdapter(e));
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
}
