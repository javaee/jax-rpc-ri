/*
 * $Id: Response.java,v 1.1 2006-04-12 20:33:07 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author JAX-RPC Development Team
 */
public class Response extends Message {

    public Response() {}

    public void addFaultBlock(Block b) {
        if (_faultBlocks.containsKey(b.getName())) {
            throw new ModelException("model.uniqueness");
        }
        _faultBlocks.put(b.getName(), b);
    }

    public Iterator getFaultBlocks() {
        return _faultBlocks.values().iterator();
    }

    public int getFaultBlockCount () {
        return _faultBlocks.size();
    }

    /* serialization */
    public Map getFaultBlocksMap() {
        return _faultBlocks;
    }
    
    public void setFaultBlocksMap(Map m) {
        _faultBlocks = m;
    }
    
    public void accept(ModelVisitor visitor) throws Exception {
        visitor.visit(this);
    }

    private Map _faultBlocks = new HashMap();
}
