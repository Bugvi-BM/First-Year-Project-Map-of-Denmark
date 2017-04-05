package Enums;

/**
 * Class details:
 *
 * @author Andreas Blanke, blan@itu.dk
 * @version 27-03-2017.
 * @project BFST
 */
public enum ZoomLevel {
    LEVEL_0(1),    // close and detailed
    LEVEL_1(1),
    LEVEL_2(2),
    LEVEL_3(4),
    LEVEL_4(9),
    LEVEL_5(16),
    LEVEL_6(25);    // abstract far away

    private int nodesAtLevel;
    private static double zoom_factor;

    ZoomLevel(int nodesAtLevel) {
        this.nodesAtLevel = nodesAtLevel;
    }

    public static ZoomLevel getZoomLevel() {
        if (zoom_factor <= 140) {           // LEVEL_6
            return ZoomLevel.LEVEL_6;
        } else if (zoom_factor <= 200) {    // LEVEL_5
            return ZoomLevel.LEVEL_5;
        } else if (zoom_factor <= 250) {    // LEVEL_4
            return ZoomLevel.LEVEL_4;
        } else if (zoom_factor <= 290) {    // LEVEL_3
            return ZoomLevel.LEVEL_3;
        } else if (zoom_factor <= 330) {    // LEVEL_2
            return ZoomLevel.LEVEL_2;
        } else if (zoom_factor <= 350) {    // LEVEL_1
            return ZoomLevel.LEVEL_1;
        } else {                            // LEVEL_0
            return ZoomLevel.LEVEL_0;
        }
    }

    public static void setZoomFactor(double zoomFactor) {
        zoom_factor = zoomFactor;
    }

    public static double getZoomFactor() {
        return zoom_factor;
    }

    public int getNodesAtLevel() {
        return nodesAtLevel;
    }

    public static int getNodesAtMaxLevel() {
        return ZoomLevel.LEVEL_6.getNodesAtLevel();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Current Level: ");
        sb.append(this.name());
        sb.append(" (displaying every ");
        sb.append(nodesAtLevel);
        sb.append(" nodes)");
        return sb.toString();
    }
}
