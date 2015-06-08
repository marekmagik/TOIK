package pl.edu.agh.araucaria.gui.visualisation.core;/*
 * pl.edu.agh.araucaria.gui.visualisation.core.DiagramTreeSearch.java
 *
 * Created on 19 March 2004, 12:11
 */

/**
 * Draws the original main.java.Araucaria tree diagram in which the entire tree is
 * displayed without any scroll bars
 *
 * @author growe
 */

import pl.edu.agh.araucaria.gui.visualisation.core.DiagramBase;

import java.awt.image.BufferedImage;

public class DiagramTreeSearch extends DiagramBase {

    @Override
    public BufferedImage getJpegImage() {
        return null;
    }

    @Override
    public void redrawTree(boolean doRepaint) {
    }
}
