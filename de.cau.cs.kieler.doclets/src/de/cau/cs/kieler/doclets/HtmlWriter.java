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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * Writer for HTML code that is reused in different documents.
 *
 * @author msp
 */
public final class HtmlWriter {

    private HtmlWriter() {
    }
    
    /** header for HTML files. */
    public static final String HTML_HEADER = "<!DOCTYPE doctype PUBLIC \"-//w3c//dtd "
            + "html 4.0 transitional//en\">\n<html>\n";
    /** file name for style sheet. */
    public static final String CSS_FILE = "style.css";
    /** style sheet link. */
    public static final String CSS_LINK = "<link rel=\"stylesheet\" type=\"text/css\" href=\"" 
            + CSS_FILE + "\">";
    /** style definitions, one definition per item. */
    public static final String[] STYLE_DEFS = {
        "td { background-color:#F0F0F0; padding:3px; border:1px solid #FAFAFA; }",
        "th { padding: 5px; border:1px solid #FAFAFA; }"
    };
    /** footer for HTML files. */
    public static final String HTML_FOOTER = "</body>\n</html>\n";
    
    /**
     * Write HTML header code.
     * 
     * @param writer writer to which output is written
     * @param title title to display on the HTML page
     * @throws IOException if writing fails
     */
    public static void writeHeader(final Writer writer, final String title)
            throws IOException {
        writer.write(HTML_HEADER + "<head><title>" + title + "</title>\n"
                + CSS_LINK + "</head>\n<body>\n");
        writer.write("<h1>" + title + "</h1>\n");
    }
    
    /**
     * Write HTML footer code.
     * 
     * @param writer writer to which output is written
     * @throws IOException if writing fails
     */
    public static void writeFooter(final Writer writer) throws IOException {
        writer.write(HTML_FOOTER);
    }
    
    /**
     * Create a style sheet file.
     * 
     * @param path path to the output directory for the style sheet file
     * @throws IOException if writing fails
     */
    public static void createStyleSheet(final String path) throws IOException {
        File outFile = new File(path, CSS_FILE);
        outFile.createNewFile();
        Writer writer = new FileWriter(outFile);
        for (String def : STYLE_DEFS) {
            writer.write(def + "\n");
        }
        writer.flush();
        writer.close();
    }
    
}
