package agents;

import gui.Interface;
import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.*;
import jadex.bridge.service.*;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.*;
import jadex.commons.future.ThreadSuspendable;
import jadex.micro.annotation.*;
import main.GarbageCollector;
import main.Position;
import plans.UpdateMap;

import java.io.FileNotFoundException;

@Agent
@Plans({ @Plan(trigger = @Trigger(goals = InterfaceAgent.GoalUpdateCity.class), body = @Body(UpdateMap.class)) })
@Description("Agent that starts an interface and update the elements on the map.")

public class InterfaceAgent{

    /*------------------------------
        Declarations
    *-----------------------------*/
    @Agent protected BDIAgent agent;
    @Belief protected Position[] truckLoc, containerLoc, depositLoc;
    @Belief Interface graphicInt;
    @Belief boolean pause = false;
    @Belief public static final long SLEEP = 50;
    public static InterfaceAgent intAgent;

    /*------------------------------
        Agent Body
    *-----------------------------*/
    @AgentBody
    public void body(){
        /*intAgent = this;
        graphicInt = new Interface(agent.getExternalAccess());
        GarbageCollector.getInstance().setInterface(graphicInt);
        if(agent == null){
            System.out.println("Agent is null");
        }
        agent.dispatchTopLevelGoal(new GoalUpdateCity()).get();*/

        try {
            graphicInt = new Interface();
            System.out.println("Estou a correr a interface pelo body do agente!");
            intAgent = this;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        GarbageCollector.getInstance().setInterface(graphicInt);
        agent.dispatchTopLevelGoal(new GoalUpdateCity()).get();

    }

    /*-----------------------------
       Goals
     *---------------------------*/
    @Goal(excludemode = Goal.ExcludeMode.Never, retry = true, succeedonpassed = false)
    public class GoalUpdateCity{
        @GoalContextCondition(rawevents = @jadex.bdiv3.annotation.RawEvent(value = "pause"))
        public boolean checkContext() {
            return !pause;
        }
    }

    /*-----------------------------
        Methods
     *---------------------------*/
    public void deployAgent(String path, jadex.bridge.service.types.cms.CreationInfo info){
        ThreadSuspendable t = new ThreadSuspendable();

        IComponentManagementService icms = SServiceProvider.getService(agent.getServiceProvider(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).get(t);
        icms.createComponent(path, info).getFirstResult(t);
    }

    public void updateCity(){

        //TODO containerLoc = GarbageCollector.getInstance().getContainersLoc();
        //TODO depositLoc = GarbageCollector.getInstance().getDepositsLoc();

        Position[] trucksLoc_aux = GarbageCollector.getInstance().getTrucksLoc();

        if(truckLoc!=null){
            //TODO fazer ciclo e verificar a posição dos novos camiões, se for igual à anterior, nao faz update à imagem
            //TODO senão apaga a imagem na posiçao anterior e coloca o camião na sua nova posicao
        }

        //TODO desenhar na cidade os depósitos (depositLoc) nas respectivas posições
        //TODO desenhar na cidade os contentores (containerLoc) nas respectivas posições


        truckLoc = new Position[trucksLoc_aux.length];
        System.arraycopy(trucksLoc_aux,0,truckLoc,0,truckLoc.length);
    }

}