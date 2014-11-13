package aiadp1.map;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.ListenableDirectedGraph;

import java.io.*;

public class MapBuilder {

    private ListenableDirectedGraph<Object, DefaultEdge> g;

    // Contructor with no arguments creates a default graph
    // Mode - 0 -> empty | 1 -> vertices | 2 -> verticesAndEdges
    public MapBuilder(int mode){
        // Hard Code Graph Here //
        this.g = new ListenableDirectedGraph<>( DefaultEdge.class );

        if( mode > 0) {
            // Add sample data to the graph
            g.addVertex( "v1" );
            g.addVertex( "v2" );
            g.addVertex( "v3" );
            g.addVertex( "v4" );

            if(mode > 1){
                g.addEdge( "v1", "v2" );
                g.addEdge( "v2", "v3" );
                g.addEdge( "v3", "v1" );
                g.addEdge( "v4", "v3" );
            }
        }
    }

    // Contructor that builds a graph from a file
    public MapBuilder(File f) {

        System.out.println("Creating graph from file " + f.getName() + ".");

        BufferedReader fis = null;

        try {
            fis = new BufferedReader(new FileReader(f));
            String graph = fis.readLine();

            this.g = new ListenableDirectedGraph<>( DefaultEdge.class );

            String vertices, edges;

            // Get the graph vertices names seperated by strings
            vertices = graph.split("\\[")[1].split("]")[0];

            for(String s: vertices.split(", ")){
                if(!s.isEmpty()) {
                    System.out.println("\tAdding vertex (" + s + ") to graph.");
                    this.g.addVertex(s);
                }
            }

            // Get the graph edges names seperated by strings
            // (v1,v2), (v2,v3), (v3,v1), (v4,v3)
            edges = graph.split("\\[")[2].split("]")[0];;

            for(String s: edges.split("\\),")){
                if(!s.isEmpty()) {
                    s = s.replace("(", " ").replace(")", " ").trim();
                    System.out.println("\tAdding edge (" + s.split(",")[0] + "->" + s.split(",")[1] + ") to graph.");
                    this.g.addEdge(s.split(",")[0], s.split(",")[1]);
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("File: \"" + f.getName() + "\" not found!");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Can't read next line from \"" + f.getName() + "\"");
            e.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
                System.out.println("Error while closing BufferedReader!");
                e.printStackTrace();
            }
        }
    }

    public ListenableDirectedGraph<Object, DefaultEdge> getGraph() {
        return g;
    }
}
