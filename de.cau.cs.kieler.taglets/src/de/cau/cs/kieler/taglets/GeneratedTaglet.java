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
package de.cau.cs.kieler.taglets;

import java.util.Map;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;

/**
 * Taglet for generated code.
 * 
 * @author msp
 */
public class GeneratedTaglet implements Taglet {

    /** the name of this taglet. */
    private static final String NAME = "generated";
    /** indicator for elements that are NOT generated. */
    private static final String NEGATION = "NOT";
    /** printed header for generated elements. */
    private static final String POS_HEADER = "Generated:";
    /** printed text for generated elements. */
    private static final String POS_TEXT = "This code was automatically generated.";
    /** printed header for elements that are NOT generated. */
    private static final String NEG_HEADER = "Not generated:";
    /** printed text for elements that are NOT generated. */
    private static final String NEG_TEXT = "This code was hand-written.";
    
    /**
     * {@inheritDoc}
     */
    public String getName() {
        return NAME;
    }

    /**
     * {@inheritDoc}
     */
    public boolean inConstructor() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean inField() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean inMethod() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean inOverview() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean inPackage() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean inType() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isInlineTag() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public String toString(final Tag tag) {
        if (tag.name().equals("@" + NAME)) {
            return getOutput(!tag.text().startsWith(NEGATION));
        } else {
            return "";
        }
    }

    /**
     * {@inheritDoc}
     */
    public String toString(final Tag[] tagArray) {
        boolean positiveOutput = false;
        for (Tag tag : tagArray) {
            if (tag.name().equals("@" + NAME)) {
                if (tag.text().startsWith(NEGATION)) {
                    return getOutput(false);
                } else {
                    positiveOutput = true;
                }
            }
        }
        if (positiveOutput) {
            return getOutput(true);
        } else {
            return "";
        }
    }
    
    /**
     * Writes the output string for this taglet.
     * 
     * @param isGenerated indicates whether the corresponding element was
     *     generated or not
     * @return an HTML formatted string
     */
    private String getOutput(final boolean isGenerated) {
        if (isGenerated) {
            return "<dt><b>" + POS_HEADER + "</b><dd>" + POS_TEXT + "</dd>\n";
        } else {
            return "<dt><b>" + NEG_HEADER + "</b><dd>" + NEG_TEXT + "</dd>\n";
        }
    }
    
    /**
     * Register this Taglet.
     * 
     * @param tagletMap the map to register this tag to
     */
    @SuppressWarnings("unchecked")
    public static void register(final Map tagletMap) {
        Taglet newTaglet = new GeneratedTaglet();
        Taglet oldTaglet = (Taglet)tagletMap.get(NAME);
        if (oldTaglet != null) {
            tagletMap.remove(NAME);
        }
        tagletMap.put(NAME, newTaglet);
    }

}
