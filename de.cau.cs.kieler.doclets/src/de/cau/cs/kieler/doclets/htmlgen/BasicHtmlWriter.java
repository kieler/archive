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

import de.cau.cs.kieler.doclets.RatingDocletConstants;
import de.cau.cs.kieler.doclets.model.Plugin;
import de.cau.cs.kieler.doclets.model.Project;

/**
 * Base class of all Html writers. Containts a whole lot of utility methods.
 * 
 * @author cds
 * @generated
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
        
        // CHECKSTYLEOFF LineLength
        
        writer.write("<!DOCTYPE doctype PUBLIC '-//w3c//dtd html 4.0 transitional//en'><html>");
        writer.write("<head>");
        writer.write("<title>" + RatingDocletConstants.TXT_TITLE + "</title>");
        writer.write("<link rel='stylesheet' type='text/css' href='style.css'>");
        
        writer.write("<script type='text/javascript'>");
        writer.write("  function expandAll() {");
        writer.write("    document.getElementById('package_klay_layered').style.display = 'block';");
        writer.write("    document.getElementById('package_klay_layered_lgraph').style.display = 'block';");
        writer.write("  }");
        writer.write("  function collapseAll() {");
        writer.write("    document.getElementById('package_klay_layered').style.display = 'none';");
        writer.write("    document.getElementById('package_klay_layered_lgraph').style.display = 'none';");
        writer.write("  }");
        writer.write("  function toggleVisibility(id) {");
        writer.write("    if (document.getElementById(id).style.display == 'none') {");
        writer.write("      document.getElementById(id).style.display = 'block';");
        writer.write("    } else {");
        writer.write("      document.getElementById(id).style.display = 'none';");
        writer.write("    }");
        writer.write("  }");
        writer.write("</script>");
        
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
