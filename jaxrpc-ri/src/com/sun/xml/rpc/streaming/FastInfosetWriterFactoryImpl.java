/*
 * $Id: FastInfosetWriterFactoryImpl.java,v 1.1 2006-04-12 20:32:46 kohlert Exp $
 *
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */

package com.sun.xml.rpc.streaming;

import java.io.OutputStream;
import javax.xml.stream.*;
import com.sun.xml.fastinfoset.stax.*;
import com.sun.xml.fastinfoset.vocab.*;

/**
 * <p> A concrete factory for FI XMLWriter objects. </p>
 *
 * @author Santiago.PericasGeertsen@sun.com
 */
public class FastInfosetWriterFactoryImpl extends XMLWriterFactory {

    static XMLWriterFactory _instance;
    
    static ThreadLocal writerLocal = new ThreadLocal();
    
    public static XMLWriterFactory newInstance() {
        if (_instance == null) {
            _instance = new FastInfosetWriterFactoryImpl();
        }
        
        return _instance;
    }
    
    public FastInfosetWriterFactoryImpl() {
    }

    public final XMLWriter createXMLWriter(OutputStream stream) {
        return createXMLWriter(stream, "UTF-8");
    }

    public final XMLWriter createXMLWriter(OutputStream stream, String encoding) {
        return createXMLWriter(stream, encoding, false);
    }

    public final XMLWriter createXMLWriter(OutputStream stream, String encoding,
        boolean declare) 
    {
        FastInfosetWriter writer = (FastInfosetWriter) writerLocal.get();
        if (writer == null) {
            writerLocal.set(writer = new FastInfosetWriter(stream, encoding));
        }
        else {
            writer.setOutputStream(stream);
            writer.setEncoding(encoding);
        }
        writer.writeStartDocument();
        return writer;    
    }
}
