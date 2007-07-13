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
