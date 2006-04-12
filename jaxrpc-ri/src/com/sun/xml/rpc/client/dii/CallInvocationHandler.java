/*
 * $Id: CallInvocationHandler.java,v 1.1 2006-04-12 20:33:58 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
