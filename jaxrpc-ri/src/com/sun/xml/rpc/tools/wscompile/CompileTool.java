/*
 * $Id: CompileTool.java,v 1.3 2007-07-13 23:36:35 ofung Exp $
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

package com.sun.xml.rpc.tools.wscompile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import com.sun.xml.rpc.processor.Processor;
import com.sun.xml.rpc.processor.ProcessorAction;
import com.sun.xml.rpc.processor.ProcessorConstants;
import com.sun.xml.rpc.processor.ProcessorNotificationListener;
import com.sun.xml.rpc.processor.ProcessorOptions;
import com.sun.xml.rpc.processor.config.Configuration;
import com.sun.xml.rpc.processor.config.ModelFileModelInfo;
import com.sun.xml.rpc.processor.config.NoMetadataModelInfo;
import com.sun.xml.rpc.processor.config.RmiModelInfo;
import com.sun.xml.rpc.processor.config.WSDLModelInfo;
import com.sun.xml.rpc.processor.config.parser.ConfigurationParser;

import com.sun.xml.rpc.processor.generator.CustomClassGenerator;
import com.sun.xml.rpc.processor.generator.CustomExceptionGenerator;
import com.sun.xml.rpc.processor.generator.EnumerationEncoderGenerator;
import com.sun.xml.rpc.processor.generator.EnumerationGenerator;
import com.sun.xml.rpc.processor.generator.FaultExceptionBuilderGenerator;
import com.sun.xml.rpc.processor.generator.HolderGenerator;
import com.sun.xml.rpc.processor.generator.InterfaceSerializerGenerator;
import com.sun.xml.rpc.processor.generator.LiteralObjectSerializerGenerator;
import com.sun.xml.rpc.processor.generator.RemoteInterfaceGenerator;
import com.sun.xml.rpc.processor.generator.RemoteInterfaceImplGenerator;
import com.sun.xml.rpc.processor.generator.SerializerRegistryGenerator;
import com.sun.xml.rpc.processor.generator.ServiceGenerator;
import com.sun.xml.rpc.processor.generator.ServiceInterfaceGenerator;
import com.sun.xml.rpc.processor.generator.ServletConfigGenerator;
import com.sun.xml.rpc.processor.generator.StubGenerator;
import com.sun.xml.rpc.processor.generator.SOAPFaultSerializerGenerator;
import com.sun.xml.rpc.processor.generator.SOAPObjectBuilderGenerator;
import com.sun.xml.rpc.processor.generator.SOAPObjectSerializerGenerator;
import com.sun.xml.rpc.processor.generator.TieGenerator;
import com.sun.xml.rpc.processor.generator.WSDLGenerator;

import com.sun.xml.rpc.processor.model.Model;
import com.sun.xml.rpc.processor.model.ModelProperties;
import com.sun.xml.rpc.processor.util.ClientProcessorEnvironment;
import com.sun.xml.rpc.processor.util.GeneratedFileInfo;
import com.sun.xml.rpc.processor.util.ModelWriter;
import com.sun.xml.rpc.processor.util.ProcessorEnvironment;
import com.sun.xml.rpc.processor.util.XMLModelWriter;
import com.sun.xml.rpc.spi.tools.CompileToolDelegate;
import com.sun.xml.rpc.tools.plugin.ToolPluginConstants;
import com.sun.xml.rpc.tools.plugin.ToolPluginFactory;

import com.sun.xml.rpc.util.JavaCompilerHelper;
import com.sun.xml.rpc.util.JAXRPCClassFactory;
import com.sun.xml.rpc.util.ToolBase;
import com.sun.xml.rpc.util.Version;
import com.sun.xml.rpc.util.VersionUtil;
import com.sun.xml.rpc.util.localization.Localizable;

/**
 *
 * @author JAX-RPC Development Team
 */
