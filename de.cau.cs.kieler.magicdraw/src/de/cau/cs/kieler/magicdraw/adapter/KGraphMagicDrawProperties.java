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

import de.cau.cs.kieler.core.properties.IProperty;
import de.cau.cs.kieler.core.properties.Property;

/**
 * @author nbw
 *
 */
public class KGraphMagicDrawProperties {

    public static final IProperty<Integer> MAGICDRAW_ID = new Property<Integer>(
            "de.cau.cs.kieler.magicdraw.id", -1);

    public enum MagicDrawElementType {
        DIAGRAMM,
        ASSOCIATION, GENERALIZATION, REALIZATION,
        COMMENT,
        CLASS, INTERFACE, DATATYPE
    }
    
    public static final IProperty<MagicDrawElementType> MAGICDRAW_TYPE = new Property<MagicDrawElementType>(
            "de.cau.cs.kieler.magicdraw.type", null);
}
