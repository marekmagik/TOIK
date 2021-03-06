package pl.edu.agh.araucaria.gui.visualisation.standard;/*
 * pl.edu.agh.araucaria.gui.visualisation.standard.FullPanel.java
 *
 * Created on 19 March 2004, 12:11
 */

/**
 * Draws the original main.java.Araucaria tree diagram in which the entire tree is
 * displayed without any scroll bars
 *
 * @author growe
 */

import pl.edu.agh.araucaria.model.TreeEdge;
import pl.edu.agh.araucaria.model.TreeVertex;
import pl.edu.agh.araucaria.history.EditAction;
import pl.edu.agh.araucaria.gui.visualisation.core.DiagramBase;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.Enumeration;
import java.util.Set;
import java.util.Vector;

public class FullPanel extends DiagramBase {
    static final int TOP_OFFSET = 12; // Shifts non-inverted diagram down so tops of schemes are visible
    static final int ARROW_LENGTH = 7;
    static final int ARROW_WIDTH = 5;
    static final int ARROW_OFFSET = NODE_DIAM / 2;

    Vector<AttributedString> ownerText, supportLabelText;
    int ownerTextX, ownerTextY;
    int supportLabelTextX, supportLabelTextY;

    /**
     * Creates new form pl.edu.agh.araucaria.gui.visualisation.standard.FullPanel
     */
    public FullPanel() {
        setLayout(new BorderLayout());
    }

    /**
     * Sets the fill, outline and text paints for a given vertex.
     * This method is part of the diagram class rather than the pl.edu.agh.araucaria.model.TreeVertex
     * class since the colour scheme depends on the diagramming method.
     *
     * @param vertex The vertex whose colours are to be set.
     */
    public void setVertexColours(TreeVertex vertex) {
        vertex.outlinePaint = Color.black;
        vertex.textPaint = Color.black;
        if (vertex.isMissing()) {
            vertex.outlinePaint = missingBorderColor;
            vertex.textPaint = missingBorderColor;
            if (vertex.isRefutation()) {
                vertex.fillPaint = new GradientPaint(0, 0, refutationColor,
                        NODE_DIAM / 8, NODE_DIAM / 8, missingRefutGradientColor, true);
            } else {
                vertex.fillPaint = missingColor;
            }
        } else if (vertex.isRefutation()) {
            vertex.fillPaint = refutationColor;
        } else if (vertex.isVirtual()) {
            vertex.fillPaint = null;
            vertex.outlinePaint = null;
            vertex.textPaint = null;
        } else // Normal vertex
        {
            vertex.fillPaint = nodeFillColor;
        }
    }

    public float getSubtreeLineWidth() {
        return 50.0f;
    }

