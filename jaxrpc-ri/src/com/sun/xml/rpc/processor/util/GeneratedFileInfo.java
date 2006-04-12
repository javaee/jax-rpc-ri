/*
 * $Id: GeneratedFileInfo.java,v 1.1 2006-04-12 20:34:59 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
