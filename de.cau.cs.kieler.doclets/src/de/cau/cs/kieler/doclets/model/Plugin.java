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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sun.javadoc.PackageDoc;

/**
 * Represents a plug-in's packages and classes. Can gather statistics about itself, which should only
 * be calculated once all containing classes have been added.
 * 
 * @author cds
 */
public class Plugin extends AbstractThingWithStatistics {
    /**
     * The plugin's parent project.
     */
    private Project parent;
    
    /**
     * Map mapping packages to containing class items.
     */
    private Map<PackageDoc, List<ClassItem>> packageToClassMap
        = new HashMap<PackageDoc, List<ClassItem>>();
    
    /**
     * Creates a new instance representing the plug-in with the given name.
     * 
     * @param name the plug-in's name.
     * @param parent the plug-in's parent project.
     */
    public Plugin(final String name, final Project parent) {
        super(name);
        this.parent = parent;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void aggregateStatistics() {
        // Reset statistics
        statsClasses = 0;
        statsGenerated = 0;
        statsDesign = new int[DesignRating.values().length];
        statsCode = new int[CodeRating.values().length];
        
        Set<PackageDoc> packages = packageToClassMap.keySet();
        for (PackageDoc packageDoc : packages) {
            for (ClassItem classItem : packageToClassMap.get(packageDoc)) {
                statsClasses++;
                if (classItem.isGenerated()) {
                    statsGenerated++;
                }
                
                // Design Rating
                DesignRating designRating = classItem.getDesignRating();
                if (designRating != null) {
                    statsDesign[designRating.ordinal()]++;
                }
                
                // Code Rating
                CodeRating codeRating = classItem.getCodeRating();
                if (codeRating != null) {
                    statsCode[codeRating.ordinal()]++;
                }
            }
        }
    }

    /**
     * Adds the given class to this plug-in.
     * 
     * @param classItem The class item to add.
     */
    public void addClass(final ClassItem classItem) {
        List<ClassItem> classItemList = packageToClassMap.get(
                classItem.getClassDoc().containingPackage());
        
        if (classItemList == null) {
            classItemList = new ArrayList<ClassItem>();
            packageToClassMap.put(classItem.getClassDoc().containingPackage(), classItemList);
        }
        
        classItemList.add(classItem);
    }

    
    /////////////////////////////////////////////////////////////////////////////
    // GETTERS
    
    /**
     * Returns the project this plug-in is part of.
     * 
     * @return the parent project.
     */
    public Project getProject() {
        return parent;
    }

    /**
     * Returns the map of packages and theirs classes.
     * 
     * @return map of packages and classes.
     */
    public Map<PackageDoc, List<ClassItem>> getPackageToClassMap() {
        return packageToClassMap;
    }
}
