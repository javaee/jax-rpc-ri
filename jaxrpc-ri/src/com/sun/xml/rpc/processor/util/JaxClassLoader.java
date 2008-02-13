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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.URLStreamHandler;
import java.net.UnknownServiceException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.SecureClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;


/**
 * This is a copy of the EJBClassLoader. Before, JaxRpc was using a URLClassLoader. This
 * was causing problems with file leaks: the URLClassLoader caches the jar files, and
 * it does not have a "done" method that will close all.
 *
 * @author JAX-RPC RI Development Team
 */
public class JaxClassLoader extends SecureClassLoader {
    /** logger for this class */
    static Logger _logger = Logger.getLogger(JaxClassLoader.class.getName());

    /** list of url entries of this class loader */
    private List urlSet = Collections.synchronizedList(new ArrayList());

    /** cache of not found resources */
    private Map notFoundResources   = new HashMap();

    /** cache of not found classes */
    private Map notFoundClasses     = new HashMap();

    /** state flag to track whether this instance has been shut off.  */
    private boolean doneCalled = false;
    /** snapshot of classloader state at the time done was called */
    private String doneSnapshot;
    
    /** streams opened by this loader */
    private Set streams = new HashSet(); // type: SentinelInputStream

    /**
     * Constructor.
     */
    public JaxClassLoader() {
        super();

        if (_logger.isLoggable(Level.FINE)) {
            _logger.log(Level.FINE, 
                        "ClassLoader: " + this + " is getting created.");
        }
    }

    /**
     * Constructor.
     *
     * @param    parent    parent class loader
     */
    public JaxClassLoader(ClassLoader parent) {
        super(parent);
    }

    public static String printStackTraceToString()
    {
        Throwable t = new Throwable("printStackTraceToString");
        t.fillInStackTrace();

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        t.printStackTrace(printWriter);
        String stackTrace = stringWriter.toString();

        printWriter.close();

        return stackTrace;
    }

    public boolean isDone() {
        return doneCalled;
    }

    /** 
     * This method should be called to free up the resources.
     * It helps garbage collection.
     */
    public void done() {

        if( doneCalled ) {
            return;
        }

        // Capture the fact that the classloader is now effectively disabled.
        // First create a snapshot of our state.  This should be called
        // before setting doneCalled = true.
        doneSnapshot = "JaxClassLoader.done() called ON " + this.toString() 
            + "\n AT " + new Date() + 
            " \n BY :" + printStackTraceToString();
        doneCalled = true;

        // closes the jar handles and sets the url entries to null
        int i = 0;
        while (i < this.urlSet.size()) { 
            URLEntry u = (URLEntry) this.urlSet.get(i);
         
            if (u.zip != null) {
                try {
                    u.zip.reallyClose();
                } catch (IOException ioe) {
                    //_logger.log(Level.FINE, "exception closing URLEntry zip" +
                    //            ioe);
                    // To help with diagnosing undeployment issues, increase the logging level
                    _logger.log(Level.INFO, "exception closing URLEntry zip" +
                                ioe);                    
                }
            }
            
            if (u.table != null) {
                u.table.clear();
                u.table = null;
            }
            
            u = null;
            i++;
        }

        closeOpenStreams();
        
        // clears out the tables       
        if (this.urlSet != null)            { this.urlSet.clear();            }
        if (this.notFoundResources != null) { this.notFoundResources.clear(); }
        if (this.notFoundClasses != null)   { this.notFoundClasses.clear();   }

        // sets all the objects to null
        this.urlSet              = null;
        this.notFoundResources   = null;
        this.notFoundClasses     = null;
    }
   

