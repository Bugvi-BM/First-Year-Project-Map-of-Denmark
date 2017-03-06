package Model;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * Created by Jakob on 06-03-2017.
 */
public final class Model extends Observable {
    private List<Road> roads;
    private List<Shape> unknown;
    private static Model model;

    private Model(){
        roads = new ArrayList<>();
        unknown = new ArrayList<>();
    }

    public static Model getInstance() {
        if(model == null) {
            model = new Model();
        }
        return model;
    }

    public void addRoad(Road road){
        roads.add(road);
    }

    public List<Road> getRoads(){
        return roads;
    }

    public void addUnknown(Shape shape){
        unknown.add(shape);
    }

    public List<Shape> getUnknown(){
        return unknown;
    }

    public void clear() {
        roads.clear();
        unknown.clear();
    }
}
