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

/**
 * Enumeration of design review ratings.
 * 
 * @author cds
 */
public enum DesignRating {
    /** no design review. */
    NONE,
    /** design review planned. */
    PROPOSED,
    /** design reviewed. */
    REVIEWED;
    
    /**
     * Returns whether this rating is a proposed rating.
     * 
     * @return true if this rating is proposed
     */
    public boolean isProposed() {
        return ordinal() == 1;
    }
    
    /**
     * Returns a modified rating that is one level lower than this rating.
     * 
     * @return a lower rating
     */
    public DesignRating getDegraded() {
        int ordinal = ordinal();
        if (ordinal > 0) {
            return values()[ordinal - 1];
        } else {
            return this;
        }
    }
}