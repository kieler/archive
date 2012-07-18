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
    
    /**
     * Generates documentation for the given root.
     * 
     * @param rootDoc the root doc
     * @return true on success
     */
    public static boolean start(final RootDoc rootDoc) {
        // read command line options
        String[][] options = rootDoc.options();
        String destination = ".";
        for (int i = 0; i < options.length; i++) {
            String[] option = options[i];
            if (option.length == 2 && DESTINATION_OPTION.equals(option[0]) && option[1].length() > 0) {
                destination = option[1];
            }
        }
        
        // Generate the rating documentation
        RatingGenerator generator = new RatingGenerator();
        try {
            generator.generateRatings(rootDoc, destination);
        } catch (Exception exception) {
            // Build a proper exception message
            StringBuilder message = new StringBuilder(
                    "Error producing rating documentation: " + exception.getMessage());
            
            StackTraceElement[] stackTrace = exception.getStackTrace();
            for (int i = 0; i < stackTrace.length; i++) {
                message.append("\n" + stackTrace[i].toString());
            }
            
            rootDoc.printError(message.toString());
            return false;
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
