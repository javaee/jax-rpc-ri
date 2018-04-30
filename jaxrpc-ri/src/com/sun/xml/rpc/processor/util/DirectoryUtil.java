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


package com.sun.xml.rpc.processor.util;

import java.io.File;

import com.sun.xml.rpc.processor.generator.GeneratorException;
import com.sun.xml.rpc.util.ClassNameInfo;

/**
 * Util provides static utility methods used by other wscompile classes.
 *
 * @author JAX-RPC Development Team
 */
public class DirectoryUtil  {
    
    public static File getOutputDirectoryFor(String theClass,
        File rootDir, ProcessorEnvironment env) throws GeneratorException {
            
        File outputDir = null;
        String qualifiedClassName = theClass;
        String packagePath = null;
        String packageName = ClassNameInfo.getQualifier(qualifiedClassName);
        if (packageName == null) {
            packageName = "";
        } else if (packageName.length() > 0) {
            packagePath = packageName.replace('.', File.separatorChar);
        }
        
        // Do we have a root directory?
        if (rootDir != null) {
            
            // Yes, do we have a package name?
            if (packagePath != null) {
                
                // Yes, so use it as the root. Open the directory...
                outputDir = new File(rootDir, packagePath);
                
                // Make sure the directory exists...
                ensureDirectory(outputDir,env);
            } else {
                
                // Default package, so use root as output dir...
                outputDir = rootDir;
            }
        } else {
            
            // No root directory. Get the current working directory...
            String workingDirPath = System.getProperty("user.dir");
            File workingDir = new File(workingDirPath);
            
            // Do we have a package name?
            if (packagePath == null) {
                
                // No, so use working directory...
                outputDir = workingDir;
            } else {
                
                // Yes, so use working directory as the root...
                outputDir = new File(workingDir, packagePath);
                
                // Make sure the directory exists...
                ensureDirectory(outputDir,env);
            }
        }
        
        // Finally, return the directory...
        return outputDir;
    }
    
    private static void ensureDirectory(File dir, ProcessorEnvironment env)
        throws GeneratorException {
            
        if (!dir.exists()) {
            dir.mkdirs();
            if (!dir.exists()) {
                throw new GeneratorException("generator.cannot.create.dir",
                    dir.getAbsolutePath());
            }
        }
    }
}

