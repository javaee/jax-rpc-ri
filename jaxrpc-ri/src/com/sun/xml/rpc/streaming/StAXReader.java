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

import org.xml.sax.InputSource;

import java.io.StringReader;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.*;
import javax.xml.transform.Source;

import com.sun.xml.rpc.sp.NamespaceSupport;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import com.sun.xml.rpc.util.xml.CDATA;

// needed for stax workaround for bug 5045462
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import com.sun.xml.messaging.saaj.util.ByteInputStream;
import com.sun.xml.messaging.saaj.util.ByteOutputStream;
import com.sun.xml.rpc.util.xml.XmlUtil;
import java.io.InputStream;
// end of stax workaround

/**
  * An implementation of XMLReader that uses StAX
  */
public class StAXReader extends XMLReaderBase {

     static final String XMLNS_NAMESPACE_URI = "http://www.w3.org/2000/xmlns/";

     // the stream reader that does the work
     private XMLStreamReader reader;

     // current state of reader
     private int state;

     // used for XMLStreamReader states that do not map to XMLReader 
     private static final int CONTINUE = -10;

     // map of StAX xml events to XMLReaderEvents states
     private static final int [] stax2XMLReader = new int [16];
     static {
         for (int i=0; i<16; i++) {
             stax2XMLReader[i] = CONTINUE;
         }
         stax2XMLReader[XMLStreamConstants.START_DOCUMENT] = XMLReader.BOF;
         stax2XMLReader[XMLStreamConstants.START_ELEMENT] = XMLReader.START;
         stax2XMLReader[XMLStreamConstants.END_ELEMENT] = XMLReader.END;
         stax2XMLReader[XMLStreamConstants.CHARACTERS] = XMLReader.CHARS;
         stax2XMLReader[XMLStreamConstants.PROCESSING_INSTRUCTION] = XMLReader.PI;
         stax2XMLReader[XMLStreamConstants.END_DOCUMENT] = XMLReader.EOF;
     }

     private QName currentName;
     private AttributesImpl currentAttributes;
     private ArrayList allPrefixes;
     private ElementIdStack elementIds;
     private int elementId;
     //private static XMLInputFactory inputFactory;

     public StAXReader(InputSource source, boolean rejectDTDs) {
         try {
             reader = getInputFactory().createXMLStreamReader(
                 source.getByteStream(), source.getEncoding());
             finishSetup();
         } catch (XMLStreamException e) {
             throw new XMLReaderException("staxreader.xmlstreamexception",
                 new LocalizableExceptionAdapter(e));
         }
     }
     
     public StAXReader(InputSource source, boolean rejectDTDs, 
        XMLStreamReader reader) 
     {
         this.reader = reader;
         finishSetup();
     }

     public StAXReader(Source source, boolean rejectDTDs) {
         try {
             /* still getting exception when trying this, so add workaround
              * for now. will ask zephyr team again.
              */
             boolean workingYet = false;
             if (workingYet) {
                 reader = getInputFactory().createXMLStreamReader(source);
             } else {
                 Transformer transformer = XmlUtil.newTransformer();
                 ByteOutputStream bos = new ByteOutputStream();
                 transformer.transform(source, new StreamResult(bos));
                 InputStream istream =
                     new ByteInputStream(bos.getBytes(), bos.getCount());
                 InputSource iSource = new InputSource(istream);
                 reader = getInputFactory().createXMLStreamReader(
                     iSource.getByteStream(), iSource.getEncoding());
             }
             finishSetup();
         } catch (XMLStreamException e) {
             throw new XMLReaderException("staxreader.xmlstreamexception",
                 new LocalizableExceptionAdapter(e));
         } catch (TransformerException te) {
             // thrown by the workaround code. todo (bobby) remove later
             throw new XMLReaderException("staxreader.xmlstreamexception",
                 new LocalizableExceptionAdapter(te));
         }
     }

     public StAXReader(StringReader stringReader, boolean rejectDTDs) {
         try {
             reader = getInputFactory().createXMLStreamReader(stringReader);
             finishSetup();
         } catch (XMLStreamException e) {
             throw new XMLReaderException("staxreader.xmlstreamexception",
                 new LocalizableExceptionAdapter(e));
         }
     }

