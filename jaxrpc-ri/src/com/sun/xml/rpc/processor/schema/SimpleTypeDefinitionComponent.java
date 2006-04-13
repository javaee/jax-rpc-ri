/*
 * $Id: SimpleTypeDefinitionComponent.java,v 1.2 2006-04-13 01:31:49 ofung Exp $
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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.sun.xml.rpc.util.NullIterator;

/**
 *
 * @author JAX-RPC Development Team
 */
public class SimpleTypeDefinitionComponent extends TypeDefinitionComponent {
    
    public static final int VARIETY_ATOMIC = 1;
    public static final int VARIETY_LIST = 2;
    public static final int VARIETY_UNION = 3;
    
    public SimpleTypeDefinitionComponent() {}
    
    public boolean isSimple() {
        return true;
    }
    
    public SimpleTypeDefinitionComponent getBaseTypeDefinition() {
        return _baseTypeDefinition;
    }
    
    public void setBaseTypeDefinition(SimpleTypeDefinitionComponent c) {
        _baseTypeDefinition = c;
    }
    
    public SimpleTypeDefinitionComponent getPrimitiveTypeDefinition() {
        return _primitiveTypeDefinition;
    }
    
    public void setPrimitiveTypeDefinition(SimpleTypeDefinitionComponent c) {
        _primitiveTypeDefinition= c;
    }
    
    public SimpleTypeDefinitionComponent getItemTypeDefinition() {
        return _itemTypeDefinition;
    }
    
    public void setItemTypeDefinition(SimpleTypeDefinitionComponent c) {
        _itemTypeDefinition = c;
    }
    
    public void setFinal(Set s) {
        _final = s;
    }
    
    public int getVarietyTag() {
        return _varietyTag;
    }
    
    public void setVarietyTag(int i) {
        _varietyTag = i;
    }
    
    public void addFacet(Facet f) {
        if (_facets == null) {
            _facets = new ArrayList();
        }
        
        _facets.add(f);
    }
    
    public Iterator facets() {
        return _facets == null ?
            NullIterator.getInstance() : _facets.iterator();
    }
    
    public void addFundamentalFacet(FundamentalFacet f) {
        if (_fundamentalFacets == null) {
            _fundamentalFacets = new ArrayList();
        }
        
        _fundamentalFacets.add(f);
    }
    
    public Iterator fundamentalFacets() {
        return _fundamentalFacets == null ?
            NullIterator.getInstance() : _fundamentalFacets.iterator();
    }
    
    public void accept(ComponentVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    
    private SimpleTypeDefinitionComponent _baseTypeDefinition;
    private List _facets;
    private List _fundamentalFacets;
    private Set _final;
    private int _varietyTag;
    private SimpleTypeDefinitionComponent _primitiveTypeDefinition;
    private SimpleTypeDefinitionComponent _itemTypeDefinition;
    private List _memberTypeDefinitions;
    private AnnotationComponent _annotation;
}
