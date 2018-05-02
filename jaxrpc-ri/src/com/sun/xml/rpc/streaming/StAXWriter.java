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

/*
  * StAXWriter.java
  *
  * Created on March 22, 2004, 2:59 PM
  */
package com.sun.xml.rpc.streaming;

import java.io.OutputStream;
import java.io.StringReader;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import com.sun.xml.rpc.util.xml.CDATA;

/**
  * An implementation of XMLWriter that uses StAX
  */
public class StAXWriter extends XMLWriterBase  {

     private final static String XML_VERSION = "1.0";

     // the stream writer that does the work
     private XMLStreamWriter writer;

     // only use the one passed in
     private PrefixFactory prefixFactory;

     // make sure we close the document
     private boolean documentEnded = false;

     private static XMLOutputFactory outputFactory = null;
     
     /*
      * Create the XMLStreamWriter and start the document
      */
     public StAXWriter(OutputStream out, String enc, boolean declare) {  
         try {
             // Need to init factory if no writer is passed
             if (outputFactory == null) {
                 outputFactory = XMLOutputFactory.newInstance();
             }
             writer = outputFactory.createXMLStreamWriter(out, enc);
             if (declare) {
                 writer.writeStartDocument(enc, XML_VERSION);
             }
         } 
         catch (XMLStreamException e) {
             throw wrapException(e);
         }     
     }
     
     public StAXWriter(OutputStream out, String enc, boolean declare,
        XMLStreamWriter writer) 
     {
         try {
             this.writer = writer;
             if (declare) {
                 writer.writeStartDocument(enc, XML_VERSION);
             }
         } 
         catch (XMLStreamException e) {
             throw wrapException(e);
         }     
     }
     
     /**
      * Returns the StAX XMLStreamWriter that is being used.
      */
     public XMLStreamWriter getXMLStreamWriter() {
         return writer;
     }

     public void startElement(String localName, String uri) {
         try {
             if (uri.equals("")) {
                 writer.writeStartElement(localName);
             } else {
                 boolean mustDeclarePrefix = false;
                 String aPrefix = getPrefix(uri);
                 if (aPrefix == null) {
                     aPrefix = prefixFactory.getPrefix(uri);
                     mustDeclarePrefix = true;
                 }
                 writer.writeStartElement(aPrefix, localName, uri);
                 if (mustDeclarePrefix) {
                     writeNamespaceDeclaration(aPrefix, uri);
                 }
             }
         } catch (XMLStreamException e) {
             throw wrapException(e);
         }
     }

     public void startElement(String localName, String uri, String prefix) {
         try {
             if (uri.equals("")) {
                 writer.writeStartElement(localName);
             } else {
                 boolean mustDeclarePrefix = false;
                 String other = writer.getPrefix(uri);
                 if (other == null) {
                     mustDeclarePrefix = true;
                 } 
                 else if (!other.equals(prefix)) {
                     mustDeclarePrefix = true;
                     writer.setPrefix(prefix, uri);
                 }                 
                 writer.writeStartElement(prefix, localName, uri);
                 if (mustDeclarePrefix) {
                     writer.writeNamespace(prefix,  uri);
                 }
             }
         } catch (XMLStreamException e) {
             throw wrapException(e);
         }
     }

     public void writeAttribute(String localName, String uri, String value) {
         try {
             if (uri.equals("")) {
                 writer.writeAttribute(localName, value);
             } else {
                 boolean mustDeclarePrefix = false;
                 String aPrefix = getPrefix(uri);
                 if (aPrefix == null) {
                     mustDeclarePrefix = true;

                     if (prefixFactory != null) {
                         aPrefix = prefixFactory.getPrefix(uri);
                     }

                     if (aPrefix == null) {
                         throw new XMLWriterException(
                             "xmlwriter.noPrefixForURI",
                             uri);
                     }
                 }
                 writer.writeAttribute(aPrefix, uri, localName, value);
                 if (mustDeclarePrefix) {
                     writeNamespaceDeclaration(aPrefix, uri);
                 }
             }
         } catch (XMLStreamException e) {
             throw wrapException(e);
         }
     }

     /*
      * Currently, all attributes are quoted by XMLStreamWriter
      */
     public void writeAttributeUnquoted(String localName, String uri, 
        String value) 
     {
         writeAttribute(localName, uri, value);
     }

     public void writeChars(String chars) {
         try {
             writer.writeCharacters(chars);
         } catch (XMLStreamException e) {
             throw wrapException(e);
         }
     }

