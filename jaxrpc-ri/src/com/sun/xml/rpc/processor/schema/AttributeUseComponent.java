/*
 * $Id: AttributeUseComponent.java,v 1.3 2007-07-13 23:36:19 ofung Exp $
 */

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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

/**
 *
 * @author JAX-RPC Development Team
 */
public class AttributeUseComponent extends Component {
    
    public AttributeUseComponent() {}
    
    public boolean isRequired() {
        return _required;
    }
    
    public void setRequired(boolean b) {
        _required = b;
    }
    
    public AttributeDeclarationComponent getAttributeDeclaration() {
        return _attributeDeclaration;
    }
    
    public void setAttributeDeclaration(AttributeDeclarationComponent c) {
        _attributeDeclaration = c;
    }
    
    public void setValue(String s) {
        _value = s;
    }
    
    public void setValueKind(Symbol s) {
        _valueKind = s;
    }
    
    public Symbol getValueKind() {
        return _valueKind;
    }
    
    public AnnotationComponent getAnnotation() {
        return _annotation;
    }
    
    public void setAnnotation(AnnotationComponent c) {
        _annotation = c;
    }
    
    public void accept(ComponentVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    
    private boolean _required;
    private AttributeDeclarationComponent _attributeDeclaration;
    private String _value;
    private Symbol _valueKind;
    private AnnotationComponent _annotation;
    
    /*
    NOTE - According to the XSD spec, an annotation doesn't belong here,
           yet the WSDL spec relies on being able to specify non-schema
           attributes in cases such as this:
     
      <s:complexType name="ArrayOfItemsItem">
        <s:complexContent mixed="false">
          <s:restriction base="soapenc:Array">
            <s:attribute n1:arrayType="s0:ItemsItem[]"
                ref="soapenc:arrayType"
                xmlns:n1="http://schemas.xmlsoap.org/wsdl/" />
          </s:restriction>
        </s:complexContent>
      </s:complexType>
     
           In this case, given that there is a "ref" attribute, the attribute
           declaration schema component should really be the corresponding
           top-level component, but then adding an annotation with the
           "n1:arrayType" attribute to it would be visible outside this
           complexType!
     */
}
