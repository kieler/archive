'''
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 *
 * http://www.informatik.uni-kiel.de/rtsys/kieler/
 * 
 * Copyright 2014 by
 * + Kiel University
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 * 
 * This code is provided under the terms of the Eclipse Public License (EPL).
 * See the file epl-v10.html for the license text.
 * 
'''
import http.client, urllib.parse, socket

class KIELERLayout:
    def layout(self, server, inFormat, outFormat, options, graph):
        """Performs a layout request for the passed graph and returns a serialized version of the
           requested output format.
       
           @param server
                      the location of the webserver, e.g., 'http://localhost:9444'.
           @param inFormat
                      the input format, e.g., 'org.json'.
           @param outFormat
                      the output format, e.g., 'org.w3.svg'.
           @param options
                      a dictionary containing (key, value) pairs representing layout options.
           @param graph
                      the graph in a serialized form of the input format.
           @return the layouted graph in a serialized form of the output format."""

        query = urllib.parse.quote_plus("graph=%s&config=%s&iFormat=%s&oFormat=%s" % (graph, options, inFormat, outFormat))
        headers = {"Content-type": "application/json; charset=utf-8", "Content-Length": len(query)}

        try:
            conn = http.client.HTTPConnection(server)
            conn.request("POST", "/live", query, headers)
            response = conn.getresponse()            
            if response.reason != 'OK':
                print(response.status, response.reason)
            data = response.read()
            conn.close()
            return data
        except (http.client.HTTPException, socket.error) as ex:
            print("Error: %s", ex)

