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
package com.sun.xml.rpc.spi.runtime;

import javax.xml.namespace.QName;

/**
 * This class is implemented by 
 * com.sun.xml.rpc.server.http.RuntimeEndpointInfo
 */
public interface RuntimeEndpointInfo {
    public void setRemoteInterface(Class klass);
    public void setImplementationClass(Class klass);
    public void setTieClass(Class klass);
    public void setName(String s);
    public void setDeployed(boolean b);
    public void setPortName(QName n);
    public void setServiceName(QName n);
    public void setUrlPattern(String s);
    public Class getTieClass();
    public Class getRemoteInterface();
    public Class getImplementationClass();
}
