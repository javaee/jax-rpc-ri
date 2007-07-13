/*
 * $Id: ToolPluginFactory.java,v 1.3 2007-07-13 23:36:35 ofung Exp $
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
 
package com.sun.xml.rpc.tools.plugin;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.streaming.Attributes;
import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLReaderFactory;
import com.sun.xml.rpc.streaming.XMLReaderUtil;
import com.sun.xml.rpc.util.localization.LocalizableMessageFactory;
import com.sun.xml.rpc.util.localization.Localizer;

/**
 * @author JAX-RPC Development Team
 *
 */
public class ToolPluginFactory {
    
    private Map pluginMap;          // id --> ToolPluginAll
    
    private static Logger logger;
    private static Localizer localizer;
    private static LocalizableMessageFactory messageFactory;
    public static final String NS_NAME =
        "http://java.sun.com/xml/ns/jax-rpc/ri/tool-plugin";
    
    private static final QName QNAME_TOOL_PLUGINS =
        new QName(NS_NAME, "toolPlugins");
    private static final QName QNAME_TOOL_PLUGIN =
        new QName(NS_NAME, "toolPlugin");
    private static final QName QNAME_EXTENSION_POINT =
        new QName(NS_NAME, "extensionPoint");
    private static final QName QNAME_EXTENSION =
        new QName(NS_NAME, "extension");
    private static final String ATTR_EXTEND_ID = "extendId";
    private static final String ATTR_EXTEND_TYPE = "type";
    private static final String ATTR_PLUGIN_ID = "pluginId";
    private static final String ATTR_CLASS_NAME = "class";
    
    
    private static ToolPluginFactory factory = new ToolPluginFactory();
    
    private class ExtensionPointTag {
        private String extendId;
        private String extendType;
        private List implementors;          // List of ToolPluginAll objs
        
        public ExtensionPointTag(String extendId, String extendType) {
            this.extendId = extendId;
            this.extendType = extendType;
        }
        
        public String toString() {
            return extendId+":"+extendType+":"+implementors;
        }
    }
    
    private class ExtensionTag {
        private String pluginId;
        private String extendId;
        
        public ExtensionTag(String pluginId, String extendId) {
            this.pluginId = pluginId;
            this.extendId = extendId;
        }
        
        public String toString() {
            return pluginId+":"+extendId;
        }
    }
    
    private class ToolPluginTag {
        private String pluginId;
        private String className;
        private Map extensionPointMap;  // extendId --> ExtensionPointTag
        private List extensionsList;    // List of ExtensionTag objs
        
        public ToolPluginTag(String pluginId, String className) {
            this.pluginId = pluginId;
            this.className = className;
        }
        
    }
    
    private class ToolPluginAll {
        private ToolPlugin toolPlugin;
        private Map extensionsMap;    // extendId --> ToolPlugin
        private ToolPluginTag toolPluginTag;
        
        public ToolPluginAll(ToolPluginTag toolPluginTag) {
            this.toolPluginTag = toolPluginTag;
        }
        
