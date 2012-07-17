/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 *
 * http://www.informatik.uni-kiel.de/rtsys/kieler/
 * 
 * Copyright 2012 by
 * + Christian-Albrechts-University of Kiel
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 * 
 * This code is provided under the terms of the Eclipse Public License (EPL).
 * See the file epl-v10.html for the license text.
 */
package de.cau.cs.kieler.doclets.model;

/**
 * Everything that can calculate class statistics inherits from this class.
 * 
 * @author cds
 */
public abstract class AbstractThingWithStatistics implements Comparable<AbstractThingWithStatistics> {
    // CHECKSTYLEOFF VisibilityModifier
    /**
     * The thing's name.
     */
    private String name = "";
    /**
     * The number of classes in this package.
     */
    protected int statsClasses = 0;
    /**
     * The number of generated classes in this package.
     */
    protected int statsGenerated = 0;
    /**
     * Design rating statistics.
     */
    protected int[] statsDesign = new int[DesignRating.values().length];
    /**
     * Code rating statistics.
     */
    protected int[] statsCode = new int[CodeRating.values().length];
    // CHECKSTYLEON VisibilityModifier
    
    
    /**
     * Creates a new instance with the given name.
     * 
     * @param name the thing's name.
     */
    public AbstractThingWithStatistics(final String name) {
        this.name = name;
    }
    
    
    /**
     * Calculates the sum of contained classes, packages, generated classes and rating statistics. This
     * should be run just once as soon as all classes have been processed.
     */
    public abstract void aggregateStatistics();
    

    /**
     * Returns this thing's fully qualified name.
     * 
     * @return the thing's fully qualified name.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Returns the number of classes in this plug-in, including generated classes.
     * 
     * @return the total number of classes.
     */
    public int getStatsClasses() {
        return statsClasses;
    }

    /**
     * Returns the number of classes that were generated.
     * 
     * @return the number of generated classes.
     */
    public int getStatsGenerated() {
        return statsGenerated;
    }

    /**
     * Returns the number of occurrences for each of the design rating levels.
     * 
     * @return array containing the number of occurrences, indexed by design rating ordinal.
     */
    public int[] getStatsDesign() {
        return statsDesign;
    }

    /**
     * Returns the number of occurrences for each of the code rating levels.
     * 
     * @return array containing the number of occurrences, indexed by code rating ordinal.
     */
    public int[] getStatsCode() {
        return statsCode;
    }
    
    /**
     * {@inheritDoc}
     */
    public int compareTo(final AbstractThingWithStatistics o) {
        return name.compareTo(o.name);
    }

}
