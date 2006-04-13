/*
 * $Id: ImportedDocumentInfo.java,v 1.2 2006-04-13 01:28:24 ofung Exp $
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
