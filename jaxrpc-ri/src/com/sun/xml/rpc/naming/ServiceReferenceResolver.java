/*
 * $Id: ServiceReferenceResolver.java,v 1.2 2006-04-13 01:28:16 ofung Exp $
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

/**
*
* @author JAX-RPC Development Team
*/
package com.sun.xml.rpc.naming;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import javax.naming.spi.ObjectFactory;
import javax.xml.rpc.Service;

public class ServiceReferenceResolver implements ObjectFactory {
    protected static final Map registeredServices =
        Collections.synchronizedMap(new HashMap());
    
    public Object getObjectInstance(Object obj, Name name,
        Context nameCtx, Hashtable environment) throws Exception {
        
        if (obj instanceof StringRefAddr) {
            StringRefAddr ref = (StringRefAddr) obj;
            if (ref.getType() == "ServiceName") {
                return registeredServices.get(ref.getContent());
            } else if (ref.getType() == "ServiceClassName") {
                Object serviceKey = ref.getContent();
                Object service = registeredServices.get(serviceKey);
                if (service == null) {
                    ClassLoader ctxLoader =
                        Thread.currentThread().getContextClassLoader();
                    service = Class.forName((String) ref.getContent(),
                        true, ctxLoader).newInstance();
                    registeredServices.put(serviceKey, service);
                }
                return service;
            }
        }
        return null;
    }
    
    public static String registerService(Service service) {
        String serviceName = getQualifiedServiceNameString(service);
        registeredServices.put(serviceName, service);
        return serviceName;
    }
    
    protected static String getQualifiedServiceNameString(Service service) {
        String serviceName = "";
        URL wsdlLocation = service.getWSDLDocumentLocation();
        if (wsdlLocation != null) {
            serviceName += wsdlLocation.toExternalForm() + ":";
        }
        serviceName += service.getServiceName().toString();
        return serviceName;
    }
    
    public Reference getServiceClassReference(Class serviceClass) {
        return getServiceClassReference(serviceClass.getName());
    }
    
    public Reference getServiceClassReference(String serviceClassName) {
        Reference reference = new Reference(serviceClassName,
            "com.sun.xml.rpc.naming.ServiceReferenceResolver", null);
        reference.add(new StringRefAddr("ServiceClassName", serviceClassName));
        return reference;
    }
}
