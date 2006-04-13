/*
 * $Id: FastInfosetWriterFactoryImpl.java,v 1.2 2006-04-13 01:33:13 ofung Exp $
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
