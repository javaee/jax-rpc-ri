/*
 * $Id: GeneratedFileInfo.java,v 1.2 2006-04-13 01:31:55 ofung Exp $
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

package com.sun.xml.rpc.processor.util;

import java.io.File;

/**
 * A container to hold info on the files that get
 * generated.
 *
 * @author JAX-RPC Development Team
 */
public class GeneratedFileInfo
    implements com.sun.xml.rpc.spi.tools.GeneratedFileInfo {
    
    /**
     * local variables
     */
    private File file = null;
    private String type = null;
    
    /* constructor */
    public GeneratedFileInfo() {}
    
    /**
     * Adds the file object to the container
     *
     * @param instance of the file to be added
     * @return void
     */
    public void setFile( File file ) {
        this.file = file;
    }
    
    /**
     * Adds the type of file it is the container
     *
     * @param Type string which specifices the type
     * @return void
     */
    public void setType( String type ) {
        this.type = type;
    }
    
    /**
     * Gets the file that got added
     *
     * @param none
     * @return File instance
     */
    public File getFile() {
        return( file );
    }
    
    /**
     * Get the file type that got added
     *
     * @param none
     * @return File type of datatype String
     */
    public String getType() {
        return ( type );
    }
}
