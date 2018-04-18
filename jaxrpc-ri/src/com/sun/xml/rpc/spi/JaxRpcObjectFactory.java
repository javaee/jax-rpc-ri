/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2018 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
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

package com.sun.xml.rpc.spi;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.URL;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import com.sun.xml.rpc.spi.runtime.ClientTransportFactory;
import com.sun.xml.rpc.spi.runtime.Implementor;
import com.sun.xml.rpc.spi.runtime.ImplementorCache;
import com.sun.xml.rpc.spi.runtime.RuntimeEndpointInfo;
import com.sun.xml.rpc.spi.runtime.SOAPMessageContext;
import com.sun.xml.rpc.spi.runtime.ServletDelegate;
import com.sun.xml.rpc.spi.runtime.Tie;
import com.sun.xml.rpc.spi.tools.CompileTool;
import com.sun.xml.rpc.spi.tools.Configuration;
import com.sun.xml.rpc.spi.tools.HandlerInfo;
import com.sun.xml.rpc.spi.tools.J2EEModelInfo;
import com.sun.xml.rpc.spi.tools.ModelFileModelInfo;
import com.sun.xml.rpc.spi.tools.Names;
import com.sun.xml.rpc.spi.tools.NamespaceMappingInfo;
import com.sun.xml.rpc.spi.tools.NamespaceMappingRegistryInfo;
import com.sun.xml.rpc.spi.tools.NoMetadataModelInfo;
import com.sun.xml.rpc.spi.tools.ProcessorEnvironment;
import com.sun.xml.rpc.spi.tools.WSDLParser;
import com.sun.xml.rpc.spi.tools.WSDLUtil;
import com.sun.xml.rpc.spi.tools.XMLModelFileFilter;

/**
 * Singleton abstract factory used to produce jaxrpc related objects.
 */
public abstract class JaxRpcObjectFactory {

    private static JaxRpcObjectFactory factory;

    private static String DEFAULT_JAXRPC_OBJECT_FACTORY =
        "com.sun.xml.rpc.util.JaxRpcObjectFactoryImpl";
    private static String JAXRPC_FACTORY_PROPERTY = "javax.xml.rpc.spi.JaxRpcObjectFactory";

    public JaxRpcObjectFactory () {}
    /**
     * Creates an instance of the specified class using the specified 
     * <code>ClassLoader</code> object.
     *
     * @exception SOAPException if the given class could not be found
     *            or could not be instantiated
     */
    private static Object newInstance(String className,
                                      ClassLoader classLoader)
        
    {
        try {
            Class spiClass;
            if (classLoader == null) {
                spiClass = Class.forName(className);
            } else {
                spiClass = classLoader.loadClass(className);
            }
            return spiClass.newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
    /**
     * Obtain an instance of a factory.
     *
     * <p> The implementation class to be used can be overridden by setting a
     * system property (name TBD). </p>
     *
     */
    public static JaxRpcObjectFactory newInstance() {
        /*
        if (factory == null) {
            //XXX FIXME Make it configurable by property
            try {
                Class klass = Class.forName(DEFAULT_JAXRPC_OBJECT_FACTORY);
                factory = (JaxRpcObjectFactory) klass.newInstance();
            } catch (Exception e) {
                //XXX FIXME  i18n.  Better Handling of the Error
                e.printStackTrace();
            }
        }
        return factory;
         */ 
        ClassLoader classLoader;
            classLoader = Thread.currentThread().getContextClassLoader();

        // Use the system property first
        try {
            String systemProp =
                System.getProperty(JAXRPC_FACTORY_PROPERTY);
            if( systemProp!=null) {
                return (JaxRpcObjectFactory) newInstance(systemProp, classLoader);
            }
        } catch (Exception e) {
        }


        String serviceId = "META-INF/services/" + JAXRPC_FACTORY_PROPERTY;
        // try to find services in CLASSPATH
        try {
            InputStream is=null;
            if (classLoader == null) {
                is=ClassLoader.getSystemResourceAsStream(serviceId);
            } else {
                is=classLoader.getResourceAsStream(serviceId);
            }
        
            if( is!=null ) {
                BufferedReader rd =
                    new BufferedReader(new InputStreamReader(is, "UTF-8"));
        
                String factoryClassName = rd.readLine();
                rd.close();

                if (factoryClassName != null &&
                    ! "".equals(factoryClassName)) {
                    return (JaxRpcObjectFactory) newInstance(factoryClassName, classLoader);
                }
            }
        } catch( Exception ex ) {
        }

        return (JaxRpcObjectFactory) newInstance(DEFAULT_JAXRPC_OBJECT_FACTORY, classLoader);
    }
    public abstract ModelFileModelInfo createModelFileModelInfo();

    public abstract NoMetadataModelInfo createNoMetadataModelInfo();

    public abstract J2EEModelInfo createJ2EEModelInfo(URL mapping)
        throws Exception;

    public abstract HandlerInfo createHandlerInfo();

    public abstract NamespaceMappingRegistryInfo createNamespaceMappingRegistryInfo();

    public abstract NamespaceMappingInfo createNamespaceMappingInfo(
        String namespaceURI,
        String javaPackageName);

    public abstract Configuration createConfiguration(ProcessorEnvironment env);

    public abstract SOAPMessageContext createSOAPMessageContext();

    public abstract Implementor createImplementor(
        ServletContext servletContext,
        Tie tie);

    public abstract RuntimeEndpointInfo createRuntimeEndpointInfo();

    /**
     * @param type The type of ClientTransportFactory
     * @see com.sun.xml.rpc.spi.runtime.ClientTransportFactoryTypes
     */
    public abstract ClientTransportFactory createClientTransportFactory(
        int type,
        OutputStream logStream);

    public abstract CompileTool createCompileTool(
        OutputStream out,
        String program);

    public abstract XMLModelFileFilter createXMLModelFileFilter();

    public abstract ImplementorCache createImplementorCache(ServletConfig config);

    public abstract ServletDelegate createServletDelegate();

    /**
     * Names provides utility methods used by other wscompile classes
     * for dealing with identifiers.  This is not the most obvious/intuitive
     * method name.  Any suggestion is welcome.
     */
    public abstract Names createNames();

    public abstract WSDLUtil createWSDLUtil();

    public abstract WSDLParser createWSDLParser();
}
