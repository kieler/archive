/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 *
 * http://www.informatik.uni-kiel.de/rtsys/kieler/
 * 
 * Copyright 2013 by
 * + Kiel University
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

import de.cau.cs.kieler.core.properties.IPropertyHolder;

/**
 * An abstract metric to be implemented by subclasses.
 *
 * @author msp
 */
public abstract class AbstractMetric {
    
    // CHECKSTYLEOFF VisibilityModifier
    // CHECKSTYLEOFF MagicNumber
    
    /**
     * The parameters.
     */
    protected Parameters parameters;
    
    /**
     * The output stream writer.
     */
    protected OutputStreamWriter outputWriter;
    
    /**
     * Generator used to generate random graphs.
     */
    protected GraphGenerator graphGenerator;
    
    /**
     * An optional class that can set additional properties on generated random graphs.
     */
    protected IPropertyHolder propertyHolder;
    
    /**
     * Creates a metric instance.
     * 
     * @param outputStream the output stream to which measurements are written
     * @param parameters user-supplied parameters controlling the graph generation and
     *                   measurement process.
     * @param propertyHolder an optional class that sets properties on generated random
     *                       graphs. Can be used to set layout properties that would
     *                       otherwise be left to default values.
     * @throws IllegalArgumentException if the parameters are not valid.
     */
    public AbstractMetric(final OutputStream outputStream, final Parameters parameters,
            final IPropertyHolder propertyHolder) {
        this.outputWriter = new OutputStreamWriter(outputStream);
        this.parameters = parameters;
        this.propertyHolder = propertyHolder;
        this.graphGenerator = new GraphGenerator(1);  // the random seed could be parameterized
        
        validateParameters();
    }
    
    /**
     * Performs a measurement parameterized according to the parameters given when this class was
     * instantiated.
     * 
     * @throws IOException if writing to the output stream fails
     */
    public void measure() throws IOException {
        // Add a shutdown hook that properly closes the output file so we will see its content
        Thread shutdownHook = new Thread() {
            public void run() {
                try {
                    outputWriter.close();
                } catch (IOException e) {
                    // ignore the exception
                }
            }
        };
        Runtime.getRuntime().addShutdownHook(shutdownHook);
        
        // Warmup. Warmup! ROAAAAAAAR!!!
        warmup();
        
        try {
            outputWriter.write(parameters.toString() + "\n");
            outputWriter.write(getTableHeaders() + "\n");
            if (parameters.linearScale) {
                int start = tenPower(parameters.startDecade);
                int end = tenPower(parameters.endDecade);
                int graphSizes = parameters.graphSizesPerDecade
                        * (parameters.endDecade - parameters.startDecade);
                double incr = (double) (end - start) / graphSizes;
                double currentSize = start;
                
                while (currentSize <= end) {
                    doGraphSizeMeasurement((int) Math.round(currentSize));
                    currentSize += incr;
                }
                
            } else {
                int currentDecade = parameters.startDecade;
                double currentSize = tenPower(parameters.startDecade);
                double incFactor = Math.pow(10, 1.0 / parameters.graphSizesPerDecade);
                
                while (currentDecade < parameters.endDecade) {
                    for (int i = 0; i < parameters.graphSizesPerDecade; i++) {
                        doGraphSizeMeasurement((int) Math.round(currentSize));
                        currentSize *= incFactor;
                    }
                    currentDecade++;
                }
                
                doGraphSizeMeasurement((int) Math.round(currentSize));
            }
        } finally {
            try {
                outputWriter.flush();
            } catch (IOException e) {
                // ignore the exception
            }
            // The output file will be closed in the main program, so we don't need the shutdown hook
            Runtime.getRuntime().removeShutdownHook(shutdownHook);
        }
    }
    
    /**
     * Returns ten by the power of {@code decade}.
     * 
     * @param decade the decade
     * @return ten by the power of {@code decade}
     */
    private static int tenPower(final int decade) {
        int p = 1;
        for (int d = 0; d < decade; d++) {
            p *= 10;
        }
        return p;
    }
    
    /**
     * Returns used table headers for csv export.
     * 
     * @return table headers as comma separated values.
     */
    protected String getTableHeaders() {
        return "";
    }
    
    /**
     * Warms up the layout provider and system cache by performing some dummy layouts.
     * 
     * @throws IOException if writing to the output stream fails
     */
    protected void warmup() throws IOException {
        // the default implementation does nothing
    }
    
    /**
     * Performs a measurement for the given number of nodes.
     * 
     * @param nodeCount number of nodes for generated graphs
     * @throws IOException if writing to the output stream fails
     */
    protected abstract void doGraphSizeMeasurement(final int nodeCount) throws IOException;
    
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
