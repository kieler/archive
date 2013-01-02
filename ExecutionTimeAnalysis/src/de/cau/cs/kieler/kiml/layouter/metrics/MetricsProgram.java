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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import de.cau.cs.kieler.core.properties.IPropertyHolder;
import de.cau.cs.kieler.core.properties.MapPropertyHolder;
import de.cau.cs.kieler.kiml.AbstractLayoutProvider;
import de.cau.cs.kieler.kiml.options.EdgeRouting;
import de.cau.cs.kieler.kiml.options.LayoutOptions;
import de.cau.cs.kieler.klay.layered.LayeredLayoutProvider;
import de.cau.cs.kieler.klay.layered.p2layers.LayeringStrategy;
import de.cau.cs.kieler.klay.layered.properties.Properties;

/**
 * Main class of the layouter metrics program. This program measures the performance
 * of KLoDD and KLay Layered on randomly created graphs.
 * 
 * @author msp
 * @author cds
 */
public final class MetricsProgram {
    
    ///////////////////////////////////////////////////////////////////////////////
    // Constants

    /**
     * Mask for getting a file name from a system time value.
     */
    private static final long TIME_MASK = 0xfff;
    
    
    ///////////////////////////////////////////////////////////////////////////////
    // Constructors and main(...) method
    
    /**
     * Hidden default constructor.
     */
    private MetricsProgram(final Parameters parameters) {
        // Create a layout provider instance
        AbstractLayoutProvider layoutProvider = new LayeredLayoutProvider();
        
        // Define a property setter for layout configuration
        IPropertyHolder propertyHolder = new MapPropertyHolder();
        propertyHolder.setProperty(LayoutOptions.RANDOM_SEED, 0);
        propertyHolder.setProperty(LayoutOptions.EDGE_ROUTING, EdgeRouting.ORTHOGONAL);
//        propertyHolder.setProperty(Properties.THOROUGHNESS, 1);
        propertyHolder.setProperty(Properties.NODE_LAYERING, LayeringStrategy.LONGEST_PATH);
        
        OutputStream fileStream = null;
        try {
            // Generate a file name
            String fileName = "measurement" + (System.currentTimeMillis() & TIME_MASK) + ".csv";
            fileStream = new FileOutputStream(fileName);
            
            // Perform measurement
            switch (parameters.mode) {
            case TIME:
                ExecutionTimeMetric executionTimeMetric = new ExecutionTimeMetric(
                        layoutProvider,
                        fileStream,
                        parameters,
                        propertyHolder);
                executionTimeMetric.measure();
                break;
            case CROSSINGS:
                EdgeCrossingsMetric edgeCrossMetric = new EdgeCrossingsMetric(
                        layoutProvider,
                        fileStream,
                        parameters,
                        propertyHolder);
                edgeCrossMetric.measure();
                break;
            }
            
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            try {
                if (fileStream != null) {
                    fileStream.close();
                }
            } catch (IOException exception) {
                // ignore exception
            }
        }
    }
    
    /**
     * Main method of the metrics program.
     * 
     * @param args command line arguments.
     */
    public static void main(final String[] args) {
        // Read command-line arguments
        Parameters parameters = null;
        try {
            parameters = new Parameters(args);
        } catch (IllegalArgumentException e) {
            printHelp(e.getMessage());
            System.exit(1);
        }
        
        // Check if only the help text is to be printed
        if (parameters.help) {
            printHelp(null);
        } else {
            new MetricsProgram(parameters);
        }
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////
    // Help
    
    /**
     * Prints a help text listing the available command line options.
     * 
     * @param msg message to be printed before the help text. May be {@code null}.
     */
    private static void printHelp(final String msg) {
        if (msg != null && msg.length() > 0) {
            System.out.println(msg);
            System.out.println();
        }
        
        System.out.println("Metrics Command Line Help");
        System.out.println("-------------------------");
        System.out.println();
        System.out.println("This program accepts the following options:");
        System.out.println(" --time     measure execution time (this is the default mode).");
        System.out.println(" --cross    measure the number of edge crossings.");
        System.out.println(" -sd <int>  start decade. (default: 1)");
        System.out.println(" -ed <int>  end decade. (default: 2)");
        System.out.println(" -md <int>  the number of different graph sizes per decade. (default: 5)");
        System.out.println(" -sg <int>  the number of graphs generated for each size. (default: 5)");
        System.out.println(" -gr <int>  the number of runs per graph. (default: 5)");
        System.out.println(" -em <int>  minimum number of edges to leave each node. (default: 1)");
        System.out.println(" -ex <int>  maximum number of edges to leave each node. (default: 2)");
        System.out.println(" -ds <float>");
        System.out.println("            density value for the number of edges; if set, this value");
        System.out.println("            overrides the -em and -ex settings.");
        System.out.println(" -pr <float> <float> (default: 0.05 0.1)");
        System.out.println("            the probability for ports to be placed on the different sides:");
        System.out.println("             1. inverted port side.");
        System.out.println("             2. northern or southern port side.");
        System.out.println("            The summed probability must not exceed 1.0.");
        System.out.println(" -x         export all generated graphs.");
        System.out.println(" -h         print this help text and end the program.");
        System.out.println();
        System.out.println("Metrics is brought to you by:");
        System.out.println(" - msp and cds,");
        System.out.println(" - the real-time and embedded systems group, and");
        System.out.println(" - the larch.");
    }

}
