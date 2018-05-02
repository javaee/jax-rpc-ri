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


package com.sun.xml.rpc.tools.wsdeploy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.sun.xml.rpc.processor.ProcessorNotificationListener;
import com.sun.xml.rpc.util.ToolBase;
import com.sun.xml.rpc.util.Version;
import com.sun.xml.rpc.util.VersionUtil;
import com.sun.xml.rpc.util.localization.*;
import com.sun.xml.rpc.util.xml.NullEntityResolver;

/**
 *
 * @author JAX-RPC Development Team
 */
public class DeployTool extends ToolBase
    implements ProcessorNotificationListener {
    
    public DeployTool(OutputStream out, String program) {
        super(out, program);
        props = new Properties();
    }
    
    protected void initialize() {
        super.initialize();
    }
    
    protected boolean parseArguments(String[] args) {
        for (int i = 0 ; i < args.length ; i++) {
            if (args[i].equals("")) {
                args[i] = null;
            } else if (args[i].startsWith("-verbose")) {
                verbose = true;
                props.setProperty("verbose", "true");
                args[i] = null;
            } else if (args[i].equals("-version")) {
                report(getMessage("wsdeploy.version",
                    Version.PRODUCT_NAME,
                    Version.VERSION_NUMBER,
                    Version.BUILD_NUMBER));
                args[i] = null;
                doNothing = true;
                return true;
            } else if (args[i].equals("-keep")) {
                keepTemporaryFiles = true;
                props.setProperty("keepGenerated", "true");
                args[i] = null;
            } else if (args[i].equals("-o")) {
                if ((i + 1) < args.length) {
                    if (destFile != null) {
                        onError(getMessage("wsdeploy.duplicateOption", "-o"));
                        usage();
                        return false;
                    }
                    args[i] = null;
                    destFile = new File(args[++i]);
                    args[i] = null;
                    if (destFile.isDirectory() ||
                        (destFile.getParentFile() != null &&
                        !destFile.getParentFile().exists())) {
                            
                        onError(getMessage("wsdeploy.invalidOutputFile",
                            destFile.getPath()));
                        usage();
                        return false;
                    }
                } else {
                    onError(getMessage("wsdeploy.missingOptionArgument",
                        "-model"));
                    usage();
                    return false;
                }
            } else if (args[i].equals("-classpath") || args[i].equals("-cp")) {
                if ((i + 1) < args.length) {
                    if (userClasspath != null) {
                        onError(getMessage("wsdeploy.duplicateOption",
                            args[i]));
                        usage();
                        return false;
                    }
                    args[i] = null;
                    userClasspath = args[++i];
                    args[i] = null;
                }
            } else if (args[i].equals("-tmpdir")) {
                if ((i + 1) < args.length) {
                    if (tmpdirBase != null) {
                        onError(getMessage("wsdeploy.duplicateOption",
                            "-tmpdir"));
                        usage();
                        return false;
                    }
                    args[i] = null;
                    tmpdirBase = new File(args[++i]);
                    args[i] = null;
                    if (!tmpdirBase.exists()) {
                        onError(getMessage("wsdeploy.noSuchDirectory",
                            tmpdirBase.getPath()));
                        usage();
                        return false;
                    }
                } else {
                    onError(getMessage("wsdeploy.missingOptionArgument",
                        "-tmpdir"));
                    usage();
                    return false;
                }
            } else if (args[i].equals("-source")) {
                if ((i + 1) < args.length) {
                    if (target != null) {
                        onError(getMessage("wsdeploy.duplicateOption",
                            "-source"));
                        usage();
                        return false;
                    }
                    args[i] = null;
                    target = new String(args[++i]);
                    args[i] = null;
                    
                } else {
                    onError(getMessage("wsdeploy.missingOptionArgument",
                        "-source"));
                    usage();
                    return false;
                }
                if (target.length() == 0) {
                    onError(getMessage("wsdeploy.invalidOption", args[i]));
                    usage();
                    return false;
                }
                if (!VersionUtil.isValidVersion(target)) {
                    onError(getMessage("wsdeploy.invalidTargetVersion",
                        target));
                    usage();
                    return false;
                }
            }
        }
        
        for (int i = 0; i < args.length; i++) {
            if (args[i] != null) {
                if (args[i].startsWith("-")) {
                    onError(getMessage("wsdeploy.invalidOption", args[i]));
                    usage();
                    return false;
                } else if (sourceFile != null) {
                    onError(getMessage("wsdeploy.multipleWarFiles", args[i]));
                    usage();
                    return false;
                }
                
                sourceFile = new File(args[i]);
                args[i] = null;
                if (!sourceFile.exists()) {
                    onError(getMessage("wsdeploy.fileNotFound",
                        sourceFile.getPath()));
                    usage();
                    return false;
                }
            }
        }
        
        if (sourceFile == null) {
            onError(getMessage("wsdeploy.missingWarFile"));
            usage();
            return false;
        }
        
        if (destFile == null) {
            onError(getMessage("wsdeploy.missingDestinationWarFile"));
            usage();
            return false;
        }
        
        if (sourceFile.equals(destFile)) {
            onError(getMessage("wsdeploy.sourceDestinationConflict"));
            usage();
            return false;
        }
        return true;
    }
    
    protected void usage() {
        report(getMessage("wsdeploy.usage", program));
    }
    
    ////////
    
    public void run() throws Exception {
        if (doNothing) {
            return;
        }
        
        if (tmpdirBase == null) {
            tmpdirBase = new File(System.getProperty("java.io.tmpdir"));
        }
        
        try {
            tmpdir = createTemporaryDirectory();
            if (verbose) {
                onInfo(getMessage("wsdeploy.info.createdTempDir",
                    tmpdir.getAbsolutePath()));
            }
            expandSourceFile();
            DeploymentDescriptorParser parser =
                new DeploymentDescriptorParser();
            File dd = translateSourceAppFileName(JAXRPC_RI_DD);
            WebServicesInfo webServicesInfo =
                parser.parse(new FileInputStream(dd));
            dd.renameTo(translateSourceAppFileName(JAXRPC_RI_DD_PROCESSED));
            process(webServicesInfo);
            defineServletsAndListeners(webServicesInfo);
            createRuntimeDescriptor(webServicesInfo);
            if (destFile != null) {
                packageDestinationFile();
            }
        } finally {
            if (tmpdir != null && !keepTemporaryFiles) {
                removeDirectory(tmpdir);
                if (verbose) {
                    onInfo(getMessage("wsdeploy.info.removedTempDir",
                        tmpdir.getAbsolutePath()));
                }
                tmpdir = null;
            }
        }
    }
    
    protected void process(WebServicesInfo webServicesInfo) throws Exception {
        
        /*
         * Here all the Endpoints which refer to the same model, in a sense
         * refering to the same service with potentially multiple ports, are
         * grouped together. This fascillates the ability to handle multiple
         * endpoints which refer to the same interface but diffrent ports
         */
        Hashtable hashtable = new Hashtable();
        ArrayList endpointClientList = null;
        ArrayList clientList = null;
        ArrayList endpointList = null;
        EndpointInfo endpointInfo = null;
        EndpointClientInfo endpointClient = null;
        Iterator iter = webServicesInfo.getEndpoints().values().iterator();
        Iterator iterClient =
            webServicesInfo.getEndpointClients().values().iterator();
        
        while ( iter.hasNext() ) {
            endpointInfo = (EndpointInfo) iter.next();
            if( endpointInfo.getModel() != null ) {
                if( hashtable.containsKey(endpointInfo.getModel()) ) {
                    endpointClientList = (ArrayList) hashtable.get(
                        endpointInfo.getModel() );
                    endpointList = (ArrayList) endpointClientList.get(
                        Constants.ENDPOINTLIST_INDEX);
                } else {
                    endpointClientList = new ArrayList();
                    endpointList  = new ArrayList();
                    clientList = new ArrayList();
                }
                endpointList.add(endpointInfo);
                endpointClientList.add(Constants.ENDPOINTLIST_INDEX,
                    endpointList);
                endpointClientList.add(Constants.ENDPOINTLIST_INDEX+1,
                    clientList);
                hashtable.put(endpointInfo.getModel(), endpointClientList);
            } else {
                process( endpointInfo, webServicesInfo );
            }
        }
        
        while( iterClient.hasNext() ) {
            endpointClient = (EndpointClientInfo)iterClient.next();
            if( endpointClient.getModel() != null ) {
                if( hashtable.containsKey(endpointClient.getModel()) ) {
                    endpointClientList = (ArrayList) hashtable.get(
                        endpointClient.getModel() );
                    clientList = (ArrayList) endpointClientList.get(
                        Constants.ENDPOINTLIST_INDEX+1);
                    clientList.add( endpointClient );
                    endpointClientList.add(Constants.ENDPOINTLIST_INDEX+1,
                        clientList);
                } else {
                    System.out.println("\n BIG PROBLEM");
                }
            }
        }
        
        // getting an enumeration of keys in the hashtable
        Enumeration e = hashtable.keys();
        String key = null;
        
        while (e.hasMoreElements()) {
            key = (String) e.nextElement();
            endpointClientList = (ArrayList) hashtable.get( key );
            ArrayList list = (ArrayList) endpointClientList.get(
                Constants.ENDPOINTLIST_INDEX);
            EndpointCompileTool compiler = new EndpointCompileTool(out,
                program, webServicesInfo, list,
                tmpdir, target, props,
                userClasspath, this);
            compiler.run();
            
            list = (ArrayList) endpointClientList.get(
                Constants.ENDPOINTLIST_INDEX+1);
            if( !list.isEmpty() ) {
                EndpointClientCompileTool compilerClient =
                    new EndpointClientCompileTool(out, program,
                        webServicesInfo, list, tmpdir,
                        target, userClasspath, this);
                compilerClient.run();
                clientSpecified = true;
            }
        }
    }
    
    protected void process(EndpointInfo endpointInfo,
        WebServicesInfo webServicesInfo) throws Exception {
            
        if (verbose) {
            onInfo(getMessage("wsdeploy.info.processingEndpoint",
                endpointInfo.getName()));
        }
        
        EndpointCompileTool compiler = new EndpointCompileTool(out, program,
            endpointInfo, webServicesInfo, tmpdir,
            target, props, userClasspath, this);
        compiler.run();
        endpointInfo.setRuntimeDeployed(compiler.wasSuccessful());
    }
    
    /*
     * The previous version of thie method created a DOM tree and style
     * sheet. This version skips a step by simply adding the servlet
     * information to the tree and saving it.
     */
    protected void defineServletsAndListeners(WebServicesInfo webServicesInfo)
        throws Exception {

        File webappdd = translateSourceAppFileName(WEBAPP_DD);
        File webappddExisting = translateSourceAppFileName(WEBAPP_DD_PROCESSED);
        webappdd.renameTo(webappddExisting);
        webappdd.delete();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        builder.setEntityResolver(new NullEntityResolver());
        Document document = builder.parse(
            new InputSource(new FileInputStream(webappddExisting)));
        String publicId =
            "-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN";
        String systemId = "http://java.sun.com/j2ee/dtds/web-app_2_3.dtd";
        DocumentType doctype = document.getDoctype();
        
        // if true later, write out DTD in transformation
        boolean isDTDType = false;
        if (doctype != null &&
            doctype.getPublicId() != null &&
                doctype.getSystemId() != null) {
                    
            // be defensive here, although doc type declarations are required in deployment descriptors
            publicId = doctype.getPublicId();
            systemId = doctype.getSystemId();
            isDTDType = true;
        }

        // correspondes to <web-app>
        Element webAppElement = document.getDocumentElement();
        
        // look backwards through list for append point
        String [] skipNodeNames = {"display-name", "description",
            "distributable", "context-param", "filter",
            "filter-mapping", "listener"};
        Node currentNode = null;
        for (int i=skipNodeNames.length-1; i>=0; i--) {
            NodeList testNodes =
                webAppElement.getElementsByTagName(skipNodeNames[i]);
            if (testNodes.getLength() > 0) {
                currentNode = testNodes.item(testNodes.getLength()-1);
                break;
            }
        }

        // handle case where original file didn't have expected elements
        if (currentNode == null) {
            currentNode = webAppElement.getChildNodes().item(0);
        }
        
        currentNode = movePastText(currentNode);
        
        // add listener after current node
        currentNode = webAppElement.insertBefore(
            document.createElement("listener"), 
            currentNode.getNextSibling());
        currentNode.appendChild(
            document.createElement("listener-class")).appendChild(
            document.createTextNode(
            "com.sun.xml.rpc.server.http.JAXRPCContextListener"));
                
        // if 'servlet' node exists skip it
        currentNode = currentNode.getNextSibling();
        if (currentNode.getNodeType() == Node.ELEMENT_NODE &&
                currentNode.getNodeName().equals("servlet")) {
            currentNode = currentNode.getNextSibling();
            currentNode = movePastText(currentNode);
        }

        // insert endpoint info -- current node now insert point
        for (Iterator iter = webServicesInfo.getEndpoints().values().iterator();
            iter.hasNext();) {
                
            EndpointInfo endpointInfo = (EndpointInfo) iter.next();
            if (!endpointInfo.isRuntimeDeployed()) {
                continue;
            }
            Element servletElement = document.createElement("servlet");
            servletElement.appendChild(document.createElement("servlet-name")).
                    appendChild(document.createTextNode(endpointInfo.getName()));
            if (isDTDType) {
                servletElement.appendChild(document.createElement("display-name")).
                        appendChild(document.createTextNode(endpointInfo.getName()));
                servletElement.appendChild(document.createElement("description")).
                        appendChild(document.createTextNode("JAX-RPC endpoint - " +
                        endpointInfo.getName()));
            }
            servletElement.appendChild(document.createElement("servlet-class")).
                    appendChild(document.createTextNode(
                    "com.sun.xml.rpc.server.http.JAXRPCServlet"));
            servletElement.appendChild(
                    document.createElement("load-on-startup")).appendChild(
                    document.createTextNode("1"));
            webAppElement.insertBefore(servletElement, currentNode);
        }
        
        // insert servlet-mapping info -- current node still insert point
        for (Iterator iter = webServicesInfo.getEndpoints().values().iterator();
            iter.hasNext();) {
                
            EndpointInfo endpointInfo = (EndpointInfo) iter.next();
            if (!endpointInfo.isRuntimeDeployed()) {
                continue;
            }
            String urlPattern = null;
            EndpointMappingInfo mappingInfo = (EndpointMappingInfo)
                webServicesInfo.getEndpointMappings().get(
                    endpointInfo.getName());
            if (mappingInfo != null) {
                urlPattern = mappingInfo.getUrlPattern();
            } else {
                urlPattern = webServicesInfo.getUrlPatternBase() +
                    "/" + endpointInfo.getName();
            }
            endpointInfo.setRuntimeUrlPattern(urlPattern);
            Element mappingElement = document.createElement("servlet-mapping");
            mappingElement.appendChild(document.createElement("servlet-name")).
                    appendChild(document.createTextNode(endpointInfo.getName()));
            mappingElement.appendChild(document.createElement("url-pattern")).
                    appendChild(document.createTextNode(urlPattern));
            webAppElement.insertBefore(mappingElement, currentNode);
        }
        
        TransformerFactory factory2 = TransformerFactory.newInstance();
        Transformer transformer = null;
        transformer = factory2.newTransformer();
        transformer.setOutputProperty("indent", "yes");
        if (isDTDType) {
            transformer.setOutputProperty("doctype-public", publicId);
            transformer.setOutputProperty("doctype-system", systemId);
        }

        transformer.transform(new DOMSource(document),
            new StreamResult(webappdd));
    }
    
    // used to skip over whitespace nodes. null if insert point at end of list
    private Node movePastText(Node node) {
        if (node != null &&
                node.getNextSibling() != null &&
                node.getNextSibling().getNodeType() == Node.TEXT_NODE) {
            return node.getNextSibling();
        }
        return node;
    }

    protected void createRuntimeDescriptor(WebServicesInfo webServicesInfo)
        throws Exception {
            
        String descriptorContents =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<endpoints xmlns='http://java.sun.com/xml/ns/jax-rpc/ri/runtime' version='1.0'>\n";
        
        for (Iterator iter = webServicesInfo.getEndpoints().values().iterator();
            iter.hasNext();) {
                
            EndpointInfo endpointInfo = (EndpointInfo) iter.next();
            if (!endpointInfo.isRuntimeDeployed()) {
                continue;
            }
            
            descriptorContents +=
                "  <endpoint\n" +
                "    name='" + endpointInfo.getName() + "'\n" +
                "    interface='" + endpointInfo.getInterface() + "'\n" +
                "    implementation='" + endpointInfo.getImplementation() + "'\n" +
                "    tie='" + endpointInfo.getRuntimeTie() + "'\n" +
                "    model='" + endpointInfo.getRuntimeModel() + "'\n";
            if (endpointInfo.getRuntimeWSDL() != null) {
                descriptorContents += "    wsdl='" +
                    endpointInfo.getRuntimeWSDL() + "'\n";
            }
            
            descriptorContents +=
                "    service='" +
                endpointInfo.getRuntimeServiceName().toString() +
                "'\n" +
                "    port='" +
                endpointInfo.getRuntimePortName().toString() +
                "'\n" +
                "    urlpattern='" +
                endpointInfo.getRuntimeUrlPattern().toString() +
                "'/>\n";
        }
        
        descriptorContents += "</endpoints>\n";
        
        FileOutputStream fos = new FileOutputStream(
            translateSourceAppFileName(JAXRPC_RI_RUNTIME));
        OutputStreamWriter writer = new OutputStreamWriter(fos, "UTF-8");
        writer.write(descriptorContents);
        writer.close();
    }
    
    protected void expandSourceFile() throws Exception {
        ZipInputStream zis =
            new ZipInputStream(new FileInputStream(sourceFile));
        while (true) {
            ZipEntry entry = zis.getNextEntry();
            if (entry == null) {
                break;
            }
            String name = entry.getName();
            if (entry.isDirectory()) {
                continue;
            }
            File outputFile = translateSourceAppFileName(name);
            outputFile.getParentFile().mkdirs();
            FileOutputStream fos = new FileOutputStream(outputFile);
            copyStream(zis, fos);
            fos.close();
        }
    }
    
    protected void packageEndpointClient() throws Exception {
        File clientFiles = new File(
            tmpdir.getAbsolutePath() + FS + "WEB-INF" + FS + "lib");
        List files = new ArrayList();
        collectAllFiles(clientFiles, files);
        String rootPath = tmpdir.getAbsolutePath();
        
        File scratchFile = File.createTempFile("jar", "jar", tmpdirBase);
        JarOutputStream jos = new JarOutputStream(
            new FileOutputStream(scratchFile));
        for (Iterator iter = files.iterator(); iter.hasNext();) {
            File file = (File) iter.next();
            String filePath = file.getAbsolutePath();
            if (filePath.startsWith(rootPath)) {
                String name = filePath.substring(rootPath.length() + 1)
                    .replace(FSCHAR, '/');
                jos.putNextEntry(new ZipEntry(name));
                FileInputStream fis = new FileInputStream(file);
                copyStream(fis, jos);
                fis.close();
                jos.closeEntry();
            }
        }
        jos.close();
        FileInputStream fis = new FileInputStream(scratchFile);
        FileOutputStream fos = new FileOutputStream(
            new File(clientFiles + FS + "Client.jar"));
        copyStream(fis, fos);
        fos.close();
        fis.close();
        scratchFile.delete();
        if (verbose) {
            onInfo(getMessage("wsdeploy.info.createdWarFile",
                destFile.getAbsolutePath()));
        }
    }
    
    protected void packageDestinationFile() throws Exception {
        
        if( clientSpecified ) {
            packageEndpointClient();
        }
        
        // collect all file names from the temporary work area
        List files = new ArrayList();
        collectAllFiles(tmpdir, files);
        String rootPath = tmpdir.getAbsolutePath();
        
        // now create the output war
        File scratchFile = File.createTempFile("war", "war", tmpdirBase);
        JarOutputStream jos = new JarOutputStream(
            new FileOutputStream(scratchFile));
        for (Iterator iter = files.iterator(); iter.hasNext();) {
            File file = (File) iter.next();
            String filePath = file.getAbsolutePath();
            if (filePath.startsWith(rootPath)) {
                String name = filePath.substring(rootPath.length() + 1)
                    .replace(FSCHAR, '/');
                jos.putNextEntry(new ZipEntry(name));
                FileInputStream fis = new FileInputStream(file);
                copyStream(fis, jos);
                fis.close();
                jos.closeEntry();
            }
        }
        jos.close();
        FileInputStream fis = new FileInputStream(scratchFile);
        FileOutputStream fos = new FileOutputStream(destFile);
        copyStream(fis, fos);
        fos.close();
        fis.close();
        scratchFile.delete();
        if (verbose) {
            onInfo(getMessage("wsdeploy.info.createdWarFile",
                destFile.getAbsolutePath()));
        }
    }
    
    protected File translateSourceAppFileName(String name) {
        if (name.charAt(0) == '/') {
            
            // map absolute paths to paths into the web application
            return translateSourceAppFileName(name.substring(1));
        } else {
            return new File(tmpdir, name.replace('/', FSCHAR));
        }
    }
    
    protected File createTemporaryDirectory() {
        String base = tmpdirBase.getAbsolutePath();
        String uniqueName = null;
        File dir = null;
        do {
            String suffix = Long.toHexString(
                (new Random()).nextLong() & 0xffffff);
            uniqueName = "jaxrpc-deploy-" + suffix;
            String dirName = base + FS + uniqueName;
            dir = new File(dirName);
        } while (dir.exists());
        dir.mkdir();
        return dir;
    }
    
    protected void collectAllFiles(File dir, List files) throws Exception {
        if (dir.isDirectory()) {
            File[] fs = dir.listFiles();
            for (int i = 0; i < fs.length; ++i) {
                if (fs[i].isDirectory()) {
                    collectAllFiles(fs[i], files);
                } else {
                    files.add(fs[i]);
                }
            }
        }
    }
    
    protected static void removeDirectory(File directory) throws IOException {
        if (directory.exists() && !directory.delete()) {
            
            // must empty the directory
            File[] files = directory.listFiles();
            for (int i = 0; i < files.length; ++i) {
                if (files[i].isDirectory()) {
                    removeDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
            directory.delete();
        }
    }
    
    protected static void copyFile(File in, File out) throws IOException {
        FileInputStream fis = new FileInputStream(in);
        FileOutputStream fos = new FileOutputStream(out);
        copyStream(fis, fos);
        fos.close();
        fis.close();
    }
    
    protected static void copyStream(InputStream is, OutputStream os)
        throws IOException {
            
        byte[] buf = new byte[1024];
        int len = 0;
        while (len != -1) {
            try {
                len = is.read(buf, 0, buf.length);
            } catch (EOFException eof){
                break;
            }
            
            if(len != -1) {
                os.write(buf, 0, len);
            }
        }
    }
    
    protected String getGenericErrorMessage() {
        return "wsdeploy.error";
    }
    
    protected String getResourceBundleName() {
        return "com.sun.xml.rpc.resources.wsdeploy";
    }
    
    ////////
    
    public void onError(Localizable msg) {
        report(getMessage("wsdeploy.error", localizer.localize(msg)));
    }
    public void onWarning(Localizable msg) {
        report(getMessage("wsdeploy.warning", localizer.localize(msg)));
    }
    public void onInfo(Localizable msg) {
        report(getMessage("wsdeploy.info", localizer.localize(msg)));
    }
    
    protected Properties props = null;
    protected boolean doNothing = false;
    protected boolean clientSpecified = false;
    protected boolean verbose = false;
    protected boolean keepTemporaryFiles = false;
    protected File sourceFile;
    protected File destFile;
    protected File tmpdirBase;
    protected File tmpdir;
    protected String userClasspath = null;
    protected String target = null;
    
    public static final String WEBAPP_DD = "WEB-INF/web.xml";
    public static final String WEBAPP_DD_PROCESSED = "WEB-INF/web-before.xml";
    public static final String JAXRPC_RI_DD = "WEB-INF/jaxrpc-ri.xml";
    public static final String JAXRPC_RI_DD_PROCESSED =
        "WEB-INF/jaxrpc-ri-before.xml";
    public static final String JAXRPC_RI_RUNTIME =
        "WEB-INF/jaxrpc-ri-runtime.xml";
    
    private static final String PS   = System.getProperty("path.separator");
    private static final char PSCHAR =
        System.getProperty("path.separator").charAt(0);
    private static final String FS   = System.getProperty("file.separator");
    private static final char FSCHAR =
        System.getProperty("file.separator").charAt(0);
}
