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

import com.nomagic.magicdraw.uml.symbols.PresentationElement;

import de.cau.cs.kieler.core.kgraph.KGraphElement;
import de.cau.cs.kieler.core.properties.IProperty;
import de.cau.cs.kieler.core.properties.Property;

/**
 * Properties to manage the transformation between MagicDraw Diagram and KGraph
 * 
 * @author nbw
 */
public class KGraphMagicDrawProperties {

    /**
     * The identification of generated {@link KGraphElement} to identify elements after the layout
     * [programmatically set]
     */
    public static final IProperty<Integer> MAGICDRAW_ID = new Property<Integer>(
            "de.cau.cs.kieler.magicdraw.id", -1);

    /**
     * Definition of various MagicDraw UML diagram elements. To be accessed using
     * {@link KGraphMagicDrawProperties#MAGICDRAW_TYPE}.
     * 
     * @author nbw
     */
    public enum MagicDrawElementType {
        DIAGRAMM, ASSOCIATION, GENERALIZATION, REALIZATION, COMMENT, CLASS, INTERFACE, DATATYPE
    }

    /**
     * Type of the mapped MagicDraw {@link PresentationElement}. [programmatically set]
     */
    public static final IProperty<MagicDrawElementType> MAGICDRAW_TYPE =
            new Property<MagicDrawElementType>("de.cau.cs.kieler.magicdraw.type", null);
}
