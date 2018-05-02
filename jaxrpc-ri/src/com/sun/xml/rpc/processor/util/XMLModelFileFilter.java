/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
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