    public void newCalcNodeCoords(TreeVertex root, int width,
                                  int leftWidth, int canvasHeight, int height,
                                  int vertSpacing, int nodeDiam, boolean invertedTree) {
        setVertexColours(root);
        // Set root node's position and width
        Vector<TreeVertex> refutationList = new Vector<>();
        // If the vertex's children aren't being hidden, add in refutations
        if (!root.hidingChildren) {
            root.getRefutationList(refutationList);
        }

        // Add the vertex itself to the refutation list
        refutationList.add(root);
        int numRefutations = refutationList.size();

        // Work out x positions for root + its refutations (height - 2*nodeDiam) - y)
        int spacing = width / numRefutations;
        int xMin = leftWidth, xMax = leftWidth + spacing;
        for (int i = 0; i < refutationList.size(); i++) {
            TreeVertex vertex = refutationList.elementAt(i);
            vertex.setExtent(xMin, xMax);
            if (invertedTree) {
                vertex.setDrawPoint((xMin + xMax) / 2, canvasHeight - 2 * nodeDiam - height);
            } else {
                vertex.setDrawPoint((xMin + xMax) / 2, height + TOP_OFFSET);
            }
            xMin += spacing;
            xMax += spacing;
        }

        // Do recursion to work out lower layers
        if (!root.hidingChildren) {
            for (int i = 0; i < refutationList.size(); i++) {
                TreeVertex vertex = refutationList.elementAt(i);
                setVertexColours(vertex);
                xMin = vertex.getXMin();
                xMax = vertex.getXMax();
                int numNonRefutationChildren = vertex.getNumberOfChildren() - vertex.getNumRefutations();
                if (numNonRefutationChildren == 0) {
                    continue;
                }
                int childWidth = (xMax - xMin) / numNonRefutationChildren;
                Vector childEdges = vertex.getEdgeList();
                int nonRefutCount = 0;
                for (int j = 0; j < childEdges.size(); j++) {
                    TreeEdge edge = (TreeEdge) childEdges.elementAt(j);
                    TreeVertex child = edge.getDestVertex();
                    if (!child.isRefutation()) {
                        newCalcNodeCoords(child, childWidth, xMin + childWidth * nonRefutCount,
                                canvasHeight, height + vertSpacing, vertSpacing, nodeDiam, invertedTree);
                        nonRefutCount++;
                    }
                }
            }
        }

        // All nodes now have positions calculated, so calculate edges
        Vector vertexList = argument.getTree().getVertexList();
        for (int i = 0; i < vertexList.size(); i++) {
            TreeVertex child = (TreeVertex) vertexList.elementAt(i);
            if (!child.visible) {
                continue;
            }
            TreeVertex parent = child.getParent();
            if (parent != null) {
                Vector edges = parent.getEdgeList();
                for (int j = 0; j < edges.size(); j++) {
                    TreeEdge parentEdge = (TreeEdge) edges.elementAt(j);
                    if (parent.isVirtual()) {
                        calcVirtualEdge(parentEdge, nodeDiam);
                    } else {
                        calcStraightEdge(parentEdge, nodeDiam);
                    }
                }
            }
        }
    }

    /**
     * Expects a breadth-first traversal to have been done first.
     * Calculates the plotting positions for the various nodes in the tree.
     */

    public void calcNodeCoords(boolean invertedTree) {
        // Set root node's position and width
        Vector bft = argument.getBreadthFirstTraversal();
        if (bft == null) {
            return;
        }
        TreeVertex root = (TreeVertex) bft.firstElement();
        int depth = argument.getTree().getDepth(root, true);
        int vertSpacing = getHeight() / depth;
        newCalcNodeCoords(root, getWidth(), 0, getHeight(), 0, vertSpacing, NODE_DIAM, invertedTree);
    }

    // Calculates the bent line used to draw a virtual edge
    // rather than the straight edge between normal vertices
    public void calcVirtualEdge(TreeEdge edge, int nodeDiam) {
        try {
            Point parentCorner = edge.getSourceVertex().getDrawPoint();
            Point corner = edge.getDestVertex().getDrawPoint();
            GeneralPath bentLine = new GeneralPath();
            Shape lineE = new Line2D.Float(parentCorner.x + nodeDiam / 2,
                    parentCorner.y + nodeDiam / 2,
                    corner.x + nodeDiam / 2,
                    parentCorner.y + nodeDiam / 2);
            bentLine.append(lineE, true);
            lineE = new Line2D.Float(corner.x + nodeDiam / 2,
                    parentCorner.y + nodeDiam / 2,
                    corner.x + nodeDiam / 2,
                    corner.y + nodeDiam / 2);
            bentLine.append(lineE, true);
            edge.setShape(bentLine, this);
            edge.setSchemeShape(bentLine, this);
        } catch (Exception ignored) {
        }
    }


