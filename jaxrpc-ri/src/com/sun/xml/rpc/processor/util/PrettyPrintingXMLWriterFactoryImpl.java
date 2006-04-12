/*
 * $Id: PrettyPrintingXMLWriterFactoryImpl.java,v 1.1 2006-04-12 20:35:00 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.util;

import java.io.OutputStream;

import com.sun.xml.rpc.streaming.XMLWriter;
import com.sun.xml.rpc.streaming.XMLWriterFactory;

/**
 * <p> A concrete factory for XMLWriter objects. </p>
 *
 * <p> By default, writers created by this factory use UTF-8
 * encoding and write the namespace declaration at the top
 * of each document they produce. </p>
 *
 * @author JAX-RPC Development Team
 */
public class PrettyPrintingXMLWriterFactoryImpl extends XMLWriterFactory {
    
    public PrettyPrintingXMLWriterFactoryImpl() {}
    
    public XMLWriter createXMLWriter(OutputStream stream) {
        return createXMLWriter(stream, "UTF-8");
    }
    
    public XMLWriter createXMLWriter(OutputStream stream, String encoding) {
        return createXMLWriter(stream, encoding, true);
    }
    
    public XMLWriter createXMLWriter(OutputStream stream, String encoding,
        boolean declare) {
            
        return new PrettyPrintingXMLWriterImpl(stream, encoding, declare);
    }
}
