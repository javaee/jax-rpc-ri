/*
 * $Id: Processor.java,v 1.2 2006-04-13 01:28:17 ofung Exp $
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

package com.sun.xml.rpc.processor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import com.sun.xml.rpc.processor.config.Configuration;
import com.sun.xml.rpc.processor.config.ModelInfo;
import com.sun.xml.rpc.processor.model.Model;
import com.sun.xml.rpc.processor.util.ProcessorEnvironment;
import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;

/**
 *
 * @author JAX-RPC Development Team
 */
public class Processor implements com.sun.xml.rpc.spi.tools.Processor {
    
    public Processor(Configuration configuration, Properties options) {
        _configuration = configuration;
        _options = options;
        _actions = new ArrayList();
        
        // find the value of the "print stack traces" property
        _printStackTrace = Boolean.valueOf(_options.getProperty(
            ProcessorOptions.PRINT_STACK_TRACE_PROPERTY)).booleanValue();
        _env = (ProcessorEnvironment)_configuration.getEnvironment();
    }
    
    public void add(ProcessorAction action) {
        _actions.add(action);
    }
    
    public com.sun.xml.rpc.spi.model.Model getModel() {
        return _model;
    }
    
    public void run() {
        runModeler();
        if (_model != null) {
            runActions();
        }
    }
    
    public void runModeler() {
        try {
            ModelInfo modelInfo = (ModelInfo)_configuration.getModelInfo();
            if (modelInfo == null) {
                throw new ProcessorException("processor.missing.model");
            }
            
            _model = modelInfo.buildModel(_options);
            
        } catch (JAXRPCExceptionBase e) {
            if (_printStackTrace) {
                _env.printStackTrace(e);
            }
            _env.error(e);
        } catch (Exception e) {
            if (_printStackTrace) {
                _env.printStackTrace(e);
            }
            _env.error(new LocalizableExceptionAdapter(e));
        }
    }
    
    public void runActions() {
        try {
            if (_model == null) {

                // avoid reporting yet another error here
                return;
            }
            
            for (Iterator iter = _actions.iterator(); iter.hasNext();) {
                ProcessorAction action = (ProcessorAction) iter.next();
                action.perform(_model, _configuration, _options);
            }
        } catch (JAXRPCExceptionBase e) {
            if (_printStackTrace) {
                _env.printStackTrace(e);
            }
            _env.error(e);
        } catch (Exception e) {
            if (_printStackTrace) {
                _env.printStackTrace(e);
            }
            _env.error(new LocalizableExceptionAdapter(e));
        }
    }
    
    private Properties _options;
    private Configuration _configuration;
    private List _actions;
    private Model _model;
    private boolean _printStackTrace;
    private ProcessorEnvironment _env;
}
