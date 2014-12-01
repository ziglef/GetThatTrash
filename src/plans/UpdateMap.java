package plans;

import agents.InterfaceAgent;
import jadex.bdi.runtime.IPlan;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;

/**
 * Created by Tiago on 29/11/2014.
 */
@Plan
public class UpdateMap {

    /*------------------------------
        Declarations
    *-----------------------------*/
    @PlanCapability protected InterfaceAgent intAgent;
    @PlanAPI protected IPlan plan;

    /*------------------------------
        Default Constructor
    *-----------------------------*/
    public UpdateMap(){}

    /*------------------------------
        Plan Body
    *-----------------------------*/
    @PlanBody
    public void body() throws InterruptedException{
        intAgent.updateCity();
        Thread.sleep(InterfaceAgent.SLEEP);
    }
}
