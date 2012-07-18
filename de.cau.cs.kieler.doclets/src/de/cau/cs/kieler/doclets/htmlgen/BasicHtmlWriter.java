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
import java.util.Date;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;

import de.cau.cs.kieler.doclets.RatingDocletConstants;
import de.cau.cs.kieler.doclets.model.AbstractThingWithStatistics;
import de.cau.cs.kieler.doclets.model.ClassItem;
import de.cau.cs.kieler.doclets.model.CodeRating;
import de.cau.cs.kieler.doclets.model.DesignRating;
import de.cau.cs.kieler.doclets.model.Plugin;
import de.cau.cs.kieler.doclets.model.Project;

/**
 * Base class of all Html writers. Containts a whole lot of utility methods.
 * 
 * @author cds
 * @generated
 * @kieler.design bla bla bla yadda yadda yadda
 */
public class BasicHtmlWriter {
    
    /////////////////////////////////////////////////////////////////////////////
    // HTML HEADERS AND FOOTERS
    
    /**
     * Generates the HTML header.
     * 
     * @param category category the current site falls into.
     * @param currentPage the current page, used to generate breadcrumbs.
     * @param writer where to write to.
     * @throws Exception if anything bad happens.
     */
    protected void generateHeader(final Categories category, final Object currentPage,
            final BufferedWriter writer) throws Exception {
        
        // We will later support more categories, but for now we only have one.
        switch (category) {
        case RATINGS:
            generateRatingsHeader(currentPage, writer);
            break;
        
        default:
            break;
        }
    }

    /**
     * Generates the HTML header for a page in the ratings category.
     * 
     * @param category category the current site falls into.
     * @param currentPage the current page, used to generate breadcrumbs.
     * @param writer where to write to.
     * @throws Exception if anything bad happens.
     */
    private void generateRatingsHeader(final Object currentPage, final BufferedWriter writer)
            throws Exception {

        // We're generating HTML code; to make things easier, we don't care about long lines.
        // CHECKSTYLEOFF LineLength
        
        writer.write("<!DOCTYPE doctype PUBLIC '-//w3c//dtd html 4.0 transitional//en'><html>");
        writer.write("<head>");
        writer.write("<title>" + RatingDocletConstants.TXT_TITLE + "</title>");
        writer.write("<link rel='stylesheet' type='text/css' href='style.css'>");
        
        writer.write("</head><body>");
        writer.write("<h1>" + RatingDocletConstants.TXT_TITLE + "</h1>");
        
        writer.write("<div class='navbar'>");
        
        writer.write("<div class='breadcrumbs'>");
        writer.write(generateRatingsBreadcrumbs(currentPage));
        writer.write("</div>");
        
        writer.write("<div class='categories'><ul>");
        writer.write("<li><a class='active' href='" + generateFileName(Categories.RATINGS, null) + "'>" + RatingDocletConstants.TXT_RATINGS + "</a></li> ");
        writer.write("<li><a href='" + generateFileName(Categories.WALLBOARD, null) + "'>" + RatingDocletConstants.TXT_WALLBOARD + "</a></li> ");
        writer.write("<li><a href='" + generateFileName(Categories.TOP_TEN, null) + "'>" + RatingDocletConstants.TXT_TOP_TEN + "</a></li> ");
        writer.write("</ul></div>");
        
        writer.write("</div>");
        
        // CHECKSTYLEON LineLength
    }

    /**
     * Generates the HTML footer.
     * 
     * @param category category the current site falls into.
     * @param currentPage the current page.
     * @param writer where to write to.
     * @throws Exception if anything bad happens.
     */
    protected void generateFooter(final Categories category, final Object currentPage,
            final BufferedWriter writer) throws Exception {
        
        // We will later support more categories, but for now we only have one.
        switch (category) {
        case RATINGS:
            generateRatingsFooter(currentPage, writer);
            break;
        
        default:
            break;
        }
    }

    /**
     * Generates the HTML footer for a page in the ratings category.
     * 
     * @param category category the current site falls into.
     * @param currentPage the current page, used to generate breadcrumbs.
     * @param writer where to write to.
     * @throws Exception if anything bad happens.
     */
    private void generateRatingsFooter(final Object currentPage, final BufferedWriter writer)
            throws Exception {
        
        writer.write("<p class='timestamp'>" + new Date().toString() + "</p>");
        writer.write("</body></html>");
    }

    
    /////////////////////////////////////////////////////////////////////////////
    // BREADCRUMB BUILDING
    
