/*
 * $Id: ModelFileModelInfo.java,v 1.1 2006-04-12 20:34:50 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package com.sun.xml.rpc.processor.config;

import java.util.Properties;

import com.sun.xml.rpc.processor.modeler.Modeler;
import com.sun.xml.rpc.processor.modeler.modelfile.ModelFileModeler;


/**
 *
 * @author JAX-RPC Development Team
 */
public class ModelFileModelInfo extends ModelInfo 
    implements com.sun.xml.rpc.spi.tools.ModelFileModelInfo {

    public ModelFileModelInfo() {
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String s) {
        location = s;
    }

    protected Modeler getModeler(Properties options) {
        return new ModelFileModeler(this, options);
    }

    private String location;
}
