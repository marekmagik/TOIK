package pl.edu.agh.araucaria.gui.dialogs;

import pl.edu.agh.araucaria.Araucaria;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * Title:        main.java.Araucaria
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      University of Dundee
 *
 * @author Glenn Rowe
 * @version 1.0
 */

public class AboutDialog extends JDialog implements ActionListener {
    private JButton okButton;

    public AboutDialog(Frame owner) {
        super(owner);
        Araucaria.createIcon(owner);
        this.setModal(true);
        this.getContentPane().setBackground(Color.yellow);

        setTitle("About main.java.Araucaria - version 3.1; June 2006");

        ImageIcon pic = new ImageIcon("images/AraucariaSplash3_1.jpg");

        JLabel aboutMessage = new JLabel(pic);
        okButton = new JButton("OK");
        okButton.addActionListener(this);

        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(aboutMessage, BorderLayout.NORTH);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);

        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        setSize(480, 255);
        pack();
        addDialogCloser();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

        setLocation(d.width / 2 - this.getSize().width / 2,
                d.height / 2 - this.getSize().height / 2);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == okButton) {
            this.setVisible(false);
            this.dispose();
        }
    }

    public void addDialogCloser() {
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

        okButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ks, "close");
        okButton.getActionMap().put("close", closeAction);
    }
}