     /*
      * Used by the constructors to get the input factory
      */
     private XMLInputFactory getInputFactory() throws XMLStreamException 
{
         //if (inputFactory == null) {
         XMLInputFactory inputFactory = XMLInputFactory.newInstance();
             
         inputFactory.setProperty("javax.xml.stream.isNamespaceAware",
                 Boolean.TRUE);
             inputFactory.setProperty("javax.xml.stream.isCoalescing",
                 Boolean.TRUE);
         //}
         return inputFactory;
     }

     /*
      * Used by the constructors to finish setup
      */
     private void finishSetup() {

         state = stax2XMLReader[reader.getEventType()];

         // this will store all parsed prefixes for getPrefixes()
         allPrefixes = new ArrayList();

         elementIds = new ElementIdStack();
     }

     /**
      * Returns the StAX XMLStreamReader that is being used. If this is
      * called, the code must call StAXReader.synchronize() after the
      * stream reader has been used so that the StAXReader has correct
      * information.
      *
      * @return The actual XMLStreamReader being used. The StAXReader object
      * will not contain valid information once the underlying reader is
      * used separately.
      *
      * @see com.sun.xml.rpc.streaming.StAXReader#synchronizeReader
      */
     public XMLStreamReader getXMLStreamReader() {
         return reader;
     }

     /**
      * Used to resync the StAXReader with its underlying XMLStreamReader.
      * The element id stack may not be valid.
      *
      * @see com.sun.xml.rpc.streaming.StAXReader#getXMLStreamReader
      */
     public void synchronizeReader() {
         currentName = null;
         currentAttributes = null;
         state = stax2XMLReader[reader.getEventType()];
     }

     public int next() {
         if (state == EOF) {
             return EOF;
         }

         currentName = null;
         currentAttributes = null;

         try {
             do {
                 state = stax2XMLReader[reader.next()];
             } while (state == CONTINUE);

             if (state == START) {
                 collectPrefixes();
                 elementId = elementIds.pushNext();
             } else if (state == END) {
                 elementId = elementIds.pop();
             }

         } catch (XMLStreamException e) {
             throw new XMLReaderException(
                 "staxreader.xmlstreamexception",
                 new LocalizableExceptionAdapter(e));
         }

         return state;
     }

     public int getState() {
         return state;
     }

     public QName getName() {
         if (currentName == null) {
             currentName = reader.getName();
         }
         return currentName;
     }

     public String getLocalName() {
         return reader.getLocalName();
     }

     public String getURI() {
         return reader.getNamespaceURI();
     }

     public Attributes getAttributes() {
         if (currentAttributes == null) {
             if (reader.getEventType() == reader.START_ELEMENT ||
                 reader.getEventType() == reader.ATTRIBUTE) {

                 currentAttributes = new AttributesImpl(reader);
             } else {
                 // create empty Attributes object when reader in illegal state
                 currentAttributes = new AttributesImpl(null);
             }
         }
         return currentAttributes;
     }

     public String getURI(String prefix) {
         return reader.getNamespaceURI(prefix);
     }

     public String getValue() {
         return reader.getText();
     }

     public int getLineNumber() {
         return reader.getLocation().getLineNumber();
     }

     /*
      * The goal is to make this method as quick as possible to
      * not slow down normal parsing. See getPrefixes() for more info.
      */
     private void collectPrefixes() {
         for (int i=0; i<reader.getNamespaceCount(); i++) {
             String prefix = reader.getNamespacePrefix(i);
             if (prefix != null) {
                 allPrefixes.add(prefix);
             }
         }
     }

     /*
      * This method is not called often, so instead of making regular
      * parsing slower by keeping track of scope, we keep a simple
      * list off all prefixes that have been read and then check them
      * here. So this is a relatively expensive operation. Checks all
      * prefixes to see if they are still bound or not -- if so, adds
      * them to a set to eliminate duplicates and returns an iterator
      * over the set.
      */
     public Iterator getPrefixes() {
         Iterator iter = allPrefixes.iterator();
         HashSet set = new HashSet(allPrefixes.size());
          while (iter.hasNext()) {
             String prefix = (String) iter.next();
             if (reader.getNamespaceURI(prefix) != null) {
                 set.add(prefix);
             }
         }
         return set.iterator();
     }