    /**
     * Add a url to the list of urls we search for a class's bytecodes.
     *
     * @param    url    url to be added
     */
    public synchronized void appendURL(URL url) {

        try {
            if (url == null) {
                _logger.log(Level.INFO,
                    "Bad URL entry: " + url);
                return;
            }

            URLEntry entry = new URLEntry(url);

            if ( !urlSet.contains(entry) ) {
                // init entry if none already exists
                entry.init();
                
                // adds the url entry to the list
                this.urlSet.add(entry);

                if (entry.isJar) {
                    // checks the manifest if a jar
                    checkManifest(entry.zip, entry.file);
                }
            } else {
                _logger.log(Level.FINE, 
                    "[EJB-CL] Ignoring duplicate URL: " + url);
            }

            // clears the "not found" cache since we are adding a new url 
            this.notFoundResources.clear();
            this.notFoundClasses.clear();

        } catch (IOException ioe) {

            _logger.log(Level.SEVERE,
                "Bad URL entry: " + url);

            _logger.log(Level.SEVERE,
                "Malformed URL entry: " + ioe);
        }
    }

    /**
     * Returns the urls of this class loader.
     *
     * @return    the urls of this class loader or an empty array
     */
    public synchronized URL[] getURLs() {

        URL[] url  = null;

        if (this.urlSet != null) {
            url  = new URL[this.urlSet.size()];

            for (int i=0; i<url.length; i++) {
                url[i] = ((URLEntry)this.urlSet.get(i)).source;
            }
        } else {
            url = new URL[0];
        }

        return url;
    }

    /**
     * Returns all the "file" protocol resources of this ClassLoader,
     * concatenated to a classpath string.
     *
     * Notice that this method is called by the setClassPath() method of
     * org.apache.catalina.loader.WebappLoader, since this ClassLoader does
     * not extend off of URLClassLoader.
     *
     * @return Classpath string containing all the "file" protocol resources
     * of this ClassLoader 
     */
    public String getClasspath() {

        StringBuffer strBuf = null;

        URL[] urls = getURLs();
        if (urls != null) {
            for (int i=0; i<urls.length; i++) {
                if (urls[i].getProtocol().equals("file")) {
                    if (strBuf == null) {
                        strBuf = new StringBuffer();
                    }
                    if (i > 0) {
                        strBuf.append(File.pathSeparator);
                    }
                    strBuf.append(urls[i].getFile());
                }
            }
        }

        return (strBuf != null) ? strBuf.toString() : null;
    }
    
    public final static int WRONG_VERSION = -1;
    public final static int MAJOR_VERSION_MULTIPLIER = 1000 * 1000;
    public final static int MINOR_VERSION_MULTIPLIER = 1000;
    public final static int SUBVERSION_MULTIPLIER = 1;
    public final static int JAVA_14 = 1 * MAJOR_VERSION_MULTIPLIER + 4
            * MINOR_VERSION_MULTIPLIER;
    public final static int JAVA_15 = 1 * MAJOR_VERSION_MULTIPLIER + 5
            * MINOR_VERSION_MULTIPLIER;
    private final static void wrongJavaVersion(final String javaVersion) {
        _logger.warning("Unknown version of Java: " + javaVersion);
    }
    private static final synchronized int getJavaVersionInternal(boolean needSubversion) {
        final String javaVersion = (String) java.security.AccessController
                .doPrivileged(new PrivilegedAction() {
                    public Object run() {
                        return System.getProperty("java.version");
                    }
                });
        if (javaVersion == null || javaVersion.length() == 0) {
            wrongJavaVersion(javaVersion);
            return WRONG_VERSION;
        }
        // Full parse:
        try {
            final int firstDotIndex = javaVersion.indexOf('.');
            if (firstDotIndex <= 0) {
                wrongJavaVersion(javaVersion);
                return WRONG_VERSION;
            }
            final int major = Integer.parseInt(javaVersion.substring(0, firstDotIndex));
            int version = major * MAJOR_VERSION_MULTIPLIER;

            final int secondDotIndex = javaVersion.indexOf('.', firstDotIndex + 1);
            if (secondDotIndex <= 0) {
                return version;
            }

            final int minor = Integer.parseInt(javaVersion.substring(firstDotIndex + 1,
                    secondDotIndex));
            version += minor * MINOR_VERSION_MULTIPLIER;

            if (needSubversion) {
                final int underscoreIndex = javaVersion.indexOf('_', secondDotIndex + 1);
                if (underscoreIndex < 0) {
                    return version;
                }
                final int subversion = Integer.parseInt(javaVersion.substring(
                        secondDotIndex + 1, underscoreIndex));
                version += subversion * SUBVERSION_MULTIPLIER;
            }
            return version;

        } catch (NumberFormatException nfe) {
            wrongJavaVersion(javaVersion);
            return WRONG_VERSION;
        }
    }
    
