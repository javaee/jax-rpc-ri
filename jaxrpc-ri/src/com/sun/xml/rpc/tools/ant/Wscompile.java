/**
 * $Id: Wscompile.java,v 1.3 2007-07-13 23:36:35 ofung Exp $
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
import java.net.URL;
import java.net.MalformedURLException;

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

import com.sun.xml.rpc.tools.wscompile.CompileTool;

/**
 * wscompile task for use with the JAXRPC project.
 *
 */
public class Wscompile extends MatchingTask {
    
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
    
    /*************************  -d option *************************/
    private File baseDir = null;
    
    /** Gets the base directory to output generated class. **/
    public File getBase() {
        return this.baseDir;
    }
    
    /** Sets the base directory to output generated class. **/
    public void setBase(File base) {
        this.baseDir = base;
    }
    
    /*************************  -define option *************************/
    protected boolean define = false;
    
    /**
     * Get the value of the "define" flag.
     */
    public boolean getDefine() {
        return define;
    }
    
    /**
     * Set the value of the "define" flag.
     */
    public void setDefine(boolean define) {
        this.define = define;
    }
    
    /********************  -f option **********************/
    /** Gets the values for the "f" flag. **/
    public String getF() {
        return getFeatures();
    }
    
    /** Sets the values for the "f" flag. **/
    public void setF(String features) {
        setFeatures(features);
    }
    
    /********************  -features option **********************/
    /*** values for features include: datahandleronly, explicitcontext,
     * infix=<name>, nodatabinding, noencodedtypes, nomultirefs,
     * novalidation, searchschema, serializeinterface, documentliteral,
     * wsi,rpcliteral, donotoverride
     ***/
    private String features = null;
    
    /** Gets the values for the "features" flag. **/
    public String getFeatures() {
        return features;
    }
    
    /** Sets the values for the "features" flag. **/
    public void setFeatures(String features) {
        this.features = features;
    }
    
    /*************************  -g option *************************/
    private boolean debug = false;
    
    /** Gets the debug flag. **/
    public boolean getDebug() {
        return debug;
    }
    
    /** Sets the debug flag. **/
    public void setDebug(boolean debug) {
        this.debug = debug;
    }
    
    /*************************  -gen option *************************/
    /** Gets the "gen" flag. */
    public boolean getGen() {
        return getClient();
    }
    
    /** Sets the "gen" flag. */
    public void setGen(boolean client) {
        setClient(client);
    }
    
    /*************************  -gen:client option *************************/
    protected boolean client = false;
    
    /** Gets the "gen:client" flag. **/
    public boolean getClient() {
        return client;
    }
    
    /** Sets the "gen:client" flag. **/
    public void setClient(boolean client) {
        this.client = client;
    }
    
    /*************************  -gen:server option *************************/
    protected boolean server = false;
    
    /** Gets the "gen:server" flag. **/
    public boolean getServer() {
        return server;
    }
    
    /** Sets the "gen:server" flag. **/
    public void setServer(boolean server) {
        this.server = server;
    }
    /*************************  -gen:both option *************************/
    protected boolean both = false;
    
    /**
     * Get the value of the "gen:both" flag.
     */
    public boolean getBoth() {
        return both;
    }
    
    /**
     * Set the value of the "gen:both" flag.
     *
     * @param both true if you want both client and server-side stuff
     *             to be generated.
     */
    public void setBoth(boolean both) {
        this.both = both;
    }
    
    /********************  -httpproxy option **********************/
    private String HTTPProxyURL = null;
    private URL proxyURL = null;
    
    /** Gets the String "httpproxy" flag. **/
    public String getHTTPProxy() {
        return HTTPProxyURL;
    }
    
