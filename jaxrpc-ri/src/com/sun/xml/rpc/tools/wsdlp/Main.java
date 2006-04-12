/*
 * $Id: Main.java,v 1.1 2006-04-12 20:35:30 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.tools.wsdlp;

import java.io.File;
import java.io.IOException;
import java.util.MissingResourceException;

import javax.xml.namespace.QName;

import org.xml.sax.InputSource;

import com.sun.xml.rpc.util.Debug;
import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.localization.Localizer;
import com.sun.xml.rpc.util.localization.Resources;

import com.sun.xml.rpc.wsdl.document.WSDLDocument;
import com.sun.xml.rpc.wsdl.document.schema.SchemaDocument;
import com.sun.xml.rpc.wsdl.framework.Entity;
import com.sun.xml.rpc.wsdl.framework.ParseException;
import com.sun.xml.rpc.wsdl.framework.ParserListener;
import com.sun.xml.rpc.wsdl.framework.ValidationException;
import com.sun.xml.rpc.wsdl.parser.SchemaParser;
import com.sun.xml.rpc.wsdl.parser.SchemaWriter;
import com.sun.xml.rpc.wsdl.parser.SOAPEntityReferenceValidator;
import com.sun.xml.rpc.wsdl.parser.WSDLParser;
import com.sun.xml.rpc.wsdl.parser.WSDLWriter;

/**
 *
 * @author JAX-RPC Development Team
 */
public class Main {
    
    public static void main(String[] args) {
        try {
            Main tool = new Main();
            tool.run(args);
            System.exit(tool.succeeded() ? 0 : 1);
        } catch (MissingResourceException e) {
            System.err.println("wsdlp: resources not available");
            System.exit(2);
        }
    }
    
    public Main() throws MissingResourceException {
        _resources = new Resources("com.sun.xml.rpc.resources.wsdlp");
        _localizer = new Localizer();
    }
    
    public boolean succeeded() {
        return _succeeded;
    }
    
    public void run(String[] args) {
        _succeeded = false;
        
        if (!processArgs(args)) {
            return;
        }
        
        try {
            
            if (_parseSchema) {
                SchemaParser parser = new SchemaParser();
                
                if (_shouldValidate) {
                    parser.setFollowImports(true);
                }
                
                InputSource inputSource =
                new InputSource(new File(_sourceFilename).toURL().toString());
                SchemaDocument document = parser.parse(inputSource);
                document.validateLocally();
                if (_shouldValidate) {
                    
                    // perform global validation
                    document.validate(new SOAPEntityReferenceValidator());
                }
                
                if (_echo) {
                    SchemaWriter writer = new SchemaWriter();
                    writer.write(document, System.out);
                }
            } else {
                WSDLParser parser = new WSDLParser();
                
                if (_beVerbose) {
                    parser.addParserListener(new ParserListener() {
                        public void ignoringExtension(QName name,
                            QName parent) {
                                
                            System.err.println(_resources.getString(
                                "message.ignoring",
                                new String[] {
                                    name.getLocalPart(),
                                    name.getNamespaceURI()}));
                        }
                        
                        public void doneParsingEntity(QName element,
                            Entity entity) {
                                
                            System.err.println(_resources.getString(
                                "message.processed",
                                new String[] {
                                    element.getLocalPart(),
                                    element.getNamespaceURI()}));
                        }
                    });
                }
                
                if (_shouldValidate) {
                    parser.setFollowImports(true);
                }
                
                InputSource inputSource = new InputSource(
                    new File(_sourceFilename).toURL().toString());
                
                /* Added useWSIBasicProfile parameter to WSDLParser.parse()
                 * that it can generate warnings when f:wsi is set to true
                 * This is done to validate WSDL for wsi compliant
                 */
                WSDLDocument document =
                    parser.parse(inputSource, _useWSIBasicProfile);
                document.validateLocally();
                
                if (_shouldValidate) {
                    
                    // perform global validation
                    document.validate(new SOAPEntityReferenceValidator());
                }
                
                if (_echo) {
                    WSDLWriter writer = new WSDLWriter();
                    writer.write(document, System.out);
                }
            }
            
            _succeeded = true;
        } catch (ParseException e) {
            System.err.println(_resources.getString(
                "error.parsing", _localizer.localize(e)));
        } catch (ValidationException e) {
            System.err.println(_resources.getString(
                "error.validation", _localizer.localize(e)));
        } catch (JAXRPCExceptionBase e) {
            System.err.println(_resources.getString(
                "error.generic", _localizer.localize(e)));
        } catch (IOException e) {
            System.err.println(_resources.getString("error.io", e.toString()));
        } catch (Exception e) {
            System.err.println(_resources.getString(
                "error.generic", e.toString()));
            if (Debug.enabled()) {
                e.printStackTrace();
            }
        }
    }
    
    private boolean processArgs(String[] args) {
        if (args.length == 0) {
            help();
            return false;
        }
        
        int ac = 0;
        while (ac < args.length) {
            String arg = args[ac];
            ac++;
            if (arg.startsWith("-")) {
                if (arg.equals("-help")) {
                    help();
                    _succeeded = true;
                    return false;
                } else if (arg.equals("-echo")) {
                    _echo = true;
                } else if (arg.equals("-schema")) {
                    _parseSchema = true;
                } else if (arg.equals("-validate")) {
                    _shouldValidate = true;
                } else if (arg.equals("-v") || arg.equals("-verbose")) {
                    _beVerbose = true;
                } else if (arg.equals("-version")) {
                    System.err.println(_resources.getString("message.version"));
                    _succeeded = true;
                    return false;
                } else if(arg.equals("-wsi")) {
                    _useWSIBasicProfile = true;
                } else {
                    usageError("error.invalidOption", arg);
                    return false;
                }
            } else {
                if (_sourceFilename != null) {
                    usageError("error.multipleFilenames", null);
                    return false;
                }
                _sourceFilename = arg;
            }
        }
        
        if (_sourceFilename == null) {
            usageError("error.missingFilename", null);
            return false;
        }
        
        //ready to go
        return true;
    }
    
    private void help() {
        System.err.println(_resources.getString("message.header"));
        System.err.println(_resources.getString("message.usage"));
    }
    
    private void printError(String msg) {
        System.err.println(_resources.getString("message.name") + ": " + msg);
    }
    
    private void usageError(String key, String arg) {
        printError(_resources.getString(key, arg));
    }
    
    private boolean _succeeded;
    private Localizer _localizer;
    private Resources _resources;
    private String _sourceFilename;
    private boolean _shouldValidate;
    private boolean _beVerbose;
    private boolean _echo;
    private boolean _parseSchema;
    private boolean _useWSIBasicProfile;
}
