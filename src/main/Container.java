package main;

import java.util.Random;

public class Container implements Runnable{

    private Position pos;
    private GarbageCollector.typeOfWaste type;
    private int occupiedCapacity, capacity;
    private String id;
    private Thread t;
    private static final long SLEEP = 2500;
    private static final int MAX_VALUE_WASTE_INC = 15;
    private Random rn;


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

    public GarbageCollector.typeOfWaste getType() {
        return type;
    }

    public Position getPosition() {
        return pos;
    }

    public int getOccupiedCapacity() {
        return occupiedCapacity;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setOccupiedCapacity(int ocapacity) {
        this.occupiedCapacity = ocapacity;
    }
}
