package map;

import javafx.util.Pair;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class CityMapBuilder extends MapBuilder {

    public CityMapBuilder(File f) {
        super(f);

        BufferedReader fis = null;

        try {
            Map<String, Vertex> vertexMap = new HashMap<>();
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
}
