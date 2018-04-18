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
 * $Id: ConfigurationParser.java,v 1.3 2007-07-13 23:36:01 ofung Exp $
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

package com.sun.xml.rpc.processor.config.parser;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.sun.xml.rpc.processor.util.ProcessorEnvironment;
import com.sun.xml.rpc.processor.config.Configuration;
import com.sun.xml.rpc.processor.config.ConfigurationException;
import com.sun.xml.rpc.processor.config.ModelInfo;
import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLReaderException;
import com.sun.xml.rpc.streaming.XMLReaderFactory;
import com.sun.xml.rpc.tools.plugin.ToolPluginConstants;
import com.sun.xml.rpc.tools.plugin.ToolPluginFactory;

/**
 *
 * @author JAX-RPC Development Team
 */
public class ConfigurationParser {

    public ConfigurationParser(ProcessorEnvironment env) {
        _env = env;
        _modelInfoParsers = new HashMap();
        _modelInfoParsers.put(
            Constants.QNAME_SERVICE, new RmiModelInfoParser(env));
        _modelInfoParsers.put(
            Constants.QNAME_WSDL, new WSDLModelInfoParser(env));
        _modelInfoParsers.put(
            Constants.QNAME_MODELFILE, new ModelFileModelInfoParser(env));
        _modelInfoParsers.put(
            Constants.QNAME_NO_METADATA, new NoMetadataModelInfoParser(env));

        /*
         * Load modelinfo parsers from the plugins which want to extend
         * this functionality
         */
        Iterator i = ToolPluginFactory.getInstance().getExtensions(
            ToolPluginConstants.WSCOMPILE_PLUGIN,
            ToolPluginConstants.WSCOMPILE_MODEL_INFO_EXT_POINT);
        while(i != null && i.hasNext()) {
            ModelInfoPlugin plugin = (ModelInfoPlugin)i.next();
            _modelInfoParsers.put(plugin.getModelInfoName(),
                plugin.createModelInfoParser(env));
        }

    }

    public com.sun.xml.rpc.spi.tools.Configuration parse(InputStream is) {
        try {
            XMLReader reader =
                XMLReaderFactory.newInstance().createXMLReader(is);
            reader.next();
            return parseConfiguration(reader);
        } catch (XMLReaderException e) {
            throw new ConfigurationException("configuration.xmlReader", e);
        }
    }

    protected com.sun.xml.rpc.spi.tools.Configuration parseConfiguration(
        XMLReader reader) {
            
        if (!reader.getName().equals(Constants.QNAME_CONFIGURATION)) {
            ParserUtil.failWithFullName("configuration.invalidElement", reader);
        }

        String version =
            ParserUtil.getAttribute(reader, Constants.ATTR_VERSION);
        if (version != null &&
            !version.equals(Constants.ATTRVALUE_VERSION_1_0)) {
            ParserUtil.failWithLocalName("configuration.invalidVersionNumber",
                reader, version);
        }

        Configuration configuration = new Configuration(_env);
        if (reader.nextElementContent() == XMLReader.START) {
            configuration.setModelInfo(parseModelInfo(reader));
        } else {
            ParserUtil.fail("configuration.missing.model", reader);
        }

        if (reader.nextElementContent() != XMLReader.END) {
            ParserUtil.fail("configuration.unexpectedContent", reader);
        }

        reader.close();
        return configuration;
    }

    protected ModelInfo parseModelInfo(XMLReader reader) {
        ModelInfoParser miParser = 
            (ModelInfoParser) _modelInfoParsers.get(reader.getName());
        if (miParser != null) {
            return miParser.parse(reader);
        } else {
            ParserUtil.fail("configuration.unknown.modelInfo", reader);
            return null; // keep javac happy
        }
    }

    private ProcessorEnvironment _env;
    private Map _modelInfoParsers;
}
