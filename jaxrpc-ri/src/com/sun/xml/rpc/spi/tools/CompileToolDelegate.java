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
package com.sun.xml.rpc.spi.tools;

/**
 * This delegate is used by the implemenation of 
 * com.sun.xml.rpc.spi.tools.wscompile.CompileTool
 * so that customized implementation could be provided to
 * override jaxrpc specific implementation
 */
public abstract class CompileToolDelegate {

    /**
     * Default constructor.  Do nothing.
     */
    public CompileToolDelegate() {
    }

    /**
     * Assuming the jaxrpc implementation of createConfiguration()
     * will not overwrite if the delegates does return a non-null
     * Configuration object.
     */
    public Configuration createConfiguration() {
        //no op
        return null;
    }

    public void preOnError() {
        //no op
    }

    /**
     * Called right after CompileTool.registerProcessorActions.
     * We probably should also expose registerProcessorAction() in
     * case someone else would like to register more processor actions.
     * But minimum set for now until the need rises.
     */
    public void postRegisterProcessorActions() {
        // no op
    }

    /**
     */
    public void postRun() {
        //no op
    }

    /**
     * Subclass of the CompileToolDelegate is responsible to set
     * its association to a CompileTool implementation that will
     * callback for any customized implementation.  
     * <p>
     * The association between the CompileToolDelegate and CompileTool
     * is bi-directional to ensure that the delegate could also
     * access environment known to CompileTool.
     * @see CompileTool
     */
    public void setCompileTool(CompileTool wscompile) {
        //no op 
    }
}
