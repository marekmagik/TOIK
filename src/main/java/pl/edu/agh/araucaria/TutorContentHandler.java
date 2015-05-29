package pl.edu.agh.araucaria;/*
 * pl.edu.agh.araucaria.TutorContentHandler.java
 *
 * Created on 07 March 2004, 08:08
 */

/**
 * @author growe
 */

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import java.util.Stack;
import java.util.Vector;
/**
 * <b>MyContentHandler</b> implements the SAX 
 *   ContentHandler interface and defines callback
 *   behavior for the SAX callbacks associated with an XML
 *   document's content.
 */

/**
 * Used by SAX to read in an AML file and extract the tree
 * structure from it - ignores schemesets, prop-texts etc.
 * Used in tree searches.
 */
public class TutorContentHandler implements ContentHandler {
    String contents = "";
    Tree m_tree;
    Stack vertexStack = new Stack();
    TreeVertex currentVertex = null;
    boolean refutation = false;
    Vector argTypeVector;
    ArgType argType = null;
    String missingString = "no";
    String supportLabel = "";
    String nodeLabel = "";
    String shortLabel = " ";
    boolean missing = false;
    int offset, tutorStart, tutorEnd;
    String propText;
    private boolean readContents = false;

    /**
     * Creates a new instance of pl.edu.agh.araucaria.TutorContentHandler
     */
    public TutorContentHandler(Tree tree) {
        m_tree = tree;
    }


    /**
     * <p>
     * Provide reference to Locator which provides
     * information about where in a document callbacks occur.
     * </p>
     *
     * @param locator Locator object tied to callback
     *                process
     */
    public void setDocumentLocator(Locator locator) {
    }

    /**
     * <p>
     * This indicates the start of a Document parse - this precedes
     * all callbacks in all SAX Handlers with the sole exception
     * of {@link #setDocumentLocator}.
     * </p>
     *
     * @throws SAXException when things go wrong
     */
    public void startDocument() throws SAXException {
    }

    /**
     * <p>
     * This indicates the end of a Document parse - this occurs after
     * all callbacks in all SAX Handlers..
     * </p>
     *
     * @throws SAXException when things go wrong
     */
    public void endDocument() throws SAXException {
    }

    /**
     * <p>
     * This will indicate that a processing instruction (other than
     * the XML declaration) has been encountered.
     * </p>
     *
     * @param target String target of PI
     * @param data   String</code containing all data sent to the PI.
     *               This typically looks like one or more attribute value
     *               pairs.
     * @throws SAXException when things go wrong
     */
    public void processingInstruction(String target, String data)
            throws SAXException {
    }

    /**
     * <p>
     * This will indicate the beginning of an XML Namespace prefix
     * mapping.  Although this typically occur within the root element
     * of an XML document, it can occur at any point within the
     * document.  Note that a prefix mapping on an element triggers
     * this callback <i>before</i> the callback for the actual element
     * itself ({@link #startElement}) occurs.
     * </p>
     *
     * @param prefix String prefix used for the namespace
     *               being reported
     * @param uri    String URI for the namespace
     *               being reported
     */
    public void startPrefixMapping(String prefix, String uri) {
//        System.out.println("Mapping starts for prefix " + prefix + 
//                           " mapped to URI " + uri);
    }

    /**
     * <p>
     * This indicates the end of a prefix mapping, when the namespace
     * reported in a {@link #startPrefixMapping} callback
     * is no longer available.
     * </p>
     *
     * @param prefix String of namespace being reported
     */
    public void endPrefixMapping(String prefix) {
//        System.out.println("Mapping ends for prefix " + prefix);
    }

