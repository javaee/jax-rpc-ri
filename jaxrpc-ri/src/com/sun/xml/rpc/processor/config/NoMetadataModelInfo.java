/*
 * $Id: NoMetadataModelInfo.java,v 1.1 2006-04-12 20:34:49 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package com.sun.xml.rpc.processor.config;

import java.util.Properties;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.modeler.Modeler;
import com.sun.xml.rpc.processor.modeler.nometadata.NoMetadataModeler;

/**
 *
 * @author JAX-RPC Development Team
 */
public class NoMetadataModelInfo extends ModelInfo
    implements com.sun.xml.rpc.spi.tools.NoMetadataModelInfo {

    public NoMetadataModelInfo() {}

    protected Modeler getModeler(Properties options) {
        return new NoMetadataModeler(this, options);
    }

    public String getLocation() {
        return _location;
    }

    public void setLocation(String s) {
        _location = s;
    }

    public String getServiceInterfaceName() {
	return _serviceInterfaceName;
    }
    
    public void setServiceInterfaceName(String s) {
	_serviceInterfaceName = s;
    }
    
    public String getInterfaceName() {
	return _interfaceName;
    }
    
    public void setInterfaceName(String s) {
	_interfaceName = s;
    }

    public String getServantName() {
	return _servantName;
    }
    
    public void setServantName(String s) {
	_servantName = s;
    }
    
    public QName getServiceName() {
	return _serviceName;
    }
    
    public void setServiceName(QName n) {
	_serviceName = n;
    }
    
    public QName getPortName() {
	return _portName;
    }
    
    public void setPortName(QName n) {
	_portName = n;
    }
    
    private String _location;
    private String _serviceInterfaceName;
    private String _interfaceName;
    private String _servantName;
    private QName _serviceName;
    private QName _portName;
}
