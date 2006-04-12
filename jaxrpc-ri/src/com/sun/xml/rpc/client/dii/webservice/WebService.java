/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.client.dii.webservice;

/**
 * @author JAX-RPC Development Team
 */
public class WebService {

    private String wsdlLocation;
    private String model;

    public WebService() {
    }

    public WebService(String wsdlLocation, String model) {
        this.wsdlLocation = wsdlLocation;
        this.model = model;
    }

    public String getWsdlLocation() {
        return this.wsdlLocation;
    }

    public void setWsdlLocation(String wsdlLocation) {
        this.wsdlLocation = wsdlLocation;
    }

    public String getModel() {
        return this.model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String toString() {
        return (
            "wsdlLocation = " + this.wsdlLocation + " model = " + model + ".");
    }
}
