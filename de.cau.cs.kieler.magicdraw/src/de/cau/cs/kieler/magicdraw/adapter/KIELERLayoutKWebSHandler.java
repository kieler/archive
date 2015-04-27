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
package de.cau.cs.kieler.magicdraw.adapter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.common.io.Files;

import de.cau.cs.kieler.kiml.options.EdgeRouting;
import de.cau.cs.kieler.kiml.options.LayoutOptions;
import de.cau.cs.kieler.kwebs.client.KIELERLayout;

/**
 * @author nbw
 * 
 */
public class KIELERLayoutKWebSHandler {

    public static String layout(String kGraph) {

        // Use local server
        final String server = "http://layout.rtsys.informatik.uni-kiel.de:9444";

        // Prepare options for class diagram layouting
        Map<String, Object> options = new HashMap<String, Object>();
        options.put(LayoutOptions.SPACING.getId(), 30.0f);
        options.put(LayoutOptions.ALGORITHM.getId(), "de.cau.cs.kieler.kiml.ogdf.planarization");
        options.put(LayoutOptions.EDGE_ROUTING.getId(), EdgeRouting.ORTHOGONAL.toString());
        options.put(LayoutOptions.BORDER_SPACING.getId(), 40.0f);

        // Perform the actual layout
        String layouted =
                KIELERLayout.layout(server, "de.cau.cs.kieler.kgraph", "de.cau.cs.kieler.kgraph",
                        options, kGraph);

        // Store layout data as SVG to validate results
        String layoutedDebug =
                KIELERLayout
                        .layout(server, "de.cau.cs.kieler.kgraph", "org.w3.svg", options, kGraph);
        layoutedDebug = layoutedDebug.replaceFirst("w=\"\\d*\"", "");
        try {
            Files.write(layoutedDebug.getBytes(), new File("/tmp/debugLayout.svg"));
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        
        return layouted;
    }
}
