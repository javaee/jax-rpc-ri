/*
 * $Id: XMLModelWriter.java,v 1.1 2006-04-12 20:35:00 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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

