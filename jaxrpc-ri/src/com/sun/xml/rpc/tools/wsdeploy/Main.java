/*
 * $Id: Main.java,v 1.1 2006-04-12 20:34:15 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


package com.sun.xml.rpc.tools.wsdeploy;

/**
 * Main "wsdeploy" program.
 *
 * @author JAX-RPC Development Team
 */
public class Main {
    
    public static void main(String[] args) {
        DeployTool tool = new DeployTool(System.out, "wsdeploy");
        System.exit(tool.run(args) ? 0 : 1);
    }
}
