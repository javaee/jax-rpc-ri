/*
 * $Id: J2EEModelInfoParser.java,v 1.2 2006-04-13 01:28:34 ofung Exp $
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
