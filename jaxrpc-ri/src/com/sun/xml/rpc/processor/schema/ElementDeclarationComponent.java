/*
 * $Id: ElementDeclarationComponent.java,v 1.2 2006-04-13 01:31:39 ofung Exp $
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

import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

/**
 *
 * @author JAX-RPC Development Team
 */
public class ElementDeclarationComponent extends Component {
    
    public ElementDeclarationComponent() {}
    
    public QName getName() {
        return _name;
    }
    
    public void setName(QName n) {
        _name = n;
    }
    
    public TypeDefinitionComponent getTypeDefinition() {
        return _typeDefinition;
    }
    
    public void setTypeDefinition(TypeDefinitionComponent c) {
        _typeDefinition = c;
    }
    
    public void accept(ComponentVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    
    public ComplexTypeDefinitionComponent getScope() {
        return _scope;
    }
    
    public void setScope(ComplexTypeDefinitionComponent c) {
        _scope = c;
    }
    
    public boolean isNillable() {
        return _nillable;
    }
    
    public void setNillable(boolean b) {
        _nillable = b;
    }
    
    public void setValue(String s) {
        _value = s;
    }
    
    public void setValueKind(Symbol s) {
        _valueKind = s;
    }
    
    public void setDisallowedSubstitutions(Set s) {
        _disallowedSubstitutions = s;
    }
    
    public void setSubstitutionsGroupExclusions(Set s) {
        _substitutionGroupExclusions = s;
    }
    
    public void setAbstract(boolean b) {
        _abstract = b;
    }
    
    private QName _name;
    private TypeDefinitionComponent _typeDefinition;
    private ComplexTypeDefinitionComponent _scope;
    private String _value;
    private Symbol _valueKind;
    private boolean _nillable;
    private List _identityConstraintDefinitions;
    private ElementDeclarationComponent _substitutionGroupAffiliation;
    private Set _substitutionGroupExclusions;
    private Set _disallowedSubstitutions;
    private boolean _abstract;
    private AnnotationComponent _annotation;
}
