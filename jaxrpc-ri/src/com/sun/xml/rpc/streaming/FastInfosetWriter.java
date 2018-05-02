/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2018 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
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

package com.sun.xml.rpc.streaming;

import java.util.Iterator;
import java.io.OutputStream;
import java.io.IOException;
import javax.xml.namespace.QName;
import javax.xml.stream.*;

import com.sun.xml.rpc.util.xml.CDATA;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;

import org.jvnet.fastinfoset.EncodingAlgorithmIndexes;
import com.sun.xml.fastinfoset.stax.StAXDocumentSerializer;

public class FastInfosetWriter extends StAXDocumentSerializer implements XMLWriter {
    
    /** 
     * JAX-RPC prefix factory.
     */
    PrefixFactory _prefixFactory;
    
    public FastInfosetWriter(OutputStream os, String encoding) {
        setOutputStream(os);
        setEncoding(encoding);
    }

    public void reset() {
        super.reset();
        _prefixFactory = null;
    }
    
    public void writeStartDocument() {
        try {
            writeStartDocument("1.0");
        } catch (XMLStreamException e) {
            throw wrapException(e);
        }
    }
    
    public void writeAttribute(String localName, String value) {
        try {
            super.writeAttribute(localName, value);
        }
        catch (XMLStreamException e) {       
            throw wrapException(e);
        }
    }
    
    public void startElement(QName name) {
        startElement(name.getLocalPart(), name.getNamespaceURI());
    }

    public void startElement(String localName) {
        startElement(localName, "");
    }

   /**
     * Write the start tag for an element.
     */
    public void startElement(String localName, String uri) {
        try {
            if (uri.length() == 0) {
                writeStartElement(localName);
            }
            else {
                String aPrefix = null;
                boolean mustDeclarePrefix = false;

                aPrefix = getPrefix(uri);
                if (aPrefix == null) {
                    mustDeclarePrefix = true;
                    if (_prefixFactory != null) {
                        aPrefix = _prefixFactory.getPrefix(uri);
                    }
                    if (aPrefix == null) {
                        throw new XMLWriterException("xmlwriter.noPrefixForURI", uri);
                    }
                }                
                
                writeStartElement(aPrefix, localName, uri);
                if (mustDeclarePrefix) {
                    writeNamespaceDeclaration(aPrefix, uri);
                }
            }
        }
        catch (XMLStreamException e) {
            throw wrapException(e);
        }
    }

    /**
     * Write the start tag for an element.
     */
    public void startElement(String localName, String uri, String prefix) {
        try {
            if (uri.length() == 0) {
                writeStartElement(localName);
            } 
            else {
                String aPrefix = null;
                boolean mustDeclarePrefix = false;

                aPrefix = getPrefix(uri);
                if (aPrefix == null) {
                    mustDeclarePrefix = true;
                    aPrefix = prefix;
                    if (aPrefix == null) {
                        throw new XMLWriterException("xmlwriter.noPrefixForURI", uri);
                    }
                }

                writeStartElement(aPrefix, localName, uri);
                if (mustDeclarePrefix) {
                    writeNamespaceDeclaration(aPrefix, uri);
                }
            }
        } 
        catch (XMLStreamException e) {
            throw wrapException(e);
        }    
    }

    /**
     * Write an attribute of the current element.
     */
    public void writeAttribute(String localName, String uri, String value) {
        try {
            if (uri.length() == 0) {
                writeAttribute(localName, value);
            } 
            else {
                boolean mustDeclarePrefix = false;
                String prefix = getPrefix(uri);
                if (prefix == null) {
                    mustDeclarePrefix = true;
                    
                    if (_prefixFactory != null) {
                        prefix = _prefixFactory.getPrefix(uri);
                    }

                    if (prefix == null) {
                        throw new XMLWriterException(
                            "xmlwriter.noPrefixForURI",
                            uri);
                    }
                }
                writeAttribute(prefix, uri, localName, value);
                if (mustDeclarePrefix) {
                    writeNamespaceDeclaration(prefix, uri);
                }
            }
        } catch (XMLStreamException e) {
            throw wrapException(e);
        }
    }
    
    public void startElement(QName name, String prefix) {
        startElement(name.getLocalPart(), name.getNamespaceURI(), prefix);
    }

    public void writeAttribute(QName name, String value) {
        writeAttribute(name.getLocalPart(), name.getNamespaceURI(), value);
    }
    
