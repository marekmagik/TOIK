package pl.edu.agh.araucaria.xml; /**
 * <b><code>MyErrorHandler</code></b> implements the SAX
 * <code>ErrorHandler</code> interface and defines callback
 * behavior for the SAX callbacks associated with an XML
 * document's errors.
 */

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XMLErrorHandler implements ErrorHandler {

    /**
     * <p>
     * This will report a warning that has occurred; this indicates
     * that while no XML rules were "broken", something appears
     * to be incorrect or missing.
     * </p>
     *
     * @param exception <code>SAXParseException</code> that occurred.
     * @throws SAXException when things go wrong
     */
    public void warning(SAXParseException exception)
            throws SAXException {
        throw new SAXException("Warning encountered");
    }

    /**
     * <p>
     * This will report an error that has occurred; this indicates
     * that a rule was broken, typically in validation, but that
     * parsing can reasonably continue.
     * </p>
     *
     * @param exception <code>SAXParseException</code> that occurred.
     * @throws SAXException when things go wrong
     */
    public void error(SAXParseException exception)
            throws SAXException {
        throw new SAXException("**Parsing Error**\n" +
                "  Line:    " +
                exception.getLineNumber() + "\n" +
                "  URI:     " +
                exception.getSystemId() + "\n" +
                "  Message: " +
                exception.getMessage());
    }

    /**
     * <p>
     * This will report a fatal error that has occurred; this indicates
     * that a rule has been broken that makes continued parsing either
     * impossible or an almost certain waste of time.
     * </p>
     *
     * @param exception <code>SAXParseException</code> that occurred.
     * @throws SAXException when things go wrong
     */
    public void fatalError(SAXParseException exception)
            throws SAXException {
        throw new SAXException("Fatal Error encountered");
    }

}


