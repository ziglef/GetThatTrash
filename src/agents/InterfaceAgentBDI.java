package agents;

import gui.Interface;
import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.*;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.*;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.*;
import jadex.commons.future.ThreadSuspendable;
import jadex.micro.annotation.*;
import main.GarbageCollector;
import main.Position;
import java.io.FileNotFoundException;

@Agent
@Description("Agent that starts an interface and update the elements on the map.")

public class InterfaceAgentBDI {

    /*------------------------------
        Declarations
    *-----------------------------*/
    public static InterfaceAgentBDI intAgent;

    @Agent
    protected BDIAgent agent;

    protected Position[] truckLoc;

    @Belief
    protected Interface graphicInt;

    @Belief
    boolean pause = false;

    @Belief
    public final long SLEEP = 50;


    /*------------------------------
        Agent Body
    *-----------------------------*/
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

    /*-----------------------------
       Plans
     *---------------------------*/
    @Plan(trigger=@Trigger(goals=AGoal.class))
    protected void basicPlan() {
        intAgent.updateCity();

       // System.out.println("UpdateCity");
        try {
            Thread.sleep(SLEEP);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /*-----------------------------
       Goals
     *---------------------------*/
    @Goal(excludemode = Goal.ExcludeMode.Never, retry = true, orsuccess = false)
    public class AGoal {

        @GoalResult
        protected int r;

        @GoalContextCondition(rawevents = @jadex.bdiv3.annotation.RawEvent(value = "pause"))
        public boolean checkContext() {
            return !pause;
        }

    }

    /*-----------------------------
        Methods
     *---------------------------*/
    public void deployAgent(String path, jadex.bridge.service.types.cms.CreationInfo info){

        System.out.println("Cheguei ao deployAgent");

        ThreadSuspendable sus = new ThreadSuspendable();
        IServiceProvider sp = agent.getServiceProvider();

       IComponentManagementService cms = null;
        try{
            cms = SServiceProvider.getService(sp, IComponentManagementService.class,
                    RequiredServiceInfo.SCOPE_PLATFORM).get(sus);
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        IComponentIdentifier ici = cms.createComponent(path, info).getFirstResult(sus);
        System.out.println("started: " + ici);
    }

    public void updateCity(){

            //UPDATEINTERFACE
        graphicInt.validate();
        graphicInt.repaint();
    }

    public InterfaceAgentBDI getIntAgent() {
        return intAgent;
    }

    public void setIntAgent(InterfaceAgentBDI intAgent) {
        this.intAgent = intAgent;
    }

    @AgentKilled
    public void killed(){
        System.out.println("Killed agent" + agent.getAgentName());
    }

}