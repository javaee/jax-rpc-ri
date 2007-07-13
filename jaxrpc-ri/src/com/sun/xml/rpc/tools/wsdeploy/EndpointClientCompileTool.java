/*
 * $Id: EndpointClientCompileTool.java,v 1.3 2007-07-13 23:36:36 ofung Exp $
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

package com.sun.xml.rpc.tools.wsdeploy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import com.sun.xml.rpc.processor.Processor;
import com.sun.xml.rpc.processor.ProcessorNotificationListener;
import com.sun.xml.rpc.processor.config.Configuration;
import com.sun.xml.rpc.processor.config.ModelFileModelInfo;
import com.sun.xml.rpc.processor.util.GeneratedFileInfo;
import com.sun.xml.rpc.processor.util.XMLModelWriter;
import com.sun.xml.rpc.tools.wscompile.ActionConstants;
import com.sun.xml.rpc.tools.wscompile.CompileTool;
import com.sun.xml.rpc.util.localization.Localizable;

/**
 *
 * @author JAX-RPC Development Team
 */
public class EndpointClientCompileTool extends CompileTool {
    
    public EndpointClientCompileTool(
        OutputStream out,
        String program,
        WebServicesInfo wsi,
        ArrayList list,
        File dir,
        String target,
        String classpath,
        ProcessorNotificationListener l) {
            
        super(out, program);
        webServicesInfo = wsi;
        targetDirectory = dir;
        additionalClasspath = classpath;
        listener = l;
        clientList = list;
        endpointClient = (EndpointClientInfo) clientList.get(0);
        useModel = endpointClient.getModel() != null;
        this.targetVersion = target;
    }
    
    protected void beforeHook() {
        
        // set things up properly
        String targetPath = targetDirectory.getAbsolutePath();
        
        nonclassDestDir = new File(targetPath + FS + "WEB-INF");
        userClasspath = targetPath + FS + "WEB-INF" + FS + "lib";
        destDir = new File(userClasspath);
        
        // needs to be changed
        userClasspath = targetPath
            + FS
            + "WEB-INF"
            + FS
            + "lib"
            + PS
            + targetPath
            + FS
            + "WEB-INF"
            + FS
            + "classes";
        
        // Add all jar files under WEB-INF/lib to the user classpath
        if (new File(targetPath + FS + "WEB-INF" + FS + "lib").exists()) {
            File[] fs = new File(
                targetPath + FS + "WEB-INF" + FS + "lib").listFiles();
            for (int counter = 0; counter < fs.length; ++counter) {
                userClasspath += PS + fs[counter];
            }
        }
        
        if (additionalClasspath != null && additionalClasspath.length() > 0) {
            userClasspath += PS + additionalClasspath;
        }
        
        if (!useModel) {
            modelFile = new File(makeModelFileName());
        } else if (targetVersion != null) {
            onWarning(getMessage(
                "wscompile.warning.ignoringTargetVersionForModel",
                endpointClient.getModel(),
                targetVersion));
            targetVersion = null;
        }
        serializerInfix = "_" + endpointClient.getName() + "_";
        keepGenerated = true;
        compilerDebug = false;
        compilerOptimize = true;
        super.beforeHook();
    }
    
    protected void withModelHook() {
    }
    
    public com.sun.xml.rpc.spi.tools.Configuration createConfiguration()
        throws Exception {
            
        // create our own configuration
        Configuration config = new Configuration(environment);
        if (useModel) {
            ModelFileModelInfo modelInfo = new ModelFileModelInfo();
            modelInfo.setLocation(makeAbsolute(endpointClient.getModel()));
            config.setModelInfo(modelInfo);
        }
        return config;
    }
    
    protected String makeTargetNamespaceURI() {
        String base = webServicesInfo.getTargetNamespaceBase();
        if (base.endsWith("/") || base.startsWith("urn:")) {
            return base + endpointClient.getName();
        } else {
            return base + "/" + endpointClient.getName();
        }
    }
    
