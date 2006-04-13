/*
 * $Id: ModelerException.java,v 1.2 2006-04-13 01:30:11 ofung Exp $
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

package com.sun.xml.rpc.processor.modeler;

import com.sun.xml.rpc.processor.ProcessorException;
import com.sun.xml.rpc.util.localization.Localizable;

/**
 * ModelerException represents an exception that occurred while
 * visiting service model.
 *
 * @see com.sun.xml.rpc.util.exception.JAXRPCExceptionBase
 *
 * @author JAX-RPC Development Team
*/
public class ModelerException extends ProcessorException {
    
    public ModelerException(String key) {
        super(key);
    }
    
    public ModelerException(String key, String arg) {
        super(key, arg);
    }
    
    public ModelerException(String key, Object[] args) {
        super(key, args);
    }
    
    public ModelerException(String key, Localizable arg) {
        super(key, arg);
    }
    
    public ModelerException(Localizable arg) {
        super("modeler.nestedModelError", arg);
    }
    
    public String getResourceBundleName() {
        return "com.sun.xml.rpc.resources.modeler";
    }
    
}
