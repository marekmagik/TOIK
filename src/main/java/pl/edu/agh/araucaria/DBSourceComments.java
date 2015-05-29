package pl.edu.agh.araucaria;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Allows user to enter source & comments when saving an argument to
 * the database.
 * <p/>
 * Also allows viewing of the source & comments when retrieving an
 * entry from the database.
 * <p/>
 * Distinguish between the two uses by setting the enterMode flag.
 */
public class DBSourceComments extends JDialog implements ActionListener {
    private JTextArea sourceText, commentsText;
    private boolean okPressed, enterMode;
    private JButton okButton, cancelButton;
    private static final String ENTER_SOURCE_LABEL = "Enter source of text:";
    private static final String ENTER_COMMENTS_LABEL = "Enter comments on argument:";
    private static final String SHOW_SOURCE_LABEL = "Source of text:";
    private static final String SHOW_COMMENTS_LABEL = "Comments on argument:";

    /**
     * If enter == true, dialog is created to allow entry of data.
     * Otherwise, allows read-only access to data.
     */
    public DBSourceComments(Frame parent, boolean modal, boolean enter) {
        super(parent, modal);
        enterMode = enter;
        addWindowListener(new WindowHandler());
        setTitle("Source & Comments");
        setSize(300, 300);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(d.width / 2 - getSize().width / 2,
                d.height / 2 - getSize().height / 2);
        initComponents();
    }

    private void initComponents() {
        JPanel panel = buildPanel();
        getContentPane().add(panel, BorderLayout.CENTER);
        okButton = new JButton("OK");
        okButton.addActionListener(this);
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel buildPanel() {
        JPanel mainPanel = new JPanel(new GridLayout(2, 0, 10, 10));
        JPanel sourcePanel = new JPanel(new BorderLayout());
        if (enterMode) {
            sourcePanel.add(new JLabel(ENTER_SOURCE_LABEL), BorderLayout.NORTH);
        } else {
            sourcePanel.add(new JLabel(SHOW_SOURCE_LABEL), BorderLayout.NORTH);
        }
        sourceText = new JTextArea();
        sourceText.setLineWrap(true);
        sourceText.setWrapStyleWord(true);
        if (!enterMode) {
            sourceText.setEditable(false);
        }
        JScrollPane sourceScroll = new JScrollPane(sourceText);
        sourcePanel.add(sourceScroll, BorderLayout.CENTER);

        JPanel commentsPanel = new JPanel(new BorderLayout());
        if (enterMode) {
            commentsPanel.add(new JLabel(ENTER_COMMENTS_LABEL), BorderLayout.NORTH);
        } else {
            commentsPanel.add(new JLabel(SHOW_COMMENTS_LABEL), BorderLayout.NORTH);
        }
        commentsText = new JTextArea();
        commentsText.setLineWrap(true);
        commentsText.setWrapStyleWord(true);
        if (!enterMode) {
            commentsText.setEditable(false);
        }
        JScrollPane commentsScroll = new JScrollPane(commentsText);
        commentsPanel.add(commentsScroll, BorderLayout.CENTER);

        mainPanel.add(sourcePanel);
        mainPanel.add(commentsPanel);
        return mainPanel;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == okButton) {
            okPressed = true;
            this.setVisible(false);
        } else if (e.getSource() == cancelButton) {
            okPressed = false;
            this.setVisible(false);
        }
    }

    /**
     * Window handler closes the main frame window
     */
    private class WindowHandler extends WindowAdapter {

        @Override
        public void windowClosing(WindowEvent event) {
            Object object = event.getSource();
            if (object == DBSourceComments.this)
                setVisible(false);
        }
    }

    public JTextArea getSourceText() {
        return sourceText;
    }

    public boolean isOkPressed() {
        return okPressed;
    }

    public JTextArea getCommentsText() {
        return commentsText;
    }
}
