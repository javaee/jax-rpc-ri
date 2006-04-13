/*
 * $Id: ContentNegotiationProperties.java,v 1.2 2006-04-13 01:26:32 ofung Exp $
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
