package pl.edu.agh.araucaria.model;

import java.io.Serializable;
import java.util.Vector;

/**
 * The pl.edu.agh.araucaria.model.Graph class represents a graph data structure.
 * To work with the graph package, you must first create a pl.edu.agh.araucaria.model.Graph object.
 * This can be done by calling the argumentless constructor, which creates
 * an empty graph that is assumed to be directed an unweighted (once
 * some vertices and edges are added): <br> <br>
 * <tt> pl.edu.agh.araucaria.model.Graph myGraph = new pl.edu.agh.araucaria.model.Graph(); </tt>
 * <br><br>
 * Alternatively, the 'directed' and 'weighted' nature of the graph may
 * be specified in the two-argument constructor.
 * <p>Once the pl.edu.agh.araucaria.model.Graph object has been created, you must add pl.edu.agh.araucaria.model.Vertex objects
 * to it. Create the pl.edu.agh.araucaria.model.Vertex objects (see pl.edu.agh.araucaria.model.Vertex class docs for details),
 * and use the addVertex() method to add a pl.edu.agh.araucaria.model.Vertex to the pl.edu.agh.araucaria.model.Graph.</p>
 * <p>To add an pl.edu.agh.araucaria.model.Edge between two pl.edu.agh.araucaria.model.Vertex objects, you should use one of
 * the addEdge() methods. One allows you to add an unweighted edge between
 * two vertices, while the other allows you to specify a weight.</p>
 * <p>Once you have built up the graph by adding pl.edu.agh.araucaria.model.Vertex and pl.edu.agh.araucaria.model.Edge objects,
 * you may call the other methods in this class to run some of the algorithms
 * such as traversal, minimum cost spanning tree, and so on.
 */
public class Graph implements Serializable {
    public static int DORMANT = 3;

    protected Vector<TreeVertex> m_vertexList;
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
        m_vertexList = new Vector<>(10, 10);
        m_depthFirstTraversal = new Vector(10, 10);
        m_targetMapList = new Vector(5, 5);
        m_state = DORMANT;
        m_directed = true;
        m_weighted = false;
        m_isModel = false;
    }


    /**
     * Returns the list of vertices as a Vector of pl.edu.agh.araucaria.model.Vertex objects.
     */
    public Vector getVertexList() {
        return m_vertexList;
    }

    /**
     * Adds a pl.edu.agh.araucaria.model.Vertex object to the list of vertices.
     */
    public void addVertex(TreeVertex newVertex) {
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

}
