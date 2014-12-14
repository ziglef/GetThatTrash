package main;

import agents.TruckAgentBDI;
import gui.Interface;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.ThreadSuspendable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Class responsible for connecting all the components between
 * the frontend and the backend
 *
 * @author Rui Grandão  - ei11010@fe.up.pt
 * @author Tiago Coelho - ei11012@fe.up.pt
 */
public class GarbageCollector {

    /*------------------------------
        Declarations
    *-----------------------------*/
    private Interface graphicInt;
    private static ArrayList<TruckAgentBDI> truckAgents;
    private static ArrayList<IExternalAccess> truckAgentsExtService;
    private static ArrayList<Container> containers;
    private static ArrayList<Deposit> deposits;
    private static GarbageCollector instance;
    public boolean memory = false, communication = false;
    private int totalUndWaste = 0, totalGlassWaste = 0, totalPlasticWaste = 0, totalPaperWaste = 0;
    private Map<Integer, Position> clfm;
    private int msgNr = 0, velocity = 3;
    private boolean generateStatistics = false;

    /*------------------------------
        Constructors
    *-----------------------------*/

    /**
     * Default constructor of GarbageCollector
     */
    protected GarbageCollector() {
        truckAgents = new ArrayList<>();
        containers = new ArrayList<>();
        deposits = new ArrayList<>();
        truckAgentsExtService = new ArrayList<>();
        clfm = new HashMap<>();
    }

    /**
     * Method thar returns the instance of GarbageCollector
     *
     * @return - GarbageCollector Instance
     */
    public static GarbageCollector getInstance() {
        if (instance == null)
            instance = new GarbageCollector();
        return instance;
    }

    /**
     * Method that returns an array with all Trucks Positions
     *
     * @return - array os Position
     */
    public Position[] getTrucksLoc() {
        Position[] aux = new Position[truckAgents.size()];
        int i = 0;
        for (TruckAgentBDI truck : truckAgents) {
            aux[i] = truck.getPosition();
            i++;
        }
        return aux;
    }

    /**
     * Method that returns an array with all Containers Positions
     *
     * @return - array os Position
     */
    public Position[] getContainerLoc() {

        Position[] aux = new Position[containers.size()];
        int i = 0;
        for (Container container : containers) {
            aux[i] = container.getPosition();
            i++;
        }

        return aux;
    }

    /**
     * Method that returns an array with all Deposits Positions
     *
     * @return - array os Position
     */
    public Position[] getDepositsLoc() {

        Position[] aux = new Position[deposits.size()];
        int i = 0;
        for (Deposit deposit : deposits) {
            aux[i] = deposit.getPosition();
            i++;
        }

        return aux;
    }

    /**
     * Method that check all the adjacent containers given one position.
     *
     * @param pos - position to check
     * @return - arraylist with adjacent containers, null if dont have
     * adjacents
     */
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

    /**
     * Method that check all the adjacent deposits  given one position.
     *
     * @param pos - position to check
     * @return - arraylist with adjacent deposits, null if dont have
     * adjacents
     */
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

    /**
     * Method that set the interface
     *
     * @param graphicInt - the graphic interface
     */
    public void setInterface(Interface graphicInt) {
        if (this.graphicInt == null)
            this.graphicInt = graphicInt;
    }

    /**
     * Method that returns the Interface
     *
     * @return - the interface
     */
    public Interface getInterface() {
        return this.graphicInt;
    }

    /**
     * Method that returns the pause state of the program
     *
     * @return true if is pause, false otherwise
     */
    public boolean getPause() {
        return graphicInt != null && graphicInt.getPause();
    }

    /**
     * Method that returns if memory is being used
     *
     * @return true if yes, false otherwise
     */
    public boolean getMemory() {
        return memory;
    }

    /**
     * Method that set memory value
     *
     * @param memory - true or false
     */
    public void setMemory(boolean memory) {
        this.memory = memory;
    }

    /**
     * Method that add a new truck agent to de app
     *
     * @param truckAgent - the truck agent
     */
    public void addTruckAgent(TruckAgentBDI truckAgent) {
        truckAgents.add(truckAgent);
    }

    /**
     * Method that returns all the truck agents in the app
     * @return - arraylist with all the trucks
     */
    public ArrayList<TruckAgentBDI> getTruckAgents() {
        return truckAgents;
    }

