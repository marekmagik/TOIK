package pl.edu.agh.araucaria.gui.popups;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

public class SubtreeCellRenderer extends JTextArea
        implements ListCellRenderer {

    private static final Color evenColor = new Color(1.0f, 1.0f, 0.75f);

    private static final Color oddColor = Color.white;

    private static final Color textColor = new Color(0.0f, 0.0f, 0.25f);

    private int paneWidth;

    public SubtreeCellRenderer(int width) {
        super();
        paneWidth = width - 20;
        setOpaque(true);
    }

    public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
        if (index % 2 != 0) {
            setBackground(oddColor);
            setForeground(textColor);
        } else {
            setBackground(evenColor);
            setForeground(textColor);
        }
        String text = (String) value;
        setText(text);
        setLineWrap(true);
        setWrapStyleWord(true);
        Graphics2D gg = (Graphics2D) list.getGraphics();
        if (gg == null) {
            System.out.println("Null graphics - index = " + index + " " + text);
            return this;
        }

        Font font = gg.getFont();
        FontRenderContext frc = gg.getFontRenderContext();
        Rectangle2D textBounds = font.getStringBounds(text, frc);
        int numLines = (int) (textBounds.getWidth() / paneWidth) + 1;
        int textHeight = (int) textBounds.getHeight();
        setRows(numLines);
        setPreferredSize(new Dimension(paneWidth, textHeight * numLines));
        return this;
    }
}