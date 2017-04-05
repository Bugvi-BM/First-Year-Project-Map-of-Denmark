package Model.Coastlines;

import Enums.BoundType;
import Enums.ZoomLevel;
import Helpers.GlobalValue;
import Model.Model;
import OSM.OSMNode;
import OSM.OSMWay;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Class details:
 *
 * @author Andreas Blanke, blan@itu.dk
 * @version 23-03-2017.
 *
 * @project BFST
 */
public class Coastline extends OSMWay {

    public static final String OSM_IDENTIFIER = "coastline";

    public Path2D toPath2D(float longFactor) {
        Path2D path = new Path2D.Float();
        Point2D node = this.getFromNode();
        path.moveTo(node.getX()*longFactor, node.getY());

        // Draws all points
        for(int i = 1 ; i < size() ; i += ZoomLevel.getNodesAtMaxLevel()){
            node = this.get(i);
            boolean isNear = isNodeNearCamera(node);
            while(isNear) {
                path.lineTo(node.getX()*longFactor, node.getY());
                int modifier = GlobalValue.getZoomLevel().getNodesAtLevel();
                i += modifier;
                if (i >= size()) break;
                node = this.get(i);
                isNear = isNodeNearCamera(node);
            }
            path.lineTo(node.getX()*longFactor, node.getY());
        }
        node = this.getFromNode();
        path.lineTo(node.getX()*longFactor, node.getY());
        return path;
    }

    private boolean isNodeNearCamera(Point2D node) {
        boolean nodeIsNear = false;
        Model model = Model.getInstance();
        float minlon = model.getCameraBound(BoundType.MIN_LONGITUDE);
        float maxlon = model.getCameraBound(BoundType.MAX_LONGITUDE);
        float minlat = model.getCameraBound(BoundType.MIN_LATITUDE);
        float maxlat = model.getCameraBound(BoundType.MAX_LATITUDE);

        if (minlon <= node.getX() && maxlon >= node.getX() &&
                minlat >= node.getY() && maxlat <= node.getY()) {
            nodeIsNear = true;
        }

        if(nodeIsNear) {
            System.out.println("minlon: "+minlon+" maxlon: "+maxlon);
            System.out.println("minlat: "+minlat+" maxlat: "+maxlat);
            System.out.println("Node at x=" + node.getX() + " y= " + node.getY() + " is near=" + nodeIsNear);
        }

        return nodeIsNear;
    }

}
