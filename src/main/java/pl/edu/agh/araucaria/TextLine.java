package pl.edu.agh.araucaria;

import java.awt.*;
import java.awt.font.TextLayout;

/**
 * Represents a single line of text with its own layout.
 * 'start' and 'end' are the indexes of the first and last
 * characters on the line, relative to the total text.
 */
public class TextLine {
    public Color textColor;
    TextLayout textLayout;
    int start, end;

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
}