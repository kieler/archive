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
package de.cau.cs.kieler.magicdraw.config;

import java.util.HashMap;
import java.util.Map;

import de.cau.cs.kieler.kiml.options.EdgeRouting;
import de.cau.cs.kieler.kiml.options.LayoutOptions;

/**
 * Configuration for layouting Class Diagramms with KIELER Layout
 * 
 * @author nbw
 */
public class KIELERClassDiagrammConfiguration implements KIELERLayoutConfiguration {

    private Map<String, Object> options;

    /**
     * Constructs a new Configurator with default configuration for Class Diagram Layout
     */
    public KIELERClassDiagrammConfiguration() {
        options = new HashMap<String, Object>();

        options.put(LayoutOptions.SPACING.getId(), 60.0f);
        options.put(LayoutOptions.ALGORITHM.getId(), "de.cau.cs.kieler.kiml.ogdf.planarization");
        options.put(LayoutOptions.EDGE_ROUTING.getId(), EdgeRouting.ORTHOGONAL.toString());
        options.put(LayoutOptions.BORDER_SPACING.getId(), 40.0f);
        options.put("de.cau.cs.kieler.kiml.ogdf.option.labelMarginDistance", 15.0f);
        options.put("de.cau.cs.kieler.kiml.ogdf.option.labelEdgeDistance", 15.0f);
    }

    /**
     * Returns the map of layout options for the online layouter
     */
    public Map<String, Object> getLayoutOptions() {
        return options;
    }

}
