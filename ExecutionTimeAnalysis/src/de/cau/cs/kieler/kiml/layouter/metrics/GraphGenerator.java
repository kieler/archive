/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 *
 * http://www.informatik.uni-kiel.de/rtsys/kieler/
 * 
 * Copyright 2009 by
 * + Kiel University
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 * 
 * This code is provided under the terms of the Eclipse Public License (EPL).
 * See the file epl-v10.html for the license text.
 */
package de.cau.cs.kieler.kiml.layouter.metrics;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import de.cau.cs.kieler.core.kgraph.KEdge;
import de.cau.cs.kieler.core.kgraph.KNode;
import de.cau.cs.kieler.core.kgraph.KPort;
import de.cau.cs.kieler.kiml.klayoutdata.KShapeLayout;
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
     * Randomizer for graph generation.
     */
    private final Random random;
    
    /**
     * Creates a graph generator with given random seed.
     * 
     * @param randomSeed seed for random number generators.
     */
    public GraphGenerator(final int randomSeed) {
        random = new Random(randomSeed);
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////
    // Graph Generation

    /**
     * Generates a random graph of given size.
     * 
     * @param nodeCount number of nodes in the graph.
     * @param parameters user-supplied parameters affecting the graph generation.
     * @return a randomly generated graph.
     * @throws IOException if exporting the graph fails
     */
    public KNode generateGraph(final int nodeCount, final Parameters parameters) throws IOException {
        // If requested, generate a proper layered graph
        if (parameters.properLayered) {
            // these parameters have to be ignored for the proper-layered mode
            parameters.allowSelfLoops = true;
            parameters.allowCycles = true;
            return generateProperLayeredGraph(nodeCount, parameters);
        }
        
        // Create parent node
        KNode graph = KimlUtil.createInitializedNode();
        
        // Create nodes
        KNode[] nodes = new KNode[nodeCount];
        for (int i = 0; i < nodeCount; i++) {
            // Create a node
            nodes[i] = KimlUtil.createInitializedNode();
            nodes[i].setParent(graph);
            
            // Set node properties
            KShapeLayout nodeLayout = nodes[i].getData(KShapeLayout.class);
            nodeLayout.setWidth(NODE_WIDTH);
            nodeLayout.setHeight(NODE_HEIGHT);
            if (parameters.withPorts) {
                nodeLayout.setProperty(LayoutOptions.PORT_CONSTRAINTS, PortConstraints.FIXED_POS);
            }
        }
        
        
        if (parameters.density > 0) {
            // Determine the total number of edges from the given density
            int edgeCount = Math.round(parameters.density * 0.5f * nodeCount * (nodeCount - 1));
            
            // Create edges
            for (int j = 0; j < edgeCount; j++) {
                int sourceIndex, targetIndex;
                int attempt = 0;  // abort the edge creation process if it takes too long
                do {
                    sourceIndex = random.nextInt(nodeCount);
                    targetIndex = random.nextInt(nodeCount);
                    attempt++;
                } while (!createEdge(nodes[sourceIndex], nodes[targetIndex], parameters)
                        && attempt < 2 * nodeCount);
            }
            
        } else if (parameters.relativeEdgeCount > 0) {
            // Determine the total number of edges from the given relative edge count
            int edgeCount = Math.round(parameters.relativeEdgeCount * nodeCount);
            
            // Create edges
            for (int j = 0; j < edgeCount; j++) {
                int sourceIndex, targetIndex;
                int attempt = 0;  // abort the edge creation process if it takes too long
                do {
                    sourceIndex = random.nextInt(nodeCount);
                    targetIndex = random.nextInt(nodeCount);
                    attempt++;
                } while (!createEdge(nodes[sourceIndex], nodes[targetIndex], parameters)
                        && attempt < 2 * nodeCount);
            }
            
        } else if (parameters.maxOutEdgesPerNode >= 0) {
            // If no other option is set, use the min. and max. number of outgoing edges per node
            if (parameters.minOutEdgesPerNode > parameters.maxOutEdgesPerNode) {
                parameters.maxOutEdgesPerNode = parameters.minOutEdgesPerNode;
            }
            // Precalculate the difference between minimal and maximal number of outgoing edges per node
            int edgeCountDiff = parameters.maxOutEdgesPerNode - parameters.minOutEdgesPerNode;
            
            // Create edges
            for (int i = 0; i < nodeCount; i++) {
                // Randomize the number of edges to generate for this node
                int edgeCount = parameters.minOutEdgesPerNode
                        + (edgeCountDiff == 0 ? 0 : random.nextInt(edgeCountDiff + 1));
                
                for (int j = 0; j < edgeCount; j++) {
                    int targetIndex;
                    int attempt = 0;  // abort the edge creation process if it takes too long
                    do {
                        targetIndex = random.nextInt(nodeCount);
                        attempt++;
                    } while (!createEdge(nodes[i], nodes[targetIndex], parameters)
                            && attempt < 2 * nodeCount);
                }
            }
            
        }
        
        return graph;
    }
    
    /**
     * Count the edges in the given graph.
     * 
     * @param graph a graph
     * @return the number of edges
     */
    public int countEdges(final KNode graph) {
        int edgeCount = 0;
        for (KNode node : graph.getChildren()) {
            edgeCount += node.getOutgoingEdges().size();
        }
        return edgeCount;
    }
    
    /**
     * Generates a random proper layered graph of given size.
     * 
     * @param nodeCount number of nodes in the graph.
     * @param parameters user-supplied parameters affecting the graph generation.
     * @return a randomly generated graph.
     * @throws IOException if exporting the graph fails
     */
    private KNode generateProperLayeredGraph(final int nodeCount, final Parameters parameters)
            throws IOException {
        // Create parent node
        KNode graph = KimlUtil.createInitializedNode();
        
        // First determine the number of layers and the number of nodes in each layer
        double meanLayerSize = Math.sqrt(nodeCount);
        LinkedList<Integer> layerSizes = new LinkedList<Integer>();
        int remainingNodes = nodeCount;
        while (remainingNodes > 0) {
            int layerSize = (int) Math.round(meanLayerSize
                    + random.nextGaussian() * meanLayerSize * 0.4);
            if (layerSize < 1) {
                layerSize = 1;
            }
            if (layerSize > remainingNodes) {
                layerSize = remainingNodes;
            }
            layerSizes.add(layerSize);
            remainingNodes -= layerSize;
        }
        
        // Create layers and nodes
        int layerCount = layerSizes.size();
        KNode[][] layers = new KNode[layerCount][];
        for (int l = 0; l < layerCount; l++) {
            layers[l] = new KNode[layerSizes.get(l)];
            for (int i = 0; i < layers[l].length; i++) {
                // Create a node
                layers[l][i] = KimlUtil.createInitializedNode();
                layers[l][i].setParent(graph);
                
                // Set node properties
                KShapeLayout nodeLayout = layers[l][i].getData(KShapeLayout.class);
                nodeLayout.setWidth(NODE_WIDTH);
                nodeLayout.setHeight(NODE_HEIGHT);
                if (parameters.withPorts) {
                    nodeLayout.setProperty(LayoutOptions.PORT_CONSTRAINTS, PortConstraints.FIXED_POS);
                }
            }
        }
        
        if (parameters.density >= 0) {
            // Determine the total number of edges from the given density
            int edgeCount = Math.round(parameters.density * 0.5f * nodeCount * (nodeCount - 1));
            
            // Create edges
            for (int j = 0; j < edgeCount; j++) {
                int sourceLayer = random.nextInt(layerCount - 1);
                int sourceIndex = random.nextInt(layers[sourceLayer].length);
                int targetIndex = random.nextInt(layers[sourceLayer + 1].length);
                createEdge(layers[sourceLayer][sourceIndex], layers[sourceLayer + 1][targetIndex],
                        parameters);
            }
            
        }  else if (parameters.relativeEdgeCount > 0) {
            // Determine the total number of edges from the given relative edge count
            int edgeCount = Math.round(parameters.relativeEdgeCount * nodeCount);
            
            // Create edges
            for (int j = 0; j < edgeCount; j++) {
                int sourceLayer = random.nextInt(layerCount - 1);
                int sourceIndex = random.nextInt(layers[sourceLayer].length);
                int targetIndex = random.nextInt(layers[sourceLayer + 1].length);
                createEdge(layers[sourceLayer][sourceIndex], layers[sourceLayer + 1][targetIndex],
                        parameters);
            }
            
        } else if (parameters.maxOutEdgesPerNode >= 0) {
            // If no other option is set, use the min. and max. number of outgoing edges per node
            if (parameters.minOutEdgesPerNode > parameters.maxOutEdgesPerNode) {
                parameters.maxOutEdgesPerNode = parameters.minOutEdgesPerNode;
            }
            // Precalculate the difference between minimal and maximal number of outgoing edges per node
            int edgeCountDiff = parameters.maxOutEdgesPerNode - parameters.minOutEdgesPerNode;
            
            // Create edges
            for (int l = 0; l < layerCount - 1; l++) {
                for (int i = 0; i < layers[l].length; i++) {
                    // Randomize the number of edges to generate for this node
                    int edgeCount = parameters.minOutEdgesPerNode + random.nextInt(edgeCountDiff);
                    
                    for (int j = 0; j < edgeCount; j++) {
                        int targetIndex = random.nextInt(layers[l + 1].length);
                        createEdge(layers[l][i], layers[l + 1][targetIndex], parameters);
                    }
                }
            }
            
        }
        
        return graph;
    }
    
    /**
     * Export the given graph.
     * 
     * @param graph a graph
     * @param serialNr the serial number to append to file names
     * @throws IOException if writing the graph to a file fails
     */
    public void exportGraph(final KNode graph, final int serialNr) throws IOException {
        // Create a resource set
        ResourceSet resourceSet = new ResourceSetImpl();
         
        // Register the default resource factory
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
            Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());
         
        // Create a resource for the given graph
        String fileName = "graph" + graph.getChildren().size() + "_" + serialNr + ".kgraph";
        URI fileURI = URI.createFileURI(new File(fileName).getAbsolutePath());
        Resource resource = resourceSet.createResource(fileURI);
        resource.getContents().add(graph);
         
        // Save the contents of the resource to the file system.
        resource.save(Collections.EMPTY_MAP);
    }
    
    /**
     * Creates an edge for the given source and target nodes.
     * 
     * @param sourceNode the source node.
     * @param targetNode the target node.
     * @param parameters user-supplied parameters affecting port generation.
     * @return true if the edge was created, or false if it is not allowed
     */
    private boolean createEdge(final KNode sourceNode, final KNode targetNode,
            final Parameters parameters) {
        if (!parameters.allowSelfLoops && sourceNode == targetNode
                || !parameters.allowCycles && reachable(targetNode, sourceNode)) {
            // the edge is not allowed -- find another one
            return false;
        } else {
        
            KEdge edge = KimlUtil.createInitializedEdge();
            edge.setSource(sourceNode);
            edge.setTarget(targetNode);
            if (parameters.withPorts) {
                edge.setSourcePort(createPort(sourceNode, edge, true, parameters));
                edge.setTargetPort(createPort(targetNode, edge, false, parameters));
            }
            return true;
        }
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
                parameters.northSouthPortProb, source);
        portLayout.setProperty(LayoutOptions.PORT_SIDE, portSide);
        portLayout.setProperty(LayoutOptions.OFFSET, 0f);
        
        switch (portSide) {
        case NORTH:
            portLayout.setXpos(random.nextFloat() * NODE_WIDTH);
            portLayout.setYpos(0.0f);
            break;
            
        case EAST:
            portLayout.setXpos(NODE_WIDTH);
            portLayout.setYpos(random.nextFloat() * NODE_HEIGHT);
            break;
            
        case SOUTH:
            portLayout.setXpos(random.nextFloat() * NODE_WIDTH);
            portLayout.setYpos(NODE_HEIGHT);
            break;
            
        case WEST:
            portLayout.setXpos(0.0f);
            portLayout.setYpos(random.nextFloat() * NODE_HEIGHT);
            break;
        }
        
        port.setNode(node);
        
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
        
        float r = random.nextFloat();
        float threshold = invertedSideProb;
        
        if (r < threshold) {
            return source ? PortSide.WEST : PortSide.EAST;
        }
        
        threshold += 0.5 * northSouthSideProb;
        
        if (r < threshold) {
            return PortSide.NORTH;
        }
        
        threshold += 0.5 * northSouthSideProb;
        
        if (r < threshold) {
            return PortSide.SOUTH;
        }
        
        return source ? PortSide.EAST : PortSide.WEST;
    }
    
    /**
     * Determine whether the given target node is reachable from the source node.
     * 
     * @param sourceNode the source node
     * @param targetNode the target node
     * @return true if the target node is reachable
     */
    private boolean reachable(final KNode sourceNode, final KNode targetNode) {
        return reachable(sourceNode, targetNode, new HashSet<KNode>());
    }
    
    /**
     * Determine whether the given target node is reachable from the source node using a recursive DFS.
     * 
     * @param node1 the source node
     * @param targetNode the target node
     * @param visited set of already visited nodes
     * @return true if the target node is reachable
     */
    private boolean reachable(final KNode node1, final KNode targetNode,
            final Set<KNode> visited) {
        visited.add(node1);
        
        for (KEdge outgoing : node1.getOutgoingEdges()) {
            KNode node2 = outgoing.getTarget();
            if (node2 == targetNode
                    || !visited.contains(node2) && reachable(node2, targetNode, visited)) {
                return true;
            }
        }
        return false;
    }
    
}
