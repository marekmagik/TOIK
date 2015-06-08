package pl.edu.agh.araucaria.prefs;

import pl.edu.agh.araucaria.Araucaria;
import pl.edu.agh.araucaria.gui.panels.text.SelectText;
import pl.edu.agh.araucaria.enums.DatabaseType;
import pl.edu.agh.araucaria.gui.visualisation.core.DiagramBase;

import java.awt.*;
import java.io.Serializable;

public class PrefParams implements Serializable {
    public Color backgroundColor;  // Background of main tree windows
    public Color backgroundText;   // Background of left-hand text panel
    public Color backgroundStatusBar;   // Background of status bar
    public Color freeVertexBackground;  // Background of free vertex panel
    // Directories
    public String textDirectory, amlDirectory, schemeDirectory;
    public int numRecentTextFiles, numRecentAmlFiles, numRecentSchemeFiles;
    // Databases
    public String ipAddress, databaseName, username, password;
    public DatabaseType databaseType;
    // pl.edu.agh.araucaria.model.Argument display options
    public boolean tutorMode;
    public int imageType;

    public PrefParams() {
        // Colors
        backgroundColor = DiagramBase.DIAGRAM_BACKGROUND;
        freeVertexBackground = DiagramBase.FREE_VERTEX_BACKGROUND;
        backgroundText = SelectText.TEXT_BACKGROUND;
        backgroundStatusBar = Araucaria.STATUSBAR_BACKGROUND;
        // Directories
        textDirectory = Araucaria.textDirectory;
        amlDirectory = Araucaria.amlDirectory;
        schemeDirectory = Araucaria.schemeDirectory;
        numRecentTextFiles = Araucaria.numRecentTextFiles;
        numRecentAmlFiles = Araucaria.numRecentAmlFiles;
        numRecentSchemeFiles = Araucaria.numRecentSchemeFiles;
        // Database
        ipAddress = Araucaria.ipAddress;
        databaseName = Araucaria.databaseName;
        username = Araucaria.username;
        password = Araucaria.password;
        databaseType = Araucaria.databaseType;
        // pl.edu.agh.araucaria.model.Argument display options
        tutorMode = Araucaria.tutorModeOn;
        imageType = Araucaria.imageType;
    }

}
