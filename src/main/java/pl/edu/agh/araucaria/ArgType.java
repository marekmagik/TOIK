package pl.edu.agh.araucaria;

import java.io.Serializable;
import java.util.Vector;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      University of Dundee
 *
 * @author mm
 * @version 1.0
 */

public class ArgType implements Serializable {

    private String name = "";

    private String conclusion = "";

    private Vector premises = new Vector();

    private Vector criticalQuestions = new Vector();


    public Vector getCriticalQuestions() {
        return criticalQuestions;
    }

    public Vector getPremises() {
        return premises;
    }

    public String getConclusion() {
        return conclusion;
    }

    public void setConclusion(String conc) {
        conclusion = conc;
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        name = newName;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("Name: ")
                .append(name)
                .append("\n");

        if (premises.size() > 0) {
            builder.append("Premises\n");
            for (int i = 0; i < premises.size(); i++)
                builder.append(premises.elementAt(i)).append("\n");
        }

        builder.append("Conclusion: ").append(conclusion).append("\n");

        if (criticalQuestions.size() > 0) {
            builder.append("Critical questions:\n");
            for (int i = 0; i < criticalQuestions.size(); i++)
                builder.append((String) criticalQuestions.elementAt(i)).append("\n");
        }

        return builder.toString();
    }
}