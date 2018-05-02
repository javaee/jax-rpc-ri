/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2018 Sun Microsystems, Inc. All rights reserved.
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
