package de.cau.cs.kieler.kwebs.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.cau.cs.kieler.core.kgraph.KNode;
import de.cau.cs.kieler.core.properties.IPropertyHolder;
import de.cau.cs.kieler.core.properties.MapPropertyHolder;
import de.cau.cs.kieler.keg.Node;
import de.cau.cs.kieler.keg.diagram.custom.random.RandomGraphGenerator;
import de.cau.cs.kieler.keg.diagram.custom.random.RandomGraphGenerator.EdgeDetermination;
import de.cau.cs.kieler.keg.diagram.custom.random.RandomGraphGenerator.GraphType;
import de.cau.cs.kieler.kwebs.transformation.KGraphXmiTransformer;

public class GraphGenerator {
    
    private static KGraphXmiTransformer transformer = new KGraphXmiTransformer();
    
    public static void main(String[] args) {
        IPropertyHolder holder = new MapPropertyHolder();
        RandomGraphGenerator generator = new RandomGraphGenerator();        
        Random random = new Random();
        holder.setProperty(RandomGraphGenerator.GRAPH_TYPE, GraphType.BICONNECTED);
        holder.setProperty(RandomGraphGenerator.EDGE_DETERMINATION, EdgeDetermination.GRAPH_EDGES);
        int errors = 0;
        for (int i = 0; i < 10000; i++) {            
            int nodes = random.nextInt(3000) + 1;
            int edges = nodes + (int)(random.nextDouble() * nodes);
            if (i % 25 == 0) {
                System.out.println("(" + i + " " + errors + " " + nodes + " " + edges + ")");
            }
            holder.setProperty(RandomGraphGenerator.NUMBER_OF_NODES, nodes);
            holder.setProperty(RandomGraphGenerator.NUMBER_OF_EDGES, edges);                    
            try {
                storeKEGAsKGraph(generator.generate(holder), nodes, edges);
            } catch (IOException e) {
                System.out.println("ERROR: " + e.getMessage());
                errors++;
            }
        }
    }

    private static final Map<String, String> REPLACEMENTS
        = new HashMap<String, String>();
    
    static {
        REPLACEMENTS.put("xmlns:keg=\"http://kieler.cs.cau.de/KEG\"", "xmlns:kgraph=\"http://kieler.cs.cau.de/KGraph\"");
        REPLACEMENTS.put("keg:Node", "kgraph:KNode");
        REPLACEMENTS.put("keg:Edge", "kgraph:KEdge");
        REPLACEMENTS.put(" nodeLabel=\"[a-zA-Z0-9]+\"", "");
    }
    
    private static int index = 0;
    
    private static final String ROOT = "C:\\kegrandom";
    
    private static void storeKEGAsKGraph(Node kegNode, int nodes, int edges) throws IOException {        
        String kgraph = transformer.serialize(kegNode);
        for (String from : REPLACEMENTS.keySet()) {
            String to = REPLACEMENTS.get(from);
            kgraph = kgraph.replaceAll(from, to);
        }
        transformer.deserialize(kgraph);            
        String filename = ROOT + "\\random_bic_" + index + "_" + nodes + "_" + edges + ".kgraph";        
        if (!new File(ROOT).exists()) {
            new File(ROOT).mkdirs();
        }
        File file = new File(filename);
        if (!file.exists()) {
            FileOutputStream outstream = new FileOutputStream(file);
            outstream.write(kgraph.getBytes());
            outstream.flush();
            outstream.close();                    
        }
        index++;
    }
    
}