    /**
     * Generates a breadcrumb bar for the page belonging to the given object.
     * 
     * @param currentPageObject object of the current page. This can either be {@code null} if the
     *                          current page is the overview page, or a project, or a plugin.
     * @param writer where to write to.
     * @return string representation of the breadcrumb bar.
     */
    private String generateRatingsBreadcrumbs(final Object currentPageObject) {
        StringBuilder crumbs = new StringBuilder();
        Project project = null;
        Plugin plugin = null;
        
        if (currentPageObject instanceof Project) {
            project = (Project) currentPageObject;
        } else if (currentPageObject instanceof Plugin) {
            plugin = (Plugin) currentPageObject;
            project = plugin.getProject();
        }
        
        crumbs.append("<a href='./index.html'>Overview</a>");
        
        if (project != null) {
            crumbs.append(" &rarr; ");
            crumbs.append("<a href='" + generateFileName(Categories.RATINGS, project)
                    + "'>Project &quot;" + project.getName() + "&quot;</a>");
            
            if (plugin != null) {
                crumbs.append(" &rarr; ");
                crumbs.append("<a href='" + generateFileName(Categories.RATINGS, plugin)
                        + "'>Plugin &quot;" + plugin.getName() + "&quot;</a>");
            }
        }
        
        return crumbs.toString();
    }

    
    /////////////////////////////////////////////////////////////////////////////
    // STATISTICS TABLE BUILDING
    
