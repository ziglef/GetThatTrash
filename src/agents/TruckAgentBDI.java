package agents;

import gui.GridCity;
import jadex.bridge.service.types.chat.IChatService;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.rules.rulesystem.rules.functions.IFunction;
import javafx.geometry.Pos;
import main.*;
import map.Vertex;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.alg.DijkstraShortestPath;
import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.*;
import jadex.micro.annotation.*;

import javax.print.attribute.standard.RequestingUserName;
import java.util.*;


@Agent
@Description("Agent that launch a new garbage truck of a specific type.")
@Arguments({
        @Argument(name = "Name", clazz = String.class, defaultvalue = "Anony"),
        @Argument(name = "Capacity", clazz = Integer.class, defaultvalue = "500"),
        @Argument(name = "Type", clazz = GarbageCollector.typeOfWaste.class),
        @Argument(name = "PositionX", clazz = Integer.class),
        @Argument(name = "PositionY", clazz = Integer.class)
})
@ProvidedServices(@ProvidedService(type=IChatService.class, implementation=@Implementation(ChatService.class)))
@RequiredServices({
        @RequiredService(name="chat", type=IChatService.class, multiple=true,
                binding=@Binding(dynamic=true, scope=Binding.SCOPE_PLATFORM))
})
public class TruckAgentBDI {

    /*------------------------------
        Declarations
    *-----------------------------*/
    @Agent
    protected BDIAgent agent;

    @Belief
    String name;

    @Belief
    int capacity;

    @Belief
    GarbageCollector.typeOfWaste type = GarbageCollector.typeOfWaste.UNDIFFERENTIATED;

    @Belief
    Position pos;

    @Belief
    int occupiedCapacity;

    @Belief
    boolean pause = false;

    @Belief
    boolean memory = false;

    @Belief
    boolean mission = false;

    private long SLEEP_LOW = 300;
    private long SLEEP_MEDIUM = 100;
    private long SLEEP_FAST = 25;

    private ArrayList<Position> steps;

    @Belief
    public static final String AGENT_PATH = "out\\production\\AIAD\\agents\\TruckAgentBDI.class";

    @Belief
    private GridCity gc;

    @Belief
    private int remainderCapacity = 0;

    private ArrayList<Position> collectorsInMemory;
    private ArrayList<Position> depositsInMemory;

    @Belief
    private Position lastPos;

    private Map<Integer, Integer> messages;


    /*------------------------------
        Agent
    *-----------------------------*/
    @AgentCreated
    public void init() {
        name = (String) agent.getArgument("Name");
        type = (GarbageCollector.typeOfWaste) agent.getArgument("Type");
        if (type == null)
            type = GarbageCollector.typeOfWaste.UNDIFFERENTIATED;
        pos = new Position(0, 0);
        pos.x = (Integer) agent.getArgument("PositionX");
        pos.y = (Integer) agent.getArgument("PositionY");
        capacity = (Integer) agent.getArgument("Capacity");
        steps = new ArrayList<>();
        occupiedCapacity = 0;
        pause = GarbageCollector.getInstance().getPause();
        GarbageCollector.getInstance().addExternalAccess(agent.getExternalAccess());
        GarbageCollector.getInstance().addTruckAgent(this);
        memory = GarbageCollector.getInstance().memory;
        mission = false;
        gc = GarbageCollector.getInstance().getInterface().getCity();
        collectorsInMemory = new ArrayList<>();
        depositsInMemory = new ArrayList<>();
        lastPos = new Position(-1, -1);
        messages = new HashMap<>();
    }

    @AgentBody
    public void body() {
        agent.dispatchTopLevelGoal(new CheckContainer());
        agent.dispatchTopLevelGoal(new WanderAroundCity());
        agent.dispatchTopLevelGoal(new GoToDeposit());
    }

    @AgentKilled
    public void killed() {
        System.out.println("Killed agent" + agent.getAgentName());
    }

    /*-----------------------------
       Goals
     *---------------------------*/
    @Goal(excludemode = Goal.ExcludeMode.Never, retry = true, orsuccess = false)
    public class WanderAroundCity {


        @GoalResult
        protected int r;

        @GoalRecurCondition(beliefs = "pause")
        public boolean checkContext() {
            return !pause;
        }

    }

    @Goal(excludemode = Goal.ExcludeMode.Never, retry = true, orsuccess = false)
    public class CheckContainer {

        @GoalResult
        protected int r;

        @GoalRecurCondition(beliefs = "pause")
        public boolean checkContext() {
            return !pause;
        }

    }

    @Goal(excludemode = Goal.ExcludeMode.Never, retry = true, orsuccess = false)
    public class GoToDeposit {

