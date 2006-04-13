/*
 * $Id: XMLModelFileFilter.java,v 1.2 2006-04-13 01:31:59 ofung Exp $
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

package com.sun.xml.rpc.processor.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLReaderException;
import com.sun.xml.rpc.streaming.XMLReaderFactory;
import com.sun.xml.rpc.processor.model.exporter.Constants;

/**
 *
 * @author JAX-RPC Development Team
 */
public class XMLModelFileFilter
    implements com.sun.xml.rpc.spi.tools.XMLModelFileFilter {
    
    public XMLModelFileFilter() {
        factory = XMLReaderFactory.newInstance();
    }
    
    public boolean isModelFile(File f) {
        if (f == null || !f.isFile() || !f.exists()) {
            return false;
        }
        
        boolean result = false;
        
        try {
            InputStream is = new FileInputStream(f);
            return isModelFile(is);
        } catch (FileNotFoundException e) {
        }
        
        return result;
    }
    
    public boolean isModelFile(java.net.URL uRL) {
        boolean result = false;
        try {
            InputStream is = uRL.openStream();
            result = isModelFile(is);
        } catch (Exception e) {
        }
        return result;
    }
    
    public boolean isModelFile(java.io.InputStream inputStream) {
        boolean result = false;
        try {
            XMLReader reader =
                factory.createXMLReader(new GZIPInputStream(inputStream));
            reader.next();
            if (reader.getState() == XMLReader.START &&
                reader.getName().equals(Constants.QNAME_MODEL)) {
                    
                result = true;
            }
            reader.close();
        } catch (XMLReaderException e) {
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
        return result;
    }
    
    private XMLReaderFactory factory;
}
