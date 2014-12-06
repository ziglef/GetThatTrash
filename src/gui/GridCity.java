package gui;

import javax.swing.*;

import agents.InterfaceAgentBDI;
import agents.TruckAgentBDI;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.ThreadSuspendable;
import main.GarbageCollector;
import main.Position;
import map.CityMapBuilder;
import map.Vertex;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class GridCity extends JPanel {

    private int gridSize = 10;
    private int width, height;
    private final String defaultTerrain = "terrainTile3.png";
    private ImageIcon imgIcon;
    private CityMapBuilder ctB;
    private boolean wasInit;

    // Mouse listener for the images //
    private MouseListener listener = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            int column = e.getX() / width;
            int row = e.getY() / height;

            /*
                Vertex v1 = new Vertex("esp1");
                v1.addProperty("coords", "0,1");
                v1.addProperty("img", "asda");
                v1.addProperty("type", "plastic");
                ctB.getGraph().addVertex(v1);
            */

            Vertex v = ctB.getVertexByCoords(row, column);
            if (v != null) { // se houver vertice
                // se for 'v' ou seja estrada
                if (v.getName().charAt(0) == 'v') {

                    System.out.println("Carreguei numa estrada");

                    String name = new String("Truck");
                    Position pos = new Position(v.getX(), v.getY());
                    TruckAgentBDI.typeOfWaste type = TruckAgentBDI.typeOfWaste.UNDIFFERENTIATED;
                    Map<String, Object> agentArguments = new HashMap<>();
                    agentArguments.put("Position", pos);
                    agentArguments.put("Capacity", 100);
                    agentArguments.put("Name", name);
                    agentArguments.put("Type", type);
                    CreationInfo info = new CreationInfo(agentArguments);
                    if (info == null) System.out.println("INFO IS NULL");

                    if (GarbageCollector.getInstance() == null)
                        System.out.println("INSTANCE IS NULL");

                    System.out.println("Working Directory = " +
                            System.getProperty("user.dir"));


                    ThreadSuspendable sus = new ThreadSuspendable();

                    IComponentManagementService cms = SServiceProvider.getService(InterfaceAgentBDI.isp, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).get(sus);

                    IComponentIdentifier ici = cms.createComponent(TruckAgentBDI.AGENT_PATH, info).getFirstResult(sus);
                    System.out.println("started: " + ici);

                } else {
                    System.out.println("Não é estrada");
                }
                //System.out.println(v.toString());
            } else { // se nao houver
                System.out.println("X: " + column + "\nY: " + row + "\n");
            }

        }
    };

    public CityMapBuilder getCtB() {
        return ctB;
    }

    public GridCity(String filePath) {
        ctB = new CityMapBuilder(new File(filePath));
        addMouseListener(listener);

        setCursor (Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.wasInit = false;
    }

    private void initThis() {
        int thisWidth = getWidth();
        int thisHeight = getHeight();

        System.out.println("w: " + getWidth());
        System.out.println("h: " + getHeight());

        int stupidBorderHeight = thisHeight - ((thisHeight / this.gridSize) * this.gridSize);
        int stupidBorderWidth = thisWidth - ((thisWidth / this.gridSize) * this.gridSize);

        thisWidth -= stupidBorderWidth;
        thisHeight -= stupidBorderHeight;

        this.width = thisWidth / this.gridSize;
        this.height = thisHeight / this.gridSize;

        this.wasInit = true;
    }

    @Override
    public void paint(Graphics g) {

        if (!wasInit)
            initThis();
        else {
            int thisWidth = getWidth();
            int thisHeight = getHeight();

            int stupidBorderHeight = thisHeight - ((thisHeight / this.gridSize) * this.gridSize);
            int stupidBorderWidth = thisWidth - ((thisWidth / this.gridSize) * this.gridSize);

            thisWidth -= stupidBorderWidth;
            thisHeight -= stupidBorderHeight;

            this.width = thisWidth / this.gridSize;
            this.height = thisHeight / this.gridSize;

        }

        cleanCity(g);

        int currV = 1;

       /* System.out.println("VertexSet: \n");
        for( Vertex v : ctB.getVertices() ){
            System.out.println( v.toString() );
        }*/

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                String imgTerrain = defaultTerrain;

                if (currV <= ctB.getVertices().size()) {
                    if (ctB.getVertexByName("v" + currV).getX() == i && ctB.getVertexByName("v" + currV).getY() == j) {
                        imgTerrain = ctB.getVertexByName("v" + currV).getProperty("img");
                        currV++;
                    }
                }

                imgIcon = new ImageIcon("resources/assets/images/" + imgTerrain);
                g.drawImage(imgIcon.getImage(), j * width, i * height, width, height, null);
            }
        }

        //TODO draw camioes

        //TODO draw contentores
        //TODO draw depositos

    }

    private void cleanCity(Graphics g) {
        g.setColor(new Color(139, 181, 74));
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
    }
}
