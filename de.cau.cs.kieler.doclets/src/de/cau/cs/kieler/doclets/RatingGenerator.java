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
package de.cau.cs.kieler.doclets;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;

import de.cau.cs.kieler.doclets.htmlgen.OverviewHtmlWriter;
import de.cau.cs.kieler.doclets.htmlgen.ProjectHtmlWriter;
import de.cau.cs.kieler.doclets.model.ClassItem;
import de.cau.cs.kieler.doclets.model.Plugin;
import de.cau.cs.kieler.doclets.model.Project;

/**
 * Does the actual work of loading classes, partitioning them into projects and plug-ins and finally
 * delegating the work of generating the HTML output.
 * 
 * @author cds
 */
public class RatingGenerator {
    /**
     * Map of project names to the actual projects. A map is used to be able to quickly find projects.
     */
    private Map<String, Project> projects = new HashMap<String, Project>();
    
    
    /**
     * Generates the rating HTML pages.
     * 
     * @param rootDoc the root documentation object containing everything to generate rating
     *                documentation for.
     * @param destination the folder to place the generated documentation in.
     * @throws Exception if something goes wrong. This is an internal tool, so we won't care about
     *                   robustness too much...
     */
    public void generateRatings(final RootDoc rootDoc, final String destination) throws Exception {
        // Create destination folder
        File destinationFolder = new File(destination);
        destinationFolder.mkdirs();
        
        // Build the map of projects, plug-ins and classes
        buildModel(rootDoc);
        aggregateStatistics();
        writeModelToDebugFile(destinationFolder);
        
        // TODO: Setup the basic output file system structure with CSS, icons, and all
        
        // TODO: Generate progress bars for ratings
        
        // Generate HTML files
        new OverviewHtmlWriter().generateOverviewPage(projects, destinationFolder);
        new ProjectHtmlWriter().generateOverviewPage(projects, destinationFolder);
    }

    
    /////////////////////////////////////////////////////////////////////////////
    // MODEL BUILDING
    
    /**
     * Builds the map of projects, plug-ins and classes.
     * 
     * @param rootDoc the root documentation object to load everything from.
     */
    private void buildModel(final RootDoc rootDoc) {
        for (ClassDoc classDoc : rootDoc.classes()) {
            // Don't include nested classes
            if (classDoc.containingClass() != null) {
                continue;
            }
            
            // Get the containing package
            PackageDoc containingPackage = classDoc.containingPackage();
            String containingPackageName = containingPackage.name();
            
            // Check if the package name is of interest to us
            if (isPackageOfInterest(containingPackageName)) {
                String projectName = extractProjectName(containingPackageName);
                Project project = retrieveProject(projectName);
                Plugin plugin = project.retrievePlugin(extractPluginName(classDoc));
                plugin.addClass(new ClassItem(classDoc));
            }
        }
    }

    /**
     * Checks if the package with the given name should be included in the rating documentation.
     * 
     * @param packageName name of the package.
     * @return {@code true} if the package should be included in the documentation.
     */
    private boolean isPackageOfInterest(final String packageName) {
        return packageName.startsWith(RatingDocletConstants.PROJECT_PREFIX);
    }

    /**
     * Returns the name of the project the given package is part of.
     * 
     * @param packageName name of the package.
     * @return name of the project.
     */
    private String extractProjectName(final String packageName) {
        int dotIndex = packageName.indexOf('.', RatingDocletConstants.PROJECT_PREFIX.length());
        return dotIndex < 0
                ? packageName.substring(RatingDocletConstants.PROJECT_PREFIX.length())
                : packageName.substring(RatingDocletConstants.PROJECT_PREFIX.length(), dotIndex);
    }
    
    /**
     * Returns the name of the plug-in the given class is part of.
     * 
     * @param classDoc class documentation for the class whose plug-in name is to be retrieved.
     * @return name of the containing plug-in.
     */
    private String extractPluginName(final ClassDoc classDoc) {
        // Look for the last occurrence of our project prefix in the source file's path
        String path = classDoc.position().file().getAbsolutePath();
        int pluginNameIndex = path.lastIndexOf(RatingDocletConstants.PROJECT_PREFIX);
        int nextSeparatorIndex = path.indexOf(File.separatorChar, pluginNameIndex);
        
        return path.substring(pluginNameIndex, nextSeparatorIndex);
    }
    
    /**
     * Retrieves the project with the given name. If there is none, a new project is created.
     * 
     * @param projectName name of the project to retrieve.
     * @return the project.
     */
    private Project retrieveProject(final String projectName) {
        Project project = projects.get(projectName);
        
        if (project == null) {
            project = new Project(projectName);
            projects.put(projectName, project);
        }
        
        return project;
    }
    
    /**
     * Aggregates the statistics of all projects.
     */
    private void aggregateStatistics() {
        for (Project project : projects.values()) {
            project.aggregateStatistics();
        }
    }

    
    /////////////////////////////////////////////////////////////////////////////
    // DEBUG
    
    /**
     * Writes the model to a debug file in the destination folder.
     * 
     * @param destinationFolder the destination folder.
     * @throws Exception if anything bad happens.
     */
    private void writeModelToDebugFile(final File destinationFolder) throws Exception {
        File outFile = new File(destinationFolder, "debug.txt");
        outFile.createNewFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
        
        for (Project project : projects.values()) {
            writer.write("Project " + project.getName() + "\n");
            
            for (Plugin plugin : project.getPlugins().values()) {
                writer.write("    Plug-In " + plugin.getName() + "\n");
                
                for (PackageDoc packageDoc : plugin.getPackageToClassMap().keySet()) {
                    writer.write("         Package " + packageDoc.name() + "\n");
                    
                    for (ClassItem classItem : plugin.getPackageToClassMap().get(packageDoc)) {
                        writer.write("              Class " + classItem.getClassDoc().name() + "\n");
                    }
                }
            }
        }
        
        writer.flush();
        writer.close();
    }
}
