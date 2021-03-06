package pl.edu.agh.araucaria.gui.dialogs;

import pl.edu.agh.araucaria.Araucaria;
import pl.edu.agh.araucaria.model.Argument;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 */
public class PropertiesDialog extends JDialog implements ActionListener {
    public Araucaria owner;
    public JTextArea sourceText, commentsText;
    public JTextField author;
    Argument argument;
    JButton okButton, cancelButton;
    boolean okPressed = false;

    public PropertiesDialog(Frame parent) {
        super(parent, true);
        owner = (Araucaria) parent;
        argument = owner.getArgument();
        WindowHandler windowHandler = new WindowHandler();
        addWindowListener(windowHandler);
        setTitle("Properties");
        setSize(300, 450);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(d.width / 2 - getSize().width / 2,
                d.height / 2 - getSize().height / 2);
        initComponents();
    }

    private void initComponents() {
        JPanel infoPanel = new JPanel(new GridLayout(9, 1));
        infoPanel.add(new JLabel(" Words in text: " + owner.getSelectText().wordCount()));
        infoPanel.add(new JLabel(" Propositions: " + argument.getTree().getVertexCount()));
        infoPanel.add(new JLabel(" Enthymemes: " + argument.getTree().getMissingCount()));
        double ratio = 0.0;
        if (argument.getTree().getVertexCount() > 0) {
            ratio = (double) argument.getTree().getMissingCount() /
                    argument.getTree().getVertexCount();
            ratio = Math.rint(100.0 * ratio) / 100.0;
        }
        infoPanel.add(new JLabel(" Ratio of enthymemes to propositions: " + ratio));
        infoPanel.add(new JLabel(" Refutations: " + argument.getTree().getRefutationCount()));
        infoPanel.add(new JLabel(" Owners: " + argument.getOwnerList().size()));
        argument.buildSupportLabelList();
        infoPanel.add(new JLabel(" Evaluations: " + argument.getSupportLabelList().size()));

        // Author
        author = new JTextField();
        author.setText(argument.getAuthor());
        JPanel authorPanel = new JPanel(new BorderLayout());
        authorPanel.add(new JLabel(" Author: "), BorderLayout.WEST);
        authorPanel.add(author, BorderLayout.CENTER);
        infoPanel.add(authorPanel);

        // Date
        infoPanel.add(new JLabel(" Date: " + argument.getDate()));

        sourceText = new JTextArea();
        sourceText.setText(argument.getSource());
        sourceText.setLineWrap(true);
        sourceText.setWrapStyleWord(true);
        JScrollPane sourceScroll = new JScrollPane(sourceText);
        JPanel sourcePanel = new JPanel(new BorderLayout());
        sourcePanel.setBorder(BorderFactory.createTitledBorder(" Source of text"));
        sourcePanel.add(sourceScroll, BorderLayout.CENTER);

        commentsText = new JTextArea();
        commentsText.setText(argument.getComments());
        commentsText.setLineWrap(true);
        commentsText.setWrapStyleWord(true);
        JScrollPane commentsScroll = new JScrollPane(commentsText);
        JPanel commentsPanel = new JPanel(new BorderLayout());
        commentsPanel.setBorder(BorderFactory.createTitledBorder(" Comments"));
        commentsPanel.add(commentsScroll, BorderLayout.CENTER);

        JPanel sourceCommentsPanel = new JPanel(new GridLayout(2, 1));
        sourceCommentsPanel.add(sourcePanel);
        sourceCommentsPanel.add(commentsPanel);

        JPanel propertiesPanel = new JPanel(new BorderLayout());
        propertiesPanel.add(infoPanel, BorderLayout.NORTH);
        propertiesPanel.add(sourceCommentsPanel, BorderLayout.CENTER);

        okButton = new JButton("OK");
        okButton.addActionListener(this);
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        addDialogCloser(okButton);
        this.getRootPane().setDefaultButton(okButton);
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        getContentPane().add(propertiesPanel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }

    public boolean isOkPressed() {
        return okPressed;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == okButton) {
            okPressed = true;
        } else if (e.getSource() == cancelButton) {
            okPressed = false;
        }
        this.setVisible(false);
    }

    public void addDialogCloser(JComponent comp) {
        AbstractAction closeAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        };

        // Then create a keystroke to use for it
        KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);

        // Finally, bind the keystroke and the action to *any* component
        // within the dialog. Note the WHEN_IN_FOCUSED bit...this is what
        // stops you having to do it for all components

        comp.getInputMap(
                JComponent.WHEN_IN_FOCUSED_WINDOW).put(ks, "close");
        comp.getActionMap().put("close", closeAction);
    }


    /**
     * Window handler closes the main frame window
     */
    class WindowHandler extends WindowAdapter {
        public void windowClosing(WindowEvent event) {
            Object object = event.getSource();
            if (object == PropertiesDialog.this)
                setVisible(false);
        }
    }

}
