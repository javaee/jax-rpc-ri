/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.client.dii.webservice;

import java.util.ArrayList;
import java.util.List;

/**
 * @author JAX-RPC Development Team
 */
public class WebServicesClient {

    private List webServices;

    public WebServicesClient() {
        webServices = new ArrayList();
    }

    public List getWebServices() {
        return webServices;
    }

    public void setWebServices(List webservices) {
        this.webServices = webservices;
    }
}
