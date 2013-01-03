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

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.cau.cs.kieler.core.alg.IKielerProgressMonitor;
import de.cau.cs.kieler.core.alg.BasicProgressMonitor;
import de.cau.cs.kieler.core.kgraph.KEdge;
import de.cau.cs.kieler.core.kgraph.KNode;
import de.cau.cs.kieler.core.math.KVector;
import de.cau.cs.kieler.core.math.KVectorChain;
import de.cau.cs.kieler.core.properties.IPropertyHolder;
import de.cau.cs.kieler.kiml.AbstractLayoutProvider;
import de.cau.cs.kieler.kiml.klayoutdata.KEdgeLayout;
import de.cau.cs.kieler.kiml.klayoutdata.KShapeLayout;

/**
 * Metric class for measurement of the number of edge crossings.
 * 
 * @author msp
 * @author cds
 */
public class EdgeCrossingsMetric extends AbstractMetric {
   
    // CHECKSTYLEOFF MagicNumber    
    
    ///////////////////////////////////////////////////////////////////////////////
    // Constructor
    
    /**
     * Creates an execution time metric instance.
     * 
     * @param layoutProvider the layout provider to examine
     * @param outputStream the output stream to which measurements are written
     * @param parameters user-supplied parameters controlling the graph generation and
     *                   measurement process.
     * @param propertyHolder an optional class that sets properties on generated random
     *                       graphs. Can be used to set layout properties that would
     *                       otherwise be left to default values.
     * @throws IllegalArgumentException if the parameters are not valid.
     */
    public EdgeCrossingsMetric(final AbstractLayoutProvider layoutProvider,
            final OutputStream outputStream, final Parameters parameters,
            final IPropertyHolder propertyHolder) {
        super(layoutProvider, outputStream, parameters, propertyHolder);
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////
    // Measurement
    
    private static final int OUTPUT_WIDTH = 150;
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGraphSizeMeasurement(final int nodeCount) throws IOException {
        
        String startString = "n = " + nodeCount + ": ";
        System.out.print(startString);
        int outPos = startString.length();
        
        long crossSum = 0;
        
        for (int i = 0; i < parameters.graphsPerSize; i++) {

            // Generate a graph with the given node and edge count
            KNode layoutGraph = graphGenerator.generateGraph(nodeCount, parameters);
            if (propertyHolder != null) {
                layoutGraph.getData(KShapeLayout.class).copyProperties(propertyHolder);
            }
            
            outputWriter.write(Integer.toString(nodeCount));
            
            // Do a bunch of layout runs and write the number of crossings for each run
            for (int j = 0; j < parameters.runsPerGraph; j++) {
                
                IKielerProgressMonitor progressMonitor = new BasicProgressMonitor();
                layoutProvider.doLayout(layoutGraph, progressMonitor);
                
                int c = computeNumberOfCrossings(layoutGraph);
                
                outputWriter.write("," + c);
                crossSum += c;
                
                if (outPos >= OUTPUT_WIDTH) {
                    // break the line if the maximal width has been reached
                    System.out.println();
                    outPos = 0;
                }
                System.out.print('.');
                outPos++;
            }
            
            outputWriter.write("\n");
            
            // Export the graph using the last computed layout, if requested
            if (parameters.exportGraphs) {
                graphGenerator.exportGraph(layoutGraph, i + 1);
            }
        }
        long averageCross = crossSum / (parameters.graphsPerSize * parameters.runsPerGraph);
        System.out.println(" -> " + averageCross);
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////
    // Utility Methods
    
    /**
     * Returns whether two line segments have an intersection.
     * 
     * @param p1
     *            start point of the first line segment
     * @param p2
     *            end point of the first line segment
     * @param q1
     *            start point of the second line segment
     * @param q2
     *            end point of the second line segment
     * @return true if the lines have an intersection
     */
    private static boolean hasIntersection(final KVector p1, final KVector p2,
            final KVector q1, final KVector q2) {
        double s = (q2.y - q1.y) * (p2.x - p1.x) - (q2.x - q1.x) * (p2.y - p1.y);
        // are the line segments parallel?
        if (s == 0) {
            return false;
        }
        double a1 = (q2.x - q1.x) * (p1.y - q1.y) - (q2.y - q1.y) * (p1.x - q1.x);
        double a2 = (p2.x - p1.x) * (p1.y - q1.y) - (p2.y - p1.y) * (p1.x - q1.x);
        double t1 = a1 / s;
        double t2 = a2 / s;
        // the line segments intersect when t1 and t2 lie in the interval (0,1)
        return 0 < t1 && t1 < 1 && 0 < t2 && t2 < 1;
    }

    /**
     * Computes the number of crossings between two vector chains.
     * 
     * @param chain1 the first vector chain
     * @param chain2 the second vector chain
     * @return the number of crossings
     */
    private static int computeNumberOfCrossings(final KVectorChain chain1, final KVectorChain chain2) {
        int numberOfCrossings = 0;
        Iterator<KVector> points1 = chain1.iterator();
        KVector p1 = points1.next();
        while (points1.hasNext()) {
            KVector p2 = points1.next();
            numberOfCrossings += computeNumberOfCrossings(p1, p2, chain2);
            p1 = p2;
        }
        return numberOfCrossings;
    }
    
    /**
     * Computes the number of crossings of a line and a vector chain.
     * 
     * @param p1 start point of the line
     * @param p2 end point of the line
     * @param chain2 a vector chain
     * @return the number of crossings
     */
    private static int computeNumberOfCrossings(final KVector p1, final KVector p2,
            final KVectorChain chain2) {
        int numberOfCrossings = 0;
        Iterator<KVector> points2 = chain2.iterator();
        KVector q1 = points2.next();
        while (points2.hasNext()) {
            KVector q2 = points2.next();
            numberOfCrossings += hasIntersection(p1, p2, q1, q2) ? 1 : 0;
            q1 = q2;
        }
        return numberOfCrossings;
    }

    /**
     * Computes the number of edge crossings in the given graph.
     * 
     * @param parentNode parent node of the graph
     * @return the total number of crossings
     */
    private int computeNumberOfCrossings(final KNode parentNode) {
        // collect all edges
        List<KVectorChain> chains = new ArrayList<KVectorChain>();
        for (KNode node : parentNode.getChildren()) {
            for (KEdge edge : node.getOutgoingEdges()) {
                KVectorChain chain = edge.getData(KEdgeLayout.class).createVectorChain();
                chains.add(chain);
            }
        }
        
        // count the number of crossings between all edges of the graph
        int edgeCount = chains.size();
        int sum = 0;
        for (int i = 0; i < edgeCount; i++) {
            KVectorChain chain1 = chains.get(i);
            for (int j = i + 1; j < edgeCount; j++) {
                KVectorChain chain2 = chains.get(j);
                int c = computeNumberOfCrossings(chain1, chain2);
                sum += c;
            }
        }

        return sum;
    }
    
}
