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
import java.util.HashMap;
import java.util.Map;

public class GarbageCollector {

    /*------------------------------
        Declarations
    *-----------------------------*/
    private Interface graphicInt;
    private static ArrayList<TruckAgentBDI> truckAgents;
    private static ArrayList<IExternalAccess> truckAgentsExtService;
    private static ArrayList<Container> containers;
    private static ArrayList<Deposit> deposits;
    private ThreadSuspendable t;
    private IExternalAccess ia;
    private IComponentManagementService icms;
    private static GarbageCollector instance;
    public boolean memory = false, communication = false;

    private Map<Integer, Position> clfm;
    private int msgNr = 0;
    private int velocity = 3;

    public int getVelocity() {
        return velocity;
    }

    public void setVelocity(int velocity) {
        this.velocity = velocity;
    }

    public static enum typeOfWaste {
        PAPER, PLASTIC, GLASS, UNDIFFERENTIATED
    }

    /*------------------------------
        Constructors
    *-----------------------------*/
    protected GarbageCollector() {
        truckAgents = new ArrayList<>();
        containers = new ArrayList<>();
        deposits = new ArrayList<>();
        truckAgentsExtService = new ArrayList<>();
        clfm = new HashMap<>();
    }

    /*------------------------------
        Declarations
    *-----------------------------*/
    public void launchAgent(String path, CreationInfo info) throws FileNotFoundException {

        BufferedReader fis = new BufferedReader(new FileReader(path));

        if (fis == null) {
            System.out.println("nao consigo ler o ficheiro");
        }

        if (InterfaceAgentBDI.intAgent == null) {
            System.out.println("Interface is NULL");
        }

        InterfaceAgentBDI.intAgent.deployAgent(path, info);
    }

    public static GarbageCollector getInstance() {
        if (instance == null)
            instance = new GarbageCollector();
        return instance;
    }

    public void addTruck(TruckAgentBDI truck) {
        truckAgents.add(truck);
    }

    public Position[] getTrucksLoc() {
        Position[] aux = new Position[truckAgents.size()];
        int i = 0;
        for (TruckAgentBDI truck : truckAgents) {
            aux[i] = truck.getPosition();
            i++;
        }
        return aux;
    }

    public Position[] getContainerLoc() {

        Position[] aux = new Position[containers.size()];
        int i = 0;
        for (Container container : containers) {
            aux[i] = container.getPosition();
            i++;
        }

        return aux;
    }

    public Position[] getDepositsLoc() {

        Position[] aux = new Position[deposits.size()];
        int i = 0;
        for (Deposit deposit : deposits) {
            aux[i] = deposit.getPosition();
            i++;
        }

        return aux;
    }

    public TruckAgentBDI getTruckByLoc(Position pos) {
        for (TruckAgentBDI truck : truckAgents) {
            if (truck.getPosition().equals(pos))
                return truck;
        }
        return null;
    }

    public Container getContainerByPos(Position pos) {
        for (Container container : containers) {
            if (container.getPosition().equals(pos))
                return container;
        }
        return null;
    }

    public ArrayList<Container> checkAdjacentContainerPos(Position pos) {

        ArrayList<Container> aux = new ArrayList<>();
        for (Container container : containers) {
            if (container.getPosition().equals(new Position(pos.x, pos.y - 1)))
                aux.add(container);
            if (container.getPosition().equals(new Position(pos.x, pos.y + 1)))
                aux.add(container);
            if (container.getPosition().equals(new Position(pos.x - 1, pos.y)))
                aux.add(container);
            if (container.getPosition().equals(new Position(pos.x + 1, pos.y)))
                aux.add(container);
        }
        return aux;
    }

    public ArrayList<Deposit> checkAdjacentDepositPos(Position pos) {

        ArrayList<Deposit> aux = new ArrayList<>();
        for (Deposit deposit : deposits) {
            if (deposit.getPosition().equals(new Position(pos.x, pos.y - 1)))
                aux.add(deposit);
            if (deposit.getPosition().equals(new Position(pos.x, pos.y + 1)))
                aux.add(deposit);
            if (deposit.getPosition().equals(new Position(pos.x - 1, pos.y)))
                aux.add(deposit);
            if (deposit.getPosition().equals(new Position(pos.x + 1, pos.y)))
                aux.add(deposit);
        }
        return aux;
    }

    public Deposit getDepositByPos(Position pos) {
        for (Deposit deposit : deposits) {
            if (deposit.getPosition().equals(pos))
                return deposit;
        }
        return null;
    }

    public Deposit checkDepositPos(Position pos) {
        for (Deposit deposit : deposits) {
            if (deposit.getPosition().equals(new Position(pos.x, pos.y - 1)))
                return deposit;
            if (deposit.getPosition().equals(new Position(pos.x, pos.y + 1)))
                return deposit;
            if (deposit.getPosition().equals(new Position(pos.x - 1, pos.y)))
                return deposit;
            if (deposit.getPosition().equals(new Position(pos.x + 1, pos.y)))
                return deposit;
        }
        return null;
    }

    public void setInterface(Interface graphicInt) {
        if (this.graphicInt == null)
            this.graphicInt = graphicInt;
    }

    public Interface getInterface() {
        return this.graphicInt;
    }

    public void togglePause() {
        //  for(TruckAgent truck : truckAgents)
        // truck.togglePause();
    }

    public boolean getPause() {
        return graphicInt != null && graphicInt.getPause();
    }


    public boolean getMemory() {
        return memory;
    }


    public void setMemory(boolean memory) {
        this.memory = memory;
    }

    public void addTruckAgent(TruckAgentBDI truckAgent) {
        truckAgents.add(truckAgent);
    }

    public ArrayList<TruckAgentBDI> getTruckAgents() {
        return truckAgents;
    }

    public ArrayList<Container> getContainers() {
        return containers;
    }

    public void addContainers(Container container) {
        containers.add(container);
    }

    public ArrayList<Deposit> getDeposits() {
        return deposits;
    }

    public void addDeposit(Deposit deposit) {
        deposits.add(deposit);
    }

    public void setCollectorOcuppiedCapacity(Position pos, int capacity) {
        getContainerByPos(pos).setOccupiedCapacity(capacity);
    }

    public void setDepositOcuppiedCapacity(Position pos, int capacity) {
        getContainerByPos(pos).setOccupiedCapacity(capacity);
    }

    public void addExternalAccess(final IExternalAccess externalAccess) {
        truckAgentsExtService.add(externalAccess);
    }

    public void restartCity() {

        for (IExternalAccess agentIEA : truckAgentsExtService)
            agentIEA.killComponent();

        truckAgents.clear();
        deposits.clear();
        containers.clear();
        truckAgentsExtService.clear();

    }

    public boolean getCommunication() {
        return communication;
    }

    public void setCommunication(boolean communication) {
        this.communication = communication;
    }

    public Position getClfmGivingNr(Integer nr) {
        return clfm.get(nr);
    }

    public void setClfm(int msgNr, Position position) {
        clfm.put(msgNr, position);
    }

    public int getMsgNr(boolean inc) {
        if(inc)  msgNr++;

        return msgNr;
    }
}