    /** Sets the String "httpproxy" flag.
     * This value can either specify the http protocol or not.
     **/
    public void setHTTPProxy(String HTTPProxy) {
        if (HTTPProxy != null && !HTTPProxy.equals("")) {
            if (HTTPProxy.startsWith("http://")) {
                this.HTTPProxyURL = HTTPProxy;
            } else {
                this.HTTPProxyURL = "http://" + HTTPProxy;
            }
            
            try {
                URL proxyServer = new URL(this.HTTPProxyURL);
                setProxyServer(proxyServer);
            } catch (MalformedURLException e) {
                throw new Error("Invalid HTTP URL specified: " +
                    this.HTTPProxyURL);
            }
        }
    }
    
    /** Gets the URL for "httpproxy" flag. **/
    public URL getProxyServer() {
        return proxyURL;
    }
    
    /** Sets the URL for "httpproxy" flag. **/
    public void setProxyServer(URL proxyURL) {
        this.proxyURL = proxyURL;
    }
    
    /********************  -import option **********************/
    protected boolean genImport = false;
    
    /** Gets the "import" flag. **/
    public boolean getImport() {
        return genImport;
    }
    
    /** Sets the "import" flag. **/
    public void setImport(boolean genImport) {
        this.genImport = genImport;
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
    
    /** Gets the "keep" flag. **/
    public boolean getKeep() {
        return keep;
    }
    
    /** Sets the "keep" flag. **/
    public void setKeep(boolean keep) {
        this.keep = keep;
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
    
    /********************  -model option **********************/
    private File modelFile = null;
    
    /** Gets the "model" file. **/
    public File getModel() {
        return modelFile;
    }
    
    /** Sets the "model" file. **/
    public void setModel(File modelFile) {
        this.modelFile = modelFile;
    }
    
    /********************  -mapping option **********************/
    private File mappingFile = null;
    
    /** Gets the "mapping" file. **/
    public File getMapping() {
        return mappingFile;
    }
    
    /** Sets the "mapping" file. **/
    public void setMapping(File mappingFile) {
        this.mappingFile = mappingFile;
    }
    
    /********************  -security option **********************/
    private File securityFile = null;
    
    /** Gets the "security" file. **/
    public File getSecurity() {
        return securityFile;
    }
    
    /** Sets the "security" file. **/
    public void setSecurity(File securityFile) {
        this.securityFile = securityFile;
    }
    
    /*************************  -nd option *************************/
    private File nonClassDir = null;
    
    /** Gets the directory for non-class generated files. **/
    public File getNonClassDir() {
        return this.nonClassDir;
    }
    
    /** Sets the directory for non-class generated files. **/
    public void setNonClassDir(File nonClassDir) {
        this.nonClassDir = nonClassDir;
    }
    
    /*************************  -O option *************************/
    private boolean optimize = false;
    
    /** Gets the optimize flag. **/
    public boolean getOptimize() {
        return optimize;
    }
    
    /** Sets the optimize flag. **/
    public void setOptimize(boolean optimize) {
        this.optimize = optimize;
    }
    
    /*************************  -s option *************************/
    private File sourceBase;
    
    /** Sets the directory to place generated source java files. **/
    public void setSourceBase(File sourceBase) {
        keep = true;
        this.sourceBase = sourceBase;
    }
    
    /** Gets the directory to place generated source java files. **/
    public File getSourceBase() {
        return sourceBase;
    }
    
    /*************************  -verbose option *************************/
    protected boolean verbose = false;
    
    /** Gets the "verbose" flag. **/
    public boolean getVerbose() {
        return verbose;
    }
    
    /** Sets the "verbose" flag. **/
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
    
    /*************************  -version option *************************/
    protected boolean version = false;
    
    /** Gets the "version" flag. **/
    public boolean getVersion() {
        return version;
    }
    
    /** Sets the "version" flag. **/
    public void setVersion(boolean version) {
        this.version = version;
    }
    
    /********************  -Xprintstacktrace option **********************/
    protected boolean xPrintStackTrace = false;
    
    /** Gets the "Xprintstacktrace" flag. **/
    public boolean getXPrintStackTrace() {
        return xPrintStackTrace;
    }
    
    /** Sets the "Xprintstacktrace" flag. **/
    public void setXPrintStackTrace(boolean xPrintStackTrace) {
        this.xPrintStackTrace = xPrintStackTrace;
    }
    
    /********************  -Xserializable option **********************/
    protected boolean xSerializable = false;
    
    /** Gets the "Xserializable" flag. **/
    public boolean getXSerializable() {
        return xSerializable;
    }
    
    /** Sets the "Xserializable" flag. **/
    public void setXSerializable(boolean xSerializable) {
        this.xSerializable = xSerializable;
    }
    
    /********************  -Xdebugmodel option **********************/
    private File xDebugModel = null;
    
    /** Gets the "Xdebugmodel" file. **/
    public File getXDebugModel() {
        return xDebugModel;
    }
    
    /** Sets the "Xdebugmodel" file. **/
    public void setXDebugModel(File xDebugModel) {
        this.xDebugModel = xDebugModel;
    }
    
    /*************************  config file *************************/
    private File config = null;
    
    /** Gets the configuration file. **/
    public File getConfig() {
        return config;
    }
    
    /** Sets the configuration file. **/
    public void setConfig(File config) {
        this.config = config;
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
            
            /* Just keep the old version as well and let addExistingToPath
             * sort it out.
             */
            classpath.addExisting(new Path(null,
                System.getProperty("java.home") +
                File.separator + "jre" +
                File.separator + "lib" +
                File.separator + "rt.jar"));
        }
        
        return classpath;
    }
    
    private Commandline setupWscompileCommand() {
        Commandline cmd = setupWscompileArgs();
        
        // classpath option (cp option just uses classpath option)
        // Path classpath = generateCompileClasspath();
        Path classpath = getClasspath();
        
        if (classpath != null && !classpath.toString().equals("")) {
            cmd.createArgument().setValue("-classpath");
            cmd.createArgument().setPath(classpath);
        }
        return cmd;
    }
    
    private Commandline setupWscompileForkCommand() {
        CommandlineJava forkCmd = new CommandlineJava();
        
        /*
         * classpath option (cp option just uses classpath option)
         * Path classpath = generateCompileClasspath();
         */
        Path classpath = getClasspath();
        forkCmd.createClasspath(getProject()).append(classpath);
        forkCmd.setClassname("com.sun.xml.rpc.tools.wscompile.Main");
        if (null != getJvmargs()) {       
            forkCmd.createVmArgument().setLine(getJvmargs());
        }
        
        Commandline cmd = setupWscompileArgs();
        cmd.createArgument(true).setLine(forkCmd.toString());
        return cmd;
    }
    
    private Commandline setupWscompileArgs() {
        Commandline cmd = new Commandline();
        
        // d option
        if (null != getBase() && !getBase().equals("")) {
            cmd.createArgument().setValue("-d");
            cmd.createArgument().setFile(getBase());
        }
        
        // define option
        if (getDefine()) {
            cmd.createArgument().setValue("-define");
        }
        
        // -source option
        if (getSource() != null && !getSource().equals("")) {
            cmd.createArgument().setValue("-source");
            cmd.createArgument().setValue(getSource());
        }
        
        // features option (f option just uses features option)
        if (getFeatures() != null && !getFeatures().equals("")) {
            cmd.createArgument().setValue("-features:" + getFeatures());
        }
        
        // g option
        if (getDebug()) {
            cmd.createArgument().setValue("-g");
        }
        
        /* gen (which is same as gen:client), gen:client, gen:server,
         * and gen:both options
         */
        if (getBoth()) {
            cmd.createArgument().setValue("-gen:both");
        } else if (getClient()) {
            cmd.createArgument().setValue("-gen:client");
        } else if (getServer()) {
            cmd.createArgument().setValue("-gen:server");
        }
        
        // httpproxy option
        if (getProxyServer() != null) {
            String host = getProxyServer().getHost();
            if (host != null && !host.equals("")) {
                String proxyVal = "-httpproxy:" + host;
                if (getProxyServer().getPort() != -1) {
                    proxyVal += ":" + getProxyServer().getPort();
                }
                
                cmd.createArgument().setValue(proxyVal);
            }
        }
        
        // import option
        if (getImport()) {
            cmd.createArgument().setValue("-import");
        }
        
        // keep option
        if (getKeep()) {
            cmd.createArgument().setValue("-keep");
        }
        
        // model option
        if (getModel() != null && !getModel().equals("")) {
            cmd.createArgument().setValue("-model");
            cmd.createArgument().setFile(getModel());
        }
        
        // mapping option
        if (getMapping() != null && !getMapping().equals("")) {
            cmd.createArgument().setValue("-mapping");
            cmd.createArgument().setFile(getMapping());
        }
        
        // security option
        if (getSecurity() != null && !getSecurity().equals("")) {
            cmd.createArgument().setValue("-security");
            cmd.createArgument().setFile(getSecurity());
        }
        
        // nd option
        if (null != getNonClassDir() && !getNonClassDir().equals("")) {
            cmd.createArgument().setValue("-nd");
            cmd.createArgument().setFile(getNonClassDir());
        }
        
        // optimize option
        if (getOptimize()) {
            cmd.createArgument().setValue("-O");
        }
        
        // s option
        if (null != getSourceBase() && !getSourceBase().equals("")) {
            cmd.createArgument().setValue("-s");
            cmd.createArgument().setFile(getSourceBase());
        }
        
        // verbose option
        if (getVerbose()) {
            cmd.createArgument().setValue("-verbose");
        }
        
        // version option
        if (getVersion()) {
            cmd.createArgument().setValue("-version");
        }
        
        // Xprintstacktrace option
        if (getXPrintStackTrace()) {
            cmd.createArgument().setValue("-Xprintstacktrace");
        }
        
        // Xserializable option
        if (getXSerializable()) {
            cmd.createArgument().setValue("-Xserializable");
        }
        
        // Xdebugmodel option
        if (getXDebugModel() != null && !getXDebugModel().equals("")) {
            cmd.createArgument().setValue("-Xdebugmodel:" + getXDebugModel());
        }
        
        // config file
        if (getConfig() != null) {
            cmd.createArgument().setValue(getConfig().toString());
        }
        
        return cmd;
    }
    
    
    /** Called by the project to let the task do it's work **/
    public void execute() throws BuildException {
        if (!getVersion() && (getConfig() == null || !getConfig().exists())) {
            throw new BuildException("wscompile config file does not exist!",
                location);
        }
        
        /* Create an instance of the rmic, redirecting output to
         * the project log
         */
        LogOutputStream logstr = null;
        boolean ok = false;
        try {
            Commandline cmd = fork ?
                setupWscompileForkCommand() : setupWscompileCommand();
            if (verbose) {
                log("command line: "+"wscompile "+cmd.toString());
            }
            if (fork) {
                int status = run(cmd.getCommandline());
                ok = (status == 0) ? true : false;
            } else {
                logstr = new LogOutputStream(this, Project.MSG_WARN);
                CompileTool compTool = new CompileTool(logstr, "wscompile");
                ok = compTool.run(cmd.getArguments());
            }
            if (!ok) {
                if (!verbose) {
                    log("Command invoked: "+"wscompile "+cmd.toString());
                }
                throw new BuildException("wscompile failed", location);
            }
        } catch (Exception ex) {
            if (ex instanceof BuildException) {
                throw (BuildException)ex;
            } else {
                throw new BuildException("Error starting wscompile: ", ex,
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
                log("Timeout: killed the sub-process", Project.MSG_WARN);
            }
            return rc;
        } catch (IOException e) {
            throw new BuildException(e, location);
        }
    }
    
}
