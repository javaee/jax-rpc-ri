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

// @(#) 1.2 jsr109ri/src/java/com/ibm/webservices/ri/etools/xmlschema/beans/DOMWriter.java, jsr109ri, jsr10911, b0240.03 9/26/02 18:18:31 [10/7/02 11:54:12]
/*************************************************************************
   Licensed Materials - Property of IBM
   5639-D57, 5630-A36, 5630-A37, 5724-D18 (C) COPYRIGHT International Business Machines Corp. 2002
   All Rights Reserved
   US Government Users Restricted Rights - Use, duplication, or
   disclosure restricted by GSA ADP Schedule Contract  with
   IBM Corp.
**************************************************************************/
/**
 * <copyright>
 *   IBM WebSphere Application Developer
 *   (C) Copyright IBM Corp. 2001
 * </copyright>
 */
package com.sun.xml.rpc.processor.modeler.j2ee.xml;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

/**
 * A simple DOM writer
 */
public class DOMWriter {
    protected PrintWriter out;
    protected int indent = 0;
    protected String encodingTag;
    protected String docTypeString;
    protected String prefix;

    /*
     * @param document document to print
     * @param writer  PrintWriter to print
     * @param encodingTag  encoding
     * @param docTypeString document type
     */
    public DOMWriter(
        Document document,
        PrintWriter writer,
        String encodingTag,
        String docTypeString,
        String prefix) {
        this.prefix = prefix;
        this.indent = indent;
        this.encodingTag = encodingTag;
        this.docTypeString = docTypeString;
        out = writer;
        print(document);
    }

    public DOMWriter(
        Document document,
        String outfile,
        String encoding,
        String encodingTag,
        String docTypeString) {
        this.encodingTag = encodingTag;
        this.docTypeString = docTypeString;

        try {
            OutputStreamWriter writer;
            if (encoding != null) {
                writer =
                    new OutputStreamWriter(
                        new FileOutputStream(outfile),
                        encoding);
            } else {
                // default to utf8
                writer =
                    new OutputStreamWriter(
                        new FileOutputStream(outfile),
                        "UTF8");
            }

            out = new PrintWriter(new BufferedWriter(writer));
            print(document);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printIndent() {
        out.print(prefix);
        for (int i = 0; i < indent; i++) {
            out.print(" ");
        }
    }

    public class XMLVisitor {
        public void visitNode(Node node) {
            switch (node.getNodeType()) {
                case Node.ATTRIBUTE_NODE :
                    {
                        visitAttr((Attr) node);
                        break;
                    }
                case Node.CDATA_SECTION_NODE :
                    {
                        visitCDATASection((CDATASection) node);
                        break;
                    }
                case Node.COMMENT_NODE :
                    {
                        visitComment((Comment) node);
                        break;
                    }
                case Node.DOCUMENT_NODE :
                    {
                        visitDocument((Document) node);
                        break;
                    }
                case Node.ELEMENT_NODE :
                    {
                        visitElement((Element) node);
                        break;
                    }
                case Node.PROCESSING_INSTRUCTION_NODE :
                    {
                        visitProcessingInstruction(
                            (ProcessingInstruction) node);
                        break;
                    }
                case Node.TEXT_NODE :
                    {
                        visitText((Text) node);
                        break;
                    }
            }
        }

        public void visitDocument(Document document) {
            if (encodingTag != null && !encodingTag.equals("")) {
                out.println(
                    "<?xml version=\"1.0\" encoding=\"" + encodingTag + "\"?>");
            }

            // Print the DOCTYPE if specified
            if (docTypeString != null && !docTypeString.equals("")) {
                out.println(docTypeString);
            }

            visitChildNodesHelper(document);
        }

        public void visitElement(Element element) {
            boolean currentElementHasChildElements = hasChildElements(element);

            printIndent();
            out.print('<' + element.getNodeName());
            visitAttributesHelper(element);
            out.print(">");

            if (currentElementHasChildElements) {
                out.print("\n");
            }

            indent += 2;
            visitChildNodesHelper(element);
            indent -= 2;

            if (currentElementHasChildElements) {
                printIndent();
            }

            out.println("</" + element.getNodeName() + ">");
        }

        public void visitAttr(Attr attr) {
            /*Don't print attribute value unless it was originally specified */
            if (attr.getSpecified()) {
                out.print(" ");
                out.print(attr.getNodeName() + "=\"" + attr.getValue() + '"');
            }
        }

        public void visitText(Text text) {
            out.print(normalize(text.getNodeValue()));
        }

        public void visitCDATASection(CDATASection cdataSection) {
        }

        public void visitComment(Comment comment) {
            printIndent();
            out.print("<!--");
            out.print(normalize(comment.getNodeValue()));
            out.println("-->");
        }

        public void visitProcessingInstruction(ProcessingInstruction pi) {
            printIndent();
            out.print("<?");
            out.print(pi.getNodeName());
            out.print(" ");
            out.print(normalize(pi.getNodeValue()));
            out.println("?>");
        }

        public boolean hasChildElements(Node node) {
            boolean result = false;
            NodeList children = node.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    result = true;
                    break;
                }
            }
            return result;
        }

        public void visitChildNodesHelper(Node node) {
            NodeList children = node.getChildNodes();

            for (int i = 0; i < children.getLength(); i++) {
                visitNode(children.item(i));
            }
        }

        public void visitAttributesHelper(Node node) {
            NamedNodeMap map = node.getAttributes();
            for (int i = 0; i < map.getLength(); i++) {
                visitNode(map.item(i));
            }
        }
    }

    /**
     * Prints the specified node, recursively.
     */
    public void print(Node node) {
        // is there anything to do?
        if (node != null) {
            XMLVisitor visitor = new XMLVisitor();
            visitor.visitNode(node);
        }
        out.flush();
    }

    /**
     * Normalize the text string
     */
    protected String normalize(String s) {
        StringBuffer str = new StringBuffer();
        s = s.trim();

        int len = (s != null) ? s.length() : 0;
        for (int i = 0; i < len; i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '<' :
                    {
                        str.append("&lt;");
                        break;
                    }
                case '>' :
                    {
                        str.append("&gt;");
                        break;
                    }
                case '&' :
                    {
                        str.append("&amp;");
                        break;
                    }
                case '"' :
                    {
                        str.append("&quot;");
                        break;
                    }
                case '\r' :
                case '\n' :
                    {
                        break;
                    }
                default :
                    {
                        str.append(ch);
                    }
            }
        }
        return (str.toString());
    }
}
