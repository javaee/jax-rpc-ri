/*
 * $Id: AttributeDeclarationComponent.java,v 1.1 2006-04-12 20:35:06 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.schema;

import javax.xml.namespace.QName;

/**
 *
 * @author JAX-RPC Development Team
 */
public class AttributeDeclarationComponent extends Component {
    
    public AttributeDeclarationComponent() {}
    
    public QName getName() {
        return _name;
    }
    
    public void setName(QName name) {
        _name = name;
    }
    
    public SimpleTypeDefinitionComponent getTypeDefinition() {
        return _typeDefinition;
    }
    
    public void setTypeDefinition(SimpleTypeDefinitionComponent c) {
        _typeDefinition = c;
    }
    
    public ComplexTypeDefinitionComponent getScope() {
        return _scope;
    }
    
    public void setScope(ComplexTypeDefinitionComponent c) {
        _scope = c;
    }
    
    public void setValue(String s) {
        _value = s;
    }
    
    // bug fix: 4968046
    public String getValue() {
        return _value;
    }

    public void setValueKind(Symbol s) {
        _valueKind = s;
    }
    
    // bug fix: 4968046
    public Symbol getValueKind() {
        return _valueKind;
    }

    
    public AnnotationComponent getAnnotation() {
        return _annotation;
    }
    
    public void setAnnotation(AnnotationComponent c) {
        _annotation = c;
    }
    
    public void accept(ComponentVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    
    private QName _name;
    private SimpleTypeDefinitionComponent _typeDefinition;
    private ComplexTypeDefinitionComponent _scope;
    private String _value;
    private Symbol _valueKind;
    private AnnotationComponent _annotation;
    
}
