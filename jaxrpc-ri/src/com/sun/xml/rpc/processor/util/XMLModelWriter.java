/*
 * $Id: XMLModelWriter.java,v 1.2 2006-04-13 01:32:00 ofung Exp $
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

package com.sun.xml.rpc.processor.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Properties;

import com.sun.xml.rpc.processor.ProcessorAction;
import com.sun.xml.rpc.processor.ProcessorOptions;
import com.sun.xml.rpc.processor.config.Configuration;
import com.sun.xml.rpc.processor.model.Model;
import com.sun.xml.rpc.processor.model.exporter.ModelExporter;

/**
 * This class writes out a Model as an XML document.
 *
 * @author JAX-RPC Development Team
 */
public class XMLModelWriter implements ProcessorAction {
    
    public XMLModelWriter(File file) throws FileNotFoundException {
        exporter = new ModelExporter(new FileOutputStream(file));
    }
    
    public void perform(Model model, Configuration config, Properties options) {
        
        /* set the target version specified using -source switch or
         * default if not specified
         */
        model.setSource(options.getProperty(
            ProcessorOptions.JAXRPC_SOURCE_VERSION));
        exporter.doExport(model);
    }
    
    private ModelExporter exporter;
}

