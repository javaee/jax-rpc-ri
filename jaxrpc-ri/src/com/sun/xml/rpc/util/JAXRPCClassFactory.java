/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
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

/*
 * $Id: JAXRPCClassFactory.java,v 1.3 2007-07-13 23:36:38 ofung Exp $ 
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

package com.sun.xml.rpc.util;

import java.util.Properties;
import java.util.Set;

import com.sun.xml.rpc.processor.config.ModelInfo;
import com.sun.xml.rpc.processor.config.WSDLModelInfo;
import com.sun.xml.rpc.processor.generator.Names;
import com.sun.xml.rpc.processor.generator.Names101;
import com.sun.xml.rpc.processor.generator.Names103;
import com.sun.xml.rpc.processor.generator.Names11;
import com.sun.xml.rpc.processor.modeler.JavaSimpleTypeCreator;
import com.sun.xml.rpc.processor.modeler.rmi.SOAPSimpleTypeCreator101;
import com.sun.xml.rpc.processor.modeler.rmi.SOAPSimpleTypeCreator103;
import com.sun.xml.rpc.processor.modeler.rmi.SOAPSimpleTypeCreator11;
import com.sun.xml.rpc.processor.modeler.rmi.SOAPSimpleTypeCreatorBase;
import com.sun.xml.rpc.processor.modeler.wsdl.SchemaAnalyzer101;
import com.sun.xml.rpc.processor.modeler.wsdl.SchemaAnalyzer103;
import com.sun.xml.rpc.processor.modeler.wsdl.SchemaAnalyzer11;
import com.sun.xml.rpc.processor.modeler.wsdl.SchemaAnalyzer111;
import com.sun.xml.rpc.processor.modeler.wsdl.SchemaAnalyzerBase;
import com.sun.xml.rpc.processor.modeler.wsdl.WSDLModeler101;
import com.sun.xml.rpc.processor.modeler.wsdl.WSDLModeler103;
import com.sun.xml.rpc.processor.modeler.wsdl.WSDLModeler11;
import com.sun.xml.rpc.processor.modeler.wsdl.WSDLModeler111;
import com.sun.xml.rpc.processor.modeler.wsdl.WSDLModeler112;
import com.sun.xml.rpc.processor.modeler.wsdl.WSDLModelerBase;
import com.sun.xml.rpc.processor.schema.InternalSchemaBuilder101;
import com.sun.xml.rpc.processor.schema.InternalSchemaBuilder103;
import com.sun.xml.rpc.processor.schema.InternalSchemaBuilder11;
import com.sun.xml.rpc.processor.schema.InternalSchemaBuilderBase;
import com.sun.xml.rpc.soap.SOAPVersion;
import com.sun.xml.rpc.wsdl.framework.AbstractDocument;

/**
 * Singleton factory class to instantiate concrete classes based on the jaxrpc version 
 * to be used to generate the code.
 * 
 * @author JAX-RPC Development Team
 */
public class JAXRPCClassFactory {
    private static final JAXRPCClassFactory factory = new JAXRPCClassFactory();

    private static String classVersion = VersionUtil.JAXRPC_VERSION_DEFAULT;

    private JAXRPCClassFactory() {
    }

    /**
     * Get the factory instance for the default version.
     * @return        JAXRPCClassFactory instance
     */
    public static JAXRPCClassFactory newInstance() {
        return factory;
    }

    /**
     * Sets the version to a static classVersion
     * @param version
     * @return
     */
    public void setSourceVersion(String version) {
        if (version == null)
            version = VersionUtil.JAXRPC_VERSION_DEFAULT;

        if (!VersionUtil.isValidVersion(version)) {
            // TODO: throw exception
        } else
            classVersion = version;
    }

