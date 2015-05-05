package de.cau.cs.kieler.magicdraw.layout;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.magicdraw.uml.symbols.paths.GeneralizationView;
import com.nomagic.magicdraw.uml.symbols.paths.PathElement;
import com.nomagic.magicdraw.uml.symbols.shapes.ShapeElement;
import com.nomagic.magicdraw.uml.symbols.shapes.TextBoxView;

import de.cau.cs.kieler.core.kgraph.KEdge;
import de.cau.cs.kieler.core.kgraph.KGraphElement;
import de.cau.cs.kieler.core.kgraph.KLabel;
import de.cau.cs.kieler.core.kgraph.KNode;
import de.cau.cs.kieler.kiml.formats.KGraphHandler;
import de.cau.cs.kieler.kiml.formats.TransformationData;
import de.cau.cs.kieler.kiml.klayoutdata.KEdgeLayout;
import de.cau.cs.kieler.kiml.klayoutdata.KShapeLayout;
import de.cau.cs.kieler.kiml.options.EdgeLabelPlacement;
import de.cau.cs.kieler.kiml.options.EdgeType;
import de.cau.cs.kieler.kiml.options.LayoutOptions;
import de.cau.cs.kieler.kiml.util.KimlUtil;

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
public class KGraphMagicDrawAdapter {

    /**
     * Mapping of MagicDraw presentation elements to generated KGraphElements. E.g. used to identify
     * source and target when adding edges
     */
    private Map<PresentationElement, KGraphElement> elementsMapping;

    /**
     * List to store the managed PresentationElements. Used to identify KGraphElements after layout
     */
    private List<PresentationElement> elementsByID;

    /**
     * Root node of the generated KGraph
     */
    private KNode kGraphRoot;

    /**
     * Initializes an adapter to generate a new KGraph from a MagicDraw diagram
     */
    public KGraphMagicDrawAdapter() {
        elementsMapping = new HashMap<PresentationElement, KGraphElement>();
        elementsByID = new ArrayList<PresentationElement>();
    }

    /**
     * Sets the root node for the KGraph. This method must be called before any other nodes are
     * added.
     * 
     * @param magicDrawNode
     *            the MagicDraw element which should be used as root node.
     * @throws {@link MagicDrawAdapterException} when a root node has already been set
     */
    public void addRootNode(PresentationElement magicDrawNode) {
        if (kGraphRoot != null) {
            throw new MagicDrawAdapterException("Trying to add duplicate root node");
        }

        // Prepare root node
        kGraphRoot = KimlUtil.createInitializedNode();
        KShapeLayout rootShapeLayout = kGraphRoot.getData(KShapeLayout.class);

        // Grab data from MagicDraw and apply to node
        Rectangle magicDrawNodeBounds = magicDrawNode.getBounds();
        rootShapeLayout.setHeight(magicDrawNodeBounds.height);
        rootShapeLayout.setWidth(magicDrawNodeBounds.width);
        rootShapeLayout.setXpos(magicDrawNodeBounds.x);
        rootShapeLayout.setYpos(magicDrawNodeBounds.y);

        // Store data in housekeeping data structures
        elementsByID.add(magicDrawNode);
        elementsMapping.put(magicDrawNode, kGraphRoot);
    }

    /**
     * Adds a MagicDraw {@link ShapeElement} to the KGraph. Generates a new KNode and initializes
     * this KNode with the current size and position of the ShapeElement
     * 
     * @param magicDrawNode
     *            The ShapeElement to be added to the KGraph
     * @throws {@link MagicDrawAdapterException} if no root node has been set in the KGraph
     */
    public void addNodeToKGraph(ShapeElement magicDrawNode) {
        if (kGraphRoot == null) {
            throw new MagicDrawAdapterException(
                    "Trying to add new node to KGraph without root node");
        }

        // Prepare new node
        KNode node = KimlUtil.createInitializedNode();
        KShapeLayout nodeLayout = node.getData(KShapeLayout.class);
        node.setParent(kGraphRoot);

        // Grab data from MagicDraw and apply to node
        Rectangle magicDrawNodeBounds = magicDrawNode.getBounds();
        nodeLayout.setHeight(magicDrawNodeBounds.height);
        nodeLayout.setWidth(magicDrawNodeBounds.width);
        nodeLayout.setXpos(magicDrawNodeBounds.x);
        nodeLayout.setYpos(magicDrawNodeBounds.y);

        // Store MagicDraw data in properties
        nodeLayout.setProperty(KGraphMagicDrawProperties.MAGICDRAW_ID, elementsByID.size());

        // Store data in housekeeping data structures
        elementsByID.add(magicDrawNode);
        elementsMapping.put(magicDrawNode, node);
    }

