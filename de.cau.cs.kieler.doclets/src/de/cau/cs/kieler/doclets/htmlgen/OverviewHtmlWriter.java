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

import de.cau.cs.kieler.doclets.model.CodeRating;
import de.cau.cs.kieler.doclets.model.DesignRating;
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
        writeTable(projectsArray, writer);
        
        // Generate the HTML footer
        generateFooter(Categories.RATINGS, null, writer);
        
        // Close the file writer
        writer.flush();
        writer.close();
    }
    
    /**
     * Generates the overview table.
     * 
     * @param projects array of projects, preferrably sorted.
     * @param writer where to write to.
     * @throws Exception if something bad happens
     */
    private void writeTable(final Project[] projects, final BufferedWriter writer) throws Exception {
        // We're generating HTML code; to make things easier, we don't care about long lines.
        // CHECKSTYLEOFF LineLength
        
        // Sums of statistics
        int totalClasses = 0;
        int totalGenerated = 0;
        int totalDesignReviewed = 0;
        int totalDesignProposed = 0;
        int totalCodeRed = 0;
        int totalCodeYellow = 0;
        int totalCodeGreen = 0;
        int totalCodeBlue = 0;
        int totalCodeProposed = 0;
        
        // Header
        writer.write("<table cellspacing='0' cellpadding='6'>");
        writer.write("  <tr class='oddheader'>");
        writer.write("    <th>&nbsp;</th>");
        writer.write("    <th class='newcolgroup'>&nbsp;</th>");
        writer.write("    <th>&nbsp;</th>");
        writer.write("    <th class='multiheader newcolgroup' colspan='3'>Design</th>");
        writer.write("    <th class='multiheader newcolgroup' colspan='6'>Code</th>");
        writer.write("  </tr>");
        writer.write("  <tr class='oddheader headerlinebottom'>");
        writer.write("    <th>Project</th>");
        writer.write("    <th class='numbercell newcolgroup'>Classes</th>");
        writer.write("    <th class='numbercell'>Generated</th>");
        writer.write("    <th class='numbercell newcolgroup'>Reviewed</th>");
        writer.write("    <th class='numbercell'>Proposed</th>");
        writer.write("    <th>Progress</th>");
        writer.write("    <th class='numbercell newcolgroup'><img src='http://rtsys.informatik.uni-kiel.de/fisheye/browse/~raw,r=HEAD/kieler/standalone/de.cau.cs.kieler.doclets/icons/red.png?format=raw' alt='red' /></th>");
        writer.write("    <th class='numbercell'><img src='http://rtsys.informatik.uni-kiel.de/fisheye/browse/~raw,r=HEAD/kieler/standalone/de.cau.cs.kieler.doclets/icons/yellow.png?format=raw' alt='yellow' /></th>");
        writer.write("    <th class='numbercell'><img src='http://rtsys.informatik.uni-kiel.de/fisheye/browse/~raw,r=HEAD/kieler/standalone/de.cau.cs.kieler.doclets/icons/green.png?format=raw' alt='green' /></th>");
        writer.write("    <th class='numbercell'><img src='http://rtsys.informatik.uni-kiel.de/fisheye/browse/~raw,r=HEAD/kieler/standalone/de.cau.cs.kieler.doclets/icons/blue.png?format=raw' alt='blue' /></th>");
        writer.write("    <th class='numbercell'>Proposed</th>");
        writer.write("    <th>Progress</th>");
        writer.write("  </tr>");
        
        // Iterate through projects (i will be used again later and is thus declared outside the loop)
        int i = 0;
        for (; i < projects.length; i++) {
            Project project = projects[i];
            
            // New table row; determine the class
            writer.write("<tr class='");
            writer.write(i % 2 == 0 ? "even" : "odd");
            writer.write(i < projects.length - 1 ? " linebottom" : "");
            writer.write("'>");
            
            // Data! DATA!
            writer.write("<td><a href='" + generateFileName(Categories.RATINGS, project) + "'>");
            writer.write("<img src='file:///home/cds/Programming/Kieler/git/cdline/standalone/de.cau.cs.kieler.doclets/icons/type_project.png' /> ");
            writer.write(project.getName() + "</a></td>");
            
            // Classes and Generated
            totalClasses += project.getStatsClasses();
            writer.write("<td class='numbercell newcolgroup'>" + project.getStatsClasses() + "</td>");

            totalGenerated += project.getStatsGenerated();
            writer.write("<td class='numbercell'>" + project.getStatsGenerated() + "</td>");
            
            // Design Ratings
            int[] statsDesign = project.getStatsDesign();
            totalDesignReviewed += statsDesign[DesignRating.REVIEWED.ordinal()];
            writer.write("<td class='numbercell newcolgroup'>" + statsDesign[DesignRating.REVIEWED.ordinal()] + "</td>");

            totalDesignProposed += statsDesign[DesignRating.PROPOSED.ordinal()];
            writer.write("<td class='numbercell'>" + statsDesign[DesignRating.PROPOSED.ordinal()] + "</td>");
            
            writer.write("<td>TODO: Graph</td>");
            
            // Code Ratings
            int[] statsCode = project.getStatsCode();
            totalCodeRed += statsCode[CodeRating.RED.ordinal()] + statsCode[CodeRating.PROP_YELLOW.ordinal()];
            writer.write("<td class='numbercell newcolgroup'>" + (statsCode[CodeRating.RED.ordinal()] + statsCode[CodeRating.PROP_YELLOW.ordinal()]) + "</td>");

            totalCodeYellow += statsCode[CodeRating.YELLOW.ordinal()] + statsCode[CodeRating.PROP_GREEN.ordinal()];
            writer.write("<td class='numbercell'>" + (statsCode[CodeRating.YELLOW.ordinal()] + statsCode[CodeRating.PROP_GREEN.ordinal()]) + "</td>");

            totalCodeGreen += statsCode[CodeRating.GREEN.ordinal()] + statsCode[CodeRating.PROP_BLUE.ordinal()];
            writer.write("<td class='numbercell'>" + (statsCode[CodeRating.GREEN.ordinal()] + statsCode[CodeRating.PROP_BLUE.ordinal()]) + "</td>");

            totalCodeBlue += statsCode[CodeRating.BLUE.ordinal()];
            writer.write("<td class='numbercell'>" + statsCode[CodeRating.BLUE.ordinal()] + "</td>");
            
            int proposed = statsCode[CodeRating.PROP_YELLOW.ordinal()]
                    + statsCode[CodeRating.PROP_GREEN.ordinal()]
                    + statsCode[CodeRating.PROP_BLUE.ordinal()];
            totalCodeProposed += proposed;
            writer.write("<td class='numbercell'>" + proposed + "</td>");

            writer.write("<td>TODO: Graph</td>");
            
            // End table row
            writer.write("</tr>");
        }
        
        // Summary table row
        writer.write("<tr class='headerlinetop ");
        writer.write(i % 2 == 0 ? "evenheader" : "oddheader");
        writer.write("'>");
        
        writer.write("<th>Total</th>");
        writer.write("<th class='numbercell newcolgroup'>" + totalClasses + "</th>");
        writer.write("<th class='numbercell'>" + totalGenerated + "</th>");
        writer.write("<th class='numbercell newcolgroup'>" + totalDesignReviewed + "</th>");
        writer.write("<th class='numbercell'>" + totalDesignProposed + "</th>");
        writer.write("<th>TODO: Graph</th>");
        writer.write("<th class='numbercell newcolgroup'>" + totalCodeRed + "</th>");
        writer.write("<th class='numbercell'>" + totalCodeYellow + "</th>");
        writer.write("<th class='numbercell'>" + totalCodeGreen + "</th>");
        writer.write("<th class='numbercell'>" + totalCodeBlue + "</th>");
        writer.write("<th class='numbercell'>" + totalCodeProposed + "</th>");
        writer.write("<th>TODO: Graph</th>");
        
        // Footer
        writer.write("</table>");

        // CHECKSTYLEON LineLength
    }
}
