/*
  * $Id: WSDLModeler111.java,v 1.2 2006-04-13 01:31:32 ofung Exp $
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

import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import com.sun.xml.rpc.processor.config.WSDLModelInfo;
import com.sun.xml.rpc.processor.modeler.JavaSimpleTypeCreator;
import com.sun.xml.rpc.wsdl.document.WSDLDocument;
import com.sun.xml.rpc.wsdl.framework.Extensible;
import com.sun.xml.rpc.wsdl.framework.Extension;

/**
 * @author JAX-RPC Development Team
 *
 * WSDLModeler for JAXRPC version 1.1.1
 */
public class WSDLModeler111 extends WSDLModelerBase {

    /**
     * @param modelInfo
     * @param options
     */
    public WSDLModeler111(WSDLModelInfo modelInfo, Properties options) {
        super(modelInfo, options);
    }

    /* 
     * @see com.sun.xml.rpc.processor.modeler.wsdl.WSDLModelerBase#getSchemaAnalyzerInstance(com.sun.xml.rpc.wsdl.document.WSDLDocument, com.sun.xml.rpc.processor.config.WSDLModelInfo, java.util.Properties, java.util.Set, com.sun.xml.rpc.processor.modeler.JavaSimpleTypeCreator)
     */
    protected SchemaAnalyzerBase getSchemaAnalyzerInstance(
        WSDLDocument document,
        WSDLModelInfo _modelInfo,
        Properties _options,
        Set _conflictingClassNames,
        JavaSimpleTypeCreator _javaTypes) {
        return new SchemaAnalyzer111(
            document,
            _modelInfo,
            _options,
            _conflictingClassNames,
            _javaTypes);

    }        

    /* 
     * Only JAXRPC SI 1.1.2 and onwards support wsdl mime extension and swaref.
     * @see com.sun.xml.rpc.processor.modeler.wsdl.WSDLModelerBase#getAnyExtensionOfType(com.sun.xml.rpc.wsdl.framework.Extensible, java.lang.Class)
     */
    protected Extension getAnyExtensionOfType(
        Extensible extensible,
        Class type) {
        return getExtensionOfType(extensible, type);
    }
}
