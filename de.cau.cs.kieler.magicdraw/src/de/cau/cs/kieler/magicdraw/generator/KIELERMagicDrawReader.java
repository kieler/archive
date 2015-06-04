package de.cau.cs.kieler.magicdraw.generator;

import java.awt.Rectangle;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.nomagic.magicdraw.openapi.uml.PresentationElementsManager;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.uml.DiagramTypeConstants;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.magicdraw.uml.symbols.paths.PathElement;
import com.nomagic.magicdraw.uml.symbols.shapes.DiagramFrameView;
import com.nomagic.magicdraw.uml.symbols.shapes.HeaderView;
import com.nomagic.magicdraw.uml.symbols.shapes.ShapeElement;
import com.nomagic.magicdraw.uml.symbols.shapes.TemplateSignatureView;
import com.nomagic.magicdraw.uml.symbols.shapes.TextAreaView;
import com.nomagic.magicdraw.uml.symbols.shapes.TextBoxView;
import com.nomagic.magicdraw.uml.symbols.shapes.TreeView;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;

import de.cau.cs.kieler.core.kgraph.KNode;
import de.cau.cs.kieler.kiml.formats.KGraphHandler;
import de.cau.cs.kieler.kiml.formats.TransformationData;
import de.cau.cs.kieler.magicdraw.layout.KIELERLayoutException;

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

/**
 * Generator for KGraphs from MagicDraw diagrams
 * 
 * @author nbw
 */
@SuppressWarnings("deprecation")
public class KIELERMagicDrawReader {

    KGraphBuilder builder;

    public KIELERMagicDrawReader() {
        builder = new KGraphBuilder();
    }

    public void transformToKGraph(DiagramPresentationElement dpe) {
        // TreeViews are MagicDraw-hyperedges.
        // We don't like hyperedges apart from our own
        // So remove all TreeViews from the diagram
        // These should only be present in Class Diagramms
        // TODO: Is this really the case?
        if (dpe.getDiagramType().getType().equals(DiagramTypeConstants.UML_CLASS_DIAGRAM)) {
            purgeTreeViews(dpe);
        }

        // Search for DiagramFrame in DiagramPresentationElement to set this as root
        Collection<PresentationElement> childElements = dpe.getPresentationElements();
        Collection<PresentationElement> diagramFrames =
                Collections2.filter(childElements, new Predicate<PresentationElement>() {
                    public boolean apply(PresentationElement pe) {
                        return (pe instanceof DiagramFrameView);
                    }
                });
        if (diagramFrames.size() != 1) {
            // Normally there should only be one Diagram Frame on the top level
            throw new KIELERLayoutException("Found no single Diagram Frame");
        } else {
            // Set root node to diagram frame
            builder.addRootNode(diagramFrames.iterator().next());
        }

        // Collect data in flat structures
        Collection<ShapeElement> nodes = new LinkedList<ShapeElement>();
        Collection<PathElement> edges = new LinkedList<PathElement>();
        recursiveCollect(dpe, nodes, edges);

        // Use handler facade to add all the ShapeElements to the graph
        KIELERShapeElementHandler handler = new KIELERShapeElementHandler();
        for (ShapeElement shapeElement : nodes) {
            handler.addElementToKGraph(shapeElement, builder);
        }

        // Add all the edges to the graph
        // TODO Maybe I can get away with this, maybe I need a similar facade like for the nodes
        for (PathElement pathElement : edges) {
            builder.addEdgeToKGraph(pathElement);
        }

    }