    /**
     * Method thar returns all the containers in the app
     * @return arraylist with all the containers
     */
    public ArrayList<Container> getContainers() {
        return containers;
    }

    /**
     * Method that add a new container to the app
     * @param container  - the container
     */
    public void addContainers(Container container) {
        containers.add(container);
    }

    /**
     * Method thar returns all the deposits in the app
     * @return arraylist with all the deposits
     */
    public ArrayList<Deposit> getDeposits() {
        return deposits;
    }

    /**
     * Method that add a new deposit to the app
     * @param deposit  - the deposit
     */
    public void addDeposit(Deposit deposit) {
        deposits.add(deposit);
    }

    /**
     * Method that add externel acess for all agents.
     *
     * @param externalAccess
     */
    public void addExternalAccess(final IExternalAccess externalAccess) {
        truckAgentsExtService.add(externalAccess);
    }

    /**
     * Method thar restart the city to create a new one,
     * clear all the components and kill all truck agents.
     */
    public void restartCity() {

        for (IExternalAccess agentIEA : truckAgentsExtService)
            agentIEA.killComponent();

        truckAgents.clear();
        deposits.clear();
        containers.clear();
        truckAgentsExtService.clear();

    }

    /**
     * Method that returns if communication is being used
     * @return true if yes, false otherwise
     */
    public boolean getCommunication() {
        return communication;
    }

    /**
     * Method that set communication value
     * @param communication - true or false
     */
    public void setCommunication(boolean communication) {
        this.communication = communication;
    }

    /**
     * Method that returns a message given the ID
     * @param nr - the ID
     * @return - Position of the component on that message
     */
    public Position getClfmGivingNr(Integer nr) {
        return clfm.get(nr);
    }

    /**
     * Method that sets a new message and position
     * @param msgNr - message ID
     * @param position - position of the component
     */
    public void setClfm(int msgNr, Position position) {
        clfm.put(msgNr, position);
    }

    /**
     * Method that returns the new messagem NR
     * @param inc - true if is to inc, false otherwise
     * @return - message number
     */
    public int getMsgNr(boolean inc) {
        if(inc)  msgNr++;

        return msgNr;
    }

    /**
     * Method that returns the total undifferentiadted
     * waste deposit to use on Chart
     * @return totalUndWaste
     */
    public int getTotalUndWaste() {
        return totalUndWaste;
    }

    /**
     * Method that returns the total glass
     * waste deposit to use on Chart
     * @return totalGlassWaste
     */
    public int getTotalGlassWaste() {
        return totalGlassWaste;
    }

    /**
     * Method that returns the total plastic
     * waste deposit to use on Chart
     * @return totalplasticwaste
     */
    public int getTotalPlasticWaste() {
        return totalPlasticWaste;
    }

    /**
     * Method that returns the total paper
     * waste deposit to use on Chart
     * @return totalpaperwaste
     */
    public int getTotalPaperWaste() {
        return totalPaperWaste;
    }

    /**
     * MEthod thart sets the value of generate statistics
     *
     * @param generateStatistics - true or false
     */
    public void setGenerateStatistics(boolean generateStatistics) {
        this.generateStatistics = generateStatistics;
    }

    /**
     * Method that returns the actual velocity of the city
     * @return the velocity
     */
    public int getVelocity() {
        return velocity * 50;
    }

    /**
     * Method that sets the new velocity of the city
     * @param velocity - the velocity
     */
    public void setVelocity(int velocity) {
        this.velocity = velocity;
    }

    /**
     * Method that returns if statistis are being generated
     * @return
     */
    public boolean getGenerateStatistics() {
        return generateStatistics;
    }

    /**
     * Method that set totalWaste deposit given the type
     * @param type - type of waste
     * @param occupiedCapacity - capacity droped
     */
    public void setWasteQuantity(typeOfWaste type, int occupiedCapacity) {
        if(type == typeOfWaste.GLASS) totalGlassWaste+=occupiedCapacity;
        if(type == typeOfWaste.PAPER) totalPaperWaste+=occupiedCapacity;
        if(type == typeOfWaste.UNDIFFERENTIATED) totalUndWaste+=occupiedCapacity;
        if(type == typeOfWaste.PLASTIC) totalPlasticWaste+=occupiedCapacity;
    }

    /**
     * Enum that represents the type of waste present on the app
     */
    public static enum typeOfWaste {
        PAPER, PLASTIC, GLASS, UNDIFFERENTIATED
    }
}