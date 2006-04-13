/*
 * $Id: XMLReaderFactoryImpl.java,v 1.2 2006-04-13 01:33:20 ofung Exp $
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
