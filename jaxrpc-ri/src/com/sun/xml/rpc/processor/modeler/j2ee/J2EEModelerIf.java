/*
 * $Id: J2EEModelerIf.java,v 1.1 2006-04-12 20:34:57 kohlert Exp $
*/

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
