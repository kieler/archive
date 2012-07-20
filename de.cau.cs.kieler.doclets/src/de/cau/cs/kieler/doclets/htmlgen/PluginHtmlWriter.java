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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.javadoc.PackageDoc;

import de.cau.cs.kieler.doclets.RatingDocletConstants;
import de.cau.cs.kieler.doclets.model.ClassItem;
import de.cau.cs.kieler.doclets.model.Plugin;
import de.cau.cs.kieler.doclets.model.Project;

/**
 * Generates the various plugin pages.
 * 
 * @author cds
 */
public class PluginHtmlWriter extends BasicHtmlWriter {
    
    /**
     * Generates the plugin pages.
     * 
     * @param projects map mapping project names to projects.
     * @param destinationFolder folder to place the documentation in.
     * @throws Exception if something bad happens.
     */
    public void generatePluginPages(final Map<String, Project> projects, final File destinationFolder)
            throws Exception {

        // Iterate over the list of projects, each containing a number of plugins
        for (Project project : projects.values()) {
            for (Plugin plugin : project.getPlugins().values()) {
                // Create the output file
                File outFile = new File(
                        destinationFolder,
                        generateFileName(Categories.RATINGS, plugin));
                outFile.createNewFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
                
                // Generate the HTML code
                generateHeader(Categories.RATINGS, plugin, writer);
                generateJavascriptCode(plugin, writer);
                generateActionBar(writer);
                generatePackageInfos(plugin, writer);
                generateFooter(Categories.RATINGS, plugin, writer);
                
                // Close the file writer
                writer.flush();
                writer.close();
            }
        }
    }

    /**
     * Generates the Javascript code used to collapse and expand stuff.
     * 
     * @param plugin the plugin to write the Javascript code for.
     * @param destinationFolder folder to place the documentation in.
     * @throws Exception if something bad happens.
     */
    private void generateJavascriptCode(final Plugin plugin, final BufferedWriter writer)
            throws Exception {
        
        writer.write("<script type='text/javascript'>");
        writer.write("  function toggleVisibility(id) {");
        writer.write("    if (document.getElementById(id).style.display == 'none') {");
        writer.write("      document.getElementById(id).style.display = 'block';");
        writer.write("    } else {");
        writer.write("      document.getElementById(id).style.display = 'none';");
        writer.write("    }");
        writer.write("  }");
        
        // Expansion function
        writer.write("  function expandAll() {");
        for (PackageDoc pkgDoc : plugin.getPackageToClassMap().keySet()) {
            writer.write("    document.getElementById('"
                    + pkgDoc.name() + "').style.display = 'block';");
        }
        writer.write("  }");
        
        // Collapse function
        writer.write("  function collapseAll() {");
        for (PackageDoc pkgDoc : plugin.getPackageToClassMap().keySet()) {
            writer.write("    document.getElementById('"
                    + pkgDoc.name() + "').style.display = 'none';");
        }
        writer.write("  }");
        
        writer.write("</script>");
    }

    /**
     * Generates the action bar that allows the user to collapse and expand all package tables.
     * 
     * @param writer where to write the output to.
     * @throws Exception if something bad happens.
     */
    private void generateActionBar(final BufferedWriter writer) throws Exception {
        // We're generating HTML code; to make things easier, we don't care about long lines.
        // CHECKSTYLEOFF LineLength
        
        writer.write("<p class='actionbar'>");
        writer.write("  Packages:");
        writer.write("  <a href='javascript:expandAll();'><img src='" + RatingDocletConstants.RES_FOLDER + "/act_expand_all.png' /> Expand all</a>");
        writer.write("  <a href='javascript:collapseAll();'><img src='" + RatingDocletConstants.RES_FOLDER + "/act_collapse_all.png' /> Collapse all</a>");
        writer.write("</p>");

        // CHECKSTYLEON LineLength
    }

    /**
     * Assembles the list of packages in the plug-in, sorts them by name, and calls a method that
     * generates package information for them.
     * 
     * @param plugin the plugin whose packages to summarize.
     * @param writer where to write the output to.
     * @throws Exception if something bad happens.
     */
    private void generatePackageInfos(final Plugin plugin, final BufferedWriter writer)
            throws Exception {
        
        // Assemble array of packages
        PackageDoc[] packages = plugin.getPackageToClassMap().keySet().toArray(new PackageDoc[0]);
        Arrays.sort(packages);
        
        for (int i = 0; i < packages.length; i++) {
            // Get sorted list of classes in the package
            ClassItem[] classes =
                    plugin.getPackageToClassMap().get(packages[i]).toArray(new ClassItem[0]);
            Arrays.sort(classes);
            
            generatePackageInfos(plugin, packages[i], classes, writer);
        }
    }
    
