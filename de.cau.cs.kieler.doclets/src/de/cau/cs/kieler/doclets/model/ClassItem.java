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
package de.cau.cs.kieler.doclets.model;

import java.util.StringTokenizer;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Tag;

import de.cau.cs.kieler.doclets.RatingDocletConstants;

/**
 * Represents a class with its review status. Note that this class has an attribute marking it as
 * generated or not. The attribute is triggered by two things. First, there's the generated javadoc
 * tag. And second, a class placed somewhere below a folder with one of the known names for generated
 * classes (src-gen, xtend-gen) is marked as a generated class.
 * 
 * @author cds
 */
public class ClassItem implements Comparable<ClassItem> {
    /**
     * The class doc object describing this class.
     */
    private ClassDoc classDoc;
    
    /**
     * Whether the class was generated or not. A generated class will usually not be counted, but may
     * be if it still has a design or code rating.
     */
    private boolean generated = false;
    
    /**
     * The class's design rating.
     */
    private DesignRating designRating = null;
    
    /**
     * Design rating details, if any.
     */
    private String designRatingDetails = null;
    
    /**
     * The class's code rating.
     */
    private CodeRating codeRating = null;
    
    /**
     * Code rating details, if any.
     */
    private String codeRatingDetails = null;

    
    /////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    
    /**
     * Creates a new class item representing the given class.
     * 
     * @param classDoc the class to be represented here.
     */
    public ClassItem(final ClassDoc classDoc) {
        this.classDoc = classDoc;
        
        // Check for the @generated tag
        if (classDoc.tags(RatingDocletConstants.TAG_GENERATED).length > 0) {
            generated = true;
        }
        
        // Check if the class is in one of the folders known for generated classes
        String path = classDoc.position().file().getPath();
        for (int i = 0; i < RatingDocletConstants.GEN_FOLDERS.length; i++) {
            if (path.indexOf(RatingDocletConstants.GEN_FOLDERS[i]) >= 0) {
                generated = true;
            }
        }
        
        // Extract the code and design review ratings
        extractCodeRating();
        extractDesignRating();
    }

    
    /////////////////////////////////////////////////////////////////////////////
    // UTILITY METHODS
    
    /**
     * Extracts the class's code rating, if any. If the class doesn't have a code rating, what happens
     * depends on whether the class was generated or not. If it was, the code rating isn't set at all.
     * If it was not, its rating is set to red.
     */
    private void extractCodeRating() {
        CodeRating codeRatingCandidate = null;
        
        // Use the latest review tag
        Tag[] codeRatingTags = classDoc.tags(RatingDocletConstants.TAG_CODE_REVIEW);

        if (codeRatingTags != null && codeRatingTags.length > 0) {
            String tagText = codeRatingTags[codeRatingTags.length - 1].text();
            if (tagText != null) {
                // Break the tag's text into tokens
                StringTokenizer tokenizer = new StringTokenizer(tagText, " \t\n\r");
                boolean proposed = false;
                String token = "";
                StringBuilder finalComment = new StringBuilder();
                
                while (tokenizer.hasMoreTokens()) {
                    token = tokenizer.nextToken();
    
                    if (token.equalsIgnoreCase(RatingDocletConstants.TAG_PROPOSED) && !proposed) {
                        // Proposed
                        proposed = true;
                    } else if (codeRatingCandidate == null) {
                        // We haven't had a code rating yet, so try to interpret this as one
                        try {
                            codeRatingCandidate = CodeRating.valueOf(token.toUpperCase());
                        } catch (IllegalArgumentException exception) {
                            // This was not a code rating, so just add it to the comment
                            finalComment.append(" " + token);
                        }
                    } else {
                        finalComment.append(" " + token);
                    }
                }
                
                // If the rating is only proposed, we must degrade the rating candidate
                if (proposed) {
                    codeRatingCandidate = codeRatingCandidate.getDegraded();
                }
                
                codeRatingDetails = finalComment.toString();
            }
        }
        
        // Apply code rating
        if (codeRatingCandidate != null) {
            // A code rating was determined
            codeRating = codeRatingCandidate;
        } else if (codeRatingCandidate == null && !isGenerated()) {
            // A code rating could not be found, but the class is not generated and must thus be
            // rated RED
            codeRating = CodeRating.RED;
        }
    }
    
    /**
     * Extracts the class's design rating, if any. If the class doesn't have a design rating, what
     * happens depends on whether the class was generated or not. If it was, the design rating isn't
     * set at all. If it was not, its rating is set to NONE.
     */
    private void extractDesignRating() {
        DesignRating designRatingCandidate = null;
        
        // Use the latest review tag
        Tag[] designRatingTags = classDoc.tags(RatingDocletConstants.TAG_DESIGN_REVIEW);
        
        if (designRatingTags != null && designRatingTags.length > 0) {
            String tagText = designRatingTags[designRatingTags.length - 1].text().trim();
            if (tagText != null) {
                // Check if the first word of the text is the PROPOSED tag
                if (tagText.toLowerCase().startsWith(RatingDocletConstants.TAG_PROPOSED)) {
                    designRatingCandidate = DesignRating.PROPOSED;
                    
                    // The comment starts after the proposed tag
                    designRatingDetails =
                            tagText.substring(RatingDocletConstants.TAG_PROPOSED.length()).trim();
                } else {
                    designRatingCandidate = DesignRating.REVIEWED;
                    designRatingDetails = tagText;
                }
            }
        }
        
        // Apply design rating
        if (designRatingCandidate != null) {
            // A design rating was determined
            designRating = designRatingCandidate;
        } else if (designRatingCandidate == null && !isGenerated()) {
            // A design rating could not be found, but the class is not generated and must thus be
            // rated NONE
            designRating = DesignRating.NONE;
        }
    }

    
    /////////////////////////////////////////////////////////////////////////////
    // GETTERS

    /**
     * Returns the class documentation object for this class.
     * 
     * @return the class doc object representing this class.
     */
    public ClassDoc getClassDoc() {
        return classDoc;
    }
    
    /**
     * Checks whether this class was generated or not.
     * 
     * @return {@code true} if this class was generated.
     */
    public boolean isGenerated() {
        return generated;
    }

    /**
     * The design rating of the class, if any.
     * 
     * @return the design rating.
     */
    public DesignRating getDesignRating() {
        return designRating;
    }

    /**
     * Details of the design rating, if any.
     * 
     * @return the design rating details.
     */
    public String getDesignRatingDetails() {
        return designRatingDetails;
    }

    /**
     * The code rating of the class, if any.
     * 
     * @return the code rating.
     */
    public CodeRating getCodeRating() {
        return codeRating;
    }

    /**
     * Details of the code rating, if any.
     * 
     * @return the code rating details.
     */
    public String getCodeRatingDetails() {
        return codeRatingDetails;
    }

    
    /////////////////////////////////////////////////////////////////////////////
    // Comparable Interface
    
    /**
     * {@inheritDoc}
     */
    public int compareTo(final ClassItem o) {
        return classDoc.name().compareTo(o.classDoc.name());
    }
}
