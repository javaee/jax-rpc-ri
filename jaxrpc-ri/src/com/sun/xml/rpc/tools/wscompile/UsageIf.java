/*
 * $Id: UsageIf.java,v 1.1 2006-04-12 20:33:18 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package com.sun.xml.rpc.tools.wscompile;

import com.sun.xml.rpc.util.localization.Localizable;

/**
 * @author JAX-RPC Development Team
 *
 */
public interface UsageIf {
    
    /**
     * @return
     */
    public Localizable getOptionsUsage();
    
    /**
     * @return
     */
    public Localizable getFeaturesUsage();
    /**
     * @return
     */
    public Localizable getInternalUsage();
    /**
     * @return
     */
    public Localizable getExamplesUsage();
    
    /**
     * After processing the argument in the array, mark the index as null
     * @param args
     * @return false if there is a problem with the expected arguments
     */
    public boolean parseArguments(String[] args, UsageError err);
    
    public class UsageError {
        public Localizable msg;
    }
}
