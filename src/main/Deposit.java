package main;

import java.util.Random;

public class Deposit implements Runnable {

    private String id;
    private GarbageCollector.typeOfWaste type;
    private Thread t;
    private int occupiedCapacity;
    private static final long SLEEP = 2000;
    private static final int MAX_VALUE_WASTE_DEC = 50;
    private Random rn;
    private Position pos;

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

    public Position getPosition() {
        return pos;
    }

    public GarbageCollector.typeOfWaste getType() {
        return type;
    }

    public int getOccupiedCapacity() {
        return occupiedCapacity;
    }

    public void setOccupiedCapacity(int ocapacity) {
        this.occupiedCapacity = ocapacity;
    }

}
