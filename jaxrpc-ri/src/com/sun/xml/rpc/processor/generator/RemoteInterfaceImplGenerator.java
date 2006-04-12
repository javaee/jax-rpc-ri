/*
 * $Id: RemoteInterfaceImplGenerator.java,v 1.1 2006-04-12 20:33:46 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.generator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import com.sun.xml.rpc.processor.ProcessorAction;
import com.sun.xml.rpc.processor.ProcessorOptions;
import com.sun.xml.rpc.processor.config.Configuration;
import com.sun.xml.rpc.processor.model.Model;
import com.sun.xml.rpc.processor.model.Port;
import com.sun.xml.rpc.processor.model.Service;
import com.sun.xml.rpc.processor.model.java.JavaInterface;
import com.sun.xml.rpc.processor.model.java.JavaMethod;
import com.sun.xml.rpc.processor.model.java.JavaParameter;
import com.sun.xml.rpc.processor.util.GeneratedFileInfo;
import com.sun.xml.rpc.processor.util.IndentingWriter;
import com.sun.xml.rpc.processor.util.ProcessorEnvironment;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;

/**
 *
 * @author JAX-RPC Development Team
 */
public class RemoteInterfaceImplGenerator implements ProcessorAction {
    private Port port;
    private boolean donotOverride;
    public RemoteInterfaceImplGenerator() {
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
            sourceDir = new File(dirPath);
            key = ProcessorOptions.DONOT_OVERRIDE_CLASSES;
            donotOverride =
                Boolean.valueOf(options.getProperty(key)).booleanValue();
            Set interfaceNames = new HashSet();

            for (Iterator iter = model.getServices(); iter.hasNext();) {
                Service service = (Service) iter.next();

                for (Iterator iter2 = service.getPorts(); iter2.hasNext();) {
                    Port port = (Port) iter2.next();
                    JavaInterface intf = port.getJavaInterface();
                    if (!interfaceNames.contains(intf.getName())) {
                        process(port);
                        interfaceNames.add(intf.getName());
                    }
                }
            }
        } finally {
            sourceDir = null;
            env = null;
        }
    }

    private void process(Port port) {
        JavaInterface intf = port.getJavaInterface();
        if (intf.getImpl() == null) {
            String className = env.getNames().interfaceImplClassName(intf);
            if ((donotOverride && GeneratorUtil.classExists(env, className))) {
                log("Class " + className + " exists. Not overriding.");
                return;
            }
            generateClassFor(className, port);
        }
    }

    private void generateClassFor(String className, Port port) {
        JavaInterface intf = port.getJavaInterface();
        try {
            File classFile =
                env.getNames().sourceFileForClass(
                    className,
                    className,
                    sourceDir,
                    env);
            /* here the file Generated is added to
               object along with its type */
            GeneratedFileInfo fi = new GeneratedFileInfo();
            fi.setFile(classFile);
            fi.setType(GeneratorConstants.FILE_TYPE_REMOTE_INTERFACE);
            env.addGeneratedFile(fi);

            IndentingWriter out =
                new IndentingWriter(
                    new OutputStreamWriter(new FileOutputStream(classFile)));
            GeneratorBase.writePackageOnly(out, className);
            out.plnI(
                "public class "
                    + Names.stripQualifier(className)
                    + " implements "
                    + env.getNames().customJavaTypeClassName(intf)
                    + ", java.rmi.Remote {");

            for (Iterator iter = intf.getMethods(); iter.hasNext();) {
                JavaMethod method = (JavaMethod) iter.next();
                out.p("public ");
                if (method.getReturnType() == null) {
                    out.p("void");
                } else {
                    out.p(method.getReturnType().getName());
                }
                out.p(" ");
                out.p(method.getName());
                out.p("(");
                boolean first = true;

                for (Iterator iter2 = method.getParameters();
                    iter2.hasNext();
                    ) {
                    JavaParameter parameter = (JavaParameter) iter2.next();
                    if (!first) {
                        out.p(", ");
                    }
                    if (parameter.isHolder()) {
                        out.p(
                            env.getNames().holderClassName(
                                port,
                                parameter.getType()));
                    } else {
                        out.p(
                            env.getNames().typeClassName(parameter.getType()));
                    }
                    out.p(" ");
                    out.p(parameter.getName());
                    first = false;
                }
                out.plnI(") throws ");
                Iterator exceptions = method.getExceptions();
                String exception;
                while (exceptions.hasNext()) {
                    exception = (String) exceptions.next();
                    out.p(exception + ", ");
                }
                out.pln(" java.rmi.RemoteException {");
                if (method.getReturnType() != null
                    && !method.getReturnType().getName().equals("void")) {
                    out.pln();
                    out.pln(
                        method.getReturnType().getName()
                            + " _retVal = "
                            + method.getReturnType().getInitString()
                            + ";");
                    out.pln("return _retVal;");
                }
                out.pOln("}");
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

    private File sourceDir;
    private ProcessorEnvironment env;
}
