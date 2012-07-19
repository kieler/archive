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

import de.cau.cs.kieler.doclets.model.Plugin;
import de.cau.cs.kieler.doclets.model.Project;

/**
 * Generates the various project pages.
 * 
 * @author cds
 */
public class ProjectHtmlWriter extends BasicHtmlWriter {
    
    /**
     * Generates the project pages.
     * 
     * @param projects map mapping project names to projects.
     * @param destinationFolder folder to place the documentation in.
     * @throws Exception if something bad happens.
     */
    public void generateProjectPages(final Map<String, Project> projects, final File destinationFolder)
            throws Exception {

        // Sort the list of projects
        Project[] projectsArray = projects.values().toArray(new Project[0]);
        Arrays.sort(projectsArray);
        
        // Iterate over the list of projects, generating an HTML page for each
        for (int i = 0; i < projectsArray.length; i++) {
            // Create the output file
            File outFile = new File(
                    destinationFolder,
                    generateFileName(Categories.RATINGS, projectsArray[i]));
            outFile.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
            
            // Assemble the list of plug-ins
            Plugin[] pluginsArray = projectsArray[i].getPlugins().values().toArray(new Plugin[0]);
            Arrays.sort(pluginsArray);
            
            // Generate the HTML code
            generateHeader(Categories.RATINGS, projectsArray[i], writer);
            generateSummaryTable(projectsArray[i], pluginsArray, writer);
            generateFooter(Categories.RATINGS, projectsArray[i], writer);
            
            // Close the file writer
            writer.flush();
            writer.close();
        }
    }
}
