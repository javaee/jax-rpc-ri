/*
 * $Id: ActionConstants.java,v 1.3 2007-07-13 23:36:35 ofung Exp $
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


package com.sun.xml.rpc.tools.wscompile;

/**
 *
 * @author JAX-RPC Development Team
 */
public interface ActionConstants {
    
    public static final String ACTION_REMOTE_INTERFACE_GENERATOR          = "remote.interface.generator";
    public static final String ACTION_REMOTE_INTERFACE_IMPL_GENERATOR     = "remote.interface.impl.generator";
    public static final String ACTION_CUSTOM_CLASS_GENERATOR              = "custom.class.generator";
    public static final String ACTION_SOAP_OBJECT_SERIALIZER_GENERATOR    = "soap.object.serializer.generator";
    public static final String ACTION_INTERFACE_SERIALIZER_GENERATOR      = "interface.serializer.generator";
    public static final String ACTION_SOAP_OBJECT_BUILDER_GENERATOR       = "soap.object.builder.generator";
    public static final String ACTION_LITERAL_OBJECT_SERIALIZER_GENERATOR = "literal.object.serializer.generator";
    public static final String ACTION_STUB_GENERATOR                      = "stub.generator";
    public static final String ACTION_TIE_GENERATOR                       = "tie.generator";
    public static final String ACTION_SERVLET_CONFIG_GENERATOR            = "servlet.config.generator";
    public static final String ACTION_WSDL_GENERATOR                      = "wsdl.generator";
    public static final String ACTION_HOLDER_GENERATOR                    = "holder.generator";
    public static final String ACTION_SERVICE_INTERFACE_GENERATOR         = "service.interface.generator";
    public static final String ACTION_SERVICE_GENERATOR                   = "service.generator";
    public static final String ACTION_SERIALIZER_REGISTRY_GENERATOR       = "serializer.registry.generator";
    public static final String ACTION_CUSTOM_EXCEPTION_GENERATOR          = "custom.exception.generator";
    public static final String ACTION_SOAP_FAULT_SERIALIZER_GENERATOR     = "soap.fault.serializer.generator";
    public static final String ACTION_FAULT_EXCEPTION_BUILDER_GENERATOR   = "fault.exception.builder.generator";
    public static final String ACTION_ENUMERATION_GENERATOR               = "enumeration.generator";
    public static final String ACTION_ENUMERATION_ENCODER_GENERATOR       = "enumeration.encoder.generator";
}
