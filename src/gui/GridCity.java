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
import main.Collector;
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
    private ImageIcon truckGlass = new ImageIcon("resources/assets/images/truckglass.png");
    private ImageIcon truckPaper = new ImageIcon("resources/assets/images/truckpaper.png");
    private ImageIcon truckPlastic = new ImageIcon("resources/assets/images/truckplastic.png");
    private ImageIcon containerGlass = new ImageIcon("resources/assets/images/containerglass.png");
    private ImageIcon containerPlastic = new ImageIcon("resources/assets/images/containerplastic.png");
    private ImageIcon containerPaper = new ImageIcon("resources/assets/images/containerpaper.png");
    private ImageIcon containerUndifferentiated = new ImageIcon("resources/assets/images/containerundifferentiated.png");
    private CityMapBuilder ctB;
    private boolean wasInit;
    private InterfaceAgentBDI intAgent;
    private IExternalAccess agent;
    private int object = -1;
    private int type_object = -1;
    private static final int TRUCK = 0;
    private static final int COLLECTOR = 1;
    private static final int DEPOSIT = 2;
    private static final int GLASS = 0;
    private static final int PAPER = 1;
    private static final int PLASTIC = 2;
    private static final int UNDIFFERENTIATED = 3;
    private int anonymousNr = 1;
    public static GridCity city;
    private Collector collector;
    private int fontSize;

    // Mouse listener for the images //
    private MouseListener listener = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            int column = e.getX() / width;
            int row = e.getY() / height;

            for(int i = 0 ; i < Interface.graphInt.getRadioComponent().length; i++){
                if(Interface.graphInt.getRadioComponent()[i].isSelected())
                    object = i;
            }

            for(int i = 0 ; i < Interface.graphInt.getRadioComponentType().length; i++){
                if(Interface.graphInt.getRadioComponentType()[i].isSelected())
                    type_object = i;
            }

            String name = Interface.graphInt.getAgentName().getText();
            if (name.length() == 0) {
                name = "Anony" + anonymousNr;
                anonymousNr++;
            }

            String capacityStr = Interface.graphInt.getAgentCapacity().getText();
            Integer capacity;
            if (capacityStr.length() == 0)
                capacity = 500;
            else
                capacity = Integer.parseInt(capacityStr);

            GarbageCollector.typeOfWaste type = GarbageCollector.typeOfWaste.UNDIFFERENTIATED;
            switch (type_object) {
                case GLASS:
                    type = GarbageCollector.typeOfWaste.GLASS;
                    break;
                case PAPER:
                    type = GarbageCollector.typeOfWaste.PAPER;
                    break;
                case PLASTIC:
                    type = GarbageCollector.typeOfWaste.PLASTIC;
                    break;
                case UNDIFFERENTIATED:
                    type = GarbageCollector.typeOfWaste.UNDIFFERENTIATED;
                    break;
                default:
                    break;
            }

            fontSize = 11;
            if(width < 64 )
                fontSize = 8;
            else if(width >=64 && width <= 99)
                fontSize = 11;
            else if(width >=100 && width <= 129)
                fontSize = 13;
            else if(width >=130 && width <= 169)
                fontSize = 15;
            else if(width >=170 && width <= 219)
                fontSize = 17;

            Vertex v = ctB.getVertexByCoords(row, column);
            if (v != null) { // se houver vertice
                // se for 'v' ou seja estrada

                    if (v.getName().charAt(0) == 'v' && object == TRUCK) {

                        Position pos = new Position(v.getX(), v.getY());

                        Map<String, Object> agentArguments = new HashMap<>();

                        agentArguments.put("Name", name);
                        agentArguments.put("PositionX", pos.x);
                        agentArguments.put("PositionY", pos.y);
                        agentArguments.put("Capacity", capacity);
                        agentArguments.put("Type", type);
                        CreationInfo info = new CreationInfo(agentArguments);

                        ThreadSuspendable sus = new ThreadSuspendable();
                        IServiceProvider sp = agent.getServiceProvider();

                        IComponentManagementService cms = null;
                        try {
                            cms = SServiceProvider.getService(sp, IComponentManagementService.class,
                                    RequiredServiceInfo.SCOPE_PLATFORM).get(sus);
                        } catch (NullPointerException ee) {
                            ee.printStackTrace();
                        }

                        IComponentIdentifier ici = cms.createComponent(TruckAgentBDI.AGENT_PATH, info).getFirstResult(sus);
                        Interface.graphInt.setInfoVisible(false);

                        validate();
                        repaint();

                    }
            } else if(v==null && object == TRUCK){
                Interface.graphInt.setInfoVisible(true);
                Interface.graphInt.setInfo("ERROR: You can' add a truck out of road!");
            }else{ // se nao for vértice
               if(ctB.checkNeighborsFor(row,column,'v')) {
                   if(object == COLLECTOR){
                      if(capacityStr.length() == 0)
                          capacity = 100;
                      collector = new Collector(name,new Position(row,column),type,capacity);
                   }
                   if(object == DEPOSIT){
                       System.out.println("Podes adicionar aqui um deposito");
                   }
                   Interface.graphInt.setInfoVisible(false);
               }else{
                   if(object == COLLECTOR){
                       Interface.graphInt.setInfoVisible(true);
                       Interface.graphInt.setInfo("ERROR: Collector should be add near a road!");
                   }
                   if(object == DEPOSIT){
                       Interface.graphInt.setInfoVisible(true);
                       Interface.graphInt.setInfo("ERROR: Deposit should be add near a road!");
                   }
               }
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
        setVisible(true);
    }

    @Override
    public void paint(Graphics g) {

        super.paintComponent(g);
        g.setFont(new Font("TimesRoman", Font.BOLD, fontSize));
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

        for (int i = 0; i < GarbageCollector.getInstance().getTruckAgents().size(); i++){

            Image img;

            GarbageCollector.typeOfWaste type = GarbageCollector.getInstance().getTruckAgents().get(i).getType();

            if(type == GarbageCollector.typeOfWaste.GLASS)
                img = truckGlass.getImage();

            else if(type == GarbageCollector.typeOfWaste.PAPER)
                img = truckPaper.getImage();

            else if(type == GarbageCollector.typeOfWaste.PLASTIC)
                img = truckPlastic.getImage();

            else
                img = truckUndifferentiated.getImage();


            g.drawImage(img,
                        GarbageCollector.getInstance().getTrucksLoc()[i].y * width,
                        GarbageCollector.getInstance().getTrucksLoc()[i].x * height,
                        width,
                        height,
                        null);


            g.setColor(Color.black);
            g.drawString(GarbageCollector.getInstance().getTruckAgents().get(i).getOccupiedCapacity() +
                    "/"+GarbageCollector.getInstance().getTruckAgents().get(i).getCapacity(),
                   GarbageCollector.getInstance().getTrucksLoc()[i].y * width + width/20*7,
                    GarbageCollector.getInstance().getTrucksLoc()[i].x * height + height/22*7);
            g.drawString(GarbageCollector.getInstance().getTruckAgents().get(i).getName(),
                    GarbageCollector.getInstance().getTrucksLoc()[i].y * width + width/28*7,
                    GarbageCollector.getInstance().getTrucksLoc()[i].x * height + height/8*7);

        }

        for (int i = 0; i < GarbageCollector.getInstance().getCollectors().size(); i++) {

            Image img;
            GarbageCollector.typeOfWaste type = GarbageCollector.getInstance().getCollectors().get(i).getType();


            if(type == GarbageCollector.typeOfWaste.GLASS)
                img = containerGlass.getImage();
            else if(type == GarbageCollector.typeOfWaste.PAPER)
                img = containerPaper.getImage();
            else if(type == GarbageCollector.typeOfWaste.PLASTIC)
                img = containerPlastic.getImage();
            else
                img = containerUndifferentiated.getImage();

            g.drawImage(img,
                    GarbageCollector.getInstance().getCollectorsLoc()[i].y * width,
                    GarbageCollector.getInstance().getCollectorsLoc()[i].x * height,
                    width,
                    height,
                    null);


            g.setColor(Color.black);
            g.drawString(GarbageCollector.getInstance().getCollectors().get(i).getOccupiedCapacity() +
                            "/"+GarbageCollector.getInstance().getCollectors().get(i).getCapacity(),
                    GarbageCollector.getInstance().getCollectorsLoc()[i].y * width + width/20*7,
                    GarbageCollector.getInstance().getCollectorsLoc()[i].x * height + height/22*7);

        }
        //TODO draw contentores
        //TODO draw depositos
    }

    private void cleanCity(Graphics g) {
        g.setColor(new Color(139, 181, 74));
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
    }

}
