package main;

/**
 * Created by Tiago on 27/11/2014.
 */
public class Location {

    public int x,y;

    public Location(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void autoMove() {

        //TODO Movimento dos camiões

    }

    //TODO Equals, toString
    @Override
    public Location clone() {
        return new Location(this.x, this.y);
    }
}
