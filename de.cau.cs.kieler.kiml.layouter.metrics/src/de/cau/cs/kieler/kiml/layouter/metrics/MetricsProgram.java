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

import de.cau.cs.kieler.klodd.hierarchical.HierarchicalDataflowLayoutProvider;

/**
 * Main class of the layouter metrics program.
 * 
 * @author msp
 */
public class MetricsProgram {

    /**
     * Main method of the metrics program.
     * 
     * @param args
     */
    public static void main(String[] args) {
        // TODO generalize metrics selection, e.g. by reading command-line arguments
        OutputStream fileStream = null;
        try {
            String fileName = "measurement" + (System.currentTimeMillis() & 0xfff)+".csv";
            fileStream = new FileOutputStream(fileName);
            ExecutionTimeMetric executionTimeMetric = new ExecutionTimeMetric(
                    new HierarchicalDataflowLayoutProvider(), fileStream);
            executionTimeMetric.measure(2, 2, 1);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        finally {
            try {
                if (fileStream != null)
                    fileStream.close();
            } catch (IOException exception) {}
        }
    }

}
