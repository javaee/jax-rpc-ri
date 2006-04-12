/*
 * $Id: XMLModelFileFilter.java,v 1.1 2006-04-12 20:34:59 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
