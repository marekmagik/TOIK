package pl.edu.agh.araucaria;/*
 * ControlFrame.java
 *
 * Created on 13 April 2004, 09:20
 */

import pl.edu.agh.araucaria.history.UndoStack;

/**
 * @author growe
 */

public interface ControlFrame {

    void setMessageLabelText(String text);

    void updateDisplays(boolean updateCurrent);

    UndoStack getUndoStack();

    void doRedo();

    void doUndo();
}
