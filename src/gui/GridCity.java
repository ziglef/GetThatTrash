package gui;

import javax.swing.*;

import agents.InterfaceAgentBDI;
import agents.TruckAgentBDI;
import jadex.bdiv3.BDIAgent;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.ThreadSuspendable;
import jadex.micro.annotation.AgentArgument;
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
    private ImageIcon truckUndifferentiated = new ImageIcon("resources/assets/images/truckundifferentiated.png");
    private CityMapBuilder ctB;
    private boolean wasInit;
    private InterfaceAgentBDI intAgent;

    private IExternalAccess agent;

    // Mouse listener for the images //
    private MouseListener listener = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            int column = e.getX() / width;
            int row = e.getY() / height;


            Vertex v = ctB.getVertexByCoords(row, column);
            if (v != null) { // se houver vertice
                // se for 'v' ou seja estrada
                if (v.getName().charAt(0) == 'v') {

                    String name = Interface.graphInt.getAgentName().getText();
                    if(name.length() == 0)
                        name = "Anonymous";

                    String capacityStr = Interface.graphInt.getAgentCapacity().getText();
                    Integer capacity;
                    if(capacityStr.length() == 0)
                        capacity = 500;
                    else
                        capacity = Integer.parseInt(capacityStr);

                    Position pos = new Position(v.getX(), v.getY());

                    TruckAgentBDI.typeOfWaste type =  TruckAgentBDI.typeOfWaste.UNDIFFERENTIATED;
                    Map<String, Object> agentArguments = new HashMap<>();

                    System.out.println("------------------------");
                    System.out.println("| >>> CLICK TO TRUCK!  |");
                    System.out.println("------------------------");
                    System.out.println("Name:" + name);
                    System.out.println("Capacity:" + capacity);
                    System.out.println("Position:" + pos.x +"-"+pos.y);
                    System.out.println("Type:" + type);

                    agentArguments.put("Name", name);
                    agentArguments.put("PositionX", pos.x);
                    agentArguments.put("PositionY", pos.y);
                    agentArguments.put("Capacity", capacity);
                    agentArguments.put("Type", type);
                    CreationInfo info = new CreationInfo(agentArguments);

                    ThreadSuspendable sus = new ThreadSuspendable();
                    IServiceProvider sp = agent.getServiceProvider();

                    IComponentManagementService cms = null;
                    try{
                        cms = SServiceProvider.getService(sp, IComponentManagementService.class,
                                RequiredServiceInfo.SCOPE_PLATFORM).get(sus);
                    }catch (NullPointerException ee){
                        ee.printStackTrace();
                    }

                    IComponentIdentifier ici = cms.createComponent(TruckAgentBDI.AGENT_PATH, info).getFirstResult(sus);
                    System.out.println("started: " + ici);


                    validate();
                    repaint();

                   /* if (info == null) System.out.println("INFO IS NULL");

                    if (GarbageCollector.getInstance() == null)
                        System.out.println("INSTANCE IS NULL");

                    System.out.println("Working Directory = " +
                            System.getProperty("user.dir"));

                    try {
                        GarbageCollector.getInstance().launchAgent(TruckAgentBDI.AGENT_PATH,info);
                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    }*/


                }else {
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

    public GridCity(String filePath, IExternalAccess agent) {
        this.agent = agent;
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
        for (int i = 0; i < GarbageCollector.getInstance().getTruckAgents().size(); i++){
            g.drawImage(truckUndifferentiated.getImage(),
                        GarbageCollector.getInstance().getTrucksLoc()[i].y * width,
                        GarbageCollector.getInstance().getTrucksLoc()[i].x * height,
                        width,height,null);
        }



        //TODO draw contentores
        //TODO draw depositos

    }

    private void cleanCity(Graphics g) {
        g.setColor(new Color(139, 181, 74));
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
    }
}
