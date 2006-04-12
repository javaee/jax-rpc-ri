/*
 * $Id: XMLWriterFactoryImpl.java,v 1.1 2006-04-12 20:32:48 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.streaming;

import java.io.OutputStream;

/**
 * <p> A concrete factory for XMLWriter objects. </p>
 *
 * <p> By default, writers created by this factory use UTF-8
 * encoding and write the namespace declaration at the top
 * of each document they produce. </p>
 *
 * @author JAX-RPC Development Team
 */
public class XMLWriterFactoryImpl extends XMLWriterFactory {

    public XMLWriterFactoryImpl() {
    }

    public XMLWriter createXMLWriter(OutputStream stream) {
        return createXMLWriter(stream, "UTF-8");
    }

    public XMLWriter createXMLWriter(OutputStream stream, String encoding) {
        return createXMLWriter(stream, encoding, true);
    }

    public XMLWriter createXMLWriter(
        OutputStream stream,
        String encoding,
        boolean declare) {
        return new XMLWriterImpl(stream, encoding, declare);
    }
}
