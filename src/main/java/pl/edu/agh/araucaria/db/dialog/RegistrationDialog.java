package pl.edu.agh.araucaria.db.dialog;

import pl.edu.agh.araucaria.Araucaria;
import pl.edu.agh.araucaria.gui.panels.SizedPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class RegistrationDialog extends JDialog implements ActionListener {
    private JPanel textPanel = new JPanel();
    private JTextField usernameTextField = new JTextField();
    private JTextField fullNameTextField = new JTextField();
    private JTextField addressTextField = new JTextField();
    private JTextField emailTextField = new JTextField();
    private JLabel whyLoginLabel = new JLabel("<html><CENTER><P>\n" +
            "You need to register once before you can log on to AraucariaDB " +
            "and use the online repository. Please provide complete details " +
            "(these will not be released to any third party).</P></CENTER> </html>",
            new ImageIcon("images/Key.gif"), JLabel.CENTER);

    private JLabel messageLabel = new JLabel(" ", JLabel.CENTER);
    private JPanel buttonPanel = new JPanel();
    private JButton okButton = new JButton();
    private JButton cancelButton = new JButton();
    private boolean okPressed = false;
    private Araucaria owner;

    public RegistrationDialog(Araucaria parent) {
        super(parent, true);
        owner = parent;
        try {
            jbInit();
            this.pack();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(d.width / 2 - this.getSize().width / 2,
                d.height / 2 - this.getSize().height / 2);
    }

    private void jbInit() throws Exception {
        this.setResizable(true);
        this.setTitle("Register to use database");
        SizedPanel masterPanel = new SizedPanel(500, 200);
        masterPanel.setLayout(new BorderLayout(5, 5));
        JPanel whyPanel = new JPanel(new GridLayout(1, 1));
        whyPanel.add(whyLoginLabel);

        JPanel labelPanel = new JPanel(new GridLayout(4, 1));
        textPanel.setLayout(new GridLayout(4, 1));
        labelPanel.add(new JLabel("Choose username: ", JLabel.RIGHT));
        textPanel.add(usernameTextField);
        labelPanel.add(new JLabel("Full name: ", JLabel.RIGHT));
        textPanel.add(fullNameTextField);
        labelPanel.add(new JLabel("Address: ", JLabel.RIGHT));
        textPanel.add(addressTextField);
        labelPanel.add(new JLabel("Email: ", JLabel.RIGHT));
        textPanel.add(emailTextField);

        JPanel dataPanel = new JPanel(new BorderLayout());
        dataPanel.add(labelPanel, BorderLayout.WEST);
        dataPanel.add(textPanel, BorderLayout.CENTER);
        dataPanel.add(messageLabel, BorderLayout.SOUTH);

        okButton.setActionCommand("okButton");
        okButton.setText("OK");
        cancelButton.setActionCommand("cancelButton");
        cancelButton.setText("Cancel");
        masterPanel.add(whyPanel, BorderLayout.NORTH);
        masterPanel.add(dataPanel, BorderLayout.CENTER);
        masterPanel.add(buttonPanel, BorderLayout.SOUTH);
        buttonPanel.add(okButton, null);
        buttonPanel.add(cancelButton, null);
        okButton.addActionListener(this);
        cancelButton.addActionListener(this);
        addDialogCloser(cancelButton);
        this.getRootPane().setDefaultButton(okButton);
        this.getContentPane().add(masterPanel);
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

    private void okButtonPressed() {
        okPressed = true;
        if (emptyFields()) {
            messageLabel.setText("Please fill in all boxes.");
        } else if (owner.doRegister()) {
            messageLabel.setText("Registration successful");
            owner.setMessageLabelText("Registration successful");
            try {
                Thread.sleep(500);
            } catch (Exception ignored) {
            }
            this.hide();
        } else {
            messageLabel.setText("Username already taken. Please choose another.");
            usernameTextField.setText("");
            owner.setMessageLabelText("Username already taken. Please choose another.");
        }
    }

    private boolean emptyFields() {
        return usernameTextField.getText().length() == 0 ||
                fullNameTextField.getText().length() == 0 ||
                addressTextField.getText().length() == 0 ||
                emailTextField.getText().length() == 0;
    }

    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == okButton) {
            okButtonPressed();
        } else {
            okPressed = false;
            this.hide();
        }
    }

    public JTextField getUsernameTextField() {
        return usernameTextField;
    }

    public JTextField getFullNameTextField() {
        return fullNameTextField;
    }

    public JTextField getAddressTextField() {
        return addressTextField;
    }

    public JTextField getEmailTextField() {
        return emailTextField;
    }

    @Override
    public Araucaria getOwner() {
        return owner;
    }

    public void setOwner(Araucaria owner) {
        this.owner = owner;
    }
}
