package graphs;

import junit.framework.Assert;
import map.CityMapBuilder;
import map.MapBuilder;
import org.junit.Test;

import java.io.File;

// Class used for testing the reading of graphs from files
public class GraphFromFile {

    MapBuilder defaultEmptyMB = new MapBuilder(0);
    MapBuilder defaultVerticesMB = new MapBuilder(1);
    MapBuilder defaultVerticesAndEdgesMB = new MapBuilder(2);

    @Test
    public void readEmptyGraphRefs(){
        MapBuilder fromFileMB = new CityMapBuilder(new File("resources/graphs/empty"));

        Assert.assertEquals(defaultEmptyMB.getGraph().toString(), fromFileMB.getGraph().toString());
        Assert.assertEquals(defaultVerticesAndEdgesMB.getVerticesRef().toString(), fromFileMB.getVerticesRef().toString());
        Assert.assertEquals(defaultVerticesAndEdgesMB.getEdgesRef().toString(), fromFileMB.getEdgesRef().toString());
    }

    @Test
    public void readVerticesGraphRefs(){
        MapBuilder fromFileMB = new CityMapBuilder(new File("resources/graphs/vertices"));

        Assert.assertEquals(defaultVerticesAndEdgesMB.getVerticesRef().toString(), fromFileMB.getVerticesRef().toString());
    }

    @Test
    public void readVerticesAndEdgesGraphRefs(){
        MapBuilder fromFileMB = new CityMapBuilder(new File("resources/graphs/verticesAndEdges"));

        Assert.assertEquals(defaultVerticesAndEdgesMB.getVerticesRef().toString(), fromFileMB.getVerticesRef().toString());
        Assert.assertEquals(defaultVerticesAndEdgesMB.getEdgesRef().toString(), fromFileMB.getEdgesRef().toString());
    }

    @Test
    public void readEmptyGraph(){
        MapBuilder fromFileMB = new CityMapBuilder(new File("resources/graphs/empty"));

        Assert.assertEquals(defaultEmptyMB.getGraph().toString(), fromFileMB.getGraph().toString());
    }

    @Test
    public void readVerticesGraph(){
        MapBuilder fromFileMB = new CityMapBuilder(new File("resources/graphs/vertices"));

        Assert.assertEquals(defaultVerticesMB.getGraph().toString(), fromFileMB.getGraph().toString());
    }

    @Test
    public void readVerticesAndEdgesGraph(){
        MapBuilder fromFileMB = new CityMapBuilder(new File("resources/graphs/verticesAndEdges"));

        Assert.assertEquals(defaultVerticesAndEdgesMB.getGraph().toString(), fromFileMB.getGraph().toString());
    }
}
