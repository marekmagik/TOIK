package pl.edu.agh.araucaria.aml;/*
 * pl.edu.agh.araucaria.aml.TextSearchTableModel.java
 *
 * Created on 30 November 2001, 16:07
 */

import pl.edu.agh.araucaria.Araucaria;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Vector;

/**
 * @author growe
 */
public class TextSearchTableModel extends AbstractTableModel {

    static final int AML = 3;

    String[] columnNames = {"id", "user", "submitted", "aml"};
    Vector rows = new Vector();
    int textSearchKey = 0;
    Araucaria parent;

    public TextSearchTableModel(Araucaria p) {
        parent = p;
    }

    public void updateTable(JTable table, LinkedList resultList) {
        try {
            rows = new Vector();
            for (Object aResultList : resultList) {
                Vector argument = (Vector) aResultList;
                Vector newRow = new Vector();
                for (int i = 0; i < getColumnCount(); i++) {
                    if (i == AML) {
                        String aml = (String) argument.elementAt(i);
                        int textStart = aml.indexOf("<TEXT>");
                        int textEnd = aml.indexOf("</TEXT>");
                        String text = aml.substring(textStart + "<TEXT>".length(), textEnd);
                        try {
                            text = text.substring(0, 80);
                        } catch (Exception ignored) {
                        }
                        newRow.addElement(text);
                    } else {
                        newRow.addElement(argument.elementAt(i));
                    }
                }
                rows.addElement(newRow);
            }
            fireTableChanged(null); // Tell the listeners a new table has arrived.
            // If the rows vector is empty, there were no records in the record set.
            if (rows.size() == 0)
                return;
            setColumnWidths(table); // Have to set column widths *after* table is refreshed.
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            // Attach selection listener for row selections with the mouse.
            ListSelectionModel rowSM = table.getSelectionModel();
            rowSM.addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    //Ignore extra messages.
                    if (e.getValueIsAdjusting()) return;

                    ListSelectionModel lsm =
                            (ListSelectionModel) e.getSource();
                    if (!lsm.isSelectionEmpty()) {
                        int selectedRow = lsm.getMinSelectionIndex();
                        textSearchKey = Integer.parseInt(getValueAt(selectedRow, 0).toString());
                        parent.openFromDB(textSearchKey);
                        parent.updateDisplays(true);
                        parent.updateSelectText();
                    }
                }
            });
        } catch (Exception ex) {
            System.err.println("In updateTable/LinkedList: " + ex);
        }
    }

    public void updateTable(JTable table, ResultSet resultSet) {
        try {
            ResultSetMetaData metaData;
            metaData = resultSet.getMetaData();

            int numberOfColumns = metaData.getColumnCount();
            columnNames = new String[numberOfColumns];
            for (int column = 1; column <= numberOfColumns; column++) {
                columnNames[column - 1] = metaData.getColumnName(column);
            }
            rows = new Vector();
            while (resultSet.next()) {
                Vector newRow = new Vector();
                for (int i = 1; i <= getColumnCount(); i++) {
                    if (columnNames[i - 1].equals("aml")) {
                        String aml = resultSet.getString(i);
                        int textStart = aml.indexOf("<TEXT>");
                        int textEnd = aml.indexOf("</TEXT>");
                        String text = aml.substring(textStart + "<TEXT>".length(), textEnd);
                        try {
                            text = text.substring(0, 80);
                        } catch (Exception ignored) {
                        }
                        newRow.addElement(text);
                    } else {
                        newRow.addElement(resultSet.getObject(i));
                    }
                }
                rows.addElement(newRow);
            }
            fireTableChanged(null); // Tell the listeners a new table has arrived.
            // If the rows vector is empty, there were no records in the record set.
            if (rows.size() == 0)
                return;
            setColumnWidths(table); // Have to set column widths *after* table is refreshed.
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            // Attach selection listener for row selections with the mouse.
            ListSelectionModel rowSM = table.getSelectionModel();
            rowSM.addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    //Ignore extra messages.
                    if (e.getValueIsAdjusting()) return;

                    ListSelectionModel lsm =
                            (ListSelectionModel) e.getSource();
                    if (!lsm.isSelectionEmpty()) {
                        int selectedRow = lsm.getMinSelectionIndex();
                        textSearchKey = Integer.parseInt(getValueAt(selectedRow, 0).toString());
                        parent.openFromDB(textSearchKey);
                        parent.updateDisplays(true);
                        parent.updateSelectText();
                    }
                }
            });
        } catch (SQLException ex) {
            System.err.println("In updateTable: " + ex);
        }
    }

    public void setColumnWidths(JTable table) {
        int i = 0;
        try {
            String[] longValues = {"", "MMM", "MMMMM", "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM"};
            TableColumn column;
            for (i = 0; i < getColumnCount(); i++) {
                column = table.getColumnModel().getColumn(i);
                Component comp = table.getDefaultRenderer(String.class).
                        getTableCellRendererComponent(
                                table, longValues[i],
                                false, false, 0, i);
                int cellWidth = comp.getPreferredSize().width;
                column.setPreferredWidth(cellWidth);
            }
        } catch (Exception e) {
            System.err.println("In setColumnWidths: col: " + i + "\n" + e);
        }
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public Class getColumnClass(int c) {
        if (getValueAt(0, c) != null)
            return getValueAt(0, c).getClass();
        return Object.class;
    }

    public String getColumnName(int column) {
        if (columnNames[column] != null) {
            return columnNames[column];
        } else {
            return "";
        }
    }

    public boolean isCellEditable(int row, int col) {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.

        // Don't allow the key value to be edited.
        return false;
    }

    public Object getValueAt(int aRow, int aColumn) {
        Vector row = (Vector) rows.elementAt(aRow);
        return row.elementAt(aColumn);
    }

    /**
     * Called automatically when a cell is edited.
     */
    public void setValueAt(Object value, int aRow, int aColumn) {

        Vector row = (Vector) rows.elementAt(aRow);
        row.setElementAt(value, aColumn);
        fireTableCellUpdated(aRow, aColumn);

        // Update the text fields and the database:
        //parent.updateTextSearchFromTable(this, aRow, aColumn, key);
    }

    public int getRowCount() {
        return rows.size();
    }

}
