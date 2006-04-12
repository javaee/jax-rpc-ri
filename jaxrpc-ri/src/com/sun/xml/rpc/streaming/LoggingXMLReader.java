/*
 * $Id: LoggingXMLReader.java,v 1.1 2006-04-12 20:32:47 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.streaming;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Iterator;

import javax.xml.namespace.QName;

/**
 *
 * @author JAX-RPC Development Team
 */
public class LoggingXMLReader implements XMLReader {
    PrintWriter log;
    XMLReader reader;

    public LoggingXMLReader(OutputStream log, XMLReader reader) {
        this(new PrintWriter(log), reader);
    }

    public LoggingXMLReader(PrintWriter log, XMLReader reader) {
        this.log = log;
        this.reader = reader;
    }

    public int next() {
        int nextState = reader.next();
        log.println("Next state: " + XMLReaderUtil.getStateName(reader));
        return nextState;
    }
    public int nextContent() {
        int nextState = reader.nextContent();
        log.println(
            "Next content state: " + XMLReaderUtil.getStateName(reader));
        return nextState;
    }
    public int nextElementContent() {
        int nextState = reader.nextElementContent();
        log.println(
            "Next element content state: "
                + XMLReaderUtil.getStateName(reader));
        return nextState;
    }
    public int getState() {
        int currentState = reader.getState();
        log.println("Current state: " + XMLReaderUtil.getStateName(reader));
        return currentState;
    }
    public QName getName() {
        QName name = reader.getName();
        log.println("name: " + name);
        return name;
    }
    public String getURI() {
        String uri = reader.getURI();
        log.println("uri: " + uri);
        return uri;
    }
    public String getLocalName() {
        String localName = reader.getLocalName();
        log.println("localName: " + localName);
        return localName;
    }
    public Attributes getAttributes() {
        Attributes attributes = reader.getAttributes();
        log.println("attributes: " + attributes);
        return attributes;
    }
    public String getValue() {
        String value = reader.getValue();
        log.println("value: " + value);
        return value;
    }
    public int getElementId() {
        int id = reader.getElementId();
        log.println("id: " + id);
        return id;
    }
    public int getLineNumber() {
        int lineNumber = reader.getLineNumber();
        log.println("lineNumber: " + lineNumber);
        return lineNumber;
    }
    public String getURI(String prefix) {
        String uri = reader.getURI(prefix);
        log.println("uri for: " + prefix + ": " + uri);
        return uri;
    }
    public Iterator getPrefixes() {
        return reader.getPrefixes();
    }
    public XMLReader recordElement() {
        return reader.recordElement();
    }
    public void skipElement() {
        reader.skipElement();
        log.println("Skipped to: " + XMLReaderUtil.getStateName(reader));
    }
    public void skipElement(int elementId) {
        reader.skipElement(elementId);
        log.println("Skipped to: " + XMLReaderUtil.getStateName(reader));
    }
    public void close() {
        reader.close();
        log.println("reader closed");
    }
}