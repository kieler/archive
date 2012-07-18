/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 *
 * http://www.informatik.uni-kiel.de/rtsys/kieler/
 * 
 * Copyright 2012 by
 * + Christian-Albrechts-University of Kiel
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 * 
 * This code is provided under the terms of the Eclipse Public License (EPL).
 * See the file epl-v10.html for the license text.
 */
package de.cau.cs.kieler.doclets.htmlgen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Map;

import de.cau.cs.kieler.doclets.model.Project;

/**
 * Generates the overview page.
 * 
 * @author cds
 */
public class OverviewHtmlWriter extends BasicHtmlWriter {
    
    /**
     * Generates the overview page.
     * 
     * @param projects map mapping project names to projects.
     * @param destinationFolder folder to place the documentation in.
     * @throws Exception if something bad happens.
     */
    public void generateOverviewPage(final Map<String, Project> projects, final File destinationFolder)
            throws Exception {
        
        // Create the output file
        File outFile = new File(destinationFolder, generateFileName(Categories.RATINGS, null));
        outFile.createNewFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
        
        // Generate the HTML header
        generateHeader(Categories.RATINGS, null, writer);

        // Sort the list of projects and write the overview table
        Project[] projectsArray = projects.values().toArray(new Project[0]);
        Arrays.sort(projectsArray);
        generateSummaryTable(projectsArray, writer);
        
        // Generate the HTML footer
        generateFooter(Categories.RATINGS, null, writer);
        
        // Close the file writer
        writer.flush();
        writer.close();
    }
}
