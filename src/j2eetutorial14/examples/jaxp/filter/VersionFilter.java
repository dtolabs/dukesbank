/*
 * Copyright (c) 2006 Sun Microsystems, Inc.  All rights reserved.  U.S.
 * Government Rights - Commercial software.  Government users are subject
 * to the Sun Microsystems, Inc. standard license agreement and
 * applicable provisions of the FAR and its supplements.  Use is subject
 * to license terms.
 *
 * This distribution may include materials developed by third parties.
 * Sun, Sun Microsystems, the Sun logo, Java and J2EE are trademarks
 * or registered trademarks of Sun Microsystems, Inc. in the U.S. and
 * other countries.
 *
 * Copyright (c) 2006 Sun Microsystems, Inc. Tous droits reserves.
 *
 * Droits du gouvernement americain, utilisateurs gouvernementaux - logiciel
 * commercial. Les utilisateurs gouvernementaux sont soumis au contrat de
 * licence standard de Sun Microsystems, Inc., ainsi qu'aux dispositions
 * en vigueur de la FAR (Federal Acquisition Regulations) et des
 * supplements a celles-ci.  Distribue par des licences qui en
 * restreignent l'utilisation.
 *
 * Cette distribution peut comprendre des composants developpes par des
 * tierces parties. Sun, Sun Microsystems, le logo Sun, Java et J2EE
 * sont des marques de fabrique ou des marques deposees de Sun
 * Microsystems, Inc. aux Etats-Unis et dans d'autres pays.
 */


import java.io.*;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.ext.LexicalHandler;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;


/**
 * Filters a program using conditional-text processing. Can be run
 * on plain text (like source code) that is contained in an outer
 * tag (to make it XML). When the +root, +xhtml, and +chg flags are absent,
 * the result is a version of the source code that can be compiled.
 * When the +root flag is present, the root tag is also output. If +xhtml,
 * the XML declaration is output along with the root tag. If the root tag
 * is <pre>, the result is an XHTML version of the code that can be
 * included in an XML document or pasted into an HTML document.
 * <p></p>
 * If either +xhtml or +root is specified, output is indented and comments
 * are echoed. Otherwise, comments are ignored and the data (presumably
 * source code or other text) is output without any special indentation.
 * <p></p>
 * Adding the +chg flag adds additional highlighting for added and
 * removed text. (Since all other tags in the file are passed through
 * intact, the program can be used to filter well-formed HTML or XML
 * data, as well.)
 * <p></p>
 * The default version for the document equals 1. That is, versions
 * are one-based, rather than zero-based.
 * <p></p>
 * The following tags control version processing, where <i>nn</i> is
 * the version number:
 * <pre>
 *   &lt;?VERSION nn ADD?>...&lt;?VERSION {nn} END ADD?>
 *   &lt;?VERSION nn DEL?>...&lt;?VERSION {nn} END DEL?>
 * </pre>
 * <!-- Plain Text Version of above:
 *   <?VERSION nn ADD?>...<VERSION {nn} END ADD?>
 *   <?VERSION nn DEL?>...<VERSION {nn} END DEL?>
 * -->
 * (Version numbers are optional on the end-tags.)
 * <p></p>
 * Because these tags are processing instructions, they can span XML
 * tags, which allows them to produce multiple versions of XML files.
 * <p></p>
 * When the targetVersion is "n", and change-indicator markup is included
 * in the output with "+chg", added text is flagged like this:<pre>
 *   &lt;new>&lt;b>...&lt;/b>&lt;/new>
 * </pre>
 * The "new" tag is ignored by HTML browsers, but can be used by XML browsers
 * to display change bars. Similarly, deleted text is flagged like this:<pre>
 *   &lt;old>&lt;strike>...&lt;/strike>&lt;/old>
 * </pre>
 * <!-- Plain Text versions of the above:
 *   <new><b>...</b></new>
 *   <old><strike>...</strike></old>
 * -->
 * <p></p>
 * On output, the special processing instruction <code>&lt;?version #?></code> is
 * replaced with the target version number (the # of the version being produced).
 * <!-- Plain Text version of the above:
 *   <?version #?>
 * -->
 * <p></p>
 * To use this class:<pre>
 *   java -classpath ".:${XML_HOME}/xml.jar" VersionFilter v {+xhtml | +root) {+chg} yourFile.xml
 * </pre>
 * where:
 * <ul>
 * <li>v     is the version number
 * <li>+xml  says to add the xml declaration and the root tag
 * <li>???+root says to output the root tag  (Root tag should be "pre" for source code)
 * <li>+chg  says to output change-indicator markup
 * </ul>
 * Examples:<ul>
 * <li>Source code for compile:<pre>  cmd N file.xml > fileN.java</pre>
 * <li>Code for use with HTML:<pre>   cmd N +root +chg file.xml > fileN.txt</pre>
 * <li>Code for use with XML:<pre>    cmd N +xhtml +chg file.xml > fileN.txt</pre>
 * <li>XML data for use:<pre>         cmd N +xhtml file.xml > fileN.xml</pre>
 * <li>XML data for book:<pre>        cmd N +xhtml +chg file.xml > file-verN.html</pre>
 * </ul>
 *
 * @author Eric Armstrong
 */
