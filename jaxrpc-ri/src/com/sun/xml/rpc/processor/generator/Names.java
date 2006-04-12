/*
 * $Id: Names.java,v 1.1 2006-04-12 20:33:45 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.generator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.Block;
import com.sun.xml.rpc.processor.model.Fault;
import com.sun.xml.rpc.processor.model.ModelProperties;
import com.sun.xml.rpc.processor.model.Operation;
import com.sun.xml.rpc.processor.model.Port;
import com.sun.xml.rpc.processor.model.Service;
import com.sun.xml.rpc.processor.model.java.JavaArrayType;
import com.sun.xml.rpc.processor.model.java.JavaInterface;
import com.sun.xml.rpc.processor.model.java.JavaStructureMember;
import com.sun.xml.rpc.processor.model.java.JavaType;
import com.sun.xml.rpc.processor.model.literal.LiteralFragmentType;
import com.sun.xml.rpc.processor.model.literal.LiteralType;
import com.sun.xml.rpc.processor.model.soap.SOAPCustomType;
import com.sun.xml.rpc.processor.model.soap.SOAPType;
import com.sun.xml.rpc.processor.util.DirectoryUtil;
import com.sun.xml.rpc.processor.util.ProcessorEnvironment;
import com.sun.xml.rpc.processor.util.StringUtils;
import com.sun.xml.rpc.streaming.PrefixFactory;
import com.sun.xml.rpc.streaming.PrefixFactoryImpl;
import com.sun.xml.rpc.util.ClassNameInfo;

/**
 * Names provides utility methods used by other wscompile classes
 * for dealing with identifiers.
 *
 * @author JAX-RPC Development Team
 */
