/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package photocatalog.util;

import java.awt.*;
import java.util.*;
import java.io.*;
import java.awt.image.*;
import javax.xml.soap.AttachmentPart;
import javax.xml.rpc.Stub;
import javax.xml.transform.stream.StreamSource;
import com.sun.xml.rpc.client.StubPropertyConstants;

public class AttachmentHelper{
    
    final int BUFSIZE = 4096;
    
    public Iterator getAttachments(Iterator iter) {
        while(iter.hasNext()) {
            Object obj = iter.next();
            if(!(obj instanceof AttachmentPart)) {
                return null;
            }
        }
        return iter;
    }
    
    public AttachmentPart getAttachment(java.net.URI ref, Iterator iter) {
        if(iter == null || ref == null) {
            System.out.println("null Iterator for AttachmentPart");
            return null;
        }
        while(iter.hasNext()) {
            AttachmentPart tempAttachment = (AttachmentPart)iter.next();
            if(ref.isOpaque() && ref.getScheme().equals("cid")) {
                String refId = ref.getSchemeSpecificPart();
                String cId = tempAttachment.getContentId();
                if(cId.equals("<"+refId+">") || cId.equals(refId)) {
                    return tempAttachment;
                }
            }
        }
        return null;
    }
    
    public boolean compareStreamSource(StreamSource src1, StreamSource src2) throws Exception{
        if(src1 == null || src2 == null) {
            System.out.println("compareStreamSource - src1 or src2 null!");
            return false;
        }
        InputStream is1 = src1.getInputStream();
        InputStream is2 = src2.getInputStream();
        int i = 0;
        int j =0;
        if( (is1 == null) || (is2==null) ) {
            System.out.println("InputStream of - src1 or src2 null!");
            return false;
        }
        while(true) {
            i = is1.read();
            j = is2.read();
            if( (i == -1) && (j == -1) ) {
                return true;
            } else if( (i == -1) || (j == -1) ) {
                System.out.println("Lengths of InputStreams did not match");
                return false;
            } else if ( i != j) {
                System.out.println("StreamSources did not match");
                //System.out.println("i:" + i + " j:" + j);
                return false;
            }
            //System.out.println("i:" + i + " j:" + j);
            
        }
        
    }
    
    public boolean compareStreamSource(StreamSource src1, StreamSource src2, int length) throws Exception{
        if(src1 == null || src2 == null) {
            System.out.println("compareStreamSource - src1 or src2 null!");
            return false;
        }
        String in = getStringFromStreamSource(src1, length);
        String out = getStringFromStreamSource(src2, length);
        //System.out.println("exp on server: "+in);
        //System.out.println("got on server: "+out);
        if(in == null)
            System.out.println("src1 is null");
        if(out == null)
            System.out.println("src2 is null");
        return in.equals(out);
    }
    
    private String getStringFromStreamSource(StreamSource src, int length) throws Exception{
        byte buf[]=null;
        if(src == null)
            return null;
        InputStream outStr = src.getInputStream();
        if(outStr != null) {
            int len = outStr.available(); if(outStr.markSupported())outStr.reset();
            buf = new byte[len];
            outStr.read(buf, 0, len);
            //System.out.println("From inputstream: "+new String(buf));
            return new String(buf);
        }else {
            char buf1[] = new char[length];
            Reader r = src.getReader();
            if(r == null)
                return null;
            r.reset();
            r.read(buf1);
            //System.out.println("From Reader: "+new String(buf));
            return new String(buf1);
        }
    }
    
    public String getStringFromStreamSource(StreamSource src) throws Exception{
        StringBuffer sb = new StringBuffer();
        int n;
        if(src == null)
            return null;
        InputStream inStr = src.getInputStream();
        if(inStr != null) {
            byte buf[] = new byte[BUFSIZE];
            while( (n = inStr.read(buf)) > 0) {
                sb.append(new String(buf,0,n));
            }
            
            //System.out.println("From inputstream: "+sb.toString());
            return sb.toString();
        }else {
            char buf1[] = new char[BUFSIZE];
            Reader r = src.getReader();
            if(r == null)
                return null;
            r.reset();
            while( (n = r.read(buf1)) > 0) {
                sb.append(buf1,0,n);
            }
            //System.out.println("From Reader: "+ sb.toString());
            return sb.toString();
        }
    }
    
    public boolean compareImages(Image image1, Image image2, Rectangle rect) {
        if(image1 == null || image2 == null)
            return false;
        
        boolean matched = false;
        
        Iterator iter1 = handlePixels(image1, rect);
        Iterator iter2 = handlePixels(image2, rect);
        
        while(iter1.hasNext() && iter2.hasNext()) {
            Pixel pixel = (Pixel)iter1.next();
            if(pixel.equals((Pixel)iter2.next())) {
                matched = true;
            }else {
                matched = false;
            }
        }
        if(matched)
            return true;
        return false;
    }
    
    public boolean compareImages(BufferedImage image1, BufferedImage image2) {
        if(image1 == null || image2 == null)
            return false;
        
        boolean matched = false;
        
        Rectangle rect = new Rectangle(0, 0, image1.getWidth(), image1.getHeight());
        
        Iterator iter1 = handlePixels(image1, rect);
        Iterator iter2 = handlePixels(image2, rect);
        
        while(iter1.hasNext() && iter2.hasNext()) {
            Pixel pixel = (Pixel)iter1.next();
            if(pixel.equals((Pixel)iter2.next())) {
                matched = true;
            }else {
                matched = false;
            }
        }
        if(matched)
            return true;
        return false;
    }
    
    public Iterator handlePixels(Image img, Rectangle rect) {
        int x = rect.x;
        int y = rect.y;
        int w = rect.width;
        int h = rect.height;
        
        int[] pixels = new int[w * h];
        PixelGrabber pg = new PixelGrabber(img, x, y, w, h, pixels, 0, w);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
            System.err.println("interrupted waiting for pixels!");
            return null;
        }
        if ((pg.getStatus() & ImageObserver.ABORT) != 0) {
            System.err.println("image fetch aborted or errored");
            return null;
        }
        ArrayList tmpList = new ArrayList();
        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                tmpList.add(handleSinglePixel(x+i, y+j, pixels[j * w + i]));
            }
        }
        return tmpList.iterator();
    }
    
    private Pixel handleSinglePixel(int x, int y, int pixel) {
        int alpha = (pixel >> 24) & 0xff;
        int red   = (pixel >> 16) & 0xff;
        int green = (pixel >>  8) & 0xff;
        int blue  = (pixel      ) & 0xff;
        return new Pixel(alpha, red, green, blue);
    }
    
    private class Pixel {
        private int a;
        private int r;
        private int g;
        private int b;
        
        Pixel(int a, int r, int g, int b) {
            this.a = a;
            this.r = r;
            this.g = g;
            this.b = b;
        }
        
        protected boolean equals(Pixel p) {
            if(p.a == a && p.r == r && p.g == g && p.b == b)
                return true;
            return false;
        }
    }
}


