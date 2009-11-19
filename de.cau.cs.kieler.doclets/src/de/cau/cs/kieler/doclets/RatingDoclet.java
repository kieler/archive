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
 */
public class RatingDoclet extends Doclet {

    /**
     * Generates documentation for the given root.
     * 
     * @param rootDoc the root doc
     * @return true on success
     */
    public static boolean start(RootDoc rootDoc) {
        return true;
    }
    
    /**
     * Check that options have the correct arguments.
     * 
     * @param options array of options
     * @param reporter utility to print argument errors
     * @return true if the options are valid
     */
    public static boolean validOptions(String[][] options,
            DocErrorReporter reporter) {
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
    public static int optionLength(String option) {
        return 0;
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
