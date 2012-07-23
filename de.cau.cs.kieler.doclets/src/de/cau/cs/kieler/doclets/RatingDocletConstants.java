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
     * Tag identifying ignored code.
     */
    public static final String TAG_IGNORE = "@kieler.ignore";

    /**
     * Tag identifying generated code.
     */
    public static final String TAG_GENERATED = "@generated";

    /**
     * Tag identifying deprecated code.
     */
    public static final String TAG_DEPRECATED = "@deprecated";
    
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
     * Name of the destination folder's subfolder where resources are put.
     */
    public static final String RES_FOLDER = "resources";
    
    /**
     * URL of the source code management system used to generate links to class files. Must end with
     * a slash.
     */
    public static final String SCM_PATH =
            "https://git.rtsys.informatik.uni-kiel.de/kieler/mainline/blobs/master/";
    
    /**
     * Beginning of Crucible URLs to which a code review number only needs to be appended to arrive at
     * a valid code review link.
     */
    public static final String CRUCIBLE_PATH = "http://rtsys.informatik.uni-kiel.de/fisheye/cru/";
    
    // INTERNATIONALIZATION
    // CHECKSTYLEOFF JavadocVariable
    public static final String TXT_TITLE = "KIELER Code Ratings";
    public static final String TXT_RATINGS = "Ratings";
    public static final String TXT_TOP_TEN = "Top 10";
    public static final String TXT_WALLBOARD = "Wallboard";
    
    
    /**
     * This class is not supposed to be instantiated.
     */
    private RatingDocletConstants() {
        // Nothing to be done here.
    }
}
