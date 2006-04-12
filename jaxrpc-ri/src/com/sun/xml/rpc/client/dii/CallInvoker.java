/*
 * $Id: CallInvoker.java,v 1.1 2006-04-12 20:33:57 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.client.dii;

import com.sun.xml.rpc.encoding.JAXRPCDeserializer;
import com.sun.xml.rpc.encoding.JAXRPCSerializer;
import com.sun.xml.rpc.encoding.soap.SOAPResponseStructure;

/**
 * @author JAX-RPC Development Team
 */
public interface CallInvoker {
    public SOAPResponseStructure doInvoke(CallRequest callInfo,
                                          JAXRPCSerializer requestSerializer,
                                          JAXRPCDeserializer responseDeserializer,
                                          JAXRPCDeserializer faultDeserializer)
            throws Exception;

    public void doInvokeOneWay(CallRequest callInfo,
                               JAXRPCSerializer requestSerializer)
            throws Exception;
}