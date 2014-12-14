package main;

import java.util.Random;

/**
 * Class responsible for creating a deposit
 *
 * @author Rui Grandão  - ei11010@fe.up.pt
 * @author Tiago Coelho - ei11012@fe.up.pt
 * @see Runnable
 */
public class Deposit implements Runnable {

    private String id;
    private GarbageCollector.typeOfWaste type;
    private Thread t;
    private int occupiedCapacity;
    private static final long SLEEP = 2000;
    private static final int MAX_VALUE_WASTE_DEC = 50;
    private Random rn;
    private Position pos;

    /**
     * Constructor of a Deposit.
     *
     * @param id - string identifier of the deposit
     * @param pos - position of the deposit
     * @param type - type of the deposit
     */
    public Deposit(String id, Position pos, GarbageCollector.typeOfWaste type) {
        this.id = id;
        this.type = type;
        this.occupiedCapacity = 0;
        this.pos = pos;
        this.rn = new Random();
        t = new Thread(this);
        t.start();
        GarbageCollector.getInstance().addDeposit(this);
    }

    /**
     * Thread that decrement the deposit garbage, simmulating reciclying
     */
    @Override
    public void run() {

        int decrement;

        while (true) {

            try {
                t.sleep(SLEEP / GarbageCollector.getInstance().getVelocity());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(!GarbageCollector.getInstance().getPause()) {
                decrement = rn.nextInt(MAX_VALUE_WASTE_DEC + 1);
                if (occupiedCapacity <= decrement)
                    occupiedCapacity = 0;
                else
                    occupiedCapacity -= decrement;
            }
        }
    }

    /**
     * Method that returns the Deposit Position
     *
     * @return - deposit position
     */
    public Position getPosition() {
        return pos;
    }

    /**
     * Method thar returns the Deposit type
     * @return - type of deposit
     */
    public GarbageCollector.typeOfWaste getType() {
        return type;
    }


    /**
     * Method thart returs the actual occupied capacity of deposit
     * @return - occupiedcapacity
     */
    public int getOccupiedCapacity() {
        return occupiedCapacity;
    }

    /**
     * Method that sets the occupied capacity of deposit
     *
     * @param ocapacity - capacity to be added to occupiedcapacity
     */
    public void setOccupiedCapacity(int ocapacity) {
        this.occupiedCapacity = ocapacity;
    }

}
