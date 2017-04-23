package View;

import Enums.BoundType;
import Enums.OSMEnums.ElementType;
import Enums.ZoomLevel;
import Helpers.GlobalValue;
import Helpers.Shapes.PolygonApprox;
import Helpers.ThemeHelper;
import Helpers.Utilities.DebugWindow;
import KDtree.KDTree;
import Main.Main;
import Model.Elements.*;
import Model.Model;

import java.awt.*;
import java.awt.geom.*;
import java.util.EnumMap;
import java.util.HashSet;


/**
 * Class details:
 * The MapCanvas is a visual component, which purpose is to display the
 * elements (roads, points, structure, etc.) of the model. The MapCanvas
 * is able to be zoomed to and from as well as panned around upon.
 *
 * @author Andreas Blanke, blan@itu.dk
 * @author Niclas Hedam, nhed@itu.dk
 * @author Nikolaj Bläser, nibl@itu.dk
 * @author Búgvi Magnussen, buma@itu.dk
 * @author Jakob Mollerup, jmol@itu.dk
 * @version 06-03-2017
 */
public class MapCanvas extends View {

    private AffineTransform transform;
    private HashSet<Element> currentSection;
    private Point2D currentPoint;
    private Rectangle2D currentRectangle;
    private EnumMap<ElementType, KDTree> elements;
    private boolean antiAliasing;

    /**
     * The base Constructor for the MapCanvas.
     */
    public MapCanvas() {
        transform = new AffineTransform();
        setBackgroundColor();
        grabFocus();
    }

    public void setBackgroundColor() {
        setBackground(ThemeHelper.color("water"));
    }

    public void toggleAntiAliasing(boolean status) {
        antiAliasing = status;
        repaint();
    }

    public void setCurrentRectangle() {
        Rectangle2D rectangle = getVisibleRect();
        rectangle.setRect(rectangle.getX(), rectangle.getY() + GlobalValue.getToolbarHeight(), rectangle.getWidth(), rectangle.getHeight());
        Point2D point = toModelCoords(new Point2D.Double(0, GlobalValue.getToolbarHeight()));
        Point2D factor = toModelCoords(new Point2D.Double(rectangle.getWidth(), rectangle.getHeight()));
        double xBounds = factor.getX() - point.getX();
        double yBounds = factor.getY() - point.getY();
        currentRectangle = new Rectangle2D.Double(point.getX(), point.getY(), xBounds, yBounds);
        Model model = Model.getInstance();
        model.setCameraBound(BoundType.MIN_LONGITUDE, (float) point.getX());
        model.setCameraBound(BoundType.MAX_LONGITUDE, (float) factor.getX());
        model.setCameraBound(BoundType.MAX_LATITUDE, (float) point.getY());
        model.setCameraBound(BoundType.MIN_LATITUDE, (float) factor.getY());
        DebugWindow.getInstance().setCameraBoundsLabel();
    }


    /**
     * Paints the MapCanvas with all the shapes that should be displayed.
     */
    @Override
    protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2D = (Graphics2D) g;
            g2D.setTransform(transform);
            if (antiAliasing) g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            else g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            drawBackground(g2D);

            setCurrentRectangle();

            drawCoastlines(g2D);


            if (GlobalValue.getDidProgramLoadDefault()) {
                drawElements(g2D);
            }

            g2D.setColor(Color.black);
            g2D.setStroke(new BasicStroke(0.00001f));
            g2D.draw(currentRectangle);

            drawBoundaries(g2D);

            Main.FPS_COUNTER.interrupt();
            DebugWindow.getInstance();

