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

/**
 * Various constant values used by the doclet.
 * 
 * @author cds
 */
public final class RatingDocletConstants {
    
    /**
     * Prefix of all plug-in names of this project. The component immediately following this prefix
     * is interpreted as project name and used to partition the list of plug-ins into projects.
     */
    public static final String PROJECT_PREFIX = "de.cau.cs.kieler.";
    
    /**
     * Tag for code reviews.
     */
    public static final String TAG_CODE_REVIEW = "@kieler.rating";
    
    /**
     * Tag for design reviews.
     */
    public static final String TAG_DESIGN_REVIEW = "@kieler.design";

    /**
     * Tag identifying generated code.
     */
    public static final String TAG_GENERATED = "@generated";
    
    /**
     * String used to indicate that a code or design status is currently just proposed.
     */
    public static final String TAG_PROPOSED = "proposed";
    
    /**
     * Folders where generated classes are placed in. Classes below these folders are regarded as
     * being generated unless they have an explicit design or code review tag.
     */
    public static final String[] GEN_FOLDERS = {"src-gen", "xtend-gen"};
    
    /**
     * The page title.
     */
    public static final String TXT_TITLE = "KIELER Code Ratings";
    
    
    /**
     * This class is not supposed to be instantiated.
     */
    private RatingDocletConstants() {
        // Nothing to be done here.
    }
}
