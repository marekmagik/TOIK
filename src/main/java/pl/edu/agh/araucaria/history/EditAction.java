package pl.edu.agh.araucaria.history;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import pl.edu.agh.araucaria.Araucaria;
import pl.edu.agh.araucaria.model.Argument;
import pl.edu.agh.araucaria.model.TreeVertex;

import java.io.ByteArrayInputStream;
import java.util.Hashtable;
import java.util.Vector;

public class EditAction {
    private String amlString;
    private Vector<TreeVertex> freeVertexList;
    private Vector freeVertexListIDs;
    private Vector hiddenList;
    private String shortLabel;
    private Araucaria parent;
    private Argument argument;
    public String description, message;

    public EditAction(Araucaria a, String d) {
        parent = a;
        argument = parent.getArgument();
        amlString = parent.getUndoAML();
        shortLabel = argument.getShortLabel();
        freeVertexList = argument.getFreeVerticesInList();
        freeVertexListIDs = argument.getFreeVerticesInListIDs();
        hiddenList = argument.getHiddenList();
        description = d;
        parent.undoMenuItem.setEnabled(true);
        parent.undoToolBar.setEnabled(true);
    }

    public void restore(boolean undo, String m, boolean showMessage) {
        message = m;
        argument.emptyTree(true);
        try {
            ByteArrayInputStream byteStream = new ByteArrayInputStream(amlString.getBytes());
            InputSource saxInput = new InputSource(byteStream);
            parent.parseXMLwithSAX(saxInput);
        } catch (SAXException e) {
            System.out.println("SAXException in parsing undo: " + e.getMessage());
            e.printStackTrace();
            System.out.println(amlString);
        } catch (Exception e) {
            System.out.println("Exception in GeneralAction undo: " + e.getMessage());
            e.printStackTrace();
        } catch (Error e) {
            System.out.println("Error in GeneralAction undo: " + e.getMessage());
        }
        // Call updateSelectText to restore all the links between the text and the
        // vertexes in the tree
        parent.updateSelectText();
        // Now add any free vertexes
        for (int i = 0; i < freeVertexList.size(); i++) {
            TreeVertex freeVertex = freeVertexList.elementAt(i);
            freeVertex.setSelected(false);
//TODO : sprawdzić zmianę

            freeVertex.setShortLabel(new String((char[]) freeVertexListIDs.elementAt(i)));
            argument.getFreeVertexList().add(freeVertex);
            freeVertex.setHasParent(false);
            freeVertex.deleteAllEdges();
            freeVertex.initRoles();
            if (!freeVertex.isMissing()) {
                parent.getSelectText().getSelectedList().add(freeVertex.getAuxObject());
            }
        }

        // Restore hidden properties
        Vector vertexList = argument.getTree().getVertexList();
        for (int i = 0; i < vertexList.size(); i++) {
            TreeVertex vertex = (TreeVertex) vertexList.elementAt(i);
            vertex.setIsHiddenTable(getHiddenTable(vertex.getShortLabelString()));
        }
        parent.getSelectText().repaint();
        argument.setShortLabel(shortLabel);
        if (showMessage) {
            String infoString = undo ? "Undoing " : "Redoing ";
            infoString += message;
            parent.setMessageLabelText(infoString);
        }
        // Needed since the standardToWigmore call made from the XML parser is done
        // before the hidden properties of the added negations have been restored
        argument.standardToWigmore();
    }

    Hashtable getHiddenTable(String id) {
        for (int i = 0; i < hiddenList.size(); i++) {
            HiddenInfo info = (HiddenInfo) hiddenList.elementAt(i);
            if (id.equals(info.getShortID())) {
                return info.getIsHiddenTable();
            }
        }
        return null;
    }
}
