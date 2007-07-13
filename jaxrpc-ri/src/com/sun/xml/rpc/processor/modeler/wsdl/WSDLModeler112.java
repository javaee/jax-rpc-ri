/*
 * $Id: WSDLModeler112.java,v 1.3 2007-07-13 23:36:17 ofung Exp $
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
package com.sun.xml.rpc.processor.modeler.wsdl;

import java.util.Properties;
import java.util.Set;

import com.sun.xml.rpc.processor.config.WSDLModelInfo;
import com.sun.xml.rpc.processor.modeler.JavaSimpleTypeCreator;
import com.sun.xml.rpc.wsdl.document.WSDLDocument;

/**
 * @author Vivek Pandey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class WSDLModeler112 extends WSDLModelerBase {

    /**
     * @param modelInfo
     * @param options
     */
    public WSDLModeler112(WSDLModelInfo modelInfo, Properties options) {
        super(modelInfo, options);
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see com.sun.xml.rpc.processor.modeler.wsdl.WSDLModelerBase#getSchemaAnalyzerInstance(com.sun.xml.rpc.wsdl.document.WSDLDocument, com.sun.xml.rpc.processor.config.WSDLModelInfo, java.util.Properties, java.util.Set, com.sun.xml.rpc.processor.modeler.JavaSimpleTypeCreator)
     */
    protected SchemaAnalyzerBase getSchemaAnalyzerInstance(
        WSDLDocument document,
        WSDLModelInfo _modelInfo,
        Properties _options,
        Set _conflictingClassNames,
        JavaSimpleTypeCreator _javaTypes) {
        return new SchemaAnalyzer112(
                document,
                _modelInfo,
                _options,
                _conflictingClassNames,
                _javaTypes);
    }

}
