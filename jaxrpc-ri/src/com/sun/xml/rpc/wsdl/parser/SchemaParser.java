/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
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

/*
 * $Id: SchemaParser.java,v 1.3 2007-07-13 23:36:48 ofung Exp $
 */

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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

package com.sun.xml.rpc.wsdl.parser;

import java.io.IOException;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import com.sun.xml.rpc.util.xml.NamedNodeMapIterator;
import com.sun.xml.rpc.util.xml.NullEntityResolver;
import com.sun.xml.rpc.util.xml.XmlUtil;
import com.sun.xml.rpc.wsdl.document.schema.Schema;
import com.sun.xml.rpc.wsdl.document.schema.SchemaAttribute;
import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;
import com.sun.xml.rpc.wsdl.document.schema.SchemaDocument;
import com.sun.xml.rpc.wsdl.document.schema.SchemaElement;
import com.sun.xml.rpc.wsdl.framework.ParseException;
import com.sun.xml.rpc.wsdl.framework.ParserContext;
import com.sun.xml.rpc.wsdl.framework.ValidationException;

/**
 * A parser for XML Schema, including the fragments found inside a WSDL document.
 *
 * @author JAX-RPC Development Team
 */
public class SchemaParser {

    public SchemaParser() {
    }

    public boolean getFollowImports() {
        return _followImports;
    }

    public void setFollowImports(boolean b) {
        _followImports = b;
    }

    public SchemaDocument parse(InputSource source) {
        SchemaDocument schemaDocument = new SchemaDocument();
        schemaDocument.setSystemId(source.getSystemId());
        ParserContext context = new ParserContext(schemaDocument, null);
        context.setFollowImports(_followImports);
        schemaDocument.setSchema(parseSchema(context, source, null));
        return schemaDocument;
    }

    public Schema parseSchema(
        ParserContext context,
        InputSource source,
        String expectedTargetNamespaceURI) {

        Schema schema =
            parseSchemaNoImport(context, source, expectedTargetNamespaceURI);
        schema.defineAllEntities();
        processImports(context, source, schema);
        return schema;
    }

    public Schema parseSchema(
        ParserContext context,
        Element e,
        String expectedTargetNamespaceURI) {
        Schema schema =
            parseSchemaNoImport(context, e, expectedTargetNamespaceURI);
        schema.defineAllEntities();
        processImports(context, null, schema);
        return schema;
    }

    protected void processImports(
        ParserContext context,
        InputSource source,
        Schema schema) {
        for (Iterator iter = schema.getContent().children(); iter.hasNext();) {
            SchemaElement child = (SchemaElement) iter.next();
            if (child.getQName().equals(SchemaConstants.QNAME_IMPORT)) {
                String location =
                    child.getValueOfAttributeOrNull(
                        Constants.ATTR_SCHEMA_LOCATION);
                String namespace =
                    child.getValueOfAttributeOrNull(Constants.ATTR_NAMESPACE);
                //bug fix: 4857762, add adjustedLocation to teh importDocuments and ignore if it 
                //exists, to avoid duplicates
                if (location != null) {
                    String adjustedLocation = null;
                    if (source != null && source.getSystemId() != null) {
                        adjustedLocation =
                            Util.processSystemIdWithBase(
                                source.getSystemId(),
                                location);
                    }
                    //bug fix: 4856674
                    if (adjustedLocation == null) {
                        adjustedLocation =
                            context.getWSDLLocation() == null
                                ? location
                                : Util.processSystemIdWithBase(
                                    context.getWSDLLocation(),
                                    location);
                    }
                    if (!context
                        .getDocument()
                        .isImportedDocument(adjustedLocation)) {
                        // bug fix: 6264237, fix for curcular dependency
                        context.getDocument().addImportedDocument(
                            adjustedLocation);

                        context.getDocument().addImportedEntity(
                            parseSchema(
                                context,
                                new InputSource(adjustedLocation),
                                namespace));
                    }
                }
            } else if (
                child.getQName().equals(SchemaConstants.QNAME_INCLUDE)
                    && (schema.getTargetNamespaceURI() != null)) {
                String location =
                    child.getValueOfAttributeOrNull(
                        Constants.ATTR_SCHEMA_LOCATION);
                if (location != null
                    && !context.getDocument().isIncludedDocument(location)) {
                    context.getDocument().addIncludedDocument(location);
                    String adjustedLocation = null;
                    if (source != null && source.getSystemId() != null) {
                        adjustedLocation =
                            Util.processSystemIdWithBase(
                                source.getSystemId(),
                                location);
                    }
                    if (adjustedLocation == null) {
                        adjustedLocation =
                            context.getDocument().getSystemId() == null
                                ? location
                                : Util.processSystemIdWithBase(
                                    context.getDocument().getSystemId(),
                                    location);
                    }
                    context.getDocument().addIncludedEntity(
                        parseSchema(
                            context,
                            new InputSource(adjustedLocation),
                            schema.getTargetNamespaceURI()));
                }
            } else if (
                child.getQName().equals(SchemaConstants.QNAME_REDEFINE)) {
                // not supported
                Util.fail("validation.unsupportedSchemaFeature", "redefine");
            }
        }
    }