        /*
        //Test text på Langeland, rotate text, successfull, saved this bit of code to look at at later times.
        AffineTransform old = g2D.getTransform();
        g2D.rotate(Math.PI / 4, 6, -55);
        g2D.setColor(Color.BLACK);
        g.drawString("Hello", 6, -55);
        g2D.setTransform(old);
        */

    }

    private void drawBackground(Graphics2D g) {
        g.setColor(ThemeHelper.color("water"));
        Path2D boundary = new Path2D.Float();
        Model model = Model.getInstance();
        boundary.moveTo(model.getMinLongitude(false), model.getMinLatitude(false));
        boundary.lineTo(model.getMaxLongitude(false), model.getMinLatitude(false));
        boundary.lineTo(model.getMaxLongitude(false), model.getMaxLatitude(false));
        boundary.lineTo(model.getMinLongitude(false), model.getMaxLatitude(false));
        boundary.lineTo(model.getMinLongitude(false), model.getMinLatitude(false));
        g.fill(boundary);
    }

    private void drawCoastlines(Graphics2D g) {
        java.util.List<Path2D> coastlines = Model.getInstance().getCoastlines();
        g.setColor(ThemeHelper.color("background"));
        for (Path2D path: coastlines) {
            g.fill(path);
        }
        //Creates outline
        if (GlobalValue.getMarkCoastlines()) {
            g.setStroke(new BasicStroke(Float.MIN_VALUE));
            g.setColor(Color.black);
            for (Path2D path : coastlines) {
                g.draw(path);
            }
        }
    }

    //TODO tænk over rækkefølgen elementerne bliver tegnet i (Jakob Nikolaj)
    private void drawElements(Graphics2D g){
        switch(ZoomLevel.getZoomLevel()){
            case LEVEL_0:
                drawWater(g, ThemeHelper.color("water"), 0.000001);
                drawPark(g, ThemeHelper.color("park"), 0.000001);
                drawForest(g, ThemeHelper.color("forest"), 0.000001);
                drawGrassland(g, ThemeHelper.color("grassland"), 0.000001);
                drawGrass(g, ThemeHelper.color("grass"), 0.000001);
                drawFarmland(g, ThemeHelper.color("farmland"), 0.000001);
                drawMeadow(g, ThemeHelper.color("meadow"), 0.000001);
                drawHeath(g, ThemeHelper.color("heath"), 0.000001);
                drawFootways(g, ThemeHelper.color("footway"), 0.000004f);
                drawBridleways(g, ThemeHelper.color("bridleway"), 0.000004f);
                drawCycleways(g, ThemeHelper.color("cycleway"), 0.000004f);
                drawPaths(g, ThemeHelper.color("path"), 0.000004f);
                drawRoads(g, ThemeHelper.color("road"), 0.00004f);
                drawSteps(g, ThemeHelper.color("steps"), 0.000004f);
                drawTracks(g, ThemeHelper.color("track"), 0.00004f);
                drawRaceways(g, ThemeHelper.color("raceway"), 0.00007f);
                drawEscapes(g, ThemeHelper.color("escape"), 0.00002f);
                drawBusGuideways(g, ThemeHelper.color("busGuideway"), 0.00006f);

                //roadborders
                drawPedestrianStreets(g, ThemeHelper.color("roadBorder"), 0.000045f);
                drawServiceRoads(g, ThemeHelper.color("roadBorder"), 0.000045f);
                drawLivingStreets(g, ThemeHelper.color("roadBorder"), 0.000055f);
                drawResidentialRoads(g, ThemeHelper.color("roadBorder"), 0.000055f);
                drawUnclassifiedRoads(g, ThemeHelper.color("roadBorder"), 0.000055f);
                drawTertiaryRoads(g, ThemeHelper.color("roadBorder"), 0.000065f);
                drawTertiaryRoadLinks(g, ThemeHelper.color("roadBorder"), 0.000065f);
                drawSecondaryRoads(g, ThemeHelper.color("roadBorder"), 0.000085f);
                drawSecondaryRoadLinks(g, ThemeHelper.color("roadBorder"), 0.000085f);
                drawPrimaryRoads(g,ThemeHelper.color("roadBorder"), 0.000085f);
                drawPrimaryRoadLinks(g,ThemeHelper.color("roadBorder"), 0.000085f);
                drawTrunkRoads(g, ThemeHelper.color("roadBorder"), 0.000125f);
                drawTrunkRoadLinks(g, ThemeHelper.color("roadBorder"), 0.00015f);
                drawMotorways(g, ThemeHelper.color("roadBorder"), 0.000155f);
                drawMotorwayLinks(g, ThemeHelper.color("roadBorder"), 0.000125f);

                //roads
                drawPedestrianStreets(g, ThemeHelper.color("pedestrianStreet"), 0.00004f);
                drawServiceRoads(g, ThemeHelper.color("serviceRoad"), 0.00004f);
                drawLivingStreets(g, ThemeHelper.color("livingStreet"), 0.00005f);
                drawResidentialRoads(g, ThemeHelper.color("residentialRoad"), 0.00005f);
                drawUnclassifiedRoads(g, ThemeHelper.color("unclassifiedRoad"), 0.00005f);
                drawTertiaryRoads(g, ThemeHelper.color("tertiaryRoad"), 0.00006f);
                drawTertiaryRoadLinks(g, ThemeHelper.color("tertiaryRoad"), 0.00006f);
                drawSecondaryRoads(g, ThemeHelper.color("secondaryRoad"), 0.00008f);
                drawSecondaryRoadLinks(g, ThemeHelper.color("secondaryRoad"), 0.00008f);
                drawPrimaryRoads(g,ThemeHelper.color("primaryRoad"), 0.00008f);
                drawPrimaryRoadLinks(g, ThemeHelper.color("primaryRoad"), 0.00008f);
                drawTrunkRoads(g, ThemeHelper.color("trunkRoad"), 0.00013f);
                drawTrunkRoadLinks(g, ThemeHelper.color("trunkRoad"), 0.0001f);
                drawMotorways(g, ThemeHelper.color("motorway"), 0.00016f);
                drawMotorwayLinks(g, ThemeHelper.color("motorway"), 0.00012f);
                drawRail(g, ThemeHelper.color("rail"), 0.00002f);

                drawBuilding(g, ThemeHelper.color("building"));

                drawRoadNames(g, ElementType.PEDESTRIAN_STREET);
                drawRoadNames(g, ElementType.SERVICE_ROAD);
                drawRoadNames(g, ElementType.LIVING_STREET);
                drawRoadNames(g, ElementType.RESIDENTIAL_ROAD);
                drawRoadNames(g, ElementType.UNCLASSIFIED_ROAD);
                drawRoadNames(g, ElementType.TERTIARY_ROAD);
                drawRoadNames(g, ElementType.SECONDARY_ROAD);
                drawRoadNames(g, ElementType.PRIMARY_ROAD);
                drawRoadNames(g, ElementType.TRUNK_ROAD);
                drawRoadNames(g, ElementType.MOTORWAY);

                drawNight(g);
                break;
            case LEVEL_1:
                drawWater(g, ThemeHelper.color("water"), 0.00005);
                drawPark(g, ThemeHelper.color("park"), 0.00005);
                drawForest(g, ThemeHelper.color("forest"), 0.00005);
                drawGrassland(g, ThemeHelper.color("grassland"), 0.00005);
                drawGrass(g, ThemeHelper.color("grass"), 0.00005);
                drawFarmland(g, ThemeHelper.color("farmland"), 0.00005);
                drawMeadow(g, ThemeHelper.color("meadow"), 0.00005);
                drawHeath(g, ThemeHelper.color("heath"), 0.00005);
                drawRaceways(g, ThemeHelper.color("raceway"), 0.00007f);
                drawEscapes(g, ThemeHelper.color("escape"), 0.00002f);
                drawBusGuideways(g, ThemeHelper.color("busGuideway"), 0.00006f);

                drawPedestrianStreets(g, ThemeHelper.color("pedestrianStreet"), 0.00006f);
                drawServiceRoads(g, ThemeHelper.color("serviceRoad"), 0.00006f);
                drawLivingStreets(g, ThemeHelper.color("livingStreet"), 0.00007f);
                drawResidentialRoads(g, ThemeHelper.color("residentialRoad"), 0.00007f);
                drawUnclassifiedRoads(g, ThemeHelper.color("unclassifiedRoad"), 0.00007f);
                drawTertiaryRoads(g, ThemeHelper.color("tertiaryRoad"), 0.0001f);
                drawTertiaryRoadLinks(g, ThemeHelper.color("tertiaryRoad"), 0.0001f);
                drawSecondaryRoads(g, ThemeHelper.color("secondaryRoad"), 0.0001f);
                drawSecondaryRoadLinks(g, ThemeHelper.color("secondaryRoad"), 0.0001f);
                drawPrimaryRoads(g,ThemeHelper.color("primaryRoad"), 0.0001f);
                drawPrimaryRoadLinks(g, ThemeHelper.color("primaryRoad"), 0.0001f);
                drawTrunkRoads(g, ThemeHelper.color("trunkRoad"), 0.00014f);
                drawTrunkRoadLinks(g, ThemeHelper.color("trunkRoad"), 0.00012f);
                drawMotorways(g, ThemeHelper.color("motorway"), 0.00018f);
                drawMotorwayLinks(g, ThemeHelper.color("motorway"), 0.00014f);

                drawRail(g, ThemeHelper.color("rail"), 0.00002f);

                drawBuilding(g, ThemeHelper.color("building"));

                drawCityNames(g, ElementType.HAMLET_NAME, 0.35f);
                drawCityNames(g, ElementType.SUBURB_NAME, 0.35f);
                drawCityNames(g, ElementType.QUARTER_NAME, 0.35f);
                drawCityNames(g, ElementType.NEIGHBOURHOOD_NAME, 0.35f);

                drawRoadNames(g, ElementType.PEDESTRIAN_STREET);
                drawRoadNames(g, ElementType.SERVICE_ROAD);
                drawRoadNames(g, ElementType.LIVING_STREET);
                drawRoadNames(g, ElementType.RESIDENTIAL_ROAD);
                drawRoadNames(g, ElementType.UNCLASSIFIED_ROAD);
                drawRoadNames(g, ElementType.TERTIARY_ROAD);
                drawRoadNames(g, ElementType.SECONDARY_ROAD);
                drawRoadNames(g, ElementType.PRIMARY_ROAD);
                drawRoadNames(g, ElementType.TRUNK_ROAD);
                drawRoadNames(g, ElementType.MOTORWAY);
                break;
            case LEVEL_2:
                drawWater(g, ThemeHelper.color("water"), 0.00008);
                drawPark(g, ThemeHelper.color("park"), 0.00008);
                drawForest(g, ThemeHelper.color("forest"), 0.00008);
                drawGrassland(g, ThemeHelper.color("grassland"), 0.00008);
                drawGrass(g, ThemeHelper.color("grass"), 0.00008);
                drawFarmland(g, ThemeHelper.color("farmland"), 0.00008);
                drawMeadow(g, ThemeHelper.color("meadow"), 0.00008);
                drawHeath(g, ThemeHelper.color("heath"), 0.00008);
                drawResidentialRoads(g, ThemeHelper.color("residentialRoad"), 0.00007f);
                drawUnclassifiedRoads(g, ThemeHelper.color("unclassifiedRoad"), 0.00007f);
                drawTertiaryRoads(g, ThemeHelper.color("tertiaryRoad"), 0.0001f);
                drawTertiaryRoadLinks(g, ThemeHelper.color("tertiaryRoad"), 0.0001f);
                drawSecondaryRoads(g, ThemeHelper.color("secondaryRoad"), 0.0001f);
                drawSecondaryRoadLinks(g, ThemeHelper.color("secondaryRoad"), 0.0001f);
                drawPrimaryRoads(g,ThemeHelper.color("primaryRoad"), 0.0001f);
                drawPrimaryRoadLinks(g, ThemeHelper.color("primaryRoad"), 0.0001f);
                drawTrunkRoads(g, ThemeHelper.color("trunkRoad"), 0.00014f);
                drawTrunkRoadLinks(g, ThemeHelper.color("trunkRoad"), 0.00012f);
                drawMotorways(g, ThemeHelper.color("motorway"), 0.00018f);
                drawMotorwayLinks(g, ThemeHelper.color("motorway"), 0.00014f);

                drawRail(g, ThemeHelper.color("rail"), 0.00002f);

                drawCityNames(g, ElementType.VILLAGE_NAME, 0.35f);
                drawCityNames(g, ElementType.HAMLET_NAME, 0.35f);
                drawCityNames(g, ElementType.SUBURB_NAME, 0.35f);
                drawCityNames(g, ElementType.QUARTER_NAME, 0.35f);
                drawCityNames(g, ElementType.NEIGHBOURHOOD_NAME, 0.35f);
                break;
            case LEVEL_3:
                drawWater(g, ThemeHelper.color("water"), 0.001);
                drawPark(g, ThemeHelper.color("park"), 0.001);
                drawForest(g, ThemeHelper.color("forest"), 0.001);
                drawGrassland(g, ThemeHelper.color("grassland"), 0.001);
                drawGrass(g, ThemeHelper.color("grass"), 0.001);
                drawFarmland(g, ThemeHelper.color("farmland"), 0.001);
                drawMeadow(g, ThemeHelper.color("meadow"), 0.001);
                drawHeath(g, ThemeHelper.color("heath"), 0.001);
                drawTertiaryRoads(g, ThemeHelper.color("tertiaryRoad"), 0.0001f);
                drawTertiaryRoadLinks(g, ThemeHelper.color("tertiaryRoad"), 0.0001f);
                drawSecondaryRoads(g, ThemeHelper.color("secondaryRoad"), 0.0001f);
                drawSecondaryRoadLinks(g, ThemeHelper.color("secondaryRoad"), 0.0001f);
                drawPrimaryRoads(g,ThemeHelper.color("primaryRoad"), 0.0001f);
                drawPrimaryRoadLinks(g, ThemeHelper.color("primaryRoad"), 0.0001f);
                drawTrunkRoads(g, ThemeHelper.color("trunkRoad"), 0.00014f);
                drawTrunkRoadLinks(g, ThemeHelper.color("trunkRoad"), 0.00012f);
                drawMotorways(g, ThemeHelper.color("motorway"), 0.00018f);
                drawMotorwayLinks(g, ThemeHelper.color("motorway"), 0.00014f);

                drawRail(g, ThemeHelper.color("rail"), 0.00002f);

                drawCityNames(g, ElementType.VILLAGE_NAME, 0.35f);
                drawCityNames(g, ElementType.HAMLET_NAME, 0.35f);
                drawCityNames(g, ElementType.SUBURB_NAME, 0.35f);
                drawCityNames(g, ElementType.QUARTER_NAME, 0.35f);
                drawCityNames(g, ElementType.NEIGHBOURHOOD_NAME, 0.35f);
                break;
            case LEVEL_4:
                drawWater(g, ThemeHelper.color("water"), 0.001);
                drawPark(g, ThemeHelper.color("park"), 0.001);
                drawForest(g, ThemeHelper.color("forest"), 0.001);
                drawGrassland(g, ThemeHelper.color("grassland"), 0.001);
                drawGrass(g, ThemeHelper.color("grass"), 0.001);
                drawFarmland(g, ThemeHelper.color("farmland"), 0.001);
                drawMeadow(g, ThemeHelper.color("meadow"), 0.001);
                drawHeath(g, ThemeHelper.color("heath"), 0.001);
                drawTertiaryRoads(g, ThemeHelper.color("tertiaryRoad"), 0.0001f);
                drawTertiaryRoadLinks(g, ThemeHelper.color("tertiaryRoad"), 0.0001f);
                drawSecondaryRoads(g, ThemeHelper.color("secondaryRoad"), 0.0001f);
                drawSecondaryRoadLinks(g, ThemeHelper.color("secondaryRoad"), 0.0001f);
                drawPrimaryRoads(g,ThemeHelper.color("primaryRoad"), 0.0001f);
                drawPrimaryRoadLinks(g, ThemeHelper.color("primaryRoad"), 0.0001f);
                drawTrunkRoads(g, ThemeHelper.color("trunkRoad"), 0.00014f);
                drawTrunkRoadLinks(g, ThemeHelper.color("trunkRoad"), 0.00012f);
                drawMotorways(g, ThemeHelper.color("motorway"), 0.00018f);
                drawMotorwayLinks(g, ThemeHelper.color("motorway"), 0.00014f);

                drawRail(g, ThemeHelper.color("rail"), 0.00002f);

                drawCityNames(g, ElementType.CITY_NAME, 0.8f);
                drawCityNames(g, ElementType.TOWN_NAME, 0.35f);
                drawCityNames(g, ElementType.VILLAGE_NAME, 0.35f);
                break;
            case LEVEL_5:
                drawWater(g, ThemeHelper.color("water"), 0.005);
                drawPark(g, ThemeHelper.color("park"), 0.005);
                drawForest(g, ThemeHelper.color("forest"), 0.005);
                drawGrassland(g, ThemeHelper.color("grassland"), 0.005);
                drawTertiaryRoads(g, ThemeHelper.color("tertiaryRoad"), 0.0001f);
                drawTertiaryRoadLinks(g, ThemeHelper.color("tertiaryRoad"), 0.0001f);
                drawSecondaryRoads(g, ThemeHelper.color("secondaryRoad"), 0.0001f);
                drawSecondaryRoadLinks(g, ThemeHelper.color("secondaryRoad"), 0.0001f);
                drawPrimaryRoads(g,ThemeHelper.color("primaryRoad"), 0.0001f);
                drawPrimaryRoadLinks(g, ThemeHelper.color("primaryRoad"), 0.0001f);
                drawTrunkRoads(g, ThemeHelper.color("trunkRoad"), 0.00014f);
                drawTrunkRoadLinks(g, ThemeHelper.color("trunkRoad"), 0.00012f);
                drawMotorways(g, ThemeHelper.color("motorway"), 0.00018f);
                drawMotorwayLinks(g, ThemeHelper.color("motorway"), 0.00014f);

                drawRail(g, ThemeHelper.color("rail"), 0.00002f);

                drawCityNames(g, ElementType.CITY_NAME, 0.8f);
                drawCityNames(g, ElementType.TOWN_NAME, 0.35f);
                break;
            case LEVEL_6:
                drawWater(g, ThemeHelper.color("water"), 0.03);
                drawPark(g, ThemeHelper.color("park"), 0.03);
                drawForest(g, ThemeHelper.color("forest"), 0.03);
                drawGrassland(g, ThemeHelper.color("grassland"), 0.03);
                drawPrimaryRoads(g,ThemeHelper.color("primaryRoad"), 0.0001f);
                drawPrimaryRoadLinks(g, ThemeHelper.color("primaryRoad"), 0.0001f);
                drawTrunkRoads(g, ThemeHelper.color("trunkRoad"), 0.00014f);
                drawTrunkRoadLinks(g, ThemeHelper.color("trunkRoad"), 0.00012f);
                drawMotorways(g, ThemeHelper.color("motorway"), 0.00018f);
                drawMotorwayLinks(g, ThemeHelper.color("motorway"), 0.00014f);
                drawCityNames(g, ElementType.CITY_NAME, 1f);
                break;
        }
    }

    private void setCurrentSection(ElementType elementType){
        currentSection = elements.get(elementType).getManySections(
                (float) currentRectangle.getMinX(),
                (float) currentRectangle.getMinY(),
                (float) currentRectangle.getMaxX(),
                (float) currentRectangle.getMaxY());
    }

    private void drawBoundaries(Graphics2D g2D) {
        g2D.setColor(ThemeHelper.color("boundary"));
        Path2D boundary = new Path2D.Float();
        Model model = Model.getInstance();
        boundary.moveTo(model.getMinLongitude(true), model.getMinLatitude(true));
        boundary.lineTo(model.getMaxLongitude(true), model.getMinLatitude(true));
        boundary.lineTo(model.getMaxLongitude(true), model.getMaxLatitude(true));
        boundary.lineTo(model.getMinLongitude(true), model.getMaxLatitude(true));
        boundary.lineTo(model.getMinLongitude(true), model.getMinLatitude(true));
        g2D.draw(boundary);
    }

    /**
     * Zooms in or out upon the elements on the MapCanvas depending on a given factor.
     */
    public void zoom(double factor) {
        DebugWindow.getInstance().setZoomLabel();
        DebugWindow.getInstance().setZoomFactorLabel();
        DebugWindow.getInstance().setFPSLabel();
        transform.preConcatenate(AffineTransform.getScaleInstance(factor, factor));
        repaint();
    }

    /**
     * Resets the MapCanvas from being zoomed in or out and panned to one or another position.
     */
    public void resetTransform(){
        transform.setToIdentity();
    }

    /**
     * Pans the MapCanvas to another position so other elements might be viewed.
     * @param dx The difference in x-coordinates between the new position to be
     *           centered and the current center point
     * @param dy The difference in y-coordinates between the new position to be
     *           centered and the current center point
     */
    public void pan(double dx, double dy) {
        transform.preConcatenate(AffineTransform.getTranslateInstance(dx, dy));
        DebugWindow.getInstance().setFPSLabel();
        repaint();
    }

    public Point2D toModelCoords(Point2D mousePosition){
        try{
            return transform.inverseTransform(mousePosition, null);

        }catch(NoninvertibleTransformException e){
            //System.out.println("NoninvertibleTransformException blev kastet i toModelCoords");
            throw new RuntimeException();
        }
    }

    public void setElements(EnumMap<ElementType, KDTree> map) {
        elements = map;
    }

    public void setCurrentSection(HashSet<Element> currentSection) {
        this.currentSection = currentSection;
    }

    public void setCurrentPoint(Point2D currentPoint) {
        this.currentPoint = currentPoint;
    }

    //Draw Roads Methods
    private void drawRail(Graphics2D g, Color color, float width){
        setCurrentSection(ElementType.RAIL);
        for (Element element : currentSection) {
            g.setColor(color);
            g.setStroke(new BasicStroke(width,BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_BEVEL));
            g.draw(element.getShape());
        }
    }

    //Draw Roads Methods
    private void drawMotorways(Graphics2D g, Color color, float width){
        setCurrentSection(ElementType.MOTORWAY);
        for (Element element : currentSection) {
            g.setColor(color);
            g.setStroke(new BasicStroke(width,BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_BEVEL));
            g.draw(element.getShape());
        }
    }
    private void drawMotorwayLinks(Graphics2D g, Color color, float width){
        setCurrentSection(ElementType.MOTORWAY_LINK);
        for (Element element : currentSection) {
            g.setColor(color);
            g.setStroke(new BasicStroke(width,BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_BEVEL));
            g.draw(element.getShape());
        }
    }
    private void drawTrunkRoads(Graphics2D g, Color color, float width) {
        setCurrentSection(ElementType.TRUNK_ROAD);
        for (Element element : currentSection) {
            g.setColor(color);
            g.setStroke(new BasicStroke(width,BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_BEVEL));
            g.draw(element.getShape());
        }
    }
    private void drawTrunkRoadLinks(Graphics2D g, Color color, float width){
        setCurrentSection(ElementType.TRUNK_ROAD_LINK);
        for (Element element : currentSection) {
            g.setColor(color);
            g.setStroke(new BasicStroke(width,BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_BEVEL));
            g.draw(element.getShape());
        }
    }
    private void drawPrimaryRoads(Graphics2D g, Color color, float width){
        setCurrentSection(ElementType.PRIMARY_ROAD);
        for (Element element : currentSection) {
            g.setColor(color);
            g.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_BEVEL));
            g.draw(element.getShape());
        }
    }
    private void drawPrimaryRoadLinks(Graphics2D g, Color color, float width){
        setCurrentSection(ElementType.PRIMARY_ROAD_LINK);
        for (Element element : currentSection) {
            g.setColor(color);
            g.setStroke(new BasicStroke(width,BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_BEVEL));
            g.draw(element.getShape());
        }
    }
    private void drawSecondaryRoads(Graphics2D g, Color color, float width){
        setCurrentSection(ElementType.SECONDARY_ROAD);
        for (Element element : currentSection) {
            g.setColor(color);
            g.setStroke(new BasicStroke(width,BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_BEVEL));
            g.draw(element.getShape());
        }
    }
    private void drawSecondaryRoadLinks(Graphics2D g, Color color, float width){
        setCurrentSection(ElementType.SECONDARY_ROAD_LINK);
        for (Element element : currentSection) {
            g.setColor(color);
            g.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_BEVEL));
            g.draw(element.getShape());
        }
    }
    private void drawTertiaryRoads(Graphics2D g, Color color, float width){
        setCurrentSection(ElementType.TERTIARY_ROAD);
        for (Element element : currentSection) {
            g.setColor(color);
            g.setStroke(new BasicStroke(width,BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_BEVEL));
            g.draw(element.getShape());
        }
    }
    private void drawTertiaryRoadLinks(Graphics2D g, Color color, float width){
        setCurrentSection(ElementType.TERTIARY_ROAD_LINK);
        for (Element element : currentSection) {
            g.setColor(color);
            g.setStroke(new BasicStroke(width,BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_BEVEL));
            g.draw(element.getShape());
        }
    }
    private void drawUnclassifiedRoads(Graphics2D g, Color color, float width){
        setCurrentSection(ElementType.UNCLASSIFIED_ROAD);
        for (Element element : currentSection) {
            g.setColor(color);
            g.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_BEVEL));
            g.draw(element.getShape());
        }
    }
    private void drawResidentialRoads(Graphics2D g, Color color, float width){
        setCurrentSection(ElementType.RESIDENTIAL_ROAD);
        for (Element element : currentSection) {
            g.setColor(color);
            g.setStroke(new BasicStroke(width,BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_BEVEL));
            g.draw(element.getShape());
        }
    }
    private void drawLivingStreets(Graphics2D g, Color color, float width){
        setCurrentSection(ElementType.LIVING_STREET);
        for (Element element : currentSection) {
            g.setColor(color);
            g.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_BEVEL));
            g.draw(element.getShape());
        }
    }
    private void drawServiceRoads(Graphics2D g, Color color, float width){
        setCurrentSection(ElementType.SERVICE_ROAD);
        for (Element element : currentSection) {
            g.setColor(color);
            g.setStroke(new BasicStroke(width,BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_BEVEL));
            g.draw(element.getShape());
        }
    }
    private void drawBusGuideways(Graphics2D g, Color color, float width){
        setCurrentSection(ElementType.BUS_GUIDEWAY);
        for (Element element : currentSection) {
            g.setColor(color);
            g.setStroke(new BasicStroke(width));
            g.draw(element.getShape());
        }
    }
    private void drawEscapes(Graphics2D g, Color color, float width){
        setCurrentSection(ElementType.ESCAPE);
        for (Element element : currentSection) {
            g.setColor(color);
            g.setStroke(new BasicStroke(width));
            g.draw(element.getShape());
        }
    }
    private void drawRaceways(Graphics2D g, Color color, float width){
        setCurrentSection(ElementType.RACEWAY);
        for (Element element : currentSection) {
            g.setColor(color);
            g.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_BEVEL));
            g.draw(element.getShape());
        }
    }
    private void drawPedestrianStreets(Graphics2D g, Color color, float width){
        setCurrentSection(ElementType.PEDESTRIAN_STREET);
        for (Element element : currentSection) {
            g.setColor(color);
            g.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_BEVEL));
            Road road = (Road) element;
            if(road.isArea()) g.fill(element.getShape());
            g.draw(element.getShape());
        }
    }
    private void drawTracks(Graphics2D g, Color color, float width){
        setCurrentSection(ElementType.TRACK);
        for (Element element : currentSection) {
            g.setColor(color);
            g.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_BEVEL));
            g.draw(element.getShape());
        }
    }
    private void drawSteps(Graphics2D g, Color color, float width){
        setCurrentSection(ElementType.STEPS);
        for (Element element : currentSection) {
            g.setColor(color);
            g.setStroke(new BasicStroke(width,
                    BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER,
                    10.0f, new float[]{0.00001f}, 0.0f));
            g.draw(element.getShape());
        }
    }
    private void drawFootways(Graphics2D g, Color color, float width){
        setCurrentSection(ElementType.FOOTWAY);
        for (Element element : currentSection) {
            g.setColor(color);
            g.setStroke(new BasicStroke(width,
                    BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER,
                    10.0f, new float[]{0.00001f}, 0.0f));
            g.draw(element.getShape());
        }
    }
    private void drawBridleways(Graphics2D g, Color color, float width){
        setCurrentSection(ElementType.BRIDLEWAY);
        for (Element element : currentSection) {
            g.setColor(color);
            g.setStroke(new BasicStroke(width,
                    BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER,
                    10.0f, new float[]{0.00001f}, 0.0f));
            g.draw(element.getShape());
        }
    }
    private void drawCycleways(Graphics2D g, Color color, float width){
        setCurrentSection(ElementType.CYCLEWAY);
        for (Element element : currentSection) {
            g.setColor(color);
            g.setStroke(new BasicStroke(width));
            g.draw(element.getShape());
        }
    }
    private void drawPaths(Graphics2D g, Color color, float width){
        setCurrentSection(ElementType.PATH);
        for (Element element : currentSection) {
            g.setColor(color);
            g.setStroke(new BasicStroke(width,
                    BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER,
                    10.0f, new float[]{0.00001f}, 0.0f));
            g.draw(element.getShape());
        }
    }
    private void drawRoads(Graphics2D g, Color color, float width){
        setCurrentSection(ElementType.ROAD);
        for (Element element : currentSection) {
            g.setColor(color);
            g.setStroke(new BasicStroke(width));
            g.draw(element.getShape());
        }
    }

    private void drawPark(Graphics2D g, Color color, Double minSizeToBeSignificant) {
        setCurrentSection(ElementType.PARK);
        for (Element element : currentSection) {
            g.setColor(color);
            Biome biome = (Biome) element;
            float size = biome.getShape().getSize();
            if(size > minSizeToBeSignificant) {
                g.fill(biome.getShape());
            }
        }
    }

    private void drawForest(Graphics2D g, Color color, Double minSizeToBeSignificant) {
        setCurrentSection(ElementType.FOREST);
        for (Element element : currentSection) {
            g.setColor(color);
            Biome biome = (Biome) element;
            float size = biome.getShape().getSize();
            if(size > minSizeToBeSignificant) {
                g.fill(biome.getShape());
            }
        }
    }

    private void drawGrassland(Graphics2D g, Color color, Double minSizeToBeSignificant){
        setCurrentSection(ElementType.GRASSLAND);
        for (Element element : currentSection) {
            g.setColor(color);
            Biome biome = (Biome) element;
            float size = biome.getShape().getSize();
            if(size > minSizeToBeSignificant) {
                g.fill(biome.getShape());
            }
        }
    }

    private void drawGrass(Graphics2D g, Color color, Double minSizeToBeSignificant){
        setCurrentSection(ElementType.GRASS);
        for (Element element : currentSection) {
            g.setColor(color);
            Biome biome = (Biome) element;
            float size = biome.getShape().getSize();
            if(size > minSizeToBeSignificant) {
                g.fill(biome.getShape());
            }
        }
    }

    private void drawMeadow(Graphics2D g, Color color, Double minSizeToBeSignificant){
        setCurrentSection(ElementType.MEADOW);
        for (Element element : currentSection) {
            g.setColor(color);
            Biome biome = (Biome) element;
            float size = biome.getShape().getSize();
            if(size > minSizeToBeSignificant) {
                g.fill(biome.getShape());
            }
        }
    }

    private void drawFarmland(Graphics2D g, Color color, Double minSizeToBeSignificant){
        setCurrentSection(ElementType.FARMLAND);
        for (Element element : currentSection) {
            g.setColor(color);
            Biome biome = (Biome) element;
            float size = biome.getShape().getSize();
            if(size > minSizeToBeSignificant) {
                g.fill(biome.getShape());
            }
        }
    }

    private void drawHeath(Graphics2D g, Color color, Double minSizeToBeSignificant){
        setCurrentSection(ElementType.HEATH);
        for (Element element : currentSection) {
            g.setColor(color);
            Biome biome = (Biome) element;
            float size = biome.getShape().getSize();
            if(size > minSizeToBeSignificant) {
                g.fill(biome.getShape());
            }
        }
    }

    private void drawWater(Graphics2D g, Color color, Double minSizeToBeSignificant) {
        setCurrentSection(ElementType.WATER);
        for (Element element : currentSection) {
            g.setColor(color);
            g.setStroke(new BasicStroke(0.00001f));
            Biome biome = (Biome) element;
            float size = biome.getShape().getSize();
            if(size > minSizeToBeSignificant) {
                g.fill(biome.getShape());
            }
        }
    }

    private  void drawBuilding(Graphics2D g, Color color){
        setCurrentSection(ElementType.BUILDING);
        Composite c = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .9f);
        g.setComposite(c);
        for (Element element : currentSection) {
            g.setColor(color);
            g.setStroke(new BasicStroke(0.00001f));
            g.fill(element.getShape());
        }
        c = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);
        g.setComposite(c);
    }

    private void drawRoadNames(Graphics2D g, ElementType type){
        setCurrentSection(type);  //TODO Se drawCityNames for a more generic version

        //Scalefactor
        float scaleFactor =  0.1f * 397.522f * (float) (Math.pow(ZoomLevel.getZoomFactor(), -2.43114f));

        //Font
        Font font = new Font("Times New Roman", Font.PLAIN, 12);

        //Transparency
        Composite c = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .7f); //TODO want transparency for road names ?
        g.setComposite(c);

        //Color
        g.setColor(ThemeHelper.color("roadName"));

        for (Element element : currentSection) {
            Road road = (Road) element;

            g.setFont(font.deriveFont(AffineTransform.getScaleInstance(scaleFactor, scaleFactor)));
            PolygonApprox polygon = (PolygonApprox) road.getShape();

            //The polygon coords
            float[] coords = polygon.getCoords();

            if (coords.length < 4) break;
            float longestVectorX1 = coords[0];
            float longestVectorY1 = coords[1];
            float longestVectorX2 = coords[2];
            float longestVectorY2 = coords[3];

            //Coords to help finding the longest vector
            float x1;
            float y1;
            float x2;
            float y2;

            //Find the longest vector in the path
            for(int i = 2 ; i < coords.length ; i += 2){
                x1 = coords[i-2];
                y1 = coords[i-1];
                x2 = coords[i];
                y2 = coords[i+1];

                if( (vectorLength(longestVectorX2-longestVectorX1, longestVectorY2-longestVectorY1)) < (vectorLength(x2-x1, y2-y1))){
                    longestVectorX1 = x1;
                    longestVectorY1 = y1;
                    longestVectorX2 = x2;
                    longestVectorY2 = y2;
                }
            }

            //Find the angle of the longest vector in the path
            double angle = vectorAngle(longestVectorX1, longestVectorY1, longestVectorX2, longestVectorY2);

            //The center of the longest vector
            float centerX = (longestVectorX2 + longestVectorX1) / 2;
            float centerY = (longestVectorY2 + longestVectorY1) / 2;

            //The length of the string (name)
            float stringLength = 0;
            for(int i = 0 ; i < road.getName().length() ; i++){
                char ch = road.getName().charAt(i);
                stringLength += getFontMetrics(font).charWidth(ch)*scaleFactor;
            }

            //Using trigonometry to find the appropiate start for the string
            float hyp = stringLength / 2;
            float hos = (float) Math.cos(angle) * hyp;
            float mod = (float) Math.sin(angle) * hyp;

            float drawFromX = centerX - hos;
            float drawFromY = centerY - mod;

            hyp = (float) 0.0001 / 4; //TODO I though it would be better to divide by 2 and not 4, but it turned out 4 is better. Also though it would matter more to adjust for various stroke sizes.
            hos = (float) Math.cos(90-angle) * hyp;
            mod = (float) Math.sin(90-angle) * hyp;

            drawFromX = drawFromX - hos;
            drawFromY = drawFromY + mod;

            //TODO cosider to move the if-statement further up, such that all the calculations dont have to be done if the name is too long.
            if(vectorLength(longestVectorX2-longestVectorX1, longestVectorY2-longestVectorY1) > stringLength){
                AffineTransform old = g.getTransform();
                g.rotate(angle, drawFromX, drawFromY);
                drawString(road.getName(), g, drawFromX, drawFromY, font, scaleFactor, false);
                g.setTransform(old);
            }

        }
    }
    private double vectorLength(float x, float y){
        return Math.sqrt( x * x + y * y);
    }
    private double dotProduct(float x1, float y1, float x2, float y2){
        double newX1 = (double) x1;
        double newY1 = (double) y1;
        double newX2 = (double) x2;
        double newY2 = (double) y2;
        return newX1 * newX2 + newY1 * newY2;
    }
    private double vectorAngle(float x1, float y1, float x2, float y2){
        double cosAngle;
        double dotProduct = dotProduct((x2-x1),(y2-y1), 1, 0f);
        double vector1Length = vectorLength((x2-x1),(y2-y1));
        double vector2Length = vectorLength(1, 0f);
        cosAngle = dotProduct / (vector1Length * vector2Length);

        if((y2-y1) < 0 && (x2-x1) > 0) return -Math.acos(cosAngle);
        if((y2-y1) < 0 && (x2-x1) < 0) return Math.acos(-cosAngle);
        if((y2-y1) > 0 && (x2-x1) > 0) return Math.acos(cosAngle);
        if((y2-y1) > 0 && (x2-x1) < 0) return -Math.acos(-cosAngle);
        if ((x2-x1) < 0) return Math.acos(-cosAngle);
        return Math.acos(cosAngle);
    }

    private void drawCityNames(Graphics2D g, ElementType type, float scaling){
        setCurrentSection(type);
        if(ZoomLevel.getZoomFactor() > -60){
            float scaleFactor;
            if(ZoomLevel.getZoomFactor() >= 100) {
                scaleFactor = scaling * 397.522f * (float) (Math.pow(ZoomLevel.getZoomFactor(), -2.43114f));
            }
            else{
                scaleFactor = 0.0054586004f;
            }
            //Font
            Font font = new Font("Arial", Font.BOLD, 12);

            //Transparency
            Composite c = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .7f);
            g.setComposite(c);

            //Color
            g.setColor(ThemeHelper.color("cityName"));
            for(Element element : currentSection){
                PlaceName placeName = (PlaceName) element;
                g.setFont(font.deriveFont(AffineTransform.getScaleInstance(scaleFactor, scaleFactor)));
                drawString(placeName.getName(), g, placeName.getX(), placeName.getY(), font, scaleFactor, true);
            }
        }
    }
    private void drawString (String s , Graphics2D g , float x , float y, Font font, float scaleFactor, boolean isShiftedLeft){
        if(s.length() > 0){
            if(isShiftedLeft) x = x - ((getFontMetrics(font).charWidth(s.charAt(s.length()/2))*scaleFactor) * s.length()/2);
            for (int i = 0 ; i < s.length() ; i++){
                char ch = s.charAt(i);
                g.drawString(ch + "", x, y) ;
                x += ((getFontMetrics(font).charWidth(ch)))*scaleFactor;
            }
        }
    }

    private void drawNight(Graphics2D g){
        switch(ThemeHelper.getCurrentTheme()){
            case "Night":
                if(ZoomLevel.getZoomFactor() >= 650){
                    float scaleFactor = 0.5f * (float) (Math.pow(ZoomLevel.getZoomFactor(), -2f));
                    setCurrentSection(ElementType.BAR);
                    for (Element element : currentSection){
                        Amenity amenity = (Amenity) element;
                        g.setColor(ThemeHelper.color("barName"));
                        Font font = Helpers.FontAwesome.getFontAwesome();
                        g.setFont(font.deriveFont(AffineTransform.getScaleInstance(scaleFactor, scaleFactor)));
                        drawString("\uf000" + "", g, amenity.getX(), amenity.getY(), font, scaleFactor, false);
                    }
                    setCurrentSection(ElementType.NIGHT_CLUB);
                    for (Element element : currentSection){
                        Amenity amenity = (Amenity) element;
                        g.setColor(ThemeHelper.color("nightClubName"));
                        Font font = Helpers.FontAwesome.getFontAwesome();
                        g.setFont(font.deriveFont(AffineTransform.getScaleInstance(scaleFactor, scaleFactor)));
                        drawString("\uf001" + "", g, amenity.getX(), amenity.getY(), font, scaleFactor, false);
                    }
                }
        }
    }
}