     public void writeChars(CDATA chars) {
         writeChars(chars.getText());
     }

     /**
      * If the first character of the string is an opening bracket,
      * then this method assumes that the string is xml and it
      * parses the string to write out the correct xml representation.
      * This is because there is no api in stax to write unescaped
      * characters.
      */
     public void writeCharsUnquoted(String chars) {
         try {
             if (chars.charAt(0) == '<') {
                 parseAndWriteXML(chars);
                 return;
             }
             writer.writeCharacters(chars);
         } catch (XMLStreamException e) {
             throw wrapException(e);
         }
     }

     /**
      * If the first character of the string is an opening bracket,
      * then this method assumes that the string is xml and it
      * parses the string to write out the correct xml representation.
      * This is because there is no api in stax to write unescaped
      * characters.
      */
     public void writeCharsUnquoted(char[] buf, int offset, int len) {
         try {
             if (buf[offset] == '<') {
                 parseAndWriteXML(new String(buf, offset, len));
                 return;
             }
             writer.writeCharacters(buf, offset, len);
         } catch (XMLStreamException e) {
             throw wrapException(e);
         }
     }

     public void writeComment(String comment) {
         try {
             writer.writeComment(comment);
         } catch (XMLStreamException e) {
             throw wrapException(e);
         }
     }

     /*
      * currently using setPrefix rather than writeNamespace due
      * to bug in impl. will revisit after switching to zephyr
      */
     public void writeNamespaceDeclaration(String prefix, String uri) {
         try {
             writer.writeNamespace(prefix, uri);        
             // Must call setPrefix() as well
             if (prefix == null || prefix.length() == 0 || prefix.equals("xmlns")) {
                 writer.setDefaultNamespace(uri);
             }
             else {
                 writer.setPrefix(prefix, uri);
             }
         } catch (XMLStreamException e) {
             throw wrapException(e);
         }
     }

     /*
      * Check namespace context for prefix before checking
      * prefix factory.
      */
     public void writeNamespaceDeclaration(String uri) {
         // String aPrefix = writer.getNamespaceContext().getPrefix(uri);
         try {
            String aPrefix = writer.getPrefix(uri);
            if (aPrefix == null) {
                 if (prefixFactory == null) {
                     throw new XMLWriterException("xmlwriter.noPrefixForURI", uri);
                } else {
                     aPrefix = prefixFactory.getPrefix(uri);
                }
            }
            writeNamespaceDeclaration(aPrefix, uri);
         }
         catch (XMLStreamException e) {
             throw wrapException(e);
         }
     }

     public void endElement() {
         try {
             writer.writeEndElement();
         } catch (XMLStreamException e) {
             throw wrapException(e);
         }
     }

     public void close() {
         try {
             if (!documentEnded) {
                 writer.writeEndDocument();
             }
             writer.close();
         } catch (XMLStreamException e) {
             throw wrapException(e);
         }
     }

     public void flush() {
         try {
             writer.flush();
         } catch (XMLStreamException e) {
             throw wrapException(e);
         }
     }

     public String getPrefix(String uri) {
         try {
             return writer.getPrefix(uri);
         } catch (XMLStreamException e) {
             throw wrapException(e);
         }
     }

     public String getURI(String prefix) {
         return writer.getNamespaceContext().getNamespaceURI(prefix);
     }

     public PrefixFactory getPrefixFactory() {
         return prefixFactory;
     }

     public void setPrefixFactory(PrefixFactory factory) {
         prefixFactory = factory;
     }

     /*
      * see writeCharsUnquoted()
      */
     private void parseAndWriteXML(String xml) throws XMLStreamException 
{
         XMLReader reader = new StAXReader(new StringReader(xml), true);
         int state = XMLReader.BOF;
         do {
             state = reader.next();
             switch(state) {
                 case XMLReader.START:
                     QName elementName = reader.getName();
                     startElement(elementName.getLocalPart(),
                         elementName.getNamespaceURI(),
                         elementName.getPrefix());
                     break;
                 case XMLReader.END:
                     endElement();
                     break;
                 case XMLReader.CHARS:
                     writeChars(reader.getValue());
             }
         } while(state != XMLReader.EOF);
     }

     private XMLWriterException wrapException(XMLStreamException e) {
         return new XMLWriterException(
             "xmlwriter.ioException",
             new LocalizableExceptionAdapter(e));
     }
}

