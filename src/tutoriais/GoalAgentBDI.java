package  tutoriais;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.*;
import jadex.micro.annotation.*;

@Agent
@Description("An agent with a goal.")
public class GoalAgentBDI {

    @Agent
    protected BDIAgent agent;

    @Belief
    boolean pause = false;

    @AgentBody
    public void body() {
        int result = (int) agent.dispatchTopLevelGoal(new AGoal("important goal")).get();
        System.out.println("Finished with " + result + "!");
    }

    @Plan(trigger=@Trigger(goals=AGoal.class))
    protected void basicPlan() {
        System.out.println("Executing basic plan.");
        if(pause)
            pause = false;
        else
            pause = true;
        System.out.println("Pause is set to : " + pause);
    }

    @Goal(excludemode = Goal.ExcludeMode.Never, retry = true, orsuccess = false)
    public class AGoal {

        @GoalParameter
        protected String p;

        @GoalResult
        protected int r;

        protected boolean pause = true;

        @GoalContextCondition(rawevents = @jadex.bdiv3.annotation.RawEvent(value = "pause"))
        public boolean checkContext() {
            return pause;
        }

        public AGoal(String p) {
            this.p = p;
        }
    }
}

