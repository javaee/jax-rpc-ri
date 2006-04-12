/*
 * $Id: XMLReaderFactoryImpl.java,v 1.1 2006-04-12 20:32:49 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.streaming;

import java.io.InputStream;

import org.xml.sax.InputSource;

/**
 * <p> A concrete factory for XMLReader objects. </p>
 *
 * @author JAX-RPC Development Team
 */
public class XMLReaderFactoryImpl extends XMLReaderFactory {

    public XMLReaderFactoryImpl() {
    }

    public XMLReader createXMLReader(InputStream in) {
        return createXMLReader(in, false);
    }

    public XMLReader createXMLReader(InputSource source) {
        return createXMLReader(source, false);
    }

    public XMLReader createXMLReader(InputStream in, boolean rejectDTDs) {
        return createXMLReader(new InputSource(in), rejectDTDs);
    }

    public XMLReader createXMLReader(InputSource source, boolean rejectDTDs) {
        return new XMLReaderImpl(source, rejectDTDs);
    }
}
