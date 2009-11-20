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
import java.util.Collection;

import com.sun.javadoc.PackageDoc;

/**
 * Generator for class and package ratings.
 *
 * @author msp
 */
public class ClassRatingGenerator {
    
    /** prefix for output file names. */
    private static final String FILE_PREFIX = "rating_";
    private static final String TITLE_PREFIX = "Class Rating for ";
    
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
        String outFileName = FILE_PREFIX + projectName + ".html";
        File outFile = new File(destinationPath, outFileName);
        outFile.createNewFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
        String capitProjectName = Character.toUpperCase(projectName.charAt(0))
                 + projectName.substring(1);
        HtmlWriter.writeHeader(writer, TITLE_PREFIX + capitProjectName);
        
        // write code rating for each package and file
        
        // write footer and close
        HtmlWriter.writeFooter(writer);
        writer.flush();
        writer.close();
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
