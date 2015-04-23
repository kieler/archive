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

import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;

import com.google.common.io.Files;
import com.nomagic.magicdraw.core.options.AbstractDiagramLayouterOptionsGroup;
import com.nomagic.magicdraw.openapi.uml.PresentationElementsManager;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.magicdraw.uml.DiagramType;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.magicdraw.uml.symbols.layout.DiagramLayouter;
import com.nomagic.magicdraw.uml.symbols.layout.UMLGraph;
import com.nomagic.magicdraw.uml.symbols.paths.GeneralizationView;
import com.nomagic.magicdraw.uml.symbols.paths.PathElement;
import com.nomagic.magicdraw.uml.symbols.shapes.DiagramFrameView;
import com.nomagic.magicdraw.uml.symbols.shapes.ShapeElement;
import com.nomagic.magicdraw.uml.symbols.shapes.TreeView;

import de.cau.cs.kieler.core.kgraph.KEdge;
import de.cau.cs.kieler.core.kgraph.KGraphElement;
import de.cau.cs.kieler.core.kgraph.KNode;
import de.cau.cs.kieler.kiml.formats.KGraphHandler;
import de.cau.cs.kieler.kiml.formats.TransformationData;
import de.cau.cs.kieler.kiml.klayoutdata.KEdgeLayout;
import de.cau.cs.kieler.kiml.klayoutdata.KPoint;
import de.cau.cs.kieler.kiml.klayoutdata.KShapeLayout;
import de.cau.cs.kieler.kiml.options.EdgeRouting;
import de.cau.cs.kieler.kiml.options.EdgeType;
import de.cau.cs.kieler.kiml.options.LayoutOptions;
import de.cau.cs.kieler.kiml.util.KimlUtil;
import de.cau.cs.kieler.kwebs.client.KIELERLayout;

public class KielerDiagramLayouter implements DiagramLayouter {

    @Override
    public boolean canLayout(DiagramPresentationElement dpe) {
        System.out.println("Checking whether KIELER can layout this diagram.");
        return (dpe.getDiagramType().equals(DiagramType.UML_CLASS_DIAGRAM));
    }

    @Override
    public void drawLayoutResults(UMLGraph graph) {
        System.out.println("UML Graph: " + graph.toString());
    }

