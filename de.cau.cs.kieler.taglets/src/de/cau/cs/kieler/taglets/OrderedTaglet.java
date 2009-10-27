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
 * Taglet for "ordered" tags in generated code.
 * 
 * @author msp
 */
public class OrderedTaglet implements Taglet {

    /** the name of this taglet */
    private static final String NAME = "ordered";
    /** printed header for this taglet */
    private static final String HEADER = "Ordered";

    /* (non-Javadoc)
     * @see com.sun.tools.doclets.Taglet#getName()
     */
    public String getName() {
        return NAME;
    }

    /* (non-Javadoc)
     * @see com.sun.tools.doclets.Taglet#inConstructor()
     */
    public boolean inConstructor() {
        return false;
    }

    /* (non-Javadoc)
     * @see com.sun.tools.doclets.Taglet#inField()
     */
    public boolean inField() {
        return true;
    }

    /* (non-Javadoc)
     * @see com.sun.tools.doclets.Taglet#inMethod()
     */
    public boolean inMethod() {
        return false;
    }

    /* (non-Javadoc)
     * @see com.sun.tools.doclets.Taglet#inOverview()
     */
    public boolean inOverview() {
        return false;
    }

    /* (non-Javadoc)
     * @see com.sun.tools.doclets.Taglet#inPackage()
     */
    public boolean inPackage() {
        return false;
    }

    /* (non-Javadoc)
     * @see com.sun.tools.doclets.Taglet#inType()
     */
    public boolean inType() {
        return false;
    }

    /* (non-Javadoc)
     * @see com.sun.tools.doclets.Taglet#isInlineTag()
     */
    public boolean isInlineTag() {
        return false;
    }

    /* (non-Javadoc)
     * @see com.sun.tools.doclets.Taglet#toString(com.sun.javadoc.Tag)
     */
    public String toString(Tag tag) {
        if (tag.name().equals("@" + NAME))
            return "<dt><b>" + HEADER + "</b><dd></dd>\n";
        else return "";
    }

    /* (non-Javadoc)
     * @see com.sun.tools.doclets.Taglet#toString(com.sun.javadoc.Tag[])
     */
    public String toString(Tag[] tagArray) {
        boolean printOutput = false;
        for (Tag tag : tagArray) {
            if (tag.name().equals("@" + NAME)) {
                printOutput = true;
                break;
            }
        }
        if (printOutput)
            return "<dt><b>" + HEADER + "</b><dd></dd>\n";
        else return "";
    }

    /**
     * Register this Taglet.
     * 
     * @param tagletMap the map to register this tag to
     */
    @SuppressWarnings("unchecked")
    public static void register(Map tagletMap) {
        Taglet newTaglet = new OrderedTaglet();
        Taglet oldTaglet = (Taglet)tagletMap.get(NAME);
        if (oldTaglet != null) {
            tagletMap.remove(NAME);
        }
        tagletMap.put(NAME, newTaglet);
    }
    
}
