/*
 * $Id: SchemaAnalyzer112.java,v 1.2 2006-04-13 01:31:29 ofung Exp $
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

import com.sun.xml.rpc.processor.config.ModelInfo;
import com.sun.xml.rpc.processor.model.literal.LiteralType;
import com.sun.xml.rpc.processor.modeler.JavaSimpleTypeCreator;
import com.sun.xml.rpc.wsdl.framework.AbstractDocument;

/**
 * @author Vivek Pandey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SchemaAnalyzer112 extends SchemaAnalyzer111 {
    /**
     * @param document
     * @param modelInfo
     * @param options
     * @param conflictingClassNames
     * @param javaTypes
     */
    public SchemaAnalyzer112(
            AbstractDocument document,
            ModelInfo modelInfo,
            Properties options,
            Set conflictingClassNames,
            JavaSimpleTypeCreator javaTypes) {
        super(document, modelInfo, options, conflictingClassNames, javaTypes);
    }

    /* (bug fix: 4999385
     * @see com.sun.xml.rpc.processor.modeler.wsdl.SchemaAnalyzerBase#doWeHandleAttributeTypeEnumeration(com.sun.xml.rpc.processor.model.literal.LiteralType)
     */
    protected boolean doWeHandleAttributeTypeEnumeration(LiteralType attributeType) {
        return isAttributeEnumeration(attributeType);
    }

}
