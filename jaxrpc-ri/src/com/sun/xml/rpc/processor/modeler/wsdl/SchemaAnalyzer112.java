/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2018 Sun Microsystems, Inc. All rights reserved.
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

import com.sun.xml.rpc.processor.config.ModelInfo;
import com.sun.xml.rpc.processor.model.literal.LiteralType;
import com.sun.xml.rpc.processor.modeler.JavaSimpleTypeCreator;
import com.sun.xml.rpc.wsdl.framework.AbstractDocument;

/**
 * @author Vivek Pandey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SchemaAnalyzer112 extends SchemaAnalyzer111 {
    /**
     * @param document
     * @param modelInfo
     * @param options
     * @param conflictingClassNames
     * @param javaTypes
     */
    public SchemaAnalyzer112(
            AbstractDocument document,
            ModelInfo modelInfo,
            Properties options,
            Set conflictingClassNames,
            JavaSimpleTypeCreator javaTypes) {
        super(document, modelInfo, options, conflictingClassNames, javaTypes);
    }

    /* (bug fix: 4999385
     * @see com.sun.xml.rpc.processor.modeler.wsdl.SchemaAnalyzerBase#doWeHandleAttributeTypeEnumeration(com.sun.xml.rpc.processor.model.literal.LiteralType)
     */
    protected boolean doWeHandleAttributeTypeEnumeration(LiteralType attributeType) {
        return isAttributeEnumeration(attributeType);
    }

}
