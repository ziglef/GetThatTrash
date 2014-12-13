package agents;

import gui.GridCity;
import javafx.geometry.Pos;
import main.Collector;
import main.Deposit;
import map.Vertex;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.alg.DijkstraShortestPath;
import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.*;
import jadex.micro.annotation.*;
import main.GarbageCollector;
import main.Position;

import java.nio.channels.GatheringByteChannel;
import java.util.ArrayList;
import java.util.List;


@Agent
@Description("Agent that launch a new garbage truck of a specific type.")
@Arguments({
        @Argument(name = "Name", clazz = String.class, defaultvalue = "Anony"),
        @Argument(name = "Capacity", clazz = Integer.class, defaultvalue = "500"),
        @Argument(name = "Type", clazz = GarbageCollector.typeOfWaste.class),
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

    GarbageCollector.typeOfWaste type = GarbageCollector.typeOfWaste.UNDIFFERENTIATED;
    Position pos;
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
    public static final long SLEEP = 300;

    private ArrayList<Position> steps;
    public static final String AGENT_PATH = "out\\production\\AIAD\\agents\\TruckAgentBDI.class";
    private GridCity gc;
    private int remainderCapacity = 0;
    private static ArrayList<Position> collectorsInMemory;
    private static ArrayList<Position> depositsInMemory;


    /*------------------------------
        Agent
    *-----------------------------*/
    @AgentCreated
    public void init() {
        name = (String) agent.getArgument("Name");
        type = (GarbageCollector.typeOfWaste) agent.getArgument("Type");
        if(type==null)
            type=GarbageCollector.typeOfWaste.UNDIFFERENTIATED;
        pos = new Position(0,0);
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

        @GoalRecurCondition(beliefs="pause")
        public boolean checkContext() {
            return !pause;
        }

    }

    @Goal(excludemode = Goal.ExcludeMode.Never, retry = true, orsuccess = false)
    public class CheckContainer {

        @GoalResult
        protected int r;

        @GoalRecurCondition(beliefs="pause")
        public boolean checkContext() {
            return !pause;
        }

    }

    @Goal(excludemode = Goal.ExcludeMode.Never, retry = true, orsuccess = false)
    public class GoToDeposit {

        @GoalResult
        protected int r;

        @GoalRecurCondition(beliefs="pause")
        public boolean checkContext() {
            return !pause;
        }

    }

    /*-----------------------------
     Plans
    *---------------------------*/
    @Plan(trigger=@Trigger(goals=WanderAroundCity.class))
    protected void walkAroundCity() {

        do{
            try {
                Thread.sleep(SLEEP);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }while(pause = GarbageCollector.getInstance().getPause());

        updatePos();
        gc.validate();
        gc.repaint();

        try {
            Thread.sleep(SLEEP);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Plan(trigger=@Trigger(goals=CheckContainer.class))
    protected void checkContainer() {

        do{
            try {
                Thread.sleep(SLEEP);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }while(pause = GarbageCollector.getInstance().getPause());

        ArrayList<Collector> adjacentsCollectors = GarbageCollector.getInstance().checkAdjacentCollectorPos(pos);

        if(adjacentsCollectors.size() > 0){
            for(int i = 0 ; i < adjacentsCollectors.size(); i++){

                if(adjacentsCollectors.get(i).getType() == type) {
                    if (!collectorsInMemory.contains(adjacentsCollectors.get(i).getPosition()) && GarbageCollector.getInstance().getMemory()) {
                        collectorsInMemory.add(adjacentsCollectors.get(i).getPosition());
                        System.out.println("Adicionei a lista dos collectors que conheco o collector na posicao " + adjacentsCollectors.get(i).getPosition().x + "-" + adjacentsCollectors.get(i).getPosition().y);
                    }

                    if (occupiedCapacity < capacity) {
                        if (occupiedCapacity + adjacentsCollectors.get(i).getOccupiedCapacity() > capacity) {
                            remainderCapacity = occupiedCapacity + adjacentsCollectors.get(i).getOccupiedCapacity() - capacity;
                            occupiedCapacity = capacity;
                            adjacentsCollectors.get(i).setOccupiedCapacity(remainderCapacity);
                            i = adjacentsCollectors.size();
                        } else {
                            occupiedCapacity += adjacentsCollectors.get(i).getOccupiedCapacity();
                            adjacentsCollectors.get(i).setOccupiedCapacity(0);
                        }
                        try {
                            Thread.sleep(SLEEP);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    @Plan(trigger=@Trigger(goals=GoToDeposit.class))
    protected void GoToDeposit() {

        do{
            try {
                Thread.sleep(SLEEP);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }while(GarbageCollector.getInstance().getPause());

        ArrayList<Deposit> adjacentDeposits = GarbageCollector.getInstance().checkAdjacentDEpositPos(pos);


        if(adjacentDeposits.size() > 0) {

            for(int i = 0 ; i < adjacentDeposits.size(); i++) {

                if (adjacentDeposits.get(i).getType() == type && occupiedCapacity != 0) {
                    adjacentDeposits.get(i).setOccupiedCapacity(adjacentDeposits.get(i).getOccupiedCapacity() + occupiedCapacity);
                    occupiedCapacity = 0;

                    try {
                        Thread.sleep(SLEEP);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (!depositsInMemory.contains(adjacentDeposits.get(i).getPosition()) && GarbageCollector.getInstance().getMemory()) {
                    depositsInMemory.add(adjacentDeposits.get(i).getPosition());
                    System.out.println("Adicionei a lista dos depositos que conheco o deposito na posicao " + adjacentDeposits.get(i).getPosition().x + "-" + adjacentDeposits.get(i).getPosition().y);
                }
            }

            steps = getShortestPath(depositsInMemory);
            System.out.println("Calculei o caminho mais curto");
        }

    }

    /*-----------------------------
       Methods
    *---------------------------*/

    public void setPause(boolean pause) {
        this.pause = pause;
    }

    public void updatePos() {

       // System.out.println("Steps size : " + steps.size());

        // have a trip to do
       if (steps.size() > 0) {
            pos = steps.remove(steps.size() - 1);
           System.out.println("Tou a fazer um passo presente em STEPS");
        } else {
            pos = autoMove();
        }

    }

    public Position autoMove() {
        ArrayList<Position> neighbors = new ArrayList<>();
        if( isConnected(pos, new Position(pos.x, pos.y - 1)) ) neighbors.add(new Position(pos.x, pos.y - 1));
        if( isConnected(pos, new Position(pos.x + 1, pos.y)) ) neighbors.add(new Position(pos.x + 1, pos.y));
        if( isConnected(pos, new Position(pos.x, pos.y + 1)) ) neighbors.add(new Position(pos.x, pos.y + 1));
        if( isConnected(pos, new Position(pos.x - 1, pos.y)) ) neighbors.add(new Position(pos.x - 1, pos.y));

        return neighbors.get(((int)(Math.random() * 1000) % neighbors.size()));
    }

    public boolean isRoad(Position pos) {
        return gc.getCtB().getVertexByCoords(pos.x, pos.y).getName().charAt(0) == 'v';
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

    public ArrayList<Position> getShortestPath(Position dest){
        //if( !isRoad(dest) ) return null;

        ArrayList<Position> path = new ArrayList<>();

        ArrayList<Position> neighbors = new ArrayList<>();
        if( isConnected(dest, new Position(dest.x, dest.y - 1)) ) neighbors.add(new Position(dest.x, dest.y - 1));
        if( isConnected(dest, new Position(dest.x + 1, dest.y)) ) neighbors.add(new Position(dest.x + 1, dest.y));
        if( isConnected(dest, new Position(dest.x, dest.y + 1)) ) neighbors.add(new Position(dest.x, dest.y + 1));
        if( isConnected(dest, new Position(dest.x - 1, dest.y)) ) neighbors.add(new Position(dest.x - 1, dest.y));

        int min = Integer.MAX_VALUE;
        int minIndex = 0;
        int i = 0;
        for( Position p : neighbors ){
            int pSize = DijkstraShortestPath.findPathBetween( gc.getCtB().getGraph(), gc.getCtB().getVertexByCoords(pos.x, pos.y), gc.getCtB().getVertexByCoords(p.x, p.y)).size();
            if( pSize < min ){
                min = pSize;
                minIndex = i;
            }
            i++;
        }

        List<DefaultEdge> stepsEdges = DijkstraShortestPath.findPathBetween( gc.getCtB().getGraph(), gc.getCtB().getVertexByCoords(pos.x, pos.y), gc.getCtB().getVertexByCoords(neighbors.get(minIndex).x, neighbors.get(minIndex).y));
        for( DefaultEdge e : stepsEdges ){
            path.add(gc.getCtB().getGraph().getEdgeTarget(e).getPosition());
        }

        return path;
    }
	
	public ArrayList<Position> getShortestPath(ArrayList<Position> dest){


        ArrayList<ArrayList<Position>> paths = new ArrayList<>();
		int min = Integer.MAX_VALUE;
		int minIndex = 0;
		int i = 0;
        System.out.println("Entrei aqui 1");

		for( Position p : dest ){
			paths.add( this.getShortestPath(p) );
            System.out.println("Entrei aqui 2");
			if( paths.get(paths.size()-1).size() < min ){
				min = paths.get(paths.size()-1).size();
				minIndex = i;
			}
			i++;

            System.out.println("Entrei aqui 3");
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

}


