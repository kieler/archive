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

import de.cau.cs.kieler.core.alg.IKielerProgressMonitor;
import de.cau.cs.kieler.core.alg.BasicProgressMonitor;
import de.cau.cs.kieler.core.kgraph.KNode;
import de.cau.cs.kieler.core.properties.IPropertyHolder;
import de.cau.cs.kieler.kiml.AbstractLayoutProvider;
import de.cau.cs.kieler.kiml.klayoutdata.KShapeLayout;

/**
 * Metric class for measurement of execution times.
 * 
 * @author msp
 * @author cds
 */
public class ExecutionTimeMetric extends AbstractMetric {
   
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
    public ExecutionTimeMetric(final AbstractLayoutProvider layoutProvider,
            final OutputStream outputStream, final Parameters parameters,
            final IPropertyHolder propertyHolder) {
        super(layoutProvider, outputStream, parameters, propertyHolder);
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////
    // Measurement

    /**
     * {@inheritDoc}
     */
    @Override
    protected void warmup() throws IOException {
        // Create a set of warmup parameters
        Parameters warmupParameters = new Parameters();
        warmupParameters.minOutEdgesPerNode = 2;
        warmupParameters.maxOutEdgesPerNode = 2;
        
        // Generate a graph
        KNode layoutGraph = graphGenerator.generateGraph(1000, warmupParameters);
        IKielerProgressMonitor progressMonitor = new BasicProgressMonitor();
        
        // Do three runs over it
        for (int i = 0; i < 3; i++) {
            layoutProvider.doLayout(layoutGraph, progressMonitor);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGraphSizeMeasurement(final int nodeCount) throws IOException {
        outputWriter.write(Integer.toString(nodeCount));
        
        System.out.print("n = " + nodeCount + ": ");
        
        double totalTime = 0.0;
        for (int i = 0; i < parameters.graphsPerSize; i++) {
            System.out.print(i);
            
            // Generate a graph with the given node and edge count
            KNode layoutGraph = graphGenerator.generateGraph(nodeCount, parameters);
            if (propertyHolder != null) {
                layoutGraph.getData(KShapeLayout.class).copyProperties(propertyHolder);
            }
            
            // Do a bunch of layout runs and take the one that took the least amount of time
            double minTime = Double.MAX_VALUE;
            for (int j = 0; j < parameters.runsPerGraph; j++) {
                System.gc();
                
                IKielerProgressMonitor progressMonitor = new BasicProgressMonitor();
                layoutProvider.doLayout(layoutGraph, progressMonitor);
                
                minTime = Math.min(minTime, progressMonitor.getExecutionTime());
                
                System.out.print(".");
            }
            
            // Export the graph using the last computed layout, if requested
            if (parameters.exportGraphs) {
                graphGenerator.exportGraph(layoutGraph, i + 1);
            }
            
            totalTime += minTime;
        }
        
        // Calculate the average time taken for the graphs
        double avgTime = totalTime / parameters.graphsPerSize;
        outputWriter.write("," + avgTime);
        System.out.println(" -> " + avgTime);
        
        outputWriter.write("\n");
    }
    
}
