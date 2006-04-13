/*
 * $Id: WildcardComponent.java,v 1.2 2006-04-13 01:31:51 ofung Exp $
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
