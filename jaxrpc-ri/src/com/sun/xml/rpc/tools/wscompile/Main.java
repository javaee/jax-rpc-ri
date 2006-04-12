/*
 * $Id: Main.java,v 1.1 2006-04-12 20:33:18 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


package com.sun.xml.rpc.tools.wscompile;

/**
 * Main "wscompile" program.
 *
 * @author JAX-RPC Development Team
 */
public class Main {
    
    public static void main(String[] args) {
        CompileTool tool = new CompileTool(System.out, "wscompile");
        System.exit(tool.run(args) ? 0 : 1);
    }
}
