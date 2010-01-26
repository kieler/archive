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
import java.io.Writer;
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

    /** tag for class ratings. */
    public static final String RATING_TAG = "@kieler.rating";
    /** tag for generated code. */
    public static final String GENERATED_TAG = "@generated";
    // CHECKSTYLEOFF LineLength
    /** the path to rating icon files. */
    public static final String RATING_ICON_PATH = "http://rtsys.informatik.uni-kiel.de/trac/kieler/browser/trunk/standalone/de.cau.cs.kieler.taglets/icons/";
    /** the path to class icon files. */
    public static final String CLASS_ICON_PATH = "http://rtsys.informatik.uni-kiel.de/trac/kieler/browser/trunk/standalone/de.cau.cs.kieler.doclets/icons/";
    // CHECKSTYLEON LineLength

    /** prefix for output file names. */
    private static final String FILE_PREFIX = "rating_";
    /** prefix for page title. */
    private static final String TITLE_PREFIX = "Class Rating for ";
    /** path, relative or absolute, to the API files. */
    private static final String API_PATH = "..";
    /** source folder for generated classes. */
    private static final String GEN_FOLDER = "src-gen";
    
    /** enumeration of ratings. */
    public enum Rating {
        /** rating red. */
        RED,
        /** rating proposed yellow. */
        PROP_YELLOW,
        /** rating yellow. */
        YELLOW,
        /** rating proposed green. */
        PROP_GREEN,
        /** rating green. */
        GREEN,
        /** rating proposed blue. */
        PROP_BLUE,
        /** rating blue. */
        BLUE;
        
        /**
         * Returns whether this rating is a proposed rating.
         * 
         * @return true if this rating is proposed
         */
        public boolean isProposed() {
            return ordinal() % 2 == 1;
        }
        
        /**
         * Returns a modified rating that is one level lower than this rating.
         * 
         * @return a lower rating
         */
        public Rating getDegraded() {
            int ordinal = ordinal();
            if (ordinal > 0) {
                return values()[ordinal - 1];
            } else {
                return this;
            }
        }
    }
    
    /** path to the destination folder. */
    private String destinationPath;
    /** number of rated classes for each rating, including proposed. */
    private int[] ratingCounts;
    /** total number of rated classes. */
    private int ratedClasses;
    
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
        writer.write("<table>\n");
        ratingCounts = new int[Rating.values().length];
        ratedClasses = 0;
        PackageDoc[] packages = containedPackages.toArray(new PackageDoc[containedPackages.size()]);
        Arrays.sort(packages);
        for (PackageDoc packageDoc : packages) {
            writer.write("<tr><td colspan=4><b><a href=\"" + API_PATH
                    + "/" + packageDoc.name().replace('.', '/') + "/package-summary.html\">"
                    + packageDoc.name() + "</a></b></td></tr>\n");
            ClassDoc[] classes = packageDoc.allClasses();
            Arrays.sort(classes);
            for (ClassDoc classDoc : classes) {
                // don't create rating for nested classes and generated classes
                if (classDoc.containingClass() == null && !isGenerated(classDoc)) {
                    writer.write("<tr>");
                    if (classDoc.isInterface()) {
                        writer.write("<td><img src=\"" + CLASS_ICON_PATH
                                + "interface.png?format=raw\"></td><td><i>"
                                + classDoc.typeName() + "</i></td>");
                    } else if (classDoc.isAbstract()) {
                        writer.write("<td><img src=\"" + CLASS_ICON_PATH
                                + "class.png?format=raw\"></td><td><i>"
                                + classDoc.typeName() + "</i></td>");
                    } else {
                        writer.write("<td><img src=\"" + CLASS_ICON_PATH
                                + "class.png?format=raw\"></td><td>"
                                + classDoc.typeName() + "</td>");
                    }
                    Rating rating = writeClassRating(writer, classDoc.tags(RATING_TAG));
                    writer.write("</tr>\n");
                    ratingCounts[rating.ordinal()]++;
                    ratedClasses++;
                }
            }
        }
        writer.write("</table>\n");
        
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
     * @return the rating for the class
     * @throws IOException if writing output fails
     */
    private Rating writeClassRating(final Writer writer, final Tag[] tagArray)
            throws IOException {
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
            if (proposed && rating == Rating.RED) {
                proposed = false;
            }
            writeClassRating(writer, proposed, rating, date);
            if (proposed) {
                return rating.getDegraded();
            } else {
                return rating;
            }
        } else {
            writeClassRating(writer, false, Rating.RED, null);
            return Rating.RED;
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
    private void writeClassRating(final Writer writer, final boolean proposed,
            final Rating rating, final String date) throws IOException {
        String ratingName = rating.toString().toLowerCase();
        writer.write("<td><img src=\"" + RATING_ICON_PATH);
        if (proposed) {
            writer.write("prop_");
        }
        writer.write(ratingName + ".png?format=raw\" alt=\"");
        if (proposed) {
            writer.write("proposed ");
        }
        writer.write(ratingName + "\"></td>");
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

    /**
     * Returns the number of classes for each type of rating. The last
     * entry is the total number of proposed ratings.
     *
     * @return the rating counts
     */
    public int[] getRatingCounts() {
        return ratingCounts;
    }

    /**
     * Returns the total number of rated classes.
     *
     * @return the rated classes
     */
    public int getRatedClasses() {
        return ratedClasses;
    }
    
    /**
     * Checks whether the given class is generated.
     * 
     * @param classDoc a class doc
     * @return true if the corresponding class is automatically generated
     */
    public static boolean isGenerated(final ClassDoc classDoc) {
        String path = classDoc.position().file().getPath();
        return classDoc.tags(ClassRatingGenerator.GENERATED_TAG).length > 0
            || path.indexOf(GEN_FOLDER) >= 0;
    }

}
