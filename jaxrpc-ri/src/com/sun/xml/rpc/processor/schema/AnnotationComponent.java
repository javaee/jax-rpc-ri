/*
 * $Id: AnnotationComponent.java,v 1.1 2006-04-12 20:35:08 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.schema;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sun.xml.rpc.util.NullIterator;
import com.sun.xml.rpc.wsdl.document.schema.SchemaAttribute;
import com.sun.xml.rpc.wsdl.document.schema.SchemaElement;

/**
 *
 * @author JAX-RPC Development Team
 */
public class AnnotationComponent extends Component {
    
    public AnnotationComponent() {}
    
    public void addApplicationInformation(SchemaElement element) {
        if (_applicationInformationElements == null) {
            _applicationInformationElements = new ArrayList();
        }
        
        _applicationInformationElements.add(element);
    }
    
    public void addUserInformation(SchemaElement element) {
        if (_userInformationElements == null) {
            _userInformationElements = new ArrayList();
        }
        
        _userInformationElements.add(element);
    }
    
    public Iterator attributes() {
        if (_attributes == null) {
            return NullIterator.getInstance();
        } else {
            return _attributes.iterator();
        }
    }
    
    public void addAttribute(SchemaAttribute attribute) {
        if (_attributes == null) {
            _attributes = new ArrayList();
        }
        
        _attributes.add(attribute);
    }
    
    public void accept(ComponentVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    
    private List _applicationInformationElements;
    private List _userInformationElements;
    private List _attributes;
}
