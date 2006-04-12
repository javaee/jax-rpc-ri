/*
 * $Id: LiteralTypeVisitor.java,v 1.1 2006-04-12 20:32:46 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.model.literal;

/**
 *
 * @author JAX-RPC Development Team
 */
public interface LiteralTypeVisitor {
    
    public void visit(LiteralSimpleType type) throws Exception;
    public void visit(LiteralSequenceType type) throws Exception;
    public void visit(LiteralArrayType type) throws Exception;
    public void visit(LiteralAllType type) throws Exception;
    public void visit(LiteralFragmentType type) throws Exception;
    public void visit(LiteralEnumerationType type) throws Exception;
    
    //xsd:list
    public void visit(LiteralListType type) throws Exception;
    public void visit(LiteralIDType type) throws Exception;
    public void visit(LiteralArrayWrapperType type) throws Exception;
    public void visit(LiteralAttachmentType type) throws Exception;
}
