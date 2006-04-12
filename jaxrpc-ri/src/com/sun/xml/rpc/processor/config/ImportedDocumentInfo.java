/*
 * $Id: ImportedDocumentInfo.java,v 1.1 2006-04-12 20:34:50 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package com.sun.xml.rpc.processor.config;

/**
 *
 * @author JAX-RPC Development Team
 */
public class ImportedDocumentInfo {

    public static final int UNKNOWN_DOCUMENT = 0;
    public static final int SCHEMA_DOCUMENT = 1;
    public static final int WSDL_DOCUMENT = 2;

    public ImportedDocumentInfo() {}
    
    public ImportedDocumentInfo(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
    
    public void setType(int i) {
        type = i;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String s) {
        namespace = s;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String s) {
        location = s;
    }

    private int type;
    private String namespace;
    private String location;
}