    /**
     * Write an attribute (unquoted) of the current element.
     */
    public void writeAttributeUnquoted(String localName, String value) {
        writeAttribute(localName, value);
    }

    /**
     * Write an attribute (unquoted) of the current element.
     */
    public void writeAttributeUnquoted(String localName,String uri,
        String value) 
    {
        writeAttribute(localName, uri, value);
    }

    public void writeAttributeUnquoted(QName name, String value) {
        writeAttributeUnquoted(
            name.getLocalPart(),
            name.getNamespaceURI(),
            value);
    }
    
    /**
     * Write a namespace declaration of the current element.
     */
    public void writeNamespaceDeclaration(String prefix, String uri) {
        try {
            setPrefix(prefix, uri);
            writeNamespace(prefix, uri);
        } 
        catch (XMLStreamException e) {
            throw wrapException(e);
        }
    }

    /**
     * Write a namespace declaration of the current element. The prefix name
     * will be generated by the PrefixFactory currently configured for
     * this writer.
     */
    public void writeNamespaceDeclaration(String uri) {
        if (_prefixFactory == null) {
            throw new XMLWriterException("xmlwriter.noPrefixForURI", uri);
        }
        String aPrefix = _prefixFactory.getPrefix(uri);
        writeNamespaceDeclaration(aPrefix, uri);
    }

    /**
     * Write character data within an element.
     */
    public void writeChars(String chars) {
        try {
            writeCharacters(chars);
        }
        catch (XMLStreamException e) {
            throw wrapException(e);
        }
    }

    /**
     * Write character data within an element.
     */
    public void writeChars(CDATA chars) {
        writeChars(chars.getText());
    }

    /**
     * Write character data within an element, skipping quoting.
     */
    public void writeCharsUnquoted(String chars) {
        writeChars(chars);
    }

    /**
     * Write character data within an element, skipping quoting.
     */
    public void writeCharsUnquoted(char[] buf, int offset, int len) {
        try {
            writeCharacters(buf, offset, len);
        }
        catch (XMLStreamException e) {
            throw wrapException(e);
        }
    }

    /**
     * Write a comment within an element.
     */
    public void writeComment(String comment) {
        try {
            super.writeComment(comment);
        } 
        catch (XMLStreamException e) {
            throw wrapException(e);
        }    
    }

    /**
     * Write the end tag for the current element.
     */
    public void endElement() {
        try {
            writeEndElement();
        } 
        catch (XMLStreamException e) {
            throw wrapException(e);
        }    
    }

    /**
     * Return the prefix factory in use by this writer.
     */
    public PrefixFactory getPrefixFactory() {
        return _prefixFactory;
    }

    /**
     * Set the prefix factory to be used by this writer.
     */
    public void setPrefixFactory(PrefixFactory factory) {
        _prefixFactory = factory;
    }

    /**
     * Return the URI for a given prefix.
     *
     * If the prefix is undeclared, return null.
     */
    public String getURI(String prefix) {
        return getNamespaceContext().getNamespaceURI(prefix);
    }

    /**
     * Return a prefix for the given URI.
     *
     * <p> If no prefix for the given URI is in scope, return null. </p>
     */
    public String getPrefix(String uri) {
        try {
            return super.getPrefix(uri);
        } 
        catch (XMLStreamException e) {
            throw wrapException(e);
        }
    }

    /**
     * Flush the writer and its underlying stream.
     */
    public void flush() {
        try {
            super.flush();
        } 
        catch (XMLStreamException e) {
            throw wrapException(e);
        }
    }

    /**
     * Close the writer and its underlying stream.
     */
    public void close() {
        try {
            writeEndDocument();
            super.close();
        }
        catch (XMLStreamException e) {
            throw wrapException(e);
        }
    }
    
    // -- FI specific methods --------------------------------------------
    
    /**
     * Backdoor method to encode bytes using the base64 built-in algorithm that
     * FI supports.
     */
    public final void writeBytes(byte[] b, int start, int length) {
        try {
            encodeTerminationAndCurrentElement(true);
            encodeCIIOctetAlgorithmData(EncodingAlgorithmIndexes.BASE64, b, start, length);
        } 
        catch (Exception e) {
            wrapException(e);
        }
    }

    // -- Implementation methods -----------------------------------------
    
    private XMLWriterException wrapException(Exception e) {
        return new XMLWriterException(
            "xmlwriter.ioException",
            new LocalizableExceptionAdapter(e));
    }
}
