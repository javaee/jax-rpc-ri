/*
 * $Id: SOAPTypeVisitor.java,v 1.1 2006-04-12 20:34:42 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
