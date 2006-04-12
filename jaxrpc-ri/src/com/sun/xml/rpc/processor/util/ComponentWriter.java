/*
 * $Id: ComponentWriter.java,v 1.1 2006-04-12 20:34:58 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.util;

import java.io.IOException;
import java.util.Iterator;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.schema.AnnotationComponent;
import com.sun.xml.rpc.processor.schema.AttributeDeclarationComponent;
import com.sun.xml.rpc.processor.schema.AttributeGroupDefinitionComponent;
import com.sun.xml.rpc.processor.schema.AttributeUseComponent;
import com.sun.xml.rpc.processor.schema.ComplexTypeDefinitionComponent;
import com.sun.xml.rpc.processor.schema.ComponentVisitor;
import com.sun.xml.rpc.processor.schema.ElementDeclarationComponent;
import com.sun.xml.rpc.processor.schema.IdentityConstraintDefinitionComponent;
import com.sun.xml.rpc.processor.schema.InternalSchemaConstants;
import com.sun.xml.rpc.processor.schema.ModelGroupComponent;
import com.sun.xml.rpc.processor.schema.ModelGroupDefinitionComponent;
import com.sun.xml.rpc.processor.schema.NotationDeclarationComponent;
import com.sun.xml.rpc.processor.schema.ParticleComponent;
import com.sun.xml.rpc.processor.schema.SimpleTypeDefinitionComponent;
import com.sun.xml.rpc.processor.schema.WildcardComponent;
import com.sun.xml.rpc.encoding.soap.SOAPConstants;
import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;

/**
 *
 * @author JAX-RPC Development Team
 */
public class ComponentWriter implements ComponentVisitor {
    
    public ComponentWriter(IndentingWriter w) {
        _writer = w;
    }
    
    public void visit(AnnotationComponent component) throws Exception {
    }
    
    public void visit(AttributeDeclarationComponent component)
        throws Exception {
            
        _writer.p("ATTRIBUTE ");
        writeName(component.getName());
        _writer.pln();
        _writer.pI();
        
        if (component.getScope() == null) {
            _writer.pln("SCOPE global");
        }
        if (component.getTypeDefinition() != null) {
            _writer.pln("TYPE");
            _writer.pI();
            component.getTypeDefinition().accept(this);
            _writer.pO();
        }
        
        _writer.pO();
    }
    
    public void visit(AttributeGroupDefinitionComponent component)
        throws Exception {
    }
    
    public void visit(AttributeUseComponent component) throws Exception {
        _writer.p("ATTRIBUTE USE ");
        _writer.pln(component.isRequired() ? "required" : "optional");
        _writer.pI();
        component.getAttributeDeclaration().accept(this);
        _writer.pO();
    }
    
    public void visit(ComplexTypeDefinitionComponent component)
        throws Exception {
            
        _writer.p("COMPLEX-TYPE ");
        writeName(component.getName());
        _writer.pln();
        
        if (component.getName() != null &&
            component.getName().equals(
                InternalSchemaConstants.QNAME_TYPE_URTYPE)) {
                    
            return;
        }
        
        _writer.pI();
        if (component.getBaseTypeDefinition() != null) {
            _writer.pln("BASE-TYPE");
            _writer.pI();
            component.getBaseTypeDefinition().accept(this);
            _writer.pO();
        }
        for (Iterator iter = component.attributeUses(); iter.hasNext();) {
            ((AttributeUseComponent) iter.next()).accept(this);
        }
        switch (component.getContentTag()) {
            case ComplexTypeDefinitionComponent.CONTENT_EMPTY:
                _writer.pln("EMPTY");
                break;
            case ComplexTypeDefinitionComponent.CONTENT_SIMPLE:
                _writer.pln("SIMPLE");
                component.getSimpleTypeContent().accept(this);
                break;
            case ComplexTypeDefinitionComponent.CONTENT_MIXED:
                _writer.pln("MIXED");
                component.getParticleContent().accept(this);
                break;
            case ComplexTypeDefinitionComponent.CONTENT_ELEMENT_ONLY:
                _writer.pln("ELEMENT-ONLY");
                component.getParticleContent().accept(this);
                break;
            default:
                
                // no-op (should not happen)
        }
        _writer.pO();
    }
    
    public void visit(ElementDeclarationComponent component) throws Exception {
        _writer.p("ELEMENT ");
        writeName(component.getName());
        _writer.pln();
        _writer.pI();
        if (component.getScope() == null) {
            _writer.pln("SCOPE global");
        }
        if (component.getTypeDefinition() != null) {
            component.getTypeDefinition().accept(this);
        }
        _writer.pO();
    }
    
    public void visit(IdentityConstraintDefinitionComponent component)
        throws Exception {
    }
    
    public void visit(ModelGroupComponent component) throws Exception {
        _writer.p("GROUP ");
        _writer.p(component.getCompositor().getName());
        _writer.pln();
        _writer.pI();
        for (Iterator iter = component.particles(); iter.hasNext();) {
            ParticleComponent particle = (ParticleComponent) iter.next();
            particle.accept(this);
        }
        _writer.pO();
    }
    
    public void visit(ModelGroupDefinitionComponent component)
        throws Exception {
    }
    
    public void visit(NotationDeclarationComponent component) throws Exception {
    }
    
    public void visit(ParticleComponent component) throws Exception {
        _writer.p("PARTICLE (");
        _writer.p(Integer.toString(component.getMinOccurs()));
        _writer.p(", ");
        if (component.isMaxOccursUnbounded()) {
            _writer.p("UNBOUNDED)");
        } else {
            _writer.p(Integer.toString(component.getMaxOccurs()));
            _writer.p(")");
        }
        _writer.pln();
        _writer.pI();
        if (component.getModelGroupTerm() != null) {
            component.getModelGroupTerm().accept(this);
        } else if (component.getElementTerm() != null) {
            component.getElementTerm().accept(this);
        }
        _writer.pO();
    }
    
    public void visit(SimpleTypeDefinitionComponent component)
        throws Exception {
            
        _writer.p("SIMPLE-TYPE ");
        writeName(component.getName());
        _writer.pln();
        
        if (component.getName() != null &&
            component.getName().equals(
                InternalSchemaConstants.QNAME_TYPE_SIMPLE_URTYPE)) {
                    
            return;
        }
        
        _writer.pI();
        _writer.pO();
    }
    
    public void visit(WildcardComponent component) throws Exception {
    }
    
    private void writeName(QName name) throws IOException {
        if (name != null) {
            String nsURI = name.getNamespaceURI();
            if (nsURI.equals(SchemaConstants.NS_XSD)) {
                _writer.p("xsd:");
            } else if (nsURI.equals(SOAPConstants.NS_SOAP_ENCODING)) {
                _writer.p("soap-enc:");
            }
            _writer.p(name.getLocalPart());
        }
    }
    
    private IndentingWriter _writer;
}
