package pl.edu.agh.araucaria;/*
 * pl.edu.agh.araucaria.DiagramTreeSearch.java
 *
 * Created on 19 March 2004, 12:11
 */

/**
 * Draws the original main.java.Araucaria tree diagram in which the entire tree is
 * displayed without any scroll bars
 *
 * @author growe
 */

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
