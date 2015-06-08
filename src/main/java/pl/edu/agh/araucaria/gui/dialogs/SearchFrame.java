package pl.edu.agh.araucaria.gui.dialogs;

import pl.edu.agh.araucaria.Araucaria;
import pl.edu.agh.araucaria.ControlFrame;
import pl.edu.agh.araucaria.gui.visualisation.core.DiagramTreeSearch;
import pl.edu.agh.araucaria.gui.panels.DisplayFrame;
import pl.edu.agh.araucaria.aml.TextSearchTableModel;
import pl.edu.agh.araucaria.gui.visualisation.core.TreeSearchPanel;
import pl.edu.agh.araucaria.exceptions.LinkException;
import pl.edu.agh.araucaria.gui.panels.FreeVertexPanel;
import pl.edu.agh.araucaria.history.UndoStack;
import pl.edu.agh.araucaria.model.Argument;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SearchFrame extends JDialog implements Runnable, ControlFrame {
    // Labels for the various diagram types
    public UndoStack undoStack = new UndoStack();
    Araucaria araucaria;
    JTabbedPane m_tabbedPane;
    JPanel m_textSearchPanel, m_treeSearchPanel, m_schemesetSearchPanel;
    TextSearchTableModel textSearchTableModel;
    JLabel textMessageLabel = new JLabel("");
    JPanel searchResultsPanel;
    ActionEvent searchEvent;
    // pl.edu.agh.araucaria.model.Tree searching panel
    Argument argument;
    DiagramTreeSearch diagram;
    JLabel schemeMessageLabel = new JLabel("");
    // pl.edu.agh.araucaria.model.Tree searching panel
    int numDisplays = 1;
    DisplayFrame[] displays = new DisplayFrame[numDisplays];
    private DisplayFrame currentDiagram;    // The currently displayed diagram in the tabbed pane
    private JTextField textSearchTextField;
    private JTable textSearchTable;
    private JButton clearDiagramButton;
    private JButton linkButton;
    private JButton unlinkButton;
    private JButton refutationButton;
    private JButton deleteButton;
    private JLabel treeStatusLabel;
    private JComboBox searchSchemesetCombo;

    /**
     * Creates new form SearchTextDialog
     */
    public SearchFrame(Frame parent, boolean modal) {
        super(parent, modal);
        araucaria = (Araucaria) parent;
        argument = new Argument();
        WindowHandler windowHandler = new WindowHandler();
        addWindowListener(windowHandler);
        setTitle("Search argument database");
        setSize(900, 630);

        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                diagram.redrawTree(true);
            }
        });

        initComponents();
    }

    public Araucaria getAraucaria() {
        return araucaria;
    }

    public void updateDisplays(boolean updateCurrent) {
        int numDiagrams = 1;
        for (int i = 0; i < numDiagrams; i++) {
            if (displays[i] != currentDiagram) {
                displays[i].setArgument(argument);
                displays[i].refreshPanels(true);
            }
            if (updateCurrent) {
                currentDiagram.setArgument(argument);
                currentDiagram.refreshPanels(true);
            }
        }
    }

    private void initComponents() {
        m_tabbedPane = new JTabbedPane();
        JPanel mainGridPanel = new JPanel(new GridLayout(1, 2));
        mainGridPanel.add(m_tabbedPane);

        addTextSearchPanel();
        addTreeSearchPanel();
        addSchemesetSearchPanel();

        searchResultsPanel = new JPanel(new BorderLayout());
        searchResultsPanel.add(new JLabel("Search results", JLabel.CENTER), BorderLayout.NORTH);
        JScrollPane textSearchScrollPane = new JScrollPane();
        textSearchTable = new JTable();
        textSearchTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        textSearchScrollPane.setViewportView(textSearchTable);
        JPanel searchTablePanel = new JPanel(new BorderLayout());
        searchTablePanel.add(textSearchScrollPane, BorderLayout.CENTER);
        searchResultsPanel.add(searchTablePanel, java.awt.BorderLayout.CENTER);

        textSearchTableModel = new TextSearchTableModel(araucaria);
        textSearchTable.setModel(textSearchTableModel);

        mainGridPanel.add(searchResultsPanel);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(mainGridPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton closeButton = new JButton("Close window");
        closeButton.setMnemonic(KeyEvent.VK_C);
        buttonPanel.add(closeButton);
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hide();
                araucaria.setMessageLabelText(" ");
            }
        });
        JButton cancelButton = new JButton("Cancel search");
        cancelButton.setMnemonic(KeyEvent.VK_A);
        buttonPanel.add(cancelButton);
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                araucaria.cancelSearch();
            }
        });
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }

    private void searchButtonActionPerformed() {
        araucaria.doTextSearchOnDB(textSearchTextField.getText(), "<TEXT>", "</TEXT>",
                textSearchTableModel, textSearchTable);
    }

    private void addTextSearchPanel() {
        m_textSearchPanel = new JPanel(new BorderLayout());
        m_tabbedPane.addTab("Text", null, m_textSearchPanel, "Search text field");

        JPanel enterTextPanel = new JPanel();
        JLabel enterTextLabel = new JLabel();
        textSearchTextField = new JTextField();
        JButton searchButton = new JButton();

        enterTextPanel.setLayout(new java.awt.BorderLayout());
        enterTextLabel.setText("Enter text to search for:");
        enterTextPanel.add(enterTextLabel, BorderLayout.NORTH);
        enterTextPanel.add(textSearchTextField, BorderLayout.CENTER);
        searchButton.setText("Search");
        searchButton.setMnemonic(KeyEvent.VK_S);
        enterTextPanel.add(searchButton, BorderLayout.SOUTH);
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                textMessageLabel.setText("Searching database. Please wait...");
                searchEvent = evt;
                Thread searchThread = new Thread(SearchFrame.this, "TextSearch");
                searchThread.start();
            }
        });

        m_textSearchPanel.add(enterTextPanel, BorderLayout.NORTH);
        m_textSearchPanel.add(textMessageLabel, BorderLayout.CENTER);
    }

    @Override
    public void run() {
        Thread currentThread = Thread.currentThread();
        if (currentThread.getName().equals("TextSearch")) {
            searchButtonActionPerformed();
            textMessageLabel.setText("Search complete.");
        } else if (currentThread.getName().equals("SchemeSearch")) {
            searchSchemesetButtonActionPerformed();
            schemeMessageLabel.setText("Search complete.");
        } else if (currentThread.getName().equals("TreeSearch")) {
            treeStatusLabel.setText("Searching database. Please wait...");
            int matches = araucaria.doTreeSearch(argument.getTree(),
                    textSearchTableModel, textSearchTable);
            if (matches == 0) {
                treeStatusLabel.setText("Search complete. No matches found.");
            } else if (matches == 1) {
                treeStatusLabel.setText("Search complete. Found 1 match.");
            } else {
                treeStatusLabel.setText("Search complete. Found " + matches + " matches.");
            }
        }
    }

    private void addTreeSearchPanel() {
        m_treeSearchPanel = new JPanel(new BorderLayout());
        JToolBar treeToolBar = new JToolBar();
        treeToolBar.setFloatable(false);
        treeToolBar.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        treeToolBar.setAlignmentX(LEFT_ALIGNMENT);

        // Toolbar
        Action clearDiagramAction = new FileActionHandler("Clear diagram", new ImageIcon("images/Document.gif"));
        clearDiagramAction.putValue(Action.SHORT_DESCRIPTION, "Clear diagram");
        clearDiagramButton = treeToolBar.add(clearDiagramAction);

        Action linkAction = new FileActionHandler("Link premises", new ImageIcon("images/Pin.gif"));
        linkAction.putValue(Action.SHORT_DESCRIPTION, "Link premises");
        linkButton = treeToolBar.add(linkAction);

        Action unlinkAction = new FileActionHandler("Unlink premises", new ImageIcon("images/PinLeft.gif"));
        unlinkAction.putValue(Action.SHORT_DESCRIPTION, "Unlink premises");
        unlinkButton = treeToolBar.add(unlinkAction);

        Action refutationAction = new FileActionHandler("Refutation", new ImageIcon("images/Widen.gif"));
        refutationAction.putValue(Action.SHORT_DESCRIPTION, "Refutation");
        refutationButton = treeToolBar.add(refutationAction);

        Action deleteAction = new FileActionHandler("Delete", new ImageIcon("images/Error.gif"));
        deleteAction.putValue(Action.SHORT_DESCRIPTION, "Delete selected items");
        deleteButton = treeToolBar.add(deleteAction);
        treeToolBar.addSeparator();


        JButton searchTreeButton = new JButton(new ImageIcon("images/SearchRow.gif"));
        treeToolBar.add(searchTreeButton);
        searchTreeButton.setToolTipText("Search database");
        searchTreeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                searchEvent = evt;
                Thread treeThread = new Thread(SearchFrame.this, "TreeSearch");
                treeThread.start();
            }
        });

        m_treeSearchPanel.add(treeToolBar, BorderLayout.NORTH);

        // Scaled diagram
        displays[0] = new DisplayFrame();
        displays[0].setMainDiagramPanel(new TreeSearchPanel());
        displays[0].setFreeVertexPanel(new FreeVertexPanel());
        displays[0].setControlFrame(this);
        displays[0].setAraucaria(araucaria);
        displays[0].setArgument(argument);
        currentDiagram = displays[0];

        JPanel canvasPanel = new JPanel(new BorderLayout());
        canvasPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        canvasPanel.add(displays[0], BorderLayout.CENTER);
        canvasPanel.setBackground(new Color(255, 255, 180));
        m_treeSearchPanel.add(canvasPanel, BorderLayout.CENTER);

        JPanel treeStatusBar = new JPanel();
        treeStatusLabel = new JLabel("Click to add a node to the search pattern");
        treeStatusBar.add(treeStatusLabel);
        m_treeSearchPanel.add(treeStatusBar, BorderLayout.SOUTH);
        m_tabbedPane.addTab("Structure", null, m_treeSearchPanel, "Search by tree structure");
    }

    // Schemeset searching panel
    private void searchSchemesetButtonActionPerformed() {
        araucaria.doTextSearchOnDB((String) searchSchemesetCombo.getSelectedItem(), "<INSCHEME", "/>",
                textSearchTableModel, textSearchTable);
    }

    private void addSchemesetSearchPanel() {
        m_schemesetSearchPanel = new JPanel(new BorderLayout());
        m_tabbedPane.addTab("Scheme", null, m_schemesetSearchPanel, "Search for scheme");

        JPanel enterSchemesetPanel = new JPanel();
        JLabel enterSchemesetLabel = new JLabel();
        searchSchemesetCombo = new JComboBox();
        searchSchemesetCombo.setEditable(true);
        JButton searchSchemesetButton = new JButton();

        enterSchemesetPanel.setLayout(new java.awt.BorderLayout());
        enterSchemesetLabel.setText("Enter scheme to search for:");
        enterSchemesetPanel.add(enterSchemesetLabel, BorderLayout.NORTH);
        enterSchemesetPanel.add(searchSchemesetCombo, BorderLayout.CENTER);
        searchSchemesetButton.setText("Search");
        searchSchemesetButton.setMnemonic(KeyEvent.VK_S);
        enterSchemesetPanel.add(searchSchemesetButton, BorderLayout.SOUTH);
        searchSchemesetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                searchEvent = evt;
                schemeMessageLabel.setText("Searching database. Please wait...");
                Thread schemeThread = new Thread(SearchFrame.this, "SchemeSearch");
                schemeThread.start();
            }
        });

        m_schemesetSearchPanel.add(enterSchemesetPanel, BorderLayout.NORTH);
        m_schemesetSearchPanel.add(schemeMessageLabel, BorderLayout.CENTER);
    }

    public void setMessageLabelText(String text) {
        treeStatusLabel.setText(text);
    }

    public UndoStack getUndoStack() {
        return undoStack;
    }

    public void doRedo() {
    }

    public void doUndo() {
    }

    /**
     * Window handler closes the main frame window
     */
    class WindowHandler extends WindowAdapter {
        public void windowClosing(WindowEvent event) {
            Object object = event.getSource();
            if (object == SearchFrame.this)
                setVisible(false);
        }
    }

    class FileActionHandler extends AbstractAction //implements ActionListener
    {

        FileActionHandler(String name, Icon icon) {
            super(name, icon);
        }

        public void actionPerformed(ActionEvent event) {
            if (event.getSource() == clearDiagramButton) {
                argument.emptyTree(true);
            } else if (event.getSource() == linkButton) {
                try {
                    argument.linkVertices();
                } catch (LinkException ignored) {
                }
            } else if (event.getSource() == unlinkButton) {
                try {
                    argument.unlinkVertices();
                } catch (LinkException ignored) {
                }
            } else if (event.getSource() == refutationButton) {
                argument.setRefutations();
            } else if (event.getSource() == deleteButton) {
                argument.deleteSelectedItems();
            }
            updateDisplays(true);
        }
    }
}