    /**
     * <p>
     * This reports the occurrence of an actual element.  It will include
     * the element's attributes, with the exception of XML vocabulary
     * specific attributes, such as
     * xmlns:[namespace prefix] and
     * xsi:schemaLocation.
     * </p>
     *
     * @param namespaceURI String namespace URI this element
     *                     is associated with, or an empty
     *                     String
     * @param localName    String name of element (with no
     *                     namespace prefix, if one is present)
     * @param rawName      String XML 1.0 version of element name:
     *                     [namespace prefix]:[localName]
     * @param atts         Attributes list for this element
     * @throws SAXException when things go wrong
     */
    public void startElement(String namespaceURI, String localName,
                             String rawName, Attributes atts)
            throws SAXException {

        // We've now read the opening tag and attribute list, so we
        // can now process each tag and build the argument tree
        if (rawName.equals("ARG")) {
            argTypeVector = null;
        } else if (rawName.equals("SCHEMESET")) {
            argTypeVector = new Vector();
        } else if (rawName.equals("SCHEME")) {
            argType = new ArgType();
        } else if (rawName.equals("CA")) {
            vertexStack.push(currentVertex);
        } else if (rawName.equals("LA")) {
            TreeVertex virtualVertex = new TreeVertex("", "V");
            virtualVertex.setVirtual(true);
            virtualVertex.setHasParent(true);
            m_tree.addVertex(virtualVertex);
            currentVertex.addEdge(virtualVertex);
            vertexStack.push(currentVertex);
            vertexStack.push(virtualVertex);
        } else if (rawName.equals("REFUTATION")) {
            vertexStack.push(currentVertex);
            refutation = true;
        } else if (rawName.equals("PROP")) {
            for (int i = 0; i < atts.getLength(); i++) {
                String attName = atts.getQName(i);
                String attValue = atts.getValue(i);
                if (attName.equals("identifier")) {
                    shortLabel = attValue;
                } else if (attName.equals("missing")) {
                    missingString = attValue;
                    missing = (missingString.equals("yes"));
                } else if (attName.equals("supportlabel")) {
                    supportLabel = attValue;
                } else if (attName.equals("nodelabel")) {
                    nodeLabel = attValue;
                }
            }
        } else if (rawName.equals("PROPTEXT")) {
            for (int i = 0; i < atts.getLength(); i++) {
                String attName = atts.getQName(i);
                String attValue = atts.getValue(i);
                if (attName.equals("offset")) {
                    offset = Integer.parseInt(attValue);
                }
            }
        } else if (rawName.equals("TUTOR")) {
            for (int i = 0; i < atts.getLength(); i++) {
                String attName = atts.getQName(i);
                String attValue = atts.getValue(i);
                if (attName.equals("start")) {
                    tutorStart = Integer.parseInt(attValue);
                } else if (attName.equals("end")) {
                    tutorEnd = Integer.parseInt(attValue);
                }
            }
            currentVertex.setTutorStart(tutorStart);
            currentVertex.setTutorEnd(tutorEnd);
        }
        // Turn on the readContents flag so that the characters() method
        // will store the data in a single string.
        readContents = true;
        contents = "";
    }

    /**
     * <p>
     * Indicates the end of an element
     * (&lt;/[element name]&gt;) is reached.  Note that
     * the parser does not distinguish between empty
     * elements and non-empty elements, so this will occur uniformly.
     * </p>
     *
     * @param namespaceURI String URI of namespace this
     *                     element is associated with
     * @param localName    String name of element without prefix
     * @param rawName      String name of element in XML 1.0 form
     * @throws SAXException when things go wrong
     */
    public void endElement(String namespaceURI, String localName,
                           String rawName)
            throws SAXException {
        if (readContents) {
            readContents = false;
        }

        if (rawName.equals("REFUTATION")) {
            TreeVertex poppedVertex = (TreeVertex) vertexStack.pop();
            poppedVertex.addEdge(currentVertex);
            currentVertex.setHasParent(true);
            currentVertex.setRefutation(true);
            currentVertex = poppedVertex;
        } else if (rawName.equals("CA")) {
            TreeVertex poppedVertex = (TreeVertex) vertexStack.pop();
            poppedVertex.addEdge(currentVertex);
            currentVertex.setHasParent(true);
            currentVertex = poppedVertex;
        } else if (rawName.equals("LA")) {
            currentVertex = (TreeVertex) vertexStack.pop();
        } else if (rawName.equals("PROPTEXT")) {
            propText = contents;
            currentVertex = new TreeVertex(propText, shortLabel);
            currentVertex.setSupportLabel(supportLabel);
            currentVertex.m_nodeLabel = nodeLabel;
            currentVertex.setOffset(offset);
            tutorStart = offset;
            tutorEnd = offset + propText.length();
            currentVertex.setTutorStart(tutorStart);
            currentVertex.setTutorEnd(tutorEnd);

            if (missing) {
                currentVertex.setMissing(true);
            }
            if (refutation) {
                currentVertex.setRefutation(true);
                refutation = false;
            }
            m_tree.addVertex(currentVertex);
            // If the top vertex on the stack is virtual, we are within an LA,
            // so we add an edge between the virtual node and the new PROP
            if (!vertexStack.isEmpty()) {
                TreeVertex topVertex = (TreeVertex) vertexStack.peek();
                if (topVertex.isVirtual()) {
                    topVertex.addEdge(currentVertex);
                    currentVertex.setHasParent(true);
                }
            }
        }
    }

    /**
     * <p>
     * This will report character data (within an element).
     * </p>
     *
     * @param ch    char[] character array with character data
     * @param start int index in array where data starts.
     * @param end   int index in array where data ends.
     * @throws SAXException when things go wrong
     */
    public void characters(char[] ch, int start, int end)
            throws SAXException {
        contents += new String(ch, start, end);
    }

    /**
     * <p>
     * This will report whitespace that can be ignored in the
     * originating document.  This is typically only invoked when
     * validation is ocurring in the parsing process.
     * </p>
     *
     * @param ch    char[] character array with character data
     * @param start int index in array where data starts.
     * @param end   int index in array where data ends.
     * @throws SAXException when things go wrong
     */
    public void ignorableWhitespace(char[] ch, int start, int end)
            throws SAXException {
    }

    /**
     * <p>
     * This will report an entity that is skipped by the parser.  This
     * should only occur for non-validating parsers, and then is still
     * implementation-dependent behavior.
     * </p>
     *
     * @param name String name of entity being skipped
     * @throws SAXException when things go wrong
     */
    public void skippedEntity(String name) throws SAXException {
    }

}


  

