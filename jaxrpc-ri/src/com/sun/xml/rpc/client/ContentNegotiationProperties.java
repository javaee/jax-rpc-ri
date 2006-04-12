/*
 * $Id: ContentNegotiationProperties.java,v 1.1 2006-04-12 20:35:21 kohlert Exp $
 */

/*
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.client;

import java.util.Map;

import javax.xml.rpc.JAXRPCException;

public class ContentNegotiationProperties {
    
    static public void initFromSystemProperties(Map props)
        throws JAXRPCException 
    {
        String value =
                System.getProperty(StubPropertyConstants.CONTENT_NEGOTIATION_PROPERTY);
        
        if (value == null) {            
            props.put(StubPropertyConstants.CONTENT_NEGOTIATION_PROPERTY,
                      "none");      // FI is off by default
        } 
        else if (value.equals("none") || value.equals("pessimistic")
                    || value.equals("optimistic")) {
            props.put(StubPropertyConstants.CONTENT_NEGOTIATION_PROPERTY,
                    value.intern());
        } 
        else {
            throw new JAXRPCException("Illegal value '" + value + "' for property " +
                    StubPropertyConstants.CONTENT_NEGOTIATION_PROPERTY);
        }
    }
}
