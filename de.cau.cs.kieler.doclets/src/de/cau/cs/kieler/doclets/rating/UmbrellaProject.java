/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 *
 * http://www.informatik.uni-kiel.de/rtsys/kieler/
 * 
 * Copyright 2013 by
 * + Kiel University
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 * 
 * This code is provided under the terms of the Eclipse Public License (EPL).
 * See the file epl-v10.html for the license text.
 */
package de.cau.cs.kieler.doclets.rating;

/**
 * An enumeration of the different umbrella projects in KIELER that we want to generate code ratings for.
 * 
 * @author cds
 */
public enum UmbrellaProject {
    /**
     * The KIELER Pragmatics project.
     */
    PRAGMATICS,
    
    /**
     * The KIELER Semantics project.
     */
    SEMANTICS;
    
    
    /**
     * Returns a name that can be displayed to the user.
     * 
     * @return displayable name.
     */
    public String toDisplayName() {
        switch (this) {
        case PRAGMATICS:
            return "KIELER Pragmatics";
        case SEMANTICS:
            return "KIELER Semantics";
        }
        
        // This line would normally never be reached
        return "";
    }
}