    private static final boolean NEED_ESCAPE_PERCENTS = getJavaVersionInternal(false) >= JAVA_15;
    private static String encodeUtf8(String text) {
        try {
            return URLEncoder.encode(text, "UTF-8");
        }catch (UnsupportedEncodingException never) {
            final LinkageError error = new LinkageError();
            error.initCause(never);
            throw error;
        }
    }
    private static final String ENCODED_PERCENT = encodeUtf8("%");
    private static final Pattern PERCENT_PATTERN = Pattern.compile("%");
    
    private static final String escapePercents(final String text) {
        if (NEED_ESCAPE_PERCENTS) {
            return PERCENT_PATTERN.matcher(text).replaceAll(ENCODED_PERCENT);
        } else {
            return text;
        }
    }
    
    /**
     * To properly close streams obtained through URL.getResource().getStream():
     * this opens the input stream on a JarFile that is already open as part 
     * of the classloader, and returns a sentinel stream on it.
     * 
     * @author fkieviet
     */
    private class InternalJarURLConnection extends JarURLConnection {
        private URL mURL;
        private URLEntry mRes;
        private String mName;
        
        /**
         * Constructor
         * 
         * @param url the URL that is a stream for
         * @param res URLEntry
         * @param name String
         * @throws MalformedURLException from super class
         */
        public InternalJarURLConnection(URL url, URLEntry res, String name)
            throws MalformedURLException {
            super(url);
            mRes = res;
            mName = name;
        }

        /**
         * @see java.net.JarURLConnection#getJarFile()
         */
        public JarFile getJarFile() throws IOException {
            return mRes.zip;
        }

        /**
         * @see java.net.URLConnection#connect()
         */
        public void connect() throws IOException {
            // Nothing
        }
        
        /**
         * @see java.net.URLConnection#getInputStream()
         */
        public InputStream getInputStream() throws IOException {
            ZipEntry entry = mRes.zip.getEntry(mName);
            return new SentinelInputStream(mRes.zip.getInputStream(entry), 1);
        }
    }
    
    /**
     * To properly close streams obtained through URL.getResource().getStream():
     * an instance of this class is instantiated for each and every URL object
     * created by this classloader. It provides a custom JarURLConnection  
     * (InternalJarURLConnection) so that the stream can be obtained from an already
     * open jar file.
     * 
     * @author fkieviet
     */
    private class InternalURLStreamHandler extends URLStreamHandler {
        private URL mURL; 
        private URLEntry mRes;
        private String mName;
        
        /**
         * Constructor
         * 
         * @param res URLEntry
         * @param name String
         */
        public InternalURLStreamHandler(URLEntry res, String name) {
            mRes = res;
            mName = name;
        }
        
        /**
         * @see java.net.URLStreamHandler#openConnection(java.net.URL)
         */
        protected URLConnection openConnection(URL u) throws IOException {
            if (u != mURL) { // ref compare on purpose
                // This should never happen
                throw new IOException("Cannot open a foreign URL; this.url=" + mURL
                    + "; foreign.url=" + u);
            }
            return new InternalJarURLConnection(u, mRes, mName);
        }
        
        /**
         * Ties the URL that this handler is associated with to the handler, so
         * that it can be asserted that somehow no other URLs are mangled in (this
         * is theoretically impossible)
         * 
         * @param url URL
         */
        public void tieUrl(URL url) {
            mURL = url;
        }
    }

