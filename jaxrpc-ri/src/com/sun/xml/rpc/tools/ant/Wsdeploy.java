/**
 * $Id: Wsdeploy.java,v 1.3 2007-07-13 23:36:35 ofung Exp $
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
package com.sun.xml.rpc.tools.ant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.LogOutputStream;
import org.apache.tools.ant.taskdefs.LogStreamHandler;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.CommandlineJava;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

import com.sun.xml.rpc.tools.wsdeploy.DeployTool;

/**
 * wsdeploy task for use with the JAXRPC project.
 *
 */
public class Wsdeploy extends MatchingTask {
    
    /********************  -f option **********************/
    
    /**
     * Gets the values for the "f" flag.
     */
    public String getF() {
        return getFeatures();
    }
    
    /**
     * Sets the values for the "f" flag.
     */
    public void setF(String features) {
        setFeatures(features);
    }
    
    /********************  -features option **********************/
    
    /**
     * values for features include: wsi
     */
    private String features = null;
    
    /**
     * Gets the values for the "features" flag.
     */
    public String getFeatures() {
        return features;
    }
    
    /**
     * Sets the values for the "features" flag.
     */
    public void setFeatures(String features) {
        this.features = features;
    }
    
    /*************************  -classpath option *************************/
    protected Path compileClasspath = null;
    
    /**
     * Gets the classpath.
     */
    public Path getClasspath() {
        return compileClasspath;
    }
    
    /**
     * Set the classpath to be used for this compilation.
     */
    public void setClasspath(Path classpath) {
        if (compileClasspath == null) {
            compileClasspath = classpath;
        } else {
            compileClasspath.append(classpath);
        }
    }
    
    /**
     * Creates a nested classpath element.
     */
    public Path createClasspath() {
        if (compileClasspath == null) {
            compileClasspath = new Path(project);
        }
        return compileClasspath.createPath();
    }
    
    /**
     * Adds a reference to a CLASSPATH defined elsewhere.
     */
    public void setClasspathRef(Reference r) {
        createClasspath().setRefid(r);
    }
    
    /*************************  -cp option *************************/
    /**
     * Gets the classpath.
     */
    public Path getCP() {
        return getClasspath();
    }
    
    /**
     * Set the classpath to be used for this compilation.
     */
    public void setCP(Path classpath) {
        setClasspath(classpath);
    }
    
    /*************************  -fork option *************************/
    private boolean fork = false;
    
    /** Gets the "keep" flag. **/
    public boolean getFork() {
        return fork;
    }
    
    /** Sets the "fork" flag. **/
    public void setFork(boolean fork) {
        this.fork = fork;
    }
    
    /********************  -jvmargs option **********************/
    protected String jvmargs;
    
    /** Gets the Java VM options. **/
    public String getJvmargs() {
        return jvmargs;
    }
    
    /** Sets the Java VM options. **/
    public void setJvmargs(String jvmargs) {
        this.jvmargs = jvmargs;
    }
    
    /*************************  -keep option *************************/
    private boolean keep = false;
    
    /** Gets the "keep" flag. */
    public boolean getKeep() {
        return keep;
    }
    
    /** Sets the "keep" flag. */
    public void setKeep(boolean keep) {
        this.keep = keep;
    }
    
    /*************************  -tmpdir option *************************/
    private File tmpDir = null;
    
    /** Gets the temporary directory to use. **/
    public File getTmpDir() {
        return this.tmpDir;
    }
    
    /** Sets the temporary directory to use. **/
    public void setTmpDir(File tmpDir) {
        this.tmpDir = tmpDir;
    }
    
    /** Gets the -source **/
    private String source = null;
    
    public String getSource() {
        return source;
    }
    
    /** Sets the -source **/
    public void setSource(String version) {
        this.source = version;
    }
    
    /********************  -o option **********************/
    private File outWarFile = null;
    
    /** Gets the output war file. */
    public File getOutWarFile() {
        return outWarFile;
    }
    
    /** Sets the output war file. */
    public void setOutWarFile(File outWarFile) {
        this.outWarFile = outWarFile;
    }
    
    /*************************  -verbose option *************************/
    protected boolean verbose = false;
    
    /** Gets the "verbose" flag. */
    public boolean getVerbose() {
        return verbose;
    }
    
    /** Sets the "verbose" flag. */
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
    
    /*************************  -version option *************************/
    protected boolean version = false;
    
    /** Gets the "version" flag. */
    public boolean getVersion() {
        return version;
    }
    
    /** Sets the "version" flag. */
    public void setVersion(boolean version) {
        this.version = version;
    }
    
    /*************************  war file *************************/
    private File warFile = null;
    
    /** Gets the input war file. */
    public File getInWarFile() {
        return warFile;
    }
    
    /** Sets the input war file. */
    public void setInWarFile(File warFile) {
        this.warFile = warFile;
    }
    
    /***********************  include ant runtime **********************/
    /** not sure if these methods are needed */
    private boolean includeAntRuntime = false;
    
    /**
     * Include ant's own classpath in this task's classpath?
     */
    public void setIncludeantruntime(boolean include) {
        includeAntRuntime = include;
    }
    
    /**
     * Gets whether or not the ant classpath is to be included in the
     * task's classpath.
     */
    public boolean getIncludeantruntime() {
        return includeAntRuntime;
    }
    
    /***********************  include java runtime **********************/
    /** not sure if these methods are needed */
    private boolean includeJavaRuntime = false;
    
    /**
     * Sets whether or not to include the java runtime libraries to this
     * task's classpath.
     */
    public void setIncludejavaruntime(boolean include) {
        includeJavaRuntime = include;
    }
    
