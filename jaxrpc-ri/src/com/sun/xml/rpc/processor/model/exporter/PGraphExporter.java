/*
 * $Id: PGraphExporter.java,v 1.3 2007-07-13 23:36:05 ofung Exp $
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