public class Names
    implements com.sun.xml.rpc.spi.tools.Names, GeneratorConstants {

    public Names() {
    }

    /**
     * Return stub class name for impl class name.
     */
    public String stubFor(com.sun.xml.rpc.spi.model.Port port) {
        return stubFor((Port) port, null);
    }

    public String stubFor(Port port, String infix) {
        String result =
            (String) port.getProperty(ModelProperties.PROPERTY_STUB_CLASS_NAME);
        if (result == null) {
            result =
                makeDerivedClassName(
                    port.getJavaInterface(),
                    STUB_SUFFIX,
                    infix);
        }
        return result;
    }

    /**
     * Return skeleton class name for impl class name.
     */
    public String skeletonFor(JavaInterface javaInterface) {
        String name =
            ClassNameInfo.replaceInnerClassSym(javaInterface.getRealName());
        return name + SKELETON_SUFFIX;
    }

    /**
     * Return tie class name for impl class name.
     */
    public String tieFor(Port port) {
        return tieFor(port, serializerNameInfix);
    }

    public String tieFor(Port port, String infix) {
        String result =
            (String) port.getProperty(ModelProperties.PROPERTY_TIE_CLASS_NAME);
        if (result == null) {
            result =
                makeDerivedClassName(
                    port.getJavaInterface(),
                    TIE_SUFFIX,
                    infix);
        }
        return result;
    }

    public String makeDerivedClassName(
        JavaInterface javaInterface,
        String suffix,
        String infix) {
        String name =
            ClassNameInfo.replaceInnerClassSym(javaInterface.getRealName());
        return name + (infix == null ? "" : UNDERSCORE + infix) + suffix;
    }

    public static String getPortName(Port port) {
        String javaPortName =
            (String) port.getProperty(ModelProperties.PROPERTY_JAVA_PORT_NAME);
        if (javaPortName != null) {
            return javaPortName;
        } else {
            QName portName =
                (QName) port.getProperty(
                    ModelProperties.PROPERTY_WSDL_PORT_NAME);
            if (portName != null) {
                return portName.getLocalPart();
            } else {
                String name = stripQualifier(port.getJavaInterface().getName());
                return ClassNameInfo.replaceInnerClassSym(name);
            }
        }
    }

    public static String stripQualifier(Class classObj) {
        String name = classObj.getName();
        return stripQualifier(name);
    }

    public static String stripQualifier(String name) {
        return ClassNameInfo.getName(name);
    }

    public static String getPackageName(String className) {
        String packageName = ClassNameInfo.getQualifier(className);
        return packageName != null ? packageName : "";
    }

    public static String getUnqualifiedClassName(String className) {
        return ClassNameInfo.getName(className).replace('$', '.');
    }

    /**
     * Return the File object that should be used as the source file
     * for the given Java class, using the supplied destination
     * directory for the top of the package hierarchy.
     */
    public File sourceFileForClass(
        String className,
        String outputClassName,
        File destDir,
        ProcessorEnvironment env)
        throws GeneratorException {
        File packageDir =
            DirectoryUtil.getOutputDirectoryFor(className, destDir, env);
        String outputName = stripQualifier(outputClassName);

        String outputFileName = outputName + JAVA_SRC_SUFFIX;
        return new File(packageDir, outputFileName);
    }

    public String typeClassName(SOAPType type) {
        return typeClassName(type.getJavaType());
    }

    public String typeClassName(JavaType type) {
        String typeName = type.getName();
        return typeName;
    }

    public String typeObjectSerializerClassName(
        String basePackage,
        SOAPType type) {
        return typeObjectSerializerClassName(
            basePackage,
            type.getJavaType(),
            SOAP_SERIALIZER_SUFFIX);
    }

    public String typeObjectArraySerializerClassName(
        String basePackage,
        SOAPType type) {
        return typeObjectArraySerializerClassName(
            basePackage,
            type.getJavaType(),
            ARRAY_SOAP_SERIALIZER_SUFFIX);
    }

    public String typeObjectSerializerClassName(
        String basePackage,
        LiteralType type) {
        return typeObjectSerializerClassName(
            basePackage,
            type.getJavaType(),
            LITERAL_SERIALIZER_SUFFIX);
    }

    public String typeObjectArraySerializerClassName(
        String basePackage,
        LiteralType type) {
        return typeObjectArraySerializerClassName(
            basePackage,
            type.getJavaType(),
            ARRAY_LITERAL_SERIALIZER_SUFFIX);
    }

    public String typeObjectSerializerClassName(
        String basePackage,
        JavaType type,
        String suffix) {
        String typeName = type.getRealName();
        return serializerClassName(basePackage, typeName, suffix);
    }

    public String typeInterfaceSerializerClassName(
        String basePackage,
        AbstractType type) {
        return typeInterfaceSerializerClassName(
            basePackage,
            type.getJavaType(),
            SOAP_INTERFACE_SERIALIZER_SUFFIX);
    }

    public String typeInterfaceSerializerClassName(
        String basePackage,
        JavaType type,
        String suffix) {
        String typeName = type.getRealName();
        return serializerClassName(basePackage, typeName, suffix);
    }

    protected String serializerClassName(
        String basePackage,
        String className,
        String suffix) {
        if (serializerNameInfix != null)
            className += serializerNameInfix;
        return (className + suffix).replace('$', '_');
    }

    public String typeObjectArraySerializerClassName(
        String basePackage,
        JavaType type,
        String suffix) {
        String typeName = type.getRealName();
        int idx = typeName.indexOf(BRACKETS);
        if (idx > 0) {
            typeName = typeName.substring(0, idx);
        }
        return serializerClassName(basePackage, typeName, suffix);
    }

    public String typeObjectBuilderClassName(
        String basePackage,
        SOAPType type) {
        return typeObjectBuilderClassName(basePackage, type.getJavaType());
    }

    public String typeObjectBuilderClassName(
        String basePackage,
        JavaType type) {
        return builderClassName(
            basePackage,
            type.getRealName(),
            SOAP_BUILDER_SUFFIX);
    }

    protected String builderClassName(
        String basePackage,
        String className,
        String suffix) {
        if (serializerNameInfix != null)
            className += serializerNameInfix;
        return (className + suffix).replace('$', '_');
    }

    public String faultBuilderClassName(
        String basePackage,
        Port port,
        Operation operation) {
        String typeName =
            port.getJavaInterface().getName()
                + UNDERSCORE
                + validExternalJavaIdentifier(operation.getUniqueName());
        return builderClassName(basePackage, typeName, FAULT_BUILDER_SUFFIX);
    }

    public String faultSerializerClassName(
        String basePackage,
        Port port,
        Operation operation) {
        String name =
            port.getJavaInterface().getName()
                + UNDERSCORE
                + validExternalJavaIdentifier(operation.getUniqueName());
        return serializerClassName(
            basePackage,
            name,
            FAULT_SOAPSERIALIZER_SUFFIX);
    }

    public static String getPackageName(Service service) {
        String portPackage =
            getPackageName(service.getJavaInterface().getName());
        return portPackage;
    }

    public String customJavaTypeClassName(JavaInterface intf) {
        String intName = intf.getName();
        return intName;
    }

    public String customJavaTypeClassName(AbstractType type) {
        String typeName = type.getJavaType().getName();
        return typeName;
    }

    private String customJavaTypeClassName(String typeName) {
        return typeName;
    }

    public String customExceptionClassName(Fault fault) {
        String typeName = fault.getJavaException().getName();
        return typeName;
    }

    public String interfaceImplClassName(
        com.sun.xml.rpc.spi.model.JavaInterface intf) {
        String intName = intf.getName() + IMPL_SUFFIX;
        return intName;
    }

    public String serializerRegistryClassName(JavaInterface intf) {
        String intName = intf.getName() + SERIALIZER_REGISTRY_SUFFIX;
        return intName;
    }

    public String holderClassName(Port port, AbstractType type) {
        return holderClassName(port, type.getJavaType());
    }

    public String holderClassName(Port port, JavaType type) {
        if (type.getHolderName() != null)
            return type.getHolderName();
        //bug fix:4904604
        String typeName = type.getName();
        if (type instanceof JavaArrayType) {
            if (((JavaArrayType) type).getSOAPArrayHolderName() != null)
                typeName = ((JavaArrayType) type).getSOAPArrayHolderName();
        }
        return holderClassName(port, typeName);
    }

    protected String holderClassName(Port port, String typeName) {
        String holderTypeName = (String) holderClassNames.get(typeName);
        if (holderTypeName == null) {
            // not a built-in holder class
            String className = port.getJavaInterface().getName();
            String packageName = getPackageName(className);
            if (packageName.length() > 0) {
                packageName += ".holders.";
            } else {
                packageName = "holders.";
            }
            typeName = stripQualifier(typeName);
            int idx = typeName.indexOf(BRACKETS);
            while (idx > 0) {
                //bug fix:4904604
                typeName =
                    typeName.substring(0, idx)
                        + ARRAY
                        + typeName.substring(idx + 2);
                idx = typeName.indexOf(BRACKETS);
            }
            //bug fix:4904604
            holderTypeName =
                packageName + validJavaClassName(typeName) + HOLDER_SUFFIX;
        }
        return holderTypeName;
    }

    public static boolean isInJavaOrJavaxPackage(String typeName) {
        return typeName.startsWith(JAVA_PACKAGE_PREFIX)
            || typeName.startsWith(JAVAX_PACKAGE_PREFIX);
    }

    public String memberName(String name) {
        return (MEMBER_PREFIX + name).replace('.', '$');
    }

    public String getClassMemberName(String className) {
        className = getUnqualifiedClassName(className);
        return memberName(className);
    }

    public String getClassMemberName(
        String className,
        AbstractType type,
        String suffix) {
        className = getUnqualifiedClassName(className);
        String additionalClassName =
            type.getJavaType().getName().replace('.', '_');
        int idx = additionalClassName.indexOf('[');
        if (idx > 0)
            additionalClassName = additionalClassName.substring(0, idx);
        return memberName(
            getPrefix(type.getName())
                + UNDERSCORE
                + validJavaName(type.getName().getLocalPart())
                + "__"
                + additionalClassName
                + UNDERSCORE
                + className
                + suffix);
    }

    public String getClassMemberName(String className, AbstractType type) {
        className = getUnqualifiedClassName(className);
        return getClassMemberName(
            getPrefix(type.getName())
                + UNDERSCORE
                + validJavaName(type.getName().getLocalPart())
                + "__"
                + className);
    }

    public String getTypeMemberName(AbstractType type) {
        return getTypeMemberName(type.getJavaType());
    }

    public String getTypeMemberName(JavaType javaType) {
        String typeName = javaType.getRealName();
        return getTypeMemberName(typeName);
    }

    /* this fix was implemented with respect to the fact that
       for StringArray ... when wsdl -> javamapping happens and
       the generated code would create variables[]name : which
       which were wrong */
    public String getTypeMemberName(String typeName) {
        typeName = getUnqualifiedClassName(typeName);
        int i = 0;
        while (typeName.endsWith(BRACKETS)) {
            typeName = typeName.substring(0, typeName.length() - 2);
            i++;
        }
        for (; i < 0; i--)
            typeName += ARRAY;

        return memberName(typeName);
    }

    public String getCustomTypeSerializerMemberName(SOAPCustomType type) {
        return getTypeQName(type.getName()) + SERIALIZER_SUFFIX;
    }

    public String getCustomTypeDeserializerMemberName(SOAPCustomType type) {
        return getTypeQName(type.getName()) + DESERIALIZER_SUFFIX;
    }

    public String getLiteralFragmentTypeSerializerMemberName(LiteralFragmentType type) {
        return getTypeQName(type.getName()) + SERIALIZER_SUFFIX;
    }

    public String getOPCodeName(String name) {
        String qname = name + OPCODE_SUFFIX;
        return validInternalJavaIdentifier(qname);
    }

    public String getQNameName(QName name) {
        String qname =
            getPrefix(name) + UNDERSCORE + name.getLocalPart() + QNAME_SUFFIX;
        return validInternalJavaIdentifier(qname);
    }

    public String getBlockQNameName(Operation operation, Block block) {
        QName blockName = block.getName();
        String qname = getPrefix(blockName);
        if (operation != null)
            qname += UNDERSCORE + operation.getUniqueName();
        qname += UNDERSCORE + blockName.getLocalPart() + QNAME_SUFFIX;
        return validInternalJavaIdentifier(qname);
    }

    public void setJavaStructureMemberMethodNames(JavaStructureMember javaMember) {
        javaMember.setReadMethod(getJavaMemberReadMethod(javaMember));
        javaMember.setWriteMethod(getJavaMemberWriteMethod(javaMember));
    }

    public String getBlockUniqueName(Operation operation, Block block) {
        QName blockName = block.getName();
        String qname = getPrefix(blockName);
        if (operation != null)
            qname += UNDERSCORE + operation.getUniqueName();
        qname += UNDERSCORE + blockName.getLocalPart();
        return validInternalJavaIdentifier(qname);
    }

    public String getTypeQName(QName name) {
        String qname =
            getPrefix(name)
                + UNDERSCORE
                + name.getLocalPart()
                + TYPE_QNAME_SUFFIX;
        return validInternalJavaIdentifier(qname);
    }

    public String validJavaClassName(String name) {
        return validJavaName(StringUtils.capitalize(name));
    }

    public String validJavaMemberName(String name) {
        return validJavaName(StringUtils.decapitalize(name));
    }

    public String validJavaPackageName(String name) {
        return validJavaName(StringUtils.decapitalize(name));
    }

    public String getIDObjectResolverName(String name) {
        return validJavaClassName(name) + "IDObjectResolver";
    }
    public String validInternalJavaIdentifier(String name) {
        // return a valid java identifier without dropping characters (i.e. do not apply
        // the mapping of XML names to Java identifiers in the spec); it's only meant
        // to be used to generate internal identifiers (e.g. variable names in generated code)

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < name.length(); ++i) {
            char ch = name.charAt(i);
            if (i == 0) {
                if (Character.isJavaIdentifierStart(ch)) {
                    sb.append(ch);
                } else {
                    sb.append("_$");
                    sb.append(Integer.toHexString((int) ch));
                    sb.append("$");
                }
            } else {
                if (Character.isJavaIdentifierPart(ch)) {
                    sb.append(ch);
                } else {
                    sb.append("$");
                    sb.append(Integer.toHexString((int) ch));
                    sb.append("$");
                }
            }
        }

        String id = sb.toString();

        String tmp = (String) reservedWords.get(id);
        if (tmp != null)
            id = tmp;
        return id;
    }

    public String validExternalJavaIdentifier(String name) {
        return validInternalJavaIdentifier(name).replace('$', '_');
    }

    public String validJavaName(String name) {
        name = wordBreakString(name);
        name = removeWhiteSpace(name);

        String tmp = (String) reservedWords.get(name);
        if (tmp != null)
            name = tmp;
        return name;
    }

    public boolean isJavaReservedWord(String name) {
        return reservedWords.get(name) != null;
    }

    /* here we check on wether return values datatype is
       boolean. If its boolean, instead of a get method
       its set a is<MethodName> to comply with JavaBeans
       Pattern spec */
    public String getJavaMemberReadMethod(JavaStructureMember member) {
        String return_value = null;
        if ((member.getType().getRealName()) == "boolean") {
            return_value = IS + StringUtils.capitalize(member.getName());
        } else {
            return_value = GET + StringUtils.capitalize(member.getName());
        }
        return (return_value);
    }

    public String getJavaMemberWriteMethod(JavaStructureMember member) {
        return SET + StringUtils.capitalize(member.getName());
    }

    public String getResponseName(String messageName) {
        return messageName + RESPONSE;
    }

    public String removeWhiteSpace(String str) {
        String tmp = removeCharacter(' ', str);
        return tmp;
    }

    public String wordBreakString(String str) {
        StringBuffer buf = new StringBuffer(str);
        char ch;
        for (int i = 0; i < buf.length(); i++) {
            ch = buf.charAt(i);
            if (Character.isDigit(ch)) {
                if (i + 1 < buf.length()
                    && !Character.isDigit(buf.charAt(i + 1))) {
                    buf.insert(1 + i++, ' ');
                }
            } else if (Character.isSpaceChar(ch) || ch == '_') {
                continue;
            } else if (!Character.isJavaIdentifierPart(ch)) {
                buf.setCharAt(i, ' ');
            } else if (!Character.isLetter(ch)) {
                buf.setCharAt(i, ' ');
            }
        }
        return buf.toString();
    }

    public String removeCharacter(int ch, String str) {
        String tmp;
        int idx = str.indexOf(ch);
        while (idx >= 0) {
            str =
                str.substring(0, idx)
                    + StringUtils.capitalize(str.substring(idx + 1).trim());
            idx = str.indexOf(' ');
        }

        return str;
    }

    public String getPrefix(QName name) {
        return getPrefix(name.getNamespaceURI());
    }

    public String getPrefix(String uri) {
        return prefixFactory.getPrefix(uri);
    }

    public void resetPrefixFactory() {
        prefixFactory = new PrefixFactoryImpl(NS_PREFIX);
    }

    public void setSerializerNameInfix(String serNameInfix) {
        if (serNameInfix != null && serNameInfix.length() > 0)
            serializerNameInfix = UNDERSCORE + serNameInfix;
    }

    public String getSerializerNameInfix() {
        // Fix for bug 4811625 and 4778136, undoing what setter does (remove beginning underscore)
        String str = serializerNameInfix;
        if ((serializerNameInfix != null)
            && (serializerNameInfix.charAt(0) == '_'))
            str = serializerNameInfix.substring(1);
        return str;
    }

    //bug fix: 4865124
    public static String getAdjustedURI(String namespaceURI, String pkgName) {
        if (pkgName == null)
            return namespaceURI;
        else if (namespaceURI == null)
            return pkgName;

        int length = namespaceURI.length();
        int i = namespaceURI.lastIndexOf('/');
        if (i == -1) {
            //check if its URN
            i = namespaceURI.lastIndexOf(':');
            if (i == -1) {
                //not a URI or URN pattern, return pkgName
                return pkgName;
            }

        }

        if ((i != -1) && (i + 1 == length)) {
            return namespaceURI + pkgName;
        } else {
            int j = namespaceURI.indexOf('.', i);
            if (j != -1)
                return namespaceURI.substring(0, i + 1) + pkgName;
            else
                return namespaceURI + "/" + pkgName;
        }
    }

    protected String serializerNameInfix = null;
    protected PrefixFactory prefixFactory = new PrefixFactoryImpl(NS_PREFIX);
    protected static Map reservedWords;
    protected static Map holderClassNames;

    static {
        reservedWords = new HashMap();
        reservedWords.put("abstract", "_abstract");
        reservedWords.put("assert", "_assert");
        reservedWords.put("boolean", "_boolean");
        reservedWords.put("break", "_break");
        reservedWords.put("byte", "_byte");
        reservedWords.put("case", "_case");
        reservedWords.put("catch", "_catch");
        reservedWords.put("char", "_char");
        reservedWords.put("class", "_class");
        reservedWords.put("const", "_const");
        reservedWords.put("continue", "_continue");
        reservedWords.put("default", "_default");
        reservedWords.put("do", "_do");
        reservedWords.put("double", "_double");
        reservedWords.put("else", "_else");
        reservedWords.put("extends", "_extends");
        reservedWords.put("false", "_false");
        reservedWords.put("final", "_final");
        reservedWords.put("finally", "_finally");
        reservedWords.put("float", "_float");
        reservedWords.put("for", "_for");
        reservedWords.put("goto", "_goto");
        reservedWords.put("if", "_if");
        reservedWords.put("implements", "_implements");
        reservedWords.put("import", "_import");
        reservedWords.put("instanceof", "_instanceof");
        reservedWords.put("int", "_int");
        reservedWords.put("interface", "_interface");
        reservedWords.put("long", "_long");
        reservedWords.put("native", "_native");
        reservedWords.put("new", "_new");
        reservedWords.put("null", "_null");
        reservedWords.put("package", "_package");
        reservedWords.put("private", "_private");
        reservedWords.put("protected", "_protected");
        reservedWords.put("public", "_public");
        reservedWords.put("return", "_return");
        reservedWords.put("short", "_short");
        reservedWords.put("static", "_static");
        reservedWords.put("strictfp", "_strictfp");
        reservedWords.put("super", "_super");
        reservedWords.put("switch", "_switch");
        reservedWords.put("synchronized", "_synchronized");
        reservedWords.put("this", "_this");
        reservedWords.put("throw", "_throw");
        reservedWords.put("throws", "_throws");
        reservedWords.put("transient", "_transient");
        reservedWords.put("true", "_true");
        reservedWords.put("try", "_try");
        reservedWords.put("void", "_void");
        reservedWords.put("volatile", "_volatile");
        reservedWords.put("while", "_while");

        holderClassNames = new HashMap();
        holderClassNames.put("int", "javax.xml.rpc.holders.IntHolder");
        holderClassNames.put("long", "javax.xml.rpc.holders.LongHolder");
        holderClassNames.put("short", "javax.xml.rpc.holders.ShortHolder");
        holderClassNames.put("float", "javax.xml.rpc.holders.FloatHolder");
        holderClassNames.put("double", "javax.xml.rpc.holders.DoubleHolder");
        holderClassNames.put("boolean", "javax.xml.rpc.holders.BooleanHolder");
        holderClassNames.put("byte", "javax.xml.rpc.holders.ByteHolder");
        holderClassNames.put(
            "java.lang.Integer",
            "javax.xml.rpc.holders.IntegerWrapperHolder");
        holderClassNames.put(
            "java.lang.Long",
            "javax.xml.rpc.holders.LongWrapperHolder");
        holderClassNames.put(
            "java.lang.Short",
            "javax.xml.rpc.holders.ShortWrapperHolder");
        holderClassNames.put(
            "java.lang.Float",
            "javax.xml.rpc.holders.FloatWrapperHolder");
        holderClassNames.put(
            "java.lang.Double",
            "javax.xml.rpc.holders.DoubleWrapperHolder");
        holderClassNames.put(
            "java.lang.Boolean",
            "javax.xml.rpc.holders.BooleanWrapperHolder");
        holderClassNames.put(
            "java.lang.Byte",
            "javax.xml.rpc.holders.ByteWrapperHolder");
        holderClassNames.put(
            "java.lang.String",
            "javax.xml.rpc.holders.StringHolder");
        holderClassNames.put(
            "java.math.BigDecimal",
            "javax.xml.rpc.holders.BigDecimalHolder");
        holderClassNames.put(
            "java.math.BigInteger",
            "javax.xml.rpc.holders.BigIntegerHolder");
        holderClassNames.put(
            "java.util.Calendar",
            "javax.xml.rpc.holders.CalendarHolder");
        holderClassNames.put(
            "javax.xml.namespace.QName",
            "javax.xml.rpc.holders.QNameHolder");
        holderClassNames.put(
            "java.lang.Object",
            "javax.xml.rpc.holders.ObjectHolder");
        holderClassNames.put("byte[]", "javax.xml.rpc.holders.ByteArrayHolder");
    }
}
