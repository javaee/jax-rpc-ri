/*
 * $Id: PGraphExporter.java,v 1.1 2006-04-12 20:34:35 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


package com.sun.xml.rpc.processor.model.exporter;

import java.io.OutputStream;
import java.util.Iterator;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.model.ModelException;

/**
 * @author JAX-RPC Development Team
 */
public class PGraphExporter extends ExporterBase {
    
    public PGraphExporter(OutputStream s) {
        super(s);
    }
    
    public void doExport(PGraph g) {
        internalDoExport(g);
    }
    
    protected void internalDoExport(Object root) {
        initialize();
        PGraph graph = (PGraph) root;
        writer.startElement(graph.getName());
        if (graph.getVersion() != null) {
            writer.writeAttribute(ATTR_VERSION, graph.getVersion());
        }
        int id = getId(graph.getRoot());
        while (!obj2serializeStack.empty()) {
            Object obj = obj2serializeStack.pop();
            obj2serialize.remove(obj);
            visit(obj);
        }
        writer.endElement();
        writer.close();
    }
    
    protected void define(Object obj, Integer id) {
        if (obj instanceof PObject) {
            PObject anObject = (PObject) obj;
            writer.startElement(getDefineObjectName());
            writer.writeAttribute(ATTR_ID, id.toString());
            writer.writeAttribute(ATTR_TYPE, anObject.getType());
            writer.endElement();
            obj2serialize.add(obj);
            obj2serializeStack.push(obj);
        } else {
            super.define(obj, id);
        }
    }
    
    protected void failUnsupportedClass(Class klass) {
        throw new ModelException("model.exporter.unsupportedClass",
            klass.getName());
    }
    
    protected QName getContainerName() {
        
        // unused
        return null;
    }
    
    protected void visit(Object obj) {
        if (obj == null) {
            return;
        }
        if (obj instanceof PObject) {
            PObject anObject = (PObject) obj;
            for (Iterator iter = anObject.getPropertyNames(); iter.hasNext();) {
                String name = (String) iter.next();
                Object value = anObject.getProperty(name);
                property(name, obj, value);
            }
        } else {
            super.visit(obj);
        }
    }
    
}
