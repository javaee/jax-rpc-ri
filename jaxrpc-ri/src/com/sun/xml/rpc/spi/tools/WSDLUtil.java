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
package com.sun.xml.rpc.spi.tools;

import java.net.URL;
import java.util.Collection;

/**
 * This interface is implemented by 
 * com.sun.xml.rpc.wsdl.WSDLUtil
 * <p>
 * The implementation of this interface will provide some utilities
 * in retrieving relevant information from a WSDL file.  We expose
 * those functionalities via this utility class instead of directly
 * putting the burden on jaxrpc implementation of WSDLParser.
 * <p>
 * We should be conservative on adding methods to this utility class.
 * Hopefully, we could use JSR 110 (parsing WSDL) implementation soon.
 */
public interface WSDLUtil {

    /**
     * Collect all relative imports from a web service's main wsdl document.
     * [This should be equivalent to WSDLParser.setFollowImports(false)]
     *
     * @param wsdlURL The URL for a wsdl document
     * @param wsdlRelativeImports outupt param in which wsdl relative imports 
     *                            will be added
     * @param schemaRelativeImports outupt param in which schema relative 
     *                              imports will be added
     */
    public void getRelativeImports(
        URL wsdlURL,
        Collection wsdlRelativeImports,
        Collection schemaRelativeImports)
        throws java.io.IOException;
}