    @Override
    public String getOptionsID() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean layout(AbstractDiagramLayouterOptionsGroup arg0,
            DiagramPresentationElement arg1,
            @SuppressWarnings("deprecation") com.nomagic.magicdraw.commands.MacroCommand arg2) {

        // Check for valid data
        System.out.println("<<<<<-----+++++ Begin KIELER Layout Output +++++----->>>>>");
        if (arg0 != null) {
            System.out.println("AbstractDiagramLayouterOptionsGroup: " + arg0.toString());
        }
        if (arg1 == null) {
            System.out.println("No DiagramPresentationElement found");
            System.out.println("<<<<<-----+++++ End KIELER Layout Output +++++----->>>>>");
            return false;
        }

        List<ShapeElement> boxes = new ArrayList<>();
        List<PathElement> paths = new ArrayList<>();
        List<PresentationElement> stuff = new ArrayList<>();

        // Collect Data
        System.out.println("KIELER: Collecting data from PresentationElements");
        collect(arg1, boxes, paths, stuff, 1);

        // Print Data
        // System.out.println("Knoten gefunden:");
        // for (ShapeElement shape : boxes) {
        // System.out.println(shape.getHumanName()
        // + " AT (" + shape.getBounds().x + "," + shape.getBounds().y
        // + ") WITH SIZE (" + shape.getBounds().width + "," + shape.getBounds().height + ")");
        // }

        // System.out.println("Pfade gefunden:");
        // for (PathElement path : paths) {
        // System.out.println(path.getHumanName()
        // + " AT (" + path.getBounds().x + "," + path.getBounds().y
        // + ") WITH SIZE (" + path.getBounds().width + "," + path.getBounds().height + ")");
        // System.out.println("Connecting " + path.getSupplier().getHumanName() + " and " +
        // path.getClient().getHumanName());
        // System.out.println("Significant points: ");
        // List<Point> points = path.getBreakPoints();
        // for (Point point : points) {
        // System.out.println(point);
        // }
        // }
        System.out.println("KIELER: Found " + boxes.size() + " nodes and " + paths.size()
                + " edges.");
        System.out.println("KIELER: Other Stuff, currently not used:");
        for (PresentationElement presentationElement : stuff) {
            System.out.println(presentationElement.getHumanType() + " AT "
                    + presentationElement.getBounds().toString());
        }

        // Create KGraph
        System.out.println("KIELER: Creating KGraph Data");
        KNode root = KimlUtil.createInitializedNode();
        Map<PresentationElement, KGraphElement> mapping = new HashMap<>();
        List<PresentationElement> elementIds = new ArrayList<>();

        // root.getData(KShapeLayout.class).getInsets().setTop(40.0f);

        Integer id = 0;

        for (ShapeElement shapeElement : boxes) {
            if (!(shapeElement instanceof TreeView)) {
                if (!(shapeElement instanceof DiagramFrameView)) {
                    KNode element = KimlUtil.createInitializedNode();
                    element.getData(KShapeLayout.class).setHeight(shapeElement.getBounds().height);
                    element.getData(KShapeLayout.class).setWidth(shapeElement.getBounds().width);
                    element.getData(KShapeLayout.class).setXpos(shapeElement.getBounds().x);
                    element.getData(KShapeLayout.class).setYpos(shapeElement.getBounds().y);
                    element.getData(KShapeLayout.class).setProperty(KGraphMagicDrawAdapter.MAGICDRAWID, id);
                    element.setParent(root);
                    mapping.put(shapeElement, element);
                } else {
                    root.getData(KShapeLayout.class).setHeight(shapeElement.getBounds().height);
                    root.getData(KShapeLayout.class).setWidth(shapeElement.getBounds().width);
                    root.getData(KShapeLayout.class).setXpos(shapeElement.getBounds().x);
                    root.getData(KShapeLayout.class).setYpos(shapeElement.getBounds().y);
                    root.getData(KShapeLayout.class).setProperty(KGraphMagicDrawAdapter.MAGICDRAWID, id);
                    mapping.put(shapeElement, root);
                }
                elementIds.add(id, shapeElement);
                id += 1;
            }
        }

        for (PathElement pathElement : paths) {
            KEdge edge = KimlUtil.createInitializedEdge();
            edge.setSource((KNode) mapping.get(pathElement.getClient()));
            edge.setTarget((KNode) mapping.get(pathElement.getSupplier()));
            if (pathElement instanceof GeneralizationView) {
                edge.getData(KEdgeLayout.class).setProperty(LayoutOptions.EDGE_TYPE,
                        EdgeType.GENERALIZATION);
            }
            // if (pathElement instanceof InterfaceRealizationView) {
            // edge.getData(KEdgeLayout.class).setProperty(LayoutOptions.EDGE_TYPE,
            // EdgeType.GENERALIZATION);
            // }
            edge.getData(KEdgeLayout.class).setProperty(KGraphMagicDrawAdapter.MAGICDRAWID, id);
            mapping.put(pathElement, edge);
            elementIds.add(id, pathElement);
            id += 1;
        }

        System.out.println("KIELER: KGraph created, sending data to layout engine");

        KGraphHandler handler = new KGraphHandler();
        TransformationData<KNode, KNode> transData = new TransformationData<>();
        transData.getTargetGraphs().add(root);
        String graph = handler.serialize(transData);

        // System.out.println(graph);

        // local server
        final String server = "http://layout.rtsys.informatik.uni-kiel.de:9444";

        // some options
        Map<String, Object> options = new HashMap<String, Object>();

        options.put(LayoutOptions.SPACING.getId(), 30.0f);
        options.put(LayoutOptions.ALGORITHM.getId(), "de.cau.cs.kieler.kiml.ogdf.planarization");
        options.put(LayoutOptions.EDGE_ROUTING.getId(), EdgeRouting.ORTHOGONAL.toString());
        options.put(LayoutOptions.BORDER_SPACING.getId(), 40.0f);

        // perform the layout
        String layouted =
                KIELERLayout.layout(server, "de.cau.cs.kieler.kgraph", "de.cau.cs.kieler.kgraph",
                        options, graph);
        String layoutedDebug =
                KIELERLayout
                        .layout(server, "de.cau.cs.kieler.kgraph", "org.w3.svg", options, graph);
        layoutedDebug = layoutedDebug.replaceFirst("w=\"\\d*\"", "");
        try {
            Files.write(layoutedDebug.getBytes(), new File("/tmp/debugLayout.svg"));
        } catch (IOException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }

        // System.out.println(layouted);

        TransformationData<KNode, KNode> layoutedTransData = new TransformationData<>();
        handler.deserialize(layouted, layoutedTransData);

        KNode layoutedKGraph = layoutedTransData.getSourceGraph();

        // Graph layouted
        PresentationElementsManager manager = PresentationElementsManager.getInstance();
        // Set drawing area
        int i = 0;
        PresentationElement diagramFrame = elementIds.get(i);
        while (!(diagramFrame instanceof DiagramFrameView)) {
            ++i;
            diagramFrame = elementIds.get(i);
        }
        try {
            manager.reshapeShapeElement((ShapeElement) diagramFrame, new Rectangle(
                    (int) layoutedKGraph.getData(KShapeLayout.class).getXpos(),
                    (int) layoutedKGraph.getData(KShapeLayout.class).getYpos(),
                    (int) layoutedKGraph.getData(KShapeLayout.class).getWidth(),
                    (int) layoutedKGraph.getData(KShapeLayout.class).getHeight()));
        } catch (ReadOnlyElementException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        // Set all nodes and edges
        Iterator<EObject> iterator = layoutedKGraph.eAllContents();
        while (iterator.hasNext()) {
            EObject eObject = iterator.next();
            if (eObject instanceof KNode) {
                KShapeLayout ksl = ((KNode) eObject).getData(KShapeLayout.class);
                try {
                    manager.movePresentationElement(elementIds.get(ksl.getProperty(KGraphMagicDrawAdapter.MAGICDRAWID)),
                            new Point((int) ksl.getXpos(), (int) ksl.getYpos()));
                } catch (ReadOnlyElementException e1) {
                    e1.printStackTrace();
                }
            } else if (eObject instanceof KEdge) {
                KEdgeLayout kel = ((KEdge) eObject).getData(KEdgeLayout.class);
                PathElement edge = (PathElement) elementIds.get(kel.getProperty(KGraphMagicDrawAdapter.MAGICDRAWID));

                List<Point> newBreakPoints = new ArrayList<>(kel.getBendPoints().size() + 2);
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
                    manager.changePathPoints(edge, supplierPoint, clientPoint, newBreakPoints);
                } catch (ReadOnlyElementException e1) {
                    e1.printStackTrace();
                }
            }
        }

        // Set drawing area
        try {
            manager.reshapeShapeElement((ShapeElement) diagramFrame, new Rectangle(
                    (int) layoutedKGraph.getData(KShapeLayout.class).getXpos(),
                    (int) layoutedKGraph.getData(KShapeLayout.class).getYpos(),
                    (int) layoutedKGraph.getData(KShapeLayout.class).getWidth(),
                    (int) layoutedKGraph.getData(KShapeLayout.class).getHeight()));
        } catch (ReadOnlyElementException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        System.out.println("<<<<<-----+++++ End KIELER Layout Output +++++----->>>>>");
        return true;
    }

    private void collect(PresentationElement element, List<ShapeElement> boxes,
            List<PathElement> paths, List<PresentationElement> stuff, int depth) {
        if (element instanceof ShapeElement) {
            boxes.add((ShapeElement) element);
        } else if (element instanceof PathElement) {
            paths.add((PathElement) element);
        } else {
            stuff.add(element);
        }

        List<PresentationElement> children = element.getPresentationElements();
        if (depth > 0) {
            for (PresentationElement presentationElement : children) {
                collect(presentationElement, boxes, paths, stuff, depth - 1);
            }
        }
    }
}
