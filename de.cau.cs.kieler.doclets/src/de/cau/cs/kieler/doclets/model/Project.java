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

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a KIELER project.
 * 
 * @author cds
 */
public class Project extends AbstractThingWithStatistics {
    /**
     * Map of plug-in names to plug-ins.
     */
    private Map<String, Plugin> plugins = new HashMap<String, Plugin>();
    
    
    /**
     * Creates a new instance representing the project with the given name.
     * 
     * @param name the project's name.
     */
    public Project(final String name) {
        super(name);
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
        statsIgnored = 0;
        statsDesign = new int[DesignRating.values().length];
        statsCode = new int[CodeRating.values().length];
        
        for (Plugin plugin : plugins.values()) {
            plugin.aggregateStatistics();
            
            statsClasses += plugin.getStatsClasses();
            statsGenerated += plugin.getStatsGenerated();
            statsIgnored += plugin.getStatsIgnored();
            
            for (int i = 0; i < statsDesign.length; i++) {
                statsDesign[i] += plugin.getStatsDesign()[i];
            }
            
            for (int i = 0; i < statsCode.length; i++) {
                statsCode[i] += plugin.getStatsCode()[i];
            }
            
            statsLoc += plugin.getStatsLoc();
        }
    }
    
    
    /////////////////////////////////////////////////////////////////////////////
    // GETTERS

    /**
     * Returns the map mapping plug-in names to plug-ins of this project.
     * 
     * @return the project's map of plug-ins.
     */
    public Map<String, Plugin> getPlugins() {
        return plugins;
    }
    
    /**
     * Retrieves the plugin with the given name. Creates one if there is none yet.
     * 
     * @param pluginName name of the plugin.
     * @return the plugin.
     */
    public Plugin retrievePlugin(final String pluginName) {
        Plugin plugin = plugins.get(pluginName);
        
        if (plugin == null) {
            plugin = new Plugin(pluginName, this);
            plugins.put(pluginName, plugin);
        }
        
        return plugin;
    }
}
