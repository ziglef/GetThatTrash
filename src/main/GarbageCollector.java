package main;

import agents.InterfaceAgent;
import agents.TruckAgent;
import gui.Interface;
//import agents.TruckAgent;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.ThreadSuspendable;

import java.util.ArrayList;

/**
 * Created by Tiago on 30/11/2014.
 */

//TODO add things about deposits and collectors
public class GarbageCollector {

    /*------------------------------
        Declarations
    *-----------------------------*/
    private Interface graphicInt;
    private ArrayList<TruckAgent> truckAgents;
    private ThreadSuspendable t;
    private IExternalAccess ia;
    private IComponentManagementService icms;
    private static GarbageCollector instance;
    public boolean memory = true, communication = true;

    /*------------------------------
        Constructors
    *-----------------------------*/
    protected GarbageCollector(){
        truckAgents = new ArrayList<>();
        instance = null;
    }

    /*------------------------------
        Declarations
    *-----------------------------*/
    public void launchAgent(String path, CreationInfo info){
        InterfaceAgent.intAgent.deployAgent(path, info);
    }

    public static GarbageCollector getInstance(){
        return instance == null? instance = new GarbageCollector() : instance;
    }

    public void addTruck(TruckAgent truck){
        truckAgents.add(truck);
    }

    public Position[] getTrucksLoc(){
        Position[] aux = new Position[truckAgents.size()];
        int i = 0;
        //for(TruckAgent truck : truckAgents)
          //  aux[++i]=truck.getLocation();

        return aux;
    }

    public TruckAgent getTruckByLoc(Position pos){
        for(TruckAgent truck : truckAgents){
            //if(truck.getPosition().equals(pos))
                return truck;
        }
        return null;
    }

    public boolean isRoad(Position pos){
        //TODO  veriricar se uma posição é estrada! - graphicInt.getSpaceByPos(pos);

        return true;
    }

    public void setInterface(Interface graphicInt){
        if(this.graphicInt == null)
            this.graphicInt = graphicInt;
    }

    public Interface getInterface(){
        return this.graphicInt;
    }

    //TODO check if we need to do agentTrip for something

    public void togglePause(){
      //  for(TruckAgent truck : truckAgents)
           // truck.togglePause();
    }

    public boolean getPause(){
        return graphicInt == null ? false : graphicInt.getPause();
    }

    private boolean getMemory(){
        return memory;
    }

    private void setMemory(boolean memory){
        this.memory = memory;
    }

    public void addTruckAgent(TruckAgent truckAgent) {
        truckAgents.add(truckAgent);
    }
}