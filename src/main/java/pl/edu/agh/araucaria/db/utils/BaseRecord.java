package pl.edu.agh.araucaria.db.utils;

import java.util.StringTokenizer;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 *
 * @author
 * @version 1.0
 */

public class BaseRecord {

    /**
     * SQL strings cannot contain single quotes. Each '
     * must be replaced by a double quote ''
     */
    public static String escapeQuotes(String s) {
        if (s == null) {
            return null;
        }
        StringTokenizer tokens = new StringTokenizer(s, "'", true);

        StringBuilder esc = new StringBuilder();
        while (tokens.hasMoreTokens()) {
            String tok = tokens.nextToken();
            esc.append(tok);
            if (tok.equals("'")) {
                esc.append("'");
            }
        }
        return esc.toString();
    }
}