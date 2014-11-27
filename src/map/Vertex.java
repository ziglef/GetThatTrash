package map;

import java.util.HashMap;
import java.util.Map;

public class Vertex {

    private Map<String, String> properties;
    private String name;

    public Vertex(String name){
        this.properties = new HashMap<>();
        this.name = name;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void addProperty(String key, String value) {
        this.properties.put(key, value);
    }

    public String getProperty(String key){
        return this.properties.get(key);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
