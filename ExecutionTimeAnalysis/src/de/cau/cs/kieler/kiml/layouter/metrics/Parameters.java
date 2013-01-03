/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 *
 * http://www.informatik.uni-kiel.de/rtsys/kieler/
 * 
 * Copyright 2011 by
 * + Christian-Albrechts-University of Kiel
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 * 
 * This code is provided under the terms of the Eclipse Public License (EPL).
 * See the file epl-v10.html for the license text.
 */
package de.cau.cs.kieler.kiml.layouter.metrics;

import java.util.NoSuchElementException;

/**
 * A class that holds parameters for the program. Can parse the command line to
 * initialize itself.
 * 
 * @author cds
 */
public class Parameters {
    
    // This is a parameter holding class for a simple utility program; turn off the
    // Checkstyle checks.
    // CHECKSTYLEOFF VisibilityModifier
    // CHECKSTYLEOFF MagicNumber
    
    /** Enumeration of modes for the metrics program. */
    public enum Mode {
        /** Measure the execution time. */
        TIME,
        /** Measure the number of edge crossings. */
        CROSSINGS;
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////
    // Class ArrayIterator
    
    /**
     * Iterates over a string array, returning the next element in different data types.
     * 
     * @author cds
     */
    private static class ArrayIterator {
        /**
         * The array to iterate over.
         */
        private String[] array = null;
        
        /**
         * Pointer to the next element to be returned.
         */
        private int nextPointer = 0;
        
        
        /**
         * Creates a new instance that iterates over the given array.
         * 
         * @param array the array to iterate over.
         */
        public ArrayIterator(final String[] array) {
            this.array = array;
        }
        
        
        /**
         * Checks if the iterator has another element to return.
         * 
         * @return {@code true} if there's another element to return, {@code false}
         *         otherwise.
         */
        public boolean hasNext() {
            return nextPointer < array.length;
        }
        
        /**
         * Returns the next element, if any.
         * 
         * @return the next element.
         * @throws NoSuchElementException if there is no next element.
         */
        public String nextString() {
            checkNext();
            return array[nextPointer++];
        }
        
        /**
         * Returns the next element, if any, converted to an int, if possible.
         * 
         * @return the next element as an integer.
         * @throws NumberFormatException if the element cannot be interpreted as an integer.
         * @throws NoSuchElementException if there is no next element.
         */
        public int nextInt() {
            checkNext();
            return Integer.valueOf(array[nextPointer++]);
        }
        
        /**
         * Returns the next element, if any, converted to a float, if possible.
         * 
         * @return the next element as a float.
         * @throws NumberFormatException if the element cannot be interpreted as a float.
         * @throws NoSuchElementException if there is no next element.
         */
        public float nextFloat() {
            checkNext();
            return Float.valueOf(array[nextPointer++]);
        }
        
        /**
         * Throws an exception if the end of the array has been reached.
         */
        private void checkNext() {
            if (!hasNext()) {
                throw new NoSuchElementException("end of array reached.");
            }
        }
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////
    // Variables
    
    /**
     * The metrics program mode.
     */
    public Mode mode = Mode.TIME;
    
    /**
     * Whether the generated graphs shall be exported.
     */
    public boolean exportGraphs = false;
    
    /**
     * Whether ports shall be created.
     */
    public boolean withPorts = true;
    
    /**
     * Whether self-loops shall be allowed.
     */
    public boolean allowSelfLoops = false;
    
    /**
     * Whether cycles shall be allowed.
     */
    public boolean allowCycles = false;
    
    /**
     * Whether a linear scale shall be used.
     */
    public boolean linearScale = false;
    
    /**
     * Whether a help text should be printed.
     */
    public boolean help = false;
    
    /**
     * The start decade.
     */
    public int startDecade = 1;
    
    /**
     * The end decade.
     */
    public int endDecade = 2;
    
    /**
     * Number of different graph sizes per decade.
     */
    public int graphSizesPerDecade = 5;
    
