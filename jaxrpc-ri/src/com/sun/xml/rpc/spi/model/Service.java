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
package com.sun.xml.rpc.spi.model;

import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

/**
 * This class is implemented by 
 * com.sun.xml.rpc.processor.model.Service
 */
public interface Service extends ModelObject {
    public Iterator getPorts();
    public QName getName();
    public List getPortsList();

    /**
     * TODO: better way to derive the generated service * implementation class
     * PE uses service.getJavaInterface() + _Impl  Should we provide a method
     * for it?
     */
    public JavaInterface getJavaIntf();
}
