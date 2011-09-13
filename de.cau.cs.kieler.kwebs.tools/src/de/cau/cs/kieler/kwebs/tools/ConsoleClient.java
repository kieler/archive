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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
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
 * @kieler.rating 2011-09-07 red
 *
 * @author swe
 */
public final class ConsoleClient {

    /** The service object. */
    private static LayoutService layoutService;

    /** The web service interface used. */
    private static LayoutServicePort layoutPort;

    /** Mapping of file extensions to format specifiers. */
    private static Hashtable<String, String> formatsByExtension
        = new Hashtable<String, String>();

    /** The KGraph model in XMI serialization. */
    public static final String FORMAT_KGRAPH_XMI
        = "de.cau.cs.kieler.format.kgraph.xmi";
    /** The KGraph model in compressed XMI serialization. */
    public static final String FORMAT_KGRAPH_XMI_COMPRESSED
        = "de.cau.cs.kieler.format.kgraph.xmi.compressed";
    /** The OGML format. */
    public static final String FORMAT_OGML
        = "net.ogdf.ogml";
    /** The GraphML format. */
    public static final String FORMAT_GRAPHML
        = "org.graphdrawing.graphml";
    /** The Graphviz Dot format. */
    public static final String FORMAT_DOT
        = "org.graphviz.dot";
    /** The Matrix Market format. */
    public static final String FORMAT_MATRIX
        = "gov.nist.math.matrix";

    /** Initialization of the extension to format specifier mapping. */
    static {
        formatsByExtension.put("kgraph", FORMAT_KGRAPH_XMI);
        formatsByExtension.put("kgraphc", FORMAT_KGRAPH_XMI_COMPRESSED);
        formatsByExtension.put("dot", FORMAT_DOT);
        formatsByExtension.put("gml", FORMAT_GRAPHML);
        formatsByExtension.put("ogml", FORMAT_OGML);
        formatsByExtension.put("mtx", FORMAT_MATRIX);
    }

    /**
     * The main application.
     * 
     * @param args
     *            the command line arguments
     */
    public static void main(final String[] args) {
       Map<String, String> arguments = parseArgs(args);
       // No command line arguments means displaying help and exit
       if (arguments.size() == 0) {
           displayHelp();
           System.exit(0);
       }
       // The address of the layout service to connect to
       String server = arguments.get("server");
       if (server == null || server.length() == 0) {
           throw new IllegalStateException("server not configured");
       }
       // The format specifier 
       String format = null;
       // The optional file to read graph from
       String infile = arguments.get("infile");
       // The optional file to write graph to
       String outfile = arguments.get("outfile");
       // The stream to read graph from
       InputStream inStream = System.in;
       // The stream to write layouted graph to; by default stdout
       OutputStream outStream = System.out;
       // Try to acquire the format specifier from file extension
       if (infile != null) {
           if (infile.indexOf(".") > -1 && infile.indexOf(".") < infile.length() - 1) {
               // Gets the format specifier by the file extension; may be null
               format = formatsByExtension.get(
                   infile.substring(infile.lastIndexOf(".") + 1).toLowerCase()
               );
           }
       }
       // Possible override of format specification
       if (arguments.containsKey("format")) {
           format = arguments.get("format");
       }
       if (format == null || format.length() == 0) {
           throw new IllegalStateException(
               "Format not configured or not deriveable from file extension"
           );
       }
       try {
           // Shall we read the graph from a file?
           if (infile != null) {
               inStream = new FileInputStream(infile);
           }
           // Shall we write the result to a file?
           if (outfile != null) { 
               outStream = new FileOutputStream(outfile);
           }
       } catch (FileNotFoundException e) { 
           // If input stream had been initialized before exception occurred we
           // need to close it
           if (inStream != null && inStream != System.in) {
               try {
                   inStream.close();
               } catch (IOException x) {
                   // Nothing we can do about it here
               }
           }
           throw new IllegalArgumentException(
               "Error while accessing file '" + infile + "' :" + e.getMessage()
           );
       }
       // Optional list of layout options
       List<GraphLayoutOption> options = new ArrayList<GraphLayoutOption>();
       // Layout options are defined by double '-' prefix; the first '-' was removed
       // when parsing the command line arguments into the argument map with parseArgs()
       for (String argument : arguments.keySet()) {
           if (argument.startsWith("-") && arguments.get(argument) != null) {
               options.add(new GraphLayoutOption(argument.substring(1), arguments.get(argument)));
           }
       }
       // Was a specific algorithm defined by command line?
       if (arguments.containsKey("algorithm")) {
           options.add(new GraphLayoutOption(
               "de.cau.cs.kieler.algorithm", 
               arguments.get("algorithm")
           ));
       }       
       try { 
           connect(server);
           String input = new String(readStream(inStream));
           if (inStream != System.in) {
               inStream.close();
           }
           String output = layoutPort.graphLayout(input, format, options); 
           outStream.write(output.getBytes());
           if (outStream != System.out) {
               outStream.flush();
               outStream.close();
           }
       } catch (Exception e) {
           e.printStackTrace();
       }
    }

