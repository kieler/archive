/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 *
 * http://www.informatik.uni-kiel.de/rtsys/kieler/
 * 
 * Copyright 2009 by
 * + Christian-Albrechts-University of Kiel
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 * 
 * This code is provided under the terms of the Eclipse Public License (EPL).
 * See the file epl-v10.html for the license text.
 */
package test.layout;

import de.cau.cs.kieler.core.KielerException;
import de.cau.cs.kieler.core.alg.IKielerProgressMonitor;
import de.cau.cs.kieler.core.alg.BasicProgressMonitor;
import de.cau.cs.kieler.core.kgraph.KEdge;
import de.cau.cs.kieler.core.kgraph.KNode;
import de.cau.cs.kieler.core.kgraph.KPort;
import de.cau.cs.kieler.kiml.layout.klayoutdata.KShapeLayout;
import de.cau.cs.kieler.kiml.layout.options.LayoutDirection;
import de.cau.cs.kieler.kiml.layout.options.LayoutOptions;
import de.cau.cs.kieler.kiml.layout.options.PortConstraints;
import de.cau.cs.kieler.kiml.layout.options.PortSide;
import de.cau.cs.kieler.kiml.layout.util.KimlLayoutUtil;
import de.cau.cs.kieler.klodd.hierarchical.HierarchicalDataflowLayoutProvider;

/**
 * Test class for the layout algorithm release.
 * 
 * @author msp
 */
public class Test {

    /**
     * The main method.
     * 
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        // create a KGraph for layout
        KNode parentNode = createGraph();
        
        // add layout options to the elements of the graph
        addLayoutOptions(parentNode);
        
        // create a progress monitor
        IKielerProgressMonitor progressMonitor = new BasicProgressMonitor();
        
        // create the layout provider
        HierarchicalDataflowLayoutProvider dataflowLayoutProvider =
            new HierarchicalDataflowLayoutProvider();
        
        // perform layout on the created graph
        try {
            dataflowLayoutProvider.doLayout(parentNode, progressMonitor);
        } catch (KielerException exception) {
            exception.printStackTrace();
        }
        
        // output layout information
        printLayoutInfo(parentNode, progressMonitor);
    }
    
    /**
     * Creates a KGraph, represented by a parent node that contains
     * the actual nodes and edges of the graph.
     * 
     * @return a parent node that contains graph elements
     */
    private static KNode createGraph() {
        // create parent node
        KNode parentNode = KimlLayoutUtil.createInitializedNode();
        
        // create child nodes
        KNode childNode1 = KimlLayoutUtil.createInitializedNode();
        childNode1.setParent(parentNode); // This automatically adds the child to the list of its parent's children.
        childNode1.getLabel().setText("node1");
        KNode childNode2 = KimlLayoutUtil.createInitializedNode();
        childNode2.setParent(parentNode);
        childNode2.getLabel().setText("node2");
        
        // create ports
        KPort port1 = KimlLayoutUtil.createInitializedPort();
        port1.setNode(childNode1); // This automatically adds the port to the node's list of ports.
        KPort port2 = KimlLayoutUtil.createInitializedPort();
        port2.setNode(childNode2);
        
        // create edges
        KEdge edge1 = KimlLayoutUtil.createInitializedEdge();
        edge1.setSource(childNode1); // This automatically adds the edge to the node's list of outgoing edges.
        edge1.setTarget(childNode2); // This automatically adds the edge to the node's list of incoming edges.
        edge1.setSourcePort(port1);
        port1.getEdges().add(edge1); // As our ports do not distinguish between incoming and outgoing edges,
        edge1.setTargetPort(port2);  // the edges must be added manually to their list of edges.
        port2.getEdges().add(edge1);
        
        return parentNode;
    }
    
    /**
     * Adds layout options to the elements of the given parent node.
     * 
     * @param parentNode parent node representing a graph
     */
    private static void addLayoutOptions(KNode parentNode) {
        // add options for the parent node
        KShapeLayout parentLayout = KimlLayoutUtil.getShapeLayout(parentNode);
        // set layout direction to horizontal
        LayoutOptions.setLayoutDirection(parentLayout,
                LayoutDirection.HORIZONTAL);
        
        // add options for the child nodes
        for (KNode childNode : parentNode.getChildren()) {
            KShapeLayout childLayout = KimlLayoutUtil.getShapeLayout(childNode);
            // set some width and height for the child
            childLayout.setWidth(30.0f);
            childLayout.setHeight(30.0f);
            // set fixed size for the child
            LayoutOptions.setFixedSize(childLayout, true);
            // set port constraints to fixed port positions
            LayoutOptions.setPortConstraints(childLayout,
                    PortConstraints.FIXED_POS);
            
            // add options for the ports
            int i = 0;
            for (KPort port : childNode.getPorts()) {
                i++;
                KShapeLayout portLayout = KimlLayoutUtil.getShapeLayout(port);
                // set position and side
                portLayout.setYpos(i * 30.0f / (childNode.getPorts().size() + 1));
                if (childNode.getLabel().getText().equals("node1")) {
                    portLayout.setXpos(30.0f);
                    LayoutOptions.setPortSide(portLayout, PortSide.EAST);
                }
                else {
                    portLayout.setXpos(0.0f);
                    LayoutOptions.setPortSide(portLayout, PortSide.WEST);
                }
            }
        }
    }
    
    /**
     * Outputs layout information on the console.
     * 
     * @param parentNode parent node representing a graph
     * @param progressMonitor progress monitor for the layout run
     */
    private static void printLayoutInfo(KNode parentNode,
            IKielerProgressMonitor progressMonitor) {
        // print execution time of the algorithm run
        System.out.println("Execution time: "
                + progressMonitor.getExecutionTime() * 1000 + " ms");
        
        // print position of each node
        for (KNode childNode : parentNode.getChildren()) {
            KShapeLayout childLayout = KimlLayoutUtil.getShapeLayout(childNode);
            System.out.println(childNode.getLabel().getText() + ": x = "
                    + childLayout.getXpos() + ", y = " + childLayout.getYpos());
        }
    }

}
