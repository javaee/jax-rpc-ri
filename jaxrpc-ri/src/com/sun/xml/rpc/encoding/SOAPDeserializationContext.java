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

package com.sun.xml.rpc.encoding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.xml.soap.SOAPMessage;

import com.sun.xml.rpc.soap.SOAPConstantsFactory;
import com.sun.xml.rpc.soap.SOAPNamespaceConstants;
import com.sun.xml.rpc.soap.SOAPVersion;
import com.sun.xml.rpc.streaming.Attributes;
import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import com.sun.xml.rpc.util.xml.XmlUtil;

/**
 *
 * @author JAX-RPC Development Team
 */
public class SOAPDeserializationContext {
    protected Stack encodingStyleContext = new Stack();
    protected String curEncodingStyle = null;
    protected boolean isSOAPEncodingStyle = false;
    protected List encodingStyleURIs = null;
    protected SOAPMessage message;
    protected Map stateIds = new HashMap();

    private SOAPVersion soapVer = SOAPVersion.SOAP_11;
    private SOAPNamespaceConstants soapNamespaceConstants = null;

    private void init(SOAPVersion ver) {
        soapNamespaceConstants =
            SOAPConstantsFactory.getSOAPNamespaceConstants(ver);
        this.soapVer = ver;
    }

    public SOAPDeserializationContext() {
        this(SOAPVersion.SOAP_11); // SOAP 1.1 is default
    }

    public SOAPDeserializationContext(SOAPVersion ver) {
        init(ver);
        pushEncodingStyle("");
    }

    public SOAPDeserializationState getStateFor(String id) {
        if (id == null) {
            return null;
        }

        SOAPDeserializationState elementState =
            (SOAPDeserializationState)stateIds.get(id);
        if (elementState == null) {
            elementState = new SOAPDeserializationState();
            stateIds.put(id, elementState);
        }
        return elementState;
    }

    public void deserializeMultiRefObjects(XMLReader reader) {

        try {
            while (reader.nextElementContent() == XMLReader.START) {
                String id = reader.getAttributes().getValue("", "id");
                if (id == null) {
                    throw new MissingTrailingBlockIDException("soap.missingTrailingBlockID");
                }

                SOAPDeserializationState elementState = getStateFor(id);
                elementState.deserialize(null, reader, this);
            }
        } catch (MissingTrailingBlockIDException e) {
            throw e;
        } catch (JAXRPCExceptionBase e) {
            throw new DeserializationException(e);
        } catch (Exception e) {
            throw new DeserializationException(
                new LocalizableExceptionAdapter(e));
        }
    }

    public void doneDeserializing() {
        for (Iterator iter = stateIds.values().iterator(); iter.hasNext();) {
            SOAPDeserializationState state =
                (SOAPDeserializationState)iter.next();
            state.promoteToCompleteOrFail();
        }
    }

    public void setMessage(SOAPMessage m) {
        message = m;
    }

    public SOAPMessage getMessage() {
        return message;
    }

    public void pushEncodingStyle(String newEncodingStyle) {
        encodingStyleContext.push(newEncodingStyle);
        initEncodingStyleInfo();
    }

    public void popEncodingStyle() {
        encodingStyleContext.pop();
        initEncodingStyleInfo();
    }

    public String getEncodingStyle() {
        return curEncodingStyle;
    }

    public boolean processEncodingStyle(XMLReader reader) throws Exception {

        Attributes attrs = reader.getAttributes();
        String newEncodingStyle =
            attrs.getValue(
                soapNamespaceConstants.getEnvelope(),
                soapNamespaceConstants.getAttrEncodingStyle());

        if (newEncodingStyle == null) {
            return false;
        } else {
            pushEncodingStyle(newEncodingStyle);
            return true;
        }
    }

    public void verifyEncodingStyle(String expectedEncodingStyle) {
        if (expectedEncodingStyle == null) {
            // no expectations
            return;
        }

        if (expectedEncodingStyle == soapNamespaceConstants.getEncoding()
            || expectedEncodingStyle.equals(
                soapNamespaceConstants.getEncoding())) {
            if (isSOAPEncodingStyle) {
                return;
            }
        } else if (encodingStyleURIs == null) {
            if (curEncodingStyle.startsWith(expectedEncodingStyle)) {
                return;
            }
        } else {
            for (int i = 0; i < encodingStyleURIs.size(); ++i) {
                String uri = (String)encodingStyleURIs.get(i);
                if (uri.startsWith(expectedEncodingStyle)) {
                    return;
                }
            }
        }

        throw new DeserializationException(
            "soap.unexpectedEncodingStyle",
            new Object[] { expectedEncodingStyle, curEncodingStyle });
    }

    private void initEncodingStyleInfo() {
        curEncodingStyle = (String)encodingStyleContext.peek();

        if (curEncodingStyle.indexOf(' ') == -1) {
            encodingStyleURIs = null;
            isSOAPEncodingStyle =
                curEncodingStyle.startsWith(
                    soapNamespaceConstants.getEncoding());
        } else {
            encodingStyleURIs = XmlUtil.parseTokenList(curEncodingStyle);
            isSOAPEncodingStyle = false;
            for (int i = 0; i < encodingStyleURIs.size(); ++i) {
                String uri = (String)encodingStyleURIs.get(i);
                if (uri.startsWith(soapNamespaceConstants.getEncoding())) {
                    isSOAPEncodingStyle = true;
                    break;
                }
            }
        }
    }

    public SOAPVersion getSOAPVersion() {
        return this.soapVer;
    }

    //xsd:IDREF
    public void addXSDIdObjectSerializer(String id, java.lang.Object obj) {
        if (idTable == null)
            idTable = new Hashtable();
        if (!idTable.containsKey(id)) {
            idTable.put(id, obj);
        } else
            throw new DeserializationException(
                "xsd.duplicateID",
                new Object[] { id });
    }

    public java.lang.Object getXSDIdObjectSerializer(String id) {
        if (idTable == null)
            return null;
        return idTable.get(id);
    }

    public void addPostDeserializationAction(PostDeserializationAction action) {
        // re-allocate buffer if necessary
        if (idResolver == null)
            idResolver = new ArrayList();
        idResolver.add(action);
    }

    /** Executes all the PostDeserializationResolvers. */
    public void runPostDeserializationAction() {
        if (idResolver != null) {
            Iterator iter = idResolver.iterator();
            while (iter.hasNext())
                 ((PostDeserializationAction)iter.next()).run(this);
        }
    }

    private Hashtable idTable;
    private ArrayList idResolver;
    private int idResolverLength = 0;
}