    // Calculates the straight edge between normal vertices
    public void calcStraightEdge(TreeEdge edge, int nodeDiam) {
        try {
            Point parentCorner = edge.getSourceVertex().getDrawPoint();
            Point corner = edge.getDestVertex().getDrawPoint();
            Shape lineE = new Line2D.Float(parentCorner.x + nodeDiam / 2,
                    parentCorner.y + nodeDiam / 2,
                    corner.x + nodeDiam / 2,
                    corner.y + nodeDiam / 2);
            GeneralPath linePath = new GeneralPath(lineE);
            // Set the scheme shape to be the line without the arrowhead
            edge.setSchemeShape((GeneralPath) linePath.clone(), this);
            // Add the arrowhead
            linePath.append(addArrowHead(parentCorner.x + nodeDiam / 2,
                    parentCorner.y + nodeDiam / 2,
                    corner.x + nodeDiam / 2,
                    corner.y + nodeDiam / 2, ARROW_OFFSET,
                    ARROW_WIDTH, ARROW_LENGTH), false);
            // Refutation edges have arrows in both directions
            if (edge.getDestVertex().isRefutation()) {
                linePath.append(addArrowHead(corner.x + nodeDiam / 2,
                        corner.y + nodeDiam / 2,
                        parentCorner.x + nodeDiam / 2,
                        parentCorner.y + nodeDiam / 2, ARROW_OFFSET,
                        ARROW_WIDTH, ARROW_LENGTH), false);
            }
            edge.setShape(linePath, this);
        } catch (Exception ignored) {
        }
    }

    /**
     * Draw vertices on top of the edge structure
     */
    public void drawNodes(Graphics2D gg) {
        Vector bft = argument.getBreadthFirstTraversal();
        if (bft == null) {
            return;
        }
        Enumeration nodeList = bft.elements();
        // Run through the traversal and draw each vertex
        // using an Ellipse2D
        // The draw point has been determined previously in
        // calcNodeCoords()
        while (nodeList.hasMoreElements()) {
            TreeVertex vertex = (TreeVertex) nodeList.nextElement();
            // Don't draw virtual or hidden nodes
            if (vertex.isVirtual() || !vertex.visible)
                continue;

            // If tree is incomplete and we're on the top layer, skip it
            if (argument.isMultiRoots() && vertex.getLayer() == 0)
                continue;

            Point corner = vertex.getDrawPoint();
            Shape node = new Ellipse2D.Float(corner.x, corner.y,
                    NODE_DIAM, NODE_DIAM);
            vertex.setShape(node, this);

            // Fill the interior of the node with vertex's fillPaint
            gg.setPaint(vertex.fillPaint);
            gg.fill(node);

            // Draw the outline with vertex's outlinePaint; bold if selected
            gg.setPaint(vertex.outlinePaint);
            if (vertex.isSelected()) {
                if (vertex.isMissing()) {
                    gg.setStroke(selectDashStroke);
                } else {
                    gg.setStroke(selectStroke);
                }
            } else {
                if (vertex.isMissing()) {
                    gg.setStroke(dashStroke);
                } else {
                    gg.setStroke(solidStroke);
                }
            }
            gg.draw(node);

            // Draw the short label on top of the vertex
            gg.setPaint(vertex.textPaint);
            String shortLabelString = new String(vertex.getShortLabel());
            if (shortLabelString.length() == 1) {
                gg.setFont(labelFont1);
                gg.drawString(shortLabelString,
                        corner.x + NODE_DIAM / 4, corner.y + 3 * NODE_DIAM / 4);
            } else if (shortLabelString.length() == 2) {
                gg.setFont(labelFont2);
                gg.drawString(shortLabelString,
                        corner.x + NODE_DIAM / 5, corner.y + 3 * NODE_DIAM / 4);
            } else if (shortLabelString.length() > 2) {
                gg.setFont(labelFont3);
                gg.drawString(shortLabelString,
                        corner.x + NODE_DIAM / 5, corner.y + 5 * NODE_DIAM / 8);
            }
            // Draw a symbol if the node is hiding its premises
            if (vertex.hidingChildren) {
                gg.setFont(labelFont2);
                gg.setPaint(Color.BLUE);
                gg.drawString("+",
                        corner.x + 7 * NODE_DIAM / 16, corner.y + 6 * NODE_DIAM / 4);
            }

            // Draw owners if requested
            gg.setPaint(vertex.textPaint);
            if (argument.isShowOwners()) {
                int ownerWidth = createOwnerText(vertex.getOwners());
                if (ownerWidth > 0) {
                    setOwnerTextPos(ownerWidth, corner.x + NODE_DIAM, corner.y);
                    drawOwnerText(gg);
                }
            }

            // Draw support labels if requested
            if (argument.isShowSupportLabels() && vertex.m_nodeLabel != null) {
                int nodeLabelWidth =
                        createSupportLabelText(vertex.m_nodeLabel, nodeLabelColor);
                if (nodeLabelWidth > 0) {
                    setNodeLabelTextPos(nodeLabelWidth, vertex);
                    drawSupportLabelText(gg);
                }
            }
        }
    }

