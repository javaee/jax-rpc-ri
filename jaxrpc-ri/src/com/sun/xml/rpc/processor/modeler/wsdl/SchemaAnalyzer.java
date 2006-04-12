/*
 * $Id: SchemaAnalyzer.java,v 1.1 2006-04-12 20:34:01 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.xml.rpc.processor.modeler.wsdl;

import java.util.Properties;
import java.util.Set;

import com.sun.xml.rpc.processor.config.ModelInfo;
import com.sun.xml.rpc.processor.modeler.JavaSimpleTypeCreator;
import com.sun.xml.rpc.wsdl.framework.AbstractDocument;

/**
  * @deprecated  This class will be deprecated. Use com.sun.xml.rpc.util.JAXRPCClassFactory 
  *               to get SchemaAnalyzerBase instance.
  * @see com.sun.xml.rpc.util.JAXRPCClassFactory#createSchemaAnalyzer(AbstractDocument, ModelInfo, Properties, Set, JavaSimpleTypeCreator) 
  * @author JAX-RPC Development Team
  */
public class SchemaAnalyzer extends SchemaAnalyzer111 {

    /**
     * @param document
     * @param modelInfo
     * @param options
     * @param conflictingClassNames
     * @param javaTypes
     */
    public SchemaAnalyzer(
        AbstractDocument document,
        ModelInfo modelInfo,
        Properties options,
        Set conflictingClassNames,
        JavaSimpleTypeCreator javaTypes) {
        super(document, modelInfo, options, conflictingClassNames, javaTypes);
    }
}
