package agents;

import gui.GridCity;
import jadex.bridge.service.types.cms.IComponentManagementService;
import main.Collector;
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
    boolean full = false;

    @Belief
    boolean mission = false;

    @Belief
    boolean communication = false;

    @Belief
    public static final long SLEEP = 300;

    private List<Position> steps;
    public static final String AGENT_PATH = "out\\production\\AIAD\\agents\\TruckAgentBDI.class";
    private GridCity gc;
    private int remainderCapacity = 0;


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
        /*if(pos == null)
            pos = uma posicao que seja estrada; mas nunca será null, só para caso haja algum erro*/
        capacity = (Integer) agent.getArgument("Capacity");
        steps = new ArrayList<>();
        occupiedCapacity = 0;
        pause = GarbageCollector.getInstance().getPause();
        GarbageCollector.getInstance().addTruckAgent(this);
        memory = GarbageCollector.getInstance().memory;
        mission = false;
        gc = GarbageCollector.getInstance().getInterface().getCity();

    }

    @AgentBody
    public void body() {
        agent.dispatchTopLevelGoal(new CheckContainer());
        agent.dispatchTopLevelGoal(new WanderAroundCity()).get();
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

        @GoalContextCondition(rawevents = @jadex.bdiv3.annotation.RawEvent(value="pause"))
        public boolean checkContext() {
            return !pause;
        }

    }

    @Goal(excludemode = Goal.ExcludeMode.Never, retry = true, orsuccess = false)
    public class CheckContainer {

        @GoalResult
        protected int r;

        @GoalContextCondition(rawevents = @jadex.bdiv3.annotation.RawEvent(value="pause"))
        public boolean checkContext() {
            return !pause;
        }

    }

    @Goal(excludemode = Goal.ExcludeMode.Never, retry = true, orsuccess = false)
    public class GoToDeposit {

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
    protected void walkAroundCity() {

        do{
            try {
                Thread.sleep(SLEEP);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }while(GarbageCollector.getInstance().getPause());

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
        }while(GarbageCollector.getInstance().getPause());


        //TODO verificar se um contentor está adjacentes à POS atual
        // se tiver um contentor adjacente, verificar o seu tipo, apanhar o lixo e se exceder a minha capacidade, deixar lá o resto
        Collector collector = GarbageCollector.getInstance().checkCollectorPos(pos);
        if(collector != null) {

            System.out.println("Passei por um contentor do meu tipo e vou apanhar o lixo");
            if(collector.getType() == type){
                try {
                    Thread.sleep(SLEEP);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if(occupiedCapacity < capacity) {
                    if (occupiedCapacity + collector.getOccupiedCapacity() > capacity) {
                        remainderCapacity = occupiedCapacity + collector.getOccupiedCapacity() - capacity;
                        occupiedCapacity = capacity;
                        GarbageCollector.getInstance().setCollectorOcuppiedCapacity(collector.getPosition(), remainderCapacity);
                    } else {
                        occupiedCapacity += collector.getOccupiedCapacity();
                        GarbageCollector.getInstance().setCollectorOcuppiedCapacity(collector.getPosition(), 0);
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


        //TODO se tiver memory = true, calcula o caminho mais curto, adiciona uma lista de steps a fazer e faz todos esses steps até ao deposito mais perto

    }

    /*-----------------------------
       Methods
    *---------------------------*/

    public void setPause(boolean pause) {
        this.pause = pause;
    }

    public void updatePos() {

        pos = autoMove();

        // have a trip to do
       /* if (steps.size() > 0) {
            System.out.println("steps.size() > 0");
        } else if (steps != null) {
            //Set the current position to the last position on the list of steps
            pos = steps.remove(steps.size() - 1);
            System.out.println("steps != null");
        } else {
            pos = autoMove();
        }*/

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
        if( !isRoad(dest) ) return null;

        ArrayList<Position> path = new ArrayList<>();

        List<DefaultEdge> stepsEdges = DijkstraShortestPath.findPathBetween( gc.getCtB().getGraph(), gc.getCtB().getVertexByCoords(pos.x, pos.y), gc.getCtB().getVertexByCoords(dest.x, dest.y));
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
	
		for( Position p : dest ){
			paths.add( this.getShortestPath( p ) );
			if( paths.get(paths.size()-1).size() < min ){
				min = paths.get(paths.size()-1).size();
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

}