    protected Schema parseSchemaNoImport(
        ParserContext context,
        InputSource source,
        String expectedTargetNamespaceURI) {
        try {
            DocumentBuilderFactory builderFactory =
                DocumentBuilderFactory.newInstance();
            builderFactory.setNamespaceAware(true);
            builderFactory.setValidating(false);
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            builder.setErrorHandler(new ErrorHandler() {
                public void error(SAXParseException e)
                    throws SAXParseException {
                    throw e;
                }
                public void fatalError(SAXParseException e)
                    throws SAXParseException {
                    throw e;
                }
                public void warning(SAXParseException err)
                    throws SAXParseException {
                    // do nothing
                }
            });
            builder.setEntityResolver(new NullEntityResolver());

            try {
                Document document = builder.parse(source);
                return parseSchemaNoImport(
                    context,
                    document,
                    expectedTargetNamespaceURI);
            } catch (IOException e) {
                throw new ParseException(
                    "parsing.ioException",
                    new LocalizableExceptionAdapter(e));
            } catch (SAXException e) {
                throw new ParseException(
                    "parsing.saxException",
                    new LocalizableExceptionAdapter(e));
            }
        } catch (ParserConfigurationException e) {
            throw new ParseException(
                "parsing.parserConfigException",
                new LocalizableExceptionAdapter(e));
        } catch (FactoryConfigurationError e) {
            throw new ParseException(
                "parsing.factoryConfigException",
                new LocalizableExceptionAdapter(e));
        }
    }

    protected Schema parseSchemaNoImport(
        ParserContext context,
        Document doc,
        String expectedTargetNamespaceURI) {
        Element root = doc.getDocumentElement();
        Util.verifyTagNSRootElement(root, SchemaConstants.QNAME_SCHEMA);
        return parseSchemaNoImport(context, root, expectedTargetNamespaceURI);
    }

    protected Schema parseSchemaNoImport(
        ParserContext context,
        Element e,
        String expectedTargetNamespaceURI) {
        Schema schema = new Schema(context.getDocument());
        String targetNamespaceURI =
            XmlUtil.getAttributeOrNull(e, Constants.ATTR_TARGET_NAMESPACE);
        //bug 4849754 fix, in both the case of xsd:include and xsd:import this should work	
        if (targetNamespaceURI != null
            && expectedTargetNamespaceURI != null
            && !expectedTargetNamespaceURI.equals(targetNamespaceURI)) {
            throw new ValidationException(
                "validation.incorrectTargetNamespace",
                new Object[] {
                    targetNamespaceURI,
                    expectedTargetNamespaceURI });
        }
        if (targetNamespaceURI == null)
            schema.setTargetNamespaceURI(expectedTargetNamespaceURI);
        else
            schema.setTargetNamespaceURI(targetNamespaceURI);

        // snapshot the current prefixes
        for (Iterator iter = context.getPrefixes(); iter.hasNext();) {
            String prefix = (String) iter.next();
            String nsURI = context.getNamespaceURI(prefix);
            if (nsURI == null) {
                // should not happen
                throw new ParseException("parsing.shouldNotHappen");
            }
            schema.addPrefix(prefix, nsURI);
        }

        context.push();
        context.registerNamespaces(e);

        // just internalize the XML fragment
        SchemaElement schemaElement =
            new SchemaElement(SchemaConstants.QNAME_SCHEMA);

        copyNamespaceDeclarations(schemaElement, e);
        copyAttributesNoNs(schemaElement, e);
        copyElementContent(schemaElement, e);

        schema.setContent(schemaElement);
        schemaElement.setSchema(schema);

        context.pop();
        context.fireDoneParsingEntity(SchemaConstants.QNAME_SCHEMA, schema);
        return schema;
    }

    protected void copyAttributesNoNs(SchemaElement target, Element source) {
        for (Iterator iter = new NamedNodeMapIterator(source.getAttributes());
            iter.hasNext();
            ) {
            Attr attr = (Attr) iter.next();
            if (attr.getName().equals(PREFIX_XMLNS)
                || attr.getName().startsWith(PREFIX_XMLNS_COLON)) {
                continue;
            }

            SchemaAttribute attribute =
                new SchemaAttribute(attr.getLocalName());
            attribute.setNamespaceURI(attr.getNamespaceURI());
            attribute.setValue(attr.getValue());
            target.addAttribute(attribute);
        }
    }

    protected void copyNamespaceDeclarations(
        SchemaElement target,
        Element source) {
        for (Iterator iter = new NamedNodeMapIterator(source.getAttributes());
            iter.hasNext();
            ) {
            Attr attr = (Attr) iter.next();
            if (attr.getName().equals(PREFIX_XMLNS)) {
                // default namespace declaration
                target.addPrefix("", attr.getValue());
            } else {
                String prefix = XmlUtil.getPrefix(attr.getName());
                if (prefix != null && prefix.equals(PREFIX_XMLNS)) {
                    String nsPrefix = XmlUtil.getLocalPart(attr.getName());
                    String uri = attr.getValue();
                    target.addPrefix(nsPrefix, uri);
                }
            }
        }
    }

    protected void copyElementContent(SchemaElement target, Element source) {
        for (Iterator iter = XmlUtil.getAllChildren(source); iter.hasNext();) {
            Element e2 = Util.nextElementIgnoringCharacterContent(iter);
            if (e2 == null)
                break;
            SchemaElement newElement = new SchemaElement(e2.getLocalName());
            newElement.setNamespaceURI(e2.getNamespaceURI());
            copyNamespaceDeclarations(newElement, e2);
            copyAttributesNoNs(newElement, e2);
            copyElementContent(newElement, e2);
            target.addChild(newElement);
            newElement.setParent(target);
        }
    }

    private boolean _followImports;

    private final static String PREFIX_XMLNS = "xmlns";
    private final static String PREFIX_XMLNS_COLON = "xmlns:";
}
