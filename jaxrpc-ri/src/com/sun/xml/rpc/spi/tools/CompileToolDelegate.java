/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.xml.rpc.spi.tools;

/**
 * This delegate is used by the implemenation of 
 * com.sun.xml.rpc.spi.tools.wscompile.CompileTool
 * so that customized implementation could be provided to
 * override jaxrpc specific implementation
 */
public abstract class CompileToolDelegate {

    /**
     * Default constructor.  Do nothing.
     */
    public CompileToolDelegate() {
    }

    /**
     * Assuming the jaxrpc implementation of createConfiguration()
     * will not overwrite if the delegates does return a non-null
     * Configuration object.
     */
    public Configuration createConfiguration() {
        //no op
        return null;
    }

    public void preOnError() {
        //no op
    }

    /**
     * Called right after CompileTool.registerProcessorActions.
     * We probably should also expose registerProcessorAction() in
     * case someone else would like to register more processor actions.
     * But minimum set for now until the need rises.
     */
    public void postRegisterProcessorActions() {
        // no op
    }

    /**
     */
    public void postRun() {
        //no op
    }

    /**
     * Subclass of the CompileToolDelegate is responsible to set
     * its association to a CompileTool implementation that will
     * callback for any customized implementation.  
     * <p>
     * The association between the CompileToolDelegate and CompileTool
     * is bi-directional to ensure that the delegate could also
     * access environment known to CompileTool.
     * @see CompileTool
     */
    public void setCompileTool(CompileTool wscompile) {
        //no op 
    }
}
