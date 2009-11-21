/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 *
 * http://www.informatik.uni-kiel.de/rtsys/kieler/
 * 
 * Copyright 2009 by
 * + Christian-Albrechts-University of Kiel
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 * 
 * This code is provided under the terms of the Eclipse Public License (EPL).
 * See the file epl-v10.html for the license text.
 */
package de.cau.cs.kieler.doclets;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.StringTokenizer;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.Tag;

/**
 * Generator for class and package ratings.
 *
 * @author msp
 */
public class ClassRatingGenerator {
    
    /** prefix for output file names. */
    private static final String FILE_PREFIX = "rating_";
    /** prefix for page title. */
    private static final String TITLE_PREFIX = "Class Rating for ";
    /** tag for class ratings. */
    private static final String RATING_TAG = "@kieler.rating";
    /** the path to icon files. */
    private static final String ICON_PATH = "http://rtsys.informatik.uni-kiel.de/trac/kieler/browser/trunk/standalone/de.cau.cs.kieler.taglets/icons/";
    
    /** enumeration of ratings. */
    private enum Rating {
        RED, YELLOW, GREEN, BLUE
    }
    
    /** path to the destination folder. */
    private String destinationPath;
    
    /**
     * Generate output document for the given project and its contained packages.
     * 
     * @param projectName name of the project
     * @param containedPackages packages in the project
     * @throws IOException if writing output fails
     */
    public void generate(final String projectName, final Collection<PackageDoc> containedPackages)
            throws IOException {
        // create output file and write header
        File outFile = new File(destinationPath, getFileName(projectName));
        outFile.createNewFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
        String capitProjectName = Character.toUpperCase(projectName.charAt(0))
                + projectName.substring(1);
        HtmlWriter.writeHeader(writer, TITLE_PREFIX + capitProjectName);
        
        // write code rating for each package and file
        PackageDoc[] packages = containedPackages.toArray(new PackageDoc[containedPackages.size()]);
        Arrays.sort(packages);
        for (PackageDoc packageDoc : packages) {
            writer.write("<p><b>" + packageDoc.name() + "</b><br>\n<table>\n");
            for (ClassDoc classDoc : packageDoc.allClasses()) {
                if (classDoc.isInterface()) {
                    writer.write("<tr><td><i>" + classDoc.typeName() + "</i></td>");
                } else {
                    writer.write("<tr><td>" + classDoc.typeName() + "</td>");
                }
                writeClassRating(writer, classDoc.tags(RATING_TAG));
                writer.write("</tr>\n");
            }
            writer.write("</table></p>\n");
        }
        
        // write footer and close
        HtmlWriter.writeFooter(writer);
        writer.flush();
        writer.close();
    }
    
    /**
     * Generates the file name for the given project.
     * 
     * @param projectName project name
     * @return file name for the project's class rating
     */
    public String getFileName(final String projectName) {
        return FILE_PREFIX + projectName + ".html";
    }
    
    /** result value subtracted for proposed ratings. */
    private static final float PROPOSED_VAL = 0.5f;
    
    /**
     * Writes the class rating for the given rating tags.
     * 
     * @param writer writer to which output is written
     * @param tagArray array of rating tags
     * @throws IOException if writing output fails
     */
    private void writeClassRating(final BufferedWriter writer, final Tag[] tagArray) throws IOException {
        // find the tag with highest rating
        int maxIndex = -1;
        float maxValue = 0;
        for (int i = 0; i < tagArray.length; i++) {
            if (tagArray[i].text() != null) {
                StringTokenizer tokenizer = new StringTokenizer(tagArray[i].text(), " \t\n\r");
                boolean proposed = false;
                while (tokenizer.hasMoreTokens()) {
                    String token = tokenizer.nextToken();
                    if ("proposed".equals(token.toLowerCase())) {
                        proposed = true;
                    } else {
                        try {
                            Rating rating = Rating.valueOf(token.toUpperCase());
                            float value = rating.ordinal() - (proposed ? PROPOSED_VAL : 0.0f);
                            if (value > maxValue) {
                                maxValue = value;
                                maxIndex = i;
                            }
                            break;
                        } catch (IllegalArgumentException exception) {
                            // ignore exception
                        }
                    }
                }
            }
        }
        // write rating for the highest rating value
        if (maxIndex >= 0) {
            String date = null;
            boolean proposed = false;
            Rating rating = null;
            StringTokenizer tokenizer = new StringTokenizer(tagArray[maxIndex].text(), " \t\n\r");
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                if ("proposed".equals(token.toLowerCase())) {
                    proposed = true;
                } else {
                    try {
                        rating = Rating.valueOf(token.toUpperCase());
                        break;
                    } catch (IllegalArgumentException exception) {
                        if (date == null) {
                            date = token;
                        }
                    }
                }
            }
            writeClassRating(writer, proposed, rating, date);
        } else {
            writeClassRating(writer, false, Rating.RED, null);
        }
    }
    
    /**
     * Writes class rating values.
     * 
     * @param writer writer to which output is written
     * @param proposed true if the rating is proposed
     * @param rating the rating
     * @param date date when the rating was decided, or {@code null}
     * @throws IOException if writing output fails
     */
    private void writeClassRating(final BufferedWriter writer, final boolean proposed,
            final Rating rating, final String date) throws IOException {
        writer.write("<td><img src=\"" + ICON_PATH);
        if (proposed && rating != Rating.RED) {
            writer.write("prop_");
        }
        writer.write(rating.toString().toLowerCase() + ".png?format=raw\" alt=\""
                + rating.toString() + "\"></b></dt><dd>");
        if (date != null) {
            writer.write("<td>" + date + "</td>");
        } else {
            writer.write("<td></td>");
        }
    }
    
    /**
     * Sets the destination path.
     *
     * @param thedestinationPath the destination path to set
     */
    public void setDestinationPath(final String thedestinationPath) {
        this.destinationPath = thedestinationPath;
    }

}
