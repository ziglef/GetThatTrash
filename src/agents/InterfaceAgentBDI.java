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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

@Agent
@Description("Agent that starts an interface and update the elements on the map.")

public class InterfaceAgentBDI {

    /*------------------------------
        Declarations
    *-----------------------------*/
    @Agent
    public static BDIAgent agent;

    protected Position[] truckLoc;

    Interface graphicInt;

    @Belief
    boolean pause = false;

    public final long SLEEP = 50;

    @Agent
    public static InterfaceAgentBDI intAgent;

    public static IServiceProvider isp;

    /*------------------------------
        Agent Body
    *-----------------------------*/
    @AgentBody
    public void body(){
        try {
            graphicInt = new Interface();
            System.out.println("Estou a correr a interface pelo body do agente!");
            //intAgent = this;

            isp = agent.getServiceProvider();
            if(isp == null)
                System.out.println("ISP IS NULL");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        GarbageCollector.getInstance().setInterface(graphicInt);
        agent.dispatchTopLevelGoal(new AGoal("Important goal")).get();
    }

    /*-----------------------------
       Plans
     *---------------------------*/
    @Plan(trigger=@Trigger(goals=AGoal.class))
    protected void basicPlan() {
        //intAgent.updateCity();

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

        @GoalParameter
        protected String p;

        @GoalResult
        protected int r;

        @GoalContextCondition(rawevents = @jadex.bdiv3.annotation.RawEvent(value = "pause"))
        public boolean checkContext() {
            return !pause;
        }

        public AGoal(String p) {
            this.p = p;
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

        //TODO containerLoc = GarbageCollector.getInstance().getContainersLoc();
        //TODO depositLoc = GarbageCollector.getInstance().getDepositsLoc();

        Position[] trucksLoc_aux = GarbageCollector.getInstance().getTrucksLoc();

        if(truckLoc!=null){
            for(int i = 0; i < truckLoc.length; i++){
                if(trucksLoc_aux[i].equals(truckLoc[i])){

                }
            }
        }

        //TODO desenhar na cidade os depósitos (depositLoc) nas respectivas posições
        //TODO desenhar na cidade os contentores (containerLoc) nas respectivas posições


        truckLoc = new Position[trucksLoc_aux.length];
        System.arraycopy(trucksLoc_aux,0,truckLoc,0,truckLoc.length);
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