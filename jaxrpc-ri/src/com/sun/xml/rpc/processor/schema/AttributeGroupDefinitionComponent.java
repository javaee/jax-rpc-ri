/*
 * $Id: AttributeGroupDefinitionComponent.java,v 1.1 2006-04-12 20:35:07 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.schema;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

/**
 *
 * @author JAX-RPC Development Team
 */
public class AttributeGroupDefinitionComponent extends Component {
    
    public AttributeGroupDefinitionComponent() {
        _attributeUses = new ArrayList();
    }
    
    public QName getName() {
        return _name;
    }
    
    public void setName(QName name) {
        _name = name;
    }
    
    public void accept(ComponentVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    
    public Iterator attributeUses() {
        return _attributeUses.iterator();
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
    
    private QName _name;
    private List _attributeUses;
    private WildcardComponent _attributeWildcard;
    private AnnotationComponent _annotation;
}