     public int getElementId() {
         return elementId;
     }

     // taken from XMLReaderImpl
     public void skipElement(int id) {
         while (!(state == EOF || (state == END && elementId == id))) {
             next();
         }
     }

     /*
      * todo -- update NamespaceSupport to take a NamespaceContext object
      * in a constructor
      */
     public XMLReader recordElement() {
         return new RecordedXMLReader(this,
             new NamespaceContextWrapper(reader.getNamespaceContext()));
     }

     public void close() {
         state = EOF;
         try {
             reader.close();
         } catch (XMLStreamException e) {
             throw new 
XMLReaderException("staxreader.xmlstreamexception",
                 new LocalizableExceptionAdapter(e));
         }
     }

     // method used for internal testing
     private void printStreamConstants() {
         System.out.println(" ");
         System.out.println("XMLStreamConstants.START_ELEMENT: " +
             XMLStreamConstants.START_ELEMENT);
         System.out.println("XMLStreamConstants.END_ELEMENT: " +
             XMLStreamConstants.END_ELEMENT);
         System.out.println("XMLStreamConstants.PROCESSING_INSTRUCTION: " +
             XMLStreamConstants.PROCESSING_INSTRUCTION);
         System.out.println("XMLStreamConstants.CHARACTERS: " +
             XMLStreamConstants.CHARACTERS);
         System.out.println("XMLStreamConstants.COMMENT: " +
             XMLStreamConstants.COMMENT);
         System.out.println("XMLStreamConstants.SPACE: " +
             XMLStreamConstants.SPACE);
         System.out.println("XMLStreamConstants.START_DOCUMENT: " +
             XMLStreamConstants.START_DOCUMENT);
         System.out.println("XMLStreamConstants.END_DOCUMENT: " +
             XMLStreamConstants.END_DOCUMENT);
         System.out.println("XMLStreamConstants.ENTITY_REFERENCE: " +
             XMLStreamConstants.ENTITY_REFERENCE);
         System.out.println("XMLStreamConstants.ATTRIBUTE: " +
             XMLStreamConstants.ATTRIBUTE);
         System.out.println("XMLStreamConstants.DTD: " +
             XMLStreamConstants.DTD);
         System.out.println("XMLStreamConstants.CDATA: " +
             XMLStreamConstants.CDATA);
         System.out.println("XMLStreamConstants.NAMESPACE: " +
             XMLStreamConstants.NAMESPACE);
         System.out.println("XMLStreamConstants.NOTATION_DECLARATION: " +
             XMLStreamConstants.NOTATION_DECLARATION);
         System.out.println("XMLStreamConstants.ENTITY_DECLARATION: " +
             XMLStreamConstants.ENTITY_DECLARATION);
     }

     public static class AttributesImpl implements Attributes {

         // stores qname and value for each attribute
         AttributeInfo [] atInfos;

         /*
          * Will create a list that contains the namespace declarations
          * as well as the other attributes.
          */
         public AttributesImpl(XMLStreamReader reader) {
             if (reader == null) {

                 // this is the case when we call getAttributes() on the
                 // reader when it is not on a start tag
                 atInfos = new AttributeInfo[0];
             } else {

                 // this is the normal case
                 int index = 0;
                 String namespacePrefix = null;
                 int namespaceCount = reader.getNamespaceCount();
                 int attributeCount = reader.getAttributeCount();
                 atInfos = new AttributeInfo[namespaceCount + attributeCount];
                 for (int i=0; i<namespaceCount; i++) {
                     namespacePrefix = reader.getNamespacePrefix(i);

                     // will be null if default prefix. QName can't take null
                     if (namespacePrefix == null) {
                         namespacePrefix = "";
                     }
                     atInfos[index++] = new AttributeInfo(
                         new QName(XMLNS_NAMESPACE_URI,
                             namespacePrefix,
                             "xmlns"),
                         reader.getNamespaceURI(i));
                 }
                 for (int i=0; i<attributeCount; i++) {
                     atInfos[index++] = new AttributeInfo(
                         reader.getAttributeName(i),
                         reader.getAttributeValue(i));
                 }
             }
         }

