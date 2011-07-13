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
import de.cau.cs.kieler.kiml.AbstractLayoutProvider;

/**
 * Metric class for measurement of execution times.
 * 
 * @author msp
 */
public class ExecutionTimeMetric {
   
    // CHECKSTYLEOFF MagicNumber
    
    /** the layout provider. */
    private AbstractLayoutProvider layoutProvider;
    /** the output stream writer. */
    private OutputStreamWriter outputWriter;
    /** number of execution time measurements per decade. */
    private int measurementsPerDecade = 5;
    /** number of layout runs for each graph. */
    private int runsPerGraph = 5;
    /** number of generated graphs for each size value. */
    private int graphsPerSize = 5;
    
    /**
     * Creates an execution time metric instance.
     * 
     * @param thelayoutProvider the layout provider to examine
     * @param outputStream the output stream to which measurements are written
     */
    public ExecutionTimeMetric(final AbstractLayoutProvider thelayoutProvider,
            final OutputStream outputStream) {
        this.layoutProvider = thelayoutProvider;
        this.outputWriter = new OutputStreamWriter(outputStream);
    }
    
    /**
     * Performs an execution time measurement for the given range of graph sizes.
     * 
     * @param startDecade starting decade
     * @param endDecade ending decade
     * @param maxEdges maximal number of edges per node count
     * @throws IOException if writing to the output stream fails
     */
    public void measure(final int startDecade, final int endDecade, final int maxEdges)
            throws IOException {
        if (startDecade > endDecade || startDecade < 0) {
            throw new IllegalArgumentException("Start decade must be non-negative"
                    + " and less or equal than end decade.");
        }
        warmup();
        
        try {
            int currentDecade = startDecade;
            double currentSize = 1;
            for (int d = 0; d < startDecade; d++) {
                currentSize *= 10;
            }
            double incFactor = Math.pow(10, 1.0 / measurementsPerDecade);
            while (currentDecade < endDecade) {
                for (int i = 0; i < measurementsPerDecade; i++) {
                    measure((int) Math.round(currentSize), maxEdges);
                    currentSize *= incFactor;
                }
                currentDecade++;
            }
            measure((int) Math.round(currentSize), maxEdges);
        } finally {
            outputWriter.flush();
        }
    }

    /**
     * Warms up the layout provider and system cache by performing some dummy layouts.
     * 
     * @throws KielerException if the layout provider fails
     */
    private void warmup() {
        KNode layoutGraph = GraphGenerator.generateGraph(10000, 2, true);
        IKielerProgressMonitor progressMonitor = new BasicProgressMonitor();
        for (int i = 0; i < 3; i++) {
            layoutProvider.doLayout(layoutGraph, progressMonitor);
        }
    }
    
    /**
     * Performs an execution time measurement for the given number of nodes.
     * 
     * @param nodeCount number of nodes for generated graphs
     * @param maxEdges maximal number of edges for generated graphs
     * @throws IOException if writing to the output stream fails
     * @throws KielerException if the layout provider fails
     */
    private void measure(final int nodeCount, final int maxEdges) throws IOException {
        outputWriter.write(Integer.toString(nodeCount));
        for (int edgeCount = 1; edgeCount <= maxEdges; edgeCount++) {
            System.out.print("n = " + nodeCount + ", m = " + edgeCount + "n: ");
            double totalTime = 0.0;
            for (int i = 0; i < graphsPerSize; i++) {
                System.out.print(i);
                KNode layoutGraph = GraphGenerator.generateGraph(nodeCount, edgeCount, true);
                double minTime = Double.MAX_VALUE;
                for (int j = 0; j < runsPerGraph; j++) {
                    System.gc();
                    IKielerProgressMonitor progressMonitor = new BasicProgressMonitor();
                    layoutProvider.doLayout(layoutGraph, progressMonitor);
                    minTime = Math.min(minTime, progressMonitor.getExecutionTime());
                    System.out.print(".");
                }
                totalTime += minTime;
            }
            double meanTime = totalTime / graphsPerSize;
            outputWriter.write("," + meanTime);
            System.out.println(" -> " + meanTime);
        }
        outputWriter.write("\n");
    }
    
    /**
     * Sets the number of layout runs for each generated graph.
     * 
     * @param therunsPerGraph the number of runs per graph to set
     */
    public void setRunsPerGraph(final int therunsPerGraph) {
        if (therunsPerGraph < 1) {
            throw new IllegalArgumentException("Number of runs per graph must be positive.");
        }
        this.runsPerGraph = therunsPerGraph;
    }

    /**
     * Sets the number of randomly generated graphs for each size value.
     * 
     * @param thegraphsPerSize the number of graphs per size to set
     */
    public void setGraphsPerSize(final int thegraphsPerSize) {
        if (runsPerGraph < 1) {
            throw new IllegalArgumentException("Number of graphs per size must be positive.");
        }
        this.graphsPerSize = thegraphsPerSize;
    }
    
    /**
     * Sets the number of execution time measurements for each decade.
     * 
     * @param themeasurementsPerDecade the number of measurements per decade
     */
    public void setMeasurementsPerDecade(final int themeasurementsPerDecade) {
        if (themeasurementsPerDecade < 1) {
            throw new IllegalArgumentException("Number of measurements per decade must be positive.");
        }
        this.measurementsPerDecade = themeasurementsPerDecade;
    }
        
}
