/*
 * $Id: ComponentVisitor.java,v 1.2 2006-04-13 01:31:38 ofung Exp $
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
