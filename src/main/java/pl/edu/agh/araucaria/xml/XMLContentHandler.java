package pl.edu.agh.araucaria.xml; /**
 * <b>MyContentHandler</b> implements the SAX
 * ContentHandler interface and defines callback
 * behavior for the SAX callbacks associated with an XML
 * document's content.
 */

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import pl.edu.agh.araucaria.ArgType;
import pl.edu.agh.araucaria.Argument;
import pl.edu.agh.araucaria.Subtree;
import pl.edu.agh.araucaria.Tree;
import pl.edu.agh.araucaria.TreeVertex;

import java.awt.geom.GeneralPath;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;


public class XMLContentHandler implements ContentHandler {

    private String contents = "";
    private Argument argument;
    private Tree tree;
    private Stack vertexStack = new Stack();
    private TreeVertex currentVertex = null;
    private GeneralPath shape = null;
    private Vector argTypeVector;
    private Hashtable diagramRoles;
    private ArgType argType = null;
    private String supportLabel = "";
    private String nodeLabel = "";
    private String shortLabel = " ";
    private boolean missing = false;
    private boolean refutation = false;
    private int offset;
    private int tutorStart;
    private int tutorEnd;
    private boolean readContents = false;

    public XMLContentHandler(Argument arg) {
        argument = arg;
        tree = arg.getTree();
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
        // We save this for later use if desired.
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
        // TODO: restore the data encoding in main.java.Araucaria class
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

        String attName, attValue;
        // We've now read the opening tag and attribute list, so we
        // can now process each tag and build the argument tree
        switch (rawName) {
            case "ARG":
                argTypeVector = null;
                break;
            case "SCHEMESET":
                argTypeVector = new Vector();
                break;
            case "SCHEME":
                argType = new ArgType();
                break;
            case "CA":
                vertexStack.push(currentVertex);
                break;
            case "LA":
                TreeVertex virtualVertex = new TreeVertex("", "V");
                virtualVertex.setVirtual(true);
                virtualVertex.setHasParent(true);
                tree.addVertex(virtualVertex);
                currentVertex.addEdge(virtualVertex);
                vertexStack.push(currentVertex);
                vertexStack.push(virtualVertex);
                break;
            case "REFUTATION":
                vertexStack.push(currentVertex);
                refutation = true;
                break;
            case "PROP":
                tutorStart = tutorEnd = -1;
                supportLabel = null;
                nodeLabel = null;
                diagramRoles = new Hashtable();
                TreeVertex.initRoles(diagramRoles);
                // Add default role for Toulmin node
                diagramRoles.put("toulmin", "data");

                for (int i = 0; i < atts.getLength(); i++) {
                    attName = atts.getQName(i);
                    attValue = atts.getValue(i);
                    switch (attName) {
                        case "identifier":
                            String idTag = attValue;
                            // Remove Wigmore prefix for numerical IDs
                            if (idTag.contains(Argument.WigmoreIDPrefix)) {
                                idTag = idTag.substring(Argument.WigmoreIDPrefix.length());
                            }
                            shortLabel = idTag;
                            break;
                        case "missing":
                            String missingString = attValue;
                            missing = (missingString.equals("yes"));
                            break;
                        case "supportlabel":
                            supportLabel = attValue;
                            break;
                        case "nodelabel":
                            nodeLabel = attValue;
                            break;
                    }
                }
                break;
            case "PROPTEXT":
                for (int i = 0; i < atts.getLength(); i++) {
                    attName = atts.getQName(i);
                    attValue = atts.getValue(i);
                    if (attName.equals("offset")) {
                        offset = Integer.parseInt(attValue);
                    }
                }
                break;
            case "OWNER":
                String ownerName = "";
                attName = atts.getQName(0);
                attValue = atts.getValue(0);
                if (attName.equals("name")) {
                    ownerName = attValue;
                    Vector ownerVector = argument.ownerExists(ownerName, 0);
                    if (ownerVector == null) {
                        ownerVector = new Vector();
                        String tla = argument.getTla(ownerName);
                        if (tla == null) tla = "***";
                        ownerVector.add(ownerName);
                        ownerVector.add(tla);
                        argument.addToOwnerList(ownerVector);
                    }
                    currentVertex.getOwners().add(ownerVector);
                }
                break;
            case "INSCHEME":
                String schemeID = "";
                String schemeName = "";
                for (int i = 0; i < atts.getLength(); i++) {
                    attName = atts.getQName(i);
                    attValue = atts.getValue(i);
                    if (attName.equals("scheme")) {
                        schemeName = attValue;
                    } else if (attName.equals("schid")) {
                        schemeID = attValue;
                    }
                }
                Subtree subtree = argument.getSubtreeByLabel(schemeID);
                if (subtree == null) {
                    // Add a new subtree
                    subtree = new Subtree();
                    subtree.setShortLabel(schemeID);

                    ArgType argType = argument.getArgTypeByName(schemeName);
                    subtree.setArgumentType(argType);

                    subtree.addVertex(currentVertex);
                    argument.getSubtreeList().add(subtree);
                } else {
                    // Add vertex to existing subtree
                    subtree.addVertex(currentVertex);
                }
                // Deal with subtrees containing linked arguments
                if (!vertexStack.isEmpty()) {
                    if (((TreeVertex) vertexStack.peek()).isVirtual()) {
                        TreeVertex virtualTemp = (TreeVertex) vertexStack.pop();
                        TreeVertex parentTemp = (TreeVertex) vertexStack.peek();
                        if (subtree.containsTreeVertex(parentTemp)) {
                            subtree.addVertex(virtualTemp);
                        }
                        vertexStack.push(virtualTemp);
                    }
                }
                break;
            case "ROLE":
                String diagType = null, element = null;
                for (int i = 0; i < atts.getLength(); i++) {
                    attName = atts.getQName(i);
                    attValue = atts.getValue(i);
                    if (attName.equals("class")) {
                        diagType = attValue;
                    } else if (attName.equals("element")) {
                        element = attValue;
                    }
                }
                diagramRoles.put(diagType, element);
                break;
            case "TUTOR":
                for (int i = 0; i < atts.getLength(); i++) {
                    attName = atts.getQName(i);
                    attValue = atts.getValue(i);
                    if (attName.equals("start")) {
                        tutorStart = Integer.parseInt(attValue);
                    } else if (attName.equals("end")) {
                        tutorEnd = Integer.parseInt(attValue);
                    }
                }
                currentVertex.setTutorStart(tutorStart);
                currentVertex.setTutorEnd(tutorEnd);
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

    // TODO: Remove references to m_canvas since display stuff doesn't belong here
    public void endElement(String namespaceURI, String localName,
                           String rawName)
            throws SAXException {
        if (readContents) {
            readContents = false;
        }

        switch (rawName) {
            case "ARG":
                argument.addXMLSubtrees();
                argument.standardToToulmin();
                argument.standardToWigmore();
                break;
            case "SCHEMESET":
                argument.setSchemeList(argTypeVector);
                break;
            case "NAME":
                argType.setName(contents);
                argTypeVector.add(argType);
                break;
            case "PREMISE":
                if (contents.length() > 0) {
                    argType.getPremises().add(contents);
                }
                break;
            case "CONCLUSION":
                if (contents.length() > 0) {
                    argType.setConclusion(contents);
                }
                break;
            case "CQ":
                if (contents.length() > 0) {
                    argType.getCriticalQuestions().add(contents);
                }
                break;
            case "TEXT":
                if (contents.length() > 0) {
                    argument.setText(contents);
                }
                break;
            case "AUTHOR":
                if (contents.length() > 0) {
                    argument.setAuthor(contents);
                }
                break;
            case "DATE":
                if (contents.length() > 0) {
                    argument.setDate(contents);
                }
                break;
            case "SOURCE":
                if (contents.length() > 0) {
                    argument.setSource(contents);
                }
                break;
            case "COMMENTS":
                if (contents.length() > 0) {
                    argument.setComments(contents);
                }
                break;
            case "REFUTATION": {
                TreeVertex poppedVertex = (TreeVertex) vertexStack.pop();
                poppedVertex.addEdge(currentVertex);
                currentVertex.setHasParent(true);
                currentVertex.setParent(poppedVertex);
                currentVertex.setRefutation(true);
                currentVertex = poppedVertex;
                break;
            }
            case "CA": {
                TreeVertex poppedVertex = (TreeVertex) vertexStack.pop();
                poppedVertex.addEdge(currentVertex);
                // If we are adding an edge to a dummy root, we are restoring
                // a broken tree, so we are in an undo operation.
                // The dummy root is the invisible root that holds the true roots
                // of the tree fragments.
                // The parent should only be set to 'true' if it is not a dummy root
                if (!new String(poppedVertex.getShortLabel()).equals("DummyRoot")) {
                    currentVertex.setHasParent(true);
                    currentVertex.setParent(poppedVertex);
                } else {
                    argument.setMultiRoots(true);
                    argument.setDummyRoot(poppedVertex);
                }
                currentVertex = poppedVertex;
                break;
            }
            case "LA":
                TreeVertex virtPop = (TreeVertex) vertexStack.pop();    // Pops virtual vertex

                currentVertex = (TreeVertex) vertexStack.pop();
                virtPop.setParent(currentVertex);
                break;
            case "PROPTEXT":
                String propText = contents;
                currentVertex = new TreeVertex(propText, shortLabel);
                currentVertex.setSupportLabel(supportLabel);
                currentVertex.m_nodeLabel = nodeLabel;
                currentVertex.roles = diagramRoles;
                // Check if the shortLabel is numeric and thus update Wigmore index
                argument.updateWigmoreIndex(shortLabel);

                // Set the short label so that new nodes will be added
                // after nodes read in from AML file
                // Only update the label if the current label is greater than
                // or equal to the current short label

                // TODO: restore short label display parameters

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
                tree.addVertex(currentVertex);
                // If the top vertex on the stack is virtual, we are within an LA,
                // so we add an edge between the virtual node and the new PROP
                if (!vertexStack.isEmpty()) {
                    TreeVertex topVertex = (TreeVertex) vertexStack.peek();
                    if (topVertex.isVirtual()) {
                        topVertex.addEdge(currentVertex);
                        currentVertex.setHasParent(true);
                        currentVertex.setParent(topVertex);
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

    public Argument getArgument() {
        return argument;
    }

    public void setArgument(Argument argument) {
        this.argument = argument;
    }

    public Tree getTree() {
        return tree;
    }

    public void setTree(Tree tree) {
        this.tree = tree;
    }

    public GeneralPath getShape() {
        return shape;
    }

    public void setShape(GeneralPath shape) {
        this.shape = shape;
    }

    public boolean isMissing() {
        return missing;
    }

    public void setMissing(boolean missing) {
        this.missing = missing;
    }

    public boolean isRefutation() {
        return refutation;
    }

    public void setRefutation(boolean refutation) {
        this.refutation = refutation;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

}

