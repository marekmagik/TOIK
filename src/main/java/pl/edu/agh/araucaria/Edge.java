package pl.edu.agh.araucaria;

import java.io.Serializable;

/**
 * Represents an pl.edu.agh.araucaria.Edge connecting two pl.edu.agh.araucaria.Vertex objects in a pl.edu.agh.araucaria.Graph object.
 */
public class Edge implements Serializable {

    private Vertex sourceVertex;

    private Vertex destVertex;

    private double weight;

    private boolean visited;

    /**
     * Creates an pl.edu.agh.araucaria.Edge leading from sourceVertex to destVertex.
     * The edge has a weight of zero.
     *
     * @param sourceVertex The start of the pl.edu.agh.araucaria.Edge.
     * @param destVertex   The end of the pl.edu.agh.araucaria.Edge.
     */
    public Edge(Vertex sourceVertex, Vertex destVertex) {
        this.sourceVertex = sourceVertex;
        this.destVertex = destVertex;
        this.weight = 0.0;
        this.visited = false;
    }

    /**
     * Creates an pl.edu.agh.araucaria.Edge leading from sourceVertex to destVertex.
     * The edge has the weight specified.
     *
     * @param sourceVertex The start of the pl.edu.agh.araucaria.Edge.
     * @param destVertex   The end of the pl.edu.agh.araucaria.Edge.
     * @param weight       The weight of the pl.edu.agh.araucaria.Edge.
     */
    public Edge(Vertex sourceVertex, Vertex destVertex,
                double weight) {
        this(sourceVertex, destVertex);
        this.weight = weight;
    }

    public Vertex getDestVertex() {
        return destVertex;
    }

    public Vertex getSourceVertex() {
        return sourceVertex;
    }

    public double getWeight() {
        return weight;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }
}
