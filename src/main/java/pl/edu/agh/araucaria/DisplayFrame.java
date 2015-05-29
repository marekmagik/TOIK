package pl.edu.agh.araucaria;/*
 * pl.edu.agh.araucaria.DisplayFrame.java
 *
 * Created on 12 April 2004, 11:16
 */

/**
 * Contains a main diagram (derived from pl.edu.agh.araucaria.DiagramBase) and a pl.edu.agh.araucaria.FreeVertexPanel.
 * Display Frame should be used to add a new display to the tabbed pane in the
 * main main.java.Araucaria window, and for the tree search panel in the database search
 * dialog.
 * @author growe
 */

import javax.swing.*;
import java.awt.*;

public class DisplayFrame extends JPanel {
    public JScrollPane mainScrollPane;
    DiagramBase mainDiagramPanel;
    FreeVertexPanel freeVertexPanel;
    /**
     * controlFrame is the top-level frame containing the pl.edu.agh.araucaria.DisplayFrame.
     * For displays in the main window, this will be the main.java.Araucaria class;
     * for displays in the database search dialog, this will be pl.edu.agh.araucaria.SearchFrame
     */
    public ControlFrame controlFrame;
    Argument argument;
    Araucaria araucaria;

    /**
     * Creates a new instance of pl.edu.agh.araucaria.DisplayFrame
     */
    public DisplayFrame() {
        setLayout(new BorderLayout());
    }

    public JScrollPane getMainScrollPane() {
        return mainScrollPane;
    }

    public DiagramBase getMainDiagramPanel() {
        return mainDiagramPanel;
    }

    public void setMainDiagramPanel(DiagramBase d) {
        mainDiagramPanel = d;
        mainDiagramPanel.setDisplayFrame(this);
        mainScrollPane = new JScrollPane(d);
        add(mainScrollPane, BorderLayout.CENTER);
        mainScrollPane.getVerticalScrollBar().setUnitIncrement(30);
        mainScrollPane.getHorizontalScrollBar().setUnitIncrement(30);
    }

    public FreeVertexPanel getFreeVertexPanel() {
        return freeVertexPanel;
    }

    public void setFreeVertexPanel(FreeVertexPanel f) {
        freeVertexPanel = f;
        freeVertexPanel.setDisplayFrame(this);
        add(f, BorderLayout.SOUTH);
    }

    public void setControlFrame(ControlFrame c) {
        controlFrame = c;
    }

    public void setArgument(Argument a) {
        argument = a;
        mainDiagramPanel.setArgument(a);
        freeVertexPanel.setArgument(a);
    }

    public void setAraucaria(Araucaria a) {
        araucaria = a;
        mainDiagramPanel.setAraucaria(a);
        freeVertexPanel.setAraucaria(a);
    }

    public void refreshPanels(boolean doRepaint) {
        mainDiagramPanel.redrawTree(doRepaint);
        this.mainScrollPane.setViewportView(mainDiagramPanel);
        freeVertexPanel.redrawTree(doRepaint);
    }
}
