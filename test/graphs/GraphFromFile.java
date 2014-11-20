package graphs;

import junit.framework.Assert;
import map.CityMapBuilder;
import map.MapBuilder;
import map.Vertex;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.ListenableDirectedGraph;
import org.junit.Test;

import java.io.File;

// Class used for testing the reading of graphs from files
public class GraphFromFile {

    MapBuilder defaultEmptyMB = new MapBuilder(0);
    MapBuilder defaultVerticesMB = new MapBuilder(1);
    MapBuilder defaultVerticesAndEdgesMB = new MapBuilder(2);

    String getGraphString( ListenableDirectedGraph<Vertex, DefaultEdge> g ){
        String s = new String("([");

        for( Vertex v : g.vertexSet() ){
            s += v.getName();
            s += ", ";
        }

        s = s.substring(0, s.length()-2);
        s += "], [";

        for( DefaultEdge e : g.edgeSet() ){
            s += g.getEdgeSource(e).getName();
            s += "->";
            s += g.getEdgeTarget(e).getName();
            s += ", ";
        }

        s = s.substring(0, s.length()-2);
        s += "])";

        return s;
    }

    @Test
    public void readEmptyGraphRefs(){
        MapBuilder fromFileMB = new CityMapBuilder(new File("resources/graphs/empty"));

        // Assert.assertEquals(getGraphString(defaultEmptyMB.getGraph()), getGraphString(fromFileMB.getGraph()));
        Assert.assertEquals(defaultEmptyMB.getVerticesRef().toString(), fromFileMB.getVerticesRef().toString());
        Assert.assertEquals(defaultEmptyMB.getEdgesRef().toString(), fromFileMB.getEdgesRef().toString());
    }

    @Test
    public void readVerticesGraphRefs(){
        MapBuilder fromFileMB = new CityMapBuilder(new File("resources/graphs/vertices"));

        // Assert.assertEquals(getGraphString(defaultVerticesMB.getGraph()), getGraphString(fromFileMB.getGraph()));
        Assert.assertEquals(defaultVerticesMB.getVerticesRef().toString(), fromFileMB.getVerticesRef().toString());
        Assert.assertEquals(defaultVerticesMB.getEdgesRef().toString(), fromFileMB.getEdgesRef().toString());
    }

    @Test
    public void readVerticesAndEdgesGraphRefs(){
        MapBuilder fromFileMB = new CityMapBuilder(new File("resources/graphs/verticesAndEdges"));

        // Assert.assertEquals(getGraphString(defaultVerticesAndEdgesMB.getGraph()), getGraphString(fromFileMB.getGraph()));
        Assert.assertEquals(defaultVerticesAndEdgesMB.getVerticesRef().toString(), fromFileMB.getVerticesRef().toString());
        Assert.assertEquals(defaultVerticesAndEdgesMB.getEdgesRef().toString(), fromFileMB.getEdgesRef().toString());
    }

    @Test
    public void readEmptyGraph(){
        MapBuilder fromFileMB = new CityMapBuilder(new File("resources/graphs/empty"));

        Assert.assertEquals(getGraphString(defaultEmptyMB.getGraph()), getGraphString(fromFileMB.getGraph()));
    }

    @Test
    public void readVerticesGraph(){
        MapBuilder fromFileMB = new CityMapBuilder(new File("resources/graphs/vertices"));

        Assert.assertEquals(getGraphString(defaultVerticesMB.getGraph()), getGraphString(fromFileMB.getGraph()));
    }

    @Test
    public void readVerticesAndEdgesGraph(){
        MapBuilder fromFileMB = new CityMapBuilder(new File("resources/graphs/verticesAndEdges"));

        Assert.assertEquals(getGraphString(defaultVerticesAndEdgesMB.getGraph()), getGraphString(fromFileMB.getGraph()));
    }
}
