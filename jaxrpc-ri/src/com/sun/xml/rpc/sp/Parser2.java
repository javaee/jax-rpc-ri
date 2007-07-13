/*
 * $Id: Parser2.java,v 1.3 2007-07-13 23:36:27 ofung Exp $
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

package com.sun.xml.rpc.sp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

//
// NOTE:  when maintaining this code, take care to keep the message
// catalogue(s) up to date!!  It's important that the diagnostics
// be informative.
//

/**
 * This implements a fast non-validating top down parser.  This one always 
 * processes external parsed entities, strictly adheres to the XML 1.0
 * specification, and provides useful diagnostics.  It supports an optimization
 * allowing faster processing of valid standalone XML documents.  For
 * multi-language applications (such as web servers using XML processing
 * to create dynamic content), a method supports choosing a locale for
 * parser diagnostics which is both understood by the message recipient
 * and supported by the parser.
 *
 * @author David Brownell
 * @author Zhenghua Li
 * @author JAX-RPC RI Development Team
 */
public final class Parser2 {

    /* 
      This class was created by starting with Parser and making a few changes:
      a) adding methods to get access to the internal attribute list and
         namespace support objects
      b) eliminating the ATTR event
      c) renumbering the remaining events to match the new XMLReader interface
      d) setting the URI of xmlns attributes to "http://www.w3.org/2000/xmlns/",
         per the XML Information Set specification
     */

    // these are the name and value of the most
    // recently parsed item
    private String curName = null;
    private String curValue = null;
    // namespace support
    private String curURI = null;

    // stack of input entities being merged
    private InputEntity in;

    // temporaries reused during parsing
    private AttributesExImpl attTmp;
    private String[] parts = new String[3];
    private StringBuffer strTmp;
    private char nameTmp[];
    private NameCache nameCache;
    private char charTmp[] = new char[2];

    // namespace support
    private boolean namespace = false;
    private NamespaceSupport ns = null;

    // parsing modes
    private boolean isInAttribute = false;

    private boolean rejectDTDs = false;

    // temporary DTD parsing state
    private boolean inExternalPE;
    private boolean doLexicalPE;
    private boolean donePrologue;
    private boolean doneEpilogue;
    private boolean doneContent;

    private AttributesExImpl attr = null;
    private int attrIndex = 0;
    private boolean startEmptyStack = true;

    // info about the document
    private boolean isStandalone;
    private String rootElementName;

    // DTD state, used during parsing
    private boolean ignoreDeclarations;
    private SimpleHashtable elements = new SimpleHashtable(47);
    private SimpleHashtable params = new SimpleHashtable(7);

    // exposed to package-private subclass
    Map notations = new HashMap(7);
    SimpleHashtable entities = new SimpleHashtable(17);

    // string constants -- use these copies so "==" works
    // package private
    static final String strANY = "ANY";
    static final String strEMPTY = "EMPTY";

    private Locale locale;
    private EntityResolver resolver;
    Locator locator;
    private boolean fastStandalone = false;

    private static final String XMLNS_NAMESPACE_URI =
        "http://www.w3.org/2000/xmlns/";

    ////////////////////////////////////////////////////////////////
    //
    // PARSER methods
    //
    ////////////////////////////////////////////////////////////////

    /**
     * Used by applications to request locale for diagnostics.
     *
     * @param l The locale to use, or null to use system defaults
     *	(which may include only message IDs).
     * @throws ParseException If no diagnostic messages are available
     *	in that locale.
     */
    public void setLocale(Locale l) throws ParseException {
        if (l != null && !messages.isLocaleSupported(l.toString()))
            fatal(messages.getMessage(locale, "P-078", new Object[] { l }));
        locale = l;
    }

    /** Returns the diagnostic locale. */
    public Locale getLocale() {
        return locale;
    }

    public String getCurName() {
        return curName;
    }

    public String getCurURI() {
        return curURI;
    }

    public String getCurValue() {
        return curValue;
    }

    public NamespaceSupport getNamespaceSupport() {
        return ns;
    }

    public AttributesEx getAttributes() {
        return attr;
    }

    public int getLineNumber() {
        return locator.getLineNumber();
    }

    public int getColumnNumber() {
        return locator.getColumnNumber();
    }

    public String getPublicId() {
        return locator.getPublicId();
    }

    public String getSystemId() {
        return locator.getSystemId();
    }

    /**
     * Chooses a client locale to use for diagnostics, using the first
     * language specified in the list that is supported by this parser.
     * That locale is then set using <a href="#setLocale(java.util.Locale)">
     * setLocale()</a>.  Such a list could be provided by a variety of user
     * preference mechanisms, including the HTTP <em>Accept-Language</em>
     * header field.
     *
     * @see com.sun.xml.rpc.sp.MessageCatalog
     *
     * @param languages Array of language specifiers, ordered with the most
     *	preferable one at the front.  For example, "en-ca" then "fr-ca",
     *  followed by "zh_CN".  Both RFC 1766 and Java styles are supported.
     * @return The chosen locale, or null.
     */
    public Locale chooseLocale(String languages[]) throws ParseException {
        Locale l = messages.chooseLocale(languages);

        if (l != null)
            setLocale(l);
        return l;
    }

    /** Lets applications control entity resolution. */
    public void setEntityResolver(EntityResolver r) {
        resolver = r;
    }

    /** Returns the object used to resolve entities */
    public EntityResolver getEntityResolver() {
        return resolver;
    }

    /**
     * Setting this flag enables faster processing of valid standalone
     * documents: external DTD information is not processed, and no
     * attribute normalization or defaulting is done.  This optimization
     * is only permitted in non-validating parsers; for validating
     * parsers, this mode is silently disabled.
     *
     * <P> For documents which are declared as standalone, but which are
     * not valid, a fatal error may be reported for references to externally
     * defined entities.  That could happen in any nonvalidating parser which
     * did not read externally defined entities.  Also, if any attribute
     * values need normalization or defaulting, it will not be done.
     */
    public void setFastStandalone(boolean value) {
        fastStandalone = value;
    }

    /**
     * Returns true if standalone documents skip processing of
     * all external DTD information.
     */
    public boolean isFastStandalone() {
        return fastStandalone;
    }

    // makes sure the parser's reset to "before a document"
    private void init() {
        in = null;

        // alloc temporary data used in parsing
        attTmp = new AttributesExImpl();
        strTmp = new StringBuffer();
        nameTmp = new char[20];
        nameCache = new NameCache();

        if (namespace) {
            if (ns == null)
                ns = new NamespaceSupport();
            else
                ns.reset();
        }

        // reset doc info
        isStandalone = false;
        rootElementName = null;
        isInAttribute = false;

        inExternalPE = false;
        doLexicalPE = false;
        donePrologue = false;
        doneEpilogue = false;
        doneContent = false;

        attr = null;
        attrIndex = 0;
        startEmptyStack = true;

        entities.clear();
        notations.clear();
        params.clear();
        elements.clear();
        ignoreDeclarations = false;

        stack.clear();
        piQueue.clear();

        // initialize predefined references ... re-interpreted later
        builtin("amp", "&#38;");
        builtin("lt", "&#60;");
        builtin("gt", ">");
        builtin("quot", "\"");
        builtin("apos", "'");

        if (locale == null)
            locale = Locale.getDefault();
        if (resolver == null)
            resolver = new Resolver();

    }

    private void builtin(String entityName, String entityValue) {
        InternalEntity entity;
        entity = new InternalEntity(entityName, entityValue.toCharArray());
        entities.put(entityName, entity);
    }

    // package private -- for subclass 
    void afterRoot() throws ParseException {
    }

    // package private -- for subclass 
    void afterDocument() {
    }

    // role is for diagnostics
    private void whitespace(String roleId) throws IOException, ParseException
    // [3] S ::= (#x20 | #x9 | #xd | #xa)+
    {
        if (!maybeWhitespace())
            fatal("P-004", new Object[] { messages.getMessage(locale, roleId)});
    }

    // S?
    private boolean maybeWhitespace() throws IOException, ParseException {
        if (!(inExternalPE && doLexicalPE))
            return in.maybeWhitespace();

        // see getc() for the PE logic -- this lets us splice
        // expansions of PEs in "anywhere".  getc() has smarts,
        // so for external PEs we don't bypass it.

        // we can marginally speed PE handling, and certainly
        // be cleaner (hence potentially more correct), by using
        // the observations that expanded PEs only start and stop
        // where whitespace is allowed.  getc wouldn't need any
        // "lexical" PE expansion logic, and no other method needs
        // to handle termination of PEs.  (parsing of literals would
        // still need to pop entities, but not parsing of references
        // in content.)

        char c = getc();
        boolean saw = false;

        while (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
            saw = true;

            // this gracefully ends things when we stop playing
            // with internal parameters.  caller should have a
            // grammar rule allowing whitespace at end of entity.
            if (in.isEOF() && !in.isInternal())
                return saw;
            c = getc();
        }
        ungetc();
        return saw;
    }

    private String maybeGetName() throws IOException, ParseException {
        NameCacheEntry entry = maybeGetNameCacheEntry();
        return (entry == null) ? null : entry.name;
    }

    private NameCacheEntry maybeGetNameCacheEntry()
        throws IOException, ParseException {
        // [5] Name ::= (Letter|'_'|':') (Namechar)*
        char c = getc();

        if (!XmlChars.isLetter(c) && c != ':' && c != '_') {
            ungetc();
            return null;
        }
        return nameCharString(c);
    }

    // Used when parsing enumerations
    private String getNmtoken() throws ParseException, IOException {
        // [7] Nmtoken ::= (Namechar)+
        char c = getc();
        if (!XmlChars.isNameChar(c))
            fatal("P-006", new Object[] { new Character(c)});
        return nameCharString(c).name;
    }

    // n.b. this gets used when parsing attribute values (for
    // internal references) so we can't use strTmp; it's also
    // a hotspot for CPU and memory in the parser (called at least
    // once for each element) so this has been optimized a bit.

    private NameCacheEntry nameCharString(char c)
        throws IOException, ParseException {
        int i = 1;

        nameTmp[0] = c;
        for (;;) {
            if ((c = in.getNameChar()) == 0)
                break;
            if (i >= nameTmp.length) {
                char tmp[] = new char[nameTmp.length + 10];
                System.arraycopy(nameTmp, 0, tmp, 0, nameTmp.length);
                nameTmp = tmp;
            }
            nameTmp[i++] = c;
        }
        return nameCache.lookupEntry(nameTmp, i);
    }

