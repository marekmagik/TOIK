package pl.edu.agh.araucaria.tutor;/*
 * pl.edu.agh.araucaria.tutor.TutorDialog.java
 *
 * Created on 25 February 2004, 11:43
 */

/**
 *
 * @author growe
 */

import pl.edu.agh.araucaria.Araucaria;
import pl.edu.agh.araucaria.model.TreeVertex;
import pl.edu.agh.araucaria.gui.panels.text.SelectText;
import pl.edu.agh.araucaria.gui.panels.text.TextLine;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.util.Enumeration;
import java.util.Vector;

public class TutorDialog extends JDialog {
    // End of variables declaration//GEN-END:variables
    public boolean okPressed;
    public int tutorStart, tutorEnd;
    SelectText selectTutorText;
    Araucaria owner;
    int offset, endOffset;
    private javax.swing.JButton tutorCancel;
    private javax.swing.JButton tutorOK;
    private javax.swing.JScrollPane tutorScrollPane;
    /**
     * Creates new form pl.edu.agh.araucaria.tutor.TutorDialog
     */
    public TutorDialog(Araucaria parent, boolean modal) {
        super(parent, modal);
        owner = parent;
        initComponents();
        initText();
    }

    private void highlightText(int offset, int endOffset) {
        Vector textLayout = selectTutorText.getTextLayout();
        Enumeration textLines = textLayout.elements();
        int lineNum = 0, anchorLine = -1, anchorEnd = -1,
                activeLine = -1, activeEnd = -1;
        while (textLines.hasMoreElements()) {
            TextLine line = (TextLine) textLines.nextElement();
            if (offset >= line.getStart() && offset <= line.getEnd()) {
                anchorLine = lineNum;
                anchorEnd = offset - line.getStart();
            }
            if (endOffset >= line.getStart() && endOffset <= line.getEnd()) {
                activeLine = lineNum;
                activeEnd = endOffset - line.getStart();
            }
            lineNum++;
        }
        selectTutorText.setSelection(anchorLine, anchorEnd, activeLine, activeEnd);
        selectTutorText.findHighlightArea();
    }

    private void initText() {
        selectTutorText = new SelectText(7 * this.getWidth() / 8, 800);
        SelectText parentSelectText = owner.getSelectText();
        selectTutorText.setText(parentSelectText.getText());
        selectTutorText.constructTextLayout();

        Vector selectedVertices = owner.getArgument().getSelectedVertices();
        if (selectedVertices.size() != 1) return;

        TreeVertex vertex = (TreeVertex) selectedVertices.elementAt(0);
        offset = vertex.getOffset();
        String propText = (String) vertex.getLabel();

        // Find the bounds of selected text to allow scroll pane to show it
        Rectangle textBounds = new Rectangle();
        if (offset >= 0) {
            // Attach vertex to text layout if not a missing premise
            // Need to find line number and char position within that line
            // for start and end of text
            endOffset = propText.length() + offset;
            highlightText(offset, endOffset);
            textBounds = selectTutorText.useCurrentText().getBounds();
            selectTutorText.clearSelection();

            tutorStart = vertex.getTutorStart();
            tutorEnd = vertex.getTutorEnd();
            highlightText(tutorStart, tutorEnd);
        }

        tutorScrollPane.getViewport().setView(selectTutorText);
        tutorScrollPane.getViewport().scrollRectToVisible(textBounds);
        selectTutorText.repaint();
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        JLabel tutorLabel = new JLabel();
        JPanel tutorButtonPanel = new JPanel();
        tutorOK = new javax.swing.JButton();
        tutorCancel = new javax.swing.JButton();
        tutorScrollPane = new javax.swing.JScrollPane();

        addWindowListener(new WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog();
            }
        });

        tutorLabel.setText("Select maximum allowable limits for premise");
        getContentPane().add(tutorLabel, java.awt.BorderLayout.NORTH);

        tutorOK.setText("OK");
        tutorOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                buttonActionPerformed(evt);
            }
        });

        tutorButtonPanel.add(tutorOK);

        tutorCancel.setText("Cancel");
        tutorCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                buttonActionPerformed(evt);
            }
        });

        tutorButtonPanel.add(tutorCancel);

        getContentPane().add(tutorButtonPanel, BorderLayout.SOUTH);

        tutorScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        tutorScrollPane.setViewportBorder(new EtchedBorder());
        getContentPane().add(tutorScrollPane, BorderLayout.CENTER);

        java.awt.Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width - 400) / 2, (screenSize.height - 300) / 2, 400, 300);
    }//GEN-END:initComponents

    private void buttonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_buttonActionPerformed
        if (evt.getSource() == tutorOK) {
            okPressed = true;
            String selectedText = selectTutorText.getSelectedText();
            if (selectTutorText.getOffset() <= offset &&
                    selectTutorText.getOffset() + selectedText.length() >= endOffset) {
                tutorStart = selectTutorText.getOffset();
                tutorEnd = tutorStart + selectedText.length();
            }
            this.setVisible(false);
        } else if (evt.getSource() == tutorCancel) {
            okPressed = false;
            this.setVisible(false);
        }
    }//GEN-LAST:event_buttonActionPerformed

    /**
     * Closes the dialog
     */
    private void closeDialog() {//GEN-FIRST:event_closeDialog
        setVisible(false);
        dispose();
    }//GEN-LAST:event_closeDialog
}