    /**
     * Adds an edge to the KGraph. The source and target of the new edge need to be present in the
     * KGraph.
     * 
     * @param magicDrawPath
     *            The {@link PathElement} to be added to the KGraph
     * @throws {@link MagicDrawAdapterException} if no root node has been set or the source or
     *         target are missing from the KGraph
     */
    public void addEdgeToKGraph(PathElement magicDrawPath) {
        if (kGraphRoot == null) {
            throw new MagicDrawAdapterException(
                    "Trying to add new node to KGraph without root node");
        }

        // Grab supplier and client from MagicDraw component
        PresentationElement supplier = magicDrawPath.getSupplier();
        PresentationElement client = magicDrawPath.getClient();
        // Check if these elements already exist in KGraph
        if (!(elementsMapping.containsKey(supplier) && elementsMapping.containsKey(client))) {
            throw new MagicDrawAdapterException(
                    "Trying to add edge before adding source and target node");
        }
        // Invert source and target due to mismatching direction of edges
        KNode source = (KNode) elementsMapping.get(client);
        KNode target = (KNode) elementsMapping.get(supplier);

        // Prepare new edge
        KEdge edge = KimlUtil.createInitializedEdge();
        edge.setSource(source);
        edge.setTarget(target);

        KEdgeLayout edgeLayout = edge.getData(KEdgeLayout.class);
        // Append generalization type to edges if needed
        if (magicDrawPath instanceof GeneralizationView) {
            edgeLayout.setProperty(LayoutOptions.EDGE_TYPE, EdgeType.GENERALIZATION);
        }
        edgeLayout.setProperty(KGraphMagicDrawProperties.MAGICDRAW_ID, elementsByID.size());

        // Store data in housekeeping data structures
        elementsByID.add(magicDrawPath);
        elementsMapping.put(magicDrawPath, edge);

        // Attach labels to edge
        attachLabels(edge, magicDrawPath);
    }

    /**
     * <p>
     * Searches for edge labels in the MagicDraw diagram and generates corresponding edge labels in
     * the KGraph.
     * </p>
     * <p>
     * The position of the label is determined by the index in the List of child elements.
     * </p>
     * 
     * @param edge
     *            The {@link KEdge} to attach the label to
     * @param pathElement
     *            The MagicDraw {@link PathElement} to parse for labels
     */
    @SuppressWarnings("deprecation")
    private void attachLabels(KEdge edge, PathElement pathElement) {
        // The actual text elements are embedded two layers below the PathElement.
        // The index in this list determines the position (Head, Tail, Center) of the label
        List<PresentationElement> pes = pathElement.getPresentationElements();
        for (int i = 0; i < pes.size(); i++) {
            // Grab all children and search for text elements
            PresentationElement property = pes.get(i);
            for (PresentationElement pe : property.getPresentationElements()) {
                // Labels at hear or tail are TextBoxViews
                // Labels in the center are TextAreaViews
                if (pe instanceof TextBoxView
                        || pe instanceof com.nomagic.magicdraw.uml.symbols.shapes.TextObject) {
                    KLabel label = KimlUtil.createInitializedLabel(edge);
                    KShapeLayout labelLayout = label.getData(KShapeLayout.class);

                    // Insert text for visualization
                    if (pe instanceof TextBoxView) {
                        label.setText(((TextBoxView) pe).getUserText());
                    } else {
                        label.setText(((com.nomagic.magicdraw.uml.symbols.shapes.TextObject) pe)
                                .getText());
                    }

                    // Append position property to the edge, depending on list index
                    switch (i) {
                    case 0:
                        labelLayout.setProperty(LayoutOptions.EDGE_LABEL_PLACEMENT,
                                EdgeLabelPlacement.HEAD);
                        break;
                    case 1:
                        labelLayout.setProperty(LayoutOptions.EDGE_LABEL_PLACEMENT,
                                EdgeLabelPlacement.TAIL);
                        break;
                    case 2:
                        labelLayout.setProperty(LayoutOptions.EDGE_LABEL_PLACEMENT,
                                EdgeLabelPlacement.CENTER);
                        break;
                    default:
                        System.out.println("Unknown label place! " + label.getText());
                        break;
                    }

                    // Set label size
                    labelLayout.setHeight((int) pe.getBounds().getHeight());
                    labelLayout.setWidth((int) pe.getBounds().getWidth());
                    labelLayout.setProperty(KGraphMagicDrawProperties.MAGICDRAW_ID,
                            elementsByID.size());

                    // Store the label data for housekeeping
                    elementsByID.add(pe);
                    elementsMapping.put(pe, label);
                }
            }
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
        return elementsByID;
    }

    /**
     * The complete KGraph
     * @return The root {@link KNode} of the generated KGraph.
     */
    public KNode getkGraphRoot() {
        return kGraphRoot;
    }

}
