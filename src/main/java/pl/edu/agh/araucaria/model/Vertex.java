package pl.edu.agh.araucaria.model;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Represents a pl.edu.agh.araucaria.model.Vertex in a pl.edu.agh.araucaria.model.Graph object
 */
public class Vertex implements Serializable {
    Object m_label;
    protected Vector m_edgeList;
    boolean m_visited;
    double m_distance;            // Distance from source in Dijkstra's alg
    int m_predecessorCount;    // Number of edges leading to vertex

    /**
     * Creates a pl.edu.agh.araucaria.model.Vertex with the specified label.
     *
     * @param label An Object (or derivative) to be used to label this vertex.
     *              pl.edu.agh.araucaria.model.Vertex labels need not be unique within a graph.
     */
    public Vertex(Object label) {
        m_label = label;
        m_visited = false;
        m_edgeList = new Vector<>(10, 10);
        m_distance = 0.0;
        m_predecessorCount = 0;
    }

    /**
     * Adds an edge (with no weight) from the current pl.edu.agh.araucaria.model.Vertex to destVertex.
     * Checks if an edge to destVertex already exists,
     * and if not, adds an edge to destVertex. You aren't allowed to
     * have more than one edge from one vertex to another.
     *
     * @param destVertex The destination pl.edu.agh.araucaria.model.Vertex for the edge.
     * @return true if the edge was added, false if an edge already exists
     * between these two vertices.
     */
    public boolean addEdge(Vertex destVertex) {
        if (edgeExists(destVertex) != null)
            return false;
        Edge newEdge = new Edge(this, destVertex);
        m_edgeList.add(newEdge);
        return true;
    }

    /**
     * Adds an edge with specified weight from the current pl.edu.agh.araucaria.model.Vertex to destVertex.
     * Checks if an edge to destVertex already exists,
     * and if not, adds an edge to destVertex. You aren't allowed to
     * have more than one edge from one vertex to another.
     *
     * @param destVertex The destination pl.edu.agh.araucaria.model.Vertex for the edge.
     * @param weight     The weight of the edge to be added.
     *                   between these two vertices.
     */
    public void addEdge(Vertex destVertex, double weight) {
        if (edgeExists(destVertex) != null)
            return;
        Edge newEdge = new Edge(this, destVertex);
        m_edgeList.add(newEdge);
    }

    /**
     * Adds an edge (with no weight) from the current pl.edu.agh.araucaria.model.Vertex to destVertex.
     * Checks if an edge to destVertex already exists,
     * and if not, adds an edge to destVertex. You aren't allowed to
     * have more than one edge from one vertex to another.
     *
     * @param destVertex The destination pl.edu.agh.araucaria.model.Vertex for the edge.
     * @param directed   'true' if the edge is directed; false if undirected.
     *                   In the latter case, an identical edge in the opposite direction is also
     *                   added.
     *                   between these two vertices.
     */
    public void addEdge(Vertex destVertex, boolean directed) {
        addEdge(destVertex);
        if (!directed) {
            destVertex.addEdge(this);
        }
    }

    /**
     * Adds an edge with specified weight from the current pl.edu.agh.araucaria.model.Vertex to destVertex.
     * Checks if an edge to destVertex already exists,
     * and if not, adds an edge to destVertex. You aren't allowed to
     * have more than one edge from one vertex to another.
     *
     * @param destVertex The destination pl.edu.agh.araucaria.model.Vertex for the edge.
     * @param directed   'true' if the edge is directed; false if undirected.
     *                   In the latter case, an identical edge in the opposite direction is also
     *                   added.
     * @param weight     The weight of the edge to be added.
     *                   between these two vertices.
     */
    public void addEdge(Vertex destVertex, boolean directed, double weight) {
        addEdge(destVertex, weight);
        if (!directed) {
            destVertex.addEdge(this, weight);
        }
    }

    /**
     * Retrieves the label of the vertex.
     *
     * @return An Object containing the label.
     */
    public Object getLabel() {
        return m_label;
    }

    public void setLabel(Object label) {
        m_label = label;
    }

    /**
     * Searches for an edge from the current vertex to destVertex.
     *
     * @param destVertex The destination pl.edu.agh.araucaria.model.Vertex.
     * @return The pl.edu.agh.araucaria.model.Edge object if it exists, null otherwise.
     */
    public Edge edgeExists(Vertex destVertex) {
        Enumeration edgeList = m_edgeList.elements();
        while (edgeList.hasMoreElements()) {
            Edge edge = (Edge) edgeList.nextElement();
            Vertex dest = edge.getDestVertex();
            if (dest == destVertex)
                return edge;
        }
        return null;
    }

    public boolean getVisited() {
        return m_visited;
    }

    public void setVisited(boolean visited) {
        m_visited = visited;
    }

    public Vector getEdgeList() {
        return m_edgeList;
    }

    public void changePredecessorCount(int count) {
        m_predecessorCount += count;
    }

    public int getPredecessorCount() {
        return m_predecessorCount;
    }

    public void setPredecessorCount(int count) {
        m_predecessorCount = count;
    }

}