    /**
     * Gets whether or not the java runtime should be included in this
     * task's classpath.
     */
    public boolean getIncludejavaruntime() {
        return includeJavaRuntime;
    }
    
    /** not sure if this method is needed */
    private Path generateCompileClasspath() {
        Path classpath = new Path(getProject());
        
        if (getClasspath() == null) {
            if (getIncludeantruntime()) {
                classpath.addExisting(Path.systemClasspath);
            }
        } else {
            if (getIncludeantruntime()) {
                classpath.addExisting(
                    getClasspath().concatSystemClasspath("last"));
            } else {
                classpath.addExisting(
                    getClasspath().concatSystemClasspath("ignore"));
            }
        }
        
        if (getIncludejavaruntime()) {
            
            // JDK > 1.1 seems to set java.home to the JRE directory.
            classpath.addExisting(new Path(null,
                System.getProperty("java.home") +
                File.separator + "lib" +
                File.separator + "rt.jar"));
            
            // Just keep the old version as well and let addExistingToPath
            // sort it out.
            classpath.addExisting(new Path(null,
                System.getProperty("java.home") +
                    File.separator + "jre" +
                    File.separator + "lib" +
                    File.separator + "rt.jar"));
        }
        
        return classpath;
    }

    private Commandline setupWsdeployCommand() {
        Commandline cmd = setupWsdeployArgs();
        
        // classpath option (cp option just uses classpath option)
        // Path classpath = generateCompileClasspath();
        Path classpath = getClasspath();
        
        if (classpath != null && !classpath.toString().equals("")) {
            cmd.createArgument().setValue("-classpath");
            cmd.createArgument().setPath(classpath);
        }
        return cmd;
    }
    
    private Commandline setupWsdeployForkCommand() {
        CommandlineJava forkCmd = new CommandlineJava();
        
        /*
         * classpath option (cp option just uses classpath option)
         * Path classpath = generateCompileClasspath();
         */
        Path classpath = getClasspath();
        forkCmd.createClasspath(getProject()).append(classpath);
        forkCmd.setClassname("com.sun.xml.rpc.tools.wsdeploy.Main");
        if (null != getJvmargs()) {       
            forkCmd.createVmArgument().setLine(getJvmargs());
        }
        
        Commandline cmd = setupWsdeployArgs();
        cmd.createArgument(true).setLine(forkCmd.toString());
        return cmd;
    }
    
    private Commandline setupWsdeployArgs() {    
        Commandline cmd = new Commandline();

        // keep option
        if (getKeep()) {
            cmd.createArgument().setValue("-keep");
        }
        
        // features option (f option just uses features option)
        if (getFeatures() != null && !getFeatures().equals("")) {
            cmd.createArgument().setValue("-features:" + getFeatures());
        }
        
        // tmpdir option
        if (null != getTmpDir() && !getTmpDir().equals("")) {
            cmd.createArgument().setValue("-tmpdir");
            cmd.createArgument().setFile(getTmpDir());
        }
        
        // -source option
        if (getSource() != null && !getSource().equals("")) {
            cmd.createArgument().setValue("-source");
            cmd.createArgument().setValue(getSource());
        }
        
        // o option
        if (getOutWarFile() != null && !getOutWarFile().equals("")) {
            cmd.createArgument().setValue("-o");
            cmd.createArgument().setFile(getOutWarFile());
        }
        
        // verbose option
        if (getVerbose()) {
            cmd.createArgument().setValue("-verbose");
        }
        
        // version option
        if (getVersion()) {
            cmd.createArgument().setValue("-version");
        }
        
        // input war file
        if (warFile != null) {
            cmd.createArgument().setValue(warFile.toString());
        }
        
        return cmd;
    }   
    
    /** Called by the project to let the task do it's work **/
    public void execute() throws BuildException {
        if (!getVersion() && (warFile == null || !warFile.exists())) {
            throw new BuildException(
                "wsdeploy input war file does not exist!", location);
        }
        
        /* Create an instance of the rmic, redirecting output to
         * the project log
         */
        LogOutputStream logstr = null;
        boolean ok = false;
        try {
            Commandline cmd = fork ?
                setupWsdeployForkCommand() : setupWsdeployCommand();
            if (verbose) {
                log("command line: "+cmd.toString());
            }
            if (fork) {
                int status = run(cmd.getCommandline());
                ok = (status == 0) ? true : false;
            } else {
                logstr = new LogOutputStream(this, Project.MSG_WARN);
                DeployTool depTool = new DeployTool(logstr, "wsdeploy");
                ok = depTool.run(cmd.getArguments());
            }
            if (!ok) {
                if (!verbose) {
                    log("Command invoked: "+cmd.toString());
                }
                throw new BuildException("wsdeploy failed", location);
            }
        } catch (Exception ex) {
            if (ex instanceof BuildException) {
                throw (BuildException)ex;
            } else {
                throw new BuildException("Error starting wsdeploy: ", ex,
                getLocation());
            }
        } finally {
            try {
                if (logstr != null) {
                    logstr.close();
                }
            } catch (IOException e) {
                throw new BuildException(e);
            }
        }
    }
    
    /**
     * Executes the given classname with the given arguments in a separate VM.
     */
    private int run(String[] command) throws BuildException {
        FileOutputStream fos = null;
        Execute exe = null;
        LogStreamHandler logstr = new LogStreamHandler(this,
            Project.MSG_INFO, Project.MSG_WARN);
        exe = new Execute(logstr);
        exe.setAntRun(project);
        exe.setCommandline(command);
        try {
            int rc = exe.execute();
            if (exe.killedProcess()) {
                log("Timeout: killed the sub-process:wsdeploy", Project.MSG_WARN);
            }
            return rc;
        } catch (IOException e) {
            throw new BuildException(e, location);
        }
    }
}
