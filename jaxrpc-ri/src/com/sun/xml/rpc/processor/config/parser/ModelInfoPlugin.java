/*
 * $Id: ModelInfoPlugin.java,v 1.1 2006-04-12 20:34:05 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.config.parser;


import com.sun.xml.rpc.processor.util.ProcessorEnvironment;
import com.sun.xml.rpc.spi.tools.ModelInfo;

import java.net.URL;

import javax.xml.namespace.QName;

/**
 *
 * @author JAX-RPC Development Team
 */
public interface ModelInfoPlugin {
    public QName getModelInfoName();
    public ModelInfoParser createModelInfoParser(ProcessorEnvironment env);
    public ModelInfo createModelInfo();
    public ModelInfo createModelInfo(URL mappingFile) throws Exception;
}
