/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
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
