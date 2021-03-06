package pl.edu.agh.araucaria.model;

import pl.edu.agh.araucaria.gui.visualisation.core.DiagramBase;

import java.awt.*;
import java.io.Serializable;
import java.util.Hashtable;

/**
 *
 */

public class TreeEdge implements Serializable {
    public int undoOrder;  // Number the edges coming out of a vertex so they can be undone in the same order
    public boolean visible = true;
    TreeVertex m_sourceVertex, m_destVertex;
    double m_weight;
    boolean m_visited;
    boolean m_selected;
    Hashtable<DiagramBase, Shape> shapeTable = new Hashtable<>();   // Hashtable for storing diagram shapes for various diagrams
    Hashtable<DiagramBase, Shape> schemeShapeTable = new Hashtable<>(); // An outline of the edge without the arrowhead, used for schemes

    /**
     * Creates an pl.edu.agh.araucaria.model.Edge leading from sourceVertex to destVertex.
     * The edge has a weight of zero.
     *
     * @param sourceVertex The start of the pl.edu.agh.araucaria.model.Edge.
     * @param destVertex   The end of the pl.edu.agh.araucaria.model.Edge.
     */
    public TreeEdge(TreeVertex sourceVertex, TreeVertex destVertex) {
        initialize(sourceVertex, destVertex);
    }

    /**
     * Creates an pl.edu.agh.araucaria.model.TreeEdge leading from sourceVertex to destVertex.
     * The edge has the weight specified.
     *
     * @param sourceVertex The start of the pl.edu.agh.araucaria.model.Edge.
     * @param destVertex   The end of the pl.edu.agh.araucaria.model.Edge.
     * @param weight       The weight of the pl.edu.agh.araucaria.model.Edge.
     */
    public TreeEdge(TreeVertex sourceVertex, TreeVertex destVertex,
                    double weight) {
        initialize(sourceVertex, destVertex);
        m_weight = weight;
    }

    protected void initialize(TreeVertex sourceVertex, TreeVertex destVertex) {
        m_sourceVertex = sourceVertex;
        m_destVertex = destVertex;
        m_weight = 0.0;
        m_visited = false;
        m_selected = false;
    }

    public TreeVertex getDestVertex() {
        return m_destVertex;
    }

    public void setDestVertex(TreeVertex dest) {
        m_destVertex = dest;
    }

    public TreeVertex getSourceVertex() {
        return m_sourceVertex;
    }

    public void setVisited(boolean visited) {
        m_visited = visited;
    }

    public Shape getShape(DiagramBase diagram) {
        return shapeTable.get(diagram);
    }

    public void setShape(Shape shape, DiagramBase diagram) {
        shapeTable.put(diagram, shape);
    }

    public Shape getSchemeShape(DiagramBase diagram) {
        return schemeShapeTable.get(diagram);
    }

    public void setSchemeShape(Shape shape, DiagramBase diagram) {
        schemeShapeTable.put(diagram, shape);
    }

    public boolean isSelected() {
        return m_selected;
    }

    public void setSelected(boolean selected) {
        m_selected = selected;
    }

} // pl.edu.agh.araucaria.model.TreeEdge
