/*
 * $Id: ModelFileModeler.java,v 1.3 2007-07-13 23:36:16 ofung Exp $
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
