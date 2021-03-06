package pl.edu.agh.araucaria.model;

import pl.edu.agh.araucaria.Araucaria;
import pl.edu.agh.araucaria.exceptions.GraphException;

import java.awt.*;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Extends the pl.edu.agh.araucaria.model.Graph class to construct a tree out of pl.edu.agh.araucaria.model.TreeVertex objects
 */

public class Tree extends Graph
        implements Serializable {
    Araucaria parent = null;

    public Tree() {
    }

    public Tree(Araucaria owner) {
        parent = owner;
    }

    /**
     * Adds a pl.edu.agh.araucaria.model.TreeVertex object to the list of vertices.
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
    public boolean addEdge(TreeVertex source, TreeVertex dest) {
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
    public boolean addEdge(TreeVertex source, TreeVertex dest, double weight) {
        if (!m_vertexList.contains(source) ||
                !m_vertexList.contains(dest))
            return false;
        source.addEdge(dest, m_directed, weight);
        return true;
    }

    /**
     * Searches the tree for an edge from source to dest. Returns
     * the edge if found, null otherwise.
     */
    public TreeEdge getEdge(TreeVertex source, TreeVertex dest) {
        Enumeration edgeList = source.getEdgeList().elements();
        while (edgeList.hasMoreElements()) {
            TreeEdge edge = (TreeEdge) edgeList.nextElement();
            if (edge.getDestVertex() == dest) {
                return edge;
            }
        }
        return null;
    }

    /**
     * Returns a vector of all vertices that have no parent.
     */
    public Vector getRoots() {
        Vector roots = new Vector();
        Enumeration vertices = m_vertexList.elements();
        while (vertices.hasMoreElements()) {
            TreeVertex vertex = (TreeVertex) vertices.nextElement();
            if (!vertex.getHasParent()) {
                roots.add(vertex);
            }
        }
        return roots;
    }

    /**
     * Clears the visited flag on all vertexes in the tree
     */
    public void clearAllVisited() {
        for (int i = 0; i < m_vertexList.size(); i++) {
            TreeVertex vertex = (TreeVertex) m_vertexList.elementAt(i);
            vertex.setVisited(false);
            Enumeration edges = vertex.getEdgeList().elements();
            while (edges.hasMoreElements()) {
                TreeEdge edge = (TreeEdge) edges.nextElement();
                edge.setVisited(false);
            }
        }
    }

    /**
     * Returns dummy root if there is one, else null
     */
    public TreeVertex getDummyRoot() {
        Enumeration vertices = m_vertexList.elements();
        while (vertices.hasMoreElements()) {
            TreeVertex vertex = (TreeVertex) vertices.nextElement();
            if (new String(vertex.getShortLabel()).equals("DummyRoot")) {
                return vertex;
            }
        }
        return null;
    }

    public void clearSchemeLabels() {
        Enumeration vertices = m_vertexList.elements();
        while (vertices.hasMoreElements()) {
            TreeVertex vertex = (TreeVertex) vertices.nextElement();
            vertex.schemeLabels = new Vector();
            vertex.m_schemeColorList = new Vector();
            vertex.schemeLayout = null;
            vertex.schemeLayoutSize = new Dimension(0, 0);
        }
    }

    public void clearVertexList() {
        m_vertexList = new Vector<>();
    }

    /**
     * Returns number of vertices in tree, excluding dummy root and virtual vertices
     */
    public int getVertexCount() {
        int count = 0;
        for (int i = 0; i < m_vertexList.size(); i++) {
            TreeVertex vertex = (TreeVertex) m_vertexList.elementAt(i);
            if (!vertex.isVirtual()) {
                count++;
            }
        }
        if (this.getRoots().size() > 1) {
            count--;
        }
        return count;
    }

    /**
     * Returns number of missing premises
     */
    public int getMissingCount() {
        int count = 0;
        for (int i = 0; i < m_vertexList.size(); i++) {
            TreeVertex vertex = (TreeVertex) m_vertexList.elementAt(i);
            if (vertex.isMissing()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns number of missing premises
     */
    public int getRefutationCount() {
        int count = 0;
        for (int i = 0; i < m_vertexList.size(); i++) {
            TreeVertex vertex = (TreeVertex) m_vertexList.elementAt(i);
            if (vertex.isRefutation()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Finds the depth of the tree.
     * If refutations is true, we count a refutation as on the
     * same level as its parent, so a refutation doesn't count towards the depth.
     * If refutation is false, all nodes count towards the depth.
     */
    public int getDepth(TreeVertex node, boolean refutations) {
        if (node == null)
            return 0;
        int currNode = 0;
        if (!node.isRefutation() || (!refutations))
            currNode = 1;
        int maxDepth = currNode;
        Vector edges = node.getEdgeList();
        for (int i = 0; i < edges.size(); i++) {
            int depth = currNode + getDepth(((TreeEdge) edges.elementAt(i)).getDestVertex(), refutations);
            if (depth > maxDepth)
                maxDepth = depth;
        }
        return maxDepth;
    }

    /**
     * Breadth-first traversal.
     * Finds the root of the tree and starts traversal from there.
     *
     * @return a Vector containing the traversal as a list of pl.edu.agh.araucaria.model.Vertex objects
     * @throws GraphException if the starting vertex is not found
     *                        in the graph.
     */
    public Vector breadthFirstTraversal() throws GraphException {
        if (getRoots().size() == 0) {
            return null;
        }
        TreeVertex root = (TreeVertex) getRoots().elementAt(0);
        return breadthFirstTraversal(root);
    }

    /**
     * Breadth-first traversal.
     *
     * @param start The starting vertex for the traversal.
     * @return a Vector containing the traversal as a list of pl.edu.agh.araucaria.model.Vertex objects
     * @throws GraphException if the starting vertex is not found
     *                        in the graph.
     */
    public Vector breadthFirstTraversal(TreeVertex start) throws GraphException {
        if (!m_vertexList.contains(start)) {
            throw new GraphException(
                    "pl.edu.agh.araucaria.model.Tree.breadthFirstTraversal: Starting vertex not found in graph: " + start.getShortLabelString());
        }
        int startIndex = m_vertexList.indexOf(start);
        m_breadthFirstTraversal = new Vector(5, 5);
        start.setLayer(0);
        start.setParent(null);

        Enumeration vertexList = prepareVertexList(start);
        TreeVertex parent;
        Vector vertexQueue = new Vector(5, 5);
        while (vertexList.hasMoreElements()) {
            TreeVertex nextVertex = (TreeVertex) vertexList.nextElement();
            if (!nextVertex.getVisited()) {
                vertexQueue.add(nextVertex);
                nextVertex.setVisited(true);
            }
            while (vertexQueue.size() > 0) {
                TreeVertex addVertex = (TreeVertex) vertexQueue.remove(0);
                m_breadthFirstTraversal.add(addVertex);
                Enumeration edgeList = addVertex.getEdgeList().elements();
                parent = addVertex;
                TreeVertex prevSibling = null;
                while (edgeList.hasMoreElements()) {
                    TreeVertex destVertex = ((TreeEdge) edgeList.nextElement()).
                            getDestVertex();
                    if (!destVertex.getVisited()) {
                        vertexQueue.add(destVertex);
                        destVertex.setVisited(true);
                        destVertex.setParent(parent);
                        destVertex.setLayer(parent.getLayer() + 1);
                        // Construct sibling list
                        if (prevSibling != null) {
                            prevSibling.setSibling(destVertex);
                        }
                        prevSibling = destVertex;
                    }
                }
            }
        }
        restoreVertexList(startIndex, start);
        return m_breadthFirstTraversal;
    }

    /**
     * Breadth-first topological sort.
     *
     * @param start The starting vertex for the sort.
     * @return a Vector containing the sorted list of pl.edu.agh.araucaria.model.Vertex objects.
     * @throws GraphException if the graph is undirected.
     * @throws GraphException if the starting vertex is not found.
     * @throws GraphException if the graph contains a cycle.
     */
    public Vector breadthFirstTopSort(TreeVertex start) throws GraphException {
        Enumeration edgeList;
        TreeVertex vertex;

        // Topological sorts only apply to directed graphs
        if (!m_directed) {
            throw new GraphException(
                    "pl.edu.agh.araucaria.model.Graph is undirected - topological sort applies only to directed graphs.");
        }
        if (!m_vertexList.contains(start)) {
            throw new GraphException("Starting vertex not found.");
        }
        m_breadthFirstTopSort = new Vector(5, 5);
        Vector vertexQueue = new Vector(5, 5);

        Enumeration vertexList = prepareVertexList(start);
        while (vertexList.hasMoreElements()) {
            vertex = (TreeVertex) vertexList.nextElement();
            vertex.setPredecessorCount(0);
        }
        // Initialize predecessor counts
        vertexList = m_vertexList.elements();
        while (vertexList.hasMoreElements()) {
            vertex = (TreeVertex) vertexList.nextElement();
            edgeList = vertex.getEdgeList().elements();
            while (edgeList.hasMoreElements()) {
                TreeEdge edge = (TreeEdge) edgeList.nextElement();
                edge.getDestVertex().changePredecessorCount(1);
            }
        }
        // Initialize queue
        vertexList = m_vertexList.elements();
        while (vertexList.hasMoreElements()) {
            vertex = (TreeVertex) vertexList.nextElement();
            if (vertex.getPredecessorCount() == 0) {
                vertexQueue.add(vertex);
            }
        }
        // No source vertices - graph has cycle
        if (vertexQueue.size() == 0)
            throw new GraphException("pl.edu.agh.araucaria.model.Graph has a cycle.");

        while (vertexQueue.size() > 0) {
            // Add first queue element to top sort
            vertex = (TreeVertex) vertexQueue.remove(0);
            m_breadthFirstTopSort.add(vertex);
            // Adjust predecessor counts
            edgeList = vertex.getEdgeList().elements();
            while (edgeList.hasMoreElements()) {
                TreeVertex destVertex = ((TreeEdge) edgeList.nextElement()).getDestVertex();
                destVertex.changePredecessorCount(-1);
                if (destVertex.getPredecessorCount() == 0) {
                    vertexQueue.add(destVertex);
                }
            }
            // If queue is empty & not all vertices added
            // to sort, graph has a cycle so give up
            if (vertexQueue.size() == 0 && m_breadthFirstTopSort.size() <
                    m_vertexList.size()) {
                throw new GraphException("pl.edu.agh.araucaria.model.Graph has a cycle.");
            }
        }
        return m_breadthFirstTopSort;
    }

    /*
     * Swaps the start vertex to the beginning of the vertexList.
     * Used in traversal algorithms where the start vertex is not
     * the first vertex in the vertex list.
     * @return an Enumeration of the vertex list Vector.
     */
    protected Enumeration prepareVertexList(TreeVertex start) {
        m_vertexList.remove(start);
        m_vertexList.add(0, start);
        Enumeration vertexList = m_vertexList.elements();
        while (vertexList.hasMoreElements()) {
            TreeVertex vertex = (TreeVertex) vertexList.nextElement();
            vertex.setVisited(false);
            vertex.setSibling(null);
        }
        vertexList = m_vertexList.elements();
        return vertexList;
    }

    /*
     * Restores the tree's vertex order by swapping the start vertex back to its original location in the vertex list.
     */
    protected void restoreVertexList(int startIndex, TreeVertex start) {
        m_vertexList.remove(start);
        m_vertexList.add(startIndex, start);
    }

    /**
     * Searches the tree
     * for a vertex with a given ID label.
     * Returns the vertex if found, null otherwise
     */
    public TreeVertex containsVertexID(String id) {
        Enumeration vertexList = m_vertexList.elements();
        while (vertexList.hasMoreElements()) {
            TreeVertex vertex = (TreeVertex) vertexList.nextElement();
            if (vertex.getShortLabelString() != null && vertex.getShortLabelString().equals(id)) {
                return vertex;
            }
        }
        return null;
    }

    /**
     * Tests if patternRoot and its subtree can be matched to a subtree
     * with targetRoot as the root node.
     */
    private boolean candidate(TreeVertex pattern, TreeVertex target) {
        TreeVertex targetfirst;

        Candidate CandList = new Candidate();
        Candidate Current = CandList;
        Current.NextTargetCandidate = null;
        Candidate CurrentPattern;
        Candidate TargetCandidate;

        boolean result;
        // Both nodes must be the same type with regards to being virtual or not.
        if (pattern.isVirtual() != target.isVirtual())
            return false;

        // Both nodes must be the same type with regards to being a refutation.
        if (pattern.isRefutation() != target.isRefutation())
            return false;

        // If pattern is a leaf, we have a match automatically
        if (pattern.getFirstChild() == null)
            return true;

        // If we get this far, pattern is NOT a leaf, so target must have a child
        if (target.getFirstChild() == null)
            return false;

        // If target has fewer children than pattern, no match possible
        if (target.getNumberOfChildren() < pattern.getNumberOfChildren())
            return false;

        CurrentPattern = Current;
        targetfirst = target.getFirstChild();
        pattern = pattern.getFirstChild();
        do {
            //Add on a new pattern (this means 0th is empty)
            CurrentPattern.NextPatternNode = new Candidate();
            CurrentPattern = CurrentPattern.NextPatternNode;
            CurrentPattern.NextTargetCandidate = null;
            CurrentPattern.NextPatternNode = null;
            //make a note of the 0th target node
            target = targetfirst;

            //and the pattern we're on
            Current = CurrentPattern;

            //init pattern candidate structure
            CurrentPattern.i = 0;
            CurrentPattern.node = pattern;
            // foreach y'
            do {
                result = candidate(pattern, target);
                if (result) {
                    //success, so put target as a candidate for pattern
                    Current.NextTargetCandidate = new Candidate();
                    Current = Current.NextTargetCandidate;
                    Current.NextPatternNode = null;
                    Current.NextTargetCandidate = null;
                    Current.node = target;
                    CurrentPattern.i++;
                }
                //check next target for candidacy
                target = target.getSibling();
            }
            while (target != null);

            // if no candidates, return fail
            if (CurrentPattern.i == 0)
                return (false);

            //work on next pattern
            pattern = pattern.getSibling();

        }
        while (pattern != null);

        //now solve the tough bit: which candidates should we go for?
        //first, brute force
        //try binding each t cand in turn, and seeing if it 'works'
        CurrentPattern = CandList.NextPatternNode;
        TargetCandidate = CurrentPattern.NextTargetCandidate;
        result = false;
        while (TargetCandidate != null) {
            CurrentPattern.Bound = TargetCandidate;
            result = works(CandList, CurrentPattern.NextPatternNode);
            if (result)
                break;
            TargetCandidate = TargetCandidate.NextTargetCandidate;
        }

        return (result);
    }

    boolean works(Candidate List, Candidate Remaining) {
        Candidate CurrentPattern, CheckPattern, TargetCandidate;
        boolean result;

        if (Remaining == null)
            return (true);

        CurrentPattern = Remaining;
        TargetCandidate = CurrentPattern.NextTargetCandidate;
        while (TargetCandidate != null) {
            result = true;
            CheckPattern = List;
            //first, next target mustn't already be bound
            while ((CheckPattern.NextPatternNode != null) &&
                    (CheckPattern.NextPatternNode.Bound != null)) {
                CheckPattern = CheckPattern.NextPatternNode;
                if (TargetCandidate.node == CheckPattern.Bound.node)
                    result = false;
            }

            if (result) {
                //second, next target must work with later stuff
                CurrentPattern.Bound = TargetCandidate;
                result = works(List, CurrentPattern.NextPatternNode);
                if (result)
                    return (true);
            }

            //try next target candidate
            TargetCandidate = TargetCandidate.NextTargetCandidate;
        }
        return false;
    }

    /**
     * Tests if 'pattern' occurs as a subtree with the current (target) tree.
     * Node labels are ignored in searching for the match - only the
     * tree structure is considered.
     */
    private boolean recursiveMatch(TreeVertex patternRoot, TreeVertex targetRoot) {
        boolean matched;
        // Try matching patternRoot with targetRoot
        matched = candidate(patternRoot, targetRoot);
        // If no match, try matching patternRoot with the sibling of targetRoot
        if (!matched) {
            if (targetRoot.getSibling() != null) {
                matched = recursiveMatch(patternRoot, targetRoot.getSibling());
            }

            // If the sibling match didn't work, go to the first child of targetRoot.
            if (!matched && targetRoot.getFirstChild() != null) {
                matched = recursiveMatch(patternRoot, targetRoot.getFirstChild());
            }
        }
        return matched;
    }

    public boolean matchSubtree(Tree pattern) {
        Vector targetRoots = this.getRoots();
        TreeVertex targetRoot = (TreeVertex) targetRoots.elementAt(0);
        // Do breadth first traversals to set up sibling lists
        this.breadthFirstTraversal(targetRoot);

        Vector patternRoots = pattern.getRoots();
        TreeVertex patternRoot = (TreeVertex) patternRoots.elementAt(0);
        pattern.breadthFirstTraversal(patternRoot);

        return recursiveMatch(patternRoot, targetRoot);
    }

    /**
     * Local class used in finding subtree matches.
     */
    class Candidate {
        int i;
        TreeVertex node;
        Candidate NextTargetCandidate;
        Candidate NextPatternNode;
        Candidate Bound;
    }
}
