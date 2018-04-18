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
 * $Id: Processor.java,v 1.3 2007-07-13 23:36:00 ofung Exp $
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
