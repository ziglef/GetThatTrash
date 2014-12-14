package agents;

import gui.GridCity;
import jadex.bridge.service.types.chat.IChatService;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import main.*;
import map.Vertex;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.alg.DijkstraShortestPath;
import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.*;
import jadex.micro.annotation.*;
import java.util.*;


/**
 * Class responsible for creating a truck Agent
 *
 * @author Rui Grandão  - ei11010@fe.up.pt
 * @author Tiago Coelho - ei11012@fe.up.pt
 *
 */
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

    @Belief
    public static final String AGENT_PATH = "out\\production\\AIAD\\agents\\TruckAgentBDI.class";

    @Belief
    private GridCity gc;

    @Belief
    private int remainderCapacity = 0;

    @Belief
    private Position lastPos;

    private ArrayList<Position> collectorsInMemory;
    private ArrayList<Position> depositsInMemory;
    private ArrayList<Position> steps;
    private long SLEEP_LOW = 300;
    private Map<Integer, Integer> messages;


    /*------------------------------
        Agent
    *-----------------------------*/
    /**
     * Method responsible to initializing the truck Agent components
     */
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

    /**
     * Method invoked immediately after truck Agent creation.
     * Start agent's goals.
     */
    @AgentBody
    public void body() {
        agent.dispatchTopLevelGoal(new CheckContainer());
        agent.dispatchTopLevelGoal(new WanderAroundCity());
        agent.dispatchTopLevelGoal(new GoToDeposit());
    }

    /**
     * Method invoked when a truck agent is killed
     */
    @AgentKilled
    public void killed() {
        System.out.println("Killed agent" + agent.getAgentName());
    }


    /*-----------------------------
       Goals
     *---------------------------*/
    /**
     * Goal of the truck to wander around the city
     */
    @Goal(excludemode = Goal.ExcludeMode.Never, retry = true, orsuccess = false)
    public class WanderAroundCity {

        @GoalResult
        protected int r;

        @GoalRecurCondition(beliefs = "pause")
        public boolean checkContext() {
            return !pause;
        }

    }

    /**
     * Goal of the truck to check a container
     */
    @Goal(excludemode = Goal.ExcludeMode.Never, retry = true, orsuccess = false)
    public class CheckContainer {

        @GoalResult
        protected int r;

        @GoalRecurCondition(beliefs = "pause")
        public boolean checkContext() {
            return !pause;
        }

    }

    /**
     * Goal of the truck to go for a deposit
     */
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
    /**
     * Truck plan to wander around the city
     * If city is on pause, plan just wait,
     * else plan call updatePos() method and repaint
     * the grid City
     *
     * @throws InterruptedException - If Thread.sleep launch an Excepection
     */
    @Plan(trigger = @Trigger(goals = WanderAroundCity.class))
    protected void walkAroundCity() throws InterruptedException {

        do {
            Thread.sleep(SLEEP_LOW);
        }while(pause = GarbageCollector.getInstance().getPause());

        Thread.sleep(SLEEP_LOW / GarbageCollector.getInstance().getVelocity());

        updatePos();
        gc.validate();
        gc.repaint();
    }

    /**
     * Truck plan to check a container.
     * If the truck is adjacent to a container, he checks the type of the container.
     * If the container have the same type, the tuck picks up the garbage;
     * If the container has more garbage than the available capacity on the truck,
     * the truck picks up the garbage until fill and leave the rest of the garbage on
     * the container.
     * If memory is being used and the truck doesn't know this container, he stores
     * the container in memory
     * If communication is being used and the container is not of the same type or if
     * the truck is full, the truck sends a message to all the other trucks with the
     * information about that container.
     *
     * @throws InterruptedException - If Thread.sleep launch an Excepection
     */
    @Plan(trigger = @Trigger(goals = CheckContainer.class))
    protected void checkContainer() throws InterruptedException{

        do {
            Thread.sleep(SLEEP_LOW);
        } while (pause = GarbageCollector.getInstance().getPause());

        ArrayList<Container> adjacentsContainers = GarbageCollector.getInstance().checkAdjacentContainerPos(pos);

        if(adjacentsContainers.size() > 0) {
            for (int i = 0; i < adjacentsContainers.size(); i++) {

                if (adjacentsContainers.get(i).getType() == type) {
                    if (!collectorsInMemory.contains(adjacentsContainers.get(i).getPosition()) && GarbageCollector.getInstance().getMemory()) {
                        collectorsInMemory.add(adjacentsContainers.get(i).getPosition());
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

                        Thread.sleep(SLEEP_LOW / GarbageCollector.getInstance().getVelocity());

                    }else{
                        prepareMessageAndSend(adjacentsContainers, i);
                    }
                }else{
                    prepareMessageAndSend(adjacentsContainers, i);
                }
            }
        }
    }

    /**
     * Truck plan to go a deposit
     * If the truck is adjacent to a deposit, he checks the type of the container.
     * If the deposit have the same type, the tuck drops the garbage;
     * If memory is being used and the truck doesn't know this deposit, he stores
     * the deposit in memory
     *
     * If truck is full and memory is being used and truck knows deposits
     * he calculates the shortest path for deposits that he knows and go for the shorter
     *
     * @throws InterruptedException - If Thread.sleep launch an Excepection
     */
    @Plan(trigger = @Trigger(goals = GoToDeposit.class))
    protected void GoToDeposit() throws InterruptedException{

        do {
            Thread.sleep(SLEEP_LOW);
        }while(GarbageCollector.getInstance().getPause());

        if (occupiedCapacity == capacity && !depositsInMemory.isEmpty() && !mission) {
            this.steps = getShortestPath(depositsInMemory);
            mission = true;
        }

        ArrayList<Deposit> adjacentDeposits = GarbageCollector.getInstance().checkAdjacentDepositPos(pos);
        if (adjacentDeposits.size() > 0) {

            for (int i = 0; i < adjacentDeposits.size(); i++) {

                if (adjacentDeposits.get(i).getType() == type && occupiedCapacity != 0) {
                    adjacentDeposits.get(i).setOccupiedCapacity(adjacentDeposits.get(i).getOccupiedCapacity() + occupiedCapacity);
                    GarbageCollector.getInstance().setWasteQuantity(type, occupiedCapacity);
                    occupiedCapacity = 0;
                    steps.clear();
                    mission = false;
                    Thread.sleep(SLEEP_LOW / GarbageCollector.getInstance().getVelocity());
                }

                if (!depositsInMemory.contains(adjacentDeposits.get(i).getPosition()) && GarbageCollector.getInstance().getMemory() && adjacentDeposits.get(i).getType() == type) {
                    depositsInMemory.add(adjacentDeposits.get(i).getPosition());
                }
            }
        }

    }

    /*-----------------------------
       Methods
    *---------------------------*/
    /**
     * Method that is responsible for create a message and send to all trucks
     *
     * @param adjacentsContainers - information about the cointainers
     * @param i - index of container on adjacentsContainers
     */
    private void prepareMessageAndSend(ArrayList<Container> adjacentsContainers, int i) {
        if(GarbageCollector.getInstance().getCommunication()){
            String message = "GARBAGE " +
                    GarbageCollector.getInstance().getMsgNr(true) + " " +
                    adjacentsContainers.get(i).getType() + " " +
                    adjacentsContainers.get(i).getOccupiedCapacity() + " " +
                    adjacentsContainers.get(i).getPosition().x + "-" + adjacentsContainers.get(i).getPosition().y;
            sendMessage(message, true);
            GarbageCollector.getInstance().setClfm(GarbageCollector.getInstance().getMsgNr(false), pos);
        }
    }

    /**
     * Method that is responsible for updating the position of trucks.
     * If the truck has a mission he gives update to the next position based
     * on a list of steps, otherwise the truck moves randomly without repeating
     * the previous position, except in extreme case (if is the only possibility)
     *
     * @throws InterruptedException - If Thread.sleep launch an Excepection
     */
    public void updatePos() throws InterruptedException {

        // have a trip to do
        if (!steps.isEmpty() && steps != null) {
            pos = steps.remove(steps.size() - 1);
        } else {
            mission = false;
            ArrayList<Position> neighbors = autoMove();
            Position aux;
            // repeat last position if is the only possibility
            if (neighbors.size() == 1) {
                lastPos = pos;
                pos = neighbors.get(0);
            } else {

                // choose a position without repeat the previous
                do {
                    aux = neighbors.get(((int) (Math.random() * 1000) % neighbors.size()));
                } while (aux.equals(lastPos));
                lastPos = pos;
                pos = aux;
            }
        }
    }

    /**
     * Method that returns a list of all the positions that are connected
     *
     * @return - the list with of Positions
     */
    public ArrayList<Position> autoMove() {
        ArrayList<Position> neighbors = new ArrayList<>();
        if (isConnected(pos, new Position(pos.x, pos.y - 1))) neighbors.add(new Position(pos.x, pos.y - 1));
        if (isConnected(pos, new Position(pos.x + 1, pos.y))) neighbors.add(new Position(pos.x + 1, pos.y));
        if (isConnected(pos, new Position(pos.x, pos.y + 1))) neighbors.add(new Position(pos.x, pos.y + 1));
        if (isConnected(pos, new Position(pos.x - 1, pos.y))) neighbors.add(new Position(pos.x - 1, pos.y));

        return neighbors;
    }

    /**
     * Method that checks if a given position is a road
     *
     * @param pos - the position to check
     * @return - true if is road, false otherwise
     */
    public boolean isRoad(Position pos) {
        Vertex v = gc.getCtB().getVertexByCoords(pos.x, pos.y);
        if (v != null)
            return v.getName().charAt(0) == 'v';
        else
            return false;
    }

    /**
     * Method that checks if 2 positions are connected, i.e.,
     * if both positions have an Edge between on the graph
     *
     * @param orig - original Position
     * @param dest - destination
     * @return - true if they are connected, false otherwise
     */
    public boolean isConnected(Position orig, Position dest) {
        Vertex origin = gc.getCtB().getVertexByCoords(orig.x, orig.y);
        Vertex destination = gc.getCtB().getVertexByCoords(dest.x, dest.y);

        return gc.getCtB().getGraph().getEdge(origin, destination) != null;
    }

    /**
     * Method that calculates the ShortestPath for a position,
     * from the actual position of the truck.
     *
     * @param dest - destination Position
     * @return - ArrayList of Positions with all the steps to go for dest
     */
    public ArrayList<Position> getShortestPath(Position dest) {
        ArrayList<Position> path = new ArrayList<>();

        ArrayList<Position> neighbors = new ArrayList<>();

        if (isRoad(new Position(dest.x, dest.y - 1))) neighbors.add(new Position(dest.x, dest.y - 1));
        if (isRoad(new Position(dest.x + 1, dest.y))) neighbors.add(new Position(dest.x + 1, dest.y));
        if (isRoad(new Position(dest.x, dest.y + 1))) neighbors.add(new Position(dest.x, dest.y + 1));
        if (isRoad(new Position(dest.x - 1, dest.y))) neighbors.add(new Position(dest.x - 1, dest.y));

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

    /**
     * Method that calculates the ShortestPath for a list of positions
     * from the actual position of the truck.
     *
     * @param dest - destination Positions
     * @return - ArrayList of Positions with all the steps to go for dest
     * for the shortest destination present on dest
     */
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

    /**
     * Method responsible for receiving a message when communication
     * is being used.
     *
     * @param name - the truck that sends the message
     * @param message - the message
     * @param original - true if is the original message, false otherwise
     */
    public void getMessage(String name, String message, boolean original) {

        if(occupiedCapacity < capacity && name != this.name){
            String[] msg = message.split(" ");
            if(original) { //if is the original message, parse it and answer
                if(msg[0].equals("GARBAGE") && msg[2].equals(type.toString())){
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
            }else{ // otherwise, check if anyone is close to the destination
                if(messages.get(Integer.parseInt(msg[1])) != null){
                    Integer distance = Integer.parseInt(msg[2]);
                    Integer myD = messages.get(Integer.parseInt(msg[1]));
                    if(distance < myD)
                        messages.remove(Integer.parseInt(msg[1]));
                }
            }
        }
    }

    /**
     * Method responsible for receiving a message when communication
     * is being used. Send the message via ACS of jadex (IChatService)
     *
     * @param message - message to be send
     * @param original - true if is the original message, false otherwis
     */
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

    /**
     * Method which has a thread waiting 2,5seconds for answers to see if other truck
     * is closer to a container. If any truck is close to that container, the own truck
     * goes to that container in a mission.
     *
     */
    public class WaitABit implements Runnable{

        private Integer msgNr;
        private TruckAgentBDI truck;

        public WaitABit(TruckAgentBDI truck, Integer msgNr){
            this.msgNr = msgNr;
            this.truck = truck;
        }

        @Override
        public void run() {

            do {
                try {
                    Thread.sleep(SLEEP_LOW);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (GarbageCollector.getInstance().getPause());


            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            if(truck.messages.get(msgNr) != null){
                Position position = GarbageCollector.getInstance().getClfmGivingNr(msgNr);
                truck.steps = truck.getShortestPath(position);
                truck.mission = true;
            }

        }
    }


    /*-----------------------------
       Getters and Setters
    *---------------------------*/
    /**
     * Method that returns the truck name identifier
     *
     * @return - name of the truck
     */
    public String getName() {
        return name;
    }

    /**
     * Method that returns the capacity of the truck
     *
     * @return - capacity of the truck
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Method that returns the actual occupied capacity of the truck
     *
     * @return - occupied capacity of the truck
     */
    public int getOccupiedCapacity() {
        return occupiedCapacity;
    }

    /**
     * Method that returns the position of the truck
     *
     * @return - the position of the truck
     */
    public Position getPosition() {
        return pos;
    }

    /**
     * Method that returns the type of the truck
     *
     * @return - the type of the truck
     */
    public GarbageCollector.typeOfWaste getType() {
        return type;
    }

}