    //CHECKSTYLEOFF LineLength
    
    /** The help text. */
    private static final String[] HELPTEXT 
        = new String[] {
            "",
            "ConsoleClient for KWebS",
            "",
            "Copyright 2011 by Real-Time and Embedded Systems Group, Department",
            "of Computer Science, Christian-Albrechts-University of Kiel",
            "",
            "This application calls a KWebS layout service and does a single layout",
            "request. It is intended to be used on script basis for doing batch tests",
            "on the service. By default the graph to be layouted is read from stdin",
            "and the result is written to stdout. You can override this behaviour by",
            "optionally specifying the -infile ord the -outfile option to read from",
            "or write to a file.",
            "",
            "Options are:",
            "",
            "-server=<ADDRESS OF LAYOUT SERVER>",
            "-infile=<FILE TO READ GRAPH FROM>",
            "-outfile=<FILE TO WRITE RESULT GRAPH TO>",
            "-format=<FORMAT ID>",
            "-algorithm=<ALGORITHM ID>",
            "--<LAYOUT OPTION ID>=<LAYOUT OPTION VALUE>"
        };

    //CHECKSTYLEON LineLength
    
    /**
     * Displays information about how to use this tool.
     */
    private static void displayHelp() {
        for (String line : HELPTEXT) {
            System.out.println(line);
        }
    }

    /** */
    private static final String QNAME_NS
        = "http://rtsys.informatik.uni-kiel.de/layout";

    /** */
    private static final String QNAME_SERVICE
        = "LayoutService";

    /** Postfix to be added to the service address when connecting to a layout service. */
    private static final String WSDL_POSTFIX
        = "?wsdl";

    /**
     * Connects to the layout service at the given address.
     * 
     * @param server
     *            the server address to connect to
     * @throws MalformedURLException
     *            when the given server address is malformed 
     */
    private static void connect(final String server) throws MalformedURLException {
        layoutService = new LayoutService(
            new URL(server + WSDL_POSTFIX),
            new QName(QNAME_NS, QNAME_SERVICE)
        );
        layoutPort = layoutService.getLayoutServicePort();         
    }
    
    /** Buffer size for stream IO. */
    private static final int BUFFER_SIZE
        = 10240;
    
    /**
     * Reads data from a stream.
     * 
     * @param inStream
     *            the stream to read from
     * @return the data read
     * @throws IOException
     *             when an error happens while reading from the stream
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
     * Parses the command line arguments and stores the key/value
     * pairs in a {@code HashMap}. A '/' or '-' at the first position is removed.
     *
     * @param args
     *            Array of type {@code String} containing
     *            the command line arguments
     * @return a {@code HashMap<String, String>} containing key/value pairs generated
     *         from the command line arguments
     */
    private static HashMap<String, String> parseArgs(final String[] args) {
        HashMap<String, String> result = new HashMap<String, String>();
        if (args == null || args.length == 0) {
            return result;
        }
        int index = 0;
        String key = null;
        String value = null;
        for (String arg : args) {
            // Exception on parsing of a command line argument is ignored
            // since it can only come from an illegal formed argument
            //CHECKSTYLEOFF EmptyBlock
            try {
                if (arg.charAt(0) == '/' || arg.charAt(0) == '-') {
                    arg = arg.substring(1);
                }
                index = arg.indexOf("=");
                // Ignoring arguments without '=' or of the form 'a=' or '=a'
                if (index > 0 && index < arg.length() - 1) { 
                    key = arg.substring(0, index);
                    value = arg.substring(index + 1);
                    if (value.startsWith("\"") && value.endsWith("\"")) {
                        value = value.substring(1, value.length() - 1).trim();
                    } else if (value.startsWith("'") && value.endsWith("'")) {
                        value = value.substring(1, value.length() - 1).trim();
                    }
                    if (key != null && key.length() > 0 && value != null) {
                        result.put(key, value);
                    }
                }
            } catch (Exception e) {
            }
            //CHECKSTYLEON EmptyBlock            
        }
        return result;
    }

    /**
     * Private constructor.
     */
    private ConsoleClient() {        
    }
    
}
