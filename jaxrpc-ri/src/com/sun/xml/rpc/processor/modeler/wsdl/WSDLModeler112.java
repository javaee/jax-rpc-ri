/*
 * $Id: WSDLModeler112.java,v 1.2 2006-04-13 01:31:33 ofung Exp $
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
import java.util.Set;

import com.sun.xml.rpc.processor.config.WSDLModelInfo;
import com.sun.xml.rpc.processor.modeler.JavaSimpleTypeCreator;
import com.sun.xml.rpc.wsdl.document.WSDLDocument;

/**
 * @author Vivek Pandey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class WSDLModeler112 extends WSDLModelerBase {

    /**
     * @param modelInfo
     * @param options
     */
    public WSDLModeler112(WSDLModelInfo modelInfo, Properties options) {
        super(modelInfo, options);
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see com.sun.xml.rpc.processor.modeler.wsdl.WSDLModelerBase#getSchemaAnalyzerInstance(com.sun.xml.rpc.wsdl.document.WSDLDocument, com.sun.xml.rpc.processor.config.WSDLModelInfo, java.util.Properties, java.util.Set, com.sun.xml.rpc.processor.modeler.JavaSimpleTypeCreator)
     */
    protected SchemaAnalyzerBase getSchemaAnalyzerInstance(
        WSDLDocument document,
        WSDLModelInfo _modelInfo,
        Properties _options,
        Set _conflictingClassNames,
        JavaSimpleTypeCreator _javaTypes) {
        return new SchemaAnalyzer112(
                document,
                _modelInfo,
                _options,
                _conflictingClassNames,
                _javaTypes);
    }

}
