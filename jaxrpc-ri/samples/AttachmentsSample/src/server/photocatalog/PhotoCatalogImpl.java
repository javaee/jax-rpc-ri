/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package photocatalog;

import com.example.photo.PhotoCatalog;
import com.example.photo.PhotoInfo;
import photocatalog.util.AttachmentHelper;
import java.net.URI;
import javax.xml.transform.stream.StreamSource;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MessageFactory;
import javax.activation.DataHandler;
import java.io.*;
import java.awt.*;
import javax.xml.rpc.server.*;
import javax.xml.rpc.handler.MessageContext;
import javax.servlet.ServletContext;
import java.util.*;
import javax.xml.soap.*;
import com.sun.xml.rpc.server.ServerPropertyConstants;

import javax.xml.transform.Source;
import org.jvnet.fastinfoset.FastInfosetSource;

public class PhotoCatalogImpl implements PhotoCatalog , ServiceLifecycle {
    ServletEndpointContext servletEndpointContext = null;
    ServletContext servletContext = null;
    
    public void init(Object context) {
        servletEndpointContext = (ServletEndpointContext) context;
        servletContext = servletEndpointContext.getServletContext();
    }
    
    public void destroy() {
        servletEndpointContext = null;
        servletContext = null;
    }
    
    public javax.activation.DataHandler addPhoto(java.awt.Image photo) throws
            java.rmi.RemoteException{
        // DataHandler dh = new DataHandler(getStatusInXml(), "text/xml");
        DataHandler dh = new DataHandler(getStatusInXml(), "application/fastinfoset");
        return dh;
    }
    
    public java.net.URI replacePhoto(PhotoInfo photoinfo, java.awt.Image newPhoto) throws
            java.rmi.RemoteException
    {
        try {
            MessageContext mc = servletEndpointContext.getMessageContext();
            String imageName = photoinfo.getCustomerName() + photoinfo.getPhotoID() +".jpg";
            java.awt.Image oldPhoto = getImage(imageName);
            AttachmentPart att = MessageFactory.newInstance().createMessage().createAttachmentPart();
            att.setContentId("<" + imageName + ">");
            att.setContent(oldPhoto,"image/jpeg");
            ArrayList list = new ArrayList();
            list.add(att);
            mc.setProperty(ServerPropertyConstants.SET_ATTACHMENT_PROPERTY, list);
            java.net.URI retVal = new java.net.URI("cid:" + imageName);
            return retVal;
        }
        catch (Exception e){
            e.printStackTrace();
            //  replacePhoto Operation failed, return null
        }
        return null;
        
    }
    
    private Source getStatusInXml() {
        try {
            InputStream is = null;
            String location = getDataDir() + "status.fi";
            is = servletContext.getResourceAsStream(location);
            return new FastInfosetSource(is);
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private String getDataDir() {
        String userDir = System.getProperty("user.dir");
        String sepChar = System.getProperty("file.separator");
        return "/WEB-INF/";
    }
    
    private Image getImage(String imageName) throws Exception {
        java.awt.Image image = null;
        String location = getDataDir() + imageName;
        InputStream is = null;
        is = servletContext.getResourceAsStream(location);
        image = javax.imageio.ImageIO.read(is);
        return image;
    }
}
