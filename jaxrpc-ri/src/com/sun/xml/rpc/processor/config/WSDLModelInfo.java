/*
 * $Id: WSDLModelInfo.java,v 1.1 2006-04-12 20:34:49 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package com.sun.xml.rpc.processor.config;

import java.util.Properties;

import com.sun.xml.rpc.processor.modeler.Modeler;
import com.sun.xml.rpc.util.JAXRPCClassFactory;

/**
 *
 * @author JAX-RPC Development Team
 */
public class WSDLModelInfo extends ModelInfo {

    public WSDLModelInfo() {}

    protected Modeler getModeler(Properties options) {
        return JAXRPCClassFactory.newInstance().createWSDLModeler(this, options);
    }

    public String getLocation() {
        return _location;
    }

    public void setLocation(String s) {
        _location = s;
    }

    private String _location;
}