    public int createSupportLabelText(String supportLabel, Color backColor) {
        Font font = new Font("SansSerif", Font.PLAIN, 11);
        int maxWidth = 0;
        supportLabelText = new Vector<>();
        AttributedString ownerAttrib = new AttributedString(supportLabel);
        ownerAttrib.addAttribute(TextAttribute.FONT, font);
        ownerAttrib.addAttribute(TextAttribute.BACKGROUND, backColor);
        supportLabelText.add(ownerAttrib);
        Rectangle2D bounds = font.getStringBounds(supportLabel, new FontRenderContext(null, false, false));
        if ((int) bounds.getWidth() > maxWidth)
            maxWidth = (int) bounds.getWidth();
        return maxWidth;
    }

    /**
     * Sets upper left position for a node label. To avoid clashing with
     * owners, node labels go on the left side of a node.
     */
    public void setNodeLabelTextPos(int width, TreeVertex vertex) {
        Point point1 = vertex.getDrawPoint();
        Point labelPoint = new Point(point1.x - width,
                point1.y + NODE_DIAM / 2);
        if (labelPoint.x - 3 < 0)
            supportLabelTextX = 3;
        else
            supportLabelTextX = labelPoint.x;
        if (labelPoint.y - 12 < 0)
            supportLabelTextY = 12;
        else
            supportLabelTextY = labelPoint.y;
    }

