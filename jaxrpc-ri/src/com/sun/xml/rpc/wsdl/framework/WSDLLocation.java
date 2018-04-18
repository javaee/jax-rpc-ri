/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
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

/*
 * $Id: WSDLLocation.java,v 1.3 2007-07-13 23:36:46 ofung Exp $
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

/**    
 *
 * Maintains wsdl:location context. This is used with 
 * ParserContext, where one each WSDL being imported its location is pushed, this will be used
 * latter to resolve relative imports of schema in SchemaParser.
 *
 * @author JAX-RPC Development Team
 */

package com.sun.xml.rpc.wsdl.framework;

public class WSDLLocation {
    WSDLLocation() {
        reset();
    }

    public void push() {
        int max = contexts.length;
        idPos++;
        if (idPos >= max) {
            LocationContext newContexts[] = new LocationContext[max * 2];
            System.arraycopy(contexts, 0, newContexts, 0, max);
            max *= 2;
            contexts = newContexts;
        }
        currentContext = contexts[idPos];
        if (currentContext == null) {
            contexts[idPos] = currentContext = new LocationContext();
        }
        if (idPos > 0) {
            currentContext.setParent(contexts[idPos - 1]);
        }

    }

    public void pop() {
        idPos--;
        if (idPos >= 0) {
            currentContext = contexts[idPos];
        }
    }

    public void reset() {
        contexts = new LocationContext[32];
        idPos = 0;
        contexts[idPos] = currentContext = new LocationContext();
    }

    public String getLocation() {
        return currentContext.getLocation();
    }

    public void setLocation(String loc) {
        currentContext.setLocation(loc);
    }

    private LocationContext[] contexts;
    private int idPos;
    private LocationContext currentContext;

    // LocationContext - inner class
    private static class LocationContext {
        void setLocation(String loc) {
            location = loc;
        }

        String getLocation() {
            return location;
        }

        void setParent(LocationContext parent) {
            parentLocation = parent;
        }

        private String location;
        private LocationContext parentLocation;
    }
}
