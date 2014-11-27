package plans;

import agents.CollectorAgent;
import jadex.bdiv3.annotation.*;
import main.Location;

/**
 * Created by Tiago on 27/11/2014.
 */

@Plan
public class GoToWasteStation {

    @PlanCapability
    private CollectorAgent collector;

    public GoToWasteStation() {}

    @PlanBody
    public void body() throws InterruptedException {

        while (collector.pause) {
            Thread.sleep(500);
        }
        if(collector.memory) {
            if(collector.aux==false) {
                Location loc = collector.getNearestBurner();
                collector.goToLocation(loc);
                collector.aux=true;
            }
        }

        collector.updatePosition();
        Thread.sleep((long) (CollectorAgent.SLEEP));
    }
}