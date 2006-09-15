/*
 * $Id: ProcessorEnvironment.java,v 1.2 2006-04-13 01:31:58 ofung Exp $
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

import java.net.URLClassLoader;

import com.sun.xml.rpc.processor.generator.Names;
import com.sun.xml.rpc.util.localization.Localizable;

/**
 *
 * @author JAX-RPC Development Team
 */
public interface ProcessorEnvironment
    extends com.sun.xml.rpc.spi.tools.ProcessorEnvironment {
        
    /*
     * Flags
     */
    int F_VERBOSE		= 1 << 0;
    int F_WARNINGS		= 1 << 1;
    
    /**
     * Set the environment flags
     */
    public void setFlags(int flags);
    
    /**
     * Get the environment flags
     */
    public int getFlags();
    
    /**
     * Get the ClassPath.
     */
    public String getClassPath();
    
    /**
     * Is verbose turned on
     */
    public boolean verbose();
    
    /**
     * Remember a generated file and its type so that it
     * can be removed later, if appropriate.
     */
    public void addGeneratedFile(GeneratedFileInfo file);
    
    
    /**
     * Delete all the generated files made during the execution of this
     * environment (those that have been registered with the "addGeneratedFile"
     * method)
     */
    public void deleteGeneratedFiles();
    
    /**
     * Get a URLClassLoader from using the classpath
     */
    public URLClassLoader getClassLoader();
    
    /**
     * Release resources, if any.
     */
    public void shutdown();
    
    public void error(Localizable msg);
    
    public void warn(Localizable msg);
    
    public void info(Localizable msg);
    
    public void printStackTrace(Throwable t);
    
    public Names getNames();
    
    public int getErrorCount();
    public int getWarningCount();
}