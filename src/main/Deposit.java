package main;


import java.util.Random;

public class Deposit implements Runnable {

    private String id;
    private GarbageCollector.typeOfWaste type;
    private Thread t;
    private int occupiedCapacity;
    private boolean pause;
    private static final long SLEEP = 2000;
    private static final int MAX_VALUE_WASTE_DEC = 25;
    private Random rn;
    private Position pos;

    public Deposit(String id, Position pos, GarbageCollector.typeOfWaste type) {
        this.id = id;
        this.type = type;
        this.occupiedCapacity = 500;
        this.pause = false;
        this.pos = pos;
        this.rn = new Random();
        t = new Thread(this);
        t.start();
        GarbageCollector.getInstance().addDeposit(this);
    }


    @Override
    public void run() {

        int decrement;

        while (!pause) {

            try {
                t.sleep(SLEEP);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            decrement = rn.nextInt(MAX_VALUE_WASTE_DEC + 1);
            if (occupiedCapacity < 0)
                occupiedCapacity = 0;
            else
                occupiedCapacity -= decrement;
            System.out.println("OccupiedCapacity:" + occupiedCapacity);
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

}
