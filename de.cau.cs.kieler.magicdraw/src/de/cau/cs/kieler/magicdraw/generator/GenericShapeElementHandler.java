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

import java.awt.Rectangle;
import java.util.List;
import java.util.Map;

import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.magicdraw.uml.symbols.shapes.ShapeElement;

import de.cau.cs.kieler.core.kgraph.KGraphElement;
import de.cau.cs.kieler.core.kgraph.KNode;
import de.cau.cs.kieler.kiml.klayoutdata.KShapeLayout;
import de.cau.cs.kieler.kiml.util.KimlUtil;
import de.cau.cs.kieler.magicdraw.layout.KIELERMagicDrawProperties;

/**
 * Generic handler that takes simple size informations from MagicDraw Shape Elements and copies
 * these to a new KNode.
 * 
 * @author nbw
 */
public class GenericShapeElementHandler implements IKIELERShapeElementHandler<ShapeElement> {

    /**
     * Adds a MagicDraw {@link ShapeElement} to the KGraph. Generates a new KNode and initializes
     * this KNode with the current size and position of the ShapeElement
     * 
     * @param magicDrawNode
     *            The ShapeElement to be added to the KGraph
     * @param kGraphRoot
     * @param elementsMapping
     * @param elementsByID
     */
    public <U extends ShapeElement> void addElementToKGraph(ShapeElement magicDrawNode,
            KGraphBuilder builder) {
        // Prepare new node
        KNode node = KimlUtil.createInitializedNode();
        KShapeLayout nodeLayout = node.getData(KShapeLayout.class);

        KGraphElement parent = builder.getElementsMapping().get(magicDrawNode.getParent());
        if (parent == null) {
            node.setParent(builder.getkGraphRoot());
        } else {
            node.setParent((KNode) parent);
        }

        // Grab data from MagicDraw and apply to node
        Rectangle magicDrawNodeBounds = magicDrawNode.getBounds();
        nodeLayout.setHeight(magicDrawNodeBounds.height);
        nodeLayout.setWidth(magicDrawNodeBounds.width);
        nodeLayout.setXpos(magicDrawNodeBounds.x);
        nodeLayout.setYpos(magicDrawNodeBounds.y);

        // Store MagicDraw data in properties
        nodeLayout.setProperty(KIELERMagicDrawProperties.MAGICDRAW_ID, builder.getElementsByID().size());

        // Store data in housekeeping data structures
        builder.getElementsByID().add(magicDrawNode);
        builder.getElementsMapping().put(magicDrawNode, node);
    }

}
