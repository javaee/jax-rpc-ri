/*
 * $Id: DeploymentException.java,v 1.1 2006-04-12 20:34:14 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.tools.wsdeploy;

import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.localization.Localizable;


/**
 * DeploymentException represents an exception that occurred at deployment time.
 *
 * @see com.sun.xml.rpc.util.exception.JAXRPCExceptionBase
 *
 * @author JAX-RPC Development Team
 */
public class DeploymentException extends JAXRPCExceptionBase {
    
    public DeploymentException(String key) {
        super(key);
    }
    
    public DeploymentException(String key, String arg) {
        super(key, arg);
    }
    
    public DeploymentException(String key, Object[] args) {
        super(key, args);
    }
    
    public DeploymentException(String key, Localizable arg) {
        super(key, arg);
    }
    
    public String getResourceBundleName() {
        return "com.sun.xml.rpc.resources.deployment";
    }
}
