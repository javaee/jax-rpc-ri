/*
 * $Id: XMLWriterFactoryImpl.java,v 1.2 2006-04-13 01:33:24 ofung Exp $
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
