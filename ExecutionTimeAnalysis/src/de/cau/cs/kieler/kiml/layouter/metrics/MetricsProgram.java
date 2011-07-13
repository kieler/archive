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

import de.cau.cs.kieler.klay.layered.LayeredLayoutProvider;
import de.cau.cs.kieler.klodd.hierarchical.HierarchicalDataflowLayoutProvider;

/**
 * Main class of the layouter metrics program.
 * 
 * @author msp
 */
public final class MetricsProgram {

    /** mask for getting a file name from a system time value. */
    private static final long TIME_MASK = 0xfff;
    
    /**
     * Hidden default constructor.
     */
    private MetricsProgram() {
    }
    
    /**
     * Main method of the metrics program.
     * 
     * @param args command line arguments
     */
    public static void main(final String[] args) {
        OutputStream fileStream = null;
        try {
            String fileName = "measurement" + (System.currentTimeMillis() & TIME_MASK) + ".csv";
            fileStream = new FileOutputStream(fileName);
            
            // measure the KLoDD Hierarchical layouter
            ExecutionTimeMetric executionTimeMetric = new ExecutionTimeMetric(
                    new HierarchicalDataflowLayoutProvider(), fileStream);
            executionTimeMetric.measure(2, 2, 1);
            
            // measure the KLay Layered layouter
            executionTimeMetric = new ExecutionTimeMetric(new LayeredLayoutProvider(), fileStream);
            executionTimeMetric.measure(2, 2, 1);
            
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

}
