/*
 * $Id: ModelFileModeler.java,v 1.1 2006-04-12 20:35:19 kohlert Exp $
*/

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.modeler.modelfile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.net.MalformedURLException;
import java.net.URL;
import java.lang.reflect.Method;
import java.util.Properties;

import com.sun.xml.rpc.processor.ProcessorOptions;
import com.sun.xml.rpc.processor.config.ModelFileModelInfo;
import com.sun.xml.rpc.processor.model.Model;
import com.sun.xml.rpc.processor.model.ModelException;
import com.sun.xml.rpc.processor.model.exporter.ModelImporter;
import com.sun.xml.rpc.processor.modeler.Modeler;
import com.sun.xml.rpc.processor.modeler.ModelerException;
import com.sun.xml.rpc.processor.util.ProcessorEnvironment;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import com.sun.xml.rpc.util.localization.LocalizableMessageFactory;

/**
 *
 * @author JAX-RPC Development Team
 */
public class ModelFileModeler implements Modeler {
    
    public ModelFileModeler(ModelFileModelInfo modelInfo, Properties options) {
        _modelInfo = modelInfo;
        _options = options;
        _messageFactory = new LocalizableMessageFactory(
            "com.sun.xml.rpc.resources.modeler");
        _env = (ProcessorEnvironment)modelInfo.getParent().getEnvironment();
    }
    
    public Model buildModel() {
        try {
            URL url = null;
            try {
                url = new URL(_modelInfo.getLocation());
            } catch (MalformedURLException e) {
                url = new File(_modelInfo.getLocation()).toURL();
            }
            
            InputStream is = url.openStream();
            ModelImporter im = new ModelImporter(url.openStream());
            Model model = im.doImport();
            
            /* set the target version (-source). If its null,
             * then don't set the value, it would have been already
             * set to default target.
             */
            if(model.getSource() != null) {
                _options.setProperty(ProcessorOptions.JAXRPC_SOURCE_VERSION,
                    model.getSource());
            }
            return model;
        } catch (IOException e) {
            throw new ModelerException(new LocalizableExceptionAdapter(e));
        } catch (ModelException e) {
            throw new ModelerException(e);
        }
    }
    
    private ModelFileModelInfo _modelInfo;
    private Properties _options;
    private LocalizableMessageFactory _messageFactory;
    private ProcessorEnvironment _env;
}
