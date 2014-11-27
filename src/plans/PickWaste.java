package plans;

import agents.CollectorAgent;
import jadex.bdiv3.annotation.*;
import main.Location;

/**
 * Created by Tiago on 27/11/2014.
 */
@Plan
public class PickWaste {

    @PlanCapability
    private CollectorAgent collector;

    public PickWaste() {}


    public void getWaste(ContainerBDI container) {

        int quantity=0;

        if(container.getQuantity() < collector.getFreeCapacity())
            quantity=container.getQuantity();
        else
            quantity=collector.getFreeCapacity();

        container.decrementWaste(quantity);
        collector.pickWaste(quantity);

        System.out.println("Picked up: "+ quantity);
    }

    @PlanBody
    public void body() throws InterruptedException {

        //TODO ter em atenção a localizacao de todos os containers, ver como está a ser feita a parte do grafo
       /*
        Location[] containers = GCollector.getInstance().getContainerlocations();
        Location loc = collector.getLocation();


        for(int i=0; i<containers.length;i++) {

            if( (loc.x+1 == containers[i].x) && (loc.y == containers[i].y) ) {
                process(containers, i);
            }
            else if( (loc.x == containers[i].x) && (loc.y+1 == containers[i].y) ) {
                process(containers, i);
            }
            else if( (loc.x-1 == containers[i].x) && (loc.y == containers[i].y) ) {
                process(containers, i);
            }
            else if( (loc.x == containers[i].x) && (loc.y-1 == containers[i].y) ) {
                process(containers, i);
            }
        }*/
    }

    private void process(Location[] clocations, int i) {
        //System.out.println("Container Encontrado!");
        ContainerBDI container = GCollector.getInstance().getContainerByLocation(clocations[i]);
        if((container.type==collector.type) && !collector.full) {
            getWaste(container);
        }
        else {
           //TODO se o container tiver lixo enviar uma mensagem para a rede de camiões a informar isso mesmo, quando for implementada a comunocação
        }
    }

}