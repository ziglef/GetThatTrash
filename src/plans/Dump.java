package plans;

import agents.CollectorAgent;
import jadex.bdiv3.annotation.*;
import main.Location;

/**
 * Created by Tiago on 27/11/2014.
 */
@Plan
public class Dump{

    @PlanCapability
    private CollectorAgent collector;

    public Dump() {}


    public void DumpWastetoBurner(BurnerBDI burner) {
        int quantity = collector.getActualWaste();
        burner.dumpWaste(quantity);
        collector.dump();
    }

    @PlanBody
    public void body() throws InterruptedException {

        //TODO ver a posicao dos depositos e procurar um deposito partindo da posicao do collectionr
        /*Location[] burnerlocations = GCollector.getInstance().getBurnerlocations();
        Location loc = collector.getLocation();

        for(int i=0; i<burnerlocations.length;i++) {
            boolean found=false;
            if( (loc.x+1 == burnerlocations[i].x) && (loc.y == burnerlocations[i].y) )
                found=true;
            else if( (loc.x == burnerlocations[i].x) && (loc.y+1 == burnerlocations[i].y) )
                found=true;
            else if( (loc.x-1 == burnerlocations[i].x) && (loc.y == burnerlocations[i].y) )
                found=true;
            else if( (loc.x == burnerlocations[i].x) && (loc.y-1 == burnerlocations[i].y) )
                found=true;

            if(found==true) {
                //System.out.println("------Burner Encontrado!");
                BurnerBDI b = GCollector.getInstance().getBurnerByLocation(burnerlocations[i]);
                DumpWastetoBurner(b);
                collector.aux=false;
                collector.onGoing=false;
            }*/
    }
}