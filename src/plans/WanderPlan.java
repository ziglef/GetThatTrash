package plans;

import agents.InterfaceAgent;
import agents.TruckAgent;
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
    protected TruckAgent truck;
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

        do{
            Thread.sleep(TruckAgent.SLEEP);
        }while(truck.isPause());

        truck.updatePos();
        Thread.sleep(TruckAgent.SLEEP);
    }
}
