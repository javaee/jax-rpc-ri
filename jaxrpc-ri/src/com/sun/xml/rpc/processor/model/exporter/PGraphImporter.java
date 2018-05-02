/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2018 Sun Microsystems, Inc. All rights reserved.
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

