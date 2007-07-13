/*
 * $Id: InternalSchema.java,v 1.3 2007-07-13 23:36:19 ofung Exp $
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

package com.sun.xml.rpc.processor.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.model.ModelException;

/**
 *
 * @author JAX-RPC Development Team
 */
public class InternalSchema {
    
    public InternalSchema(InternalSchemaBuilderBase builder) {
        _builder = builder;
        
        _typeDefinitions = new HashMap();
        _attributeDeclarations = new HashMap();
        _elementDeclarations = new HashMap();
        _attributeGroupDefinitions = new HashMap();
        _modelGroupDefinitions = new HashMap();
        _notationDeclarations = new ArrayList();
        _annotations = new ArrayList();
    }
    
    public void add(TypeDefinitionComponent c) {
        _typeDefinitions.put(c.getName(), c);
    }
    
    public TypeDefinitionComponent findTypeDefinition(QName name) {
        Object result = _typeDefinitions.get(name);
        if (result == null) {
            try {
                result = _builder.getTypeDefinitionComponentBeingDefined(name);
                if (result == null) {
                    result = _builder.buildTypeDefinition(name);
                }
            } catch (ModelException e) {
                result = e;
                _typeDefinitions.put(name, result);
            }
        }
        if (result instanceof ModelException) {
            throw (ModelException) result;
        } else {
            return (TypeDefinitionComponent) result;
        }
    }
    
    public void add(AttributeDeclarationComponent c) {
        _attributeDeclarations.put(c.getName(), c);
    }
    
    public AttributeDeclarationComponent findAttributeDeclaration(QName name) {
        Object result = _attributeDeclarations.get(name);
        if (result == null) {
            try {
                result = _builder.buildAttributeDeclaration(name);
            } catch (ModelException e) {
                result = e;
                _attributeDeclarations.put(name, result);
            }
        }
        if (result instanceof ModelException) {
            throw (ModelException) result;
        } else {
            return (AttributeDeclarationComponent) result;
        }
    }
    
    public void add(ElementDeclarationComponent c) {
        _elementDeclarations.put(c.getName(), c);
    }
    
    public ElementDeclarationComponent findElementDeclaration(QName name) {
        Object result = _elementDeclarations.get(name);
        if (result == null) {
            try {
                result = _builder.buildElementDeclaration(name);
            } catch (ModelException e) {
                result = e;
                _elementDeclarations.put(name, result);
            }
        }
        if (result instanceof ModelException) {
            throw (ModelException) result;
        } else {
            return (ElementDeclarationComponent) result;
        }
    }
    
    public void add(AttributeGroupDefinitionComponent c) {
        _attributeGroupDefinitions.put(c.getName(), c);
    }
    
    public AttributeGroupDefinitionComponent findAttributeGroupDefinition(
        QName name) {
            
        Object result = _attributeGroupDefinitions.get(name);
        if (result == null) {
            try {
                result = _builder.buildAttributeGroupDefinition(name);
            } catch (ModelException e) {
                result = e;
                _attributeGroupDefinitions.put(name, result);
            }
        }
        if (result instanceof ModelException) {
            throw (ModelException) result;
        } else {
            return (AttributeGroupDefinitionComponent) result;
        }
    }
    
    public void add(ModelGroupDefinitionComponent c) {
        _modelGroupDefinitions.put(c.getName(), c);
    }
    
    public ModelGroupDefinitionComponent findModelGroupDefinition(QName name) {
        Object result = _modelGroupDefinitions.get(name);
        if (result == null) {
            try {
                result = _builder.buildModelGroupDefinition(name);
            } catch (ModelException e) {
                result = e;
                _modelGroupDefinitions.put(name, result);
            }
        }
        if (result instanceof ModelException) {
            throw (ModelException) result;
        } else {
            return (ModelGroupDefinitionComponent) result;
        }
    }
    
    public void add(NotationDeclarationComponent c) {
        _notationDeclarations.add(c);
    }
    
    public void add(AnnotationComponent c) {
        _annotations.add(c);
    }
    
    public SimpleTypeDefinitionComponent getSimpleUrType() {
        return _builder.getSimpleUrType();
    }
    
    public ComplexTypeDefinitionComponent getUrType() {
        return _builder.getUrType();
    }
    
    private InternalSchemaBuilderBase _builder;
    private Map _typeDefinitions;
    private Map _attributeDeclarations;
    private Map _elementDeclarations;
    private Map _attributeGroupDefinitions;
    private Map _modelGroupDefinitions;
    private List _notationDeclarations;
    private List _annotations;
}