    /**
     * Internal implementation of find resource.
     *
     * @param    res    url resource entry
     * @param    name   name of the resource 
     */
    private URL findResource0(final URLEntry res,
                              final String name) {

        Object result =
        AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {

                if (res.isJar) {
                    
                    try {
                        JarEntry jarEntry = res.zip.getJarEntry(name);

                        if (jarEntry != null) {
                            // Provide a custom URL with a special handler to fix 
                            // problems with URL.getResource().getStream().
                            InternalURLStreamHandler handler = new InternalURLStreamHandler(res, name);
                            URL ret = new URL(null, "jar:" + res.source + "!/"
                                + escapePercents(name), handler);
                            handler.tieUrl(ret);
                            return ret;
                        }

                    } catch (IOException e) {                      
                        _logger.log(Level.FINE,
                                    "Exception in loader: " + e ,e);
                    }
                } else { // directory
                    try {
                        File resourceFile = 
                            new File(res.file.getCanonicalPath() 
                                        + File.separator + name);

                        if (resourceFile.exists()) {
                            // If we make it this far, 
                            // the resource is in the directory.
                            return  resourceFile.toURL();
                        }

                    } catch (IOException e) {                       
                        _logger.log(Level.FINE,
                            "Exception in loader: " + e ,e);
                    }
                }

                return null;
                
            } // End for -- each URL in classpath.
        });

        return (URL) result;
    }

    protected URL findResource(String name) {

        if( doneCalled ) {
            _logger.log(Level.WARNING, "findResource(" + name + ") was called on a "
                + "classloader that was already closed.",
                new Throwable("Illegal call on classloader " + this));
            return null;
        }

        // resource is in the not found list
        String nf = (String) notFoundResources.get(name);
        if (nf != null && nf.equals(name) ) {
            return null;
        }

        int i = 0;
        while (i < this.urlSet.size()) { 

            URLEntry u = (URLEntry) this.urlSet.get(i);
            
            if (!u.hasItem(name)) {
                i++;
                continue;
            }
            
            URL url = findResource0(u, name);
            if (url != null) return url;
            i++;
        }

        // add resource to the not found list
        synchronized (this.notFoundResources) {
            notFoundResources.put(name, name);
        }

        return null;
    }


    /**
     * Checks the manifest of the given jar file.
     * 
     * @param    jar    the jar file that may contain manifest class path
     * @param    file   file pointer to the jar 
     *
     * @throws   IOException   if an i/o error
     */
    private void checkManifest(JarFile jar, File file) throws IOException {

        if ( (jar == null) || (file == null) ) return;

        Manifest man = jar.getManifest();
        if (man == null) return;

        synchronized (this) {
            String cp = man.getMainAttributes().getValue(
                                        Attributes.Name.CLASS_PATH);
            if (cp == null) return;

            StringTokenizer st = new StringTokenizer(cp, " ");

            while (st.hasMoreTokens()) {
                String entry = st.nextToken();

                File newFile = new File(file.getParentFile(), entry);

                // add to class path of this class loader
                try {
                    appendURL(newFile.toURL());
                } catch (MalformedURLException ex) {
                    _logger.log(Level.SEVERE,
                        "Malformed URL: " + newFile + ": " + ex);
                }
            }
        }
    }
                
    /**
     * Internal implementation of load class.
     * 
     * @param    res        url resource entry
     * @param    entryName  name of the class
     */
    private byte[] loadClassData0(final URLEntry res, final String entryName) {

        Object result =
        AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                try {
                    InputStream classStream = null;
                    
                    if (res.isJar) { // It is a jarfile..
                        JarFile zip = res.zip;
                        ZipEntry entry = zip.getEntry(entryName);
                        if (entry != null) {
                            classStream = zip.getInputStream(entry);
                            
                            byte[] classData = getClassData(classStream);
                            
                            return classData;
                        }
                    } else { // Its a directory....
                        File classFile = new File (res.file, 
                                    entryName.replace('/', File.separatorChar));

                        if (classFile.exists()) {
                            byte abyte0[];                                      // merged change from update1
                            try {                                               // merged change from update1 - line 403
                                classStream = new FileInputStream(classFile);
                                
                                byte[] classData = getClassData(classStream);
                                
                                abyte0 = classData;                             // merged change from update1 - line 405
                                //classStream.close();                          // merged change from update1 - removed
                            }
                            finally {                                           // merged change from update1 - line 412-418
                                if(classStream != null) {
                                    try {
                                        classStream.close();
                                    } catch(IOException closeIOE) {
                                        _logger.log(Level.INFO, "Load failed: " + closeIOE, closeIOE); // increased logging level to help debugging
                                    }
                                }
                            }
                            return abyte0;
                            //return classData;                                 // merged change from update1 - replaced
                        }
                    }
                } catch (IOException ioe) {
                    _logger.log(Level.FINE,
                                "Load failed: " + ioe, ioe);
                }
                return null;
            }
        });
        return (byte[]) result;
    }

    protected Class findClass(String name) throws ClassNotFoundException {
    
        try {
               
        if( doneCalled ) {
            _logger.log(Level.WARNING, "findClass(" + name + ") was called on a "
                + "classloader that was already closed.",
                new Throwable("Illegal call on classloader " + this));
            throw new ClassNotFoundException(name);
        }

        String nf = (String) notFoundClasses.get(name);
        if (nf != null && nf.equals(name) ) {
            throw new ClassNotFoundException(name);
        }

        // search thru the JARs for a file of the form java/lang/Object.class
        String entryName = name.replace('.', '/') + ".class";

        int i = 0;
        while (i < urlSet.size()) { 
            URLEntry u = (URLEntry) this.urlSet.get(i);
            
            if (!u.hasItem(entryName)) {
                i++;
                continue;
            }

            byte[] result = null;
            
            result = loadClassData0(u, entryName);
            
            if (result != null) {
                // Define package information if necessary
                int lastPackageSep = name.lastIndexOf('.');
                if ( lastPackageSep != -1 ) {
                    String packageName = name.substring(0, lastPackageSep);
                    if( getPackage(packageName) == null ) {
                        try {

                            // There's a small chance that one of our parents
                            // could define the same package after getPackage
                            // returns null but before we call definePackage,
                            // since the parent classloader instances 
                            // are not locked.  So, just catch the exception
                            // that is thrown in that case and ignore it.
                            // 
                            // It's unclear where we would get the info to
                            // set all spec and impl data for the package,
                            // so just use null.  This is consistent will the
                            // JDK code that does the same.
                            definePackage(packageName, null, null, null, 
                                          null, null, null, null);
                        } catch(IllegalArgumentException iae) {
                            // duplicate attempt to define same package.
                            // safe to ignore.
                            _logger.log(Level.FINE, "duplicate package " +
                                "definition attempt for " + packageName, iae);
                        }
                    }
                }

                Class clz = defineClass(name, result, 0, result.length);
                
                return clz;
            }
            i++;
        }

        // add to the not found classes list
        synchronized (this.notFoundClasses) {
            notFoundClasses.put(name, name);
        }

        throw new ClassNotFoundException(name);
        
        } finally {
            //m.end();
        }
    }

    /**
     * Returns the byte array from the given input stream.
     * 
     * @param    istream    input stream to the class or resource
     * 
     * @throws   IOException  if an i/o error
     */
    private byte[] getClassData(InputStream istream) throws IOException {

        BufferedInputStream bstream = new BufferedInputStream(istream);
        byte[] buf = new byte[4096];
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        int num = 0;
        try {                                                                   // added to approximate changes from update1
            while( (num = bstream.read(buf)) != -1) {
                bout.write(buf, 0, num);
            }
        } finally {                                                             // added to approximate changes from update1
            if (bstream != null) {                                              // added to approximate changes from update1
                try {                                                           // added to approximate changes from update1
                    bstream.close();                                            
                } catch(IOException closeIOE) {                                 // added to approximate changes from update1
                    _logger.log(Level.INFO, "Load exception: " + closeIOE, closeIOE); // added to approximate changes from update1
                }                                                               // added to approximate changes from update1
            }                                                                   // added to approximate changes from update1
        }                                                                       // added to approximate changes from update1

        return bout.toByteArray();
    }

    /**
     * Returns a string representation of this class loader.
     *
     * @return   a string representation of this class loader
     */
    public String toString() {
        
        StringBuffer buffer = new StringBuffer();

        buffer.append("JaxClassLoader : \n");
        if( doneCalled ) {
            buffer.append("doneCalled = true" + "\n");
            if( doneSnapshot != null ) {
                buffer.append("doneSnapshot = " + doneSnapshot);
            }
        } else {
            buffer.append("urlSet = " + this.urlSet + "\n");
            buffer.append("doneCalled = false " + "\n");
        }
        buffer.append(" Parent -> " + getParent() + "\n");
        
        return buffer.toString();
    }
    
    /**
     * @see java.lang.ClassLoader#getResourceAsStream(java.lang.String)
     */
    public InputStream getResourceAsStream(final String name) {
        InputStream ret = super.getResourceAsStream(name);
        
        // Wrap with sentinel to catch unclosed streams
        if (ret != null) {
            // The stream may be obtained through a URL object; in this case
            // there is already a sentinel around the stream
            if (!(ret instanceof SentinelInputStream)) {
                ret = new SentinelInputStream(ret, 1);
            }
        }
        return ret;
    }
    
    /**
     * The JarFile objects loaded in the classloader may get exposed to the 
     * application code (e.g. EJBs) through calls of
     * ((JarURLConnection) getResource().openConnection()).getJarFile().
     * 
     * This class protects the jar file from being closed by such an application.
     * 
     * @author fkieviet
     */
    private static class ProtectedJarFile extends JarFile {
        /**
         * Constructor
         * 
         * @param file File
         * @throws IOException from parent
         */
        public ProtectedJarFile(File file) throws IOException {
            super(file);
        }
        
        /**
         * Do nothing
         * 
         * @see java.util.zip.ZipFile#close()
         */
        public void close() {
            // nothing
            _logger.log(Level.WARNING, "Illegal call to close() detected", new Throwable());
        }
        
        /**
         * Really close the jar file
         * 
         * @throws IOException from parent
         */
        public void reallyClose() throws IOException {
            super.close();
        }
        
        /**
         * @see java.lang.Object#finalize()
         */
        protected void finalize() throws IOException {
            reallyClose();
        }
    }

    /**
     * URL entry - keeps track of the url resources.
     */
    protected static class URLEntry {

        /** the url */
        URL source;

        /** file of the url */
        File file;

        /** jar file if url is a jar else null */
        ProtectedJarFile zip;

        /** true if url is a jar */
        boolean isJar  = false;
        
        Hashtable table = null;

        URLEntry(URL url) {
            source  = url;

        }
        
        void init() throws IOException {
            file    = new File(source.getFile());
            isJar  = file.isFile();

            if (isJar) {
                zip = new ProtectedJarFile(file);
            }
            
            table = new Hashtable();
            cacheItems();
        }
        
        private void cacheItems() throws IOException {
            if (isJar) {
                // cache entry names from jar file
                for (Enumeration e = zip.entries(); e.hasMoreElements(); ) {
                    ZipEntry curEntry = (ZipEntry) e.nextElement();
                    table.put(curEntry.getName(), curEntry.getName());
                }
                
            } else {
                // cache entry names from directory
                if (file.exists()) {
                    fillTable(file, table, "");
                }
            }
        }
        
        private void fillTable(File f, Hashtable t, String parent) throws IOException {
            
            String localName = (parent.equals("")) ? "" : parent + "/";
            
            File[] children = f.listFiles();
            for (int i = 0; i < children.length; i++) {
                File curFile = children[i];
                if (curFile.isFile()) {
                    t.put(localName + curFile.getName(),
                        localName + curFile.getName());
                } else if (curFile.isDirectory()) {
                    fillTable(curFile, t, localName + curFile.getName());
                }
            }
        }
        
        boolean hasItem(String item) {
            // in the case of ejbc stub compilation, classloader is created before stubs
            // gets generated, thus we need to return true for this case.
            if (table.size() == 0) {
                return true;
            }
            
            // special handling
            if (item.startsWith("./")) {
                return table.containsKey(item.substring(2, item.length()));
            }
            
            return table.containsKey(item);
        }

        public String toString() {
            return "URLEntry : " + source.toString();
        }

        /**
         * Returns true if two URL entries has equal URLs.
         *
         * @param  obj   URLEntry to compare against
         * @return       true if both entry has equal URL
         */
        public boolean equals(Object obj) {

            boolean tf = false;

            if (obj instanceof URLEntry) {
                if (source.equals(( (URLEntry)obj ).source)) {
                    tf = true;
                }
            }

            return tf;
        }

        /**
         * Since equals is overridden, we need to override hashCode as well.
         */
        public int hashCode() {
            return source.hashCode();

        }

    }

    private Set getStreams() {
        return streams;
    }
    
    /**
     *Closes any streams that remain open, logging a warning for each.
     *<p>
     *This method should be invoked when the loader will no longer be used
     *and the app will no longer explicitly close any streams it may have opened.
     */
    private void closeOpenStreams() {
        if (streams != null) {
            SentinelInputStream[] toclose = (SentinelInputStream[]) streams.toArray(new SentinelInputStream[streams.size()]); 
            for (int i = 0; i < toclose.length; i++) {
                try {
                    toclose[i].closeWithWarning();
                } catch (IOException ioe) {
                    _logger.log(Level.WARNING, "Error closing an open stream during loader clean-up", ioe);
                }
            }
            streams.clear();
            streams = null;
        }
    }

    /**
     * Lets give a push to release resources!
     * @author vtsyganok
     */
    protected class SentinelInputStream extends FilterInputStream {
        private boolean closed;
        private final StackTraceElement[] stackAtAllocation;
        private final int skipFrames;
        
        /**
         * Constructs new FilteredInputStream which reports InputStreams not closed properly.
         * When Garbage collector deletes the class and stream is still open this class will
         * report stack trace of method created the stream but not closed.
         * 
         * @param in - InputStream to be wrapped
         * @param skipFrames - number additional SrackTrace frames to be skipped. 
         * (StackTrace frames corresponding to SentinelInputStream are skipped always) 
         */
        protected SentinelInputStream(final InputStream in, final int skipFrames) {
            super(in);
            stackAtAllocation = new Throwable().getStackTrace();
            //We have to skip one extra frame for Throwable constructor
            //and one for SentinelInputStream constructor
            //but for convenience (0-based array indexes) I'm adding only 1 here:
            this.skipFrames = skipFrames + 1;
            getStreams().add(this);
        }
        
        /**
         * Invoked by Garbage Collector. If underlaying InputStream was not closed properly,
         * the stack trace of the constructor will be logged!
         */
        protected void finalize() throws Throwable {
            if (!closed && this.in != null){
                // This should never happen: the stream should already be closed by the
                // done() method of the classloader

                try {
                    in.close();
                } catch (IOException ignored) {
                    //Cannot do anything here.
                }
                //Well, give them a stack trace!
                report();
            }
            super.finalize();
        }
        
        private void _close() throws IOException {
            closed = true;
            getStreams().remove(this);
            super.close();
        }
        
        /**
         * Closes underlaying input stream.
         */
        public void close() throws IOException {
            _close();
        }
        
        private void closeWithWarning() throws IOException {
            _close();
            report();
        }
        
        /**
         * Report "left-overs"!
         */
        private void report(){
            final int count = stackAtAllocation.length;
            //This is my estimate on the amount of memory required:
            final StringBuffer buffer = new StringBuffer(count << 16); // == 256 * count
            buffer.append("An InputStream was opened, but not closed explicitly. This may" +
                    " cause undeployment problems. To help fixing this defect," +
                    " following is the stacktrace of where the stream was opened: "
                    );
            for (int i = this.skipFrames; i < count; i++){
                buffer.append('\n');
                buffer.append(stackAtAllocation[i].toString());
            }
            _logger.warning(buffer.toString());
        }
    }
}
