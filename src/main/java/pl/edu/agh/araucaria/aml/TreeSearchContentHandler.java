package pl.edu.agh.araucaria.aml; /**
 * <b><code>MyContentHandler</code></b> implements the SAX 
 *   <code>ContentHandler</code> interface and defines callback
 *   behavior for the SAX callbacks associated with an XML
 *   document's content.
 */

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import pl.edu.agh.araucaria.model.Tree;
import pl.edu.agh.araucaria.model.TreeVertex;

import java.util.Stack;

/**
 * Used by SAX to read in an AML file and extract the tree
 * structure from it - ignores schemesets, prop-texts etc.
 * Used in tree searches.
 */
public class TreeSearchContentHandler implements ContentHandler {

    String contents = "";
    Tree m_tree;
    Stack<TreeVertex> vertexStack = new Stack<>();
    TreeVertex currentVertex = null;
    boolean refutation = false;
    String missingString = "no";
    String shortLabel = "";
    boolean missing = false;
    String propText;
    private boolean readContents = false;

    public TreeSearchContentHandler(Tree tree) {
        m_tree = tree;
    }

    /**
     * <p>
     * Provide reference to <code>Locator</code> which provides
     * information about where in a document callbacks occur.
     * </p>
     *
     * @param locator <code>Locator</code> object tied to callback
     *                process
     */
    public void setDocumentLocator(Locator locator) {
    }

    /**
     * <p>
     * This indicates the start of a Document parse - this precedes
     * all callbacks in all SAX Handlers with the sole exception
     * of <code>{@link #setDocumentLocator}</code>.
     * </p>
     *
     */
    public void startDocument() throws SAXException {
    }

    /**
     * <p>
     * This indicates the end of a Document parse - this occurs after
     * all callbacks in all SAX Handlers.</code>.
     * </p>
     *
     */
    public void endDocument() throws SAXException {
    }

    /**
     * <p>
     * This will indicate that a processing instruction (other than
     * the XML declaration) has been encountered.
     * </p>
     *
     * @param target <code>String</code> target of PI
     * @param data   <code>String</code containing all data sent to the PI.
     *               This typically looks like one or more attribute value
     *               pairs.
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
     * itself (<code>{@link #startElement}</code>) occurs.
     * </p>
     *
     * @param prefix <code>String</code> prefix used for the namespace
     *               being reported
     * @param uri    <code>String</code> URI for the namespace
     *               being reported
     */
    public void startPrefixMapping(String prefix, String uri) {
    }

    /**
     * <p>
     * This indicates the end of a prefix mapping, when the namespace
     * reported in a <code>{@link #startPrefixMapping}</code> callback
     * is no longer available.
     * </p>
     *
     * @param prefix <code>String</code> of namespace being reported
     */
    public void endPrefixMapping(String prefix) {
    }

    /**
     * <p>
     * This reports the occurrence of an actual element.  It will include
     * the element's attributes, with the exception of XML vocabulary
     * specific attributes, such as
     * <code>xmlns:[namespace prefix]</code> and
     * <code>xsi:schemaLocation</code>.
     * </p>
     *
     * @param namespaceURI <code>String</code> namespace URI this element
     *                     is associated with, or an empty
     *                     <code>String</code>
     * @param localName    <code>String</code> name of element (with no
     *                     namespace prefix, if one is present)
     * @param rawName      <code>String</code> XML 1.0 version of element name:
     *                     [namespace prefix]:[localName]
     * @param atts         <code>Attributes</code> list for this element
     */
    public void startElement(String namespaceURI, String localName,
                             String rawName, Attributes atts)
            throws SAXException {

        // We've now read the opening tag and attribute list, so we
        // can now process each tag and build the argument tree
        switch (rawName) {
            case "ARG":
                break;
            case "SCHEMESET":
                break;
            case "SCHEME":
                break;
            case "NAME":
                break;
            case "FORM":
                break;
            case "PREMISE":
                break;
            case "CONCLUSION":
                break;
            case "CQ":
                break;
            case "TEXT":
                break;
            case "CA":
                vertexStack.push(currentVertex);
                break;
            case "LA":
                TreeVertex virtualVertex = new TreeVertex("", "V");
                virtualVertex.setVirtual(true);
                virtualVertex.setHasParent(true);
                m_tree.addVertex(virtualVertex);
                currentVertex.addEdge(virtualVertex);
                vertexStack.push(currentVertex);
                vertexStack.push(virtualVertex);
                break;
            case "REFUTATION":
                vertexStack.push(currentVertex);
                refutation = true;
                break;
            case "PROP":
                for (int i = 0; i < atts.getLength(); i++) {
                    String attName = atts.getQName(i);
                    String attValue = atts.getValue(i);
                    if (attName.equals("identifier")) {
                        shortLabel = attValue;
                    } else if (attName.equals("missing")) {
                        missingString = attValue;
                        missing = (missingString.equals("yes"));
                    }
                }
                break;
            case "PROPTEXT":
                break;
            case "INSCHEME":
                break;
        }
        // Turn on the readContents flag so that the characters() method
        // will store the data in a single string.
        readContents = true;
        contents = "";
    }

    /**
     * <p>
     * Indicates the end of an element
     * (<code>&lt;/[element name]&gt;</code>) is reached.  Note that
     * the parser does not distinguish between empty
     * elements and non-empty elements, so this will occur uniformly.
     * </p>
     *
     * @param namespaceURI <code>String</code> URI of namespace this
     *                     element is associated with
     * @param localName    <code>String</code> name of element without prefix
     * @param rawName      <code>String</code> name of element in XML 1.0 form
     */
    public void endElement(String namespaceURI, String localName,
                           String rawName)
            throws SAXException {
        if (readContents) {
            readContents = false;
        }

        switch (rawName) {
            case "ARG":
                break;
            case "SCHEMESET":
                break;
            case "SCHEME":
                break;
            case "NAME":
                break;
            case "FORM":
                break;
            case "PREMISE":
                break;
            case "CONCLUSION":
                break;
            case "CQ":
                break;
            case "TEXT":
                break;
            case "REFUTATION": {
                TreeVertex poppedVertex = vertexStack.pop();
                poppedVertex.addEdge(currentVertex);
                currentVertex.setHasParent(true);
                currentVertex.setRefutation(true);
                currentVertex = poppedVertex;
                break;
            }
            case "CA": {
                TreeVertex poppedVertex = vertexStack.pop();
                poppedVertex.addEdge(currentVertex);
                currentVertex.setHasParent(true);
                currentVertex = poppedVertex;
                break;
            }
            case "LA":
                vertexStack.pop();    // Pops virtual vertex

                currentVertex = vertexStack.pop();
                break;
            case "PROPTEXT":
                propText = "";
                currentVertex = new TreeVertex(propText, shortLabel);
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
                    TreeVertex topVertex = vertexStack.peek();
                    if (topVertex.isVirtual()) {
                        topVertex.addEdge(currentVertex);
                        currentVertex.setHasParent(true);
                    }
                }
                break;
        }
    }

    /**
     * <p>
     * This will report character data (within an element).
     * </p>
     *
     * @param ch    <code>char[]</code> character array with character data
     * @param start <code>int</code> index in array where data starts.
     * @param end   <code>int</code> index in array where data ends.
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
     * @param ch    <code>char[]</code> character array with character data
     * @param start <code>int</code> index in array where data starts.
     * @param end   <code>int</code> index in array where data ends.
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
     * @param name <code>String</code> name of entity being skipped
     */
    public void skippedEntity(String name) throws SAXException {
    }

}


