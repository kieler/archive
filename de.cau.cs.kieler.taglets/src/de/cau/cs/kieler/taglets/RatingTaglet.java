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
import java.util.StringTokenizer;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;

/**
 *
 * @author msp
 */
public class RatingTaglet implements Taglet {

    /** the name of this taglet. */
    private static final String NAME = "kieler.rating";
    /** the header that is displayed in generated markup. */
    private static final String HEADER = "Rating";
    /** the path to icon files. */
    private static final String ICON_PATH = "http://rtsys.informatik.uni-kiel.de/trac/kieler/browser/trunk/standalone/de.cau.cs.kieler.taglets/icons/";
    
    /** enumeration of ratings. */
    private enum Rating {
        RED, YELLOW, GREEN, BLUE
    }
    
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
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean inField() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean inMethod() {
        return false;
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
        return false;
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
        return toString(new Tag[] {tag});
    }

    /** result value subtracted for proposed ratings. */
    private static final float PROPOSED_VAL = 0.5f;
    
    /**
     * {@inheritDoc}
     */
    public String toString(final Tag[] tagArray) {
        int maxIndex = -1;
        float maxValue = 0;
        for (int i = 0; i < tagArray.length; i++) {
            if (tagArray[i].name().equals("@" + NAME) && tagArray[i].text() != null) {
                StringTokenizer tokenizer = new StringTokenizer(tagArray[i].text(), " \t\n\r");
                boolean proposed = false;
                while (tokenizer.hasMoreTokens()) {
                    String token = tokenizer.nextToken();
                    if ("proposed".equals(token.toLowerCase())) {
                        proposed = true;
                    } else {
                        try {
                            Rating rating = Rating.valueOf(token.toUpperCase());
                            float value = rating.ordinal() - (proposed ? PROPOSED_VAL : 0.0f);
                            if (value > maxValue) {
                                maxValue = value;
                                maxIndex = i;
                            }
                            break;
                        } catch (IllegalArgumentException exception) {
                            // ignore exception
                        }
                    }
                }
            }
        }
        if (maxIndex >= 0) {
            String date = null;
            boolean proposed = false;
            Rating rating = null;
            StringBuffer comment = new StringBuffer();
            StringTokenizer tokenizer = new StringTokenizer(tagArray[maxIndex].text(), " \t\n\r");
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                if (rating == null) {
                    if ("proposed".equals(token.toLowerCase())) {
                        proposed = true;
                    } else {
                        try {
                            rating = Rating.valueOf(token.toUpperCase());
                        } catch (IllegalArgumentException exception) {
                            if (date == null) {
                                date = token;
                            }
                        }
                    }
                } else {
                    comment.append(token + " ");
                }
            }
            return generateMarkup(date, proposed, rating, comment.toString());
        } else {
            return generateMarkup(null, false, Rating.RED, null);
        }
    }
    
    /** additional options for image tags. */
    private static final String IMG_OPTIONS = "align=\"bottom\"";
    
    /**
     * Generates HTML code for the given rating specification.
     * 
     * @param date date of code rating
     * @param proposed indicates whether the rating is proposed
     * @param rating the (proposed or final) rating
     * @param comment additional comment
     * @return a string to display in API documentation
     */
    private static String generateMarkup(final String date, final boolean proposed,
            final Rating rating, final String comment) {
        StringBuffer output = new StringBuffer("<dt><b>" + HEADER + " <img src=\"" + ICON_PATH);
        if (proposed && rating != Rating.RED) {
            output.append("prop_");
        }
        output.append(rating.toString().toLowerCase() + ".png?format=raw\" alt=\""
                + rating.toString() + "\" " + IMG_OPTIONS + "></b></dt><dd>");
        if (date != null) {
            output.append("(" + date + ") ");
        }
        if (comment != null) {
            output.append(comment);
        }
        output.append("</dd>");
        return output.toString();
    }
    
    /**
     * Register this Taglet.
     * 
     * @param tagletMap the map to register this tag to
     */
    @SuppressWarnings("unchecked")
    public static void register(final Map tagletMap) {
        Taglet newTaglet = new RatingTaglet();
        Taglet oldTaglet = (Taglet)tagletMap.get(NAME);
        if (oldTaglet != null) {
            tagletMap.remove(NAME);
        }
        tagletMap.put(NAME, newTaglet);
    }

}
