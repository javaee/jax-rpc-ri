/*
  * $Id: WSDLModeler.java,v 1.1 2006-04-12 20:34:01 kohlert Exp $
*/

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.xml.rpc.processor.modeler.wsdl;

import java.util.Properties;

import com.sun.xml.rpc.processor.config.WSDLModelInfo;

/**
  * @deprecated  This class will be deprecated. Use com.sun.xml.rpc.util.JAXRPCClassFactory 
  *               to get WSDLModelerBase instance.
  * @see com.sun.xml.rpc.util.JAXRPCClassFactory#createWSDLModeler(WSDLModelInfo, Properties) 
  * @author JAX-RPC Development Team
  */
public class WSDLModeler extends WSDLModeler112 {

    /**
     * @param modelInfo
     * @param options
     */
    public WSDLModeler(WSDLModelInfo modelInfo, Properties options) {
        super(modelInfo, options);
    }
}
