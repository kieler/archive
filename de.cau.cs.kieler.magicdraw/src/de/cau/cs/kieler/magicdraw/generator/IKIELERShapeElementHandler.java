/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 *
 * http://www.informatik.uni-kiel.de/rtsys/kieler/
 * 
 * Copyright 2015 by
 * + Christian-Albrechts-University of Kiel
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 * 
 * This code is provided under the terms of the Eclipse Public License (EPL).
 * See the file epl-v10.html for the license text.
 */
package de.cau.cs.kieler.magicdraw.generator;

import com.nomagic.magicdraw.uml.symbols.shapes.ShapeElement;

/**
 * Interface for all Shape Element handlers. These are then encapsulated using the
 * {@link KIELERShapeElementHandler} facade.
 * 
 * @author nbw
 */
public interface IKIELERShapeElementHandler<T extends ShapeElement> {

    /**
     * Adds a {@link ShapeElement} to the KGraph.
     * 
     * @param pe The ShapeElement to add
     * @param kGraphRoot The root node of the KGraph to add the new element to
     * @param elementsMapping The mapping of existing PresentationElements and KGraph elements
     * @param elementsByID The ids of PresentationElements for future reference
     */
    public <U extends ShapeElement> void addElementToKGraph(T pe, KGraphBuilder builder);
}
