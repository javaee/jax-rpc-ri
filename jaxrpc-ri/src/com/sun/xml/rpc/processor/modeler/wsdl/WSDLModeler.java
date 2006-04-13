/*
  * $Id: WSDLModeler.java,v 1.2 2006-04-13 01:31:30 ofung Exp $
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
