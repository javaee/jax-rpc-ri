/*
 * $Id: J2EEModelInfoParser.java,v 1.1 2006-04-12 20:34:05 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.config.parser;

import java.io.IOException;

import com.sun.xml.rpc.processor.config.ConfigurationException;
import com.sun.xml.rpc.processor.config.J2EEModelInfo;
import com.sun.xml.rpc.processor.config.ModelInfo;
import com.sun.xml.rpc.processor.modeler.j2ee.JaxRpcMappingXml;
import com.sun.xml.rpc.processor.util.ProcessorEnvironment;
import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;

/**
 *
 * @author JAX-RPC Development Team
 */
public class J2EEModelInfoParser extends ModelInfoParser {
    
    public J2EEModelInfoParser(ProcessorEnvironment env) {
        super(env);
    }

    public ModelInfo parse(XMLReader reader) {
        J2EEModelInfo modelInfo = new J2EEModelInfo();
        modelInfo.setJavaPackageName("package_ignored");
        String location = ParserUtil.getMandatoryNonEmptyAttribute(reader,
            Constants.ATTR_WSDL_LOCATION);
        modelInfo.setLocation(location);
        String mapping = ParserUtil.getMandatoryNonEmptyAttribute(reader,
            Constants.ATTR_LOCATION);
        try {
            modelInfo.setJaxRcpMappingXml(new JaxRpcMappingXml(mapping));
        } catch (IOException e) {
            throw new ConfigurationException(
                "configuration.nestedConfigurationError", 
                new LocalizableExceptionAdapter(e));            
        }

        return modelInfo;
    }
}
