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
package de.cau.cs.kieler.kiml.layouter.metrics;

import java.util.Random;

import de.cau.cs.kieler.core.kgraph.KEdge;
import de.cau.cs.kieler.core.kgraph.KNode;
import de.cau.cs.kieler.core.kgraph.KPort;
import de.cau.cs.kieler.kiml.klayoutdata.KShapeLayout;
import de.cau.cs.kieler.kiml.options.Direction;
import de.cau.cs.kieler.kiml.options.LayoutOptions;
import de.cau.cs.kieler.kiml.options.PortConstraints;
import de.cau.cs.kieler.kiml.options.PortSide;
import de.cau.cs.kieler.kiml.util.KimlUtil;

/**
 * Generator for random graphs.
 * 
 * @author msp
 * @author cds
 */
public final class GraphGenerator {
    
    // CHECKSTYLEOFF MagicNumber
    
    ///////////////////////////////////////////////////////////////////////////////
    // Constants
    
    /**
     * Width of generated nodes.
     */
    private static final float NODE_WIDTH = 20.0f;
    
    /**
     * Height of generated nodes.
     */
    private static final float NODE_HEIGHT = 20.0f;
    
    
    ///////////////////////////////////////////////////////////////////////////////
    // Variables
    
    /**
     * Randomizer to determine port placements.
     */
    private Random randomizer = null;
    
    
    ///////////////////////////////////////////////////////////////////////////////
    // Constructor
    
    /**
     * Creates a new instance.
     */
    public GraphGenerator() {
        randomizer = new Random();
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////
    // Graph Generation

    /**
     * Generates a random graph of given size.
     * 
     * @param nodeCount number of nodes in the graph.
     * @param edgesPerNode number of outgoing edges for each node.
     * @param withPorts if true, ports are generated and connected to the edges.
     * @param parameters user-supplied paramaters affecting the graph generation.
     * @return a randomly generated graph.
     */
    public KNode generateGraph(final int nodeCount, final int edgesPerNode,
            final boolean withPorts, final Parameters parameters) {
        
        // Create parent node
        KNode layoutGraph = KimlUtil.createInitializedNode();
        KShapeLayout nodeLayout = layoutGraph.getData(KShapeLayout.class);
        nodeLayout.setProperty(LayoutOptions.DIRECTION, Direction.RIGHT);
        
        // Create nodes
        KNode[] nodes = new KNode[nodeCount];
        for (int i = 0; i < nodeCount; i++) {
            // Create a node
            nodes[i] = KimlUtil.createInitializedNode();
            nodes[i].getLabel().setText("N" + i);
            nodes[i].setParent(layoutGraph);
            
            // Set node properties
            nodeLayout = nodes[i].getData(KShapeLayout.class);
            nodeLayout.setWidth(NODE_WIDTH);
            nodeLayout.setHeight(NODE_HEIGHT);
            nodeLayout.setProperty(LayoutOptions.FIXED_SIZE, true);
            if (withPorts) {
                nodeLayout.setProperty(LayoutOptions.PORT_CONSTRAINTS, PortConstraints.FIXED_POS);
            }
        }
        
        // Create edges
        for (int i = 0; i < nodeCount; i++) {
            for (int j = 0; j < edgesPerNode; j++) {
                KEdge edge = KimlUtil.createInitializedEdge();
                edge.setSource(nodes[i]);
                int targetIndex = (int) (Math.random() * nodeCount);
                edge.setTarget(nodes[targetIndex]);
                if (withPorts) {
                    edge.setSourcePort(createPort(nodes[i], edge, true, parameters));
                    edge.setTargetPort(createPort(nodes[targetIndex], edge, false, parameters));
                }
            }
        }
        
        return layoutGraph;
    }
    
    /**
     * Creates a port, adds it to the given node at a random side, and adds the
     * given edge to the new port.
     * 
     * @param node node for the new port.
     * @param edge edge to connect to the new port.
     * @param source {@code true} if the port to be created will be the source port
     *               of the edge, {@code false} if it will be its target port.
     * @param parameters user-supplied parameters affecting port generation.
     * @return a new port.
     */
    private KPort createPort(final KNode node, final KEdge edge, final boolean source,
            final Parameters parameters) {
        
        KPort port = KimlUtil.createInitializedPort();
        KShapeLayout portLayout = port.getData(KShapeLayout.class);
        PortSide portSide = determinePortPlacement(parameters.invertedPortProb,
                parameters.northSouthPortProb,
                source);
        portLayout.setProperty(LayoutOptions.PORT_SIDE, portSide);
        
        switch (portSide) {
        case NORTH:
            portLayout.setXpos((float) (Math.random() * NODE_WIDTH));
            portLayout.setYpos(0.0f);
            break;
            
        case EAST:
            portLayout.setXpos(NODE_WIDTH);
            portLayout.setYpos((float) (Math.random() * NODE_HEIGHT));
            break;
            
        case SOUTH:
            portLayout.setXpos((float) (Math.random() * NODE_WIDTH));
            portLayout.setYpos(NODE_HEIGHT);
            break;
            
        case WEST:
            portLayout.setXpos(0.0f);
            portLayout.setYpos((float) (Math.random() * NODE_HEIGHT));
            break;
        }
        
        port.setNode(node);
        port.getEdges().add(edge);
        
        return port;
    }
    
    /**
     * Calculates a port placement.
     * 
     * @param invertedSideProb the probability for ports to be placed on an odd side.
     * @param northSouthSideProb the probability for ports to be placed on the northern or
     *                           southern side.
     * @param source {@code true} if the port is the source of an edge, {@code false}
     *               otherwise.
     * @return a port placement.
     */
    private PortSide determinePortPlacement(final float invertedSideProb, final float northSouthSideProb,
            final boolean source) {
        
        float random = randomizer.nextFloat();
        float border = invertedSideProb;
        
        if (random < border) {
            return source ? PortSide.WEST : PortSide.EAST;
        }
        
        border += 0.5 * northSouthSideProb;
        
        if (random < border) {
            return PortSide.NORTH;
        }
        
        border += 0.5 * northSouthSideProb;
        
        if (random < border) {
            return PortSide.SOUTH;
        }
        
        return source ? PortSide.EAST : PortSide.WEST;
    }
}
