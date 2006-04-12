/*
 * $Id: LiteralAttachmentType.java,v 1.1 2006-04-12 20:32:44 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.xml.rpc.processor.model.literal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.model.java.JavaType;

/**
 * @author Vivek Pandey
 *
 */
public class LiteralAttachmentType extends LiteralType {

    public LiteralAttachmentType() {
    }

    public LiteralAttachmentType(QName name, JavaType javaType) {
        super(name, javaType);
    }
    
    /* (non-Javadoc)
     * @see com.sun.xml.rpc.processor.model.literal.LiteralType#accept(com.sun.xml.rpc.processor.model.literal.LiteralTypeVisitor)
     */
    public void accept(LiteralTypeVisitor visitor) throws Exception {
        visitor.visit(this);

    }
    
    public void setMIMEType(String mimeType) {
        this.mimeType = mimeType;
    }
    
    public String getMIMEType() {
        return mimeType;
    }
    
    // a part in mime:content can have alternative mimeTypes
    public void addAlternateMIMEType(String mimeType) {
        if(alternateMIMETypes == null){
            alternateMIMETypes = new ArrayList();
        }
        alternateMIMETypes.add(mimeType);
    }   
    
    public void addAlternateMIMEType(Iterator mimeTypes) {
        if(alternateMIMETypes == null){
            alternateMIMETypes = new ArrayList();
        }
        while(mimeTypes.hasNext()) {
            alternateMIMETypes.add((String)mimeTypes.next());
        }
    }
    
    public List getAlternateMIMETypes() {
        if(alternateMIMETypes == null) {
            List mimeTypes = new ArrayList();
            mimeTypes.add(mimeType);
            return mimeTypes;
        }
        return alternateMIMETypes;
    }
    
    public void setAlternateMIMETypes(List l) {        
        alternateMIMETypes = l;
    }
    
    public void setSwaRef(boolean isSwaRef){
        this.isSwaRef = isSwaRef;  
    }
    
    public boolean isSwaRef() {
        return isSwaRef;
    }
    
    public String getContentID() {
        return contentId;
    }
    
    public void setContentID(String id) {
        contentId = id;
    }
    
    private String mimeType;
    private List alternateMIMETypes;
    private String contentId;
    private boolean isSwaRef = false;
}
