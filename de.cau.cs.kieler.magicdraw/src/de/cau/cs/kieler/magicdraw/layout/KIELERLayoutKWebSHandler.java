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
package de.cau.cs.kieler.magicdraw.layout;

import java.io.File;
import java.io.IOException;

import com.google.common.io.Files;

import de.cau.cs.kieler.kwebs.client.KIELERLayout;
import de.cau.cs.kieler.magicdraw.config.IKIELERLayoutConfiguration;

/**
 * Encapsulates the layouting web service to allow for easy switching to other communication paths
 * 
 * @author nbw
 */
public class KIELERLayoutKWebSHandler {

    public static String layout(String kGraph, IKIELERLayoutConfiguration config) {
        // Use local server
        final String server = "http://localhost:9444";

        // Perform the actual layout
        String layouted =
                KIELERLayout.layout(server, "de.cau.cs.kieler.kgraph", "de.cau.cs.kieler.kgraph",
                        config.getLayoutOptions(), kGraph);

        // Store layout data as SVG to validate results
        String layoutedDebug =
                KIELERLayout.layout(server, "de.cau.cs.kieler.kgraph", "org.w3.svg",
                        config.getLayoutOptions(), kGraph);
        layoutedDebug = layoutedDebug.replaceFirst("w=\"\\d*\"", "");
        try {
            Files.write(layoutedDebug.getBytes(), new File("/Users/nbw/debugLayout.svg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return layouted;
    }
}
