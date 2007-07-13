/*
 * $Id: ComplexTypeDefinitionComponent.java,v 1.3 2007-07-13 23:36:19 ofung Exp $
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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *
 * @author JAX-RPC Development Team
 */
public class ComplexTypeDefinitionComponent extends TypeDefinitionComponent {
    
    public static final int CONTENT_EMPTY = 1;
    public static final int CONTENT_SIMPLE = 2;
    public static final int CONTENT_MIXED = 3;
    public static final int CONTENT_ELEMENT_ONLY = 4;
    
    public ComplexTypeDefinitionComponent() {
        _attributeUses = new ArrayList();
    }
    
    public boolean isComplex() {
        return true;
    }
    
    public TypeDefinitionComponent getBaseTypeDefinition() {
        return _baseTypeDefinition;
    }
    
    public void setBaseTypeDefinition(TypeDefinitionComponent c) {
        _baseTypeDefinition = c;
    }
    
    public Symbol getDerivationMethod() {
        return _derivationMethod;
    }
    
    public void setDerivationMethod(Symbol s) {
        _derivationMethod = s;
    }
    
    public void setProhibitedSubstitutions(Set s) {
        _prohibitedSubstitutions = s;
    }
    
    public void setFinal(Set s) {
        _final = s;
    }
    
    public boolean isAbstract() {
        return _abstract;
    }
    
    public void setAbstract(boolean b) {
        _abstract = b;
    }
    
    public Iterator attributeUses() {
        return _attributeUses.iterator();
    }
    
    public boolean hasNoAttributeUses() {
        return _attributeUses.size() == 0;
    }
    
    public void addAttributeUse(AttributeUseComponent c) {
        _attributeUses.add(c);
    }
    
    public void addAttributeGroup(AttributeGroupDefinitionComponent c) {
        for (Iterator iter = c.attributeUses(); iter.hasNext();) {
            AttributeUseComponent a = (AttributeUseComponent) iter.next();
            addAttributeUse(a);
        }
    }
    
    public int getContentTag() {
        return _contentTag;
    }
    
    public void setContentTag(int i) {
        _contentTag = i;
    }
    
    public SimpleTypeDefinitionComponent getSimpleTypeContent() {
        return _simpleTypeContent;
    }
    
    public void setSimpleTypeContent(SimpleTypeDefinitionComponent c) {
        _simpleTypeContent = c;
    }
    
    public ParticleComponent getParticleContent() {
        return _particleContent;
    }
    
    public void setParticleContent(ParticleComponent c) {
        _particleContent = c;
    }
    
    public void accept(ComponentVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    
    private TypeDefinitionComponent _baseTypeDefinition;
    private Symbol _derivationMethod;
    private Set _final;
    private boolean _abstract;
    private List _attributeUses;
    private WildcardComponent _attributeWildcard;
    private int _contentTag;
    private SimpleTypeDefinitionComponent _simpleTypeContent;
    private ParticleComponent _particleContent;
    private Set _prohibitedSubstitutions;
    private List _annotations;
}