        @GoalResult
        protected int r;

        @GoalRecurCondition(beliefs = "pause")
        public boolean checkContext() {
            return !pause;
        }

    }

    /*-----------------------------
     Plans
    *---------------------------*/
    @Plan(trigger = @Trigger(goals = WanderAroundCity.class))
    protected void walkAroundCity() throws InterruptedException {

        do {
            try {
                Thread.sleep(SLEEP_LOW);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (pause = GarbageCollector.getInstance().getPause());

        try {
            switch (GarbageCollector.getInstance().getVelocity()){
                case 1: Thread.sleep(SLEEP_LOW);
                    System.out.println("LOW");break;
                case 2: Thread.sleep(SLEEP_MEDIUM);
                    System.out.println("MEDIUM");break;
                case 3: Thread.sleep(SLEEP_FAST);
                    System.out.println("FAST");break;
                default: break;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        updatePos();
        gc.validate();
        gc.repaint();

    }

    @Plan(trigger = @Trigger(goals = CheckContainer.class))
    protected void checkContainer() {

        do {
            try {
                Thread.sleep(SLEEP_LOW);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (pause = GarbageCollector.getInstance().getPause());

        ArrayList<Container> adjacentsContainers = GarbageCollector.getInstance().checkAdjacentContainerPos(pos);

        if(adjacentsContainers.size() > 0) {
            for (int i = 0; i < adjacentsContainers.size(); i++) {

                if (adjacentsContainers.get(i).getType() == type) {
                    if (!collectorsInMemory.contains(adjacentsContainers.get(i).getPosition()) && GarbageCollector.getInstance().getMemory()) {
                        collectorsInMemory.add(adjacentsContainers.get(i).getPosition());
                        //System.out.println("Adicionei a lista dos collectors que conheco o collector na posicao " + adjacentsCollectors.get(i).getPosition().x + "-" + adjacentsCollectors.get(i).getPosition().y);
                    }

                    if (occupiedCapacity < capacity) {
                        if (occupiedCapacity + adjacentsContainers.get(i).getOccupiedCapacity() > capacity) {
                            remainderCapacity = occupiedCapacity + adjacentsContainers.get(i).getOccupiedCapacity() - capacity;
                            occupiedCapacity = capacity;
                            adjacentsContainers.get(i).setOccupiedCapacity(remainderCapacity);
                            i = adjacentsContainers.size();
                        } else {
                            occupiedCapacity += adjacentsContainers.get(i).getOccupiedCapacity();
                            adjacentsContainers.get(i).setOccupiedCapacity(0);
                        }
                        try {
                            switch (GarbageCollector.getInstance().getVelocity()){
                                case 1: Thread.sleep(SLEEP_LOW);break;
                                case 2: Thread.sleep(SLEEP_MEDIUM);break;
                                case 3: Thread.sleep(SLEEP_FAST);break;
                                default: break;
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }else{
                        if(GarbageCollector.getInstance().getCommunication()){
                            //TODO apanhar sempre, só quando tiver mais de metade ou só quando tiver cheio
                            String message = "GARBAGE " +
                                    GarbageCollector.getInstance().getMsgNr(true) + " " +
                                    adjacentsContainers.get(i).getType() + " " +
                                    adjacentsContainers.get(i).getOccupiedCapacity() + " " +
                                    adjacentsContainers.get(i).getPosition().x + "-" + adjacentsContainers.get(i).getPosition().y;
                            sendMessage(message, true);
                            System.out.println("Mensagem inserida: " + (GarbageCollector.getInstance().getMsgNr(false)));
                            GarbageCollector.getInstance().setClfm(GarbageCollector.getInstance().getMsgNr(false), pos);
                        }
                    }
                }else{
                    if(GarbageCollector.getInstance().getCommunication()){
                        String message = "GARBAGE " +
                                GarbageCollector.getInstance().getMsgNr(true) + " " +
                                adjacentsContainers.get(i).getType() + " " +
                                adjacentsContainers.get(i).getOccupiedCapacity() + " " +
                                adjacentsContainers.get(i).getPosition().x + "-" + adjacentsContainers.get(i).getPosition().y;
                        sendMessage(message, true);
                        System.out.println("Mensagem inserida: " + (GarbageCollector.getInstance().getMsgNr(false)));
                        GarbageCollector.getInstance().setClfm(GarbageCollector.getInstance().getMsgNr(false), pos);
                    }
                }
            }
        }
    }

    @Plan(trigger = @Trigger(goals = GoToDeposit.class))
    protected void GoToDeposit() {

        do {
            try {
                Thread.sleep(SLEEP_LOW);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (GarbageCollector.getInstance().getPause());

        if (occupiedCapacity == capacity && !depositsInMemory.isEmpty() && !mission) {
            this.steps = getShortestPath(depositsInMemory);
            mission = true;
        }

        ArrayList<Deposit> adjacentDeposits = GarbageCollector.getInstance().checkAdjacentDepositPos(pos);
        if (adjacentDeposits.size() > 0) {

            for (int i = 0; i < adjacentDeposits.size(); i++) {

                if (adjacentDeposits.get(i).getType() == type && occupiedCapacity != 0) {
                    adjacentDeposits.get(i).setOccupiedCapacity(adjacentDeposits.get(i).getOccupiedCapacity() + occupiedCapacity);
                    occupiedCapacity = 0;
                    steps.clear();
                    mission = false;
                    try {
                        switch (GarbageCollector.getInstance().getVelocity()){
                            case 1: Thread.sleep(SLEEP_LOW);
                                System.out.println("LOW");break;
                            case 2: Thread.sleep(SLEEP_MEDIUM);
                                System.out.println("MEDIUM");break;
                            case 3: Thread.sleep(SLEEP_FAST);
                                System.out.println("FAST");break;
                            default: break;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (!depositsInMemory.contains(adjacentDeposits.get(i).getPosition()) && GarbageCollector.getInstance().getMemory() && adjacentDeposits.get(i).getType() == type) {
                    depositsInMemory.add(adjacentDeposits.get(i).getPosition());
                    System.out.println(name + ": Adicionei a lista dos depositos que conheco o deposito na posicao " + adjacentDeposits.get(i).getPosition().x + "-" + adjacentDeposits.get(i).getPosition().y);
                }
            }
        }

    }

    /*-----------------------------
       Methods
    *---------------------------*/


    public void setPause(boolean pause) {
        this.pause = pause;
    }

    public void updatePos() throws InterruptedException {

        // have a trip to do
        if (!steps.isEmpty() && steps != null) {
            pos = steps.remove(steps.size() - 1);
        } else {
            mission = false;
            ArrayList<Position> neighbors = autoMove();
            Position aux;
            if (neighbors.size() == 1) {
                lastPos = pos;
                pos = neighbors.get(0);
            } else {
                do {
                    aux = neighbors.get(((int) (Math.random() * 1000) % neighbors.size()));
                } while (aux.equals(lastPos));
                lastPos = pos;
                pos = aux;
            }
        }
    }

    public ArrayList<Position> autoMove() {
        ArrayList<Position> neighbors = new ArrayList<>();
        if (isConnected(pos, new Position(pos.x, pos.y - 1))) neighbors.add(new Position(pos.x, pos.y - 1));
        if (isConnected(pos, new Position(pos.x + 1, pos.y))) neighbors.add(new Position(pos.x + 1, pos.y));
        if (isConnected(pos, new Position(pos.x, pos.y + 1))) neighbors.add(new Position(pos.x, pos.y + 1));
        if (isConnected(pos, new Position(pos.x - 1, pos.y))) neighbors.add(new Position(pos.x - 1, pos.y));

        //if(neighbors.size() == 1)
        return neighbors;
        // else
        // return neighbors.get(((int)(Math.random() * 1000) % neighbors.size()));
    }

    public boolean PositionComparison(Position pos1, Position pos2) {
        return pos1.equals(pos2);
    }

    public boolean isRoad(Position pos) {
        Vertex v = gc.getCtB().getVertexByCoords(pos.x, pos.y);
        if (v != null)
            return v.getName().charAt(0) == 'v';
        else
            return false;
    }

    public boolean isConnected(Position orig, Position dest) {
        Vertex origin = gc.getCtB().getVertexByCoords(orig.x, orig.y);
        Vertex destination = gc.getCtB().getVertexByCoords(dest.x, dest.y);

        return gc.getCtB().getGraph().getEdge(origin, destination) != null;
    }

    public Position getPosition() {
        return pos;
    }


    public GarbageCollector.typeOfWaste getType() {
        return type;
    }

    public ArrayList<Position> getShortestPath(Position dest) {
        ArrayList<Position> path = new ArrayList<>();

        ArrayList<Position> neighbors = new ArrayList<>();

        if (isRoad(new Position(dest.x, dest.y - 1)))
            neighbors.add(new Position(dest.x, dest.y - 1));

        if (isRoad(new Position(dest.x + 1, dest.y)))
            neighbors.add(new Position(dest.x + 1, dest.y));

        if (isRoad(new Position(dest.x, dest.y + 1)))
            neighbors.add(new Position(dest.x, dest.y + 1));

        if (isRoad(new Position(dest.x - 1, dest.y)))
            neighbors.add(new Position(dest.x - 1, dest.y));

        int min = Integer.MAX_VALUE;
        int minIndex = 0;
        int i = 0;
        for (Position p : neighbors) {
            int pSize = DijkstraShortestPath.findPathBetween(gc.getCtB().getGraph(), gc.getCtB().getVertexByCoords(pos.x, pos.y), gc.getCtB().getVertexByCoords(p.x, p.y)).size();
            if (pSize < min) {
                min = pSize;
                minIndex = i;
            }
            i++;
        }

        List<DefaultEdge> stepsEdges = DijkstraShortestPath.findPathBetween(gc.getCtB().getGraph(), gc.getCtB().getVertexByCoords(pos.x, pos.y), gc.getCtB().getVertexByCoords(neighbors.get(minIndex).x, neighbors.get(minIndex).y));
        for (DefaultEdge e : stepsEdges) {
            path.add(gc.getCtB().getGraph().getEdgeTarget(e).getPosition());
        }

        Collections.reverse(path);

        return path;
    }

    public ArrayList<Position> getShortestPath(ArrayList<Position> dest) {

        ArrayList<ArrayList<Position>> paths = new ArrayList<>();
        int min = Integer.MAX_VALUE;
        int minIndex = 0;
        int i = 0;

        for (Position p : dest) {
            paths.add(this.getShortestPath(p));
            if (paths.get(paths.size() - 1).size() < min) {
                min = paths.get(paths.size() - 1).size();
                minIndex = i;
            }
            i++;
        }

        return paths.get(minIndex);
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getOccupiedCapacity() {
        return occupiedCapacity;
    }

    public void getMessage(String name, String message, boolean original) {


        if(occupiedCapacity < capacity && name != this.name){
            System.out.println(this.name+":"+ " Recebi de " + name + " a mensagem: " + message);
            String[] msg = message.split(" ");
            if(original) {
                System.out.println("1");
                if(msg[0].equals("GARBAGE") && msg[2].equals(type.toString())){
                    System.out.println("2");
                    String[] position = msg[4].split("-");
                    Position aux_pos = new Position(Integer.parseInt(position[0]), Integer.parseInt(position[1]));
                    int distance = getShortestPath(aux_pos).size();
                    String message2send = "DIST " + msg[1] + " " + distance;
                    sendMessage(message2send, false);
                    messages.put(Integer.parseInt(msg[1]), distance);
                    WaitABit wt = new WaitABit(this, Integer.parseInt(msg[1]));
                    Thread t = new Thread(wt);
                    t.start();
                }
            }else{
                System.out.println("Recebi uma mensagem que nao era original e que nao era minha");
                if(messages.get(Integer.parseInt(msg[1])) != null){
                    System.out.println("Essa mensagem estava no meu mapa de mensagens");
                    Integer distance = Integer.parseInt(msg[2]);
                    Integer myD = messages.get(Integer.parseInt(msg[1]));
                    System.out.println("Distancia : " + distance +"; Minha distancia: " + myD);
                    if(distance < myD)
                        messages.remove(Integer.parseInt(msg[1]));
                }
            }
        }
    }

    public void sendMessage(final String message, final boolean original){

        IFuture<Collection<IChatService>> cs = agent.getServiceContainer().getRequiredServices("chat");
        cs.addResultListener(new DefaultResultListener<Collection<IChatService>>() {
            @Override
            public void resultAvailable(Collection<IChatService> iChatServices) {
                for(Iterator<IChatService> it=iChatServices.iterator(); it.hasNext(); ) {
                    IChatService chat = it.next();
                    chat.message(name, message, original);
                }
            }
        });
    }

    public class WaitABit implements Runnable{

        private Integer msgNr;
        private TruckAgentBDI truck;

        public WaitABit(TruckAgentBDI truck, Integer msgNr){
            this.msgNr = msgNr;
            this.truck = truck;
        }

        @Override
        public void run() {

            System.out.println("Vou esperar respostas durante 5s");

            do {
                try {
                    Thread.sleep(SLEEP_LOW);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (GarbageCollector.getInstance().getPause());


            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            if(truck.messages.get(msgNr) != null){
                System.out.println("Tenho a mensagem, vou calcular o caminho mais curto para la e vou - " + truck.name);
                Position position = GarbageCollector.getInstance().getClfmGivingNr(msgNr);
                System.out.println(position);
                truck.steps = truck.getShortestPath(position);
                truck.mission = true;
            }

        }
    }

}


