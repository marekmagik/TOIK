package pl.edu.agh.araucaria;

import pl.edu.agh.araucaria.exceptions.GraphException;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Vector;

/**
 * The pl.edu.agh.araucaria.Graph class represents a graph data structure.
 * To work with the graph package, you must first create a pl.edu.agh.araucaria.Graph object.
 * This can be done by calling the argumentless constructor, which creates
 * an empty graph that is assumed to be directed an unweighted (once
 * some vertices and edges are added): <br> <br>
 * <tt> pl.edu.agh.araucaria.Graph myGraph = new pl.edu.agh.araucaria.Graph(); </tt>
 * <br><br>
 * Alternatively, the 'directed' and 'weighted' nature of the graph may
 * be specified in the two-argument constructor.
 * <p>Once the pl.edu.agh.araucaria.Graph object has been created, you must add pl.edu.agh.araucaria.Vertex objects
 * to it. Create the pl.edu.agh.araucaria.Vertex objects (see pl.edu.agh.araucaria.Vertex class docs for details),
 * and use the addVertex() method to add a pl.edu.agh.araucaria.Vertex to the pl.edu.agh.araucaria.Graph.</p>
 * <p>To add an pl.edu.agh.araucaria.Edge between two pl.edu.agh.araucaria.Vertex objects, you should use one of
 * the addEdge() methods. One allows you to add an unweighted edge between
 * two vertices, while the other allows you to specify a weight.</p>
 * <p>Once you have built up the graph by adding pl.edu.agh.araucaria.Vertex and pl.edu.agh.araucaria.Edge objects,
 * you may call the other methods in this class to run some of the algorithms
 * such as traversal, minimum cost spanning tree, and so on.
 */
public class Graph implements Serializable {
    public static int DORMANT = 3;

    protected Vector m_vertexList;
    protected Vector m_depthFirstTraversal;
    protected Vector m_breadthFirstTraversal;
    protected Vector m_breadthFirstTopSort;
    protected int m_state;
    protected boolean m_directed;    // Is the graph directed or undirected?
    protected boolean m_weighted;    // Do the edges have weights or costs?
    protected boolean m_isModel;     // True if this graph is in the model set

    // A graph in the decomposition can be mapped to one or more subgraphs
    // of the target graph. We store these maps in another Vector.
    protected Vector m_targetMapList;

    /**
     * Creates an empty, directed, unweighted graph.
     */
    public Graph() {
        m_vertexList = new Vector(10, 10);
        m_depthFirstTraversal = new Vector(10, 10);
        m_targetMapList = new Vector(5, 5);
        m_state = DORMANT;
        m_directed = true;
        m_weighted = false;
        m_isModel = false;
    }

    /**
     * Creates an empty graph.
     *
     * @param directed Specifies if the graph is directed or undirected.
     * @param weighted Specifies if the edges in the graph have weights
     */
    public Graph(boolean directed, boolean weighted) {
        this();
        m_directed = directed;
        m_weighted = weighted;
    }


    /**
     * Returns the list of vertices as a Vector of pl.edu.agh.araucaria.Vertex objects.
     */
    public Vector getVertexList() {
        return m_vertexList;
    }

    /**
     * Adds a pl.edu.agh.araucaria.Vertex object to the list of vertices.
     */
    public void addVertex(Vertex newVertex) {
        m_vertexList.add(newVertex);
    }

    /**
     * Adds an edge from the source vertex to the dest vertex.
     * Tests that both vertices are part of the graph.
     * If the graph is undirected, an edge from dest to source
     * is also added.
     *
     * @return false if either vertex is not in the graph,
     * true otherwise
     */
    public boolean addEdge(Vertex source, Vertex dest) {
        if (!m_vertexList.contains(source) ||
                !m_vertexList.contains(dest))
            return false;
        source.addEdge(dest, m_directed);
        return true;
    }

    /**
     * Adds an edge with the specified weight from the source vertex to the dest vertex.
     * Tests that both vertices are part of the graph.
     * If the graph is undirected, an identical edge from dest to source
     * is also added.
     *
     * @return false if either vertex is not in the graph,
     * true otherwise
     */
    public boolean addEdge(Vertex source, Vertex dest, double weight) {
        if (!m_vertexList.contains(source) ||
                !m_vertexList.contains(dest))
            return false;
        source.addEdge(dest, m_directed, weight);
        return true;
    }

    protected void recursiveDFT(Vertex start) {
        if (start.getVisited()) return;
        m_depthFirstTraversal.add(start);
        start.setVisited(true);
        Enumeration edgeList = start.getEdgeList().elements();
        while (edgeList.hasMoreElements()) {
            Vertex nextVertex = ((Edge) edgeList.nextElement()).getDestVertex();
            recursiveDFT(nextVertex);
        }
    }

    /*
     * Swaps the start vertex to the beginning of the vertexList.
     * Used in traversal algorithms where the start vertex is not
     * the first vertex in the vertex list.
     * @return an Enumeration of the vertex list Vector.
     */
    protected Enumeration prepareVertexList(Vertex start) {
        m_vertexList.remove(start);
        m_vertexList.add(0, start);
        Enumeration vertexList = m_vertexList.elements();
        while (vertexList.hasMoreElements()) {
            ((Vertex) vertexList.nextElement()).setVisited(false);
        }
        vertexList = m_vertexList.elements();
        return vertexList;
    }

    /*
     * Restores the graph's vertex order by swapping the start vertex back to its original location in the vertex list.
     */
    protected void restoreVertexList(int startIndex, Vertex start) {
        m_vertexList.remove(start);
        m_vertexList.add(startIndex, start);
    }

    /**
     * Calculates the depth-first traversal.
     *
     * @param start The starting vertex for the traversal.
     * @return a Vector containing the traversal as a list of pl.edu.agh.araucaria.Vertex objects.
     * @throws GraphException if the starting vertex is not found in the graph.
     */
    public Vector depthFirstTraversal(Vertex start) throws GraphException {
        if (!m_vertexList.contains(start)) {
            throw new GraphException("Starting vertex not found in graph.");
        }
        int startIndex = m_vertexList.indexOf(start);
        m_depthFirstTraversal = new Vector(5, 5);

        Enumeration vertexList = prepareVertexList(start);
        while (vertexList.hasMoreElements()) {
            recursiveDFT((Vertex) vertexList.nextElement());
        }
        restoreVertexList(startIndex, start);
        return m_depthFirstTraversal;
    }

}