    //
    // much similarity between parsing entity values in DTD
    // and attribute values (in DTD or content) ... both follow
    // literal parsing rules, newline canonicalization, etc
    //
    // leaves value in 'strTmp' ... either a "replacement text" (4.5),
    // or else partially normalized attribute value (the first bit
    // of 3.3.3's spec, without the "if not CDATA" bits).
    //
    private void parseLiteral(boolean isEntityValue)
        throws IOException, ParseException {
        // [9] EntityValue ::=
        //	'"' ([^"&%] | Reference | PEReference)* '"'
        //    |	"'" ([^'&%] | Reference | PEReference)* "'"
        // [10] AttValue ::=
        //	'"' ([^"&]  | Reference		     )* '"'
        //    |	"'" ([^'&]  | Reference		     )* "'"

        // Only expand PEs in getc() when processing entity value literals
        // and do not expand when processing AttValue.  Save state of
        // doLexicalPE and restore it before returning.
        boolean savedLexicalPE = doLexicalPE;
        doLexicalPE = isEntityValue;

        char quote = getc();
        char c;
        InputEntity source = in;

        if (quote != '\'' && quote != '"')
            fatal("P-007");

        // don't report entity expansions within attributes,
        // they're reported "fully expanded" via SAX
        isInAttribute = !isEntityValue;

        // get value into strTmp
        strTmp = new StringBuffer();

        // scan, allowing entity push/pop wherever ...
        // expanded entities can't terminate the literal!
        for (;;) {
            if (in != source && in.isEOF()) {
                // we don't report end of parsed entities
                // within attributes (no SAX hooks)
                in = in.pop();
                continue;
            }
            if ((c = getc()) == quote && in == source)
                break;

            //
            // Basically the "reference in attribute value"
            // row of the chart in section 4.4 of the spec
            //
            if (c == '&') {
                String entityName = maybeGetName();

                if (entityName != null) {
                    nextChar(';', "F-020", entityName);

                    // 4.4 says:  bypass these here ... we'll catch
                    // forbidden refs to unparsed entities on use
                    if (isEntityValue) {
                        strTmp.append('&');
                        strTmp.append(entityName);
                        strTmp.append(';');
                        continue;
                    }
                    expandEntityInLiteral(entityName, entities, isEntityValue);

                    // character references are always included immediately
                } else if ((c = getc()) == '#') {
                    int tmp = parseCharNumber();

                    if (tmp > 0xffff) {
                        tmp = surrogatesToCharTmp(tmp);
                        strTmp.append(charTmp[0]);
                        if (tmp == 2)
                            strTmp.append(charTmp[1]);
                    } else
                        strTmp.append((char) tmp);
                } else
                    fatal("P-009");
                continue;

            }

            // expand parameter entities only within entity value literals
            if (c == '%' && isEntityValue) {
                String entityName = maybeGetName();

                if (entityName != null) {
                    nextChar(';', "F-021", entityName);
                    if (inExternalPE)
                        expandEntityInLiteral(
                            entityName,
                            params,
                            isEntityValue);
                    else
                        fatal("P-010", new Object[] { entityName });
                    continue;
                } else
                    fatal("P-011");
            }

            // For attribute values ...
            if (!isEntityValue) {
                // 3.3.3 says whitespace normalizes to space...
                if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
                    strTmp.append(' ');
                    continue;
                }

                // "<" not legal in parsed literals ...
                if (c == '<')
                    fatal("P-012");
            }

            strTmp.append(c);
        }

