/*
 * $Id: IdentityConstraintDefinitionComponent.java,v 1.1 2006-04-12 20:35:05 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.schema;

import java.util.List;

import javax.xml.namespace.QName;

/**
 *
 * @author JAX-RPC Development Team
 */
public class IdentityConstraintDefinitionComponent extends Component {
    
    public IdentityConstraintDefinitionComponent() {}
    
    public void accept(ComponentVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    
    private QName _name;
    private Symbol _identityConstraintCategory;
    private String _selector;
    private List _fields;
    private IdentityConstraintDefinitionComponent _referencedKey;
    private AnnotationComponent _annotation;
}
