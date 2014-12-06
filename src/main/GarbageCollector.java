package main;

import agents.InterfaceAgentBDI;
import agents.TruckAgentBDI;

import gui.Interface;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.ThreadSuspendable;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
    private static ArrayList<TruckAgentBDI> truckAgents;
    private ThreadSuspendable t;
    private IExternalAccess ia;
    private IComponentManagementService icms;
    private static GarbageCollector instance;
    public boolean memory = true, communication = true;
    private InterfaceAgentBDI intBDI;

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
    public void launchAgent(String path, CreationInfo info) throws FileNotFoundException {

        BufferedReader fis = new BufferedReader(new FileReader(path));

        if(fis == null){
            System.out.println("nao consigo ler o ficheiro");
        }


        if(InterfaceAgentBDI.intAgent == null){
            System.out.println("Interface is NULL");
        }

        InterfaceAgentBDI.intAgent.deployAgent(path, info);
    }

    public static GarbageCollector getInstance(){
        if(instance == null)
            instance = new GarbageCollector();
        return instance;
    }

    public void addTruck(TruckAgentBDI truck){
        truckAgents.add(truck);
    }

    public Position[] getTrucksLoc(){
        Position[] aux = new Position[truckAgents.size()];
        int i = 0;
        for(TruckAgentBDI truck : truckAgents)
            aux[++i]=truck.getLocation();

        return aux;
    }

    public TruckAgentBDI getTruckByLoc(Position pos){
        for(TruckAgentBDI truck : truckAgents){
            if(truck.getLocation().equals(pos))
                return truck;
        }
        return null;
    }


    public void setInterface(Interface graphicInt){
        if(this.graphicInt == null)
            this.graphicInt = graphicInt;
    }

    public Interface getInterface(){
        return this.graphicInt;
    }

    //TODO steps (List) of truckTrip

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

    public void addTruckAgent(TruckAgentBDI truckAgent) {
        truckAgents.add(truckAgent);
    }

    public ArrayList<TruckAgentBDI> getTruckAgents() {
        return truckAgents;
    }


}