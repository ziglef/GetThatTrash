package plans;

import agents.CollectorAgent;
import jadex.bdi.runtime.IPlan;
import jadex.bdiv3.annotation.*;

/**
 * Created by Tiago on 27/11/2014.
 */
@Plan
public class Wander {

    @PlanCapability
    protected CollectorAgent collector;

    @PlanAPI
    protected IPlan rplan;

    public Wander() {}

    @PlanBody
    public void body() throws InterruptedException {

        while (collector.pause) {
            Thread.sleep(500);
        }
        collector.updatePosition();
        Thread.sleep((long) (CollectorAgent.SLEEP));
    }

}
