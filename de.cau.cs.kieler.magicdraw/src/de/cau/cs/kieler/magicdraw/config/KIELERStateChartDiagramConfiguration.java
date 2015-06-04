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
 * Configuration for layouting state charts with the KIELER layout infrastructure.
 * 
 * @author nbw
 */
public class KIELERStateChartDiagramConfiguration implements IKIELERLayoutConfiguration {

    private Map<String, Object> options;

    /**
     * Constructs a new Configurator with default configuration for State Chart Diagram Layout
     */
    public KIELERStateChartDiagramConfiguration() {
        options = new HashMap<String, Object>();

        // Use Case diagrams have hierarchical edges, so need klay layered and hierarchy
        options.put(LayoutOptions.ALGORITHM.getId(), "de.cau.cs.kieler.klay.layered");
        options.put(LayoutOptions.LAYOUT_HIERARCHY.getId(), false);
        options.put(LayoutOptions.SPACING.getId(), 60.0f);
        options.put(LayoutOptions.EDGE_ROUTING.getId(), EdgeRouting.ORTHOGONAL.toString());
        options.put(LayoutOptions.BORDER_SPACING.getId(), 40.0f);
        // options.put("de.cau.cs.kieler.kiml.ogdf.option.labelMarginDistance", 15.0f);
        // options.put("de.cau.cs.kieler.kiml.ogdf.option.labelEdgeDistance", 15.0f);
    }

    /**
     * Returns the map of layout options for the online layouter
     */
    public Map<String, Object> getLayoutOptions() {
        return options;
    }

}
