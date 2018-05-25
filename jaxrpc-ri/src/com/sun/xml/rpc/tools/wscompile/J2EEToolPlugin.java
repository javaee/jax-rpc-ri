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

package com.sun.xml.rpc.tools.wscompile;

import java.io.File;
import java.net.URL;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.Processor;
import com.sun.xml.rpc.processor.generator.JaxRpcMappingGenerator;
import com.sun.xml.rpc.processor.util.ProcessorEnvironment;
import com.sun.xml.rpc.processor.config.J2EEModelInfo;
import com.sun.xml.rpc.processor.config.parser.J2EEModelInfoParser;
import com.sun.xml.rpc.processor.config.parser.Constants;
import com.sun.xml.rpc.processor.config.parser.ModelInfoPlugin;
import com.sun.xml.rpc.processor.config.parser.ModelInfoParser;
import com.sun.xml.rpc.processor.modeler.j2ee.JaxRpcMappingXml;
import com.sun.xml.rpc.spi.tools.ModelInfo;
import com.sun.xml.rpc.tools.plugin.ToolPlugin;
import com.sun.xml.rpc.util.localization.Localizable;
import com.sun.xml.rpc.util.localization.LocalizableMessageFactory;
import com.sun.xml.rpc.util.localization.Localizer;

/**
 *
 * @author JAX-RPC Development Team
 */
public class J2EEToolPlugin extends ToolPlugin
    implements UsageIf, ModelInfoPlugin, ProcessorActionsIf {
    
    private LocalizableMessageFactory messageFactory;
    protected Localizer localizer = new Localizer();
    protected File mappingFile = null;
    
    public J2EEToolPlugin() {
        messageFactory = new LocalizableMessageFactory(
            "com.sun.xml.rpc.resources.j2ee");
    }
    
    /* (non-Javadoc)
     * @see com.sun.xml.rpc.processor.config.parser.ModelInfoPlugin#getModelInfoName()
     */
    public QName getModelInfoName() {
        return Constants.QNAME_J2EE_MAPPING_FILE;
    }
    
    /* (non-Javadoc)
     * @see com.sun.xml.rpc.processor.config.parser.ModelInfoPlugin#createModelInfoParser(com.sun.xml.rpc.processor.util.ProcessorEnvironment)
     */
    public ModelInfoParser createModelInfoParser(ProcessorEnvironment env) {
        return new J2EEModelInfoParser(env);
    }
    
    /* (non-Javadoc)
     * @see com.sun.xml.rpc.processor.config.parser.ModelInfoPlugin#createModelInfo(java.net.URL)
     */
    public ModelInfo createModelInfo(URL mappingFile) throws Exception {
        JaxRpcMappingXml mapping =
            new JaxRpcMappingXml(mappingFile.toExternalForm());
        J2EEModelInfo modelInfo = new J2EEModelInfo(mapping);
        return modelInfo;
    }
    
    /* (non-Javadoc)
     * @see com.sun.xml.rpc.processor.config.parser.ModelInfoPlugin#createModelInfo()
     */
    public ModelInfo createModelInfo() {
        return new J2EEModelInfo();
    }
    
    
    public Localizable getOptionsUsage() {
        return messageFactory.getMessage("j2ee.usage.options", (Object[]) null);
    }
    
    public Localizable getFeaturesUsage() {
        return null;
    }
    
    public Localizable getInternalUsage() {
        return null;
    }
    
    public Localizable getExamplesUsage() {
        return null;
    }
    
    public boolean parseArguments(String[] args, UsageError err) {
        mappingFile = null;
        
		for (int i = 0; i < args.length; i++) {
			if (args[i] != null && args[i].equals("-mapping")) {
				if ((i + 1) < args.length) {
					if (mappingFile != null) {
						err.msg =
							messageFactory.getMessage(
								"j2ee.duplicateOption",
								new Object[] { "-mapping" });
						return false;
					}
					args[i] = null;
					mappingFile = new File(args[++i]);
					args[i] = null;
				} else {
					err.msg =
						messageFactory.getMessage(
							"j2ee.missingOptionArgument",
							new Object[] { "-mapping" });
					return false;
				}
			}
		}
        
        return true;
    }
    
    public void registerActions(Processor processor) {
        if (mappingFile != null) {
            processor.add(new JaxRpcMappingGenerator(mappingFile));
        }
    }
    
}
