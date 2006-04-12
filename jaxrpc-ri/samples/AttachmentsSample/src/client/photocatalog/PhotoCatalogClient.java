/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


package photocatalog;

import com.example.photo.*;
import photocatalog.util.AttachmentHelper;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.*;
import com.sun.xml.rpc.client.StubBase;
import javax.xml.rpc.ServiceFactory;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MessageFactory;
import javax.xml.transform.stream.StreamSource;
import java.util.*;
import com.sun.xml.rpc.client.StubPropertyConstants;
import javax.activation.DataHandler;

public class PhotoCatalogClient {
    
    public static AttachmentHelper helper = new AttachmentHelper();
    PhotoCatalog_Stub stub = null;
    
    public static void main(String[] args) {
        try {
            System.setProperty("log.dir", "logs");
            System.setProperty("log.file", "messages.log");
            
            PhotoCatalogClient pcClient = new PhotoCatalogClient();
            pcClient.stub = (PhotoCatalog_Stub) (new PhotoCatalogService_Impl().getPhotoCatalogPort());           
            pcClient.invokeAddPhoto();
            pcClient.invokeReplacePhoto();
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void invokeAddPhoto() throws Exception {
        java.awt.Image newPhoto = getImage("duke1.jpg");
        javax.activation.DataHandler dh = stub.addPhoto(newPhoto);
        StreamSource statusSource = getStatusInXml();
        String expectedStatus = helper.getStringFromStreamSource(statusSource);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        dh.writeTo(baos);
        String receivedStatus = new String(baos.toByteArray());
        if (receivedStatus.equals(expectedStatus)) {
            System.out.println("New Photo is added sucessfully.");
        } 
        else {
            System.out.println("addPhoto operation failed");
        }        
    }
    
    public void invokeReplacePhoto() throws Exception {
        java.awt.Image newPhoto = getImage("duke2.jpg");
        java.net.URI oldPhotoRef= stub.replacePhoto(new PhotoInfo("duke", 1), newPhoto);
        Collection c = (Collection) ((PhotoCatalog_Stub) stub)._getProperty(
                StubPropertyConstants.GET_ATTACHMENT_PROPERTY);
        
        if (oldPhotoRef != null) {
            AttachmentPart att = helper.getAttachment(oldPhotoRef, c.iterator());
            if (att.getContent() instanceof Image) {
                Image oldPhoto = (Image) att.getContent();
                Image clientOldPhoto = getImage("duke1.jpg");
                boolean result = helper.compareImages( oldPhoto, clientOldPhoto, new Rectangle(0, 0, 100, 120));
                if( result == true)
                    System.out.println("Old Photo is sucessfully replaced");
            }
        } 
        else {
            System.out.println(" replacePhoto operation failed");
        }
    }
    
    private StreamSource getStatusInXml() {
        try {
            InputStream is = null;
            String location = getDataDir() + "status.fi";
            File f = new File(location);
            is = new FileInputStream(f);
            return new StreamSource(is);
        } 
        catch (Exception e) { 
            e.printStackTrace();
        }
        return null;
    }
    
    private static Image getImage(String imageName) throws Exception{
        String imageLocation = getDataDir() + imageName;
        return javax.imageio.ImageIO.read(new File(imageLocation));
    }
    
    private static String getDataDir() {
        String userDir = System.getProperty("user.dir");
        String sepChar = System.getProperty("file.separator");
        return userDir+sepChar+ "data/";
    }
}
