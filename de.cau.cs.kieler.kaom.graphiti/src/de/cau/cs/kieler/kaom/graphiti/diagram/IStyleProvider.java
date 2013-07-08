/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 *
 * http://www.informatik.uni-kiel.de/rtsys/kieler/
 * 
 * Copyright 2010 by
 * + Christian-Albrechts-University of Kiel
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 * 
 * This code is provided under the terms of the Eclipse Public License (EPL).
 * See the file epl-v10.html for the license text.
 */
package de.cau.cs.kieler.kaom.graphiti.diagram;

import org.eclipse.graphiti.mm.algorithms.styles.Style;

/**
 * Interface for style providers of Graphiti diagram editors.
 *
 * @author msp
 */
public interface IStyleProvider {
    
    /**
     * Returns the default style of this style provider.
     * 
     * @return the default style
     */
    Style getStyle();
    
    /**
     * Returns the specified style as provided by this style provider.
     * 
     * @param id the style identifier
     * @return the corresponding style, or {@code null} if the style was not recognized
     */
    Style getStyle(String id);

}
