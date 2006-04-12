/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