         public int getLength() {
             return atInfos.length;
         }

         public String getLocalName(int index) {
             if (index >= 0 && index < atInfos.length) {
                 return atInfos[index].getLocalName();
             }
             return null;
         }

         public QName getName(int index) {
             if (index >= 0 && index < atInfos.length) {
                 return atInfos[index].getName();
             }
             return null;
         }

         public String getPrefix(int index) {
             if (index >= 0 && index < atInfos.length) {
                 return atInfos[index].getName().getPrefix();
             }
             return null;
         }

         public String getURI(int index) {
             if (index >= 0 && index < atInfos.length) {
                 return atInfos[index].getName().getNamespaceURI();
             }
             return null;
         }

         public String getValue(int index) {
             if (index >= 0 && index < atInfos.length) {
                 return atInfos[index].getValue();
             }
             return null;
         }

         public String getValue(QName name) {
             int index = getIndex(name);
             if (index != -1) {
                 return atInfos[index].getValue();
             }
             return null;
         }

         public String getValue(String localName) {
             int index = getIndex(localName);
             if (index != -1) {
                 return atInfos[index].getValue();
             }
             return null;
         }

         public String getValue(String uri, String localName) {
             int index = getIndex(uri, localName);
             if (index != -1) {
                 return atInfos[index].getValue();
             }
             return null;
         }

         public boolean isNamespaceDeclaration(int index) {
             if (index >= 0 && index < atInfos.length) {
                 return atInfos[index].isNamespaceDeclaration();
             }
             return false;
         }

         public int getIndex(QName name) {
             for (int i=0; i<atInfos.length; i++) {
                 if (atInfos[i].getName().equals(name)) {
                     return i;
                 }
             }
             return -1;
         }

         public int getIndex(String localName) {
             for (int i=0; i<atInfos.length; i++) {
                 if (atInfos[i].getName().getLocalPart().equals(localName)) {
                     return i;
                 }
             }
             return -1;
         }

         public int getIndex(String uri, String localName) {
             QName qName;
             for (int i=0; i<atInfos.length; i++) {
                 qName = atInfos[i].getName();
                 if (qName.getNamespaceURI().equals(uri) &&
                     qName.getLocalPart().equals(localName)) {

                     return i;
                 }
             }
             return -1;
         }

     }

     // used by AttributeImpl to store attributes
     static class AttributeInfo {

         private QName name;
         private String value;

         public AttributeInfo(QName name, String value) {
             this.name = name;
             if (value == null) {
                 // e.g., <return xmlns=""> -- stax returns null
                 this.value = "";
             } else {
                 this.value = value;
             }
         }

         QName getName() {
             return name;
         }

         String getValue() {
             return value;
         }

         /*
          * Return "xmlns:" as part of name if namespace.
          */
         String getLocalName() {
             if (isNamespaceDeclaration()) {
                 if (name.getLocalPart().equals("")) {
                     return "xmlns";
                 }
                 return "xmlns:" + name.getLocalPart();
             }
             return name.getLocalPart();
         }

         boolean isNamespaceDeclaration() {
             return (name.getNamespaceURI() == XMLNS_NAMESPACE_URI);
         }
     }

     /* This class is used to make a NamespaceContext object act as a
      * NamespaceSupport object to pass to RecordedXMLReader. Am hoping
      * the context issues will take care of themselves as reader.next()
      * is called.
      */
     static class NamespaceContextWrapper extends NamespaceSupport {
         private NamespaceContext context;

         public NamespaceContextWrapper(NamespaceContext context) {
             this.context = context;
         }

         public String getPrefix(String uri) {
             return context.getPrefix(uri);
         }

         public String getURI(String prefix) {
             return context.getNamespaceURI(prefix);
         }

         public Iterator getPrefixes(String uri) {
             return context.getPrefixes(uri);
         }

         public Iterator getPrefixes() {
             if (true) { throw new UnsupportedOperationException(); }
             return null; // todo
         }

     }
}

