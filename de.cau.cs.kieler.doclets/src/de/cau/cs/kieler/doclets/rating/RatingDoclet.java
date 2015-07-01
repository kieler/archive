/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 *
 * http://www.informatik.uni-kiel.de/rtsys/kieler/
 * 
 * Copyright 2009 by
 * + Kiel University
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 * 
 * This code is provided under the terms of the Eclipse Public License (EPL).
 * See the file epl-v10.html for the license text.
 */
package de.cau.cs.kieler.doclets.rating;

import java.io.IOException;

import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.Doclet;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.RootDoc;

/**
 * Doclet for code ratings.
 *
 * @author msp
 * @author cds
 */
public class RatingDoclet extends Doclet {

    /** command line option for destination folder. */
    private static final String DESTINATION_OPTION = "-d";
    
    /** command line option for KIELER project to generate code ratings for. */
    private static final String PROJECT_OPTION = "-p";
    
    /**
     * Generates documentation for the given root.
     * 
     * @param rootDoc the root doc
     * @return true on success
     */
    public static boolean start(final RootDoc rootDoc) {
        
        String destination = ".";
        UmbrellaProject umbrellaProject = null;
        
        // read command line options
        String[][] options = rootDoc.options();
        for (int i = 0; i < options.length; i++) {
            String[] option = options[i];
            
            if (DESTINATION_OPTION.equals(option[0])) {
                // we require an option value that specifies the destination folder
                if (option.length == 2 && !option[1].isEmpty()) {
                    destination = option[1];
                } else {
                    throw new RuntimeException("no value specified for parameter '"
                            + DESTINATION_OPTION + "'.");
                }
            } else if (PROJECT_OPTION.equals(option[0])) {
                // we require an option value that specifies the project
                if (option.length == 2 && !option[1].isEmpty()) {
                    umbrellaProject = UmbrellaProject.valueOf(option[1].toUpperCase());
                } else {
                    throw new RuntimeException("no value specified for parameter '"
                            + PROJECT_OPTION + "'");
                }
            }
        }
        
        // Check if an umbrella project was specified
        if (umbrellaProject == null) {
            throw new RuntimeException(
                    "The project must be specified ('-p pragmatics' or '-p semantics')");
        }
        
        // Generate the rating documentation
        RatingGenerator generator = new RatingGenerator();
        try {
            generator.generateRatings(umbrellaProject, rootDoc, destination);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        
        return true;
    }
    
    /**
     * Check that options have the correct arguments.
     * 
     * @param options array of options
     * @param reporter utility to print argument errors
     * @return true if the options are valid
     */
    public static boolean validOptions(final String[][] options, final DocErrorReporter reporter) {
        boolean foundDestOption = false;
        
        for (int i = 0; i < options.length; i++) {
            if (DESTINATION_OPTION.equals(options[i][0])) {
                if (foundDestOption) {
                    reporter.printError("Only one " + DESTINATION_OPTION + " argument allowed.");
                    return false;
                } else {
                    foundDestOption = true;
                }
            }
        }
        
        return true;
    }
    
    /**
     * Check for doclet-added options. Returns the number of arguments
     * you must specify on the command line for the given option.
     * 
     * @param option a command line option
     * @return number of arguments on the command line for an option
     *      including the option name itself
     */
    public static int optionLength(final String option) {
        if (DESTINATION_OPTION.equals(option)) {
            return 2;
        } else if (PROJECT_OPTION.equals(option)) {
            return 2;
        } else {
            return 0;
        }
    }
    
    /**
     * Return the version of the Java Programming Language supported by
     * this doclet. 
     * 
     * @return the language version supported by this doclet
     */
    public static LanguageVersion languageVersion() {
        return LanguageVersion.JAVA_1_5;
    }
}
