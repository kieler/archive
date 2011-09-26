/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 *
 * http://www.informatik.uni-kiel.de/rtsys/kieler/
 *
 * Copyright 2008 by
 * + Christian-Albrechts-University of Kiel
 *     + Department of Computer Science
 *         + Real-Time and Embedded Systems Group
 *
 * This code is provided under the terms of the Eclipse Public License (EPL).
 * See the file epl-v10.html for the license text.
 */

package de.cau.cs.kieler.kwebs.tools;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import de.cau.cs.kieler.kwebs.jaxws.GraphLayoutOption;
import de.cau.cs.kieler.kwebs.jaxws.LayoutService;
import de.cau.cs.kieler.kwebs.jaxws.LayoutServicePort;

/**
 * Utility application for calling the layout service from the console. The graph to be layouted is
 * read from stdin and the result is written to stdout for script based usage.
 *
 * @author swe
 */
public final class ConsoleClient {
    
    /**
     * Hidden constructor to prevent instantiation.
     */
    private ConsoleClient() {        
    }

    /** The KGraph format identifier in XMI serialization. */
    static final String FORMAT_KGRAPH_XMI = "de.cau.cs.kieler.format.kgraph.xmi";
    /** The KGraph format identifier in compressed XMI serialization. */
    static final String FORMAT_KGRAPH_XMI_COMPRESSED = "de.cau.cs.kieler.format.kgraph.xmi.compressed";
    /** The OGML format identifier. */
    static final String FORMAT_OGML = "net.ogdf.ogml";
    /** The GraphML format identifier. */
    static final String FORMAT_GRAPHML = "org.graphdrawing.graphml";
    /** The Graphviz Dot format identifier. */
    static final String FORMAT_DOT = "org.graphviz.dot";
    /** The Matrix Market format identifier. */
    static final String FORMAT_MATRIX = "gov.nist.math.matrix";

    /** The default server address: Rtsys group server at CAU Kiel. */
    static final String DEFAULT_SERVER = "http://rtsys.informatik.uni-kiel.de:9442/layout";
    /** The namespace of the web service. */
    private static final String QNAME_NS = "http://rtsys.informatik.uni-kiel.de/layout";
    /** The service name of the web service. */
    private static final String QNAME_SERVICE = "LayoutService";
    /** Postfix to be added to the service address when connecting to a layout service. */
    private static final String WSDL_POSTFIX = "?wsdl";
    
    /**
     * The main application.
     * 
     * @param args the command line arguments
     * @throws Exception if the application ends with an error
     */
    public static void main(final String[] args) throws Exception {
        int exitCode = 0;
        
        // Parse the command-line arguments
        Arguments arguments = new Arguments(args);
        
        // The address of the layout service to connect to
        String server = arguments.getParam(Arguments.SERVER);
        if (server == null) {
            server = DEFAULT_SERVER;
        }
       
        // The format specifier 
        String format = arguments.getParam(Arguments.FORMAT);
        // The optional file to read graph from
        String infile = arguments.getParam(Arguments.INFILE);
        // The optional file to write graph to
        String outfile = arguments.getParam(Arguments.OUTFILE);
        // Try to acquire the format specifier from file extension
        if (format == null && infile != null) {
            int extIndex = infile.lastIndexOf('.');
            if (extIndex >= 0) {
                String extension = infile.substring(extIndex + 1).toLowerCase();
                format = formatsByExtension().get(extension);
            }
        }
        if (format == null) {
            format = FORMAT_KGRAPH_XMI;
        }
        
        // The stream to read the input graph from
        InputStream inStream = System.in;
        // The stream to write the resulting graph to
        OutputStream outStream = System.out;
        try {
            // Shall we read the graph from a file?
            if (infile != null) {
                inStream = new FileInputStream(infile);
            }
            // Shall we write the result to a file?
            if (outfile != null) { 
                outStream = new FileOutputStream(outfile);
            }
            
            // Optional list of layout options
            List<GraphLayoutOption> options = new ArrayList<GraphLayoutOption>();
            for (Map.Entry<String, String> entry : arguments.getOptions()) {
                options.add(new GraphLayoutOption(entry.getKey(), entry.getValue()));
            }

            // Connect to the layout service and call it
            LayoutServicePort layoutPort = connect(server);
            String input = new String(readStream(inStream));
            String output = layoutPort.graphLayout(input, format, options);
            
            // Write result to the output stream
            outStream.write(output.getBytes());
            outStream.flush();
            
        } catch (Exception exception) {
            if (arguments.getParam(Arguments.STACKTRACE) != null) { 
                exception.printStackTrace();
            } else {
                System.err.println("Error: " + exception.getMessage());
            }
            exitCode = 1;
        } finally {
            try {
                inStream.close();
                outStream.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }

    /**
     * Connect to the layout service at the given address.
     * 
     * @param server
     *            the server address to connect to
     * @returns the layout service port
     * @throws MalformedURLException
     *            when the given server address is malformed
     */
    private static LayoutServicePort connect(final String server) throws MalformedURLException {
        LayoutService layoutService = new LayoutService(new URL(server + WSDL_POSTFIX),
                new QName(QNAME_NS, QNAME_SERVICE));
        return layoutService.getLayoutServicePort();         
    }
    
    /** Buffer size for stream IO. */
    private static final int BUFFER_SIZE = 2048;
    
    /**
     * Reads data from a stream.
     * 
     * @param inStream the stream to read from
     * @return the data as a {@code String} instance
     * @throws IOException when an error happens while reading from the stream
     */
    private static byte[] readStream(final InputStream inStream) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int read = -1;
        byte[] buffer = new byte[BUFFER_SIZE];
        do {
            read = inStream.read(buffer);
            if (read > 0) {
                out.write(buffer, 0, read);
            }
        } while (read > 0);
        return out.toByteArray();
    }

    /**
     * Creates a mapping of file extensions to format specifiers.
     * 
     * @return a file extensions mapping
     */
    private static Hashtable<String, String> formatsByExtension() {
        Hashtable<String, String> formats = new Hashtable<String, String>();
        formats.put("kgraph", FORMAT_KGRAPH_XMI);
        formats.put("xmi", FORMAT_KGRAPH_XMI);
        formats.put("dot", FORMAT_DOT);
        formats.put("graphviz", FORMAT_DOT);
        formats.put("graphml", FORMAT_GRAPHML);
        formats.put("ogml", FORMAT_OGML);
        formats.put("mtx", FORMAT_MATRIX);
        formats.put("matrix", FORMAT_MATRIX);
        return formats;
    }
    
}
