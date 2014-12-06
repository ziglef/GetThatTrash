import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Description;

@Agent
@Description("An agent with a goal.")
public class GoalAgentBDI {

    @Agent
    protected BDIAgent agent;

    @AgentBody
    public void body() {
        int result = (int) agent.dispatchTopLevelGoal(new AGoal("important goal")).get();
        System.out.println("Finished with " + result + "!");
    }

    @Plan(trigger=@Trigger(goals=AGoal.class))
    protected void basicPlan() {
        System.out.println("Executing basic plan.");
    }

}