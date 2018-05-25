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

package com.sun.xml.rpc.wsdl.parser;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.wsdl.document.schema.Schema;
import com.sun.xml.rpc.wsdl.document.schema.SchemaAttribute;
import com.sun.xml.rpc.wsdl.document.schema.SchemaDocument;
import com.sun.xml.rpc.wsdl.document.schema.SchemaElement;
import com.sun.xml.rpc.wsdl.framework.WriterContext;

/**
 * A writer for XML Schema fragments within a WSDL document.
 *
 * @author JAX-RPC Development Team
 */
public class SchemaWriter {

    public SchemaWriter() {
    }

    public void write(SchemaDocument document, OutputStream os)
        throws IOException {
        WriterContext context = new WriterContext(os);
        writeSchema(context, document.getSchema());
        context.flush();
    }

    public void writeSchema(WriterContext context, Schema schema)
        throws IOException {
        context.push();
        try {
            writeTopSchemaElement(context, schema);
        } catch (Exception e) {
        } finally {
            context.pop();
        }
    }

    protected void writeTopSchemaElement(WriterContext context, Schema schema)
        throws IOException {
        SchemaElement schemaElement = schema.getContent();
        QName name = schemaElement.getQName();

        // make sure that all namespaces we expect are actually declared
        for (Iterator iter = schema.prefixes(); iter.hasNext();) {
            String prefix = (String) iter.next();
            String expectedURI = schema.getURIForPrefix(prefix);
            if (!expectedURI.equals(context.getNamespaceURI(prefix))) {
                context.declarePrefix(prefix, expectedURI);
            }
        }

        for (Iterator iter = schemaElement.prefixes(); iter.hasNext();) {
            String prefix = (String) iter.next();
            String uri = schemaElement.getURIForPrefix(prefix);
            context.declarePrefix(prefix, uri);
        }

        context.writeStartTag(name);

        for (Iterator iter = schemaElement.attributes(); iter.hasNext();) {
            SchemaAttribute attribute = (SchemaAttribute) iter.next();
            if (attribute.getNamespaceURI() == null) {
                context.writeAttribute(
                    attribute.getLocalName(),
                    attribute.getValue(context));
            } else {
                context.writeAttribute(
                    context.getQNameString(attribute.getQName()),
                    attribute.getValue(context));
            }
        }

        context.writeAllPendingNamespaceDeclarations();

        for (Iterator iter = schemaElement.children(); iter.hasNext();) {
            SchemaElement child = (SchemaElement) iter.next();
            writeSchemaElement(context, child);
        }

        context.writeEndTag(name);
    }

    protected void writeSchemaElement(
        WriterContext context,
        SchemaElement schemaElement)
        throws IOException {
        QName name = schemaElement.getQName();

        if (schemaElement.declaresPrefixes()) {
            context.push();
        }

        context.writeStartTag(name);

        if (schemaElement.declaresPrefixes()) {
            for (Iterator iter = schemaElement.prefixes(); iter.hasNext();) {
                String prefix = (String) iter.next();
                String uri = schemaElement.getURIForPrefix(prefix);
                context.writeNamespaceDeclaration(prefix, uri);
                context.declarePrefix(prefix, uri);
            }
        }

        for (Iterator iter = schemaElement.attributes(); iter.hasNext();) {
            SchemaAttribute attribute = (SchemaAttribute) iter.next();
            if (attribute.getNamespaceURI() == null) {
                context.writeAttribute(
                    attribute.getLocalName(),
                    attribute.getValue(context));
            } else {
                context.writeAttribute(
                    context.getQNameString(attribute.getQName()),
                    attribute.getValue(context));
            }
        }

        for (Iterator iter = schemaElement.children(); iter.hasNext();) {
            SchemaElement child = (SchemaElement) iter.next();
            writeSchemaElement(context, child);
        }

        context.writeEndTag(name);

        if (schemaElement.declaresPrefixes()) {
            context.pop();
        }
    }
}
