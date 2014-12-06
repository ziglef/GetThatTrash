package plans;

import agents.TruckAgentBDI;
import jadex.bdi.runtime.IPlan;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;

@Plan
public class WanderPlan{

    /*------------------------------
        Declarations
    *-----------------------------*/
    @PlanCapability
    protected TruckAgentBDI truck;
    @PlanAPI
    protected IPlan plan;

    /*------------------------------
        Default Constructor
    *-----------------------------*/
    public WanderPlan(){}

    /*------------------------------
        Plan Body
    *-----------------------------*/
    @PlanBody
    public void body() throws InterruptedException{

        System.out.println("BODY DO PLANO DO CAMIAO");

        do{
            Thread.sleep(TruckAgentBDI.SLEEP);
        }while(truck.isPause());

        truck.updatePos();
        Thread.sleep(TruckAgentBDI.SLEEP);
    }
}
