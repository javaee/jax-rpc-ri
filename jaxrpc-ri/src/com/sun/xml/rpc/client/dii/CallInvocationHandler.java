/*
 * $Id: CallInvocationHandler.java,v 1.3 2007-07-13 23:35:55 ofung Exp $
 */

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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

package com.sun.xml.rpc.client.dii;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.rpc.Call;
import javax.xml.rpc.Stub;
import javax.xml.rpc.holders.Holder;

import com.sun.xml.rpc.client.StubPropertyConstants;
import com.sun.xml.rpc.soap.streaming.SOAPNamespaceConstants;
import com.sun.xml.rpc.util.Holders;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;

/**
 * @author JAX-RPC Development Team
 */
public class CallInvocationHandler implements InvocationHandler, Stub {
    private static final Set recognizedProperties;

    private Map callMap = new HashMap();
    private Map properties = new HashMap();
    private Class portInterface;

    static {
        Set temp = new HashSet();
        temp.add(USERNAME_PROPERTY);
        temp.add(PASSWORD_PROPERTY);
        temp.add(ENDPOINT_ADDRESS_PROPERTY);
        temp.add(SESSION_MAINTAIN_PROPERTY);
        temp.add(StubPropertyConstants.HTTP_COOKIE_JAR);
        recognizedProperties = Collections.unmodifiableSet(temp);
    }

    //InvocationHandler for the Dynamic Proxy
    CallInvocationHandler(Class portInterface) {
        this.portInterface = portInterface;
    }

    //add the call and store in the call map keyed by Method
    public void addCall(Method key, Call call) {
        callMap.put(key, call);
    }

    public Call getCall(Method key) {
        return (Call) callMap.get(key);
    }

    //does a sanity check and makes sure configured call actually
    //exists in the java methods declaring class
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Exception {
        if (portInterface.equals(method.getDeclaringClass())) {
            Call call = getCall(method);
            if (call == null) {
                String knownMethodNames = "";
                Iterator eachKnownMethod = callMap.keySet().iterator();
                while (eachKnownMethod.hasNext()) {
                    Method knownMethod = (Method) eachKnownMethod.next();
                    knownMethodNames += "\n" + knownMethod.getName();
                }
                throw new DynamicInvocationException("dii.dynamicproxy.method.unrecognized",
                        new Object[]{
                            method != null ? method.getName() : null,
                            knownMethodNames});
            }
            //good it checks out ok do it
            return doCall(call, args);
        } else {
            //this method isn't in the port interface
            //invoke anyway
            return doNonPort(method, args);
        }
    }

    protected Object doCall(Call call, Object[] args) throws RemoteException {
        //if no args just make Object so we don't get NPE
        if (args == null) {
            args = new Object[0];
        }

        //process holders
        Holder[] holders = new Holder[args.length];
        int[] holderLocations = new int[args.length];

        int lastHolderIndex = 0;
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg instanceof Holder) {
                Holder holderArg = (Holder) arg;
                //get the holder arg location
                holderLocations[lastHolderIndex] = i;
                //save the holder argument
                holders[lastHolderIndex] = holderArg;
                //get the value of the holder and
                //set as argument
                args[i] = Holders.getValue(holderArg);

                ++lastHolderIndex;
            }
        }

        //invoke BasicCall.invoke(args)
        Object returnValue = call.invoke(args);

        //we got the return value, let's get other out values
        //and set the appropriate holder with the returned value
        if (lastHolderIndex > 0) {
            List outputValues = call.getOutputValues();
            Iterator eachOutputValue = outputValues.iterator();
            int holderIndex = 0;
            while (eachOutputValue.hasNext()) {
                Object outParameter = eachOutputValue.next();

                int holderLocation = holderLocations[holderIndex];
                Holder holder = holders[holderIndex];
                Holders.setValue(holder, outParameter);
                args[holderLocation] = holder;

                ++holderIndex;
            }
        }
        return returnValue;
    }

    protected Object doNonPort(Method method, Object[] args) {
        //use reflection to invoke this unknown method
        try {
            return method.invoke(this, args);
        } catch (Exception e) {
            throw new DynamicInvocationException("dii.exception.nested",
                    new LocalizableExceptionAdapter(e));
        }
    }

    public void _setProperty(String name, Object value) {
        if (!recognizedProperties.contains(name)) {
            throw new IllegalArgumentException("Call object does not recognize property: " + name);
        }

        properties.put(name, value);

        setPropertyOnCallObjects(name, value);
    }

    public Object _getProperty(String name) {
        return properties.get(name);
    }

    public Iterator _getPropertyNames() {
        return properties.keySet().iterator();
    }

    private void setPropertyOnCallObjects(String propertyName, Object value) {
        for (Iterator eachCall = callMap.values().iterator();
             eachCall.hasNext();
                ) {

            Call call = (Call) eachCall.next();

            if (Stub.ENDPOINT_ADDRESS_PROPERTY.equals(propertyName)) {
                call.setTargetEndpointAddress((String) value);
            } else {
                call.setProperty(propertyName, value);
            }
        }
    }

    //used by streaming sender and callInvokerImpl during invocation
    // - do not remove
    public String _getDefaultEnvelopeEncodingStyle() {
        return null;
    }

    public String _getImplicitEnvelopeEncodingStyle() {
        return "";
    }

    public String _getEncodingStyle() {
        return SOAPNamespaceConstants.ENCODING;
    }

    public void _setEncodingStyle(String encodingStyle) {
        throw new UnsupportedOperationException("cannot set encoding style");
    }
}
