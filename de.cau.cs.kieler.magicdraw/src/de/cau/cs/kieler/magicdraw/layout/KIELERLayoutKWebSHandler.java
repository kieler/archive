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
import de.cau.cs.kieler.magicdraw.config.KIELERLayoutConfiguration;

/**
 * @author nbw
 * 
 */
public class KIELERLayoutKWebSHandler {

    public static String layout(String kGraph, KIELERLayoutConfiguration config) {

        // Use local server
        final String server = "http://layout.rtsys.informatik.uni-kiel.de:9444";

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
            Files.write(layoutedDebug.getBytes(), new File("/tmp/debugLayout.svg"));
        } catch (IOException e2) {
            e2.printStackTrace();
        }

        return layouted;
    }
}
