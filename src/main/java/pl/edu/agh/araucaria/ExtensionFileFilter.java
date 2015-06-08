package pl.edu.agh.araucaria;/*
 * @(#)pl.edu.agh.araucaria.ExtensionFileFilter.java	1.8 98/08/26
 *
 * Copyright 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */


import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * A convenience implementation of FileFilter that filters out
 * all files except for those type extensions that it knows about.
 * <p/>
 * Extensions are of the type ".foo", which is typically found on
 * Windows and Unix boxes, but not on Macinthosh. Case is ignored.
 * <p/>
 * Example - create a new filter that filerts out all files
 * but gif and jpg image files:
 * <p/>
 * JFileChooser chooser = new JFileChooser();
 * pl.edu.agh.araucaria.ExtensionFileFilter filter = new pl.edu.agh.araucaria.ExtensionFileFilter(
 * new String{"gif", "jpg"}, "JPEG & GIF Images")
 * chooser.addChoosableFileFilter(filter);
 * chooser.showOpenDialog(this);
 *
 * @author Jeff Dinkins
 * @version 1.8 08/26/98
 */
public class ExtensionFileFilter extends FileFilter {

    private Hashtable<String, ExtensionFileFilter> filters = new Hashtable<>();
    private String description = null;
    private String fullDescription = null;

    /**
     * Creates a file filter. If no filters are added, then all
     * files are accepted.
     *
     * @see #addExtension
     */
    public ExtensionFileFilter() {
    }

    /**
     * Creates a file filter that accepts the given file type.
     * Example: new pl.edu.agh.araucaria.ExtensionFileFilter("jpg", "JPEG Image Images");
     * <p/>
     * Note that the "." before the extension is not needed. If
     * provided, it will be ignored.
     *
     * @see #addExtension
     */
    public ExtensionFileFilter(String extension, String description) {
        if (extension != null) {
            addExtension(extension);
        }
        if (description != null) {
            setDescription(description);
        }
    }

    /**
     * Return true if this file should be shown in the directory pane,
     * false if it shouldn't.
     * <p/>
     * Files that begin with "." are ignored.
     *
     * @see #getExtension
     * //* @see FileFilter#accepts
     */
    @Override
    public boolean accept(File f) {
        if (f != null) {
            if (f.isDirectory()) {
                return true;
            }
            String extension = getExtension(f);
            if (extension != null && filters.get(getExtension(f)) != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return the extension portion of the file's name .
     *
     * @see #getExtension
     * @see FileFilter#accept
     */
    private String getExtension(File f) {
        if (f != null) {
            String filename = f.getName();
            int i = filename.lastIndexOf('.');
            if (i > 0 && i < filename.length() - 1) {
                return filename.substring(i + 1).toLowerCase();
            }
        }
        return null;
    }

    /**
     * Adds a filetype "dot" extension to filter against.
     * <p/>
     * For example: the following code will create a filter that filters
     * out all files except those that end in ".jpg" and ".tif":
     * <p/>
     * pl.edu.agh.araucaria.ExtensionFileFilter filter = new pl.edu.agh.araucaria.ExtensionFileFilter();
     * filter.addExtension("jpg");
     * filter.addExtension("tif");
     * <p/>
     * Note that the "." before the extension is not needed and will be ignored.
     */
    public void addExtension(String extension) {
        filters.put(extension.toLowerCase(), this);
        fullDescription = null;
    }


    /**
     * Returns the human readable description of this filter. For
     * example: "JPEG and GIF Image Files (*.jpg, *.gif)"
     *
     * @see FileFilter#getDescription
     */
    @Override
    public String getDescription() {
        if (fullDescription == null) {
            fullDescription = description == null ? "(" : description + " (";
            // build the description from the extension list

            StringBuilder descriptionBuilder = new StringBuilder(fullDescription);

            Enumeration extensions = filters.keys();
            if (extensions != null) {
                descriptionBuilder.append(".").append(extensions.nextElement());
                while (extensions.hasMoreElements()) {
                    descriptionBuilder.append(", ").append(extensions.nextElement());
                }
            }
            descriptionBuilder.append(")");
            fullDescription = descriptionBuilder.toString();
        }
        return fullDescription;
    }

    /**
     * Sets the human readable description of this filter. For
     * example: filter.setDescription("Gif and JPG Images");
     */
    private void setDescription(String description) {
        this.description = description;
        fullDescription = null;
    }
}
