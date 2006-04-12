/*
 * $Id: ModelIf.java,v 1.1 2006-04-12 20:33:18 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.xml.rpc.tools.wscompile;

/**
 * @author JAX-RPC Development Team
 *
 */
public interface ModelIf {
    public void updateModel(ModelProperty property);
    public class ModelProperty {
        public String attr;
        public String value;
    }
}
