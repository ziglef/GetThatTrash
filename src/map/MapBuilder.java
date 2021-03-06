package map;

import javafx.util.Pair;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.ListenableDirectedGraph;

import java.io.*;
import java.util.ArrayList;

/**
 * Class responsible for build a map
 *
 * @author Rui Grandão  - ei11010@fe.up.pt
 * @author Tiago Coelho - ei11012@fe.up.pt
 */
public class MapBuilder {


    private ListenableDirectedGraph<Vertex, DefaultEdge> g;
    private ArrayList<String> verticesRef;
    private ArrayList<Pair<String, String>> edgesRef;

    /**
     * Default constructor
     */
    public MapBuilder(){
        this.g = new ListenableDirectedGraph<>( DefaultEdge.class );
        this.verticesRef = new ArrayList<>();
        this.edgesRef = new ArrayList<>();
    }



    /**
     *  // Contructor with no arguments creates a default graph
     *  Mode - 0 -> empty | 1 -> vertices | 2 -> verticesAndEdges
     *
     * @param mode - mode
     */
    public MapBuilder(int mode){
        // Hard Code Graph Here //
        // Initialize fields
        this();

        if( mode > 0) {
            Vertex v1 = new Vertex("v1");
            Vertex v2 = new Vertex("v2");
            Vertex v3 = new Vertex("v3");
            Vertex v4 = new Vertex("v4");

            // Add sample data to the graph
            g.addVertex( v1 );
            g.addVertex( v2 );
            g.addVertex( v3 );
            g.addVertex( v4 );

            verticesRef.add( "v1" );
            verticesRef.add( "v2" );
            verticesRef.add( "v3" );
            verticesRef.add( "v4" );

            if(mode > 1){
                g.addEdge( v1, v2 );
                g.addEdge( v2, v3 );
                g.addEdge( v3, v1 );
                g.addEdge( v4, v3 );

                edgesRef.add( new Pair<>( "v1", "v2" ) );
                edgesRef.add( new Pair<>( "v2", "v3" ) );
                edgesRef.add( new Pair<>( "v3", "v1" ) );
                edgesRef.add( new Pair<>( "v4", "v3" ) );
            }
        }
    }

    /**
     * Contructor that builds a graph from a file
     * @param f - the file
     */
    public MapBuilder(File f) {

        // Initialize fields
        this();
        BufferedReader fis = null;

        try {
            fis = new BufferedReader(new FileReader(f));
            String graph = fis.readLine();

            String vertices, edges;

            // Get the graph vertices names seperated by strings
            vertices = graph.split("\\[")[1].split("]")[0];

            for(String s: vertices.split(", ")){
                if(!s.isEmpty()) {
                    verticesRef.add(s);
                }
            }

            // Get the graph edges names seperated by strings
            // (v1,v2), (v2,v3), (v3,v1), (v4,v3)
            edges = graph.split("\\[")[2].split("]")[0];

            for(String s: edges.split("\\),")){
                if(!s.isEmpty()) {
                    s = s.replace("(", " ").replace(")", " ").trim();
                    edgesRef.add(new Pair<>(s.split(",")[0], s.split(",")[1]));
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

    /**
     *
     * @return
     */
    public ListenableDirectedGraph<Vertex, DefaultEdge> getGraph() {
        return g;
    }

    /**
     *
     * @return
     */
    public ArrayList<String> getVerticesRef() { return verticesRef; }

    /**
     *
     * @return
     */
    public ArrayList<Pair<String, String>> getEdgesRef() { return edgesRef; }
}
