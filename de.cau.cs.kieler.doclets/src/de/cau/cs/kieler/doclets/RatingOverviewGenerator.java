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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;

import de.cau.cs.kieler.doclets.ClassRatingGenerator.Rating;

/**
 * Generator for code rating overviews.
 *
 * @author msp
 */
public class RatingOverviewGenerator {

    /** prefix for all packages of the project. */
    private static final String PROJECT_PREFIX = "de.cau.cs.kieler.";
    /** title of the rating overview. */
    private static final String TITLE = "KIELER Rating Overview";
    /** factor for percentage calculation. */
    private static final float PERC_FACT = 100;
    
    /** path to the destination folder. */
    private String destinationPath;

    /**
     * Generate output documents for the given root doc.
     * 
     * @param rootDoc the root doc
     * @throws IOException if writing output fails
     */
    public void generate(final RootDoc rootDoc) throws IOException {
        // create destination folder
        File destinationDir = new File(destinationPath);
        destinationDir.mkdirs();
        
        // gather all subprojects and their packages
        Map<String, Set<PackageDoc>> projectMap = new LinkedHashMap<String, Set<PackageDoc>>();
        for (ClassDoc classDoc : rootDoc.classes()) {
            PackageDoc packageDoc = classDoc.containingPackage();
            String packageName = packageDoc.name();
            if (packageName.startsWith(PROJECT_PREFIX)) {
                int dotIndex = packageName.indexOf('.', PROJECT_PREFIX.length());
                String projectName = dotIndex < 0
                        ? packageName.substring(PROJECT_PREFIX.length())
                        : packageName.substring(PROJECT_PREFIX.length(), dotIndex);
                Set<PackageDoc> projectSet = projectMap.get(projectName);
                if (projectSet == null) {
                    projectSet = new HashSet<PackageDoc>();
                    projectMap.put(projectName, projectSet);
                }
                projectSet.add(packageDoc);
            }
        }
        
        // write file header and table headers
        File outFile = new File(destinationPath, "index.html");
        outFile.createNewFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
        HtmlWriter.writeHeader(writer, TITLE);
        writer.write("<table>\n<tr><th>Project</th>");
        Rating[] ratings = Rating.values();
        for (int i = 0; i < ratings.length; i++) {
            writer.write("<th>%" + ratings[i].toString().toLowerCase() + "</th>");
        }
        writer.write("</tr>\n");

        // generate output for each subproject
        ClassRatingGenerator classRatingGenerator = new ClassRatingGenerator();
        classRatingGenerator.setDestinationPath(destinationPath);
        ArrayList<Entry<String, Set<PackageDoc>>> entries
                = new ArrayList<Entry<String, Set<PackageDoc>>>(projectMap.entrySet());
        Collections.sort(entries, new Comparator<Entry<String, Set<PackageDoc>>>() {
            public int compare(final Entry<String, Set<PackageDoc>> e1,
                    final Entry<String, Set<PackageDoc>> e2) {
                String str1 = e1.getKey(), str2 = e2.getKey();
                return str1.compareTo(str2);
            }
        });
        for (Entry<String, Set<PackageDoc>> entry : entries) {
            String projectName = entry.getKey();
            Set<PackageDoc> containedPackages = entry.getValue();
            rootDoc.printNotice("Generating rating for project '" + projectName + "'...");
            float[] relRatings = classRatingGenerator.generate(projectName, containedPackages);
            
            String capitProjectName = Character.toUpperCase(projectName.charAt(0))
                    + projectName.substring(1);
            writer.write("<tr><td><a href=\"" + classRatingGenerator.getFileName(projectName)
                    + "\">" + capitProjectName + "</a></td>");
            for (int i = 0; i < relRatings.length; i++) {
                writer.write("<td>" + Math.round(PERC_FACT * relRatings[i]) + "</td>");
            }
            writer.write("</tr>\n");
        }
        writer.write("</table>\n");
        
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
