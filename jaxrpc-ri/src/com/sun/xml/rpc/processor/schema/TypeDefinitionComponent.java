/*
 * $Id: TypeDefinitionComponent.java,v 1.1 2006-04-12 20:35:07 kohlert Exp $
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
public abstract class TypeDefinitionComponent extends Component {
    
    public TypeDefinitionComponent() {}
    
    public QName getName() {
        return _name;
    }
    
    public void setName(QName name) {
        _name = name;
    }
    
    public boolean isSimple() {
        return false;
    }
    
    public boolean isComplex() {
        return false;
    }
    
    private QName _name;
}
