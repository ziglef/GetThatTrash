package main;

/**
 * Created by Tiago on 27/11/2014.
 */
public class Position {

    public int x,y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void autoMove() {

        //TODO Movimento dos camiões

    }

    //TODO Equals, toString
    @Override
    public Position clone() {
        return new Position(this.x, this.y);
    }

}
