package RouteSearch;

import Enums.TravelType;
import Helpers.HelperFunctions;
import Helpers.LongToPointMap;
import KDtree.KDTree;
import Model.Elements.Element;
import Model.Elements.Road;
import Model.Model;
import OSM.OSMWay;

import java.awt.geom.Point2D;
import java.util.*;

/**
 * Class details:
 *
 * @author Andreas Blanke, blan@itu.dk
 * @version 27-04-2017
 */
public class GraphFactory {

    private List<Edge> roadSegments;
    private Map<Point2D,LinkedList<Road>> adjacent;
    private Graph graph;
    private LongToPointMap points;

    public GraphFactory(KDTree roads) {
        if (roads == null) throw new NullPointerException("KDTree has not been initialized");
        Model model = Model.getInstance();
        HashSet<Element> roadSet = roads.getManySections(model.getMinLongitude(false),model.getMinLatitude(false),
                model.getMaxLongitude(false),model.getMaxLatitude(false));
        roadSegments = new LinkedList<>();
        adjacent = new HashMap<>();
        points = new LongToPointMap(Integer.MAX_VALUE);
        makeGraph(roadSet);
    }

    private void makeGraph(HashSet<Element> roads) {
        for (Object way: roads) {
            if (way instanceof Road) {
                Road road = (Road) way;

                // Adds all points to the adjacency list
                for (OSMWay osmWay: road.getRelation()) {
                    Point2D lastPoint = osmWay.getFromNode();
                    points.put(lastPoint.hashCode(),(float) lastPoint.getX(),
                            (float) lastPoint.getY());

                    for (int i = 1; i < osmWay.size(); i++) {
                        Point2D point = osmWay.get(i);
                        addRoadToAdjacent(point,road);
                        points.put(point.hashCode(),(float) lastPoint.getX(),
                                (float) lastPoint.getY());

                        float length = (float) HelperFunctions.distanceInMeters(point,lastPoint);
                        if (road.isTravelByBikeAllowed()) {
                            roadSegments.add(new Edge(lastPoint.hashCode(),point.hashCode(),
                                    road.getMaxSpeed(),length,TravelType.BICYCLE));
                            if (!road.isOneWay()) {
                                roadSegments.add(new Edge(point.hashCode(),lastPoint.hashCode(),
                                        road.getMaxSpeed(),length,TravelType.BICYCLE));
                            }
                        }
                        if (road.isTravelByFootAllowed()) {
                            roadSegments.add(new Edge(lastPoint.hashCode(),point.hashCode(),
                                    road.getMaxSpeed(),length,TravelType.WALK));
                            if (!road.isOneWay()) {
                                roadSegments.add(new Edge(point.hashCode(),lastPoint.hashCode(),
                                        road.getMaxSpeed(),length,TravelType.WALK));
                            }
                        }
                        if (road.isTravelByCarAllowed()) {
                            roadSegments.add(new Edge(lastPoint.hashCode(),point.hashCode(),
                                    road.getMaxSpeed(),length,TravelType.VEHICLE));
                            if (!road.isOneWay()) {
                                roadSegments.add(new Edge(point.hashCode(),lastPoint.hashCode(),
                                        road.getMaxSpeed(),length,TravelType.VEHICLE));
                            }
                        }
                        lastPoint = point;
                    }
                }
            } // else ignore
        }
        graph = new Graph(roadSegments.size());
        for (Edge edge : roadSegments) {
            graph.addEdge(edge);
        }
    }

    private void addRoadToAdjacent(Point2D point, Road road) {
        if (adjacent.containsKey(point)) {
            adjacent.get(point).add(road);
        } else {    // make new adjacency list
            LinkedList<Road> adj = new LinkedList<>();
            adj.add(road);
            adjacent.put(point,adj);
        }
    }

    public Graph getGraph() {
        return graph;
    }

    public LongToPointMap getPointMap() {
        return points;
    }

    public Map<Point2D, LinkedList<Road>> getAdjacent() {
        return adjacent;
    }
}