    /**
     * Generates the information for the given classes from the given package. The classes will be
     * printed in the order they are in the array.
     * 
     * @param plugin plugin that contains the package.
     * @param pack the package.
     * @param classes the classes in the package.
     * @param writer where to write the output to.
     * @throws Exception if something bad happens.
     */
    private void generatePackageInfos(final Plugin plugin, final PackageDoc pack,
            final ClassItem[] classes, final BufferedWriter writer) throws Exception {
        
        writer.write("<h2 class='package'>");
        writer.write("<a href='#' onclick='toggleVisibility(\"" + pack.name() + "\");'>");
        writer.write("<img src='" + getIconForThing(pack) + "' /> " + pack.name());
        writer.write("</a></h2>");
        writer.write("<div id='" + pack.name() + "'><table cellspacing='0' cellpadding='6'>");
        writer.write("  <tr class='oddheader headerlinebottom'>");
        writer.write("    <th width='30%'>Thing</th>");
        writer.write("    <th width='35%' class='newcolgroup'>Design Review</th>");
        writer.write("    <th width='35%'>Code Review</th>");
        writer.write("  </tr>");
        
        // Iterate through classes (not every class is written into the table, requiring the use of a
        // second iteration variable to get alternating row colors)
        int tableRow = 0;
        for (int i = 0; i < classes.length; i++) {
            // Generated classes are only added to the table if they have explicit ratings
            if (classes[i].isGenerated()
                    && classes[i].getDesignRating() == null
                    && classes[i].getCodeRating() == null) {
                
                continue;
            }
            
            // New table row; determine the class
            writer.write("<tr class='linebottom ");
            writer.write(tableRow % 2 == 0 ? "even" : "odd");
            writer.write("'>");
            
            // Data! DATA!
            writer.write("<td>" + linkifyClassName(plugin, classes[i]));
            writer.write(generateClassNameHtml(classes[i], true));
            writer.write("</a></td>");
            
            // Design review
            writer.write("<td class='newcolgroup'>");
            writer.write("<img src='" + getIconForDesignRating(classes[i].getDesignRating()) + "' /> ");
            writer.write(linkifyReviewComment(classes[i].getDesignRatingDetails()));
            writer.write("</td>");
            
            // Code review
            writer.write("<td>");
            writer.write("<img src='" + getIconForCodeRating(classes[i].getCodeRating()) + "' /> ");
            writer.write(linkifyReviewComment(classes[i].getCodeRatingDetails()));
            writer.write("</td>");
            
            // End table row
            writer.write("</tr>");
            
            tableRow++;
        }
        
        writer.write("</table></div>");
    }
    
    /**
     * Returns a link pointing to the given class, without adding text to the link or closing it.
     * 
     * @param plugin the plugin that contains the class.
     * @param clazz the class to turn into a link.
     * @return link linking to the class, but only the opening tag.
     */
    private String linkifyClassName(final Plugin plugin, final ClassItem clazz) {
        /* We assume the path to look like the following:
         * 
         *   ...../kieler_dir/{plugins | standalone}/plugin-dir/...
         * 
         * We first look for the index of the plug-in directory. Once we've found that, we get the
         * substring right up to, but excluding, the path separator character just preceding the
         * plug-in directory. In that substring, we look for the last occurrence of the separator
         * character, which tells us where the "plugins" or "standalone" folder starts.
         */
        
        String path = clazz.getClassDoc().position().file().getAbsolutePath();
        
        // The plugin name must be in there somewhere
        int pluginNameIndex = path.lastIndexOf(plugin.getName());
        
        // We are looking for the name of the directory containing the plugin directory
        int containingDirIndex = path.substring(0, pluginNameIndex - 1).lastIndexOf(File.separator);
        
        // Generate URL
        return "<a href='" + RatingDocletConstants.SCM_PATH
                + path.substring(containingDirIndex + 1) + "'>";
    }
    
    /**
     * Looks for references to Crucible reviews in the text and linkifies them.
     * 
     * @param comment the comment text.
     * @return linkified version of the comment text.
     */
    private String linkifyReviewComment(final String comment) {
        if (comment == null) {
            return "";
        } else {
            // We're looking for crucible review names (e.g., 'KI-16')
            Pattern cruciblePattern = Pattern.compile("\\bKI-\\d+\\b");
            Matcher patternMatcher = cruciblePattern.matcher(comment);
            
            // Build the linkified comment
            StringBuffer linkified = new StringBuffer();
            
            while (patternMatcher.find()) {
                String crucibleReview = patternMatcher.group();
                String reviewLink = "<a href='" + RatingDocletConstants.CRUCIBLE_PATH + crucibleReview
                        + "'>";
                patternMatcher.appendReplacement(
                        linkified, reviewLink + crucibleReview + "</a>");
            }
            patternMatcher.appendTail(linkified);
            
            return linkified.toString();
        }
    }
}
