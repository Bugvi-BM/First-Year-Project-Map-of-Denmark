package Model.Elements;

import Helpers.Shapes.PolygonApprox;

/**
 * Created by Jakob on 04-05-2017.
 */
public class Rail extends Element {
    private boolean isInTunnel;

    public Rail(PolygonApprox polygon, boolean isInTunnel){
        super(polygon);
        this.isInTunnel = isInTunnel;
    }
    public PolygonApprox getShape() { return (PolygonApprox)super.getShape(); }

    public boolean isInTunnel() {
        return isInTunnel;
    }
}