    /**
     * Draws the edge structure of the tree
     */
    public void drawEdges(Graphics2D gg) {
        gg.setPaint(Color.black);
        Vector bft = argument.getBreadthFirstTraversal();
        if (bft == null) {
            return;
        }
        Enumeration nodeList = bft.elements();
        // For each vertex...
        while (nodeList.hasMoreElements()) {
            // Get its edge list...
            TreeVertex vertex = (TreeVertex) nodeList.nextElement();
            Enumeration edges = vertex.getEdgeList().elements();
            // For each edge in the list...
            while (edges.hasMoreElements()) {
                TreeEdge edge = (TreeEdge) edges.nextElement();
                if (!edge.visible) {
                    continue;
                }
                // If we have several vertices on layer 0, only draw
                // edges for layers below that
                if (!(argument.isMultiRoots() && vertex.getLayer() == 0)) {
                    // If the edge has been selected with the mouse,
                    // use a thick line
                    if (edge.isSelected()) {
                        gg.setStroke(selectStroke);
                    }
                    gg.draw(edge.getShape(this));
                    // If we used a thick line, reset the stroke to normal
                    // line for next edge.
                    if (edge.isSelected()) {
                        gg.setStroke(solidStroke);
                    }
                    TreeVertex edgeSource = edge.getDestVertex();
                    if (argument.isShowSupportLabels()) {
                        if (edgeSource.getSupportLabel() != null) {
                            int supportLabelWidth =
                                    createSupportLabelText(edgeSource.getSupportLabel(), supportLabelColor);
                            if (supportLabelWidth > 0) {
                                setSupportLabelTextPos(supportLabelWidth, vertex, edgeSource);
                                drawSupportLabelText(gg);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Creates the text for a list of owners of a given vertex
     */
    public int createOwnerText(Set ownerSet) {
        Font font = new Font("SansSerif", Font.PLAIN, 11);
        Color backColor = ownersColor;
        int maxWidth = 0;
        ownerText = new Vector<>();
        String ownerString;
        Vector<Vector> rows = new Vector<>();

        for (Object anOwnerSet : ownerSet) {
            Vector owner = (Vector) anOwnerSet;
            rows.add(owner);
        }
        sort(rows);
        for (int i = 0; i < rows.size(); i++) {
            Vector owner = rows.elementAt(i);
            ownerString = (String) owner.elementAt(1);
            if (ownerString.length() == 0)
                continue;
            AttributedString ownerAttrib = new AttributedString(ownerString);
            ownerAttrib.addAttribute(TextAttribute.FONT, font);
            ownerAttrib.addAttribute(TextAttribute.BACKGROUND, backColor);
            ownerText.add(ownerAttrib);
            Rectangle2D bounds = font.getStringBounds(ownerString, new FontRenderContext(null, false, false));
            if ((int) bounds.getWidth() > maxWidth)
                maxWidth = (int) bounds.getWidth();
        }
        return maxWidth;
    }

    /**
     * Sets the upper left position for an owner list on the canvas.
     * If the node is too wide to fit between the given x value and
     * the right-hand edge of the canvas, the x value is shifted over.
     */
    public void setOwnerTextPos(int width, int x, int y) {
        int canvasWidth = this.getWidth();
        if (x + 3 + width > canvasWidth)
            ownerTextX = canvasWidth - width - 10;
        else
            ownerTextX = x + 3;
        if (y - 12 < 0)
            ownerTextY = 12;
        else
            ownerTextY = y;
    }

    /**
     * Sets upper left position for an edge label. To match node labels
     * we put labels on the left.
     */
    public void setSupportLabelTextPos(int width, TreeVertex vertex, TreeVertex edgeSource) {
        Point point1 = vertex.getDrawPoint();
        Point point2 = edgeSource.getDrawPoint();
        Point labelPoint;
        if (vertex.isVirtual()) {
            labelPoint = new Point(point2.x + NODE_DIAM / 2 - width,
                    (point1.y + point2.y + NODE_DIAM) / 2);
        } else if (edgeSource.isRefutation()) {
            labelPoint = new Point((point1.x + point2.x + NODE_DIAM) / 2 - width / 2,
                    (point1.y + point2.y + NODE_DIAM) / 2);
        } else {
            labelPoint = new Point((point1.x + point2.x + NODE_DIAM) / 2 - width,
                    (point1.y + point2.y + NODE_DIAM) / 2);
        }
        if (labelPoint.x - 3 < 0)
            supportLabelTextX = 3;
        else
            supportLabelTextX = labelPoint.x;
        if (labelPoint.y - 12 < 0)
            supportLabelTextY = 12;
        else
            supportLabelTextY = labelPoint.y;
    }

    public void drawSupportLabelText(Graphics2D gg) {
        int y;
        for (int i = 0; i < supportLabelText.size(); i++) {
            AttributedString attrString = supportLabelText.elementAt(i);
            AttributedCharacterIterator iter = attrString.getIterator();
            FontMetrics fontMetrics = gg.getFontMetrics();
            y = supportLabelTextY + i * (fontMetrics.getAscent());
            gg.drawString(iter, supportLabelTextX, y);
        }
    }

    public void drawOwnerText(Graphics2D gg) {
        int y;
        gg.setFont(labelFont1);
        for (int i = 0; i < ownerText.size(); i++) {
            AttributedString attrString = ownerText.elementAt(i);
            AttributedCharacterIterator iter = attrString.getIterator();
            FontMetrics fontMetrics = gg.getFontMetrics();
            y = ownerTextY + i * (fontMetrics.getAscent());
            gg.drawString(iter, ownerTextX, y);
        }
    }

    public void paint(java.awt.Graphics g) {
        super.paintComponent(g);
        Graphics2D gg = (Graphics2D) g;
        gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gg.setPaint(getDiagramBackground());
        gg.fill(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
        if (argument != null && argument.getBreadthFirstTraversal() != null) {
            redrawTree(false);
            drawSubtrees(gg);
            drawEdges(gg);
            drawNodes(gg);
            if (displayText) {
                drawText(gg);
            }
        }
    }

    public BufferedImage getJpegImage() {
        Dimension canvasSize = this.getSize();
        BufferedImage image;
        try {
            image = new BufferedImage((int) getPreferredSize().getWidth(),
                    (int) getPreferredSize().getHeight(), BufferedImage.TYPE_INT_RGB);
        } catch (IllegalArgumentException ex) {
            image = new BufferedImage(getDisplayFrame().getMainDiagramPanel().getWidth(),
                    getDisplayFrame().getMainDiagramPanel().getHeight(),
                    BufferedImage.TYPE_INT_RGB);
        }
        //System.out.println("pl.edu.agh.araucaria.gui.visualisation.standard.FullPanel size: " + diagramSize);
        Graphics2D gg = image.createGraphics();
        gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (argument != null && argument.getBreadthFirstTraversal() != null) {
            gg.setPaint(Color.white);
            gg.fillRect(0, 0, canvasSize.width, canvasSize.height);
            drawSubtrees(gg);
            drawEdges(gg);
            drawNodes(gg);
            return image;
        }
        return null;
    }

    public void displayEdgeText(TreeEdge edge) {
        displayText = true;
        TreeVertex child = edge.getDestVertex();
        TreeVertex vertex = edge.getSourceVertex();
        String supports = vertex.getLabel().toString();
        String labelText = child.getLabel().toString();
        // If vertex is virtual, print its parent.
        // If child is virtual, it is the linking vertex for a number of
        // linked arguments, so print the conclusion only.
        if (child.isVirtual()) {
            labelText = "(linked premises)";
        }
        if (vertex.isVirtual())
            supports = vertex.getParent().getLabel().toString();
        String postitString = prepareMessageLabel(labelText,
                supports, MAX_POSTIT_SIZE);
        setTextPos(createText(postitString), mouseX, mouseY);
        repaint();
    }

    public void redrawTree(boolean doRepaint) {
        if (argument.isMultiRoots()) {
            argument.deleteDummyRoot();
            argument.setMultiRoots(false);
        }
        TreeVertex root = null;
        Vector roots = argument.getTree().getRoots();
        if (roots.size() > 1) {
            if (argument.getDummyRoot() == null) {
                argument.addDummyRoot(roots);
            }
            root = argument.getDummyRoot();
        } else if (roots.size() == 1) {
            root = (TreeVertex) roots.firstElement();
        }
        // if root == null, the entire tree has been erased,
        // so call emptyTree() to clean things up and
        // clear the display
        if (root == null) {
            argument.emptyTree(false);
        } else {
            calcTreeShape(root);
        }
        diagramSize = new Dimension(getWidth(), getHeight());
        if (doRepaint) {
            repaint();
            getDisplayFrame().getMainScrollPane().setViewportView(this);
        }
    }

    public void calcTreeShape(TreeVertex root) {
        if (root == null) {
            return;
        }
        calcNodeCoords(argument.isInvertedTree());
    }

    protected void createPopupMenu() {
        // pl.edu.agh.araucaria.model.Vertex
        vertexPopup = new JPopupMenu();
        vertexTextMenu = new JMenuItem("Show text");
        editVertexIDMenu = new JMenuItem("Edit ID");
        linkMenu = new JMenuItem("Link");
        unlinkMenu = new JMenuItem("Unlink");
        ownerVertexMenu = new JMenuItem("Modify ownership");
        labelVertexMenu = new JMenuItem("Modify evaluation");
        setPremisesVisMenu = new JMenuItem("Collapse premises");
        vertexPopup.add(vertexTextMenu);
        vertexTextMenu.addActionListener(this);
        vertexPopup.add(editVertexIDMenu);
        editVertexIDMenu.addActionListener(this);
        vertexPopup.add(linkMenu);
        linkMenu.addActionListener(this);
        vertexPopup.add(unlinkMenu);
        unlinkMenu.addActionListener(this);
        vertexPopup.add(ownerVertexMenu);
        ownerVertexMenu.addActionListener(this);
        vertexPopup.add(labelVertexMenu);
        labelVertexMenu.addActionListener(this);
        vertexPopup.add(setPremisesVisMenu);
        setPremisesVisMenu.addActionListener(this);
        // Missing premise
        missingPopup = new JPopupMenu();
        missingTextMenu = new JMenuItem("Show text");
        editMissingTextMenu = new JMenuItem("Edit text");
        editMissingIDMenu = new JMenuItem("Edit ID");
        linkMissingMenu = new JMenuItem("Link");
        unlinkMissingMenu = new JMenuItem("Unlink");
        ownerMissingMenu = new JMenuItem("Modify ownership");
        labelMissingMenu = new JMenuItem("Modify evaluation");
        missingPopup.add(missingTextMenu);
        missingTextMenu.addActionListener(this);
        missingPopup.add(editMissingTextMenu);
        editMissingTextMenu.addActionListener(this);
        missingPopup.add(editMissingIDMenu);
        editMissingIDMenu.addActionListener(this);
        missingPopup.add(linkMissingMenu);
        linkMissingMenu.addActionListener(this);
        missingPopup.add(unlinkMissingMenu);
        unlinkMissingMenu.addActionListener(this);
        missingPopup.add(ownerMissingMenu);
        ownerMissingMenu.addActionListener(this);
        missingPopup.add(labelMissingMenu);
        labelMissingMenu.addActionListener(this);
        // pl.edu.agh.araucaria.model.Edge
        edgePopup = new JPopupMenu();
        edgeTextMenu = new JMenuItem("Show text");
        edgeLinkMenu = new JMenuItem("Link");
        edgeUnlinkMenu = new JMenuItem("Unlink");
        edgeLabelMenu = new JMenuItem("Modify evaluation");
        edgePopup.add(edgeTextMenu);
        edgeTextMenu.addActionListener(this);
        edgePopup.add(edgeLinkMenu);
        edgeLinkMenu.addActionListener(this);
        edgePopup.add(edgeUnlinkMenu);
        edgeUnlinkMenu.addActionListener(this);
        edgePopup.add(edgeLabelMenu);
        edgeLabelMenu.addActionListener(this);
        // Scheme
        schemePopup = new JPopupMenu();
        editSchemeMenu = new JMenuItem("Edit scheme");
        schemePopup.add(editSchemeMenu);
        editSchemeMenu.addActionListener(this);
        // Background
        backgroundPopup = new JPopupMenu();
        aboutAraucariaMenu = new JMenuItem("About main.java.Araucaria");
        backgroundPopup.add(aboutAraucariaMenu);
        aboutAraucariaMenu.addActionListener(this);
    }

    // Popup menu event handlers
    //

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vertexTextMenu || e.getSource() == missingTextMenu) {
            showVertexText();
        } else if (e.getSource() == editMissingTextMenu) {
            editMissingText();
        } else if (e.getSource() == editVertexIDMenu || e.getSource() == editMissingIDMenu) {
            editVertexID();
        } else if (e.getSource() == ownerVertexMenu || e.getSource() == ownerMissingMenu) {
            araucaria.doModifyOwnership();
        } else if (e.getSource() == labelVertexMenu || e.getSource() == edgeLabelMenu ||
                e.getSource() == labelMissingMenu) {
            araucaria.doModifyEvaluation();
        } else if (e.getSource() == linkMenu || e.getSource() == edgeLinkMenu ||
                e.getSource() == linkMissingMenu) {
            araucaria.doLink();
        } else if (e.getSource() == unlinkMenu || e.getSource() == edgeUnlinkMenu ||
                e.getSource() == unlinkMissingMenu) {
            araucaria.doUnlink();
        } else if (e.getSource() == edgeTextMenu) {
            displayEdgeText(mouseEdge);
        } else if (e.getSource() == editSchemeMenu) {
            argument.showSubtreeDialog(mouseSubtree);
            displayFrame.controlFrame.getUndoStack().push(new EditAction(araucaria, "editing scheme"));
        } else if (e.getSource() == setPremisesVisMenu) {
            showHidePremises();
        }
    }


    // Mouse events - methods override those in pl.edu.agh.araucaria.gui.visualisation.core.DiagramBase

    /**
     * Pressing the left mouse button together with shift key clears
     * any highlighted (yellow) text. It also adds any selected vertex or
     * edge to the existing set of selected vertexes and edges.
     */
    public void leftMouseReleased(MouseEvent e) {
        super.leftMouseReleased(e);
        araucaria.updateDisplays(true, false);
    }


    public void rightMouseReleased(MouseEvent e) {
    }
}
