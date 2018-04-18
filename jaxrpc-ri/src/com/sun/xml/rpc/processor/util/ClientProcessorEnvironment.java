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
 * $Id: ClientProcessorEnvironment.java,v 1.3 2007-07-13 23:36:22 ofung Exp $
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

package com.sun.xml.rpc.processor.util;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sun.xml.rpc.processor.ProcessorNotificationListener;
import com.sun.xml.rpc.processor.generator.Names;
import com.sun.xml.rpc.util.JAXRPCClassFactory;
import com.sun.xml.rpc.util.localization.Localizable;

/**
 *
 * @author JAX-RPC Development Team
 */
public class ClientProcessorEnvironment extends ProcessorEnvironmentBase
    implements ProcessorEnvironment {
    
    /**
     * The stream where error message are printed.
     */
    private OutputStream out;
    
    /**
     * A printwriter created lazily in case there are exceptions to report.
     */
    private PrintStream outprintstream;
    
    /**
     * listener for error/warning/info notifications
     */
    private ProcessorNotificationListener listener;
    
    /**
     * The classpath to use
     */
    private String classPath;
    
    /**
     * list of generated source files created in this environment and
     * its type
     */
    private List generatedFiles = new ArrayList();
    
    /**
     * The number of errors and warnings
     */
    private int nwarnings;
    private int nerrors;
    
    /**
     * flags
     */
    private int flags;
    
    private Names names;
    
    /**
     * Create a ClientProcessorEnvironment with the given class path,
     * stream for messages and ProcessorNotificationListener.
     */
    public ClientProcessorEnvironment(
        OutputStream out,
        String classPath,
        ProcessorNotificationListener listener) {
            
        this.out = out;
        this.classPath = classPath;
        this.listener = listener;
        flags = 0;
        
        //bug fix:4904604
        names = JAXRPCClassFactory.newInstance().createNames();
    }
    
    /**
     * Set the environment flags
     */
    public void setFlags(int flags) {
        this.flags = flags;
    }
    
    /**
     * Get the environment flags
     */
    public int getFlags() {
        return flags;
    }
    
    /**
     * Get the ClassPath.
     */
    public String getClassPath() {
        return classPath;
    }
    
    /**
     * Is verbose turned on
     */
    public boolean verbose() {
        return (flags & F_VERBOSE) != 0;
    }
    
    /**
     * Remember info on  generated source file generated so that it
     * can be removed later, if appropriate.
     */
    public void addGeneratedFile(GeneratedFileInfo file) {
        generatedFiles.add(file);
    }
    
    /**
     * Return all the generated files and its types.
     */
    public Iterator getGeneratedFiles() {
        return generatedFiles.iterator();
    }
    
    /**
     * Delete all the generated source files made during the execution
     * of this environment (those that have been registered with the
     * "addGeneratedFile" method).
     */
    public void deleteGeneratedFiles() {
        synchronized (generatedFiles) {
            Iterator iter = generatedFiles.iterator();
            while (iter.hasNext()) {
                File file = ((GeneratedFileInfo)iter.next()).getFile();
                if (file.getName().endsWith(".java")) {
                    file.delete();
                }
            }
            generatedFiles.clear();
        }
    }
    
    /**
     * Release resources, if any.
     */
    public void shutdown() {
        listener = null;
        generatedFiles = null;
    }
    
    public void error(Localizable msg) {
        if (listener != null) {
            listener.onError(msg);
        }
        nerrors++;
    }
    
    public void warn(Localizable msg) {
        if (warnings()) {
            nwarnings++;
            if (listener != null) {
                listener.onWarning(msg);
            }
        }
    }
    
    public void info(Localizable msg) {
        if (listener != null) {
            listener.onInfo(msg);
        }
    }
    
    public void printStackTrace(Throwable t) {
        if (outprintstream == null) {
            outprintstream = new PrintStream(out);
        }
        t.printStackTrace(outprintstream);
    }
    
    public Names getNames() {
        return names;
    }
    
    public int getErrorCount() {
        return nerrors;
    }
    
    public int getWarningCount() {
        return nwarnings;
    }
    
    private boolean warnings() {
        return (flags & F_WARNINGS) != 0;
    }
    
    //bug fix:4904604
    //to called in compileTool after env is
    public void setNames(Names names) {
        this.names = names;
    }
    
}
