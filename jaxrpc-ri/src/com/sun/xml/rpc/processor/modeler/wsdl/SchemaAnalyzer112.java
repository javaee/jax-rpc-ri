/*
 * $Id: SchemaAnalyzer112.java,v 1.1 2006-04-12 20:33:59 kohlert Exp $
 */

/*
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
