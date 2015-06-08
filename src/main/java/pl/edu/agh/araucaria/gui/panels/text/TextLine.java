package pl.edu.agh.araucaria.gui.panels.text;

import java.awt.*;
import java.awt.font.TextLayout;

/**
 * Represents a single line of text with its own layout.
 * 'start' and 'end' are the indexes of the first and last
 * characters on the line, relative to the total text.
 */
public class TextLine {
    private Color textColor;
    private final TextLayout textLayout;
    private final int start;
    private final int end;

    public TextLine(TextLayout layout, int s, int e) {
        textLayout = layout;
        start = s;
        end = e;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public TextLayout getLayout() {
        return textLayout;
    }

    public Color getTextColor() {
        return textColor;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }
}