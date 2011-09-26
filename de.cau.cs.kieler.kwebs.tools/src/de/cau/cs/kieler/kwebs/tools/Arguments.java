/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 *
 * http://www.informatik.uni-kiel.de/rtsys/kieler/
 * 
 * Copyright 2011 by
 * + Christian-Albrechts-University of Kiel
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 * 
 * This code is provided under the terms of the Eclipse Public License (EPL).
 * See the file epl-v10.html for the license text.
 */
package de.cau.cs.kieler.kwebs.tools;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author msp
 */
public class Arguments {
    
    /** Display a help message. */
    public static final String[] HELP = { "help", "-h" };
    /** Specify server address. */
    public static final String[] SERVER = { "server", "-s", "url" };
    /** Read from the given input file. */
    public static final String[] INFILE = { "infile", "-i", "input", "in" };
    /** Write to the given output file. */
    public static final String[] OUTFILE = { "outfile", "-o", "output", "out" };
    /** Select a specific graph format. */
    public static final String[] FORMAT = { "format", "-f" };
    /** Display full stack traces. */
    public static final String[] STACKTRACE = { "stacktrace", "-t", "trace" };
    
    /** The list of known parameters, except {@link #HELP}. */
    private static final String[][] PARAMS = { SERVER, INFILE, OUTFILE, FORMAT, STACKTRACE };
    
    /**
     * Check whether the given parameter is one of the known program parameters.
     * This also returns false if the parameter is in {@link #HELP}.
     * 
     * @param param a user-given parameter
     * @return true if the parameter is known
     */
    private static boolean isknown(final String param) {
        for (String[] parSet : PARAMS) {
            for (String p : parSet) {
                if (p.equals(param)) {
                    return true;
                }
            }
        }
        return false;
    }

    /** map of tool parameters read from the command line. */
    private HashMap<String, String> parsedParams = new HashMap<String, String>();
    /** map of layout options read from the command line. */
    private HashMap<String, String> parsedOptions = new HashMap<String, String>();
    
    /**
     * Parse the command line arguments and stores the key/value pairs for later use.
     *
     * @param args Array of type {@code String} containing the command line arguments
     */
    public Arguments(final String[] args) {
        for (String arg : args) {
            Map<String, String> argMap = null;
            int keyIndex = 0;
            if (arg.startsWith("--")) {
                argMap = parsedOptions;
                keyIndex = 2;
            } else {
                argMap = parsedParams;
            }
            int valueIndex = arg.indexOf('=');
            String key, value = "";
            if (valueIndex < 0) {
                key = arg.substring(keyIndex);
            } else {
                key = arg.substring(keyIndex, valueIndex);
                value = arg.substring(valueIndex + 1);
                if (value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2) {
                    value = value.substring(1, value.length() - 1).trim();
                } else if (value.startsWith("'") && value.endsWith("'") && value.length() >= 2) {
                    value = value.substring(1, value.length() - 1).trim();
                }
            }
            if (key.length() == 0 || keyIndex == 0 && !isknown(key) || argMap.containsKey(key)) {
                helpExit(key.equals(HELP[0]) || key.equals(HELP[1]));
            }
            argMap.put(key, value);
        }
    }
    
    /**
     * Return the parameter value for the given key set.
     * 
     * @param keyset a parameter key set
     * @return the corresponding value, or the empty string if no value was specified,
     *     or {@code null} if the parameter is not present.
     */
    public String getParam(final String[] keyset) {
        for (String key : keyset) {
            String value = parsedParams.get(key);
            if (value != null) {
                return value;
            }
        }
        return null;
    }
    
    /**
     * Return the layout options given as parameters.
     * 
     * @return the set of options
     */
    public Set<Map.Entry<String, String>> getOptions() {
        return parsedOptions.entrySet();
    }
    
    /**
     * Display help on tool usage and exit.
     * 
     * @param normal whether the help message is generated normally, i.e. by the
     *     {@link #HELP} parameter
     */
    private static void helpExit(final boolean normal) {
        if (normal) {
            for (String line : HELP_INTRO) {
                System.out.println(line);
            }
            System.out.println();
            for (String line : HELP_USAGE) {
                System.out.println(line);
            }
            System.exit(0);
        } else {
            for (String line : HELP_USAGE) {
                System.err.println(line);
            }
            System.exit(1);
        }
    }
    
    /** The introductory help text. */
    private static final String[] HELP_INTRO = new String[] {
            "Console Client for the KIELER Layout Web Service (KWebS)",
            "   Copyright 2011 by Real-Time and Embedded Systems Group,",
            "   Department of Computer Science, Christian-Albrechts-Universitaet zu Kiel",
            "",
            "Performs a request to a KWebS layout web service by sending a graph",
            "and receiving the layout result. If called with no arguments, the input",
            "is read from stdin, the result is written to stdout, the KGraph format is",
            "assumed, and the KIELER layout server is used, which is available at",
            "   " + ConsoleClient.DEFAULT_SERVER
        };
    
    /** The usage help text. */
    private static final String[] HELP_USAGE = new String[] {
        "Available arguments:",
        "   " + HELP[0] + " or " + HELP[1],
        "      Display detailed information.",
        "   " + STACKTRACE[0] + " or " + STACKTRACE[1],
        "      Print complete stack traces in case of error.",
        "   " + SERVER[0] + "=val or " + SERVER[1] + "=val",
        "      Choose 'val' as server address.",
        "   " + INFILE[0] + "=val or " + INFILE[1] + "=val",
        "      Read the input from the file specified by 'val'. If possible, the graph",
        "      format is derived from the file extension.",
        "   " + OUTFILE[0] + "=val or " + OUTFILE[1] + "=val",
        "      Write the result to the file specified by 'val'.",
        "   " + FORMAT[0] + "=val or " + FORMAT[1] + "=val",
        "      Choose 'val' as graph format.",
        "   --key=val",
        "      Set the layout parameter 'key' to 'val'. A layout parameter can be",
        "      a fully qualified layout option identifier or only its last segment."
    };

}
