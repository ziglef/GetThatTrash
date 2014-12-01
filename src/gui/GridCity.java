package gui;

import javax.swing.*;
import map.CityMapBuilder;
import map.Vertex;

import java.awt.*;
import java.io.File;
/**
 * Created by Tiago on 01/12/2014.
 */
public class GridCity extends JPanel {

    private int gridSize = 10;
    private int width, height;
    private String defaultTerrain = "terrainTile3.png";
    private ImageIcon imgIcon;
    private CityMapBuilder ctB;

    public GridCity(String filePath){
     ctB = new CityMapBuilder( new File(filePath) );
}

    @Override
    public void paint(Graphics g) {

        cleanCity(g);

        width = this.getWidth() / gridSize;
        height = this.getHeight() / gridSize;


        int currV = 1;

        System.out.println("VertexSet: \n");
        for( Vertex v : ctB.getVertices() ){
            System.out.println( v.toString() );
        }

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                String buttonTerrain = defaultTerrain;

                if( currV <= ctB.getVertices().size() ) {
                    if (ctB.getVertexByName("v" + currV).getX() == i && ctB.getVertexByName("v" + currV).getY() == j) {
                        buttonTerrain = ctB.getVertexByName("v" + currV).getProperty("img");
                        currV++;
                    }
                }
                imgIcon = new ImageIcon("resources/assets/images/" + buttonTerrain);
                g.drawImage(imgIcon.getImage(), j*width, i*height, width,height,null);
            }
        }

        //TODO draw camioes
        //TODO draw contentores
        //TODO draw depositos

    }

    private void cleanCity(Graphics g) {
        g.setColor(new Color(139,181,74));
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
    }
}
