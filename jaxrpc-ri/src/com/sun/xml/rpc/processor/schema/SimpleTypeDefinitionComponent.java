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