    /**
     * Returns the instance of SchemaAnalyzer for specific target version set for the factory.
     * 
     * @param document
     * @param modelInfo
     * @param options
     * @param conflictingClassNames
     * @param javaTypes
     * @return
     */
    public SchemaAnalyzerBase createSchemaAnalyzer(
        AbstractDocument document,
        ModelInfo modelInfo,
        Properties options,
        Set conflictingClassNames,
        JavaSimpleTypeCreator javaTypes) {
        SchemaAnalyzerBase schemaAnalyzer = null;
        if (classVersion.equals(VersionUtil.JAXRPC_VERSION_101))
            schemaAnalyzer =
                (SchemaAnalyzerBase) new SchemaAnalyzer101(document,
                    modelInfo,
                    options,
                    conflictingClassNames,
                    javaTypes);
        else if (classVersion.equals(VersionUtil.JAXRPC_VERSION_103))
            schemaAnalyzer =
                (SchemaAnalyzerBase) new SchemaAnalyzer103(document,
                    modelInfo,
                    options,
                    conflictingClassNames,
                    javaTypes);
        else if (classVersion.equals(VersionUtil.JAXRPC_VERSION_11))
            schemaAnalyzer =
                (SchemaAnalyzerBase) new SchemaAnalyzer11(document,
                    modelInfo,
                    options,
                    conflictingClassNames,
                    javaTypes);
        else if (classVersion.equals(VersionUtil.JAXRPC_VERSION_111))
            schemaAnalyzer =
                (SchemaAnalyzerBase) new SchemaAnalyzer111(document,
                    modelInfo,
                    options,
                    conflictingClassNames,
                    javaTypes);
        else if (classVersion.equals(VersionUtil.JAXRPC_VERSION_112))
            schemaAnalyzer =
                (SchemaAnalyzerBase) new SchemaAnalyzer111(document,
                    modelInfo,
                    options,
                    conflictingClassNames,
                    javaTypes);
        else if (classVersion.equals(VersionUtil.JAXRPC_VERSION_113))
            schemaAnalyzer =
                (SchemaAnalyzerBase) new SchemaAnalyzer111(document,
                    modelInfo,
                    options,
                    conflictingClassNames,
                    javaTypes);
        else {
            // TODO: throw exception                                
        }
        return schemaAnalyzer;
    }

    /**
     * Returns the instance of InternalSchemaBuilderBase for specific target version set for the factory.
     * 
     * @param document
     * @param modelInfo
     * @param options
     * @param conflictingClassNames
     * @param javaTypes
     * @return
     */
    public InternalSchemaBuilderBase createInternalSchemaBuilder(
        AbstractDocument document,
        Properties options) {
        InternalSchemaBuilderBase internalSchemaBuilder = null;
        if (classVersion.equals(VersionUtil.JAXRPC_VERSION_101))
            internalSchemaBuilder =
                (InternalSchemaBuilderBase) new InternalSchemaBuilder101(document,
                    options);
        else if (classVersion.equals(VersionUtil.JAXRPC_VERSION_103))
            internalSchemaBuilder =
                (InternalSchemaBuilderBase) new InternalSchemaBuilder103(document,
                    options);
        else if (
            classVersion.equals(VersionUtil.JAXRPC_VERSION_11)
                || classVersion.equals(VersionUtil.JAXRPC_VERSION_111)
                || classVersion.equals(VersionUtil.JAXRPC_VERSION_112)
                || classVersion.equals(VersionUtil.JAXRPC_VERSION_113))
            internalSchemaBuilder =
                (InternalSchemaBuilderBase) new InternalSchemaBuilder11(document,
                    options);
        else {
            // TODO: throw exception                                
        }
        return internalSchemaBuilder;
    }

    /**
     * Returns the WSDLModeler for specific target version.
     * 
     * @param modelInfo
     * @param options
     * @return
     */
    public WSDLModelerBase createWSDLModeler(
        WSDLModelInfo modelInfo,
        Properties options) {
        WSDLModelerBase wsdlModeler = null;
        if (classVersion.equals(VersionUtil.JAXRPC_VERSION_101))
            wsdlModeler =
                (WSDLModelerBase) new WSDLModeler101(modelInfo, options);
        else if (classVersion.equals(VersionUtil.JAXRPC_VERSION_103))
            wsdlModeler =
                (WSDLModelerBase) new WSDLModeler103(modelInfo, options);
        else if (classVersion.equals(VersionUtil.JAXRPC_VERSION_11))
            wsdlModeler =
                (WSDLModelerBase) new WSDLModeler11(modelInfo, options);
        else if (
            classVersion.equals(VersionUtil.JAXRPC_VERSION_111))
            wsdlModeler =
                (WSDLModelerBase) new WSDLModeler111(modelInfo, options);
        else if (
            classVersion.equals(VersionUtil.JAXRPC_VERSION_112) ||
            classVersion.equals(VersionUtil.JAXRPC_VERSION_113))
            wsdlModeler =
                (WSDLModelerBase) new WSDLModeler112(modelInfo, options);
        else {
            // TODO: throw exception                                
        }
        return wsdlModeler;
    }

