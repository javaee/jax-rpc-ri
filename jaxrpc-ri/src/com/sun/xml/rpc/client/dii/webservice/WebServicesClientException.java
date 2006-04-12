/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.xml.rpc.client.dii.webservice;

import com.sun.xml.rpc.processor.ProcessorException;
import com.sun.xml.rpc.util.localization.Localizable;

/**
 * @author JAX-RPC Development Team
 */
public class WebServicesClientException extends ProcessorException {

    public WebServicesClientException(String key) {
        super(key);
    }

    public WebServicesClientException(String key, String arg) {
        super(key, arg);
    }

    public WebServicesClientException(String key, Object[] args) {
        super(key, args);
    }

    public WebServicesClientException(String key, Localizable arg) {
        super(key, arg);
    }

    public String getResourceBundleName() {
        return "com.sun.xml.rpc.resources.client.dii.webservice";
    }

}
