/*
 * $Id: J2EEModelerIf.java,v 1.2 2006-04-13 01:30:15 ofung Exp $
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
package com.sun.xml.rpc.processor.modeler.j2ee;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.model.Operation;
import com.sun.xml.rpc.processor.model.literal.LiteralType;
import com.sun.xml.rpc.wsdl.document.Message;
import com.sun.xml.rpc.wsdl.document.soap.SOAPBody;
import com.sun.xml.rpc.processor.modeler.JavaSimpleTypeCreator;
import com.sun.xml.rpc.processor.modeler.wsdl.WSDLModelerBase.ProcessSOAPOperationInfo;


public interface J2EEModelerIf {
    public LiteralType getElementTypeToLiteralType(QName elementType);
    public boolean useSuperExplicitServiceContextForDocLit(Message inputMessage);
    public boolean useSuperExplicitServiceContextForRpcLit(Message inputMessage);
    public boolean useSuperExplicitServiceContextForRpcEncoded(Message inputMessage);
    public boolean isSuperUnwrappable();
    public LiteralType getSuperElementTypeToLiteralType(QName elementType);
    public String getSuperJavaNameForOperation(Operation operation);
    public ProcessSOAPOperationInfo getInfo();
    public Message getSuperOutputMessage();
    public Message getSuperInputMessage();
    public SOAPBody getSuperSOAPRequestBody();
    public SOAPBody getSuperSOAPResponseBody();
    public JavaSimpleTypeCreator getJavaTypes();
}
