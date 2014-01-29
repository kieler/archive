/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 *
 * http://www.informatik.uni-kiel.de/rtsys/kieler/
 * 
 * Copyright 2013 by
 * + Christian-Albrechts-University of Kiel
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 * 
 * This code is provided under the terms of the Eclipse Public License (EPL).
 * See the file epl-v10.html for the license text.
 */
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

/**
 * @author uru
 */
public class KIELERLayout {

    public static final String DEFAULT_SERVER = "http://localhost:9444";
    private static final String LAYOUT_HANDLER = "/live";

    /**
     * Performs a layout request for the passed graph and returns a serialized version of the
     * requested output format.
     * 
     * @param server
     *            the location of the webserver, e.g., 'http://localhost:9444'.
     * @param inFormat
     *            the input format, e.g., 'org.json'.
     * @param outFormat
     *            the output format, e.g., 'org.w3.svg'.
     * @param options
     *            a map containing (key, value) pairs representing layout options.
     * @param graph
     *            the graph in a serialized form of the input format.
     * @return the layouted graph in a serialized form of the output format.
     */
    public static String layout(final String server, final String inFormat, final String outFormat,
            final Map<String, Object> options, final String graph) {

        // gather all required information
        String usedServer = server != null ? server : DEFAULT_SERVER;
        String charset = "UTF-8";

        // convert the options to a json object
        String jsonOptions = "{";
        if (options != null) {
            for (Entry<String, Object> entry : options.entrySet()) {
                // json supports a trailing comma
                jsonOptions += entry.getKey() + ": " + entry.getValue().toString() + ",";
            }
        }
        jsonOptions += "}";

        // assemble the query string
        try {
            String query =
                    String.format("graph=%s&config=%s&iFormat=%s&oFormat=%s",
                            URLEncoder.encode(graph, charset),
                            URLEncoder.encode(jsonOptions, charset),
                            URLEncoder.encode(inFormat, charset),
                            URLEncoder.encode(outFormat, charset));
            
            // connect to the server and issue the request
            final URL url = new URL(usedServer + LAYOUT_HANDLER);
            final URLConnection urlConnection = url.openConnection();
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            urlConnection.setRequestProperty("type", "POST");
            urlConnection.setRequestProperty("Content-Length", "" + query.getBytes().length);
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            // write the data to the post
            urlConnection.getOutputStream().write(query.getBytes());
            urlConnection.getOutputStream().close();
            // open the connection
            urlConnection.connect();

            // wait for a result
            final InputStream inputStream = urlConnection.getInputStream();
            return convertStreamToString(inputStream);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String convertStreamToString(final InputStream is) {
        Scanner scanner = new Scanner(is);
        Scanner s = scanner.useDelimiter("\\A");
        String content = s.hasNext() ? s.next() : "";
        scanner.close();
        return content;
    }
}
