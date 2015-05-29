package pl.edu.agh.araucaria.gui.panels;

import javax.swing.*;
import java.awt.*;

public class SizedPanel extends JPanel {
    private int width;
    private int height;

    public SizedPanel(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public Dimension getPreferredSize() {
        return new Dimension(width, height);
    }
}
