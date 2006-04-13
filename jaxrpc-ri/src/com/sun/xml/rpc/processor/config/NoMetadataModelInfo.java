/*
 * $Id: NoMetadataModelInfo.java,v 1.2 2006-04-13 01:28:27 ofung Exp $
 */

/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License).  You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the license at
 * https://glassfish.dev.java.net/public/CDDLv1.0.html.
 * See the License for the specific language governing
 * permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at https://glassfish.dev.java.net/public/CDDLv1.0.html.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * you own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
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
