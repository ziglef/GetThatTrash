package agents;


import gui.GridCity;
import map.Vertex;
import plans.WanderPlan;
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
        @Argument(name = "Position", clazz = Position.class)
})
@Plans({
        @Plan(trigger = @Trigger(goals = TruckAgentBDI.WanderAroundCity.class), body = @Body(WanderPlan.class)),
        // @Plan(trigger = @Trigger(goals = TruckAgent.CheckContainer.class), body = @Body(PickWasteFromContainerPlan.class)),
        // @Plan(trigger = @Trigger(goals = TruckAgent.GoToDeposit.class), body = @Body(GoToDepositPlan.class)),
        // @Plan(trigger = @Trigger(goals = TruckAgent.DumpWaste.class), body = @Body(DumpWastePlan.class))
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
    boolean pause = false, memory = false, full = false, mission = false, communication = false;

    @Belief
    public static final long SLEEP = 500;

    private List<Position> steps;
    public static final String AGENT_PATH = "src/agents/TruckAgent.class";
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
        if (type == null) type = typeOfWaste.UNDIFFERENTIATED;
        pos = (Position) agent.getArgument("Position");
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
    }

    @AgentBody
    public void body() {
        // agent.dispatchTopLevelGoal(new CheckContainer());
        System.out.println("------------------------");
        System.out.println("| >>> BODY OF A TRUCK!  |");
        System.out.println("------------------------");
        agent.dispatchTopLevelGoal(new WanderAroundCity());
        // agent.dispatchTopLevelGoal(new DumpWaste());
    }

    @AgentKilled
    public void killed() {
        System.out.println("Killed agent" + agent.getAgentName());
    }

    /*-----------------------------
        Goals
    *---------------------------*/
    @Goal(excludemode = Goal.ExcludeMode.Never, retry = true, succeedonpassed = false)
    public class WanderAroundCity {

        @GoalContextCondition(rawevents = @jadex.bdiv3.annotation.RawEvent(value = "full"))
        public boolean checkContext() {
            return (!full && !pause);
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
                        road = true;
                        return newPos;
                    }
                    break;
                case 1:
                    newPos = new Position(pos.x + 1, pos.y);
                    if (isRoad(newPos)) {
                        road = true;
                        return newPos;
                    }
                    break;
                case 2:
                    newPos = new Position(pos.x, pos.y + 1);
                    if (isRoad(newPos)) {
                        road = true;
                        return newPos;
                    }
                    break;
                case 3:
                    newPos = new Position(pos.x-1, pos.y);
                    if (isRoad(newPos)) {
                        road = true;
                        return newPos;
                    }
                    break;
                default:
                    break;
            }
        }

        return null;
    }

    public boolean isRoad(Position pos) {
        Vertex v = gc.getCtB().getVertexByCoords(pos.x, pos.y);
        if (v != null && v.getName().charAt(0) == 'v')
            return true;
        return false;
    }

    public int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    public Position getLocation() {
        return pos.clone();
    }
}


