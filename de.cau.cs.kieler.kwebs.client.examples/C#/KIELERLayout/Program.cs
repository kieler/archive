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
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Web;
using System.Net;
using System.IO;

/**
 * @author uru
 */
namespace KielerCSharp {
    class Program {

        /**
         * Exemplarily execution of a layout request.
         */
        public static void Main(string[] args) {
            
            // local server
            String server = "http://localhost:9444";
            // sample graph
            String graph = "{id:\"root\",children:[{id:\"n1\",labels:[{text:\"n1\"}],width:100,height:100},"
                        + "{id:\"n2\",labels:[{text:\"n2\"}],width:100,height:50,children:[{id:\"n3\","
                        + "labels:[{text:\"n3\"}],width:20,height:20},{id:\"n4\",labels:[{text:\"n4\"}],width:20,"
                        + "height:20}],edges:[{id:\"e4\",source:\"n3\",target:\"n4\"}]}],"
                        + "edges:[{id:\"e1\",source:\"n1\",target:\"n2\"}]}";

            // some layout options
            Dictionary<String, Object> options = new Dictionary<String, Object>();
            options.Add("spacing", 100);
            options.Add("algorithm", "de.cau.cs.kieler.klay.layered");

            // perform the layout
            String layouted = KIELER.KIELERLayout.layout(server, "org.json", "org.w3.svg", options, graph);

            // just print it to the console
            Console.WriteLine(layouted);
            Console.ReadLine();
        }
    }
}
