package agents;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.*;
import jadex.micro.annotation.*;
import jadex.rules.eca.annotations.Event;
import main.Location;
import plans.Dump;
import plans.GoToWasteStation;
import plans.PickWaste;
import plans.Wander;

import java.util.LinkedList;

/**
 * Created by Tiago on 27/11/2014.
 */

//TODO Plans, Services

@Agent
@Description("Collector agent.")
@Arguments({@Argument(name="Name", clazz=String.class, defaultvalue="Name"),
            @Argument(name="Location", clazz= Location.class),
            @Argument(name="Type", clazz= CollectorAgent.Typeof_Trash.class),
            @Argument(name="Capacity", clazz=Integer.class)
})
@Plans({ @Plan(trigger = @Trigger(goals = CollectorAgent.PerfomeWander.class), body = @Body(Wander.class)),
        @Plan(trigger = @Trigger(goals = CollectorAgent.CheckContainer.class), body = @Body(PickWaste.class)),
        @Plan(trigger = @Trigger(goals = CollectorAgent.GoToWasteStation.class), body = @Body(GoToWasteStation.class)),
        @Plan(trigger = @Trigger(goals = CollectorAgent.Dump.class), body = @Body(Dump.class)) })

public class CollectorAgent {

    @Agent
    protected BDIAgent agent;

    @Belief
    public String name;

    @Belief
    public Typeof_Trash type;

    @Belief
    private Location location;

    @Belief
    private int capacity, actualWaste;

    @Belief
    public boolean full = false, pause = false, memory=false, aux=false;

    @Belief
    public static final long SLEEP = 300;

    private LinkedList<Location> steps;

    @AgentCreated
    public void init() {

        name = (String) agent.getArgument("Name");
        location = (Location) agent.getArgument("Location");
        if(location == null)
            location = new Location(0, 0);

        type = (Typeof_Trash) agent.getArgument("Type");
        if(type == null)
            type = Typeof_Trash.UNDIFFERENTIATED;

        capacity = 100;
        actualWaste = 0;

        this.pause = GCollector.getInstance().getPauseState();
        GCollector.getInstance().addCollectorAgent(this);

    }



    @AgentBody
    public void body() {
        agent.dispatchTopLevelGoal(new CheckContainer());
        agent.dispatchTopLevelGoal(new PerfomeWander());
        agent.dispatchTopLevelGoal(new Dump());
    }


    @AgentKilled
    public void killed() {
        System.out.println("Agent " + name + " was killed!");
    }

    /*-----------------------------------------------
      Goals
      ---------------------------------------------*/
    @Goal(excludemode = Goal.ExcludeMode.Never, retry = true, succeedonpassed = false)
    public class PerfomeWander {

        @GoalContextCondition(rawevents = @jadex.bdiv3.annotation.RawEvent(value = "full"))
        public boolean checkContext() {
            return (!pause && !full);
        }
    }

    @Goal(excludemode = Goal.ExcludeMode.Never, retry = true, succeedonpassed = false)
    public class CheckContainer {
        @GoalContextCondition(rawevents = @jadex.bdiv3.annotation.RawEvent(value = "pause"))
        public boolean checkContext(){
            return (!pause);
        }
    }

    @Goal(excludemode = Goal.ExcludeMode.Never, retry = true, succeedonpassed = false)
    public class GoToWasteStation {

        protected boolean fullOnGoal;

        @GoalCreationCondition(rawevents = @jadex.bdiv3.annotation.RawEvent(value = "full"))
        public GoToWasteStation(@Event("full") boolean fullE) {
            fullOnGoal = fullE;
        }

        @GoalDropCondition(rawevents = @jadex.bdiv3.annotation.RawEvent(value = "full"))
        public boolean checkDrop() {
            return (!full || !memory);
        }
    }

    @Goal(excludemode = Goal.ExcludeMode.Never, retry = true, succeedonpassed = false)
    public class Dump {
        @GoalContextCondition(rawevents = @jadex.bdiv3.annotation.RawEvent(value = "pause"))
        public boolean checkContext() {
            return !pause;
        }
    }

    /*-----------------------------------------------
     Methods
     ---------------------------------------------*/
    public void updatePosition() throws InterruptedException {

        if (this.steps != null && this.steps.size() == 0)
            steps = GCollector.getInstance().getAgentTrip(location);

        if (steps != null)
            this.location = steps.removeFirst();
        else
            this.location.autoMove();
    }
    public Location getNearestBurner() {

        Location nearestLoc = null;

        //TODO Calcular caminho mais curto da posicao atual até ao depósito

        return nearestLoc;
    }

    public int getActualWaste() {
        return actualWaste;
    }

    public int getFreeCapacity() {
        return capacity - actualWaste;
    }

    public void pickWaste(int quantity) {
        if(quantity>0) {
            actualWaste+=quantity;
            if(actualWaste == capacity)
                full=true;
        }
    }

    public Location getLocation() {
        return location.clone();
    }

    public void goToLocation(Location destination) {
        // TODO Mover no grafo o agente da position atual para destination
    }

    public void dump() {
        actualWaste = 0;
        full = false;
    }

    public static enum Typeof_Trash{
        PAPER,
        GLASS,
        PACKAGING,
        UNDIFFERENTIATED,
    }
}
