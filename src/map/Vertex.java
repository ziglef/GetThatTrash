package map;

import main.Position;

import java.util.HashMap;
import java.util.Map;

/**
 * Class responsible for treat Vertex
 *
 * @author Rui Grandão  - ei11010@fe.up.pt
 * @author Tiago Coelho - ei11012@fe.up.pt
 */
public class Vertex {

    private Map<String, String> properties;
    private String name;
    private int x, y;

    /**
     *
     * @param name
     */
    public Vertex(String name){
        this.properties = new HashMap<>();
        this.name = name;

        this.x = -1;
        this.y = -1;
    }

    /**
     *
     * @return
     */
    public Map<String, String> getProperties() {
        return properties;
    }

    /**
     *
     * @param key
     * @param value
     */
    public void addProperty(String key, String value) {
        if( key.equals("coords") ){
            this.x = Integer.parseInt(value.split(",")[0]);
            this.y = Integer.parseInt(value.split(",")[1]);
        }

        this.properties.put(key, value);
    }

    /**
     *
     * @param key
     * @return
     */
    public String getProperty(String key){
        return this.properties.get(key);
    }

    /**
     *
     * @return
     */
    public String getName() {
        return this.name;
    }

    /**
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     */
    public int getX() {
        return this.x;
    }

    /**
     *
     * @param x
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     *
     * @return
     */
    public int getY() {
        return this.y;
    }

    /**
     *
     * @return
     */
    public Position getPosition() {
        return new Position(this.x, this.y);
    }

    /**
     *
     * @param y
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     *
     * @param x
     * @param y
     * @return
     */
    public boolean isAt(int x, int y){
        if( this.x == x && this.y == y )
            return true;
        return false;
    }

    /**
     *
     * @return
     */
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
