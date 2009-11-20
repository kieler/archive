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
package de.cau.cs.kieler.doclets;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Writer for HTML code that is reused in different documents.
 *
 * @author msp
 */
public final class HtmlWriter {

    private HtmlWriter() {
    }
    
    /** header for HTML files. */
    public static final String HTML_HEADER = "<!DOCTYPE doctype PUBLIC \"-//w3c//dtd html 4.0 transitional//en\">\n<html>\n";
    /** footer for HTML files. */
    public static final String HTML_FOOTER = "</body>\n</html>\n";
    
    /**
     * Write HTML header code.
     * 
     * @param writer writer to which output is written
     * @param title title to display on the HTML page
     * @throws IOException if writing fails
     */
    public static void writeHeader(final BufferedWriter writer, final String title)
            throws IOException {
        writer.write(HTML_HEADER + "<head><title>" + title + "</title></head>\n<body>\n");
        writer.write("<h1>" + title + "</h1>\n");
    }
    
    /**
     * Write HTML footer code.
     * 
     * @param writer writer to which output is written
     * @throws IOException if writing fails
     */
    public static void writeFooter(final BufferedWriter writer) throws IOException {
        writer.write(HTML_FOOTER);
    }
    
}
