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

package com.sun.xml.rpc.streaming;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.util.xml.CDATA;

/**
 * <p> The XMLWriter interface is used to write XML documents. </p>
 *
 * <p> Concrete XMLWriters can be created using a XMLWriterFactory. </p>
 *
 * @see XMLWriterFactory
 *
 * @author JAX-RPC Development Team
 */
public interface XMLWriter {

    /**
     * Write the start tag for an element.
     */
    public void startElement(QName name);

    /**
     * Write the start tag for an element.
     */
    public void startElement(QName name, String prefix);

    /**
     * Write the start tag for an element.
     */
    public void startElement(String localName);

    /**
     * Write the start tag for an element.
     */
    public void startElement(String localName, String uri);

    /**
     * Write the start tag for an element.
     */
    public void startElement(String localName, String uri, String prefix);

    /**
     * Write an attribute of the current element.
     */
    public void writeAttribute(QName name, String value);

    /**
     * Write an attribute of the current element.
     */
    public void writeAttribute(String localName, String value);

    /**
     * Write an attribute of the current element.
     */
    public void writeAttribute(String localName, String uri, String value);

    /**
     * Write an attribute (unquoted) of the current element.
     */
    public void writeAttributeUnquoted(QName name, String value);

    /**
     * Write an attribute (unquoted) of the current element.
     */
    public void writeAttributeUnquoted(String localName, String value);

    /**
     * Write an attribute (unquoted) of the current element.
     */
    public void writeAttributeUnquoted(
        String localName,
        String uri,
        String value);

    /**
     * Write a namespace declaration of the current element.
     */
    public void writeNamespaceDeclaration(String prefix, String uri);

    /**
     * Write a namespace declaration of the current element. The prefix name
     * will be generated by the PrefixFactory currently configured for
     * this writer.
     */
    public void writeNamespaceDeclaration(String uri);

    /**
     * Write character data within an element.
     */
    public void writeChars(String chars);

    /**
     * Write character data within an element.
     */
    public void writeChars(CDATA chars);

    /**
     * Write character data within an element, skipping quoting.
     */
    public void writeCharsUnquoted(String chars);

    /**
     * Write character data within an element, skipping quoting.
     */
    public void writeCharsUnquoted(char[] buf, int offset, int len);

    /**
     * Write a comment within an element.
     */
    public void writeComment(String comment);

    /**
     * Write the end tag for the current element.
     */
    public void endElement();

    /**
     * Return the prefix factory in use by this writer.
     */
    public PrefixFactory getPrefixFactory();

    /**
     * Set the prefix factory to be used by this writer.
     */
    public void setPrefixFactory(PrefixFactory factory);

    /**
     * Return the URI for a given prefix.
     *
     * If the prefix is undeclared, return null.
     */
    public String getURI(String prefix);

    /**
     * Return a prefix for the given URI.
     *
     * <p> If no prefix for the given URI is in scope, return null. </p>
     */
    public String getPrefix(String uri);

    /**
     * Flush the writer and its underlying stream.
     */
    public void flush();

    /**
     * Close the writer and its underlying stream.
     */
    public void close();
}