    /**
     * Generates a statistics table for the given array of items.
     * 
     * @param items array of items in the order they are to appear in the generated table.
     * @param writer where to write to.
     * @throws Exception if something bad happens
     */
    protected void generateSummaryTable(final AbstractThingWithStatistics[] items,
            final BufferedWriter writer) throws Exception {
        
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
        for (; i < items.length; i++) {
            AbstractThingWithStatistics item = items[i];
            
            // New table row; determine the class
            writer.write("<tr class='");
            writer.write(i % 2 == 0 ? "even" : "odd");
            writer.write(i < items.length - 1 ? " linebottom" : "");
            writer.write("'>");
            
            // Data! DATA!
            writer.write("<td><a href='" + generateFileName(Categories.RATINGS, item) + "'>");
            writer.write("<img src='" + getIconForThing(item) + "' /> ");
            writer.write(item.getName() + "</a></td>");
            
            // Classes and Generated
            totalClasses += item.getStatsClasses();
            writer.write("<td class='numbercell newcolgroup'>" + item.getStatsClasses() + "</td>");

            totalGenerated += item.getStatsGenerated();
            writer.write("<td class='numbercell'>" + item.getStatsGenerated() + "</td>");
            
            // Design Ratings
            int[] statsDesign = item.getStatsDesign();
            totalDesignReviewed += statsDesign[DesignRating.REVIEWED.ordinal()];
            writer.write("<td class='numbercell newcolgroup'>" + statsDesign[DesignRating.REVIEWED.ordinal()] + "</td>");

            totalDesignProposed += statsDesign[DesignRating.PROPOSED.ordinal()];
            writer.write("<td class='numbercell'>" + statsDesign[DesignRating.PROPOSED.ordinal()] + "</td>");
            
            writer.write("<td>TODO: Graph</td>");
            
            // Code Ratings
            int[] statsCode = item.getStatsCode();
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
    
    /**
     * Returns the proper icon URL for the given thing.
     * 
     * @param thing the thing to return the icon for. Either a {@code Project}, a {@code Plugin},
     *              a {@code ClassItem}, a {@code PackageDoc}, a {@code CodeRating}, or a
     *              {@code DesignRating}.
     * @return the URL of a proper icon, or the empty string if the thing was of an unexpected type.
     */
    protected String getIconForThing(final Object thing) {
        if (thing instanceof Project) {
            return "file:///home/cds/Programming/Kieler/git/cdline/standalone/de.cau.cs.kieler.doclets/icons/type_project.png";
        } else if (thing instanceof Plugin) {
            return "file:///home/cds/Programming/Kieler/git/cdline/standalone/de.cau.cs.kieler.doclets/icons/type_plugin.png";
        } else if (thing instanceof ClassItem) {
            // There's different finds of classes...
            ClassDoc classDoc = ((ClassItem) thing).getClassDoc();
            
            if (classDoc.isEnum()) {
                return "file:///home/cds/Programming/Kieler/git/cdline/standalone/de.cau.cs.kieler.doclets/icons/type_enum.png";
            } else if (classDoc.isInterface()) {
                return "file:///home/cds/Programming/Kieler/git/cdline/standalone/de.cau.cs.kieler.doclets/icons/type_interface.png";
            } else {
                return "file:///home/cds/Programming/Kieler/git/cdline/standalone/de.cau.cs.kieler.doclets/icons/type_class.png";
            }
        } else if (thing instanceof PackageDoc) {
            return "file:///home/cds/Programming/Kieler/git/cdline/standalone/de.cau.cs.kieler.doclets/icons/type_package.png";
        } else {
            return "";
        }
    }
    
    /**
     * Returns the proper icon URL for the given code rating.
     * 
     * @param rating the rating to return the icon for.
     * @return the URL of a proper icon.
     */
    protected String getIconForCodeRating(final CodeRating rating) {
        if (rating == null) {
            return "file:///home/cds/Programming/Kieler/git/cdline/standalone/de.cau.cs.kieler.doclets/icons/code_red.png";
        }
        
        switch (rating) {
        case RED:
            return "file:///home/cds/Programming/Kieler/git/cdline/standalone/de.cau.cs.kieler.doclets/icons/code_red.png";
        
        case PROP_YELLOW:
            return "file:///home/cds/Programming/Kieler/git/cdline/standalone/de.cau.cs.kieler.doclets/icons/code_yellow_prop.png";
        
        case YELLOW:
            return "file:///home/cds/Programming/Kieler/git/cdline/standalone/de.cau.cs.kieler.doclets/icons/code_yellow.png";
            
        case PROP_GREEN:
            return "file:///home/cds/Programming/Kieler/git/cdline/standalone/de.cau.cs.kieler.doclets/icons/code_green_prop.png";
        
        case GREEN:
            return "file:///home/cds/Programming/Kieler/git/cdline/standalone/de.cau.cs.kieler.doclets/icons/code_green.png";
            
        case PROP_BLUE:
            return "file:///home/cds/Programming/Kieler/git/cdline/standalone/de.cau.cs.kieler.doclets/icons/code_blue_prop.png";
        
        case BLUE:
            return "file:///home/cds/Programming/Kieler/git/cdline/standalone/de.cau.cs.kieler.doclets/icons/code_blue.png";
        
        default:
            return "";
        }
    }
    
    /**
     * Returns the proper icon URL for the given design rating.
     * 
     * @param rating the rating to return the icon for.
     * @return the URL of a proper icon.
     */
    protected String getIconForDesignRating(final DesignRating rating) {
        if (rating == null) {
            return "file:///home/cds/Programming/Kieler/git/cdline/standalone/de.cau.cs.kieler.doclets/icons/design_no.png";
        }
        
        switch (rating) {
        case NONE:
            return "file:///home/cds/Programming/Kieler/git/cdline/standalone/de.cau.cs.kieler.doclets/icons/design_no.png";
            
        case PROPOSED:
            return "file:///home/cds/Programming/Kieler/git/cdline/standalone/de.cau.cs.kieler.doclets/icons/design_prop.png";
            
        case REVIEWED:
            return "file:///home/cds/Programming/Kieler/git/cdline/standalone/de.cau.cs.kieler.doclets/icons/design_yes.png";
        
        default:
            return "";
        }
    }
    
    
    /////////////////////////////////////////////////////////////////////////////
    // UTILITY METHODS
    
    /**
     * Returns the name of the HTML file for the given object in the given site category.
     * 
     * @param category the site category.
     * @param targetObject the object to generate the file name for. Can be {@code null} for overview
     *                     pages, but can also be a project or a plugin.
     * @return the file name.
     */
    public static String generateFileName(final Categories category, final Object targetObject) {
        switch (category) {
        case RATINGS:
            if (targetObject == null) {
                return "index.html";
            } else if (targetObject instanceof Project) {
                return "proj_" + ((Project) targetObject).getName() + ".html";
            } else if (targetObject instanceof Plugin) {
                return "plug_" + ((Plugin) targetObject).getName() + ".html";
            }
        
        default:
            return "index.html";
        }
    }
}
