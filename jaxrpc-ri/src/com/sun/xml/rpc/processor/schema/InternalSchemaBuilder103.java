/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
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

/*
 * $Id: InternalSchemaBuilder103.java,v 1.3 2007-07-13 23:36:19 ofung Exp $
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

import java.util.Iterator;
import java.util.Properties;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;
import com.sun.xml.rpc.wsdl.document.schema.SchemaElement;
import com.sun.xml.rpc.wsdl.framework.AbstractDocument;
import com.sun.xml.rpc.wsdl.parser.Constants;

/**
 * @author JAX-RPC Development Team
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class InternalSchemaBuilder103 extends InternalSchemaBuilderBase {
    
    /**
     * @param document
     * @param options
     */
    public InternalSchemaBuilder103(AbstractDocument document,
        Properties options) {
            
        super(document, options);
        
        // TODO Auto-generated constructor stub
    }
    
    /* (non-Javadoc)
     * @see com.sun.xml.rpc.processor.schema.InternalSchemaBuilderBase#processElementParticle(com.sun.xml.rpc.wsdl.document.schema.SchemaElement, com.sun.xml.rpc.processor.schema.ParticleComponent, com.sun.xml.rpc.processor.schema.ComplexTypeDefinitionComponent, com.sun.xml.rpc.processor.schema.InternalSchema)
     */
    protected void processElementParticle(
        SchemaElement element,
        ParticleComponent component,
        ComplexTypeDefinitionComponent scope,
        InternalSchema schema) {
            
        // SPEC - 3.9
        component.setTermTag(ParticleComponent.TERM_ELEMENT);
        
        // property: term
        ElementDeclarationComponent term =
            new ElementDeclarationComponent();

        internalBuildElementDeclaration(term, element, schema);

        String refAttr = element.getValueOfAttributeOrNull(Constants.ATTR_REF);
        if (refAttr != null) {
            
            // cannot deal with element refs yet
            failUnimplemented("F004");
        }
        
        // property: name, target namespace
        String nameAttr =
            element.getValueOfMandatoryAttribute(Constants.ATTR_NAME);
        String formAttr =
            element.getValueOfAttributeOrNull(Constants.ATTR_FORM);
        if (formAttr == null) {
            formAttr = element.getRoot().getValueOfAttributeOrNull(
                Constants.ATTR_ELEMENT_FORM_DEFAULT);
            if (formAttr == null) {
                formAttr = "";
            }
        }
        if (formAttr.equals(Constants.ATTRVALUE_QUALIFIED)) {
            term.setName(new QName(
                element.getSchema().getTargetNamespaceURI(), nameAttr));
        } else {
            term.setName(new QName(nameAttr));
        }
        
        //     property: scope
        term.setScope(scope);
        
        component.setTermTag(ParticleComponent.TERM_ELEMENT);
        component.setElementTerm(term);
    }
    
    /* (non-Javadoc)
     * @see com.sun.xml.rpc.processor.schema.InternalSchemaBuilderBase#buildRestrictionSimpleTypeDefinition(com.sun.xml.rpc.processor.schema.SimpleTypeDefinitionComponent, com.sun.xml.rpc.wsdl.document.schema.SchemaElement, com.sun.xml.rpc.processor.schema.InternalSchema)
     */
    protected void buildRestrictionSimpleTypeDefinition(
        SimpleTypeDefinitionComponent component,
        SchemaElement element,
        InternalSchema schema) {
            
        // property: base type definition
        String baseAttr =
            element.getValueOfAttributeOrNull(Constants.ATTR_BASE);
        if (baseAttr != null) {
            TypeDefinitionComponent base =
                schema.findTypeDefinition(element.asQName(baseAttr));
            if (base.isSimple()) {
                component.setBaseTypeDefinition(
                    (SimpleTypeDefinitionComponent) base);
            } else {
                failValidation("validation.notSimpleType",
                    base.getName().getLocalPart());
            }
        } else {
            failUnimplemented("F012");
        }
        
        // property: variety
        component.setVarietyTag(
            component.getBaseTypeDefinition().getVarietyTag());
        
        // property: primitive type definition
        component.setPrimitiveTypeDefinition(
            component.getBaseTypeDefinition().getPrimitiveTypeDefinition());
        
        // property: final
        String finalAttr =
            element.getValueOfAttributeOrNull(Constants.ATTR_FINAL);
        if (finalAttr == null) {
            finalAttr = element.getRoot().getValueOfAttributeOrNull(
                Constants.ATTR_FINAL_DEFAULT);
            if (finalAttr == null) {
                finalAttr = "";
            }
        }
        if (finalAttr.equals("")) {
            
            // no disallowed substitutions
            component.setFinal(_setEmpty);
        } else if (finalAttr.equals(Constants.ATTRVALUE_ALL)) {
            component.setFinal(_setExtResListUnion);
        } else {
            component.setFinal(parseSymbolSet(finalAttr, _setExtResListUnion));
            
            // TODO - implement this
            failUnimplemented("F013");
        }
        
        // right now, we only support the enumeration facet
        boolean gotOne = false;
        EnumerationFacet enumeration = new EnumerationFacet();
        for (Iterator iter = element.children(); iter.hasNext();) {
            SchemaElement child = (SchemaElement) iter.next();
            gotOne = true;
            if (child.getQName().equals(SchemaConstants.QNAME_ENUMERATION)) {
                String valueAttr = child.getValueOfAttributeOrNull(
                    Constants.ATTR_VALUE);
                if (valueAttr == null) {
                    failValidation("validation.missingRequiredAttribute",
                        Constants.ATTR_VALUE, child.getQName().getLocalPart());
                }
                enumeration.addValue(valueAttr);
            } else {
                failUnimplemented("F014");
            }
        }
        
        component.addFacet(enumeration);
    }
    
}
