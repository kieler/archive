package de.cau.cs.kieler.magicdraw.adapter;

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

import java.util.ArrayList;
import java.util.List;

import com.nomagic.magicdraw.core.options.AbstractDiagramLayouterOptionsGroup;
import com.nomagic.magicdraw.openapi.uml.PresentationElementsManager;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.magicdraw.uml.DiagramTypeConstants;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.magicdraw.uml.symbols.layout.DiagramLayouter;
import com.nomagic.magicdraw.uml.symbols.layout.UMLGraph;
import com.nomagic.magicdraw.uml.symbols.paths.PathElement;
import com.nomagic.magicdraw.uml.symbols.shapes.DiagramFrameView;
import com.nomagic.magicdraw.uml.symbols.shapes.ShapeElement;
import com.nomagic.magicdraw.uml.symbols.shapes.TreeView;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;

import de.cau.cs.kieler.core.kgraph.KNode;

public class KielerDiagramLayouter implements DiagramLayouter {

    public boolean canLayout(DiagramPresentationElement dpe) {
        // Currently only Class Diagrams can be layouted
        String type = dpe.getDiagramType().getType();
        return type.equals(DiagramTypeConstants.UML_CLASS_DIAGRAM);
    }

    public void drawLayoutResults(UMLGraph graph) {
        // TODO Auto-generated method stub
    }

    public String getOptionsID() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean layout(AbstractDiagramLayouterOptionsGroup arg0,
            DiagramPresentationElement arg1,
            @SuppressWarnings("deprecation") com.nomagic.magicdraw.commands.MacroCommand arg2) {
        if (arg1 == null) {
            throw new RuntimeException("No DiagramPresentationElement found for layouting!");
        }

        // Collect Data
        List<ShapeElement> boxes = new ArrayList<ShapeElement>();
        List<PathElement> paths = new ArrayList<PathElement>();
        List<TreeView> trees = new ArrayList<TreeView>();
        List<PresentationElement> stuff = new ArrayList<PresentationElement>();
        DiagramFrameView rootFrame = collect(arg1, boxes, paths, trees, stuff, 1);

        // Remove TreeViews from diagram
        PresentationElementsManager manager = PresentationElementsManager.getInstance();
        for (TreeView treeView : trees) {
            List<PathElement> pathElements = treeView.getConnectedPathElements();
            for (PathElement pathElement : pathElements) {
                Element el = pathElement.getElement();
                try {
                    PathElement pe =
                            manager.createPathElement(el, pathElement.getClient(),
                                    pathElement.getSupplier());
                    paths.add(pe);
                    paths.remove(pathElement);
                } catch (ReadOnlyElementException e) {
                    e.printStackTrace();
                }
            }
            try {
                manager.deletePresentationElement(treeView);
            } catch (ReadOnlyElementException e) {
                e.printStackTrace();
            }
        }

        // Create KGraph
        KGraphMagicDrawAdapter kGraphAdapter = new KGraphMagicDrawAdapter();
        kGraphAdapter.addRootNode(rootFrame);
        for (ShapeElement shapeElement : boxes) {
            kGraphAdapter.addNodeToKGraph(shapeElement);
        }
        for (PathElement pathElement : paths) {
            kGraphAdapter.addEdgeToKGraph(pathElement);
        }

        // Serialize KGraph for layout through WebService
        String kGraphPre = KGraphMagicDrawAdapter.serialize(kGraphAdapter.getkGraphRoot());

        // Layout KGraph through WebService
        String layouted = KIELERLayoutKWebSHandler.layout(kGraphPre);

        // Generate new KGraph from layouted String representation
        KNode kGraph = KGraphMagicDrawAdapter.deserialize(layouted);

        KGraphMagicDrawUpdater updater =
                new KGraphMagicDrawUpdater(kGraph, kGraphAdapter.getElementsByID());
        updater.applyLayout();

        return true;
    }

    private DiagramFrameView collect(PresentationElement element, List<ShapeElement> boxes,
            List<PathElement> paths, List<TreeView> trees, List<PresentationElement> stuff,
            int depth) {
        DiagramFrameView rootFrame = null;
        if (element instanceof DiagramFrameView) {
            rootFrame = (DiagramFrameView) element;
        } else if (element instanceof TreeView) {
            trees.add((TreeView) element);
        } else if (element instanceof ShapeElement) {
            boxes.add((ShapeElement) element);
        } else if (element instanceof PathElement) {
            paths.add((PathElement) element);
        } else {
            stuff.add(element);
        }

        List<PresentationElement> children = element.getPresentationElements();
        if (depth > 0) {
            for (PresentationElement presentationElement : children) {
                DiagramFrameView newRoot =
                        collect(presentationElement, boxes, paths, trees, stuff, depth - 1);
                if (newRoot != null) {
                    rootFrame = newRoot;
                }
            }
        }

        return rootFrame;
    }
}
