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
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.magicdraw.uml.symbols.shapes.ComponentHeaderView;
import com.nomagic.magicdraw.uml.symbols.shapes.ComponentView;
import com.nomagic.magicdraw.uml.symbols.shapes.ShapeElement;
import com.nomagic.magicdraw.uml.symbols.shapes.StereotypeIconView;

import de.cau.cs.kieler.core.kgraph.KGraphElement;
import de.cau.cs.kieler.core.kgraph.KNode;
import de.cau.cs.kieler.kiml.klayoutdata.KShapeLayout;
import de.cau.cs.kieler.kiml.options.LayoutOptions;
import de.cau.cs.kieler.kiml.options.SizeConstraint;
import de.cau.cs.kieler.kiml.util.KimlUtil;
import de.cau.cs.kieler.magicdraw.layout.KIELERLayoutException;
import de.cau.cs.kieler.magicdraw.layout.KIELERMagicDrawProperties;

/**
 * Specialized handler for ComponentViews. These can contain hierarchy and need to take care of
 * insets.
 * 
 * @author nbw
 */
@SuppressWarnings("deprecation")
public class ComponentViewHandler implements IKIELERShapeElementHandler<ComponentView> {

    /**
     * {@inheritDoc}
     */
    public <U extends ShapeElement> void addElementToKGraph(ComponentView pe, KGraphBuilder builder) {

        // Prepare new node
        KNode node = KimlUtil.createInitializedNode();
        KShapeLayout nodeLayout = node.getData(KShapeLayout.class);

        KGraphElement parent = builder.getElementsMapping().get(pe.getParent());
        if (parent == null) {
            node.setParent(builder.getkGraphRoot());
        } else {
            node.setParent((KNode) parent);
        }

        // Grab size and position from MagicDraw and apply to node
        Rectangle magicDrawNodeBounds = pe.getBounds();
        nodeLayout.setHeight(magicDrawNodeBounds.height);
        nodeLayout.setWidth(magicDrawNodeBounds.width);
        nodeLayout.setXpos(magicDrawNodeBounds.x);
        nodeLayout.setYpos(magicDrawNodeBounds.y);

        // Grab insets for the header text lines
        Collection<ComponentHeaderView> headers =
                Collections2.transform(Collections2.filter(pe.getPresentationElements(),
                        new Predicate<PresentationElement>() {
                            public boolean apply(PresentationElement input) {
                                return (input instanceof ComponentHeaderView);
                            }
                        }), new Function<PresentationElement, ComponentHeaderView>() {
                    public ComponentHeaderView apply(PresentationElement input) {
                        return (ComponentHeaderView) input;
                    }
                });
        // There should be only one header element, throw exception otherwise
        if (headers.size() != 1) {
            throw new KIELERLayoutException("No single Header View found");
        }

        // Grab all relevant elements with a set size in the header
        Collection<PresentationElement> elements =
                Collections2.filter(headers.iterator().next().getPresentationElements(),
                        new Predicate<PresentationElement>() {
                            public boolean apply(PresentationElement input) {
                                // Throw away elements without any size constraints
                                if (input.getBounds().getWidth() == 0
                                        || input.getBounds().getHeight() == 0) {
                                    return false;
                                }
                                // Throw away the small icon in the top right
                                if (input instanceof StereotypeIconView) {
                                    return false;
                                }
                                return true;
                            }
                        });
        // Add all the heights and set as inset on the KNode
        for (PresentationElement pes : elements) {
            Rectangle headerSize = pes.getBounds();
            nodeLayout.getInsets().setTop(
                    nodeLayout.getInsets().getTop() + (float) headerSize.getHeight());
        }

        // Store identification in properties
        nodeLayout.setProperty(KIELERMagicDrawProperties.MAGICDRAW_ID, builder.getElementsByID().size());

        // Allow resizing of node to make child elements more compact
        nodeLayout.setProperty(LayoutOptions.SIZE_CONSTRAINT, SizeConstraint.minimumSize());

        // Store data in housekeeping data structures
        builder.getElementsByID().add(pe);
        builder.getElementsMapping().put(pe, node);
    }
}
