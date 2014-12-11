package agents;

import gui.GridCity;
import map.Vertex;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.alg.DijkstraShortestPath;
import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.*;
import jadex.micro.annotation.*;
import main.GarbageCollector;
import main.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


@Agent
@Description("Agent that launch a new garbage truck of a specific type.")
@Arguments({
        @Argument(name = "Name", clazz = String.class, defaultvalue = "AnonymousTruck"),
        @Argument(name = "Capacity", clazz = Integer.class, defaultvalue = "500"),
        @Argument(name = "Type", clazz = TruckAgentBDI.typeOfWaste.class),
        @Argument(name = "PositionX", clazz = Integer.class),
        @Argument(name = "PositionY", clazz = Integer.class)
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
    typeOfWaste type = typeOfWaste.UNDIFFERENTIATED;

    @Belief
    Position pos;

    @Belief
    int occupiedCapacity;

    @Belief
    boolean pause = false;

    @Belief
    boolean memory = false;

    @Belief
    boolean full = false;

    @Belief
    boolean mission = false;

    @Belief
    boolean communication = false;

    @Belief
    public static final long SLEEP = 500;

    private List<Position> steps;
    public static final String AGENT_PATH = "out\\production\\AIAD\\agents\\TruckAgentBDI.class";
    private GridCity gc;


    public static enum typeOfWaste {
        PAPER, PLASTIC, GLASS, UNDIFFERENTIATED
    }

    /*------------------------------
        Agent
    *-----------------------------*/
    @AgentCreated
    public void init() {
        name = (String) agent.getArgument("Name");
        type = (typeOfWaste) agent.getArgument("Type");
        if(type==null)
            type=typeOfWaste.UNDIFFERENTIATED;
        pos = new Position(0,0);
        pos.x = (Integer) agent.getArgument("PositionX");
        pos.y = (Integer) agent.getArgument("PositionY");
        /*if(pos == null)
            pos = uma posicao que seja estrada; mas nunca será null, só para caso haja algum erro*/
        capacity = (Integer) agent.getArgument("Capacity");
        steps = new ArrayList<>();
        occupiedCapacity = 0;
        pause = GarbageCollector.getInstance().getPause();
        GarbageCollector.getInstance().addTruckAgent(this);
        memory = GarbageCollector.getInstance().memory;
        mission = false;

        System.out.println("------------------------");
        System.out.println("| >>> CREATE A TRUCK!  |");
        System.out.println("------------------------");
        System.out.println("Name:" + name);
        System.out.println("Capacity:" + capacity);
        System.out.println("Position:" + pos.x +"-"+pos.y);
        System.out.println("Type:" + type);
    }

    @AgentBody
    public void body() {
        // agent.dispatchTopLevelGoal(new CheckContainer());
        System.out.println("------------------------");
        System.out.println("| >>> BODY OF A TRUCK!  |");
        System.out.println("------------------------");
        agent.dispatchTopLevelGoal(new WanderAroundCity()).get();
        // agent.dispatchTopLevelGoal(new DumpWaste());
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

        @GoalContextCondition(rawevents = @jadex.bdiv3.annotation.RawEvent(value="pause"))
        public boolean checkContext() {
            return !pause;
        }

    }

    /*-----------------------------
     Plans
    *---------------------------*/
    @Plan(trigger=@Trigger(goals=WanderAroundCity.class))
    protected void truckPlan() {

        do{
            try {
                Thread.sleep(SLEEP);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }while(isPause());

        this.updatePos();

        try {
            Thread.sleep(SLEEP);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /*-----------------------------
       Methods
    *---------------------------*/
    public boolean isPause() {
        return pause;
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }

    public void updatePos() {

        // have a trip to do
        if (steps.size() > 0) {
            //TODO get trip
        } else if (steps != null) {
            //Set the current position to the last position on the list of steps
            pos = steps.remove(steps.size() - 1);
        } else {
            pos = autoMove();
        }

    }

    public Position autoMove() {

        Position newPos;
        boolean road = false;

        while (!road) {
            int random = randInt(0, 3);
            switch (random) {
                case 0:
                    newPos = new Position(pos.x, pos.y - 1);
                    if (isRoad(newPos)) {
                        if (isConnected(pos, newPos)) {
                            road = true;
                            return newPos;
                        }
                    }
                    break;
                case 1:
                    newPos = new Position(pos.x + 1, pos.y);
                    if (isRoad(newPos)) {
                        if (isConnected(pos, newPos)) {
                            road = true;
                            return newPos;
                        }
                    }
                    break;
                case 2:
                    newPos = new Position(pos.x, pos.y + 1);
                    if (isRoad(newPos)) {
                        if (isConnected(pos, newPos)) {
                            road = true;
                            return newPos;
                        }
                    }
                    break;
                case 3:
                    newPos = new Position(pos.x-1, pos.y);
                    if (isRoad(newPos)) {
                        if (isConnected(pos, newPos)) {
                            road = true;
                            return newPos;
                        }
                    }
                    break;
                default:
                    break;
            }
        }
        return null;
    }

    public boolean isRoad(Position pos) {
        return gc.getCtB().getVertexByCoords(pos.x, pos.y).getName().charAt(0) == 'v';
    }

    public boolean isConnected(Position orig, Position dest) {
        Vertex origin = gc.getCtB().getVertexByCoords(orig.x, orig.y);
        Vertex destination = gc.getCtB().getVertexByCoords(dest.x, dest.y);

        return gc.getCtB().getGraph().getEdge(origin, destination) != null;
    }

    public int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    public Position getPosition() {
        return pos;
    }

<<<<<<< HEAD
    public typeOfWaste getType() {
        return type;
=======
    public ArrayList<Position> getShortestPath(Position dest){
        if( !isRoad(dest) ) return null;

        ArrayList<Position> path = new ArrayList<>();

        List<DefaultEdge> stepsEdges = DijkstraShortestPath.findPathBetween( gc.getCtB().getGraph(), gc.getCtB().getVertexByCoords(pos.x, pos.y), gc.getCtB().getVertexByCoords(dest.x, dest.y));
        for( DefaultEdge e : stepsEdges ){
            path.add(gc.getCtB().getGraph().getEdgeTarget(e).getPosition());
        }

        return path;
>>>>>>> ce3149a5f35f666f5360f35e62ce157782750d3c
    }
}