        public ToolPlugin getToolPlugin() {
            if (toolPlugin == null) {
                ClassLoader cll =
                    Thread.currentThread().getContextClassLoader();
                try {
                    Class cl = cll.loadClass(toolPluginTag.className);
                    toolPlugin = (ToolPlugin)cl.newInstance();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            return toolPlugin;
        }
        
        public Iterator getExtensions(String extendId) {
            if (toolPluginTag.extensionPointMap == null) {
                return null;
            }
            ExtensionPointTag ExtensionPointTag = (ExtensionPointTag)
                toolPluginTag.extensionPointMap.get(extendId);
            if (ExtensionPointTag == null) {
                return null;
            }
            List allList = ExtensionPointTag.implementors;
            if (allList == null) {
                return null;
            }
            if (extensionsMap == null) {
                extensionsMap = new HashMap();
            }
            List implList = (List)extensionsMap.get(extendId);
            if (implList == null) {
                implList = new ArrayList();
                Iterator i = allList.iterator();
                while(i.hasNext()) {
                    ToolPluginAll implAll = (ToolPluginAll)i.next();
                    ToolPlugin toolPlugin = implAll.getToolPlugin();
                    implList.add(toolPlugin);
                }
                extensionsMap.put(extendId, implList);
            }
            return implList.iterator();
        }
    }
    
    
    private ToolPluginFactory() {
        messageFactory = new LocalizableMessageFactory(
            "com.sun.xml.rpc.resources.toolplugin");
        logger = Logger.getLogger(com.sun.xml.rpc.util.Constants.LoggingDomain +
            ".toolplugin");
        localizer = new Localizer();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try {
            Enumeration urls = loader.getResources("META-INF/jaxrpc/ToolPlugin.xml");
            while (urls != null && urls.hasMoreElements()) {
                URL url = (URL) urls.nextElement();
                InputStream in = new BufferedInputStream(url.openStream());
                XMLReader reader =
                    XMLReaderFactory.newInstance().createXMLReader(in);
                reader.next();
                QName tag = reader.getName();
                if (tag.equals(QNAME_TOOL_PLUGINS)) {
                    parseToolPlugins(reader);
                }
                reader.nextElementContent();
                XMLReaderUtil.verifyReaderState(reader, XMLReader.EOF);
                in.close();
            }
            linkExtensionsWithExtensionPoints();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void linkExtensionsWithExtensionPoints() {
        if (pluginMap != null) {
            Iterator i = pluginMap.entrySet().iterator();
            while(i.hasNext()) {
                Map.Entry entry = (Map.Entry)i.next();
                ToolPluginAll toolPluginAll = (ToolPluginAll)entry.getValue();
                List list = toolPluginAll.toolPluginTag.extensionsList;
                if (list != null) {
                    Iterator j = list.iterator();
                    while(j.hasNext()) {
                        ExtensionTag extensionTag = (ExtensionTag)j.next();
                        if (toolPluginAll.toolPluginTag.pluginId.equals(
                            extensionTag.pluginId)) {
                                
                            if (logger.isLoggable(Level.WARNING)) {
                                logger.warning(localizer.localize(
                                    messageFactory.getMessage(
                                        "no.self.extension",
                                        extensionTag.pluginId)));
                            }
                            continue;
                        }
                        setExtensionImplRef(extensionTag.pluginId,
                            extensionTag.extendId,
                            toolPluginAll);
                    }
                }
            }
        }
    }
    
    private void setExtensionImplRef(String pluginId, String extendId,
        ToolPluginAll impl) {
        
        ToolPluginAll base = (ToolPluginAll)pluginMap.get(pluginId);
        if (base != null) {
            ToolPluginTag toolPluginTag = (ToolPluginTag)base.toolPluginTag;
            if (toolPluginTag.extensionPointMap != null) {
                ExtensionPointTag tag = (ExtensionPointTag)
                    toolPluginTag.extensionPointMap.get(extendId);
                if (tag != null) {
                    if (tag.implementors == null) {
                        tag.implementors = new ArrayList();
                    }
                    tag.implementors.add(impl);
                } else {
                    if (logger.isLoggable(Level.WARNING)) {
                        logger.warning(localizer.localize(
                            messageFactory.getMessage("unknown.extensionPoint",
                                extendId)));
                    }
                }
            } else {
                if (logger.isLoggable(Level.WARNING)) {
                    logger.warning(localizer.localize(messageFactory.getMessage(
                        "unknown.extensionPoint", extendId)));
                }
            }
        } else {
            if (logger.isLoggable(Level.WARNING)) {
                logger.warning(localizer.localize(messageFactory.getMessage(
                    "unknown.plugin", pluginId)));
            }
        }
        
    }
    
    private ExtensionTag parseExtension(XMLReader reader) {
        Attributes attrs = reader.getAttributes();
        String pluginId = attrs.getValue(ATTR_PLUGIN_ID);
        String extendId = attrs.getValue(ATTR_EXTEND_ID);
        if (pluginId == null || extendId == null) {
            if (logger.isLoggable(Level.WARNING)) {
                logger.warning(localizer.localize(messageFactory.getMessage(
                    "no.pluginId.or.extendId")));
            }
        }
        reader.nextElementContent();
        XMLReaderUtil.verifyReaderState(reader, XMLReader.END);
        ExtensionTag extensionTag = new ExtensionTag(
        pluginId, extendId);
        return extensionTag;
    }
    
    private ExtensionPointTag parseExtendPoint(XMLReader reader) {
        Attributes attrs = reader.getAttributes();
        String extendId = attrs.getValue(ATTR_EXTEND_ID);
        String extendType = attrs.getValue(ATTR_EXTEND_TYPE);
        if (extendId == null || extendType == null) {
            if (logger.isLoggable(Level.WARNING)) {
                logger.warning(localizer.localize(messageFactory.getMessage(
                    "no.extendId.or.extendType")));
            }
        }
        reader.nextElementContent();
        XMLReaderUtil.verifyReaderState(reader, XMLReader.END);
        ExtensionPointTag ExtensionPointTag =
            new ExtensionPointTag(extendId, extendType);
        return ExtensionPointTag;
    }
    
    private  void parseToolPlugins(XMLReader reader) {
        while (reader.nextElementContent() == XMLReader.START) {
            
            QName tag = reader.getName();
            if (tag.equals(QNAME_TOOL_PLUGIN)) {
                parseToolPlugin(reader);
            } else {
                if (logger.isLoggable(Level.WARNING)) {
                    logger.warning(localizer.localize(messageFactory.getMessage(
                        "unknown.tag", tag.toString())));
                }
            }
        }
        XMLReaderUtil.verifyReaderState(reader, XMLReader.END);
    }
    
    private void parseToolPlugin(XMLReader reader) {
        Attributes attrs = reader.getAttributes();
        String pluginId = attrs.getValue(ATTR_PLUGIN_ID);
        String className = attrs.getValue(ATTR_CLASS_NAME);
        ToolPluginTag toolPluginTag =
            this.new ToolPluginTag(pluginId, className);
        while (reader.nextElementContent() == XMLReader.START) {
            QName tag = reader.getName();
            if (tag.equals(QNAME_EXTENSION_POINT)) {
                ExtensionPointTag ExtensionPointTag = parseExtendPoint(reader);
                if (toolPluginTag.extensionPointMap == null) {
                    toolPluginTag.extensionPointMap = new HashMap();
                }
                toolPluginTag.extensionPointMap.put(ExtensionPointTag.extendId,
                    ExtensionPointTag);
            } else if (tag.equals(QNAME_EXTENSION)) {
                ExtensionTag ExtensionTag = parseExtension(reader);
                if (toolPluginTag.extensionsList == null) {
                    toolPluginTag.extensionsList = new ArrayList();
                }
                toolPluginTag.extensionsList.add(ExtensionTag);
            } else {
                if (logger.isLoggable(Level.WARNING)) {
                    logger.warning(localizer.localize(messageFactory.getMessage(
                        "unknown.tag", tag.toString())));
                }
            }
        }
        XMLReaderUtil.verifyReaderState(reader, XMLReader.END);
        if (pluginMap == null) {
            pluginMap = new HashMap();
        }
        pluginMap.put(pluginId, new ToolPluginAll(toolPluginTag));
    }
    
    
    public ToolPlugin getPlugin(String pluginId) {
        ToolPlugin toolPlugin = null;
        if (pluginMap != null) {
            ToolPluginAll toolPluginAll =
                (ToolPluginAll) pluginMap.get(pluginId);
            if (toolPluginAll != null) {
                toolPlugin = toolPluginAll.getToolPlugin();
            }
        }
        return toolPlugin;
    }
    
    public Iterator getExtensions(String pluginId, String extendId) {
        Iterator i = null;
        if (pluginMap != null) {
            ToolPluginAll toolPluginAll =
                (ToolPluginAll) pluginMap.get(pluginId);
            if (toolPluginAll != null) {
                ToolPlugin toolPlugin = toolPluginAll.getToolPlugin();
                if (toolPlugin != null) {
                    return toolPluginAll.getExtensions(extendId);
                }
            }
        }
        return i;
    }
    
    private void printAll() {
        System.out.println("All Plugins");
        System.out.println("===========");
        if (pluginMap != null) {
            Iterator i = pluginMap.entrySet().iterator();
            while(i.hasNext()) {
                Map.Entry entry = (Map.Entry)i.next();
                System.out.println("pluginId="+entry.getKey());
                System.out.println("------------------------");
                ToolPluginAll toolPluginAll = (ToolPluginAll)entry.getValue();
                System.out.println("toolPlugin="+toolPluginAll.toolPlugin);
                System.out.println("extensionPointMap="+
                    toolPluginAll.toolPluginTag.extensionPointMap);
                System.out.println("extensionsList="+
                    toolPluginAll.toolPluginTag.extensionsList);
            }
        }
        
    }
    
    public static ToolPluginFactory getInstance() {
        if (factory == null) {
            factory = new ToolPluginFactory();
        }
        return factory;
    }
    
    
}
