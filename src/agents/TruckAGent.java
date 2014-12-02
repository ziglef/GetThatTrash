package agents;


import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.*;
import jadex.micro.annotation.*;
import main.GarbageCollector;
import main.Position;

import java.util.ArrayList;
import java.util.List;


@Agent
@Description("Agent that launch a new garbage truck of a specific type.")
@Arguments({
        @Argument(name="Name", clazz=String.class, defaultvalue = "AnonymousTruck"),
        @Argument(name="Capacity", clazz=Integer.class, defaultvalue="500"),
        @Argument(name="Type", clazz=TruckAgent.typeOfWaste.class),
        @Argument(name="Position", clazz=Position.class)
})
/*@Plans({
        @Plan(trigger = @Trigger(goals = TruckAgent.WanderAroundCity.class), body = @Body(WanderPlan.class)),
        @Plan(trigger = @Trigger(goals = TruckAgent.CheckContainer.class), body = @Body(PickWasteFromContainerPlan.class)),
        @Plan(trigger = @Trigger(goals = TruckAgent.GoToDeposit.class), body = @Body(GoToDepositPlan.class)),
        @Plan(trigger = @Trigger(goals = TruckAgent.DumpWaste.class), body = @Body(DumpWastePlan.class))
})*/

public class TruckAgent {

    @Agent protected BDIAgent agent;
    @Belief String name;
    @Belief int capacity;
    @Belief typeOfWaste type = typeOfWaste.UNDIFFERENTIATED;
    @Belief Position pos;
    @Belief int occupiedCapacity;
    @Belief boolean pause = false, memory = false, full = false, mission = false, communication = false;
    @Belief static final long SLEEP = 500;
    private List<Position> steps;
    public static final String AGENT_PATH = "src/agents/TruckAgent.class";

    public static enum typeOfWaste{
        PAPER, PLASTIC, GLASS, UNDIFFERENTIATED
    }

    @AgentCreated
    public void init() {
        name = (String) agent.getArgument("Name");
        type = (typeOfWaste) agent.getArgument("Type");
        if(type == null) type = typeOfWaste.UNDIFFERENTIATED;
        pos = (Position) agent.getArgument("Position");
        capacity = (Integer) agent.getArgument("Capacity");
        steps = new ArrayList<>();
        occupiedCapacity = 0;
        pause = GarbageCollector.getInstance().getPause();
        GarbageCollector.getInstance().addTruckAgent(this);
        memory=GarbageCollector.getInstance().memory;
        mission=false;
    }

}


