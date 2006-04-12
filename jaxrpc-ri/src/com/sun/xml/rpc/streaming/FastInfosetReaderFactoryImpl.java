/*
 * $Id: FastInfosetReaderFactoryImpl.java,v 1.1 2006-04-12 20:32:50 kohlert Exp $
 *
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */

package com.sun.xml.rpc.streaming;

import java.io.InputStream;

import org.xml.sax.InputSource;
import javax.xml.stream.*;
import com.sun.xml.fastinfoset.stax.*;

/**
 * <p> A concrete factory for FI XMLReader objects. </p>
 *
 * @author Santiago.PericasGeertsen@sun.com
 *
 */
public class FastInfosetReaderFactoryImpl extends XMLReaderFactory {
    
    static ThreadLocal readerLocal = new ThreadLocal();

    static XMLReaderFactory _instance;
    
    public static XMLReaderFactory newInstance() {
        if (_instance == null) {
            _instance = new FastInfosetReaderFactoryImpl();
        }
        
        return _instance;
    }
    
    public FastInfosetReaderFactoryImpl() {
    }

    public final XMLReader createXMLReader(InputStream in) {
        return createXMLReader(in, false);
    }

    public final XMLReader createXMLReader(InputSource source) {
        return createXMLReader(source, false);
    }

    public final XMLReader createXMLReader(InputSource source, boolean rejectDTDs) {
        return createXMLReader(source.getByteStream(), rejectDTDs);        
    }
    
    public final XMLReader createXMLReader(InputStream in, boolean rejectDTDs) {
        FastInfosetReader reader = (FastInfosetReader) readerLocal.get();
        if (reader == null) {
            readerLocal.set(reader = new FastInfosetReader(in));
        }
        else {
            reader.setInputStream(in);
        }
        return reader;
    }

}
