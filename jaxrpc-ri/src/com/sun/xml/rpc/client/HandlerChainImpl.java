/*
 * $Id: HandlerChainImpl.java,v 1.3 2007-07-13 23:35:55 ofung Exp $
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

package com.sun.xml.rpc.client;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.xml.namespace.QName;
import javax.xml.rpc.handler.Handler;
import javax.xml.rpc.handler.HandlerChain;
import javax.xml.rpc.handler.HandlerInfo;
import javax.xml.rpc.handler.MessageContext;
import javax.xml.rpc.soap.SOAPFaultException;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;

import com.sun.xml.rpc.soap.message.SOAPMessageContext;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;

/**
 * @author JAX-RPC Development Team
 */
public class HandlerChainImpl extends Vector implements HandlerChain {
    protected List handlerInfos;

    String[] roles = null;

    public HandlerChainImpl(List handlerInfos) {
        this.handlerInfos = handlerInfos;
        createHandlerInstances();
    }

    private void createHandlerInstances() {
        for (int i = 0; i < handlerInfos.size(); i++)
            add(newHandler(getHandlerInfo(i)));
    }

    public boolean handleFault(MessageContext _context) {
        SOAPMessageContext context = (SOAPMessageContext) _context;

        int n = context.getCurrentHandler();

        for (int i = n; i >= 0; i--) {
            context.setCurrentHandler(i);

            try {
                if (getHandlerInstance(i).handleFault(context) == false) {
                    return false;
                }
            } catch (SOAPFaultException sfe) {
                throw sfe;
            } catch (RuntimeException re) {
                deleteHandlerInstance(i);
                setElementAt(newHandler(getHandlerInfo(i)), i);
                throw re;
            }
        }

        context.setCurrentHandler(-1);
        return true;
    }

    public boolean handleRequest(MessageContext _context) {

        SOAPMessageContext context = (SOAPMessageContext) _context;
        context.setRoles(roles);

        for (int i = 0; i < size(); i++) {
            Handler currentHandler = getHandlerInstance(i);
            context.setCurrentHandler(i);

            try {
                if (currentHandler.handleRequest(context) == false) {
                    return false;
                }
            } catch (SOAPFaultException sfe) {
                throw sfe;
            } catch (RuntimeException re) {
                deleteHandlerInstance(i);
                setElementAt(newHandler(getHandlerInfo(i)), i);
                throw re;
            }
        }
        context.setCurrentHandler(-1);
        return true;
    }

    public boolean handleResponse(MessageContext _context) {
        if (size() > 0) {
            SOAPMessageContext context = (SOAPMessageContext) _context;

            int n = context.getCurrentHandler();

            if (n == -1)
                n = size() - 1;

            for (int i = n; i >= 0; i--) {
                context.setCurrentHandler(i);

                try {
                    if (getHandlerInstance(i).handleResponse(context)
                        == false) {
                        context.setCurrentHandler(-1);
                        return false;
                    }
                } catch (SOAPFaultException sfe) {
                    throw sfe;
                } catch (RuntimeException re) {
                    deleteHandlerInstance(i);
                    setElementAt(newHandler(getHandlerInfo(i)), i);
                    throw re;
                }
            }

            context.setCurrentHandler(-1);
        }
        return true;
    }

    boolean initialized = false;

    public void init(java.util.Map config) {
        // TODO: How to implement this?
    }

    public void destroy() {
        for (int i = 0; i < size(); i++)
            deleteHandlerInstance(i);
        clear();
    }

    protected void deleteHandlerInstance(int index) {
        Handler h = getHandlerInstance(index);
        h.destroy();
        removeHandlerFromPool(h.getClass());
    }

    /*
     * Allow handlers to be added so that handler and handler infos
     * lists are kept in sync.
     */
    public void addHandlerInfo(int index, HandlerInfo handlerInfo) {
        handlerInfos.add(index, handlerInfo);
        add(index, newHandler(handlerInfo));
    }
    
    /*
     * Allow handlers to be added so that handler and handler infos
     * lists are kept in sync. This version of method simply appends
     * handler.
     */
    public void addHandlerInfo(HandlerInfo handlerInfo) {
        addHandlerInfo(handlerInfos.size(), handlerInfo);
    }

    protected Handler getHandlerInstance(int index) {
        return (Handler) castToHandler(get(index));
    }

    protected HandlerInfo getHandlerInfo(int index) {
        return (HandlerInfo) handlerInfos.get(index);
    }

    Hashtable handlerPool = new Hashtable();

    protected void removeHandlerFromPool(Class clz) {
        handlerPool.remove(clz.getName());
    }

    protected Handler getHandlerFromPool(HandlerInfo handlerInfo) {
        Class clz = handlerInfo.getHandlerClass();
        Handler h = (Handler) handlerPool.get(clz.getName());
        if (h == null)
            try {
                h = (Handler) clz.newInstance();
                h.init(handlerInfo);
                addUnderstoodHeaders(h.getHeaders());
                handlerPool.put(clz.getName(), h);
            } catch (Exception ex) {
                throw new HandlerException(
                    "Unable to instantiate handler: ",
                    new Object[] {
                        handlerInfo.getHandlerClass(),
                        new LocalizableExceptionAdapter(ex)});
            }
        return h;
    }

    protected Handler newHandler(HandlerInfo handlerInfo) {
        return getHandlerFromPool(handlerInfo);
    }

    public void setRoles(String[] soapActorNames) {
        this.roles = soapActorNames;
    }

    public String[] getRoles() {
        return roles;
    }

    protected Handler castToHandler(Object o) {
        if (!(o instanceof Handler)) {
            throw new HandlerException(
                "handler.chain.contains.handler.only",
                new Object[] { o.getClass().getName()});
        }
        return (Handler) o;
    }

    List understoodHeaders = new ArrayList();

    public void addUnderstoodHeaders(QName[] ignoredHeaders) {
        if (ignoredHeaders != null)
            for (int i = 0; i < ignoredHeaders.length; i++)
                understoodHeaders.add(ignoredHeaders[i]);
    }

    public boolean checkMustUnderstand(MessageContext mc)
        throws SOAPException {
        if (roles != null && !isEmpty()) {
            SOAPMessage soapMessage = ((SOAPMessageContext) mc).getMessage();
            SOAPHeader header =
                soapMessage.getSOAPPart().getEnvelope().getHeader();
            if (header == null) {
                return true;
            }
            for (int i = 0; i < roles.length; i++) {
                String actor = roles[i];
                Iterator it = header.examineMustUnderstandHeaderElements(actor);

                while (it.hasNext()) {
                    SOAPHeaderElement element = (SOAPHeaderElement) it.next();
                    Name saajName = element.getElementName();
                    QName qname =
                        new QName(saajName.getURI(), saajName.getLocalName());
                    if (!understoodHeaders.contains(qname)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
