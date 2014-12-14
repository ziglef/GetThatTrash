package agents;

import gui.Interface;
import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.*;
import jadex.micro.annotation.*;
import main.GarbageCollector;
import main.Position;
import java.io.FileNotFoundException;

/**
 * Class responsible for create an Interface agent and launch the Interface
 *
 * @author Rui Grandão  - ei11010@fe.up.pt
 * @author Tiago Coelho - ei11012@fe.up.pt
 *
 */
@Agent
@Description("Agent that starts an interface and update the elements on the map.")
public class InterfaceAgentBDI {

    /*------------------------------
        Declarations
    *-----------------------------*/
    public static InterfaceAgentBDI intAgent;

    @Agent
    protected BDIAgent agent;

    @Belief
    protected Interface graphicInt;

    @Belief
    boolean pause = false;

    @Belief
    public final long SLEEP = 50;


    /*------------------------------
        Agent
    *-----------------------------*/
    /**
     * Method invoked emmediately after truck Agent creation.
     * Launch Interface and start agent's goals.
     */
    @AgentBody
    public void body(){
        try {
            graphicInt = new Interface(agent.getExternalAccess());
            intAgent = this;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        GarbageCollector.getInstance().setInterface(graphicInt);
        agent.dispatchTopLevelGoal(new AGoal()).get();
    }

    /**
     * Method invoked when agent is killed
     */
    @AgentKilled
    public void killed() {
        System.out.println("Killed agent" + agent.getAgentName());
    }


    /*-----------------------------
       Plans
     *---------------------------*/
    /**
     * Interface plan to update the city on the interface
     *
     * @throws InterruptedException - If Thread.sleep launch an Excepection
     */
    @Plan(trigger=@Trigger(goals=AGoal.class))
    protected void basicPlan() throws  InterruptedException{

        intAgent.updateCity();

        Thread.sleep(SLEEP);

    }

    /*-----------------------------
       Goals
     *---------------------------*/
    /**
     * Goal of the interface agente to updates interface
     */
    @Goal(excludemode = Goal.ExcludeMode.Never, retry = true, orsuccess = false)
    public class AGoal {

        @GoalResult
        protected int r;

        @GoalRecurCondition(beliefs="pause")
        public boolean checkContext() {
            return !pause;
        }

    }

    /*-----------------------------
        Methods
     *---------------------------*/
    /**
     * Method that validate and repaint the city on the interface
     */
    public void updateCity(){
        graphicInt.validate();
        graphicInt.repaint();
    }


}