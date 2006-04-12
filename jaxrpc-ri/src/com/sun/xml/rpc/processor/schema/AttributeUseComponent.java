/*
 * $Id: AttributeUseComponent.java,v 1.1 2006-04-12 20:35:06 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
