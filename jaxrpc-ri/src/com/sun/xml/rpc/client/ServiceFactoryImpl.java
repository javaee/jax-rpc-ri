/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2018 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.sun.xml.rpc.client;

import java.util.Properties;

import javax.xml.namespace.QName;
import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceException;
import javax.xml.rpc.ServiceFactory;

import com.sun.xml.rpc.client.dii.ConfiguredService;
import com.sun.xml.rpc.processor.modeler.ModelerException;

/**
 * <p> A concrete factory for Service objects. </p>
 *
 * @author JAX-RPC Development Team
 */
public class ServiceFactoryImpl extends ServiceFactory {

    public ServiceFactoryImpl() {
    }
   
    public Service createService(java.net.URL wsdlDocumentLocation, QName name)
        throws ServiceException {
        if (wsdlDocumentLocation == null) {
            throw new IllegalArgumentException("wsdlDocumentLocation must not be null");
        }

        //for a dii and dynamic proxy client using wsdl, the wsdl will
        //be examined and a configured service created
        try {
            ConfiguredService service =
                new ConfiguredService(name, wsdlDocumentLocation);
            if (service.getServiceException() != null)
                throw service.getServiceException();
            return service;
        } catch (ModelerException ex) {
            throw new ServiceException(ex);
        }
    }

    public Service createService(QName name) throws ServiceException {
    	//only qname is known, a BasicService is returned
        return new BasicService(name);
    }

    public Service createService(Class serviceInterface, QName name)
        throws ServiceException {
        //create a service using the service qname and an interface
        if (!Service.class.isAssignableFrom(serviceInterface)) {
            throw new ServiceExceptionImpl(
                "service.interface.required",
                serviceInterface.getName());
        }
        //here the service implementation class is given a name and the
        //service is created using the implementation class
        String serviceImplementationName = serviceInterface.getName() + "_Impl";
        Service service = createService(serviceImplementationName);
        if (service.getServiceName().equals(name)) {
            return service;
        } else {
            throw new ServiceExceptionImpl(
                "service.implementation.not.found",
                serviceInterface.getName());
        }
    }

    private Service createService(String serviceImplementationName)
        throws ServiceException {
        if (serviceImplementationName == null) {
            throw new IllegalArgumentException();
        }
        //load the service implementation class using classloader.loadClass()
        try {
            Class serviceImplementationClass =
                Thread.currentThread().getContextClassLoader().loadClass(
                    serviceImplementationName);
            //make sure it is a BasicService
            if (!BasicService
                .class
                .isAssignableFrom(serviceImplementationClass)) {
                throw new ServiceExceptionImpl(
                    "service.implementation.not.found",
                    serviceImplementationName);
            }
            //create a new instance of the service class
            //and return
            try {            	
                Service service =
                    (Service) serviceImplementationClass.newInstance();
                if (service.getServiceName() != null) {
                    return service;
                } else {
                    throw new ServiceExceptionImpl(
                        "service.implementation.not.found",
                        serviceImplementationName);
                }
            } catch (InstantiationException e) {
                throw new ServiceExceptionImpl(
                    "service.implementation.cannot.create",
                    serviceImplementationClass.getName());
            } catch (IllegalAccessException e) {
                throw new ServiceExceptionImpl(
                    "service.implementation.cannot.create",
                    serviceImplementationClass.getName());
            }
        } catch (ClassNotFoundException e) {
            throw new ServiceExceptionImpl(
                "service.implementation.not.found",
                serviceImplementationName);
        }
    }

    public Service loadService(java.lang.Class serviceInterface)
        throws ServiceException {
        if (serviceInterface == null) {
            throw new IllegalArgumentException();
        }

        //load a service given the service interface
        if (!Service.class.isAssignableFrom(serviceInterface)) {
            throw new ServiceExceptionImpl(
                "service.interface.required",
                serviceInterface.getName());
        }
        // assign the service implementation class name
        String serviceImplementationName = serviceInterface.getName() + "_Impl";
        //create the service
        Service service = createService(serviceImplementationName);

        return (service);
    }

    public Service loadService(
        java.net.URL wsdlDocumentLocation,
        Class serviceInterface,
        Properties properties)
        throws ServiceException {
        	
        //check for null arguments
        if (wsdlDocumentLocation == null) {
            throw new IllegalArgumentException("wsdlDocumentLocation must not be null");
        }

        if (serviceInterface == null) {
            throw new IllegalArgumentException();
        }

        //check to make sure this is a service
        if (!Service.class.isAssignableFrom(serviceInterface)) {
            throw new ServiceExceptionImpl(
                "service.interface.required",
                serviceInterface.getName());
        }
        //make the service implementation class name
        String serviceImplementationName = serviceInterface.getName() + "_Impl";
        //create the service
        Service service = createService(serviceImplementationName);

        return (service);
    }

    public Service loadService(
        java.net.URL wsdlDocumentLocation,
        QName ServiceName,
        Properties properties)
        throws ServiceException {
        	
        //check for null arguments
        if (wsdlDocumentLocation == null) {
            throw new IllegalArgumentException("wsdlDocumentLocation must not be null");
        }
        
        //get the service implementation class name
        String serviceImplementationName = null;
        if (properties != null) {
            serviceImplementationName =
                properties.getProperty(StubPropertyConstants.SERVICEIMPL_NAME);
        }
        if (serviceImplementationName == null) {
            throw new IllegalArgumentException(
                "Properties should contain the property:"
                    + StubPropertyConstants.SERVICEIMPL_NAME);
        }
        //create the service
        Service service = createService(serviceImplementationName);
        if (service.getServiceName().equals(ServiceName)) {
            return service;
        } else {
            throw new ServiceExceptionImpl(
                "service.implementation.not.found",
                serviceImplementationName);
        }
    }
}
