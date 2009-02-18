/*
 * $Id: InternalSchema.java,v 1.2.2.1 2009-02-18 15:35:05 anbubala Exp $
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
                // CR-6610901, Merge from JavaCAPS RTS for backward compatibility
                //result = _builder.buildModelGroupDefinition(name);
                result = _builder.buildModelGroupDefinition(name, null);
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