    /**
     * Number of graphs generated for each graph size.
     */
    public int graphsPerSize = 5;
    
    /**
     * Number of measurements per graph.
     */
    public int runsPerGraph = 5;
    
    /**
     * Minimum number of outgoing edges allowed for a node.
     */
    public int minOutEdgesPerNode = 1;
    
    /**
     * Maximum number of outgoing edges allowed for a node.
     */
    public int maxOutEdgesPerNode = 2;
    
    /**
     * Optional density value (overrides the minimum and maximum number of edges).
     */
    public float density = -1;
    
    /**
     * The probability for a port to become an inverted port.
     */
    public float invertedPortProb = 0.05f;
    
    /**
     * The probability for a port to be placed on the northern or southern side, with both
     * sides sharing an equal probability.
     */
    public float northSouthPortProb = 0.1f;
    
    
    ///////////////////////////////////////////////////////////////////////////////
    // Constructors
    
    /**
     * Default constructor.
     */
    public Parameters() {
        
    }
    
    /**
     * Constructor that uses the given command line to initialize itself, if possible.
     * 
     * @param args the command line.
     * @throws IllegalArgumentException if an error occurs parsing the command line.
     */
    public Parameters(final String[] args) {
        ArrayIterator iterator = new ArrayIterator(args);
        
        while (iterator.hasNext()) {
            String arg = iterator.nextString();
            
            // See which argument this is
            if (arg.equals("--time")) {
                mode = Mode.TIME;
            } else if (arg.equals("--cross")) {
                mode = Mode.CROSSINGS;
            } else if (arg.equals("-h")) {
                help = true;
            } else if (arg.equals("-x")) {
                exportGraphs = true;
            } else if (arg.equals("-ln")) {
                linearScale = true;
            } else if (arg.equals("-sd")) {
                try {
                    startDecade = iterator.nextInt();
                } catch (Exception e) {
                    throw new IllegalArgumentException(
                            "Option -sd must be followed by an integer value.");
                }
            } else if (arg.equals("-ed")) {
                try {
                    endDecade = iterator.nextInt();
                } catch (Exception e) {
                    throw new IllegalArgumentException(
                            "Option -ed must be followed by an integer value.");
                }
            } else if (arg.equals("-md")) {
                try {
                    graphSizesPerDecade = iterator.nextInt();
                } catch (Exception e) {
                    throw new IllegalArgumentException(
                            "Option -md must be followed by an integer value.");
                }
            } else if (arg.equals("-sg")) {
                try {
                    graphsPerSize = iterator.nextInt();
                } catch (Exception e) {
                    throw new IllegalArgumentException(
                            "Option -sg must be followed by an integer value.");
                }
            } else if (arg.equals("-gr")) {
                try {
                    runsPerGraph = iterator.nextInt();
                } catch (Exception e) {
                    throw new IllegalArgumentException(
                            "Option -gr must be followed by an integer value.");
                }
            } else if (arg.equals("-em")) {
                try {
                    minOutEdgesPerNode = iterator.nextInt();
                } catch (Exception e) {
                    throw new IllegalArgumentException(
                            "Option -em must be followed by an integer value.");
                }
            } else if (arg.equals("-ex")) {
                try {
                    maxOutEdgesPerNode = iterator.nextInt();
                } catch (Exception e) {
                    throw new IllegalArgumentException(
                            "Option -ex must be followed by an integer value.");
                }
            } else if (arg.equals("-ds")) {
                try {
                    density = iterator.nextFloat();
                } catch (Exception e) {
                    throw new IllegalArgumentException(
                            "Option -ds must be followed by a floating point value.");
                }
            } else if (arg.equals("-pr")) {
                try {
                    invertedPortProb = iterator.nextFloat();
                    northSouthPortProb = iterator.nextFloat();
                } catch (Exception e) {
                    throw new IllegalArgumentException(
                            "Option -pr must be followed by two float values.");
                }
            }
        }
    }
}
