/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.xml.rpc.spi.tools;

import java.net.URL;

/**
 * This interface is implemented by 
 * com.sun.xml.rpc.wsdl.parser.WSDLParser
 * <p>
 * The implementation of this interface will provide some utilities
 * in retrieving relevant information from a WSDL file.  
 */
public interface WSDLParser {
    public WSDLDocument getWSDLDocument(URL wsdlLocation);
}
