/*
 * $Id: AnnotationComponent.java,v 1.2 2006-04-13 01:31:34 ofung Exp $
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
