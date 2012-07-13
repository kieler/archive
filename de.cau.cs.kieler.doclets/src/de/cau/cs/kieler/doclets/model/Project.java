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

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a KIELER project.
 * 
 * @author cds
 */
public class Project extends AbstractThingWithStatistics {
    /**
     * The project's name.
     */
    private String name;
    
    /**
     * List of plug-ins in this project.
     */
    private List<Plugin> plugins = new ArrayList<Plugin>();
    
    
    /**
     * Creates a new instance representing the project with the given name.
     * 
     * @param name the project's name.
     */
    public Project(final String name) {
        this.name = name;
    }
    
    
    /**
     * Adds the given plug-in to this project.
     * 
     * @param plugin the plug-in to add.
     */
    public void addPlugin(final Plugin plugin) {
        plugins.add(plugin);
    }

    /**
     * {@inheritDoc}
     * 
     * <p>This particular implementation tells all of its plug-ins to aggregate their statistics as
     * well.</p>
     */
    @Override
    public void aggregateStatistics() {
        // Reset statistics
        statsClasses = 0;
        statsGenerated = 0;
        statsDesign = new int[DesignRating.values().length];
        statsCode = new int[CodeRating.values().length];
        
        for (Plugin plugin : plugins) {
            plugin.aggregateStatistics();
            
            statsClasses += plugin.getStatsClasses();
            statsGenerated += plugin.getStatsGenerated();
            
            for (int i = 0; i < statsDesign.length; i++) {
                statsDesign[i] += plugin.getStatsDesign()[i];
            }
            
            for (int i = 0; i < statsCode.length; i++) {
                statsCode[i] += plugin.getStatsCode()[i];
            }
        }
    }


    /**
     * Returns the project's name.
     * 
     * @return the project's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the list of plug-ins in this project.
     * 
     * @return the project's list of plug-ins.
     */
    public List<Plugin> getPlugins() {
        return plugins;
    }
}
