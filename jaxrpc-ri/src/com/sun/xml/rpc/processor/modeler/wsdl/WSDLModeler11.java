/*
  * $Id: WSDLModeler11.java,v 1.2 2006-04-13 01:31:32 ofung Exp $
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
package com.sun.xml.rpc.processor.modeler.wsdl;

import java.util.Properties;
import java.util.Set;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.config.WSDLModelInfo;
import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.Fault;
import com.sun.xml.rpc.processor.modeler.JavaSimpleTypeCreator;
import com.sun.xml.rpc.wsdl.document.MessagePart;
import com.sun.xml.rpc.wsdl.document.WSDLDocument;
import com.sun.xml.rpc.wsdl.framework.Extensible;
import com.sun.xml.rpc.wsdl.framework.Extension;

/**
 * @author JAX-RPC Development Team
 *
 * WSDLModeler for JAXRPC version 1.1
 */
public class WSDLModeler11 extends WSDLModelerBase {

    /**
     * @param modelInfo
     * @param options
     */
    public WSDLModeler11(WSDLModelInfo modelInfo, Properties options) {
        super(modelInfo, options);
    }

    /**
     * bug fix: 4884736, this method can be overriden from subclasses of WSDLModelerBase
     * Returns soapbinding:fault name. If null then gives warning for wsi R2721 and uses 
     * wsdl:fault name.
     * 
     * @param faultPartName - to be used by versions < 1.1
     * @param soapFaultName
     * @param bindFaultName
     * @return
     */
    protected String getFaultName(
        String faultPartName,
        String soapFaultName,
        String bindFaultName,
        String faultMessageName) {
          
        return (soapFaultName == null) ? bindFaultName : soapFaultName;
    }

    protected String getLiteralJavaMemberName(Fault fault) {
        String javaMemberName =
            fault.getBlock().getName().getLocalPart();
        return javaMemberName;
    }

    /* 
     * @see com.sun.xml.rpc.processor.modeler.wsdl.WSDLModelerBase#getSchemaAnalyzerInstance(com.sun.xml.rpc.wsdl.document.WSDLDocument, com.sun.xml.rpc.processor.config.WSDLModelInfo, java.util.Properties, java.util.Set, com.sun.xml.rpc.processor.modeler.JavaSimpleTypeCreator)
     */
    protected SchemaAnalyzerBase getSchemaAnalyzerInstance(
        WSDLDocument document,
        WSDLModelInfo _modelInfo,
        Properties _options,
        Set _conflictingClassNames,
        JavaSimpleTypeCreator _javaTypes) {
        return new SchemaAnalyzer11(
            document,
            _modelInfo,
            _options,
            _conflictingClassNames,
            _javaTypes);

    }
    /* (non-Javadoc)
     * @see com.sun.xml.rpc.processor.modeler.wsdl.WSDLModelerBase#getHeaderFaultSequenceType(com.sun.xml.rpc.processor.model.AbstractType, com.sun.xml.rpc.wsdl.document.MessagePart, javax.xml.namespace.QName)
     */
    protected AbstractType getHeaderFaultSequenceType(
        AbstractType faultType,
        MessagePart faultPart,
        QName elemName) {
        return faultType;
    }

    /* (non-Javadoc)
     * @see com.sun.xml.rpc.processor.modeler.wsdl.WSDLModelerBase#isSingleInOutPart(java.util.Set, com.sun.xml.rpc.wsdl.document.MessagePart)
     */
    protected boolean isSingleInOutPart(
        Set inputParameterNames,
        MessagePart outputPart) {
        return false;
    }
        
    /* 
     * Only JAXRPC SI 1.1.2 and onwards support wsdl mime extension and swaref.
     * @see com.sun.xml.rpc.processor.modeler.wsdl.WSDLModelerBase#getAnyExtensionOfType(com.sun.xml.rpc.wsdl.framework.Extensible, java.lang.Class)
     */
    protected Extension getAnyExtensionOfType(
            Extensible extensible,
            Class type) {
        return getExtensionOfType(extensible, type);
    }
    
}
