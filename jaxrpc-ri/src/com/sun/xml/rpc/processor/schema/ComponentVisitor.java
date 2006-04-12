/*
 * $Id: ComponentVisitor.java,v 1.1 2006-04-12 20:35:06 kohlert Exp $
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
public interface ComponentVisitor {
    public void visit(AnnotationComponent component) throws Exception;
    public void visit(AttributeDeclarationComponent component) throws Exception;
    public void visit(AttributeGroupDefinitionComponent component)
        throws Exception;
    public void visit(AttributeUseComponent component) throws Exception;
    public void visit(ComplexTypeDefinitionComponent component)
        throws Exception;
    public void visit(ElementDeclarationComponent component) throws Exception;
    public void visit(IdentityConstraintDefinitionComponent component)
        throws Exception;
    public void visit(ModelGroupComponent component) throws Exception;
    public void visit(ModelGroupDefinitionComponent component) throws Exception;
    public void visit(NotationDeclarationComponent component) throws Exception;
    public void visit(ParticleComponent component) throws Exception;
    public void visit(SimpleTypeDefinitionComponent component) throws Exception;
    public void visit(WildcardComponent component) throws Exception;
}
