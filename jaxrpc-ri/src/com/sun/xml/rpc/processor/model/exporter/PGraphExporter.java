/*
 * $Id: PGraphExporter.java,v 1.2 2006-04-13 01:29:37 ofung Exp $
 */

/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License).  You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the license at
 * https://glassfish.dev.java.net/public/CDDLv1.0.html.
 * See the License for the specific language governing
 * permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at https://glassfish.dev.java.net/public/CDDLv1.0.html.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * you own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
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
