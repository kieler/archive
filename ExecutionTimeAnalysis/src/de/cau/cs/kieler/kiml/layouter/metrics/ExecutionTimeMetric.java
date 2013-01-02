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
import java.io.OutputStreamWriter;

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
public class ExecutionTimeMetric {
   
    // CHECKSTYLEOFF MagicNumber
    
    
    ///////////////////////////////////////////////////////////////////////////////
    // Variables
    
    /**
     * The layout provider.
     */
    private AbstractLayoutProvider layoutProvider;
    
    /**
     * The output stream writer.
     */
    private OutputStreamWriter outputWriter;
    
    /**
     * The parameters.
     */
    private Parameters parameters;
    
    /**
     * An optional class that can set additional properties on generated random graphs.
     */
    private IPropertyHolder propertyHolder;
    
    /**
     * Generator used to generate random graphs.
     */
    private GraphGenerator graphGenerator;
    
    
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
        
        this.layoutProvider = layoutProvider;
        this.outputWriter = new OutputStreamWriter(outputStream);
        this.parameters = parameters;
        this.propertyHolder = propertyHolder;
        this.graphGenerator = new GraphGenerator();
        
        validateParameters();
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////
    // Measurement
    
    /**
     * Performs an execution time measurement parameterized according to the parameters
     * given when this class was instantiated.
     * 
     * @throws IOException if writing to the output stream fails
     */
    public void measure() throws IOException {
        // Warmup. Warmup! ROAAAAAAAR!!!
        warmup();
        
        try {
            int currentDecade = parameters.startDecade;
            double currentSize = 1;
            for (int d = 0; d < parameters.startDecade; d++) {
                currentSize *= 10;
            }
            
            double incFactor = Math.pow(10, 1.0 / parameters.graphSizesPerDecade);
            
            while (currentDecade < parameters.endDecade) {
                for (int i = 0; i < parameters.graphSizesPerDecade; i++) {
                    doGraphSizeMeasurement((int) Math.round(currentSize));
                    currentSize *= incFactor;
                }
                currentDecade++;
            }
            
            doGraphSizeMeasurement((int) Math.round(currentSize));
        } finally {
            outputWriter.flush();
        }
    }

    /**
     * Warms up the layout provider and system cache by performing some dummy layouts.
     */
    private void warmup() throws IOException {
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
     * Performs an execution time measurement for the given number of nodes.
     * 
     * @param nodeCount number of nodes for generated graphs
     * @throws IOException if writing to the output stream fails
     */
    private void doGraphSizeMeasurement(final int nodeCount) throws IOException {
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
    
    
    ///////////////////////////////////////////////////////////////////////////////
    // Utility Methods
    
    /**
     * Validates the parameters and throws an exception if something's not right.
     * 
     * @throws IllegalArgumentException if the parameters are not valid.
     */
    private void validateParameters() {
        // Start and end decade
        if (parameters.startDecade > parameters.endDecade || parameters.startDecade < 0) {
            throw new IllegalArgumentException("Start decade must be non-negative"
                    + " and less or equal than end decade.");
        }
        
        // Graph sizes and runs
        if (parameters.graphSizesPerDecade < 1) {
            throw new IllegalArgumentException("There must be at least one graph size per decade.");
        }

        if (parameters.graphsPerSize < 1) {
            throw new IllegalArgumentException("There must be at least one graph per size.");
        }

        if (parameters.runsPerGraph < 1) {
            throw new IllegalArgumentException("There must be at least one run per graph.");
        }
        
        // Number of edges
        
        if (parameters.density > 1) {
            throw new IllegalArgumentException("The density value must be between 0 and 1.");
        }
        
        if (parameters.minOutEdgesPerNode > parameters.maxOutEdgesPerNode
                || parameters.minOutEdgesPerNode < 0) {
            throw new IllegalArgumentException("Minimum number of outgoing edges per node must be"
                    + " non-negative and less or equal than maximum number of outgoing edges per node.");
        }
        
        // Probabilities
        if (parameters.invertedPortProb < 0.0f || parameters.northSouthPortProb < 0.0f
                || parameters.invertedPortProb + parameters.northSouthPortProb > 1.0f) {
            throw new IllegalArgumentException("Port side probabilities must be greater than or equal"
                    + " to 0.0 and must add up to at most 1.0.");
        }
    }
}