public class CompileTool extends ToolBase
    implements ProcessorNotificationListener,
        com.sun.xml.rpc.spi.tools.CompileTool {
    
    public CompileTool(OutputStream out, String program) {
        super(out, program);
        listener = this;
    }
    
    protected void initialize() {
        super.initialize();
        properties = new Properties();
        actions = new HashMap();
        actions.put(ActionConstants.ACTION_REMOTE_INTERFACE_GENERATOR,
            new RemoteInterfaceGenerator());
        actions.put(ActionConstants.ACTION_REMOTE_INTERFACE_IMPL_GENERATOR,
            new RemoteInterfaceImplGenerator());
        actions.put(ActionConstants.ACTION_CUSTOM_CLASS_GENERATOR,
            new CustomClassGenerator());
        actions.put(ActionConstants.ACTION_SOAP_OBJECT_SERIALIZER_GENERATOR,
            new SOAPObjectSerializerGenerator());
        actions.put(ActionConstants.ACTION_INTERFACE_SERIALIZER_GENERATOR,
            new InterfaceSerializerGenerator());
        actions.put(ActionConstants.ACTION_SOAP_OBJECT_BUILDER_GENERATOR,
            new SOAPObjectBuilderGenerator());
        actions.put(ActionConstants.ACTION_LITERAL_OBJECT_SERIALIZER_GENERATOR,
            new LiteralObjectSerializerGenerator());
        actions.put(ActionConstants.ACTION_STUB_GENERATOR, new StubGenerator());
        actions.put(ActionConstants.ACTION_TIE_GENERATOR, new TieGenerator());
        actions.put(ActionConstants.ACTION_SERVLET_CONFIG_GENERATOR,
            new ServletConfigGenerator());
        actions.put(ActionConstants.ACTION_WSDL_GENERATOR, new WSDLGenerator());
        actions.put(ActionConstants.ACTION_HOLDER_GENERATOR,
            new HolderGenerator());
        actions.put(ActionConstants.ACTION_SERVICE_INTERFACE_GENERATOR,
            new ServiceInterfaceGenerator());
        actions.put(ActionConstants.ACTION_SERVICE_GENERATOR,
            new ServiceGenerator());
        actions.put(ActionConstants.ACTION_SERIALIZER_REGISTRY_GENERATOR,
            new SerializerRegistryGenerator());
        actions.put(ActionConstants.ACTION_CUSTOM_EXCEPTION_GENERATOR,
            new CustomExceptionGenerator());
        actions.put(ActionConstants.ACTION_SOAP_FAULT_SERIALIZER_GENERATOR,
            new SOAPFaultSerializerGenerator());
        actions.put(ActionConstants.ACTION_ENUMERATION_GENERATOR,
            new EnumerationGenerator());
        actions.put(ActionConstants.ACTION_ENUMERATION_ENCODER_GENERATOR,
            new EnumerationEncoderGenerator());
        actions.put(ActionConstants.ACTION_FAULT_EXCEPTION_BUILDER_GENERATOR,
            new FaultExceptionBuilderGenerator());
    }
    
    /* SPI methods */
    
    public com.sun.xml.rpc.spi.tools.ProcessorEnvironment getEnvironment() {
        return environment;
    }
    
    public com.sun.xml.rpc.spi.tools.Processor getProcessor() {
        return processor;
    }
    
    public void setDelegate(CompileToolDelegate delegate) {
        this.delegate = delegate;
    }
    
    protected boolean parseArguments(String[] args) {
        String debugModelFileName = null;
        String modelFileName = null;
        
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("")) {
                args[i] = null;
            } else if (args[i].equals("-g")) {
                compilerDebug = true;
                args[i] = null;
            } else if (args[i].equals("-O")) {
                compilerOptimize = true;
                args[i] = null;
            } else if (args[i].equals("-verbose")) {
                verbose = true;
                args[i] = null;
            } else if (args[i].equals("-import")) {
                if (mode != MODE_UNSPECIFIED) {
                    onError(getMessage("wscompile.tooManyModesSpecified"));
                    usage();
                    return false;
                }
                mode = MODE_IMPORT;
                dontGenerateWrapperClasses = true;
                args[i] = null;
            } else if (args[i].equals("-define")) {
                if (mode != MODE_UNSPECIFIED) {
                    onError(getMessage("wscompile.tooManyModesSpecified"));
                    usage();
                    return false;
                }
                mode = MODE_DEFINE;
                args[i] = null;
                
                //bugfix: 4916204, change -target switch to -source
            } else if (args[i].equals("-source")) {
                if ((i + 1) < args.length) {
                    if (targetVersion != null) {
                        onError(getMessage("wscompile.duplicateOption",
                            "-source"));
                        usage();
                        return false;
                    }
                    args[i] = null;
                    targetVersion = new String(args[++i]);
                    args[i] = null;
                    
                } else {
                    onError(getMessage("wscompile.missingOptionArgument",
                        "-source"));
                    usage();
                    return false;
                }
                if (targetVersion.length() == 0) {
                    onError(getMessage("wscompile.invalidOption", args[i]));
                    usage();
                    return false;
                }
                if (!VersionUtil.isValidVersion(targetVersion)) {
                    onError(getMessage("wscompile.invalidTargetVersion",
                        targetVersion));
                    usage();
                    return false;
                }
            } else if (args[i].startsWith("-gen")) {
                if (mode != MODE_UNSPECIFIED) {
                    onError(getMessage("wscompile.tooManyModesSpecified"));
                    usage();
                    return false;
                }
                if (args[i].equals("-gen") || args[i].equals("-gen:client")) {
                    mode = MODE_GEN_CLIENT;
                    args[i] = null;
                } else if (args[i].equals("-gen:server")) {
                    mode = MODE_GEN_SERVER;
                    args[i] = null;
                } else if (args[i].equals("-gen:both")) {
                    mode = MODE_GEN_BOTH;
                    args[i] = null;
                } else {
                    onError(getMessage("wscompile.invalidOption", args[i]));
                    usage();
                    return false;
                }
            } else if (args[i].equals("-version")) {
                report(getVersion());
                doNothing = true;
                args[i] = null;
                return true;
            } else if (args[i].equals("-keep")) {
                keepGenerated = true;
                args[i] = null;
            } else if (args[i].equals("-d")) {
                if ((i + 1) < args.length) {
                    if (destDir != null) {
                        onError(getMessage("wscompile.duplicateOption", "-d"));
                        usage();
                        return false;
                    }
                    args[i] = null;
                    destDir = new File(args[++i]);
                    args[i] = null;
                    if (!destDir.exists()) {
                        onError(getMessage("wscompile.noSuchDirectory",
                            destDir.getPath()));
                        usage();
                        return false;
                    }
                } else {
                    onError(getMessage(
                        "wscompile.missingOptionArgument", "-d"));
                    usage();
                    return false;
                }
            } else if (args[i].equals("-nd")) {
                if ((i + 1) < args.length) {
                    if (nonclassDestDir != null) {
                        onError(getMessage("wscompile.duplicateOption", "-nd"));
                        usage();
                        return false;
                    }
                    args[i] = null;
                    nonclassDestDir = new File(args[++i]);
                    args[i] = null;
                    if (!nonclassDestDir.exists()) {
                        onError(getMessage("wscompile.noSuchDirectory",
                            nonclassDestDir.getPath()));
                        usage();
                        return false;
                    }
                } else {
                    onError(getMessage("wscompile.missingOptionArgument",
                        "-nd"));
                    usage();
                    return false;
                }
            } else if (args[i].equals("-s")) {
                if ((i + 1) < args.length) {
                    if (sourceDir != null) {
                        onError(getMessage("wscompile.duplicateOption", "-s"));
                        usage();
                        return false;
                    }
                    args[i] = null;
                    sourceDir = new File(args[++i]);
                    args[i] = null;
                    if (!sourceDir.exists()) {
                        onError(getMessage("wscompile.noSuchDirectory",
                            sourceDir.getPath()));
                        usage();
                        return false;
                    }
                } else {
                    onError(getMessage(
                        "wscompile.missingOptionArgument", "-s"));
                    usage();
                    return false;
                }
            } else if (args[i].equals("-model")) {
                if ((i + 1) < args.length) {
                    if (modelFile != null) {
                        onError(getMessage(
                            "wscompile.duplicateOption", "-model"));
                        usage();
                        return false;
                    }
                    args[i] = null;
                    modelFileName = args[++i];
                    args[i] = null;
                } else {
                    onError(getMessage("wscompile.missingOptionArgument",
                        "-model"));
                    usage();
                    return false;
                }
            } else if (args[i].equals("-classpath") || args[i].equals("-cp")) {
                if ((i + 1) < args.length) {
                    if (userClasspath != null) {
                        onError(getMessage("wscompile.duplicateOption",
                            args[i]));
                        usage();
                        return false;
                    }
                    args[i] = null;
                    userClasspath = args[++i];
                    args[i] = null;
                }
            } else if (args[i].startsWith("-f:") ||
                args[i].startsWith("-features:")) {
                    
                String featureString =
                    args[i].substring(args[i].startsWith("-f:") ? 3 : 10);
                StringTokenizer tokenizer =
                    new StringTokenizer(featureString, ",");
                while (tokenizer.hasMoreTokens()) {
                    String feature = tokenizer.nextToken().trim();
                    if (feature.equals("datahandleronly")) {
                        useDataHandlerOnly = true;
                    } else if (feature.equals("nodatabinding")) {
                        noDataBinding = true;
                    } else if (feature.equals("noencodedtypes")) {
                        noEncodedTypes = true;
                    } else if (feature.equals("nomultirefs")) {
                        noMultiRefEncoding = true;
                    } else if (feature.equals("novalidation")) {
                        noValidation = true;
                    } else if (feature.equals("explicitcontext")) {
                        explicitServiceContext = true;
                    } else if (feature.equals("norpcstructures")) {
                        dontGenerateRPCStructures = true;
                    } else if (feature.startsWith("infix=") ||
                        feature.startsWith("infix:")) {
                            
                        String value = feature.substring(6);
                        if (value.length() == 0) {
                            onError(getMessage("wscompile.invalidFeatureSyntax",
                                "infix"));
                            usage();
                            return false;
                        }
                        serializerInfix = value;
                    } else if (feature.equals("searchschema")) {
                        searchSchemaForSubtypes = true;
                    } else if (feature.equals("serializeinterfaces")) {
                        serializeInterfaces = true;
                    } else if (feature.equals("documentliteral")) {
                        useDocLiteralEncoding = true;
                    } else if (feature.equals("rpcliteral")) {
                        useRPCLiteralEncoding = true;
                    } else if (feature.equals("wsi")) {
                        useWSIBasicProfile = true;
                    } else if (feature.equals("useonewayoperations")) {
                        generateOneWayMethods = true;
                    } else if (feature.equals("resolveidref")) {
                        resolveIDREF = true;
                    } else if (feature.equals("strict")) {
                        strictCompliance = true;
                    } else if (feature.equals("jaxbenumtype")) {
                        jaxbEnumType = true;
                    } else if (feature.equals("unwrap")) {
                        if (wrapperFlagSeen && !unwrapDocLitWrappers) {
                            onError(getMessage("wscompile.bothWrapperFlags"));
                        }
                        wrapperFlagSeen = true;
                        unwrapDocLitWrappers = true;
                    } else if (feature.equals("donotoverride")) {
                        donotOverride = true;
                    } else if (feature.equals("donotunwrap")) {
                        if (wrapperFlagSeen && unwrapDocLitWrappers) {
                            onError(getMessage("wscompile.bothWrapperFlags"));
                        }
                        wrapperFlagSeen = true;
                        unwrapDocLitWrappers = false;
                    } else {
                        onError(getMessage("wscompile.unknownFeature",
                            feature));
                        usage();
                        return false;
                    }
                }
                args[i] = null;
            } else if (args[i].startsWith("-httpproxy:")) {
                String value = args[i].substring(11);
                if (value.length() == 0) {
                    onError(getMessage("wscompile.invalidOption", args[i]));
                    usage();
                    return false;
                }
                int index = value.indexOf(':');
                if (index == -1) {
                    System.setProperty("proxySet", TRUE);
                    System.setProperty("proxyHost", value);
                    System.setProperty("proxyPort", "8080");
                } else {
                    System.setProperty("proxySet", TRUE);
                    System.setProperty("proxyHost", value.substring(0, index));
                    System.setProperty("proxyPort", value.substring(index + 1));
                }
                args[i] = null;
            } else if (args[i].equals("-Xprintstacktrace")) {
                printStackTrace = true;
                args[i] = null;
            } else if (args[i].equals("-Xserializable")) {
                serializable = true;
                args[i] = null;
            } else if (args[i].startsWith("-Xdebugmodel")) {
                int index = args[i].indexOf(':');
                if (index == -1) {
                    onError(getMessage("wscompile.invalidOption", args[i]));
                    usage();
                    return false;
                }
                debugModelFileName = args[i].substring(index + 1);
                args[i] = null;
            } else if (args[i].startsWith("-help")) {
                help();
                return false;
            }
        }

		// bug# 4984891
		if (modelFileName != null) {
//			if (nonclassDestDir != null) {
//				modelFileName =
//					nonclassDestDir
//						+ System.getProperty("file.separator")
//						+ modelFileName;
//			}
			modelFile = new File(modelFileName);
			if (modelFile.isDirectory() ||
				(modelFile.getParentFile() != null &&
				!modelFile.getParentFile().exists())) {
                            
				onError(getMessage("wscompile.invalidModel",
					modelFile.getPath()));
				usage();
				return false;
			}
		}

        // check here as order doesn't matter.(-Xdebugmodel depends on -nd)
        if (debugModelFileName != null) {
			if (nonclassDestDir != null) {
				debugModelFileName =
					nonclassDestDir
						+ System.getProperty("file.separator")
						+ debugModelFileName;
			}
            debugModelFile = new File(debugModelFileName);
            if (debugModelFile.isDirectory() ||
                (debugModelFile.getParentFile() != null &&
                !debugModelFile.getParentFile().exists())) {
                
                onError(getMessage("wscompile.invalidPath",
                    debugModelFile.getPath()));
                usage();
                return false;
            }
        }
        
        /*
         * Take care of plugins-spcific arguments
         */
        Iterator iter = ToolPluginFactory.getInstance().getExtensions(
            ToolPluginConstants.WSCOMPILE_PLUGIN,
            ToolPluginConstants.WSCOMPILE_USAGE_EXT_POINT);
        while(iter != null && iter.hasNext()) {
            UsageIf plugin = (UsageIf)iter.next();
            UsageIf.UsageError error = new UsageIf.UsageError();
            if (!plugin.parseArguments(args, error)) {
                onError(error.msg);
                usage();
                return false;
            }
        }
        
        for (int i = 0; i < args.length; i++) {
            if (args[i] != null) {
                if (args[i].startsWith("-")) {
                    onError(getMessage("wscompile.invalidOption", args[i]));
                    usage();
                    return false;
                } else if (configFile != null) {
                    onError(getMessage("wscompile.multipleConfigurationFiles",
                        args[i]));
                    usage();
                    return false;
                }
                configFile = new File(args[i]);
                args[i] = null;
                if (!configFile.exists()) {
                    onError(getMessage("wscompile.fileNotFound",
                        configFile.getPath()));
                    usage();
                    return false;
                }
            }
        }
        
        if (mode == MODE_UNSPECIFIED) {
            onError(getMessage("wscompile.noModeSpecified"));
            usage();
            return false;
        }
        
        if (mode != MODE_IMPORT) {
            dontGenerateRPCStructures = false;
        }
        
        if (configFile == null) {
            onError(getMessage("wscompile.missingConfigurationFile"));
            usage();
            return false;
        }
        
        // -source flag overrides other conflicting flags
        if(targetVersion != null) {
            resetOptionsForTargetVersion();
        }
        
        /* check if user mentioned optional flag togather with
         * "strictCompliance" flag
         */
        return checkForConflictingFlags();
    }
    
    /**
     *
     */
    protected void resetOptionsForTargetVersion() {
        
        /* these options are not in 1.0.1 and 1.0.3:
         * documentliteral, rpcliteral, jaxbenumtype, resolveidref,
         * strict, useonewayoperations, wsi, norpcstructures (not only in 1.0.1)
         */
        ArrayList optionList = null;
        if(VersionUtil.isVersion101(targetVersion) ||
            VersionUtil.isVersion103(targetVersion)) {
                
            optionList = new ArrayList();
            if (dontGenerateWrapperClasses) {
                dontGenerateWrapperClasses = false;
            }
            if (donotOverride) {
                donotOverride = false;
            }
            if (serializable) {
                serializable = false;
            }
            if(useDocLiteralEncoding) {
                useDocLiteralEncoding = false;
                optionList.add("documentliteral");
            }
            if(useRPCLiteralEncoding) {
                useRPCLiteralEncoding = false;
                optionList.add("rpcliteral");
            }
            if(useWSIBasicProfile) {
                useWSIBasicProfile = false;
                optionList.add("wsi");
            }
            if(jaxbEnumType) {
                jaxbEnumType = false;
                optionList.add("jaxbenumType");
            }
            if(resolveIDREF) {
                resolveIDREF = false;
                optionList.add("resolveidref");
            }
            if(strictCompliance) {
                strictCompliance = false;
                optionList.add("strict");
            }
            if(generateOneWayMethods) {
                generateOneWayMethods = false;
                optionList.add("useonewayoperations");
            }
            if(VersionUtil.isVersion101(targetVersion) &&
                dontGenerateRPCStructures) {
                    
                dontGenerateRPCStructures = false;
                optionList.add("norpcstructures");
            }
        }
        if(optionList != null && !optionList.isEmpty()) {
            StringBuffer str = new StringBuffer();
            for(Iterator iter = optionList.iterator(); iter.hasNext();) {
                str.append((String)iter.next());
                if(iter.hasNext())
                    str.append(", ");
            }
            onWarning(getMessage("wscompile.conflictingFeature.sourceVersion",
                new Object[] {targetVersion, str.toString()}));
        }
    }
    
    private boolean checkForConflictingFlags() {
        
        //as of now, optional feature IDREF flag: resolveIDREF
        if (strictCompliance && resolveIDREF) {
            onError(getMessage("wscompile.conflictingFeatureRequest",
                new Object[] { "resolveIDREF", "strictCompliance" }));
            return false;
        }
        return true;
    }
    
    public Localizable getVersion() {
        return getMessage("wscompile.version",
            Version.PRODUCT_NAME,
            Version.VERSION_NUMBER,
            Version.BUILD_NUMBER);
    }
    
    public String getVersionString() {
        return localizer.localize(getVersion());
    }
    
    protected void usage() {
        report(getMessage("wscompile.usage", program));
    }

    protected void help() {
        report(getMessage("wscompile.help", program));
        
        /*
         * print plugins-spcific usage
         */
        Iterator i = ToolPluginFactory.getInstance().getExtensions(
        ToolPluginConstants.WSCOMPILE_PLUGIN,
        ToolPluginConstants.WSCOMPILE_USAGE_EXT_POINT);
        while(i != null && i.hasNext()) {
            UsageIf plugin = (UsageIf)i.next();
            report(plugin.getOptionsUsage());
        }
        report(getMessage("wscompile.usage.features"));
        report(getMessage("wscompile.usage.internal"));
        report(getMessage("wscompile.usage.examples"));
        
    }
    
    ////////
    
    public void run() throws Exception {
        
        if (doNothing) {
            return;
        }
        try {
            boolean genClient = (mode == MODE_GEN_BOTH) ||
                (mode == MODE_GEN_CLIENT);
            beforeHook();
            
            environment = createEnvironment();
            if (delegate != null) {
                configuration = (Configuration)delegate.createConfiguration();
            } 
			/* else {
                configuration = (Configuration)createConfiguration();
            } */

			// if there is no delegate or the delegate returns an null configuration, use the default
            if (configuration == null) {
                configuration = (Configuration)createConfiguration();
            }

            
            // ignore -source option if the processing is thru model
            if((targetVersion != null) &&
                (configuration.getModelInfo() instanceof ModelFileModelInfo)) {
                    
                onWarning(getMessage(
                    "wscompile.warning.ignoringTargetVersionForModel",
                    ((ModelFileModelInfo)
                        configuration.getModelInfo()).getLocation(),
                    targetVersion));
                targetVersion = null;
                
                /* beforeHook is already run, so we need to reset
                 * the targetVersion property
                 */
                properties.setProperty(ProcessorOptions.JAXRPC_SOURCE_VERSION,
                    getSourceVersion());
            }
            
            //bug fix:4904604
            // initialize JAXRPCFactory with the target version
            JAXRPCClassFactory.newInstance().setSourceVersion(
                properties.getProperty(ProcessorOptions.JAXRPC_SOURCE_VERSION));
            setEnvironmentValues(environment);
            if (genClient &&
                configuration.getModelInfo() instanceof RmiModelInfo) {
                    
                onWarning(getMessage("wscompile.warning.seimode"));
            }
            processor = new Processor(configuration, properties);
            processor.runModeler();
			if (genClient) {
				Model model = (Model) processor.getModel();

				// model may be null if a newer model is
				// read by an older runtime
				if (model != null) {
					if (configuration.getModelInfo()
						instanceof ModelFileModelInfo) {

						String modelerName =
							(String) model.getProperty(
								ModelProperties.PROPERTY_MODELER_NAME);
						if (modelerName != null
							&& modelerName.equals(
								"com.sun.xml.rpc.processor.modeler.rmi.RmiModeler")) {

							onWarning(
								getMessage("wscompile.warning.modelfilemode"));
						}
					}
				}
			}
            withModelHook();
            registerProcessorActions(processor);
            processor.runActions();
            if (environment.getErrorCount() == 0) {
                compileGeneratedClasses();
            }
            afterHook();
        } finally {
            if (!keepGenerated) {
                removeGeneratedFiles();
            }
            if (environment != null) {
                environment.shutdown();
            }
        }
    }
    
    public boolean wasSuccessful() {
        return environment == null || environment.getErrorCount() == 0;
    }
    
    protected String getGenericErrorMessage() {
        return "wscompile.error";
    }
    
    protected String getResourceBundleName() {
        return "com.sun.xml.rpc.resources.wscompile";
    }
    
    public void printStackTrace(Throwable t) {
        if (printStackTrace) {
            if (environment != null) {
                environment.printStackTrace(t);
            } else {
                super.printStackTrace(t);
            }
        }
    }
    
    // processing hooks
    protected void beforeHook() {
        if (destDir == null) {
            destDir = new File(".");
        }
        if (sourceDir == null) {
            sourceDir = destDir;
        }
        if (nonclassDestDir == null) {
            nonclassDestDir = destDir;
        }
        
        properties.setProperty(ProcessorConstants.JAXRPC_VERSION,
            getVersionString());
        properties.setProperty(ProcessorOptions.SOURCE_DIRECTORY_PROPERTY,
            sourceDir.getAbsolutePath());
        properties.setProperty(ProcessorOptions.DESTINATION_DIRECTORY_PROPERTY,
            destDir.getAbsolutePath());
        properties.setProperty(
            ProcessorOptions.NONCLASS_DESTINATION_DIRECTORY_PROPERTY,
            nonclassDestDir.getAbsolutePath());
        properties.setProperty(ProcessorOptions.ENCODE_TYPES_PROPERTY,
            (noEncodedTypes ? FALSE : TRUE));
        properties.setProperty(ProcessorOptions.MULTI_REF_ENCODING_PROPERTY,
            (noMultiRefEncoding ? FALSE : TRUE));
        properties.setProperty(ProcessorOptions.VALIDATE_WSDL_PROPERTY,
            (noValidation ? FALSE : TRUE));
        properties.setProperty(
            ProcessorOptions.EXPLICIT_SERVICE_CONTEXT_PROPERTY,
            (explicitServiceContext ? TRUE : FALSE));
        properties.setProperty(ProcessorOptions.PRINT_STACK_TRACE_PROPERTY,
            (printStackTrace ? TRUE : FALSE));
        properties.setProperty(ProcessorOptions.NO_DATA_BINDING_PROPERTY,
            (noDataBinding ? TRUE : FALSE));
        properties.setProperty(ProcessorOptions.SERIALIZE_INTERFACES_PROPERTY,
            (serializeInterfaces ? TRUE : FALSE));
        properties.setProperty(ProcessorOptions.USE_DATA_HANDLER_ONLY,
            (useDataHandlerOnly ? TRUE : FALSE));
        properties.setProperty(ProcessorOptions.SEARCH_SCHEMA_FOR_SUBTYPES,
            (searchSchemaForSubtypes ? TRUE : FALSE));
        properties.setProperty(ProcessorOptions.DONT_GENERATE_RPC_STRUCTURES,
            (dontGenerateRPCStructures ? TRUE : FALSE));
        properties.setProperty(ProcessorOptions.USE_DOCUMENT_LITERAL_ENCODING,
            (useDocLiteralEncoding ? TRUE : FALSE));
        properties.setProperty(ProcessorOptions.USE_RPC_LITERAL_ENCODING,
            (useRPCLiteralEncoding ? TRUE : FALSE));
        properties.setProperty(ProcessorOptions.USE_WSI_BASIC_PROFILE,
            (useWSIBasicProfile ? TRUE : FALSE));
        properties.setProperty(ProcessorOptions.GENERATE_ONE_WAY_OPERATIONS,
            (generateOneWayMethods ? TRUE : FALSE));
        properties.setProperty(ProcessorOptions.ENABLE_IDREF,
            (resolveIDREF ? TRUE : FALSE));
        properties.setProperty(ProcessorOptions.STRICT_COMPLIANCE,
            (strictCompliance ? TRUE : FALSE));
        properties.setProperty(ProcessorOptions.JAXB_ENUMTYPE,
            (jaxbEnumType ? TRUE : FALSE));
        properties.setProperty(ProcessorOptions.JAXRPC_SOURCE_VERSION,
            getSourceVersion());
        properties.setProperty(ProcessorOptions.UNWRAP_DOC_LITERAL_WRAPPERS,
            (unwrapDocLitWrappers ? TRUE : FALSE));
        properties.setProperty(ProcessorOptions.DONT_GENERATE_WRAPPER_CLASSES,
            ((dontGenerateWrapperClasses &&
            (strictCompliance || useWSIBasicProfile))  ? TRUE : FALSE));
        properties.setProperty(ProcessorOptions.GENERATE_SERIALIZABLE_IF,
            (serializable ? "true" : "false"));
        properties.setProperty(ProcessorOptions.DONOT_OVERRIDE_CLASSES,
            (donotOverride ? "true" : "false"));
    }
    
    /**
     * @return
     */
    protected String getSourceVersion() {
        if (targetVersion == null) {
            
            /* no target specified, defaulting to the default version,
             * which is the latest version
             */
            return VersionUtil.JAXRPC_VERSION_DEFAULT;
        }
        return targetVersion;
    }
    
    protected void withModelHook() {
        /*
         * plugins may update model
         */
        Model model = (com.sun.xml.rpc.processor.model.Model)processor.getModel();
        Iterator iter = ToolPluginFactory.getInstance().getExtensions(
            ToolPluginConstants.WSCOMPILE_PLUGIN,
            ToolPluginConstants.WSCOMPILE_MODEL_EXT_POINT);
        while(iter != null && iter.hasNext()) {
            ModelIf plugin = (ModelIf)iter.next();
            ModelIf.ModelProperty property = new ModelIf.ModelProperty();
            plugin.updateModel(property);
            if (property.attr != null) {
                model.setProperty(property.attr, property.value);
            }
        }        
    }
    
    protected void afterHook() {
        if (delegate != null) {
            delegate.postRun();
        }
    }
    
    public void removeGeneratedFiles() {
        environment.deleteGeneratedFiles();
    }
    
    protected ProcessorEnvironment createEnvironment() throws Exception {
        String cpath = userClasspath +
            File.pathSeparator +
            System.getProperty("java.class.path");
        ProcessorEnvironment env =
            new ClientProcessorEnvironment(System.out, cpath, listener);
        return env;
    }
    
    //bug fix:4904604
    protected void setEnvironmentValues(ProcessorEnvironment env) {
        
        //first of all, set the Names as per target version
        ((ClientProcessorEnvironment) env).setNames(
            JAXRPCClassFactory.newInstance().createNames());
        
        if (serializerInfix != null) {
            env.getNames().setSerializerNameInfix(serializerInfix);
        }
        int envFlags = env.getFlags();
        envFlags |= ProcessorEnvironment.F_WARNINGS;
        if (verbose) {
            envFlags |= ProcessorEnvironment.F_VERBOSE;
        }
        env.setFlags(envFlags);
    }
    
    protected com.sun.xml.rpc.spi.tools.Configuration createConfiguration()
        throws Exception {
            
        FileInputStream ins = new FileInputStream(configFile);
        ConfigurationParser parser = new ConfigurationParser(environment);
        return (Configuration) parser.parse(ins);
    }
    
    protected void registerProcessorActions(Processor processor) {
        if (modelFile != null) {
            try {
                processor.add(new XMLModelWriter(modelFile));
            } catch (FileNotFoundException e) {
                
                // should not happen
                environment.error(getMessage("wscompile.invalidModel",
                    modelFile.getPath()));
            }
        }
        
        if (debugModelFile != null) {
            try {
                processor.add(new ModelWriter(debugModelFile));
            } catch (FileNotFoundException e) {
                
                // should not happen
                environment.error(getMessage("wscompile.invalidPath",
                    debugModelFile.getPath()));
            }
        }
        
        /*
         * plugins may register processor actions
         */
        Iterator iter = ToolPluginFactory.getInstance().getExtensions(
            ToolPluginConstants.WSCOMPILE_PLUGIN,
            ToolPluginConstants.WSCOMPILE_PROC_ACTION_EXT_POINT);
        while(iter != null && iter.hasNext()) {
            ProcessorActionsIf plugin = (ProcessorActionsIf)iter.next();
            plugin.registerActions(processor);
        }
        
        
        boolean genStub = false;
        boolean genService = false;
        boolean genServiceInterface = false;
        boolean genTie = false;
        boolean genWsdl = false;
        boolean genSerializer = false;
        boolean genInterface = false;
        boolean genInterfaceTemplate = false;
        boolean genCustomClasses = false;
        
        if (mode == MODE_GEN_CLIENT || mode == MODE_GEN_BOTH) {
            genStub = true;
            genService = true;
            genServiceInterface = true;
            genInterface = true;
            genCustomClasses = true;
            genSerializer = true;
        }
        
        if (mode == MODE_GEN_SERVER || mode == MODE_GEN_BOTH) {
            genTie = true;
            genInterface = true;
            genCustomClasses = true;
            genSerializer = true;
            genWsdl = true;
        }
        
        if (mode == MODE_IMPORT) {
            if (!(configuration.getModelInfo() instanceof WSDLModelInfo)) {
                environment.error(
                    getMessage("wscompile.importRequiresWsdlConfig"));
            }
            genInterface = true;
            genInterfaceTemplate = true;
            genServiceInterface = true;
            genCustomClasses = true;
        }
        
        if (mode == MODE_DEFINE) {
            if (!(configuration.getModelInfo() instanceof RmiModelInfo)) {
                environment.error(
                    getMessage("wscompile.defineRequiresServiceConfig"));
            }
            genWsdl = true;
        }
        
        /* avoid generating certain artifacts that may conflict with
         * what the user provided
         */
        if (processor.getModel() != null) {
            if (configuration.getModelInfo() instanceof RmiModelInfo) {
                genInterface = false;
            } else if (configuration.getModelInfo() instanceof WSDLModelInfo) {
                genWsdl = false;
            } else if (configuration.getModelInfo() instanceof
                NoMetadataModelInfo) {
                    
                genInterface = false;
                genWsdl = false;
            }
        }
        
        if (genServiceInterface) {
            processor.add(getAction(
                ActionConstants.ACTION_SERVICE_INTERFACE_GENERATOR));
        }
        if (genService) {
            processor.add(getAction(ActionConstants.ACTION_SERVICE_GENERATOR));
        }
        if (genInterface) {
            processor.add(getAction(
                ActionConstants.ACTION_REMOTE_INTERFACE_GENERATOR));
            processor.add(getAction(
                ActionConstants.ACTION_ENUMERATION_GENERATOR));
            processor.add(getAction(
                ActionConstants.ACTION_CUSTOM_EXCEPTION_GENERATOR));
            processor.add(getAction(ActionConstants.ACTION_HOLDER_GENERATOR));
        }
        if (genCustomClasses) {
            processor.add(getAction(
                ActionConstants.ACTION_CUSTOM_CLASS_GENERATOR));
        }
        if (genInterfaceTemplate) {
            processor.add(getAction(
                ActionConstants.ACTION_REMOTE_INTERFACE_IMPL_GENERATOR));
        }
        if (genSerializer) {
            processor.add(getAction(
                ActionConstants.ACTION_ENUMERATION_ENCODER_GENERATOR));
            processor.add(getAction(
                ActionConstants.ACTION_INTERFACE_SERIALIZER_GENERATOR));
            processor.add(getAction(
                ActionConstants.ACTION_SOAP_OBJECT_SERIALIZER_GENERATOR));
            processor.add(getAction(
                ActionConstants.ACTION_SOAP_OBJECT_BUILDER_GENERATOR));
            processor.add(getAction(
                ActionConstants.ACTION_LITERAL_OBJECT_SERIALIZER_GENERATOR));
            processor.add(getAction(
                ActionConstants.ACTION_SOAP_FAULT_SERIALIZER_GENERATOR));
            processor.add(getAction(
                ActionConstants.ACTION_FAULT_EXCEPTION_BUILDER_GENERATOR));
        }
        if (genStub) {
            processor.add(getAction(ActionConstants.ACTION_STUB_GENERATOR));
        }
        if (genTie) {
            processor.add(getAction(ActionConstants.ACTION_TIE_GENERATOR));
        }
        if (genSerializer) {
            processor.add(getAction(
                ActionConstants.ACTION_SERIALIZER_REGISTRY_GENERATOR));
        }
        if (genWsdl) {
            processor.add(getAction(ActionConstants.ACTION_WSDL_GENERATOR));
        }
        if (delegate != null) {
            delegate.postRegisterProcessorActions();
        }
    }
    
    protected String createClasspathString() {
        if (userClasspath == null) {
            userClasspath = "";
        }
        return userClasspath + File.pathSeparator +
            System.getProperty("java.class.path");
    }
    
    protected void compileGeneratedClasses() {
        List sourceFiles = new ArrayList();
        
        for (Iterator iter = environment.getGeneratedFiles(); iter.hasNext();) {
            GeneratedFileInfo fileInfo = (GeneratedFileInfo)iter.next();
            File f = fileInfo.getFile();
            if (f.exists() && f.getName().endsWith(".java")) {
                sourceFiles.add(f.getAbsolutePath());
            }
        }
        
        if (sourceFiles.size() > 0) {
            String classDir = destDir.getAbsolutePath();
            String classpathString = createClasspathString();
            String[] args = new String[4 + (compilerDebug == true ? 1 : 0) +
                (compilerOptimize == true ? 1 : 0) +
                sourceFiles.size()];
            args[0] = "-d";
            args[1] = classDir;
            args[2] = "-classpath";
            args[3] = classpathString;
            int baseIndex = 4;
            if (compilerDebug) {
                args[baseIndex++] = "-g";
            }
            if (compilerOptimize) {
                args[baseIndex++] = "-O";
            }
            for (int i = 0; i < sourceFiles.size(); ++i) {
                args[baseIndex + i] = (String)sourceFiles.get(i);
            }
            
            // ByteArrayOutputStream javacOutput = new ByteArrayOutputStream();
            JavaCompilerHelper compilerHelper = new JavaCompilerHelper(out);
            boolean result = compilerHelper.compile(args);
            if (!result) {
                environment.error(getMessage("wscompile.compilationFailed"));
            }
        }
    }
    
    protected ProcessorAction getAction(String name) {
        return (ProcessorAction)actions.get(name);
    }
    
    ////////
    
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
    
    protected Properties properties;
    protected ProcessorEnvironment environment;
    protected Configuration configuration;
    protected Processor processor;
    protected ProcessorNotificationListener listener;
    protected Map actions;
    protected CompileToolDelegate delegate = null;
    
    protected File configFile;
    protected File modelFile;
    protected File sourceDir;
    protected File destDir;
    protected File nonclassDestDir;
    protected File debugModelFile;
    protected int mode = MODE_UNSPECIFIED;
    protected boolean doNothing = false;
    protected boolean compilerDebug = false;
    protected boolean compilerOptimize = false;
    protected boolean verbose = false;
    protected boolean noDataBinding = false;
    protected boolean noEncodedTypes = false;
    protected boolean noMultiRefEncoding = false;
    protected boolean noValidation = false;
    protected boolean explicitServiceContext = false;
    protected boolean printStackTrace = false;
    protected boolean keepGenerated = false;
    protected boolean serializable = false;
    protected boolean donotOverride = false;
    protected boolean serializeInterfaces = false;
    protected boolean searchSchemaForSubtypes = false;
    protected boolean useDataHandlerOnly = false;
    protected boolean dontGenerateRPCStructures = false;
    protected boolean useDocLiteralEncoding = false;
    protected boolean useRPCLiteralEncoding = false;
    protected boolean useWSIBasicProfile = false;
    protected boolean generateOneWayMethods = false;
    protected boolean resolveIDREF = false;
    protected boolean strictCompliance = false;
    protected boolean jaxbEnumType = false;
    protected boolean unwrapDocLitWrappers = false;
    protected boolean wrapperFlagSeen = false;
    protected boolean dontGenerateWrapperClasses = false;
    protected String  targetVersion = null;
    
    protected String serializerInfix = null;
    
    protected String userClasspath = null;
    
    protected static final int MODE_UNSPECIFIED = 0;
    protected static final int MODE_IMPORT = 2;
    protected static final int MODE_DEFINE = 3;
    protected static final int MODE_GEN_CLIENT = 4;
    protected static final int MODE_GEN_SERVER = 5;
    protected static final int MODE_GEN_BOTH = 6;
}
