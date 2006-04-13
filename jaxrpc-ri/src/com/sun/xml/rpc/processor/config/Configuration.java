/*
 * $Id: Configuration.java,v 1.2 2006-04-13 01:28:21 ofung Exp $
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
 
package com.sun.xml.rpc.processor.config;

import com.sun.xml.rpc.processor.util.ProcessorEnvironment;

/**
 *
 * @author JAX-RPC Development Team
 */
public class Configuration implements com.sun.xml.rpc.spi.tools.Configuration {

    public Configuration(com.sun.xml.rpc.spi.tools.ProcessorEnvironment env) {
        _env = (ProcessorEnvironment)env;
    }

    public com.sun.xml.rpc.spi.tools.ModelInfo getModelInfo() {
        return _modelInfo;
    }

    public void setModelInfo(com.sun.xml.rpc.spi.tools.ModelInfo i) {
        _modelInfo = (ModelInfo)i;
        _modelInfo.setParent(this);
    }
    
    public com.sun.xml.rpc.spi.tools.ProcessorEnvironment getEnvironment() {
        return _env;
    }
    
    private ProcessorEnvironment _env;
    private ModelInfo _modelInfo;
}