    /**
     * Traverses the complete diagram and collects all relevant nodes and edges. The data is
     * filtered to only contain nodes with a set size and removes some unwanted elements.
     * 
     * @param dpe
     *            The PresentationElement to start the recursion from. The current iteration adds
     *            all relevant children of the element to the lists.
     * @param nodes
     *            The flat collection of nodes to return
     * @param edges
     *            The flat collection of edges to return
     */
    private void recursiveCollect(PresentationElement parent, Collection<ShapeElement> nodes,
            Collection<PathElement> edges) {
        // Make sure that the collections have been initialized
        if (nodes == null)
            nodes = new LinkedList<ShapeElement>();
        if (edges == null)
            edges = new LinkedList<PathElement>();

        // Check if the parent is part of the collection, if not add it.
        if (parent instanceof ShapeElement) {
            if (!(nodes.contains(parent))) {
                nodes.add((ShapeElement) parent);
            }
        }

        // Grab all Presentation Elements and filter out unwanted elements
        Collection<PresentationElement> children = parent.getPresentationElements();
        Collection<ShapeElement> shapes =
                Collections2.transform(
                        Collections2.filter(children, new Predicate<PresentationElement>() {
                            // Filter all the elements
                            public boolean apply(PresentationElement pe) {

                                // Make sure only Shape Elements are here, no Path Elements
                                if (!(pe instanceof ShapeElement))
                                    return false;
                                // Some elements are just boring stuff we don't need at the moment
                                if (pe instanceof DiagramFrameView)
                                    return false;
                                if (pe instanceof HeaderView)
                                    return false;
                                if (pe instanceof TextBoxView)
                                    return false;
                                if (pe instanceof TextAreaView)
                                    return false;
                                if (pe instanceof TemplateSignatureView)
                                    return false;

                                // Remove Elements without a size
                                Rectangle bounds = pe.getBounds();
                                if (bounds.getWidth() == 0 || bounds.getHeight() == 0)
                                    return false;

                                // Remove Things without a Name (Like the sign at the top right of
                                // ComponentViews)
                                if (pe.getName() == null)
                                    return false;

                                // So now I think we should keep the element
                                return true;
                            }
                        }), new Function<PresentationElement, ShapeElement>() {
                            // Cast all elements to ShapeElements to produce a proper formatted list
                            public ShapeElement apply(PresentationElement pe) {
                                return ((ShapeElement) pe);
                            }
                        });
        // Filter the Path elements similar to shape elements
        Collection<PathElement> paths =
                Collections2.transform(
                        Collections2.filter(children, new Predicate<PresentationElement>() {
                            // Only PathElements are allowed beyond this point
                            public boolean apply(PresentationElement pe) {
                                return (pe instanceof PathElement);
                            }
                        }), new Function<PresentationElement, PathElement>() {
                            // So we know they are PathElements so use them as PathElements
                            public PathElement apply(PresentationElement pe) {
                                return ((PathElement) pe);
                            }
                        });

        // Add all this stuff to the collections
        nodes.addAll(shapes);
        edges.addAll(paths);

        // Continue the collection for all other elements
        for (ShapeElement shapeElement : shapes) {
            recursiveCollect(shapeElement, nodes, edges);
        }
    }

    /**
     * <p>
     * Removes TreeViews from diagrams and replaces them with dedicated simple connections.
     * </p>
     * <p>
     * Currently only the root layer is inspected and modified. This should be sufficient because
     * class diagrams should not have hierarchy.
     * </p>
     * 
     * @param dpe
     *            The root {@link DiagramPresentationElement} to clean
     */
    private void purgeTreeViews(DiagramPresentationElement dpe) {
        // Filter all TreeViews from root layer
        Collection<PresentationElement> children = dpe.getPresentationElements();
        Collection<TreeView> trees =
                Collections2.transform(
                        Collections2.filter(children, new Predicate<PresentationElement>() {
                            // Filter all TreeViews
                            public boolean apply(PresentationElement pe) {
                                return (pe instanceof TreeView);
                            }
                        }), new Function<PresentationElement, TreeView>() {
                            // Cast all TreeViews to TreeView
                            public TreeView apply(PresentationElement pe) {
                                return ((TreeView) pe);
                            }
                        });

        if (!trees.isEmpty()) {
            // Prepare Session
            SessionManager sm = SessionManager.getInstance();
            sm.createSession("Removing TreeViews from Diagram");
            // Grab manager to change elements
            PresentationElementsManager manager = PresentationElementsManager.getInstance();
            for (TreeView treeView : trees) {
                List<PathElement> pathElements = treeView.getConnectedPathElements();
                try {
                    // Create new paths for each bundled path
                    for (PathElement pathElement : pathElements) {
                        Element el = pathElement.getElement();
                        manager.createPathElement(el, pathElement.getClient(),
                                pathElement.getSupplier());
                    }
                    // Remove TreeView and attached edges
                    manager.deletePresentationElement(treeView);
                } catch (ReadOnlyElementException e) {
                    e.printStackTrace();
                }
            }
            // Close session and "commit" changes to model
            sm.closeSession();
        }
    }

    /**
     * Serializes a KGraph by passing it to a {@link KGraphHandler}.
     * 
     * @param kGraph
     *            The KGraph to be serialized
     * @return The serialized KGraph in XMI String representation
     */
    public static String serialize(KNode kGraph) {
        // Grab KGraphHandler to manage serialization
        KGraphHandler handler = new KGraphHandler();
        TransformationData<KNode, KNode> transData = new TransformationData<KNode, KNode>();
        transData.getTargetGraphs().add(kGraph);

        // Generate String representation
        String result = handler.serialize(transData);

        return result;
    }

    /**
     * Generates a new KGraph from serialized XMI String
     * 
     * @param kGraphString
     *            The XMI String to deserialize
     * @return KGraph matching the XMI String
     */
    public static KNode deserialize(String kGraphString) {
        // Prepare KGraphHandler and TransformationData
        KGraphHandler handler = new KGraphHandler();
        TransformationData<KNode, KNode> transData = new TransformationData<KNode, KNode>();
        handler.deserialize(kGraphString, transData);

        KNode kGraph = transData.getSourceGraph();
        return kGraph;
    }

    /**
     * Returns the original PresentationElements by the assigned ID
     * 
     * @return List of {@link PresentationElement} ordered by assigned ID in KGraph
     */
    public List<PresentationElement> getElementsByID() {
        return builder.getElementsByID();
    }

    /**
     * The complete KGraph
     * 
     * @return The root {@link KNode} of the generated KGraph.
     */
    public KNode getkGraphRoot() {
        return builder.getkGraphRoot();
    }

}
