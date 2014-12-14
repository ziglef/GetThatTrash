package map;

import javafx.util.Pair;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Class responsible for make a city based on a file
 *
 * @author Rui Grandão  - ei11010@fe.up.pt
 * @author Tiago Coelho - ei11012@fe.up.pt
 */
public class CityMapBuilder extends MapBuilder {

    Map<String, Vertex> vertexMap;

    /**
     * Default constructor to CityMapBuilder
     * @param f - the file to parse
     */
    public CityMapBuilder(File f) {
        super(f);
        vertexMap = new HashMap<>();

        BufferedReader fis = null;

        try {
            fis = new BufferedReader(new FileReader(f));

            if( this.getVerticesRef().size() > 0 ){
                for(String vs : this.getVerticesRef()){
                    Vertex v = new Vertex(vs);
                    vertexMap.put(vs, v);

                    String vertexs = fis.readLine().trim();
                    while( !vertexs.equalsIgnoreCase(vs+":") ){
                        vertexs = fis.readLine().trim();
                    }

                    while( !(vertexs = fis.readLine().trim()).equalsIgnoreCase("end") )
                        v.addProperty(vertexs.split(":")[0], vertexs.split(":")[1]);

                    this.getGraph().addVertex(v);
                }
            }

            if( this.getEdgesRef().size() > 0 ){
                for( Pair<String, String> es : this.getEdgesRef() ){
                    this.getGraph().addEdge(vertexMap.get(es.getKey()), vertexMap.get(es.getValue()));
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
     * Method that returns a Vertex given an position
     * @param x - pos x
     * @param y - pos y
     * @return - Vertex if exists, null otherwise
     */
    public Vertex getVertexByCoords(int x, int y){
        for( Vertex v : vertexMap.values() ){
            if( v.isAt(x,y) ) {
                return v;
            }
        }
        return null;
    }

    /**
     * Method that return a Vertex present on a VertexMap, given his key
     *
     * @param key - the key
     * @return - the Vertex
     */
    public Vertex getVertexByName(String key) {
        return this.vertexMap.get(key);
    }

    /**
     *
     *
     * @return
     */
    public Collection<Vertex> getVertices() {
        return this.vertexMap.values();
    }

    /**
     * Method that check neighbors for a given position
     * @param x - x pos
     * @param y - y pos
     * @param c - char identifier of the vertex
     * @return - true if has neighbors, false otherwise
     */
    public boolean checkNeighborsFor(int x, int y, char c){
        // left
        if( getVertexByCoords(x-1, y) != null )
            return getVertexByCoords(x-1, y).getName().charAt(0) == c;

        // right
        if( getVertexByCoords(x+1, y) != null )
            return getVertexByCoords(x+1, y).getName().charAt(0) == c;

        // top
        if( getVertexByCoords(x, y-1) != null )
            return getVertexByCoords(x, y-1).getName().charAt(0) == c;

        // bottom
        if( getVertexByCoords(x, y+1) != null )
            return getVertexByCoords(x, y+1).getName().charAt(0) == c;

        return false;
    }
}
