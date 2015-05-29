package pl.edu.agh.araucaria.history;/*
 * pl.edu.agh.araucaria.history.HiddenInfo.java
 *
 * Created on 16 August 2004, 11:45
 */

/**
 * Utility class for retaining the hidden/display info during undo/redo
 * @author growe
 */

import java.util.Hashtable;

public class HiddenInfo {
    private Hashtable isHiddenTable;
    private String shortID;

    /**
     * Creates a new instance of pl.edu.agh.araucaria.history.HiddenInfo
     */
    public HiddenInfo(Hashtable h, String s) {
        isHiddenTable = h;
        shortID = s;
    }

    public Hashtable getIsHiddenTable() {
        return isHiddenTable;
    }

    public String getShortID() {
        return shortID;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(shortID);
        builder.append(": ");
        if (isHiddenTable != null)
            builder.append(isHiddenTable.toString());
        return builder.toString();
    }

}
