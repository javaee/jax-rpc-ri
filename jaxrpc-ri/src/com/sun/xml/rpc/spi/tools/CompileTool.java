/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.xml.rpc.spi.tools;

public interface CompileTool {

    /**
     * Which one should we use? run() or run(String args[]).
     * We have to define setters for each arguments if we were to
     * use run();
     */
    public void run() throws Exception;

    /**
     * run(String[] args) for now.
     */
    public boolean run(String[] args);

    public void setDelegate(CompileToolDelegate delegate);

    public Processor getProcessor();
    public ProcessorEnvironment getEnvironment();
}
