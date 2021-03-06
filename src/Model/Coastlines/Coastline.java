package Model.Coastlines;

import Enums.BoundType;
import Enums.ZoomLevel;
import Helpers.GlobalValue;
import Helpers.HelperFunctions;
import Model.Model;
import OSM.OSMWay;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Class details:
 * An object representing a coastline. A Coastline is an ordered
 * collection of points that when creating paths between those points
 * a consisting coastline is generated. The generation method for
 * these paths is determined by the current view as well as the
 * current zoom level. The path itself is either generated by only
 * adding every 25th point to the path or by using Douglas-Peucker
 * algorithm for path generalization.
 *
 * Note that for coastline to function without error, then the
 * collection HAS TO BE IN PERFECT ORDER or else its path will be
 * displayed incorrectly.
 *
 * @see HelperFunctions pathGeneralization()
 */
public class Coastline extends OSMWay {

    // osm identifier name
    static final String OSM_IDENTIFIER = "coastline";

    /**
     * Generates a semi-complete path of the coastline
     * using either a quick generation method or a quality
     * generation method.
     */
    public Path2D toPath2D()
    {
        // Setup
        Path2D path = new Path2D.Float();
        Point2D node = this.getFromNode();
        path.moveTo(node.getX(), node.getY());

        if (GlobalValue.getZoomLevel() != ZoomLevel.LEVEL_6) {
            // Go through the collection of points and create a path
            int lastI = 0;
            int increase = ZoomLevel.getNodesAtMaxLevel();
            for (int i = 0; i < size() - 1; ) {
                i += increase;
                if (i >= size())
                    i = size() - 1;
                node = get(lastI);
                boolean isFromNear = isNodeNearCamera(node);
                path.lineTo(node.getX(), node.getY());
                node = get(i);
                boolean isToNear = isNodeNearCamera(node);
                if (isToNear || isFromNear) {
                    path.append(qualityGeneratePath(lastI, i, ZoomLevel.LEVEL_6), true);
                } else {
                    path.append(quickGeneratePath(lastI, i,
                            ZoomLevel.getNodesAtMaxLevel()), true);
                }
                lastI = i;
            }
        } else {
            path.append(quickGeneratePath(0,this.size()-1,21),true);
        }

        // Finish path (loop back)
        path.closePath();
        return path;
    }

    /**
     * Generates a segment of coastline using the Douglas-Peucker
     * algorithm and returns the corresponding path for that segment
     * @param startpoint Index of start point, including
     * @param endpoint Index of end point, including
     * @param level The current zoomlevel
     * @see HelperFunctions pathGeneralization() for recursive method
     */
    private Path2D qualityGeneratePath(int startpoint, int endpoint,
        ZoomLevel level)
    {
        Path2D path = new Path2D.Float();
        // Copy array
        List<Point2D> copy = new ArrayList<>();
        for (int i = startpoint; i <= endpoint; i++) {
            copy.add(this.get(i));
        }

        // Generate simplified path
        double epsilon = level.getEpsilonValueBasedOnZoomLevel();
        List<Point2D> newPoints = HelperFunctions.pathGeneralization(copy, epsilon);

        // Add start point
        Point2D start = newPoints.get(0);
        path.moveTo(start.getX(), start.getY());

        // Add generalized points
        for (int i = 1; i < newPoints.size(); i++) {
            Point2D point = newPoints.get(i);
            path.lineTo(point.getX(), point.getY());
        }
        return path;
    }

    /**
     * Generates a segment of the coastline using a "quick" method
     * that adds every 25th or so point to a path and returns that
     * path.
     * @param start The start point, including
     * @param end The end point, including
     */
    private Path2D quickGeneratePath(int start, int end, int increase)
    {
        Path2D path = new Path2D.Float();
        // Add start point
        Point2D startPoint = this.get(start);
        path.moveTo(startPoint.getX(), startPoint.getY());

        // Add points
        for (int i = start; i < end;
             i += increase) {
            Point2D point = this.get(i);
            path.lineTo(point.getX(), point.getY());
        }

        // Add end point
        Point2D endPoint = this.get(end);
        path.lineTo(endPoint.getX(), endPoint.getY());
        return path;
    }

    /**
     * Checks whether a Point / Node is within the current view
     */
    private boolean isNodeNearCamera(Point2D node)
    {
        boolean nodeIsNear = false;
        Model model = Model.getInstance();
        float buffer = 0.2f;
        float minlon = model.getCameraBound(BoundType.MIN_LONGITUDE) - buffer;
        float maxlon = model.getCameraBound(BoundType.MAX_LONGITUDE) + buffer;
        float minlat = model.getCameraBound(BoundType.MIN_LATITUDE) + buffer;
        float maxlat = model.getCameraBound(BoundType.MAX_LATITUDE) - buffer;

        if (minlon <= node.getX() && maxlon >= node.getX() && minlat >= node.getY() && maxlat <= node.getY()) {
            nodeIsNear = true;
        }

        return nodeIsNear;
    }

    /**
     * Creates a string representing the coastline, e.g. with
     * coordinates for each point in its path and the total amount
     * of points.
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Coastline of size: ");
        sb.append(size());
        sb.append("\n");
        for (int i = 0; i < size(); i++) {
            Point2D point = get(i);
            sb.append("(");
            sb.append(point.getX());
            sb.append("; ");
            sb.append(-point.getY());
            sb.append(")");
            sb.append("\n");
        }
        sb.append("\n");
        return sb.toString();
    }
}
