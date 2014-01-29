using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using System.Net;

namespace KIELER {
    
    /**
     * @author uru
     */
    class KIELERLayout {
        
        public static String DEFAULT_SERVER = "http://localhost:9444";
        private static String LAYOUT_HANDLER = "/live";

        /// <summary>
        ///     Performs a layout request for the passed graph and returns a serialized version of the
        ///     requested output format.
        /// </summary>
        /// <param name="server">
        ///      the location of the webserver, e.g., 'http://localhost:9444'.
        /// </param>
        /// <param name="inFormat"> 
        ///            the input format, e.g., 'org.json'.
        /// </param>
        /// <param name="outFormat"> 
        ///            the output format, e.g., 'org.w3.svg'.
        /// </param>
        /// <param name="options"> 
        ///            a dictionary containing (key, value) pairs representing layout options.
        /// </param>
        /// <param name="graph"> 
        ///            the graph in a serialized form of the input format.
        /// </param>
        /// <returns>the layouted graph in a serialized form of the output format.</returns>
        ///
        public static String layout(String server, String inFormat, String outFormat,
             Dictionary<String, Object> options, String graph) {

            // gather all required information
            String usedServer = server != null ? server : DEFAULT_SERVER;

            // convert the options to a json object
            String jsonOptions = "{";
            if (options != null) {
                foreach (KeyValuePair<String, Object> pair in options) {
                    // json supports a trailing comma
                    jsonOptions += pair.Key + ": " + pair.Value.ToString() + ",";
                }
            }
            jsonOptions += "}";


            // assemble the query string
            String query =
                    String.Format("graph={0}&config={1}&iFormat={2}&oFormat={3}",
                            Uri.EscapeUriString(graph),
                            Uri.EscapeUriString(jsonOptions),
                            Uri.EscapeUriString(inFormat),
                            Uri.EscapeUriString(outFormat));

            // connect to the server and issue the request
            HttpWebRequest request = (HttpWebRequest)WebRequest.Create(usedServer + LAYOUT_HANDLER);
            request.Method = "POST";
            request.ContentType = "application/json; charset=utf-8";
            // write the query to the request stream
            byte[] bytes = Encoding.UTF8.GetBytes(query);
            request.ContentLength = bytes.Length;
            request.GetRequestStream().Write(bytes, 0, bytes.Length);
            request.GetRequestStream().Close();

            String result = "";
            // retrieve a response
            HttpWebResponse response = (HttpWebResponse)request.GetResponse();
            using (StreamReader reader = new StreamReader(response.GetResponseStream())) {
                result += reader.ReadToEnd();
            }

            return result;
        }
    }
}
