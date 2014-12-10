package map;

import main.Position;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Vertex {

    private Map<String, String> properties;
    private String name;
    private int x, y;

    public Vertex(String name){
        this.properties = new HashMap<>();
        this.name = name;

        this.x = -1;
        this.y = -1;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void addProperty(String key, String value) {
        if( key.equals("coords") ){
            this.x = Integer.parseInt(value.split(",")[0]);
            this.y = Integer.parseInt(value.split(",")[1]);
        }

        this.properties.put(key, value);
    }

    public String getProperty(String key){
        return this.properties.get(key);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public Position getPosition() {
        return new Position(this.x, this.y);
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isAt(int x, int y){
        if( this.x == x && this.y == y )
            return true;
        return false;
    }

    public String toString(){
        String s = "";

        s += this.name + ": \n";
        for( Map.Entry<String, String> ps : this.properties.entrySet() ){
            s += ps.getKey() + ": " + ps.getValue() + "\n";
        }

        s += "x" + ": " + this.x + "\n";
        s += "y" + ": " + this.y + "\n";

        return s;
    }

}
