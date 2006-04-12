/*
 * $Id: ConfigurationParser.java,v 1.1 2006-04-12 20:34:04 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
