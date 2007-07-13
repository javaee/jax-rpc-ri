/*
 * $Id: ServiceInterfaceGenerator.java,v 1.3 2007-07-13 23:36:02 ofung Exp $
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
                    getPortMethodStr += " throws ServiceException;";
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
