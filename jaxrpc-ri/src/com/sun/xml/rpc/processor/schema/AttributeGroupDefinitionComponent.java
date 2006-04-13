/*
 * $Id: AttributeGroupDefinitionComponent.java,v 1.2 2006-04-13 01:31:35 ofung Exp $
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
