/*
 * $Id: NotationDeclarationComponent.java,v 1.1 2006-04-12 20:35:06 kohlert Exp $
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
public class NotationDeclarationComponent extends Component {
    
    public NotationDeclarationComponent() {}
    
    public void accept(ComponentVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    
    private QName _name;
    private String _systemIdentifier;
    private String _publicIdentifier;
    private AnnotationComponent _annotation;
}