    /**
     * Returns the Names for specific target version.
     * //bug fix:4904604     
     * @return
     */
    public Names createNames() {
        Names names = null;
        if (classVersion.equals(VersionUtil.JAXRPC_VERSION_101))
            names = (Names) new Names101();
        else if (classVersion.equals(VersionUtil.JAXRPC_VERSION_103))
            names = (Names) new Names103();
        else if (
            classVersion.equals(VersionUtil.JAXRPC_VERSION_11)
                || classVersion.equals(VersionUtil.JAXRPC_VERSION_111)
                || classVersion.equals(VersionUtil.JAXRPC_VERSION_112)
                || classVersion.equals(VersionUtil.JAXRPC_VERSION_113))
            names = (Names) new Names11();
        else {
            // TODO: throw exception                                
        }
        return names;
    }

    /**
     * Returns the SOAPSimpleTypeCreatorBase for specific target version.
     * 
     * @return
     */
    public SOAPSimpleTypeCreatorBase createSOAPSimpleTypeCreator() {
        SOAPSimpleTypeCreatorBase soapType = null;
        if (classVersion.equals(VersionUtil.JAXRPC_VERSION_101))
            soapType =
                (SOAPSimpleTypeCreatorBase) new SOAPSimpleTypeCreator101();
        else if (classVersion.equals(VersionUtil.JAXRPC_VERSION_103))
            soapType =
                (SOAPSimpleTypeCreatorBase) new SOAPSimpleTypeCreator103();
        else if (
            classVersion.equals(VersionUtil.JAXRPC_VERSION_11)
                || classVersion.equals(VersionUtil.JAXRPC_VERSION_111)
                || classVersion.equals(VersionUtil.JAXRPC_VERSION_112)
                || classVersion.equals(VersionUtil.JAXRPC_VERSION_113))
            soapType =
                (SOAPSimpleTypeCreatorBase) new SOAPSimpleTypeCreator11();
        else {
            // TODO: throw exception                                
        }
        return soapType;
    }

    /**
     * Returns the SOAPSimpleTypeCreatorBase for specific target version.
     * 
     * @return
     */
    public SOAPSimpleTypeCreatorBase createSOAPSimpleTypeCreator(boolean useStrictMode) {
        SOAPSimpleTypeCreatorBase soapType = null;
        if (classVersion.equals(VersionUtil.JAXRPC_VERSION_101))
            soapType =
                (SOAPSimpleTypeCreatorBase) new SOAPSimpleTypeCreator101(useStrictMode);
        else if (classVersion.equals(VersionUtil.JAXRPC_VERSION_103))
            soapType =
                (SOAPSimpleTypeCreatorBase) new SOAPSimpleTypeCreator103(useStrictMode);
        else if (
            classVersion.equals(VersionUtil.JAXRPC_VERSION_11)
                || classVersion.equals(VersionUtil.JAXRPC_VERSION_111)
                || classVersion.equals(VersionUtil.JAXRPC_VERSION_112)
                || classVersion.equals(VersionUtil.JAXRPC_VERSION_113))
            soapType =
                (SOAPSimpleTypeCreatorBase) new SOAPSimpleTypeCreator11(useStrictMode);
        else {
            // TODO: throw exception                                
        }
        return soapType;
    }

    /**
     * Returns the SOAPSimpleTypeCreatorBase for specific target version.
     * 
     * @return
     */
    public SOAPSimpleTypeCreatorBase createSOAPSimpleTypeCreator(
        boolean useStrictMode,
        SOAPVersion version) {
        SOAPSimpleTypeCreatorBase soapType = null;
        if (classVersion.equals(VersionUtil.JAXRPC_VERSION_101))
            soapType =
                (SOAPSimpleTypeCreatorBase) new SOAPSimpleTypeCreator101(useStrictMode,
                    version);
        else if (classVersion.equals(VersionUtil.JAXRPC_VERSION_103))
            soapType =
                (SOAPSimpleTypeCreatorBase) new SOAPSimpleTypeCreator103(useStrictMode,
                    version);
        else if (classVersion.equals(VersionUtil.JAXRPC_VERSION_11) ||
                    classVersion.equals(VersionUtil.JAXRPC_VERSION_111) || 
                    classVersion.equals(VersionUtil.JAXRPC_VERSION_112) ||
                    classVersion.equals(VersionUtil.JAXRPC_VERSION_113))
            soapType =
                (SOAPSimpleTypeCreatorBase) new SOAPSimpleTypeCreator11(useStrictMode,
                    version);
        else {
            // TODO: throw exception                                
        }
        return soapType;
    }

    public String getVersion() {
        return classVersion;
    }
}