    protected String makeTypeNamespaceURI() {
        String base = webServicesInfo.getTypeNamespaceBase();
        if (base.endsWith("/") || base.startsWith("urn:")) {
            return base + endpointClient.getName();
        } else {
            return base + "/" + endpointClient.getName();
        }
    }
    
    protected String makeModelFileName() {
        return targetDirectory.getAbsolutePath()
            + FS
            + "WEB-INF"
            + FS
            + endpointClient.getName()
            + "_model.xml.gz";
    }
    
    protected String makeJavaPackageName() {
        return "jaxrpc.generated." +
            environment.getNames().validJavaPackageName(
                endpointClient.getName());
    }
    
    protected String makeAbsolute(String s) {
        if (s == null) {
            return null;
        }
        return new File(targetDirectory.getAbsolutePath() + s)
            .getAbsolutePath();
    }
    
    protected String makeAppRelative(File f) {
        if (f == null) {
            return null;
        }
        String s = f.getAbsolutePath();
        String target = targetDirectory.getAbsolutePath();
        if (s.startsWith(target)) {
            return s.substring(target.length()).replace(FSCHAR, '/');
        } else {
            
            // TODO - isn't this an error?
            return null;
        }
    }
    
    protected File findGeneratedFileEndingWith(String s) {
        for (Iterator iter = environment.getGeneratedFiles();
            iter.hasNext();) {
                
            GeneratedFileInfo fileInfo = (GeneratedFileInfo) iter.next();
            File file = fileInfo.getFile();
            if (file.getAbsolutePath().endsWith(s)) {
                return file;
            }
        }
        return null;
    }
    
    protected void registerProcessorActions(Processor processor) {
        
        // completely override the actions in the base class
        if (!useModel) {
            try {
                processor.add(new XMLModelWriter(modelFile));
            } catch (FileNotFoundException e) {
                
                // should not happen
            }
        }
        
        processor.add(getAction(
            ActionConstants.ACTION_SERVICE_INTERFACE_GENERATOR));
        processor.add(getAction(ActionConstants.ACTION_SERVICE_GENERATOR));
        processor.add(getAction(ActionConstants.ACTION_CUSTOM_CLASS_GENERATOR));
        processor.add(getAction(
            ActionConstants.ACTION_ENUMERATION_ENCODER_GENERATOR));
        processor.add(getAction(
            ActionConstants.ACTION_LITERAL_OBJECT_SERIALIZER_GENERATOR));
        processor.add(getAction(
            ActionConstants.ACTION_SOAP_FAULT_SERIALIZER_GENERATOR));
        processor.add(getAction(
            ActionConstants.ACTION_FAULT_EXCEPTION_BUILDER_GENERATOR));
        if (delegate != null) {
            delegate.postRegisterProcessorActions();
        }
    }
    
    /* Methods for localization of Messages */
    public void onError(Localizable msg) {
        if (delegate != null) {
            delegate.preOnError();
        }
        report(getMessage("wscompile.error", localizer.localize(msg)));
    }
    public void onWarning(Localizable msg) {
        report(getMessage("wscompile.warning", localizer.localize(msg)));
    }
    public void onInfo(Localizable msg) {
        report(getMessage("wscompile.info", localizer.localize(msg)));
    }
    
    protected WebServicesInfo webServicesInfo;
    protected File targetDirectory;
    protected boolean useModel;
    protected String additionalClasspath;
    protected Hashtable hashtable;
    protected ArrayList vector;
    protected EndpointClientInfo endpointClient;
    protected ArrayList clientList;
    protected boolean localUseWSIBasicProfile = false;
    
    private final static String PS = System.getProperty("path.separator");
    private final static char PSCHAR =
        System.getProperty("path.separator").charAt(0);
    private final static String FS = System.getProperty("file.separator");
    private final static char FSCHAR =
        System.getProperty("file.separator").charAt(0);
}
