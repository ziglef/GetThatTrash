package main;

/**
 * Class responsible for treat the Position of
 * each element in the city
 *
 * @author Rui Grandão  - ei11010@fe.up.pt
 * @author Tiago Coelho - ei11012@fe.up.pt
 */
public class Position {

    public int x,y;

    /**
     * Constructor of position
     * @param x - x value
     * @param y - y value
     */
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Method that checks if a given positon are equals to this one
     * @param pos - position to compare
     * @return - true if are equal, false otherwise
     */
    public boolean equals(Position pos){
        return (pos.x == this.x && pos.y == this.y);
    }

    /**
     * Method that converts one Position to string
     *
     * @return - the string representing the Position
     */
    public String toString(){
        String s = "";
        s += "x" + ": " + this.x + "\n";
        s += "y" + ": " + this.y + "\n";
        return s;
    }

}
