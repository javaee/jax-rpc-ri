/*
 * $Id: ProcessorEnvironmentBase.java,v 1.2.2.1 2008-02-13 10:59:40 venkatajetti Exp $
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

package com.sun.xml.rpc.processor.util;

import java.io.File;
import java.net.URLClassLoader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

/**
 *
 * @author JAX-RPC Development Team
 */
public abstract class ProcessorEnvironmentBase implements ProcessorEnvironment {
    
    private JaxClassLoader classLoader = null;
    
    /**
     * Get a URLClassLoader from using the classpath
     */
    public ClassLoader getClassLoader() {
        if (classLoader == null) {
            URL[] urls = pathToURLs(getClassPath().toString());
            JaxClassLoader l = new JaxClassLoader();
            for (int i = 0; i < urls.length; i++) {
                l.appendURL(urls[i]);
            }
            classLoader = l;
//            classLoader = new URLClassLoader(urls);
        }
        return classLoader;
        }
    /**
     * Release resources, if any.
     */
    public void shutdown() {
        if (classLoader != null) {
//            if (classLoader instanceof JaxClassLoader) {
//                ((JaxClassLoader) classLoader).done();
//            }
            classLoader.done();
            classLoader = null;
        }
    }
    
    /**
     * Utility method for converting a search path string to an array
     * of directory and JAR file URLs.
     *
     * @param path the search path string
     * @return the resulting array of directory and JAR file URLs
     */
    public static URL[] pathToURLs(String path) {
        StringTokenizer st = new StringTokenizer(path, File.pathSeparator);
        URL[] urls = new URL[st.countTokens()];
        int count = 0;
        while (st.hasMoreTokens()) {
            String element = st.nextToken();
            URL url = fileToURL(new File(element));
            if (url != null) {
                urls[count++] = url;
            }
        }
        if (urls.length != count) {
            URL[] tmp = new URL[count];
            System.arraycopy(urls, 0, tmp, 0, count);
            urls = tmp;
        }
        return urls;
    }
    
    /**
     * Returns the directory or JAR file URL corresponding to the specified
     * local file name.
     *
     * @param file the File object
     * @return the resulting directory or JAR file URL, or null if unknown
     */
    public static URL fileToURL(File file) {
        String name;
        try {
            name = file.getCanonicalPath();
        } catch (IOException e) {
            name = file.getAbsolutePath();
        }
        name = name.replace(File.separatorChar, '/');
        if (!name.startsWith("/")) {
            name = "/" + name;
        }
        
        // If the file does not exist, then assume that it's a directory
        if (!file.isFile()) {
            name = name + "/";
        }
        try {
            return new URL("file", "", name);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("file");
        }
    }
    
}
