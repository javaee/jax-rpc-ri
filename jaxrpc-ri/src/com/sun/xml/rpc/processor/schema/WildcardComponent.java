/*
 * $Id: WildcardComponent.java,v 1.1 2006-04-12 20:35:08 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.schema;

import java.util.Set;

/**
 *
 * @author JAX-RPC Development Team
 */
public class WildcardComponent extends Component {
    
    public static final int NAMESPACE_CONSTRAINT_ANY = 1;
    public static final int NAMESPACE_CONSTRAINT_NOT = 2;
    public static final int NAMESPACE_CONSTRAINT_NOT_ABSENT = 3;
    public static final int NAMESPACE_CONSTRAINT_SET = 4;
    public static final int NAMESPACE_CONSTRAINT_SET_OR_ABSENT = 5;
    
    public WildcardComponent() {}
    
    public void setProcessContents(Symbol s) {
        _processContents = s;
    }
    
    public int getNamespaceConstraintTag() {
        return _namespaceConstraintTag;
    }
    
    public void setNamespaceConstraintTag(int i) {
        _namespaceConstraintTag = i;
    }
    
    public String getNamespaceName() {
        return _namespaceName;
    }
    
    public void setNamespaceName(String s) {
        _namespaceName = s;
    }
    
    public void accept(ComponentVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    
    private Symbol _processContents;
    private int _namespaceConstraintTag;
    private String _namespaceName;
    private Set _namespaceSet;
    private AnnotationComponent _annotation;
}
