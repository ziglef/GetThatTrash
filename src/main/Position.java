package main;

public class Position {

    public int x,y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

<<<<<<< HEAD
=======
    public boolean equals(Position pos){
        return (pos.x == this.x && pos.y == this.y);
    }

    public String toString(){
        String s = "";
        s += "x" + ": " + this.x + "\n";
        s += "y" + ": " + this.y + "\n";
        return s;
    }

>>>>>>> ce3149a5f35f666f5360f35e62ce157782750d3c
}