public class VersionFilter extends DefaultHandler implements LexicalHandler {
    // Strings we'll output for change-indicators
    static final String START_NEW = "<new><b>";
    static final String END_NEW = "</b></new>";
    static final String START_OLD = "<old><strike>";
    static final String END_OLD = "</strike></old>";
    static boolean chg = false; // whether to output change-indicators
    static boolean root = false; // whether to output root element
    static boolean xhtml = false; // whether to output xml declaration & do indenting
    static int targetVersion = 0; // The version to produce
    static String targetString = "1"; // version# string
    static private Writer out;
    int currVersion = 0; // The version for current element
    boolean echo = true; // Says to echo the text we see.
    boolean highlight = false; // Says to highlight text with bolding
    boolean strikeout = false; // Says to use strikethrough font

    // Tells us we saw a processing nstruction so we can swallow the NL
    // that follows it.
    boolean pi = false;
    int resumeLevel = 0; // Level at which we turned echo off
    int noBoldLevel = 0; // Level at which we turned highlighting on
    int noStrikeLevel = 0; // Level at which we turned strikethrough on
    int piLevel = 0; // Current processing-instruction level
    String wsBuf = ""; // Queue up whitespace until we know what to do with it

    //    private int tagLevel = 0;             // Depth in the tag-tree
    private int indentLevel = -1; // Amount to indent formatted output
                                  //   (does not include add/del tags)
    private String indentString = "    "; // Amount to indent

    //-----------------------------------------------------------
    // Helpers ...
    //-----------------------------------------------------------
    String lastBuf = ""; // Last character string seen
    String startString = "";
    String endString = "";
    int lastLevel = -1;

    // Do this when NLs in buffer are replaced by lineEnd
    // in the printBuf routine:
    //String lineEnd =  System.getProperty("line.separator");
    String lineEnd = "\n";

