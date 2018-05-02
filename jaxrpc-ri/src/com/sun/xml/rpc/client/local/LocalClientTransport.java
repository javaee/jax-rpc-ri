/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.xml.rpc.client.local;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;

import com.sun.xml.rpc.client.ClientTransport;
import com.sun.xml.rpc.client.ClientTransportException;
import com.sun.xml.rpc.soap.message.Handler;
import com.sun.xml.rpc.soap.message.SOAPMessageContext;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import com.sun.xml.rpc.util.localization.Localizable;

/**
 * @author JAX-RPC Development Team
 */
public class LocalClientTransport implements ClientTransport {

    //this class is used primarily for debugging purposes
    public LocalClientTransport(Handler handler) {
        _handler = handler;
    }

    public LocalClientTransport(Handler handler, OutputStream logStream) {
        _handler = handler;
        _logStream = logStream;
    }

    public void invoke(String endpoint, SOAPMessageContext context) {
        try {
            if (_logStream != null) {
                String s = "\n******************\nRequest\n";
                _logStream.write(s.getBytes());
                context.getMessage().writeTo(_logStream);
            }

            setSOAPMessageFromSAAJ(context);

            _handler.handle(context);

            setSOAPMessageFromSAAJ(context);            

            // set this because a sender cannot rely on it being set
            // automatically
            context.setFailure(false);

            if (_logStream != null) {
                String s = "\nResponse\n";
                _logStream.write(s.getBytes());
                context.getMessage().writeTo(_logStream);
                s = "\n******************\n\n";
                _logStream.write(s.getBytes());
            }
        } catch (Exception e) {
            if (e instanceof Localizable) {
                throw new ClientTransportException(
                    "local.client.failed",
                    (Localizable) e);
            } else {
                throw new ClientTransportException(
                    "local.client.failed",
                    new LocalizableExceptionAdapter(e));
            }
        }
    }

    /**
     * Set the SOAPMessage in the context using stream thru SAAJ, this is 
     * to closely match http behaviour
     * 
     * @param context
     */
    private void setSOAPMessageFromSAAJ(SOAPMessageContext context) throws Exception{
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        context.getMessage().writeTo(os);

        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());

        SOAPMessage message =
            MessageFactory.newInstance().createMessage(
                context.getMessage().getMimeHeaders(),
                is);
        context.setMessage(message);
    }

    public void invokeOneWay(String endpoint, SOAPMessageContext context) {
        try {
            if (_logStream != null) {
                String s = "\n******************\nRequest\n";
                _logStream.write(s.getBytes());
                context.getMessage().writeTo(_logStream);
            }

            _handler.handle(context);

            // set this because a sender cannot rely on it being set
            // automatically
            context.setFailure(false);

        } catch (Exception e) {
            if (e instanceof Localizable) {
                throw new ClientTransportException(
                    "local.client.failed",
                    (Localizable) e);
            } else {
                throw new ClientTransportException(
                    "local.client.failed",
                    new LocalizableExceptionAdapter(e));
            }
        }
    }

    private Handler _handler;
    private OutputStream _logStream;
}
