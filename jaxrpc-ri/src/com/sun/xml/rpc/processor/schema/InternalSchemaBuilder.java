/*
 * $Id: InternalSchemaBuilder.java,v 1.1 2006-04-12 20:35:05 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.schema;

import java.util.Properties;

import com.sun.xml.rpc.wsdl.framework.AbstractDocument;

/**
 * @deprecated  This class will be deprecated. Use com.sun.xml.rpc.util.JAXRPCClassFactory 
 *               to get SchemaAnalyzerBase instance.
 * @see com.sun.xml.rpc.util.JAXRPCClassFactory#createInternalSchemaBuilder(AbstractDocument, Properties) 
 * @author JAX-RPC Development Team
 */
public class InternalSchemaBuilder extends InternalSchemaBuilderBase {
    
    /**
     * @param document
     * @param options
     */
    public InternalSchemaBuilder(AbstractDocument document,
        Properties options) {
            
        super(document, options);
    }
    
}