    public static void main(String[] argv) throws IOException {
        //        InputSource input;
        if ((argv.length < 2) ||
                (argv.length > 4) ||
                ((argv.length == 3) &&
                !(argv[1].equals("+xhtml") || argv[1].equals("+root") ||
                argv[1].equals("+chg"))) ||
                ((argv.length == 4) &&
                !((argv[1].equals("+xhtml") || argv[1].equals("+root")) &&
                argv[2].equals("+chg")))) {
            System.err.println(
                "Usage: java VersionFilter version# {+xhtml | +root) {+chg} xmlfile");
            System.exit(1);
        }

        // Default factory setting == non-validating parser.
        SAXParserFactory factory = SAXParserFactory.newInstance();
        DefaultHandler handler = new VersionFilter();

        try {
            // Set up output stream
            out = new OutputStreamWriter(System.out, "UTF8");

            targetString = argv[0];

            int filenameIndex = 1;

            if (argv[1].equals("+xhtml") || argv[1].equals("+root")) {
                root = true;

                if (argv[1].equals("+xhtml")) {
                    xhtml = true;
                }

                filenameIndex = 2;

                if (argv[2].equals("+chg")) {
                    chg = true;
                    filenameIndex = 3;
                }
            } else if (argv[1].equals("+chg")) {
                chg = true;
                filenameIndex = 2;
            }

            // Convert versionID to numeric
            String filename = argv[filenameIndex];
            targetVersion = Integer.parseInt(targetString);

            // Get an instance of the parser and parse the file.
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(new File(filename), handler);
        } catch (SAXParseException err) {
            System.out.println("** Parsing error" + ", line " +
                err.getLineNumber() + ", uri " + err.getSystemId());
            System.out.println("   " + err.getMessage());

            // Unpack the delivered exception to get the exception it contains
            Exception x = err;

            if (err.getException() != null) {
                x = err.getException();
            }

            x.printStackTrace();
        } catch (SAXException e) {
            Exception x = e;

            if (e.getException() != null) {
                x = e.getException();
            }

            x.printStackTrace();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        System.exit(0);
    }

    //===========================================================
    // SAX DocumentHandler methods
    //===========================================================
    public void setDocumentLocator(Locator l) {
        // Save this to resolve relative URIs or to give diagnostics.
        //        try {
        //          out.write ("LOCATOR");
        //          out.write ("\n SYS ID: " + l.getSystemId() );
        //          out.flush ();
        //        } catch (IOException e) {
        //            // Ignore errors
        //        }
    }

    public void startDocument() throws SAXException {
        if (xhtml) {
            //emit ("<?xml version='1.0' encoding='UTF-8'?>", 0);
            emit("<html xmlns='http://www.w3.org/1999/xhtml' xml:lang='en' lang='en'>",
                0);
            emit("\n<body>", 0);
            emit("\n<pre>", 0);
            nl();
            indentLevel--; // act like an end tag seen
        }
    }

    public void endDocument() throws SAXException {
        if (xhtml) {
            emit("\n</pre>", 0);
            emit("\n</body>", 0);
            emit("\n</html>", 0);
        }

        try {
            nl();
            out.flush();
            out = null;
        } catch (IOException e) {
            throw new SAXException("I/O error", e);
        }
    }

    public void startElement(java.lang.String namespaceURI,
        java.lang.String sName, // simple name (localName)
        java.lang.String qName, // qualified name
        Attributes attrs) throws SAXException {
        // If we're in a delete-section or a section added for a later
        // revision, ignore everything until we close that section.
        if (!echo) {
            return;
        }

        // Ignore root element tag unless instructed otherwise
        if ((indentLevel == -1) && !root) {
            return;
        }

        // Output tags and their attributes
        // Track the indentation level for formatted xml output
        int level = indentLevel + 1;
        String eName = sName; // element name

        if ("".equals(eName)) {
            eName = qName; // not namespaceAware
        }

        emit("<" + eName, level);

        if (attrs != null) {
            for (int i = 0; i < attrs.getLength(); i++) {
                nl();
                emit("  " + attrs.getLocalName(i) + "=\"" + attrs.getValue(i) +
                    "\"", level);
            }
        }

        if (attrs.getLength() != 0) {
            nl();
        }

        emit(">", level);
        nl();
    }

    public void endElement(java.lang.String namespaceURI,
        java.lang.String sName, // simple name
        java.lang.String qName // qualified name
    ) throws SAXException {
        // If we're in a delete-section or a section added for a later
        // revision, ignore everything until we close that section.
        if (!echo) {
            return;
        }

        // Ignore root element tag unless instructed otherwise
        if (!root && (indentLevel == -1)) {
            return;
        }

        // Echo other tags
        // Track the indentation level for formatted xml output
        String eName = sName; // element name

        if ("".equals(eName)) {
            eName = qName; // not namespaceAware
        }

        emit("</" + eName + ">", indentLevel);
        nl();
        indentLevel--; // end tag seen
    }

    public void characters(char[] buf, int offset, int len)
        throws SAXException {
        // Don't echo unless the version is appropriate
        if (!echo) {
            return;
        }

        String s = new String(buf, offset, len);

        // Kill any whitespace after a processing instruction
        // along with the NL that follows it.
        if (pi) {
            pi = false;

            int ix = s.indexOf('\n');

            if (ix > -1) {
                boolean allWhitespace = true;

                for (int j = 0; j < ix; j++) {
                    if ((s.charAt(j) != ' ') && (s.charAt(j) != '\t')) {
                        allWhitespace = false;

                        break;
                    }
                }

                if (allWhitespace) {
                    //System.out.println("\nchars: |"+s+"|");
                    if (ix < s.length()) {
                        s = s.substring(ix + 1);
                    } else {
                        s = "";
                    }
                }
            }
        }

        if (s.trim()
                 .equals("")) {
            // Accumulate the whitespace
            wsBuf += s;
        } else {
            // We have character data.
            if (xhtml) {
                // Escape '&' and '<' for well-formed XML output.
                s = "";

                for (int i = 0; i < len; i++) {
                    if (buf[offset + i] == '<') {
                        s += "&lt;";
                    } else if (buf[offset + i] == '&') {
                        s += "&amp;";
                    } else if (buf[offset + i] == '>') {
                        s += "&gt;";
                    } else {
                        s += buf[offset + i];
                    }
                }
            }

            // Emit the data, along with any whitespace we've accumulated,
            // then clear the whitespace buffer.
            emit(wsBuf + s, indentLevel);
            wsBuf = "";
        }
    }

    // Check to see if character is a whitespace char
    protected boolean white(char c) {
        if (c == ' ') {
            return true;
        }

        if (c == '\n') {
            return true;
        }

        if (c == '\t') {
            return true;
        }

        return false;
    }

    // Trim ending blanks from a string
    protected String rtrim(String s) {
        while (s.endsWith(" ")) {
            s = s.substring(0, s.length() - 1);
        }

        return s;
    }

    public void ignorableWhitespace(char[] buf, int offset, int len)
        throws SAXException {
        // If it exists, ignore it!
    }

    // WHAT WE NEED TO DO:
    // * BUFFER THE LAST CHARACTER STRING WE SAW
    // * WHEN WE SEE A PROCESSSING INSTRUCTION, REMOVE THE
    //   TRAILING BLANKS FROM THAT STRING.
    // * FLUSH THE BUFFER WHEN:
    //    --we see another character string
    //    --we see any other element
    //    --we come to the end of the document
    public void processingInstruction(String target, String data)
        throws SAXException {
        // Remove whitespace immediately ahead of the processing instruction.
        int ix = wsBuf.lastIndexOf('\n');

        if (ix >= 0) {
            wsBuf = wsBuf.substring(0, ix + 1);
        }

        //System.out.print("\nPI: " + target + " " + data);

        // Tell character handler to kill the NL after the PI.
        pi = true;

        // Remove rightmost blanks from previous character string
        // stored in lastBuf.
        lastBuf = rtrim(lastBuf);

        // Process version control instructions
        if (target.equals("version")) {
            // OUTPUT TARGET VERSION#
            if (data.equals("#")) {
                // If we're in a delete-section or a section added for a later
                // revision, ignore this instruction.
                if (!echo) {
                    return;
                }

                emit("" + targetString, indentLevel);

                return;
            }

            // END-SECTION TAG
            if (data.endsWith("end add") || data.endsWith("end del")) {
                if (!echo && (piLevel == resumeLevel)) {
                    echo = true;
                }

                // If we're at the end of highlight-section, turn off highlighting
                if (highlight &&
                        data.endsWith("end add") &&
                        (piLevel == noBoldLevel)) {
                    highlight = false;
                    endNew();
                } else if (strikeout &&
                        data.endsWith("end del") &&
                        (piLevel == noStrikeLevel)) {
                    strikeout = false;
                    endOld();
                }

                piLevel--;

                return;
            }

            // START-SECTION TAG
            if (data.endsWith("add") || data.endsWith("del")) {
                piLevel++;

                // If we're in a delete-section or a section added for a later
                // revision, ignore everything until we close that section.
                if (!echo) {
                    return;
                }

                // Figure out the version
                currVersion = Integer.parseInt(data.substring(0,
                            data.length() - 4));

                // Determine whether to echo this element
                echo = true;

                if ((data.endsWith("add") && (currVersion > targetVersion)) ||
                        (chg && data.endsWith("del") &&
                        (currVersion < targetVersion)) ||
                        (!chg && data.endsWith("del") &&
                        (currVersion <= targetVersion))) {
                    echo = false;
                    resumeLevel = piLevel;

                    return;
                }

                // Determine whether to highlight or strikeout succeeding elements
                if (chg) {
                    if (data.endsWith("add") && (currVersion == targetVersion)) {
                        highlight = true;
                        noBoldLevel = piLevel;
                        startNew();

                        return;
                    } else if (data.endsWith("del") &&
                            (currVersion == targetVersion)) {
                        strikeout = true;
                        noStrikeLevel = piLevel;
                        startOld();

                        return;
                    }
                }
            }

            return;
        }

        // Output other processing instructions
        emit("<?" + target + " " + data + "?>", indentLevel + 1);
        nl();
        indentLevel--; // act like an end tag seen
    }

    public void skippedEntity(String name) throws SAXException {
        // Ignore it
    }

    public void startPrefixMapping(String prefix, String url)
        throws SAXException {
        // Ignore it
    }

    public void endPrefixMapping(String prefix) throws SAXException {
        // Ignore it
    }

    //-----------------------------------------------------------
    // Lexical Event Listener Methods
    //-----------------------------------------------------------
    public void comment(char[] ch, int start, int length)
        throws SAXException {
        // If we're in a delete-section or a section added for a later
        // revision, ignore everything until we close that section.
        if (!echo) {
            return;
        }

        // Root-element tag is only included if we are producing
        // pretty-print output. In that case, echo comments.
        if (!root) {
            return;
        }

        // Echo Comments
        String text = new String(ch, start, length);
        emit("<!-- " + text + " -->", indentLevel + 1);
        nl();
        indentLevel--; // act like an end tag seen
    }

    public void startCDATA() throws SAXException {
        // Nada
    }

    public void endCDATA() throws SAXException {
        // Nada
    }

    public void startEntity(java.lang.String name) throws SAXException {
        // Nada
    }

    public void endEntity(java.lang.String name) throws SAXException {
        // Nada
    }

    public void startDTD(String name, String publicId, String systemId)
        throws SAXException {
        // Ignore it
    }

    public void endDTD() throws SAXException {
        // Ignore it
    }

    void startNew() throws SAXException {
        printBuf(); // flush buffer
        startString = START_NEW;
        endString = END_NEW;
    }

    void startOld() throws SAXException {
        printBuf(); // flush buffer
        startString = START_OLD;
        endString = END_OLD;
    }

    void endNew() throws SAXException {
        printBuf(); // flush buffer
        startString = "";
        endString = "";
    }

    void endOld() throws SAXException {
        printBuf(); // flush buffer
        startString = "";
        endString = "";
    }

    // Wrap I/O exceptions in SAX exceptions, to
    // suit handler signature requirements
    private void emit(String s, int level) throws SAXException {
        if (level != indentLevel) {
            //|| (lastBuf.indexOf('\n') != -1)
            if (lastBuf != "") {
                nl(); // flush buffer
            }

            indentLevel = level;
        }

        lastBuf += s;
    }

    // Flush the buffer and start a new line
    private void nl() throws SAXException {
        try {
            // Check last non-space character. If it's a newline,
            // don't write a second line end. It's already got one.
            int i = lastBuf.length() - 1;

            while (i >= 0) {
                if ((lastBuf.charAt(i) != ' ') && (lastBuf.charAt(i) != '\t')) {
                    break;
                }

                i--;
            }

            boolean writeNL = true;

            if ((i > 0) && (lastBuf.charAt(i) == '\n')) {
                writeNL = false;
            }

            printBuf();

            if (writeNL) {
                out.write(lineEnd);
            }
        } catch (IOException e) {
            throw new SAXException("I/O error", e);
        }
    }

    // Add end-string tags before NLs in the existing buffer
    // and put startString tags after any spaces on the next
    // line.
    private void printBuf() throws SAXException {
        if (lastBuf.equals("")) {
            return;
        }

        int i = 0;
        int lastInsertPosition = -1;
        StringBuffer sb = new StringBuffer(lastBuf);

        if (root) {
            // Root-element tag is only included if we are producing
            // pretty-print output. In that case, do indentation.
            for (int j = 0; j < indentLevel; j++) {
                // Indent the line
                sb.insert(i, indentString);
                i += indentString.length();
            }
        }

        while (white(sb.charAt(i))) {
            i++;

            if (i >= sb.length()) {
                break;
            }
        }

        if (i < sb.length()) {
            // Not all whitespace
            sb.insert(i, startString);
            i += (startString.length() + 1);
        }

LOOP: while (i < sb.length()) {
            if (sb.charAt(i) == '\n') {
                sb.insert(i, endString);
                i += endString.length();
                lastInsertPosition = i;
                i++;

                if (i >= sb.length()) {
                    break LOOP;
                }

                if (root) {
                    // Root-element tag is only included if we are producing
                    // pretty-print output. In that case, do indentation.
                    for (int j = 0; j < indentLevel; j++) {
                        // Indent the line
                        sb.insert(i, indentString);
                        i += indentString.length();
                    }
                }

                while (white(sb.charAt(i))) {
                    // Move past whitespace, including multiple NLs
                    i++;

                    if (i >= sb.length()) {
                        break LOOP;
                    }
                }

                sb.insert(i, startString);
                i += startString.length();
                lastInsertPosition = i;
            }

            i++;
        }

        // If there are characters after the final insert,
        // we were called by nl(). Make sure there is a
        // tag-terminator after the last non-white character.
        for (i = sb.length() - 1; i >= lastInsertPosition; i--) {
            if (!white(sb.charAt(i))) {
                if ((i + 1) == sb.length()) {
                    sb.append(endString);
                } else {
                    sb.insert(i + 1, endString);
                }

                break;
            }
        }

        try {
            out.write(sb.toString());
            out.flush();
        } catch (IOException e) {
            throw new SAXException("I/O error", e);
        }

        lastBuf = "";
    }

    //printBuf
}
