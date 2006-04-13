/*
 * $Id: XMLWriterBase.java,v 1.2 2006-04-13 01:33:22 ofung Exp $
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

import javax.xml.namespace.QName;

import com.sun.xml.rpc.util.xml.CDATA;

/**
 * <p> A base class for XMLWriter implementations. </p>
 *
 * <p> It provides the implementation of some derived XMLWriter methods. </p>
 *
 * @author JAX-RPC Development Team
 */
public abstract class XMLWriterBase implements XMLWriter {

    public void startElement(String localName) {
        startElement(localName, "");
    }

    public void startElement(QName name) {
        startElement(name.getLocalPart(), name.getNamespaceURI());
    }

    public void startElement(QName name, String prefix) {
        startElement(name.getLocalPart(), name.getNamespaceURI(), prefix);
    }

    public void writeAttribute(String localName, String value) {
        writeAttribute(localName, "", value);
    }

    public void writeAttribute(QName name, String value) {
        writeAttribute(name.getLocalPart(), name.getNamespaceURI(), value);
    }

    public void writeAttributeUnquoted(QName name, String value) {
        writeAttributeUnquoted(
            name.getLocalPart(),
            name.getNamespaceURI(),
            value);
    }

    public void writeAttributeUnquoted(String localName, String value) {
        writeAttributeUnquoted(localName, "", value);
    }

    public abstract void writeChars(CDATA chars);

    public abstract void writeChars(String chars);

    public void writeComment(String comment) {
    }
}
