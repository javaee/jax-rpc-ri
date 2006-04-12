/*
 * $Id: Modeler.java,v 1.1 2006-04-12 20:34:25 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


package com.sun.xml.rpc.processor.modeler;

import com.sun.xml.rpc.processor.model.Model;

/**
 *
 * @author JAX-RPC Development Team
*/
public interface Modeler {
    /**
     * Returns the top model of a Web Service. May throw a
     * ModelException if there is a problem with the model.
     *
     * @return Model - the root Node of the model of the Web Service
     *
     * @exception ModelerException
     */
    public Model buildModel();
}
