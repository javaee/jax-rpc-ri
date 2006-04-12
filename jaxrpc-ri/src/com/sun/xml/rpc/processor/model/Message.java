/*
 * $Id: Message.java,v 1.1 2006-04-12 20:33:08 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author JAX-RPC Development Team
 */
public abstract class Message extends ModelObject {
    
    public void addBodyBlock(Block b) {
        if (_bodyBlocks.containsKey(b.getName())) {
            throw new ModelException("model.uniqueness");
        }
        _bodyBlocks.put(b.getName(), b);
        b.setLocation(Block.BODY);
    }
    
    public Iterator getBodyBlocks() {
        return _bodyBlocks.values().iterator();
    }
    
    public int getBodyBlockCount() {
        return _bodyBlocks.size();
    }
    
    /* serialization */
    public Map getBodyBlocksMap() {
        return _bodyBlocks;
    }
    
    /* serialization */
    public void setBodyBlocksMap(Map m) {
        _bodyBlocks = m;
    }
    
    public boolean isBodyEmpty() {
        return getBodyBlocks().hasNext();
    }
    
    public boolean isBodyEncoded() {
        boolean isEncoded = false;
        for (Iterator iter = getBodyBlocks(); iter.hasNext();) {
            Block bodyBlock = (Block) iter.next();
            if (bodyBlock.getType().isSOAPType()) {
                isEncoded = true;
            }
        }
        return isEncoded;
    }
    
    public void addHeaderBlock(Block b) {
        if (_headerBlocks.containsKey(b.getName())) {
            throw new ModelException("model.uniqueness");
        }
        _headerBlocks.put(b.getName(), b);
        b.setLocation(Block.HEADER);
    }
    
    public Iterator getHeaderBlocks() {
        return _headerBlocks.values().iterator();
    }
    
    public int getHeaderBlockCount() {
        return _headerBlocks.size();
    }
    
    /* serialization */
    public Map getHeaderBlocksMap() {
        return _headerBlocks;
    }
    
    /* serialization */
    public void setHeaderBlocksMap(Map m) {
        _headerBlocks = m;
    }
    
    /** attachment block */
    public void addAttachmentBlock(Block b) {
        if (_attachmentBlocks.containsKey(b.getName())) {
            throw new ModelException("model.uniqueness");
        }
        _attachmentBlocks.put(b.getName(), b);
        b.setLocation(Block.ATTACHMENT);
    }

    public Iterator getAttachmentBlocks() {
        return _attachmentBlocks.values().iterator();
    }

    public int getAttachmentBlockCount () {
        return _attachmentBlocks.size();
    }

        /* serialization */
    public Map getAttachmentBlocksMap() {
        return _attachmentBlocks;
    }

    /* serialization */
    public void setAttachmentBlocksMap(Map m) {
        _attachmentBlocks = m;
    }

    public void addParameter(Parameter p) {
        if (_parametersByName.containsKey(p.getName())) {
            throw new ModelException("model.uniqueness");
        }
        _parameters.add(p);
        _parametersByName.put(p.getName(), p);
    }
    
    public Parameter getParameterByName(String name) {
        if (_parametersByName.size() != _parameters.size()) {
            initializeParametersByName();
        }
        return (Parameter) _parametersByName.get(name);
    }
    
    public Iterator getParameters() {
        return _parameters.iterator();
    }
    
    /* serialization */
    public List getParametersList() {
        return _parameters;
    }
    
    /* serialization */
    public void setParametersList(List l) {
        _parameters = l;
    }
    
    private void initializeParametersByName() {
        _parametersByName = new HashMap();
        if (_parameters != null) {
            for (Iterator iter = _parameters.iterator(); iter.hasNext();) {
                Parameter param = (Parameter) iter.next();
                if (param.getName() != null &&
                    _parametersByName.containsKey(param.getName())) {
                        
                    throw new ModelException("model.uniqueness");
                }
                _parametersByName.put(param.getName(), param);
            }
        }
    }
        
    private Map _attachmentBlocks = new HashMap();
    private Map _bodyBlocks = new HashMap();
    private Map _headerBlocks = new HashMap();
    private List _parameters = new ArrayList();
    private Map _parametersByName = new HashMap();    
}
