/*
 * @(#)$Id: Main.java,v 1.1 2006-04-12 20:34:58 kohlert Exp $
 */
  
/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
