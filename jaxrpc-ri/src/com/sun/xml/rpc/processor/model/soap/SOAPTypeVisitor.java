/*
 * $Id: SOAPTypeVisitor.java,v 1.2 2006-04-13 01:30:08 ofung Exp $
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

package com.sun.xml.rpc.processor.model.soap;

/**
 *
 * @author JAX-RPC Development Team
 */
public interface SOAPTypeVisitor {
    
    public void visit(SOAPArrayType type) throws Exception;
    public void visit(SOAPCustomType type) throws Exception;
    public void visit(SOAPEnumerationType type) throws Exception;
    public void visit(SOAPSimpleType type) throws Exception;
    public void visit(SOAPAnyType type) throws Exception;
    public void visit(SOAPOrderedStructureType type) throws Exception;
    public void visit(SOAPUnorderedStructureType type) throws Exception;
    public void visit(RPCRequestOrderedStructureType type) throws Exception;
    public void visit(RPCRequestUnorderedStructureType type) throws Exception;
    public void visit(RPCResponseStructureType type) throws Exception;
    public void visit(SOAPListType type) throws Exception;
}
