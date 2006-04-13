/*
 * $Id: FastInfosetReaderFactoryImpl.java,v 1.2 2006-04-13 01:33:12 ofung Exp $
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
