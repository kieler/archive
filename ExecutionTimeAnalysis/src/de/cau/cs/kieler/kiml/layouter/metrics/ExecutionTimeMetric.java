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

import de.cau.cs.kieler.core.alg.IFactory;
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
    
    /** This metric uses exactly one layout provider instance for measuring execution time. */
    private AbstractLayoutProvider layoutProvider;
    /** The algorithm phases that shall be output additionally to the total execution time. */
    private String[] phases;
    
    ///////////////////////////////////////////////////////////////////////////////
    // Constructor
    
    /**
     * Creates an execution time metric instance.
     * 
     * @param layoutFactory the layout algorithm factory
     * @param outputStream the output stream to which measurements are written
     * @param parameters user-supplied parameters controlling the graph generation and
     *                   measurement process.
     * @param propertyHolder an optional class that sets properties on generated random
     *                       graphs. Can be used to set layout properties that would
     *                       otherwise be left to default values.
     * @param phases the phases that shall be output additionally to the total execution time
     * @throws IllegalArgumentException if the parameters are not valid.
     */
    public ExecutionTimeMetric(final IFactory<AbstractLayoutProvider> layoutFactory,
            final OutputStream outputStream, final Parameters parameters,
            final IPropertyHolder propertyHolder, final String[] phases) {
        super(outputStream, parameters, propertyHolder);
        this.layoutProvider = layoutFactory.create();
        this.phases = phases;
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////
    // Measurement

    /**
     * {@inheritDoc}
     */
    @Override
    protected void warmup() throws IOException {
        System.out.println("Warmup...");
        
        // Create a set of warmup parameters
        Parameters warmupParameters = new Parameters();
        warmupParameters.allowCycles = true;
        warmupParameters.relativeEdgeCount = 2.0f;
        
        // Generate a graph
        KNode layoutGraph = graphGenerator.generateGraph(1000, warmupParameters);
        if (propertyHolder != null) {
            layoutGraph.getData(KShapeLayout.class).copyProperties(propertyHolder);
        }
        IKielerProgressMonitor progressMonitor = new BasicProgressMonitor();
        
        // Do the warmup layout
        layoutProvider.doLayout(layoutGraph, progressMonitor);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGraphSizeMeasurement(final int nodeCount) throws IOException {
        outputWriter.write(Integer.toString(nodeCount));
        System.out.print("n = " + nodeCount + ": ");
        if (parameters.density > 0) {
            int edgeCount = Math.round(parameters.density * 0.5f * nodeCount * (nodeCount - 1));
            outputWriter.write(", " + Integer.toString(edgeCount));
        } else if (parameters.relativeEdgeCount > 0) {
            int edgeCount = Math.round(parameters.relativeEdgeCount * nodeCount);
            outputWriter.write(", " + Integer.toString(edgeCount));
        } else {
            int edgeCount = Math.round((parameters.maxOutEdgesPerNode + parameters.minOutEdgesPerNode)
                    * 0.5f * nodeCount);
            outputWriter.write(", " + Integer.toString(edgeCount));
        }
        
        double totalTime = 0.0;
        double[] phaseTimes = new double[phases.length];
        for (int i = 0; i < parameters.graphsPerSize; i++) {
            
            // Generate a graph with the given node and edge count
            KNode layoutGraph = graphGenerator.generateGraph(nodeCount, parameters);
            if (propertyHolder != null) {
                layoutGraph.getData(KShapeLayout.class).copyProperties(propertyHolder);
            }
            
            // Check the generated graph
            if (layoutGraph.getChildren().size() != nodeCount) {
                throw new IllegalStateException("The graph has " + layoutGraph.getChildren().size()
                        + " nodes, but I expected " + nodeCount);
            }
            System.out.print("m=" + graphGenerator.countEdges(layoutGraph));
            
            // Do a bunch of layout runs and take the one that took the least amount of time
            double minTime = Double.MAX_VALUE;
            double[] minPhaseTimes = new double[phases.length];
            for (int j = 0; j < parameters.runsPerGraph; j++) {
                System.gc();
                
                IKielerProgressMonitor progressMonitor = new BasicProgressMonitor();
                layoutProvider.doLayout(layoutGraph, progressMonitor);
                double time = progressMonitor.getExecutionTime();
                
                if (time < minTime) {
                    minTime = time;
                    for (int k = 0; k < phases.length; k++) {
                        minPhaseTimes[k] = getPhaseTime(progressMonitor, phases[k]);
                    }
                }
                
                System.out.print(".");
            }
            
            // Export the graph using the last computed layout, if requested
            if (parameters.exportGraphs) {
                graphGenerator.exportGraph(layoutGraph, i + 1);
            }
            
            totalTime += minTime;
            for (int k = 0; k < phases.length; k++) {
                if (minPhaseTimes[k] >= 0) {
                    phaseTimes[k] += minPhaseTimes[k];
                }
            }
        }
        
        // Calculate the average time taken for the graphs
        double avgTime = totalTime / parameters.graphsPerSize;
        outputWriter.write(", " + avgTime);
        System.out.print(" -> " + avgTime);
        if (phases.length > 0) {
            System.out.print(" (");
            for (int k = 0; k < phases.length; k++) {
                double avgPhaseTime = phaseTimes[k] / parameters.graphsPerSize;
                outputWriter.write(", " + avgPhaseTime);
                if (k > 0) {
                    System.out.print(", ");
                }
                System.out.print(avgPhaseTime);
            }
            System.out.print(")");
        }
        
        outputWriter.write("\n");
        System.out.println();
    }
    
    private double getPhaseTime(final IKielerProgressMonitor monitor, final String phase) {
        for (IKielerProgressMonitor task : monitor.getSubMonitors()) {
            if (phase.equalsIgnoreCase(task.getTaskName())) {
                return task.getExecutionTime();
            }
        }
        return -1;
    }
    
}
