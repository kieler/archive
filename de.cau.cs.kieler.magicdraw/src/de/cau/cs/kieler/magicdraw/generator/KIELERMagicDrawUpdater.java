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

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

import com.nomagic.magicdraw.openapi.uml.PresentationElementsManager;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.magicdraw.uml.symbols.paths.PathElement;
import com.nomagic.magicdraw.uml.symbols.shapes.DiagramFrameView;

import de.cau.cs.kieler.core.kgraph.KEdge;
import de.cau.cs.kieler.core.kgraph.KLabel;
import de.cau.cs.kieler.core.kgraph.KNode;
import de.cau.cs.kieler.kiml.klayoutdata.KEdgeLayout;
import de.cau.cs.kieler.kiml.klayoutdata.KPoint;
import de.cau.cs.kieler.kiml.klayoutdata.KShapeLayout;
import de.cau.cs.kieler.magicdraw.layout.KGraphMagicDrawProperties;

/**
 * Applies the layout from a KGraph to the MagicDraw Presentation.
 * 
 * @author nbw
 */
public class KIELERMagicDrawUpdater {

    /**
     * Update the MagicDraw diagram with the positions from the KGraph
     * 
     * @param elementsByID The stored {@link PresentationElement}s of the MagicDraw diagram
     * @param kGraphRoot The KGraph with layout data
     */
    public static void applyLayout(List<PresentationElement> elementsByID, KNode kGraphRoot) {
        PresentationElementsManager manager = PresentationElementsManager.getInstance();

        // Try to set drawing area prior to layout to ensure enough space is available
        DiagramFrameView diagramFrame = null;
        for (PresentationElement presentationElement : elementsByID) {
            if (presentationElement instanceof DiagramFrameView) {
                diagramFrame = (DiagramFrameView) presentationElement;
                break;
            }
        }
        KShapeLayout rootFrameLayout = kGraphRoot.getData(KShapeLayout.class);
        Rectangle rootFrameRectangle =
                new Rectangle((int) rootFrameLayout.getXpos(), (int) rootFrameLayout.getYpos(),
                        (int) rootFrameLayout.getWidth(), (int) rootFrameLayout.getHeight());
        try {
            manager.reshapeShapeElement(diagramFrame, rootFrameRectangle);
        } catch (ReadOnlyElementException e) {
            e.printStackTrace();
        }

        // Move all nodes according to new layout
        Iterator<EObject> iterator = kGraphRoot.eAllContents();
        while (iterator.hasNext()) {
            EObject eObject = iterator.next();
            if (eObject instanceof KNode) {
                KShapeLayout ksl = ((KNode) eObject).getData(KShapeLayout.class);
                int elementID = ksl.getProperty(KGraphMagicDrawProperties.MAGICDRAW_ID);
                Point pos = new Point((int) ksl.getXpos(), (int) ksl.getYpos());
                try {
                    manager.movePresentationElement(elementsByID.get(elementID), pos);
                } catch (ReadOnlyElementException e) {
                    e.printStackTrace();
                }
            }
        }

        // Move all edges according to new layout
        iterator = kGraphRoot.eAllContents();
        while (iterator.hasNext()) {
            EObject eObject = iterator.next();
            if (eObject instanceof KEdge) {
                KEdgeLayout kel = ((KEdge) eObject).getData(KEdgeLayout.class);
                int elementID = kel.getProperty(KGraphMagicDrawProperties.MAGICDRAW_ID);

                List<Point> newBreakPoints = new ArrayList<Point>(kel.getBendPoints().size() + 2);

                Point clientPoint =
                        new Point((int) kel.getSourcePoint().getX(), (int) kel.getSourcePoint()
                                .getY());
                Point supplierPoint =
                        new Point((int) kel.getTargetPoint().getX(), (int) kel.getTargetPoint()
                                .getY());

                newBreakPoints.add(supplierPoint);
                for (KPoint point : kel.getBendPoints()) {
                    newBreakPoints.add(1, new Point((int) point.getX(), (int) point.getY()));
                }
                newBreakPoints.add(clientPoint);

                try {
                    manager.changePathPoints((PathElement) elementsByID.get(elementID),
                            supplierPoint, clientPoint, newBreakPoints);
                } catch (ReadOnlyElementException e) {
                    e.printStackTrace();
                }
            }
        }

        // Move all labels according to new layout
        iterator = kGraphRoot.eAllContents();
        while (iterator.hasNext()) {
            EObject eObject = iterator.next();
            if (eObject instanceof KLabel) {
                KShapeLayout ksl = ((KLabel) eObject).getData(KShapeLayout.class);
                int elementID = ksl.getProperty(KGraphMagicDrawProperties.MAGICDRAW_ID);
                Point pos = new Point((int) ksl.getXpos(), (int) ksl.getYpos());
                try {
                    manager.movePresentationElement(elementsByID.get(elementID), pos);
                } catch (ReadOnlyElementException e) {
                    e.printStackTrace();
                }
            }
        }

        // Set drawing area again to cut away surplus space
        try {
            manager.reshapeShapeElement(diagramFrame, rootFrameRectangle);
        } catch (ReadOnlyElementException e) {
            e.printStackTrace();
        }

    }
}
