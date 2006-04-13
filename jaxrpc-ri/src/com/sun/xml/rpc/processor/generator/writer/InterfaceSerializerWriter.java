/*
 * $Id: InterfaceSerializerWriter.java,v 1.2 2006-04-13 01:29:13 ofung Exp $
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

package com.sun.xml.rpc.processor.generator.writer;

import com.sun.xml.rpc.processor.generator.GeneratorConstants;
import com.sun.xml.rpc.processor.generator.Names;
import com.sun.xml.rpc.processor.model.soap.SOAPType;

/**
 *
 * @author JAX-RPC Development Team
 */
public class InterfaceSerializerWriter
    extends SOAPObjectSerializerWriter
    implements GeneratorConstants {

    public InterfaceSerializerWriter(
        String basePackage,
        SOAPType type,
        Names names) {
        super(basePackage, type, names);
        serializerName =
            names.typeInterfaceSerializerClassName(
                basePackage,
                (SOAPType) type);
        serializerMemberName = names.getClassMemberName(serializerName);
    }
}
