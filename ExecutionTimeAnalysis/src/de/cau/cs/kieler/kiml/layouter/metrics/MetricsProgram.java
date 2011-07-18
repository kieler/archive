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

import de.cau.cs.kieler.core.kgraph.KNode;
import de.cau.cs.kieler.kiml.klayoutdata.KShapeLayout;
import de.cau.cs.kieler.klay.layered.LayeredLayoutProvider;
import de.cau.cs.kieler.klay.layered.p2layers.LayeringStrategy;
import de.cau.cs.kieler.klay.layered.properties.Properties;
import de.cau.cs.kieler.klodd.hierarchical.HierarchicalDataflowLayoutProvider;

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
    private MetricsProgram(final String[] args) {
        // Read command-line arguments
        Parameters parameters = null;
        try {
            parameters = new Parameters(args);
        } catch (IllegalArgumentException e) {
            printHelp(e.getMessage());
        }
        
        // Define a property setter that makes KLay Layered use the Longest Path Layerer
        ExecutionTimeMetric.PropertySetter layeredPropertySetter =
            new ExecutionTimeMetric.PropertySetter() {
            
            public void setProperties(final KNode graph) {
                graph.getData(KShapeLayout.class).setProperty(
                        Properties.NODE_LAYERING,
                        LayeringStrategy.LONGEST_PATH);
            }
        };
        
        OutputStream fileStream = null;
        try {
            // Generate a file name
            String fileName = "measurement" + (System.currentTimeMillis() & TIME_MASK) + ".csv";
            fileStream = new FileOutputStream(fileName);
            
            // Measure the KLoDD Hierarchical layouter
            ExecutionTimeMetric executionTimeMetric = new ExecutionTimeMetric(
                    new HierarchicalDataflowLayoutProvider(),
                    fileStream,
                    parameters,
                    null);
            executionTimeMetric.measure();
            
            // Measure the KLay Layered layouter
            executionTimeMetric = new ExecutionTimeMetric(
                    new LayeredLayoutProvider(),
                    fileStream,
                    parameters,
                    layeredPropertySetter);
            executionTimeMetric.measure();
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
        new MetricsProgram(args);
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////
    // Help
    
    /**
     * Prints a help text listing the available command line options.
     * 
     * @param msg message to be printed before the help text. May be {@code null}.
     */
    private void printHelp(final String msg) {
        if (msg != null && msg.length() > 0) {
            System.out.println(msg);
            System.out.println();
        }
        
        System.out.println("Metrics Command Line Help");
        System.out.println("-------------------------");
        System.out.println();
        System.out.println("This program accepts the following options:");
        System.out.println(" -sd <int>  start decade. (default: 1)");
        System.out.println(" -ed <int>  end decade. (default: 2)");
        System.out.println(" -md <int>  the number of different graph sizes per decade. (default: 5)");
        System.out.println(" -sg <int>  the number of graphs generated for each size. (default: 5)");
        System.out.println(" -gr <int>  the number of runs per graph. (default: 5)");
        System.out.println(" -em <int>  minimum number of edges to leave each node. (default: 1)");
        System.out.println(" -ex <int>  maximum number of edges to leave each node. (default: 2)");
        System.out.println(" -pr <float> <float> (default: both 0.0)");
        System.out.println("            the probability for ports to be placed on the different sides:");
        System.out.println("             1. inverted port side.");
        System.out.println("             2. northern or southern port side.");
        System.out.println("            The summed probability must not exceed 1.0.");
        System.out.println();
        System.out.println("Metrics is brought to you by:");
        System.out.println(" - msp and cds,");
        System.out.println(" - the real-time and embedded systems group, and");
        System.out.println(" - the larch.");
    }

}
