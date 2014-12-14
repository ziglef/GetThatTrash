package main;

import java.util.Random;

/**
 * Class responsible for creating a container
 *
 * @author Rui Grandão  - ei11010@fe.up.pt
 * @author Tiago Coelho - ei11012@fe.up.pt
 * @see Runnable
 */
public class Container implements Runnable{

    private Position pos;
    private GarbageCollector.typeOfWaste type;
    private int occupiedCapacity, capacity;
    private String id;
    private Thread t;
    private static final long SLEEP = 2500;
    private static final int MAX_VALUE_WASTE_INC = 15;
    private Random rn;


    /**
     * Constructor of a container
     *
     * @param id - string identifier of a container
     * @param pos - position of the container
     * @param type - type of the container
     * @param capacity - capacity of the container
     */
    public Container(String id, Position pos, GarbageCollector.typeOfWaste type, int capacity){
        this.id = id;
        this.pos = pos;
        this.type = type;
        this.capacity = capacity;
        this.occupiedCapacity = 0;
        this.rn = new Random();
        this.t = new Thread(this);
        this.t.start();
        GarbageCollector.getInstance().addContainers(this);
    }

    /**
     * Thread that increment the garbage on a container
     */
    @Override
    public void run() {

        int increment;

        while(true) {

            try {
                t.sleep(SLEEP / GarbageCollector.getInstance().getVelocity());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(!GarbageCollector.getInstance().getPause()) {
                increment = rn.nextInt(MAX_VALUE_WASTE_INC + 1);
                if (occupiedCapacity < capacity) {
                    if (occupiedCapacity + increment > capacity)
                        occupiedCapacity = capacity;
                    else
                        occupiedCapacity += increment;
                }
            }
        }
    }

    /**
     * Method thar returns the container type
     *
     * @return - container type
     */
    public GarbageCollector.typeOfWaste getType() {
        return type;
    }

    /**
     * Method thar returns the container position
     *
     * @return - container position
     */
    public Position getPosition() {
        return pos;
    }

    /**
     * Method thar returns the actual occupied capacity of the container
     *
     * @return - container occupiedcapacity
     */
    public int getOccupiedCapacity() {
        return occupiedCapacity;
    }

    /**
     * Method thar returns the container total capacity
     *
     * @return - container capacity
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Method thar sets the occupied capacity of the container
     *
     * @param ocapacity - capacity to set on occupiedCapacity
     */
    public void setOccupiedCapacity(int ocapacity) {
        this.occupiedCapacity = ocapacity;
    }
}
