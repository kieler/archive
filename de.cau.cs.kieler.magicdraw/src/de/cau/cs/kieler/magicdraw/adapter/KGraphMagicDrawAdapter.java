package de.cau.cs.kieler.magicdraw.adapter;

import de.cau.cs.kieler.core.kgraph.KNode;
import de.cau.cs.kieler.core.properties.IProperty;
import de.cau.cs.kieler.core.properties.Property;

/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 *
 * http://www.informatik.uni-kiel.de/rtsys/kieler/
 * 
 * Copyright 2013 by
 * + Christian-Albrechts-University of Kiel
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 * 
 * This code is provided under the terms of the Eclipse Public License (EPL).
 * See the file epl-v10.html for the license text.
 */

public class KGraphMagicDrawAdapter {

    public static final IProperty<Integer> MAGICDRAWID = new Property<Integer>(
            "de.cau.cs.kieler.magicdraw.id", 0);

    public KNode generateKGraph() {
        return null;
    }
}
