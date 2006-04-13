/*
 * @(#)$Id: Main.java,v 1.2 2006-04-13 01:34:50 ofung Exp $
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
package com.sun.xml.rpc.installer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class Main {
    public static void main(String[] args) throws IOException {
    	Reader in = new InputStreamReader(Main.class.getResourceAsStream("/license.txt"));
        new LicenseForm(in) {
            protected void install() {
                try {
                    Main.install();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.show();
    }
    
    /**
     * Does the actual installation.
     */
    private static void install() throws IOException {
        ZipInputStream zip = new ZipInputStream(Main.class.getResourceAsStream("/package.zip"));
        ZipEntry e;
        while((e=zip.getNextEntry())!=null) {
            File name = new File(e.getName());
            System.out.println(name);
            if( e.isDirectory() ) {
                name.mkdirs();
            } else {
                if( !name.exists() )
                    copyStream( zip,  new FileOutputStream(name) );
            }
        }
        zip.close();
        System.out.println("installation complete");
    }

    public static void copyStream( InputStream in, OutputStream out ) throws IOException {
        byte[] buf = new byte[256];
        int len;
        while((len=in.read(buf))>=0) {
            out.write(buf,0,len);
        }
        out.close();
    }
}
