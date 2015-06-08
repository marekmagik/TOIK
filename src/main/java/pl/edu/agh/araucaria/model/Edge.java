package pl.edu.agh.araucaria.model;

import java.io.Serializable;

/**
 * Represents an pl.edu.agh.araucaria.model.Edge connecting two pl.edu.agh.araucaria.model.Vertex objects in a pl.edu.agh.araucaria.model.Graph object.
 */
public class Edge implements Serializable {

    private Vertex sourceVertex;

    private Vertex destVertex;

    /**
     * Creates an pl.edu.agh.araucaria.model.Edge leading from sourceVertex to destVertex.
     * The edge has a weight of zero.
     *
     * @param sourceVertex The start of the pl.edu.agh.araucaria.model.Edge.
     * @param destVertex   The end of the pl.edu.agh.araucaria.model.Edge.
     */
    public Edge(Vertex sourceVertex, Vertex destVertex) {
        this.sourceVertex = sourceVertex;
        this.destVertex = destVertex;
    }

    public Vertex getDestVertex() {
        return destVertex;
    }

    public Vertex getSourceVertex() {
        return sourceVertex;
    }
}
