/*
 * $Id: J2EESchemaAnalyzer.java,v 1.2 2006-04-13 01:30:15 ofung Exp $
 */

package com.sun.xml.rpc.processor.modeler.j2ee;

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

import java.util.Properties;
import java.util.Set;

import com.sun.xml.rpc.processor.config.J2EEModelInfo;
import com.sun.xml.rpc.processor.modeler.JavaSimpleTypeCreator;
import com.sun.xml.rpc.wsdl.framework.AbstractDocument;

/**
 *
 * @author JAX-RPC RI Development Team
 */
public class J2EESchemaAnalyzer extends J2EESchemaAnalyzer112 {

    public J2EESchemaAnalyzer(
        AbstractDocument document,
        J2EEModelInfo modelInfo,
        Properties options,
        Set conflictingClassNames,
        JavaSimpleTypeCreator javaTypes) {
        super(document, modelInfo, options, conflictingClassNames, javaTypes);
    }

}
