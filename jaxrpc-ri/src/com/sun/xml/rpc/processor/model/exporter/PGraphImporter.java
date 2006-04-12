/*
 * $Id: PGraphImporter.java,v 1.1 2006-04-12 20:34:34 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


package com.sun.xml.rpc.processor.model.exporter;

import java.io.InputStream;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.model.ModelException;
import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLReaderUtil;

/**
 * @author JAX-RPC Development Team
 */
public class PGraphImporter extends ImporterBase {
    
    public PGraphImporter(InputStream s) {
        super(s);
    }
    
    public PGraph doImport() {
        return (PGraph) internalDoImport();
    }
    
    protected Object internalDoImport() {
        initialize();
        PGraph graph = new PGraph();
        
        reader.nextElementContent();
        if (reader.getState() != XMLReader.START) {
            failInvalidSyntax(reader);
        }
        graph.setName(reader.getName());
        String versionAttr = getRequiredAttribute(reader, ATTR_VERSION);
        graph.setVersion(versionAttr);
        while (reader.nextElementContent() != XMLReader.END) {
            if (reader.getName().equals(getDefineImmediateObjectName())) {
                parseDefineImmediateObject(reader);
            } else if (reader.getName().equals(getDefineObjectName())) {
                parseDefineObject(reader);
            } else if (reader.getName().equals(getPropertyName())) {
                parseProperty(reader);
            } else {
                failInvalidSyntax(reader);
            }
        }
        XMLReaderUtil.verifyReaderState(reader, XMLReader.END);
        
        // the root object is invariably the first
        graph.setRoot((PObject) id2obj.get(new Integer(1)));
        return graph;
    }
    
    protected void parseDefineObject(XMLReader reader) {
        String idAttr = getRequiredAttribute(reader, ATTR_ID);
        String typeAttr = getRequiredAttribute(reader, ATTR_TYPE);
        Integer id = parseId(reader, idAttr);
        if (getObjectForId(id) != null) {
            failInvalidId(reader, id);
        }
        PObject obj = createPObject();
        obj.setType(typeAttr);
        id2obj.put(id, obj);
        verifyNoContent(reader);
    }
    
    protected PObject createPObject() {
        return new PObject();
    }
    
    protected QName getContainerName() {
        
        // unused
        return null;
    }
    
    protected void property(XMLReader reader, Object subject,
        String name, Object value) {
            
        if (subject instanceof PObject) {
            PObject obj = (PObject) subject;
            obj.setProperty(name, value);
        } else {
            super.property(reader, subject, name, value);
        }
    }
    
    protected void failInvalidSyntax(XMLReader reader) {
        throw new ModelException("model.importer.syntaxError",
            Integer.toString(reader.getLineNumber()));
    }
    
    protected void failInvalidVersion(XMLReader reader, String version) {
        throw new ModelException("model.importer.invalidVersion",
            new Object[] { Integer.toString(reader.getLineNumber()), version });
    }
    
	protected void failInvalidMinorMinorOrPatchVersion(
		XMLReader reader,
		String targetVersion,
		String currentVersion) {
		throw new ModelException(
			"model.importer.invalidMinorMinorOrPatchVersion",
			new Object[] {
				Integer.toString(reader.getLineNumber()),
				targetVersion,
				currentVersion });
	}
    
    protected void failInvalidClass(XMLReader reader, String className) {
        throw new ModelException("model.importer.invalidClass",
            new Object[] { Integer.toString(reader.getLineNumber()),
                className });
    }
    
    protected void failInvalidId(XMLReader reader, Integer id) {
        throw new ModelException("model.importer.invalidId",
            new Object[] { Integer.toString(reader.getLineNumber()),
                id.toString()});
    }
    
    protected void failInvalidLiteral(XMLReader reader,
        String type, String value) {
            
        throw new ModelException("model.importer.invalidLiteral",
            Integer.toString(reader.getLineNumber()));
    }
    
    protected void failInvalidProperty(XMLReader reader, Object subject,
        String name, Object value) {
            
        throw new ModelException("model.importer.invalidProperty",
            Integer.toString(reader.getLineNumber()));
    }
    
}