        isInAttribute = false;
        doLexicalPE = savedLexicalPE;
    }

    // does a SINGLE expansion of the entity (often reparsed later)
    private void expandEntityInLiteral(
        String name,
        SimpleHashtable table,
        boolean isEntityValue)
        throws ParseException, IOException {
        Object entity = table.get(name);

        //
        // Note:  if entity is a PE (value.isPE) there is an XML
        // requirement that the content be "markkupdecl", but that error
        // is ignored here (as permitted by the XML spec).
        //
        if (entity instanceof InternalEntity) {
            InternalEntity value = (InternalEntity) entity;
            pushReader(value.buf, name, !value.isPE);

        } else if (entity instanceof ExternalEntity) {
            if (!isEntityValue) // must be a PE ...
                fatal("P-013", new Object[] { name });
            // if this returns false ...
            pushReader((ExternalEntity) entity);

        } else if (entity == null) {
            //
            // Note:  much confusion about whether spec requires such
            // errors to be fatal in many cases, but none about whether
            // it allows "normal" errors to be unrecoverable!
            //
            fatal((table == params) ? "V-022" : "P-014", new Object[] { name });
        }
    }

    // [11] SystemLiteral ::= ('"' [^"]* '"') | ("'" [^']* "'")
    // for PUBLIC and SYSTEM literals, also "<?xml ...type='literal'?>'

    // NOTE:  XML spec should explicitly say that PE ref syntax is
    // ignored in PIs, comments, SystemLiterals, and Pubid Literal
    // values ... can't process the XML spec's own DTD without doing
    // that for comments.

    private String getQuotedString(String type, String extra)
        throws IOException, ParseException {
        // use in.getc to bypass PE processing
        char quote = in.getc();

        if (quote != '\'' && quote != '"')
            fatal(
                "P-015",
                new Object[] {
                     messages.getMessage(
                        locale,
                        type,
                        new Object[] { extra })
            });

        char c;

        strTmp = new StringBuffer();
        while ((c = in.getc()) != quote)
            strTmp.append((char) c);
        return strTmp.toString();
    }

    private String parsePublicId() throws IOException, ParseException {
        // [12] PubidLiteral ::= ('"' PubidChar* '"') | ("'" PubidChar* "'")
        // [13] PubidChar ::= #x20|#xd|#xa|[a-zA-Z0-9]|[-'()+,./:=?;!*#@$_%]
        String retval = getQuotedString("F-033", null);
        for (int i = 0; i < retval.length(); i++) {
            char c = retval.charAt(i);
            if (" \r\n-'()+,./:=?;!*#@$_%0123456789".indexOf(c) == -1
                && !(c >= 'A' && c <= 'Z')
                && !(c >= 'a' && c <= 'z'))
                fatal("P-016", new Object[] { new Character(c)});
        }
        strTmp = new StringBuffer();
        strTmp.append(retval);
        return normalize(false);
    }

    // [14] CharData ::= [^<&]* - ([^<&]* ']]>' [^<&]*)
    // handled by:  InputEntity.parsedContent()

    private boolean maybeComment(boolean skipStart)
        throws IOException, ParseException {
        // [15] Comment ::= '<!--'
        //		( (Char - '-') | ('-' (Char - '-'))*
        //		'-->'
        if (!in.peek(skipStart ? "!--" : "<!--", null))
            return false;

        boolean savedLexicalPE = doLexicalPE;
        boolean saveCommentText;

        doLexicalPE = false;
        saveCommentText = false;
        if (saveCommentText)
            strTmp = new StringBuffer();

        oneComment : for (;;) {
            try {
                // bypass PE expansion, but permit PEs
                // to complete ... valid docs won't care.
                for (;;) {
                    int c = getc();
                    if (c == '-') {
                        c = getc();
                        if (c != '-') {
                            if (saveCommentText)
                                strTmp.append('-');
                            ungetc();
                            continue;
                        }
                        nextChar('>', "F-022", null);
                        break oneComment;
                    }
                    if (saveCommentText)
                        strTmp.append((char) c);
                }
            } catch (EndOfInputException e) {
                //
                // This is fatal EXCEPT when we're processing a PE...
                // in which case a validating processor reports an error.
                // External PEs are easy to detect; internal ones we
                // infer by being an internal entity outside an element.
                //
                if (inExternalPE || (!donePrologue && in.isInternal())) {
                    in = in.pop();
                    continue;
                }
                fatal("P-017");
            }
        }
        doLexicalPE = savedLexicalPE;
        return true;
    }

    // [18] CDSect ::= CDStart CData CDEnd
    // [19] CDStart ::= '<![CDATA['
    // [20] CData ::= (Char* - (Char* ']]>' Char*))
    // [21] CDEnd ::= ']]>'
    //
    //	... handled by InputEntity.unparsedContent()

    private void maybeXmlDecl() throws IOException, ParseException {
        // [23] XMLDecl ::= '<?xml' VersionInfo EncodingDecl?
        //			SDDecl? S? '>'
        if (!peek("<?xml"))
            return;

        readVersion(true, "1.0");
        readEncoding(false);
        readStandalone();
        maybeWhitespace();
        if (!peek("?>")) {
            char c = getc();
            fatal(
                "P-023",
                new Object[] { Integer.toHexString(c), new Character(c)});
        }
    }

    // collapsing several rules together ... 
    // simpler than attribute literals -- no reference parsing!
    private String maybeReadAttribute(String name, boolean must)
        throws IOException, ParseException {
        // [24] VersionInfo ::= S 'version' Eq \'|\" versionNum \'|\"
        // [80] EncodingDecl ::= S 'encoding' Eq \'|\" EncName \'|\"
        // [32] SDDecl ::=  S 'standalone' Eq \'|\" ... \'|\"
        if (!maybeWhitespace()) {
            if (!must)
                return null;
            fatal("P-024", new Object[] { name });
            // NOTREACHED
        }

        if (!peek(name))
            if (must)
                fatal("P-024", new Object[] { name });
            else {
                // To ensure that the whitespace is there so that when we
                // check for the next attribute we assure that the
                // whitespace still exists.
                ungetc();
                return null;
            }

        // [25] Eq ::= S? '=' S?
        maybeWhitespace();
        nextChar('=', "F-023", null);
        maybeWhitespace();

        return getQuotedString("F-035", name);
    }

    private void readVersion(boolean must, String versionNum)
        throws IOException, ParseException {
        String value = maybeReadAttribute("version", must);

        // [26] versionNum ::= ([a-zA-Z0-9_.:]| '-')+

        if (must && value == null)
            fatal("P-025", new Object[] { versionNum });
        if (value != null) {
            int length = value.length();
            for (int i = 0; i < length; i++) {
                char c = value.charAt(i);
                if (!((c >= '0' && c <= '9')
                    || c == '_'
                    || c == '.'
                    || (c >= 'a' && c <= 'z')
                    || (c >= 'A' && c <= 'Z')
                    || c == ':'
                    || c == '-'))
                    fatal("P-026", new Object[] { value });
            }
        }
        if (value != null && !value.equals(versionNum))
            error("P-027", new Object[] { versionNum, value });
    }

    private void maybeMisc(boolean eofOK) throws IOException, ParseException {
        // Misc*
        while (!eofOK || !in.isEOF()) {
            // [27] Misc ::= Comment | PI | S
            if (maybeComment(false) || maybePI(false) || maybeWhitespace())
                continue;
            else
                break;
        }
    }

    // common code used by most markup declarations
    // ... S (Q)Name ...
    private String getMarkupDeclname(String roleId, boolean qname)
        throws IOException, ParseException {
        String name;

        whitespace(roleId);
        name = maybeGetName();
        if (name == null)
            fatal("P-005", new Object[] { messages.getMessage(locale, roleId)});
        return name;
    }

    private boolean maybeDoctypeDecl() throws IOException, ParseException {
        // [28] doctypedecl ::= '<!DOCTYPE' S Name
        //	(S ExternalID)?
        //	S? ('[' (markupdecl|PEReference|S)* ']' S?)?
        //	'>'
        if (!peek("<!DOCTYPE"))
            return false;

        if (rejectDTDs) {
            fatal("P-085");
        }

        ExternalEntity externalSubset = null;

        rootElementName = getMarkupDeclname("F-014", true);
        if (maybeWhitespace()
            && (externalSubset = maybeExternalID()) != null) {
            maybeWhitespace();
        }
        if (in.peekc('[')) {
            in.startRemembering();
            for (;;) {
                //Pop PEs when they are done.
                if (in.isEOF() && !in.isDocument()) {
                    in = in.pop();
                    continue;
                }
                if (maybeMarkupDecl()
                    || maybePEReference()
                    || maybeWhitespace())
                    continue;
                else if (peek("<!["))
                    fatal("P-028");
                else
                    break;
            }
            nextChar(']', "F-024", null);
            maybeWhitespace();
        }

        nextChar('>', "F-025", null);

        // [30] extSubset ::= TextDecl? extSubsetDecl
        // [31] extSubsetDecl ::= ( markupdecl | conditionalSect
        //		| PEReference | S )*
        //	... same as [79] extPE, which is where the code is

        if (externalSubset != null && false) { // ## Ignore DOCTYPE
            externalSubset.name = "(DOCTYPE)";
            externalSubset.isPE = true;
            externalParameterEntity(externalSubset);
        }

        // params are no good to anyone starting now -- bye!
        params.clear();

        // make sure notations mentioned in attributes
        // and entities were declared ... those are validity
        // errors, but we must always clean up after them!
        List v = new ArrayList();

        for (Iterator e = notations.keySet().iterator(); e.hasNext();) {
            String name = (String) e.next();
            Object value = notations.get(name);

            if (value == Boolean.TRUE) {
                v.add(name);
            } else if (value instanceof String) {
                v.add(name);
            }
        }
        while (!v.isEmpty()) {
            Object name = v.get(0);
            v.remove(name);
            notations.remove(name);
        }

        return true;
    }

    private boolean maybeMarkupDecl() throws IOException, ParseException {
        // [29] markupdecl ::= elementdecl | Attlistdecl
        //	       | EntityDecl | NotationDecl | PI | Comment
        return maybeElementDecl()
            || maybeAttlistDecl()
            || maybeEntityDecl()
            || maybeNotationDecl()
            || maybePI(false)
            || maybeComment(false);
    }

    private void readStandalone() throws IOException, ParseException {
        String value = maybeReadAttribute("standalone", false);

        // [32] SDDecl ::= ... "yes" or "no"
        if (value == null || "no".equals(value))
            return;
        if ("yes".equals(value)) {
            isStandalone = true;
            return;
        }
        fatal("P-029", new Object[] { value });
    }

    private static final String XmlLang = "xml:lang";

    private boolean isXmlLang(String value) {
        // [33] LanguageId ::= Langcode ('-' Subcode)*
        // [34] Langcode ::= ISO639Code | IanaCode | UserCode
        // [35] ISO639Code ::= [a-zA-Z] [a-zA-Z]
        // [36] IanaCode ::= [iI] '-' SubCode
        // [37] UserCode ::= [xX] '-' SubCode
        // [38] SubCode ::= [a-zA-Z]+

        // the ISO and IANA codes (and subcodes) are registered,
        // but that's neither a WF nor a validity constraint.

        int nextSuffix;
        char c;

        if (value.length() < 2)
            return false;
        c = value.charAt(1);
        if (c == '-') { // IANA, or user, code
            c = value.charAt(0);
            if (!(c == 'i' || c == 'I' || c == 'x' || c == 'X'))
                return false;
            nextSuffix = 1;
        } else if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
            // 2 letter ISO code, or error
            c = value.charAt(0);
            if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')))
                return false;
            nextSuffix = 2;
        } else
            return false;

        // here "suffix" ::= '-' [a-zA-Z]+ suffix*
        while (nextSuffix < value.length()) {
            c = value.charAt(nextSuffix);
            if (c != '-')
                break;
            while (++nextSuffix < value.length()) {
                c = value.charAt(nextSuffix);
                if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')))
                    break;
            }
        }
        return value.length() == nextSuffix && c != '-';
    }

    private boolean defaultAttributes(
        AttributesExImpl attributes,
        ElementDecl element)
        throws ParseException {
        boolean didDefault = false;

        // default anything the document didn't provide.
        // check #REQUIRED values.
        for (Iterator e = element.attributes.keys(); e.hasNext();) {
            String key = (String) e.next();
            String value = attributes.getValue(key);
            AttributeDecl info;

            if (value != null)
                continue;

            info = (AttributeDecl) element.attributes.get(key);
            if (info.defaultValue != null) {
                attributes.addAttribute(
                    "",
                    key,
                    key,
                    info.type,
                    info.defaultValue,
                    info.defaultValue,
                    false);
                didDefault = true;
            }
        }
        return didDefault;
    }

    private boolean maybeElementDecl() throws IOException, ParseException {
        // [45] elementDecl ::= '<!ELEMENT' S Name S contentspec S? '>'
        // [46] contentspec ::= 'EMPTY' | 'ANY' | Mixed | children
        InputEntity start = peekDeclaration("!ELEMENT");

        if (start == null)
            return false;

        // n.b. for content models where inter-element whitespace is 
        // ignorable, we mark that fact here.
        String name = getMarkupDeclname("F-015", true);
        ElementDecl element = (ElementDecl) elements.get(name);
        boolean declEffective = false;

        if (element != null) {
            if (element.contentType != null) {
                // don't override previous declaration
                element = new ElementDecl(name);
            } // else <!ATTLIST name ...> came first
        } else {
            element = new ElementDecl(name);
            if (!ignoreDeclarations) {
                elements.put(element.name, element);
                declEffective = true;
            }
        }
        element.isFromInternalSubset = !inExternalPE;

        whitespace("F-000");
        if (peek(strEMPTY)) {
            element.contentType = strEMPTY;
            element.ignoreWhitespace = true;
        } else if (peek(strANY)) {
            element.contentType = strANY;
            element.ignoreWhitespace = false;
        } else
            element.contentType = getMixedOrChildren(element);

        maybeWhitespace();
        char c = getc();
        if (c != '>')
            fatal("P-036", new Object[] { name, new Character(c)});

        return true;
    }

    // We're leaving the content model as a regular expression;
    // it's an efficient natural way to express such things, and
    // libraries often interpret them.  No whitespace in the
    // model we store, though!

    private String getMixedOrChildren(ElementDecl element)
        throws IOException, ParseException {
        InputEntity start;

        // [47] children ::= (choice|seq) ('?'|'*'|'+')?
        strTmp = new StringBuffer();

        nextChar('(', "F-028", element.name);
        start = in;
        maybeWhitespace();
        strTmp.append('(');

        if (peek("#PCDATA")) {
            strTmp.append("#PCDATA");
            getMixed(element.name, start);
            element.ignoreWhitespace = false;
        } else {
            element.model = getcps(element.name, start);
            element.ignoreWhitespace = true;
        }
        return strTmp.toString();
    }

    // '(' S? already consumed
    // matching ')' must be in "start" entity if validating
    private ContentModel getcps(String element, InputEntity start)
        throws IOException, ParseException {
        // [48] cp ::= (Name|choice|seq) ('?'|'*'|'+')?
        // [49] choice ::= '(' S? cp (S? '|' S? cp)* S? ')'
        // [50] seq    ::= '(' S? cp (S? ',' S? cp)* S? ')'
        boolean decided = false;
        char type = 0;
        ContentModel retval, current, temp;

        retval = current = temp = null;

        do {
            String tag;

            tag = maybeGetName();
            if (tag != null) {
                strTmp.append(tag);
                temp = getFrequency(null);
            } else if (peek("(")) {
                InputEntity next = in;
                strTmp.append('(');
                maybeWhitespace();
                temp = getFrequency(getcps(element, next));
            } else
                fatal(
                    (type == 0) ? "P-039" : ((type == ',') ? "P-037" : "P-038"),
                    new Object[] { new Character(getc())});

            maybeWhitespace();
            if (decided) {
                char c = getc();

                if (current != null) {
                    current.next = null;
                    current = current.next;
                }
                if (c == type) {
                    strTmp.append(type);
                    maybeWhitespace();
                    continue;
                } else if (c == '\u0029') { // rparen
                    ungetc();
                    continue;
                } else {
                    fatal(
                        (type == 0) ? "P-041" : "P-040",
                        new Object[] { new Character(c), new Character(type)});
                }
            } else {
                type = getc();
                if (type == '|' || type == ',') {
                    decided = true;
                    retval = current = null;
                } else {
                    retval = current = temp;
                    ungetc();
                    continue;
                }
                strTmp.append(type);
            }
            maybeWhitespace();
        } while (!peek(")"));
        strTmp.append(')');
        return getFrequency(retval);
    }

    private ContentModel getFrequency(ContentModel original)
        throws IOException, ParseException {
        char c = getc();

        if (c == '?' || c == '+' || c == '*') {
            strTmp.append(c);
            if (original == null)
                return null;
            if (original.type == 0) { // foo* etc
                original.type = c;
                return original;
            }
            return null;
        } else {
            ungetc();
            return original;
        }
    }

    // '(' S? '#PCDATA' already consumed 
    // matching ')' must be in "start" entity if validating
    private void getMixed(String element, InputEntity start)
        throws IOException, ParseException {
        // [51] Mixed ::= '(' S? '#PCDATA' (S? '|' S? Name)* S? ')*'
        //		| '(' S? '#PCDATA'                   S? ')'
        maybeWhitespace();
        if (peek("\u0029*") || peek("\u0029")) {
            strTmp.append(')');
            return;
        }

        while (peek("|")) {
            String name;

            strTmp.append('|');
            maybeWhitespace();

            name = maybeGetName();
            if (name == null)
                fatal(
                    "P-042",
                    new Object[] { element, Integer.toHexString(getc())});
            strTmp.append(name);
            maybeWhitespace();
        }

        if (!peek("\u0029*")) // right paren
            fatal("P-043", new Object[] { element, new Character(getc())});
        strTmp.append(')');
    }

    private boolean maybeAttlistDecl() throws IOException, ParseException {
        // [52] AttlistDecl ::= '<!ATTLIST' S Name AttDef* S? '>'
        InputEntity start = peekDeclaration("!ATTLIST");

        if (start == null)
            return false;

        String name = getMarkupDeclname("F-016", true);
        ElementDecl element = (ElementDecl) elements.get(name);

        if (element == null) {
            // not yet declared -- no problem.
            element = new ElementDecl(name);
            if (!ignoreDeclarations)
                elements.put(name, element);
        }

        maybeWhitespace();
        while (!peek(">")) {

            // [53] AttDef ::= S Name S AttType S DefaultDecl
            // [54] AttType ::= StringType | TokenizedType | EnumeratedType
            name = maybeGetName();
            if (name == null)
                fatal("P-044", new Object[] { new Character(getc())});
            whitespace("F-001");

            AttributeDecl a = new AttributeDecl(name);
            a.isFromInternalSubset = !inExternalPE;

            // Note:  use the type constants from AttributeDecl
            // so that "==" may be used (faster)

            // [55] StringType ::= 'CDATA'
            if (peek(AttributeDecl.CDATA))
                a.type = AttributeDecl.CDATA;

            // [56] TokenizedType ::= 'ID' | 'IDREF' | 'IDREFS'
            //		| 'ENTITY' | 'ENTITIES'
            //		| 'NMTOKEN' | 'NMTOKENS'
            // n.b. if "IDREFS" is there, both "ID" and "IDREF"
            // match peekahead ... so this order matters!
            else if (peek(AttributeDecl.IDREFS))
                a.type = AttributeDecl.IDREFS;
            else if (peek(AttributeDecl.IDREF))
                a.type = AttributeDecl.IDREF;
            else if (peek(AttributeDecl.ID)) {
                a.type = AttributeDecl.ID;
                if (element.id == null)
                    element.id = name;
            } else if (peek(AttributeDecl.ENTITY))
                a.type = AttributeDecl.ENTITY;
            else if (peek(AttributeDecl.ENTITIES))
                a.type = AttributeDecl.ENTITIES;
            else if (peek(AttributeDecl.NMTOKENS))
                a.type = AttributeDecl.NMTOKENS;
            else if (peek(AttributeDecl.NMTOKEN))
                a.type = AttributeDecl.NMTOKEN;

            // [57] EnumeratedType ::= NotationType | Enumeration
            // [58] NotationType ::= 'NOTATION' S '(' S? Name
            //		(S? '|' S? Name)* S? ')'
            else if (peek(AttributeDecl.NOTATION)) {
                a.type = AttributeDecl.NOTATION;
                whitespace("F-002");
                nextChar('(', "F-029", null);
                maybeWhitespace();

                List v = new ArrayList();
                do {
                    if ((name = maybeGetName()) == null)
                        fatal("P-068");
                    // permit deferred declarations
                    v.add(name);
                    maybeWhitespace();
                    if (peek("|"))
                        maybeWhitespace();
                } while (!peek(")"));
                a.values = new String[v.size()];
                for (int i = 0; i < v.size(); i++)
                    a.values[i] = (String) v.get(i);

                // [59] Enumeration ::= '(' S? Nmtoken (S? '|' Nmtoken)* S? ')'
            } else if (peek("(")) {
                a.type = AttributeDecl.ENUMERATION;
                maybeWhitespace();

                List v = new ArrayList();
                do {
                    name = getNmtoken();
                    v.add(name);
                    maybeWhitespace();
                    if (peek("|"))
                        maybeWhitespace();
                } while (!peek(")"));
                a.values = new String[v.size()];
                for (int i = 0; i < v.size(); i++)
                    a.values[i] = (String) v.get(i);
            } else
                fatal("P-045", new Object[] { name, new Character(getc())});

            // [60] DefaultDecl ::= '#REQUIRED' | '#IMPLIED'
            //		| (('#FIXED' S)? AttValue)
            whitespace("F-003");
            if (peek("#REQUIRED"))
                a.isRequired = true;
            else if (peek("#FIXED")) {
                a.isFixed = true;
                whitespace("F-004");
                parseLiteral(false);
                if (a.type != AttributeDecl.CDATA)
                    a.defaultValue = normalize(false);
                else
                    a.defaultValue = strTmp.toString();
            } else if (!peek("#IMPLIED")) {
                parseLiteral(false);
                if (a.type != AttributeDecl.CDATA)
                    a.defaultValue = normalize(false);
                else
                    a.defaultValue = strTmp.toString();
            }

            if (XmlLang.equals(a.name)
                && a.defaultValue != null
                && !isXmlLang(a.defaultValue))
                error("P-033", new Object[] { a.defaultValue });

            if (!ignoreDeclarations
                && element.attributes.get(a.name) == null) {
                element.attributes.put(a.name, a);
            }
            maybeWhitespace();
        }
        return true;
    }

    // used when parsing literal attribute values,
    // or public identifiers.
    //
    // input in strTmp
    private String normalize(boolean invalidIfNeeded) throws ParseException {
        // this can allocate an extra string...

        String s = strTmp.toString();
        String s2 = s.trim();
        boolean didStrip = false;

        if (s != s2) {
            s = s2;
            s2 = null;
            didStrip = true;
        }
        strTmp = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!XmlChars.isSpace(c)) {
                strTmp.append(c);
                continue;
            }
            strTmp.append(' ');
            while (++i < s.length() && XmlChars.isSpace(s.charAt(i)))
                didStrip = true;
            i--;
        }
        if (didStrip)
            return strTmp.toString();
        else
            return s;
    }

    private boolean maybeConditionalSect() throws IOException, ParseException {
        // [61] conditionalSect ::= includeSect | ignoreSect

        if (!peek("<!["))
            return false;

        String keyword;
        InputEntity start = in;

        maybeWhitespace();

        if ((keyword = maybeGetName()) == null)
            fatal("P-046");
        maybeWhitespace();
        nextChar('[', "F-030", null);

        // [62] includeSect ::= '<![' S? 'INCLUDE' S? '['
        //				extSubsetDecl ']]>'
        if ("INCLUDE".equals(keyword)) {
            for (;;) {
                while (in.isEOF() && in != start)
                    in = in.pop();
                if (in.isEOF()) {
                    in = in.pop();
                }
                if (peek("]]>"))
                    break;

                doLexicalPE = false;
                if (maybeWhitespace())
                    continue;
                if (maybePEReference())
                    continue;
                doLexicalPE = true;
                if (maybeMarkupDecl() || maybeConditionalSect())
                    continue;

                fatal("P-047");
            }

            // [63] ignoreSect ::= '<![' S? 'IGNORE' S? '['
            //			ignoreSectcontents ']]>'
            // [64] ignoreSectcontents ::= Ignore ('<!['
            //			ignoreSectcontents ']]>' Ignore)*
            // [65] Ignore ::= Char* - (Char* ('<![' | ']]>') Char*)
        } else if ("IGNORE".equals(keyword)) {
            int nestlevel = 1;
            // ignoreSectcontents
            doLexicalPE = false;
            while (nestlevel > 0) {
                char c = getc(); // will pop input entities
                if (c == '<') {
                    if (peek("!["))
                        nestlevel++;
                } else if (c == ']') {
                    if (peek("]>"))
                        nestlevel--;
                } else
                    continue;
            }
        } else
            fatal("P-048", new Object[] { keyword });
        return true;
    }

    // parse decimal or hex numeric character reference
    private int parseCharNumber() throws ParseException, IOException {
        char c;
        int retval = 0;

        // n.b. we ignore overflow ...
        if (getc() != 'x') {
            ungetc();
            for (;;) {
                c = getc();
                if (c >= '0' && c <= '9') {
                    retval *= 10;
                    retval += (c - '0');
                    continue;
                }
                if (c == ';')
                    return retval;
                fatal("P-049");
            }
        } else
            for (;;) {
                c = getc();
                if (c >= '0' && c <= '9') {
                    retval <<= 4;
                    retval += (c - '0');
                    continue;
                }
                if (c >= 'a' && c <= 'f') {
                    retval <<= 4;
                    retval += 10 + (c - 'a');
                    continue;
                }
                if (c >= 'A' && c <= 'F') {
                    retval <<= 4;
                    retval += 10 + (c - 'A');
                    continue;
                }
                if (c == ';')
                    return retval;
                fatal("P-050");
            }
    }

    // parameter is a UCS-4 character ... i.e. not just 16 bit UNICODE,
    // though still subject to the 'Char' construct in XML
    private int surrogatesToCharTmp(int ucs4) throws ParseException {
        if (ucs4 <= 0xffff) {
            if (XmlChars.isChar(ucs4)) {
                charTmp[0] = (char) ucs4;
                return 1;
            }
        } else if (ucs4 <= 0x0010ffff) {
            // we represent these as UNICODE surrogate pairs
            ucs4 -= 0x10000;
            charTmp[0] = (char) (0xd800 | ((ucs4 >> 10) & 0x03ff));
            charTmp[1] = (char) (0xdc00 | (ucs4 & 0x03ff));
            return 2;
        }
        fatal("P-051", new Object[] { Integer.toHexString(ucs4)});
        // NOTREACHED
        return -1;
    }

    private boolean maybePEReference() throws IOException, ParseException {
        // This is the SYNTACTIC version of this construct.
        // When processing external entities, there is also
        // a LEXICAL version; see getc() and doLexicalPE.

        // [69] PEReference ::= '%' Name ';'
        if (!in.peekc('%'))
            return false;

        String name = maybeGetName();
        Object entity;

        if (name == null)
            fatal("P-011");
        nextChar(';', "F-021", name);
        entity = params.get(name);

        if (entity instanceof InternalEntity) {
            InternalEntity value = (InternalEntity) entity;
            pushReader(value.buf, name, false);

        } else if (entity instanceof ExternalEntity) {
            externalParameterEntity((ExternalEntity) entity);

        } else if (entity == null) {
            //
            // NOTE:  by treating undefined parameter entities as 
            // nonfatal, we are assuming that the contradiction
            // between them being a WFC versus a VC is resolved in
            // favor of the latter.  Further, we are assuming that
            // validating parsers should behave like nonvalidating
            // ones in such a case:  ignoring further declarations.
            //
            ignoreDeclarations = true;
            warning("V-022", new Object[] { name });
        }
        return true;
    }

    private boolean maybeEntityDecl() throws IOException, ParseException {
        // [70] EntityDecl ::= GEDecl | PEDecl
        // [71] GEDecl ::= '<!ENTITY' S       Name S EntityDef S? '>'
        // [72] PEDecl ::= '<!ENTITY' S '%' S Name S PEDEF     S? '>'
        // [73] EntityDef ::= EntityValue | (ExternalID NDataDecl?)
        // [74] PEDef     ::= EntityValue |  ExternalID
        //
        InputEntity start = peekDeclaration("!ENTITY");

        if (start == null)
            return false;

        String entityName;
        SimpleHashtable defns;
        ExternalEntity externalId;
        boolean doStore;

        // PE expansion gets selectively turned off several places:
        // in ENTITY declarations (here), in comments, in PIs.

        // Here, we allow PE entities to be declared, and allows
        // literals to include PE refs without the added spaces
        // required with their expansion in markup decls.

        doLexicalPE = false;
        whitespace("F-005");
        if (in.peekc('%')) {
            whitespace("F-006");
            defns = params;
        } else
            defns = entities;

        ungetc(); // leave some whitespace
        doLexicalPE = true;
        entityName = getMarkupDeclname("F-017", false);
        whitespace("F-007");
        externalId = maybeExternalID();

        //
        // first definition sticks ... e.g. internal subset PEs are used
        // to override DTD defaults.  It's also an "error" to incorrectly
        // redefine builtin internal entities, but since reporting such
        // errors is optional we only give warnings ("just in case") for
        // non-parameter entities.
        //
        doStore = (defns.get(entityName) == null);
        if (!doStore && defns == entities)
            warning("P-054", new Object[] { entityName });

        // if we skipped a PE, ignore declarations since the
        // PE might have included an ovrriding declaration
        doStore &= !ignoreDeclarations;

        // internal entities
        if (externalId == null) {
            char value[];
            InternalEntity entity;

            doLexicalPE = false; // "ab%bar;cd" -maybe-> "abcd"
            parseLiteral(true);
            doLexicalPE = true;
            if (doStore) {
                value = new char[strTmp.length()];
                if (value.length != 0)
                    strTmp.getChars(0, value.length, value, 0);
                entity = new InternalEntity(entityName, value);
                entity.isPE = (defns == params);
                entity.isFromInternalSubset = !inExternalPE;
                defns.put(entityName, entity);
            }

            // external entities (including unparsed)
        } else {
            // [76] NDataDecl ::= S 'NDATA' S Name
            if (defns == entities && maybeWhitespace() && peek("NDATA")) {
                externalId.notation = getMarkupDeclname("F-018", false);

                // flag undeclared notation for checking after
                // the DTD is fully processed
            }
            externalId.name = entityName;
            externalId.isPE = (defns == params);
            externalId.isFromInternalSubset = !inExternalPE;
            if (doStore) {
                defns.put(entityName, externalId);
            }
        }
        maybeWhitespace();
        nextChar('>', "F-031", entityName);
        return true;
    }

    private ExternalEntity maybeExternalID()
        throws IOException, ParseException {
        // [75] ExternalID ::= 'SYSTEM' S SystemLiteral
        //		| 'PUBLIC' S' PubidLiteral S Systemliteral
        String temp = null;
        ExternalEntity retval;

        if (peek("PUBLIC")) {
            whitespace("F-009");
            temp = parsePublicId();
        } else if (!peek("SYSTEM"))
            return null;

        retval = new ExternalEntity(in);
        retval.publicId = temp;
        whitespace("F-008");
        retval.systemId = parseSystemId();
        return retval;
    }

    private String parseSystemId() throws IOException, ParseException {
        String uri = getQuotedString("F-034", null);
        int temp = uri.indexOf(':');

        // resolve relative URIs ... must do it here since
        // it's relative to the source file holding the URI!

        // "new java.net.URL (URL, string)" conforms to RFC 1630,
        // but we can't use that except when the URI is a URL.
        // The entity resolver is allowed to handle URIs that are
        // not URLs, so we pass URIs through with scheme intact
        if (temp == -1 || uri.indexOf('/') < temp) {
            String baseURI;

            baseURI = in.getSystemId();
            if (baseURI == null)
                baseURI = "NODOCTYPE:///tmp/"; // ## Ignore DOCTYPE
            // fatal ("P-055", new Object [] { uri });
            if (uri.length() == 0)
                uri = ".";
            baseURI = baseURI.substring(0, baseURI.lastIndexOf('/') + 1);
            if (uri.charAt(0) != '/')
                uri = baseURI + uri;
            else {
                // slashes at the beginning of a relative URI are
                // a special case we don't handle.
                throw new InternalError();
            }

            // letting other code map any "/xxx/../" or "/./" to "/",
            // since all URIs must handle it the same.
        }
        // check for fragment ID in URI
        if (uri.indexOf('#') != -1)
            error("P-056", new Object[] { uri });
        return uri;
    }

    private void maybeTextDecl() throws IOException, ParseException {
        // [77] TextDecl ::= '<?xml' VersionInfo? EncodingDecl S? '?>'
        if (peek("<?xml")) {
            readVersion(false, "1.0");
            readEncoding(true);
            maybeWhitespace();
            if (!peek("?>"))
                fatal("P-057");
        }
    }

    private void externalParameterEntity(ExternalEntity next)
        throws IOException, ParseException {
        //
        // Reap the intended benefits of standalone declarations:
        // don't deal with external parameter entities, except to
        // validate the standalone declaration.
        //
        // perhaps:  also add an option to skip reading external
        // PEs when not validating, so this behaves like the parsers
        // in Gecko and IE5.  Means setting ignoreDeclarations ...
        //
        if (isStandalone && fastStandalone)
            return;

        // n.b. "in external parameter entities" (and external
        // DTD subset, same grammar) parameter references can
        // occur "within" markup declarations ... expansions can
        // cross syntax rules.  Flagged here; affects getc().

        // [79] ExtPE ::= TextDecl? extSubsetDecl
        // [31] extSubsetDecl ::= ( markupdecl | conditionalSect
        //		| PEReference | S )*
        InputEntity pe;

        inExternalPE = true;

        // if this returns false ...
        pushReader(next);

        pe = in;
        maybeTextDecl();
        while (!pe.isEOF()) {
            // pop internal PEs (and whitespace before/after)
            if (in.isEOF()) {
                in = in.pop();
                continue;
            }
            doLexicalPE = false;
            if (maybeWhitespace())
                continue;
            if (maybePEReference())
                continue;
            doLexicalPE = true;
            if (maybeMarkupDecl() || maybeConditionalSect())
                continue;
            break;
        }
        // if (in != pe) throw new InternalError ("who popped my PE?");
        if (!pe.isEOF())
            fatal("P-059", new Object[] { in.getName()});
        in = in.pop();
        inExternalPE = !in.isDocument();
        doLexicalPE = false;
    }

    private void readEncoding(boolean must)
        throws IOException, ParseException {
        // [81] EncName ::= [A-Za-z] ([A-Za-z0-9._] | '-')*
        String name = maybeReadAttribute("encoding", must);

        if (name == null)
            return;
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z'))
                continue;
            if (i != 0
                && ((c >= '0' && c <= '9') || c == '-' || c == '_' || c == '.'))
                continue;
            fatal("P-060", new Object[] { new Character(c)});
        }

        //
        // This should be the encoding in use, and it's even an error for
        // it to be anything else (in certain cases that are impractical to
        // to test, and may even be insufficient).  So, we do the best we
        // can, and warn if things look suspicious.  Note that Java doesn't
        // uniformly expose the encodings, and that the names it uses
        // internally are nonstandard.  Also, that the XML spec allows
        // such "errors" not to be reported at all.
        //
        String currentEncoding = in.getEncoding();

        if (currentEncoding != null && !name.equalsIgnoreCase(currentEncoding))
            warning("P-061", new Object[] { name, currentEncoding });
    }

    private boolean maybeNotationDecl() throws IOException, ParseException {
        // [82] NotationDecl ::= '<!NOTATION' S Name S
        //		(ExternalID | PublicID) S? '>'
        // [83] PublicID ::= 'PUBLIC' S PubidLiteral
        InputEntity start = peekDeclaration("!NOTATION");

        if (start == null)
            return false;

        String name = getMarkupDeclname("F-019", false);
        ExternalEntity entity = new ExternalEntity(in);

        whitespace("F-011");
        if (peek("PUBLIC")) {
            whitespace("F-009");
            entity.publicId = parsePublicId();
            if (maybeWhitespace()) {
                if (!peek(">"))
                    entity.systemId = parseSystemId();
            }
        } else if (peek("SYSTEM")) {
            whitespace("F-008");
            entity.systemId = parseSystemId();
        } else
            fatal("P-062");
        maybeWhitespace();
        nextChar('>', "F-032", name);
        if (entity.systemId != null && entity.systemId.indexOf('#') != -1)
            error("P-056", new Object[] { entity.systemId });

        Object value = notations.get(name);
        if (value != null && value instanceof ExternalEntity)
            warning("P-063", new Object[] { name });

        // if we skipped a PE, ignore declarations since the
        // PE might have included an ovrriding declaration
        else if (!ignoreDeclarations) {
            notations.put(name, entity);
        }
        return true;
    }

    ////////////////////////////////////////////////////////////////
    //
    //	UTILITIES
    //
    ////////////////////////////////////////////////////////////////

    private char getc() throws IOException, ParseException {
        if (!(inExternalPE && doLexicalPE)) {
            char c = in.getc();
            if (c == '%' && doLexicalPE)
                fatal("P-080");
            return c;
        }

        //
        // External parameter entities get funky processing of '%param;'
        // references.  It's not clearly defined in the XML spec; but it
        // boils down to having those refs be _lexical_ in most cases to
        // include partial syntax productions.  It also needs selective
        // enabling; "<!ENTITY % foo ...>" must work, for example, and
        // if "bar" is an empty string PE, "ab%bar;cd" becomes "abcd"
        // if it's expanded in a literal, else "ab  cd".  PEs also do
        // not expand within comments or PIs, and external PEs are only
        // allowed to have markup decls (and so aren't handled lexically).
        //
        // This PE handling should be merged into maybeWhitespace, where
        // it can be dealt with more consistently.
        //
        // Also, there are some validity constraints in this area.
        //
        char c;

        while (in.isEOF()) {
            if (in.isInternal() || (doLexicalPE && !in.isDocument()))
                in = in.pop();
            else {
                fatal("P-064", new Object[] { in.getName()});
            }
        }
        if ((c = in.getc()) == '%' && doLexicalPE) {
            // PE ref ::= '%' name ';'
            String name = maybeGetName();
            Object entity;

            if (name == null)
                fatal("P-011");
            nextChar(';', "F-021", name);
            entity = params.get(name);

            // push a magic "entity" before and after the
            // real one, so ungetc() behaves uniformly
            pushReader(" ".toCharArray(), null, false);
            if (entity instanceof InternalEntity)
                pushReader(((InternalEntity) entity).buf, name, false);
            else if (entity instanceof ExternalEntity)
                // PEs can't be unparsed!
                // if this returns false ...
                pushReader((ExternalEntity) entity);
            else if (entity == null)
                // see note in maybePEReference re making this be nonfatal.
                fatal("V-022");
            else
                throw new InternalError();
            pushReader(" ".toCharArray(), null, false);
            return in.getc();
        }
        return c;
    }

    private void ungetc() // throws IOException, ParseException
    {
        in.ungetc();
    }

    //private boolean peek (String s) throws IOException, ParseException
    //{ return in.peek (s, null); }
    private boolean peek(String s) throws IOException, ParseException {
        return in.peek(s, null);
    }
    // Return the entity starting the specified declaration
    // (for validating declaration nesting) else null.

    private InputEntity peekDeclaration(String s)
        throws IOException, ParseException {
        InputEntity start;

        if (!in.peekc('<'))
            return null;
        start = in;
        if (in.peek(s, null))
            return start;
        in.ungetc();
        return null;
    }

    private void nextChar(char c, String location, String near)
        throws IOException, ParseException {
        while (in.isEOF() && !in.isDocument())
            in = in.pop();
        if (!in.peekc(c))
            fatal(
                "P-008",
                new Object[] {
                    new Character(c),
                    messages.getMessage(locale, location),
                    (near == null ? "" : ('"' + near + '"'))});
    }

    private void pushReader(char buf[], String name, boolean isGeneral)
        throws ParseException {
        InputEntity r = InputEntity.getInputEntity(locale);
        r.init(buf, name, in, !isGeneral);
        in = r;
    }

    // returns false if the external entity is being ignored ...
    // potentially possible in nonvalidating parsers, but not
    // currently supported.  (See notes everywhere this is called;
    // both error handling, and reporting start/stop of entity
    // expansion, are issues!  Also, SAX has no way to say "don't
    // read this entity".)

    private boolean pushReader(ExternalEntity next)
        throws ParseException, IOException {
        try {
            InputEntity r = InputEntity.getInputEntity(locale);
            InputSource s = next.getInputSource(resolver);

            r.init(s, next.name, in, next.isPE);
            in = r;
        } catch (SAXException e) {
            throw translate(e);
        }
        return true;
    }

    // error handling convenience routines
    // we now treat every error as non-recoverable fatal errors.
    private void warning(String messageId, Object parameters[])
        throws ParseException {
        fatal(messages.getMessage(locale, messageId, parameters));
    }

    // package private ... normally returns.
    void error(String messageId, Object parameters[]) throws ParseException {
        fatal(messages.getMessage(locale, messageId, parameters));
    }

    private void fatal(String message) throws ParseException {
        fatal(message, null, null);
    }

    private void fatal(String message, Object parameters[])
        throws ParseException {
        fatal(message, parameters, null);
    }

    private void fatal(String messageId, Object parameters[], Exception e)
        throws ParseException {
        String m = messages.getMessage(locale, messageId, parameters);
        String m2 = ((e == null) ? null : e.toString());
        if (m2 != null) {
            m = m + ": " + m2;
        }
        ParseException x =
            new ParseException(
                m,
                getPublicId(),
                getSystemId(),
                getLineNumber(),
                getColumnNumber());
        // not continuable ... e.g. basic well-formedness errors
        throw x;
    }

    private ParseException translate(SAXException x) {
        String m = x.getMessage();
        if (x.getException() != null) {
            String n = x.getException().toString();
            if (m != null)
                m = m + ": " + n;
            else
                m = n;
        }
        return new ParseException(m);
    }

    //
    // LOCATOR -- used for err reporting. the app calls us,
    // we tell where the parsing current event happened.
    //
    class DocLocator implements Locator {

        public String getPublicId() {
            return (in == null) ? null : in.getPublicId();
        }

        public String getSystemId() {
            return (in == null) ? null : in.getSystemId();
        }

        public int getLineNumber() {
            return (in == null) ? -1 : in.getLineNumber();
        }

        public int getColumnNumber() {
            return (in == null) ? -1 : in.getColumnNumber();
        }
    }

    //
    // Map char arrays to strings ... cuts down both on memory and
    // CPU usage for element/attribute/other names that are reused.
    //
    // Documents typically repeat names a lot, so we more or less
    // intern all the strings within the document; since some strings
    // are repeated in multiple documents (e.g. stylesheets) we go
    // a bit further, and intern globally.
    //
    static final class NameCache {
        //
        // Unless we auto-grow this, the default size should be a
        // reasonable bit larger than needed for most XML files
        // we've yet seen (and be prime).  If it's too small, the
        // penalty is just excess cache collisions.
        //
        NameCacheEntry hashtable[] = new NameCacheEntry[541];

        //
        // Usually we just want to get the 'symbol' for these chars
        //
        String lookup(char value[], int len) {
            return lookupEntry(value, len).name;
        }

        //
        // Sometimes we need to scan the chars in the resulting
        // string, so there's an accessor which exposes them.
        // (Mostly for element end tags.)
        //
        NameCacheEntry lookupEntry(char value[], int len) {
            int index = 0;
            NameCacheEntry entry;

            // hashing to get index
            for (int i = 0; i < len; i++)
                index = index * 31 + value[i];
            index &= 0x7fffffff;
            index %= hashtable.length;

            // return entry if one's there ...
            for (entry = hashtable[index]; entry != null; entry = entry.next) {
                if (entry.matches(value, len))
                    return entry;
            }

            // else create new one
            entry = new NameCacheEntry();
            entry.chars = new char[len];
            System.arraycopy(value, 0, entry.chars, 0, len);
            entry.name = new String(entry.chars);
            entry.name = entry.name.intern(); // "global" intern
            entry.next = hashtable[index];
            hashtable[index] = entry;
            return entry;
        }
    }

    static final class NameCacheEntry {
        String name;
        char chars[];
        NameCacheEntry next;

        boolean matches(char value[], int len) {
            if (chars.length != len)
                return false;
            for (int i = 0; i < len; i++)
                if (value[i] != chars[i])
                    return false;
            return true;
        }
    }

    //
    // Message catalog for diagnostics.
    //
    static final Catalog messages = new Catalog();

    static final class Catalog extends MessageCatalog {
        Catalog() {
            super(Parser.class);
        }
    }

    ////////////////////////////////////////////////////////////////////////
    //
    // this is the start of the implementation of the non-recursive
    // top down parser. An explicit stack is used to record the current
    // state of the parser.
    //
    ////////////////////////////////////////////////////////////////////////

    // this is necessary for lazy parser initialization
    private InputSource input = null;

    // coalescing support
    private boolean coalescing = false;
    private StringBuffer charsBuffer = null;
    private int cacheRet = -1;
    private String cacheName = null;
    private String cacheValue = null;

    /* added this new buffer to avoid create lots of StringBuffers in
       coalescing mode */
    private String simpleCharsBuffer = null;

    /* added this so that the namespace declarations made by an element are
       still visible when we are positioned on its end tag */
    private boolean lastRetWasEnd = false;

    /**
     * Create a new parser with the specified input stream
     * and the coalescing property.
     *
     * @param in the input stream.
     * @param coalescing the parser will coalesce character data 
     *                   if, and only if, this parameter is <tt>true</tt> 
     * @param namespaceAware
     *          the parser will support namespaces if, and only if, 
     *          this parameter is <tt>true</tt> 
     *
     */
    public Parser2(
        InputStream in,
        boolean coalescing,
        boolean namespaceAware) {
        this(new InputSource(in), coalescing, namespaceAware, false);
    }

    /**
     * Create a new parser with the specified input stream
     * and the coalescing property.
     *
     * @param in the input stream.
     * @param coalescing the parser will coalesce character data 
     *                   if, and only if, this parameter is <tt>true</tt> 
     * @param namespaceAware
     *          the parser will support namespaces if, and only if, 
     *          this parameter is <tt>true</tt> 
     *
     * @param rejectDTDs
     *          the parser will throw an exception if the document
     *          contains a Document Type Declaration
     *
     */
    public Parser2(
        InputStream in,
        boolean coalescing,
        boolean namespaceAware,
        boolean rejectDTDs) {
        this(new InputSource(in), coalescing, namespaceAware, rejectDTDs);
    }

    /**
     * Create a new non-coalescing parser with the specified input stream.
     *
     * @param in the input stream.
     */
    public Parser2(InputStream in) {
        this(new InputSource(in), false, false, false);
    }

    /**
      * Create a new parser with the specified input file.
      * and the coalescing property.
      *
      * @param file the input file.
      * @param coalescing the parser will coalesce character data 
      *                   if, and only if, this parameter is <tt>true</tt> 
      * @param namespaceAware
      *          the parser will support namespaces if, and only if, 
      *          this parameter is <tt>true</tt> 
      *
      */
    public Parser2(File file, boolean coalescing, boolean namespaceAware)
        throws IOException {
        this(file, coalescing, namespaceAware, false);
    }

    /**
     * Create a new parser with the specified input file.
     *
     * @param file the input file.
     * @param coalescing the parser will coalesce character data 
     *                   if, and only if, this parameter is <tt>true</tt> 
     * @param namespaceAware
     *          the parser will support namespaces if, and only if, 
     *          this parameter is <tt>true</tt> 
     * @param rejectDTDs
     *          the parser will throw an exception if the document
     *          contains a Document Type Declaration
     *
     */
    public Parser2(
        File file,
        boolean coalescing,
        boolean namespaceAware,
        boolean rejectDTDs)
        throws IOException {
        InputStream in = new BufferedInputStream(new FileInputStream(file));
        InputSource is = new InputSource(in);
        is.setSystemId(file.toURL().toString());
        locator = new DocLocator();
        this.input = is;
        this.coalescing = coalescing;
        this.namespace = namespaceAware;
        this.rejectDTDs = rejectDTDs;
    }

    /**
     * Create a new non-coalescing parser with the specified input file.
     *
     * @param file the input file.
     */
    public Parser2(File file) throws IOException {
        InputStream in = new BufferedInputStream(new FileInputStream(file));
        InputSource is = new InputSource(in);
        is.setSystemId(file.toURL().toString());
        locator = new DocLocator();
        this.input = is;
    }

    private Parser2(
        InputSource input,
        boolean coalescing,
        boolean namespaceAware,
        boolean rejectDTDs) {
        locator = new DocLocator();
        this.input = input;
        this.coalescing = coalescing;
        this.namespace = namespaceAware;
        this.rejectDTDs = rejectDTDs;
    }

    // A stack to store recursive elements
    private FastStack stack = new FastStack(100);
    // A queue to store pis 
    private PIQueue piQueue = new PIQueue(10);

    // State flags for the parse method to decide what to do
    // with the element on top of the stack

    // orig state flags: these are mostly used in epilogue
    // processes
    private static final int ELEMENT_IN_CONTENT = 0x0001;
    private static final int ELEMENT_ROOT = 0x0002;
    private static final int CONTENT_IN_ELEMENT = 0x0004;
    private static final int CONTENT_IN_INTREF = 0x0008;
    private static final int CONTENT_IN_EXTREF = 0x0010;

    // current stat flags: these are used by the parser
    // to decide what to do next
    private static final int ELEMENT = 0x0100;
    private static final int CONTENT = 0x0400;

    private static final int START = 1;
    private static final int END = 2;
    private static final int CHARS = 3;
    private static final int PI = 4;
    private static final int EMPTY = 10;
    private static final int ATTR = 11; // not used anymore

    // these are temp variables used during the process of
    // an element
    private boolean haveAttributes = false;
    private int startLine;
    private boolean hasContent = true;

    // called at the beginning of parsing a document
    private void prologue() throws IOException, ParseException {
        init();
        if (input == null)
            fatal("P-000");
        in = InputEntity.getInputEntity(locale);
        in.init(input, null, null, false);

        // [1] document ::= prolog element Misc*
        // [22] prolog ::= XMLDecl? Misc* (DoctypeDecl Misc *)?

        maybeXmlDecl();
        maybeMisc(false);

        maybeDoctypeDecl();

        maybeMisc(false);
    }

    /**
     * Parse and return the next parsing state. This is the
     * entry point for higher level parsers like the 
     * StreamingParser.
     */
    public int parse() throws ParseException, IOException {
        int ret = 0;
        try {
            if (!donePrologue) {
                prologue();
                donePrologue = true;
            }
            if ((ret = retrievePIs()) != -1) {
                return ret;
            }

            if (!doneContent) {
                if (!coalescing) {
                    if ((ret = parseContent()) != EMPTY) {
                        return ret;
                    } else {
                        doneContent = true;
                    }
                } else {
                    if (lastRetWasEnd) {
                        ns.slideContextUp();
                        lastRetWasEnd = false;
                    }

                    // return the cached value if it exists.
                    if (cacheRet != -1) {
                        ret = cacheRet;
                        curName = cacheName;
                        curValue = cacheValue;
                        cacheRet = -1;
                        cacheName = null;
                        cacheValue = null;

                        if (namespace) {
                            if (ret == START) {
                                // playing back a START event
                                ns.slideContextDown();
                            } else if (ret == END) {
                                // playing back a END event
                                lastRetWasEnd = true;
                            }
                        }

                        return ret;
                    } else {
                        while ((ret = parseContent()) != EMPTY) {
                            if (ret == CHARS) {
                                // buffer the chars and loop
                                if (simpleCharsBuffer == null) {
                                    simpleCharsBuffer = curValue;
                                } else {
                                    if (charsBuffer == null) {
                                        charsBuffer = new StringBuffer();
                                        charsBuffer.append(simpleCharsBuffer);
                                    }
                                    charsBuffer.append(curValue);
                                }
                                continue;
                            } else if (ret != CHARS) {
                                if (simpleCharsBuffer != null) {
                                    // save the value for next invoke of parse
                                    cacheRet = ret;
                                    cacheName = curName;
                                    cacheValue = curValue;

                                    if (charsBuffer == null) {
                                        curName = null;
                                        curValue = simpleCharsBuffer;

                                    } else {
                                        // flush the char buffer
                                        curName = null;
                                        curValue = charsBuffer.toString();
                                        charsBuffer = null;
                                    }
                                    simpleCharsBuffer = null;

                                    if (namespace) {
                                        if (cacheRet == START) {
                                            // caching a START event
                                            ns.slideContextUp();
                                        } else if (cacheRet == END) {
                                            // caching a END event
                                            ns.slideContextDown();
                                        }
                                    }

                                    return CHARS;
                                } else {
                                    if (ret == END) {
                                        lastRetWasEnd = true;
                                        ns.slideContextDown();
                                    }
                                    return ret;
                                }
                            }
                        }
                        doneContent = true;
                    }
                }
            }

            if (!doneEpilogue) {
                epilogue();
                doneEpilogue = true;
            }
            return retrievePIs();
        } catch (EndOfInputException e) {
            if (!in.isDocument()) {
                String name = in.getName();
                try {
                    do { // force a relevant URI and line number  
                        in = in.pop();
                    } while (in.isInternal());
                    fatal("P-002", new Object[] { name }, e);
                } catch (IOException x) {
                    fatal("P-002", new Object[] { name }, e);
                }
            } else {
                fatal("P-003", null, e);
            }
        } catch (RuntimeException e) {
            // Don't discard location that triggered the exception
            throw new ParseException(
                e.getMessage() != null
                    ? e.getMessage()
                    : e.getClass().getName(),
                getPublicId(),
                getSystemId(),
                getLineNumber(),
                getColumnNumber());
        }

        return ret;
    }

    // retrieve pis from the pi queue
    private int retrievePIs() {
        if (!piQueue.empty()) {
            curName = piQueue.getNextTarget();
            curValue = piQueue.getNextContent();
            return PI;
        } else {
            return -1;
        }
    }

    // after the main content has been parsed
    private void epilogue() throws IOException, ParseException {
        try {
            afterRoot();
            maybeMisc(true);
            if (!in.isEOF())
                fatal(
                    "P-001",
                    new Object[] { Integer.toHexString(((int) getc()))});
        } catch (EndOfInputException e) {
            if (!in.isDocument()) {
                String name = in.getName();
                do { // force a relevant URI and line number  
                    in = in.pop();
                } while (in.isInternal());
                fatal("P-002", new Object[] { name }, e);
            } else {
                fatal("P-003", null, e);
            }
        } catch (RuntimeException e) {
            // Don't discard location that triggered the exception
            throw new ParseException(
                e.getMessage() != null
                    ? e.getMessage()
                    : e.getClass().getName(),
                getPublicId(),
                getSystemId(),
                getLineNumber(),
                getColumnNumber());
        } finally {
            // recycle temporary data used during parsing
            strTmp = null;
            attTmp = null;
            nameTmp = null;
            nameCache = null;

            // ditto input sources etc
            if (in != null) {
                in.close();
                in = null;
            }

            // get rid of all DTD info ... some of it would be
            // useful for editors etc, investigate later.

            params.clear();
            entities.clear();
            notations.clear();
            elements.clear();

            afterDocument();
        }
    }

    // parse an element
    private ElementDecl getElement() throws IOException, ParseException {
        // [39] element ::= EmptyElemTag | Stag content ETag
        // [40] STag ::= '<' Name (S Attribute)* S? '>'

        NameCacheEntry name;
        ElementDecl element;

        // the leading "<" has already been consumed

        name = maybeGetNameCacheEntry();

        // n.b. InputEntity guarantees 1+N char pushback always,
        // and maybeGetName won't use more than one to see if
        // it's instead "<?", "<!--", "<![CDATA[", or an error.
        if (name == null)
            return null;

        element = (ElementDecl) elements.get(name.name);
        if (element == null || element.contentType == null) {
            // minimize repetitive diagnostics
            element = new ElementDecl(name.name);
            element.contentType = strANY;
            elements.put(name.name, element);
        }
        // save the line number here so we can give better diagnostics
        // by identifying where the element started; WF errors may be
        // reported thousands of lines "late".
        startLine = in.getLineNumber();

        // Track whether we saw whitespace before an attribute;
        // in some cases it's required, though superfluous
        boolean sawWhite = in.maybeWhitespace();
        // Each pass through this loop reads
        //	Name eq AttValue S?
        // Loop exits on ">", "/>", or error
        for (;;) {
            if (in.peekc('>'))
                break;

            // [44] EmptyElementTag ::= '<' Name (S Attribute)* S? '/>'
            if (in.peekc('/')) {
                hasContent = false;
                break;
            }

            //Need to have a whitespace between attributes.
            if (!sawWhite)
                fatal("P-030");

            // [41] Attribute ::= Name Eq AttValue

            String attName;
            AttributeDecl info;
            String value;

            attName = maybeGetName();
            // Need to do this as we have already consumed the 
            // whitespace and didn't see the end tag.
            if (attName == null)
                fatal("P-031", new Object[] { new Character(getc())});

            if (attTmp.getValue(attName) != null)
                fatal("P-032", new Object[] { attName });

            // [25] Eq ::= S? '=' S?
            in.maybeWhitespace();
            nextChar('=', "F-026", attName);
            in.maybeWhitespace();

            parseLiteral(false);
            sawWhite = in.maybeWhitespace();

            // normalize and check values right away.

            info =
                (element == null)
                    ? null
                    : (AttributeDecl) element.attributes.get(attName);
            if (info == null) {
                value = strTmp.toString();
            } else {
                if (!AttributeDecl.CDATA.equals(info.type)) {
                    value = normalize(!info.isFromInternalSubset);
                } else
                    value = strTmp.toString();
            }

            if (XmlLang.equals(attName) && !isXmlLang(value))
                error("P-033", new Object[] { value });

            attTmp.addAttribute(
                "",
                attName,
                attName,
                (info == null) ? AttributeDecl.CDATA : info.type,
                value,
                (info == null) ? null : info.defaultValue,
                true);
            haveAttributes = true;
        }
        if (element != null)
            attTmp.setIdAttributeName(element.id);

        // if we had ATTLIST decls, handle required & defaulted attributes
        // before telling next layer about this element
        if (element != null && element.attributes.size() != 0)
            haveAttributes =
                defaultAttributes(attTmp, element) || haveAttributes;

        attr = attTmp;
        return element;
    }

    private boolean maybeReferenceInContent()
        throws IOException, ParseException {
        // [66] CharRef ::= ('&#' [0-9]+) | ('&#x' [0-9a-fA-F]*) ';'
        // [67] Reference ::= EntityRef | CharRef
        // [68] EntityRef ::= '&' Name ';'
        if (!in.peekc('&'))
            return false;
        else
            return true;
    }

    private boolean maybeEntityReference() throws IOException, ParseException {
        // [66] CharRef ::= ('&#' [0-9]+) | ('&#x' [0-9a-fA-F]*) ';'
        // [68] EntityRef ::= '&' Name ';'
        if (!in.peekc('#'))
            return true;
        else
            return false;
    }

    private Object getEntityReference() throws IOException, ParseException {
        String name = maybeGetName();
        if (name == null)
            fatal("P-009");
        nextChar(';', "F-020", name);
        Object entity = entities.get(name);
        err(" after in = " + in);

        if (entity == null) {
            //
            // Note:  much confusion about whether spec requires such
            // errors to be fatal in many cases, but none about whether
            // it allows "normal" errors to be unrecoverable!
            //
            fatal("P-014", new Object[] { name });
        }
        return entity;
    }

    //////////////////////////////////////////////////////////////////
    //
    // epilogues for elements, contents and refs.
    //
    //////////////////////////////////////////////////////////////////

    private void elementEpilogue(ElementDecl element)
        throws IOException, ParseException {
        if (!in.peek(element.name, null)) {
            fatal(
                "P-034",
                new Object[] { element.name, new Integer(startLine)});
        }
        in.maybeWhitespace();
        nextChar('>', "F-027", element.name);
        return;
    }

    private void intRefEpilogue(StackElement elt)
        throws IOException, ParseException {
        InternalEntity entity = (InternalEntity) elt.entity;
        InputEntity last = elt.in;
        if (in != last && !in.isEOF()) {
            while (in.isInternal())
                in = in.pop();
            fatal("P-052", new Object[] { entity.name });
        }
        in = in.pop();
        return;
    }

    private void extRefEpilogue(StackElement elt)
        throws IOException, ParseException {
        ExternalEntity entity = (ExternalEntity) elt.entity;

        if (!in.isEOF())
            fatal("P-058", new Object[] { entity.name });
        in = in.pop();
        return;
    }

    private boolean maybePI(boolean skipStart)
        throws IOException, ParseException {
        // [16] PI ::= '<?' PITarget
        //		(S (Char* - (Char* '?>' Char*)))?
        //		'?>'
        // [17] PITarget ::= Name - (('X'|'x')('M'|'m')('L'|'l')
        boolean savedLexicalPE = doLexicalPE;

        if (!in.peek(skipStart ? "?" : "<?", null))
            return false;
        doLexicalPE = false;

        String target = maybeGetName();
        String piContent = null;

        if (target == null)
            fatal("P-018");
        if ("xml".equals(target))
            fatal("P-019");
        if ("xml".equalsIgnoreCase(target))
            fatal("P-020", new Object[] { target });

        if (maybeWhitespace()) {
            strTmp = new StringBuffer();
            try {
                for (;;) {
                    // use in.getc to bypass PE processing
                    char c = in.getc();
                    //Reached the end of PI.
                    if (c == '?' && in.peekc('>'))
                        break;
                    strTmp.append(c);
                }
            } catch (EndOfInputException e) {
                fatal("P-021");
            }
            piContent = strTmp.toString();
        } else {
            if (!in.peek("?>", null))
                fatal("P-022");
            piContent = "";
        }

        doLexicalPE = savedLexicalPE;
        piQueue.in(target, piContent);
        return true;
    }

    /*
     * If namespace is true, we need to process the element
     * name and attributes for any name space declaration
     * and usages.
     */
    private void processStartElement(ElementDecl elt)
        throws IOException, ParseException {
        ns.pushContext();
        boolean seenDecl = false;

        int length = attr.getLength();
        for (int i = 0; i < length; i++) {
            String attRawName = attr.getQName(i);
            String value = attr.getValue(i);

            // Found a declaration...

            boolean isNamespaceDecl = false;
            String prefix = "";

            if (attRawName.startsWith("xmlns")) {
                isNamespaceDecl = true;
                if (attRawName.length() == 5) {
                    // no-op
                } else if (attRawName.charAt(5) == ':') {
                    prefix = attRawName.substring(6);
                } else {
                    isNamespaceDecl = false;
                }
            }

            if (isNamespaceDecl) {
				if (!ns.declarePrefix(prefix, value)) {
					// we made a nonfatal error fatal here:
					fatal(
						"P-086",
						new Object[] { prefix });
				}

                // we still return the xmlns: attribute to the
                // upper level
                seenDecl = true;

                // here we set the namespace URI of xmlns attributes
                // to the value "http://www.w3.org/2000/xmlns/", as
                // described in the XML Information Set specification
                attr.setURI(i, XMLNS_NAMESPACE_URI);

            } else {

                String attName[] = ns.processName(attRawName, parts, true);
                if (attName == null) {
                    // do not signal an error here
                } else {
                    attr.setURI(i, attName[0]);
                    attr.setLocalName(i, attName[1]);
                }
            }
        }

        // If there was a Namespace declaration,
        // we have to make a second pass just
        // to be safe -- this will happen very
        // rarely, possibly only once for each
        // document.
        if (seenDecl) {
            length = attr.getLength();
            for (int i = 0; i < length; i++) {
                String attRawName = attr.getQName(i);

                if (attRawName.startsWith("xmlns")) {
                    // Could be a namespace declaration

                    if (attRawName.length() == 5
                        || attRawName.charAt(5) == ':') {
                        // Default or non-default NS declaration
                        continue;
                    }
                }

                // assert(not a namespace declaration)
                String attName[] = ns.processName(attRawName, parts, true);
                if (attName == null) {
                    // do not signal an error here
                } else {
                    attr.setURI(i, attName[0]);
                    attr.setLocalName(i, attName[1]);
                }
            }
        }

        getSetCurName(elt.name, false);
        curValue = null;
    }

    /*
     * Set the element name and uri properly
     * if namespace is true.
     */
    private void processEndElement(ElementDecl elt)
        throws IOException, ParseException {
        getSetCurName(elt.name, false);
        ns.popContext();
    }

    /*
     * process the rawName and set the name, local name and
     * uri properly.
     */
    private void getSetCurName(String rawName, boolean isAttribute)
        throws ParseException {
        String names[] = ns.processName(rawName, parts, isAttribute);

        /* this "if" statement and the "throws ParseException" clause above
           were added so that when a name contains an invalid (i.e. undeclared)
           prefix an exception is thrown. Similarly, error "P-084" was specially
           created and added to the resources/Message_en.properties file.
        */

        if (names == null) {
            fatal("P-084", new Object[] { rawName });
        }

        curURI = names[0];
        curName = names[1];
        curValue = null;
        return;
    }

    /*
     * This is where all the content is parsed.
     */
    private int parseContent() throws IOException, ParseException {

        ElementDecl elt = null;

        while (true) {
            // we need a loop here since one pop and process action
            // may not generate a new parsed state

            if (stack.empty()) {
                // if the stack is empty, either we haven't started yet,
                // or we are done
                if (!startEmptyStack)
                    return EMPTY;

                //
                // One root element
                //
                if (startEmptyStack
                    && (!in.peekc('<') || (elt = getElement()) == null)) {
                    fatal("P-067");
                } else {
                    // push the root element, return the START state
                    startEmptyStack = false;
                    stack.push(
                        newStackElement(
                            ELEMENT_ROOT,
                            ELEMENT,
                            elt,
                            null,
                            null));
                    if (!haveAttributes && hasContent)
                        stack.push(
                            newStackElement(
                                CONTENT_IN_ELEMENT,
                                CONTENT,
                                elt,
                                null,
                                null));
                    // set the name and value
                    if (!namespace) {
                        curName = elt.name;
                        curValue = null;
                    } else {
                        processStartElement(elt);
                    }
                    return START;
                }
            } else {
                // when we pop an element from the stack, it could be in the of
                // the following states:
                // 1: cur_state = ELEMENT: we can get more attributes, if there
                //                         are any.
                // 2: cur_state = CONTENT: we are processing content

                StackElement se = (StackElement) stack.pop();
                elt = se.elt;
                switch (se.curState) {
                    case ELEMENT :
                        if (attr == null) {
                            // assertion, die if we come here
                            fatal("P-082");
                        }

                        // eliminated all ATTR events
                        // exhaust the attributes
                        // if (attrIndex < attr.getLength()) {
                        if (false) {
                            curName = attr.getLocalName(attrIndex);
                            curValue = attr.getValue(attrIndex);
                            curURI = attr.getURI(attrIndex);
                            attrIndex++;
                            stack.push(se);
                            return ATTR;
                        } else {
                            // we are done with the attributes, we need
                            // to proceed to parse the content if there
                            // is any
                            if (haveAttributes) {
                                attr = null;
                                attrIndex = 0;
                                attTmp.clear();
                                haveAttributes = false;
                            }
                            if (hasContent) {
                                // push back the element and push a new
                                // content onto the stack
                                stack.push(se);
                                stack.push(
                                    newStackElement(
                                        CONTENT_IN_ELEMENT,
                                        CONTENT,
                                        elt,
                                        null,
                                        null));
                            } else {
                                // if there are no content, means we are
                                // done with the parsing of the current element
                                hasContent = true;
                                nextChar('>', "F-027", elt.name);
                                freeStackElement(se);
                                curName = elt.name;
                                if (!namespace) {
                                    curValue = null;
                                } else {
                                    processEndElement(elt);
                                }
                                return END;
                            }
                        }
                        break;
                    case CONTENT :
                        ElementDecl e2 = null;
                        StackElement se2 = null;
                        String chars = null;
                        if (in.peekc('<')) {
                            if ((e2 = getElement()) != null) {
                                // an embedded element
                                stack.push(se);
                                stack.push(
                                    newStackElement(
                                        ELEMENT_IN_CONTENT,
                                        ELEMENT,
                                        e2,
                                        null,
                                        null));
                                if (!haveAttributes && hasContent)
                                    stack.push(
                                        newStackElement(
                                            CONTENT_IN_ELEMENT,
                                            CONTENT,
                                            e2,
                                            null,
                                            null));
                                if (!namespace) {
                                    curName = e2.name;
                                    curValue = null;
                                } else {
                                    processStartElement(e2);
                                }
                                return START;
                            } else if (in.peekc('/')) {
                                // fall through
                            } else if (maybeComment(true)) {
                                // loop back 
                                stack.push(se);
                                break;
                            } else if (maybePI(true)) {
                                // return a PI
                                stack.push(se);
                                curName = piQueue.getNextTarget();
                                curValue = piQueue.getNextContent();
                                return PI;
                            } else if (
                                (chars =
                                    in.getUnparsedContent(
                                        (elt != null) && elt.ignoreWhitespace,
                                        null))
                                    != null) {
                                // chars
                                stack.push(se);
                                if (chars.length() != 0) {
                                    curName = null;
                                    curValue = chars;
                                    return CHARS;
                                } else {
                                    break;
                                }
                            } else {
                                char c = getc();
                                fatal(
                                    "P-079",
                                    new Object[] {
                                        Integer.toHexString(c),
                                        new Character(c)});
                                // NOTREACHED
                            }
                        } else if (
                            elt != null
                                && elt.ignoreWhitespace
                                && in.ignorableWhitespace()) {
                            stack.push(se);
                            break;
                        } else if (
                            (chars = in.getParsedContent(coalescing))
                                != null) {
                            // chars
                            stack.push(se);
                            if (chars.length() != 0) {
                                curName = null;
                                curValue = chars;
                                return CHARS;
                            } else {
                                break;
                            }
                        } else if (in.isEOF()) {
                            // check if is allowed
                            if (se.origState == CONTENT_IN_ELEMENT) {
                                fatal("P-035");
                            }
                        } else if (maybeReferenceInContent()) {
                            // start parsing a reference
                            if (maybeEntityReference()) {
                                stack.push(se);
                                Object entity = getEntityReference();
                                InputEntity last = in;
                                if (entity instanceof InternalEntity) {
                                    InternalEntity e = (InternalEntity) entity;
                                    stack.push(
                                        newStackElement(
                                            CONTENT_IN_INTREF,
                                            CONTENT,
                                            elt,
                                            e,
                                            last));
                                    pushReader(e.buf, e.name, true);
                                    break;
                                } else if (entity instanceof ExternalEntity) {
                                    ExternalEntity e = (ExternalEntity) entity;
                                    if (e.notation != null)
                                        fatal("P-053", new Object[] { e.name });
                                    if (!pushReader(e)) {
                                        break;
                                    }
                                    maybeTextDecl();
                                    stack.push(
                                        newStackElement(
                                            CONTENT_IN_EXTREF,
                                            CONTENT,
                                            elt,
                                            e,
                                            null));
                                    break;
                                } else {
                                    throw new InternalError();
                                }
                            } else {
                                stack.push(se);
                                int ret =
                                    surrogatesToCharTmp(parseCharNumber());
                                curName = null;
                                curValue = new String(charTmp, 0, ret);
                                return CHARS;
                            }
                        }

                        // we come here either because we see </, or we see EOF
                        // means content is complete, do the epilogue
                        if (se.origState == CONTENT_IN_ELEMENT) {
                            // end of content in element
                            se2 = (StackElement) stack.pop();
                            if (se2.curState != ELEMENT) {
                                // the stack state is not right
                                fatal("P-083");
                            }
                            // element also ended
                            elementEpilogue(elt);
                            curName = elt.name;
                            if (!namespace) {
                                curValue = null;
                            } else {
                                processEndElement(elt);
                            }
                            freeStackElement(se);
                            freeStackElement(se2);
                            return END;
                        } else if (se.origState == CONTENT_IN_INTREF) {
                            // end of an internal reference
                            intRefEpilogue(se);
                            freeStackElement(se);
                        } else if (se.origState == CONTENT_IN_EXTREF) {
                            // end of an external reference
                            extRefEpilogue(se);
                            freeStackElement(se);
                        }
                        break;
                    default :
                        // we should not come here
                        fatal("P-083");
                        break;
                }
            }
        }
    }


    // a fast, customized stack implementation
    private final class FastStack {
        private StackElement first;

        public FastStack(int initialCapacity) {
            // no-op
        }

        public boolean empty() {
            return first == null;
        }

        public void push(StackElement e) {
            if (first == null) {
                first = e;
            } else {
                e.next = first;
                first = e;
            }
        }

        public StackElement pop() {
            StackElement result = first;
            first = first.next;
            result.next = null;
            return result;
        }

        public void clear() {
            first = null;
        }
    }

    // stack element
    private final class StackElement {
        int origState;
        int curState;
        ElementDecl elt;
        EntityDecl entity;
        InputEntity in;
        // added this field to support the new FastStack implementation above
        StackElement next;

        public StackElement(
            int origState,
            int curState,
            ElementDecl elt,
            EntityDecl entity,
            InputEntity in) {
            this.origState = origState;
            this.curState = curState;
            this.elt = elt;
            this.entity = entity;
            this.in = in;
        }
    }

    private StackElement newStackElement(
        int origState,
        int curState,
        ElementDecl elt,
        EntityDecl entity,
        InputEntity in) {
        return new StackElement(origState, curState, elt, entity, in);
    }

    private void freeStackElement(StackElement e) {
    }

    // a queue to store processes PIs 
    private final class PIQueue {
        private String[] pi;
        private int size = 0;
        private int index = 0;

        public PIQueue(int initialCapacity) {
            this.pi = new String[2 * initialCapacity];
        }

        public boolean empty() {
            return size == index;
        }

        public void clear() {
            size = 0;
        }

        public void in(String target, String content) {
            ensureCapacity();
            pi[size++] = target;
            pi[size++] = content;
        }

        public String getNextTarget() {
            String result = null;
            if (index < size) {
                result = pi[index];
                pi[index++] = null;
            }
            return result;
        }

        public String getNextContent() {
            String result = null;
            if (index < size) {
                result = pi[index];
                pi[index++] = null;
            }
            return result;
        }

        /**
         * Ensure space for at least one more element, roughly
         * doubling the capacity each time the array needs to grow.
         */
        private void ensureCapacity() {
            if (pi.length == size) {
                String oldPi[] = pi;
                pi = new String[2 * pi.length + 2];
                System.arraycopy(oldPi, 0, pi, 0, size);
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////
    //
    // following are all convenient methods for debugging purposes
    //
    /////////////////////////////////////////////////////////////////////////

    private void err(String msg) {
    }

    private void debug() {
    }
}
