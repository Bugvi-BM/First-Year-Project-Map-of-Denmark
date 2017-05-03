package View;

import Helpers.ThemeHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by  on .
 *
 * @author bugvimagnussen
 * @version 26/04/2017
 */
public class PointsOfInterestBar extends View {

    private final int SPACE_BETWEEN_PROFILES = 10;
    private final int HORIZONTAL_NOPOI_LABEL_NORTH_OFFSET = 35;
    private final int NOPOI_LARGE_OFFSET = -20;
    private final int NOPOI_SMALL_OFFSET = -10;

    private final PointProfile NO_POI_SAVED = new PointProfile("No Points Of Interest Saved!", 0, 0);

    private int orientation;
    private List<PointProfile> points;
    public PointsOfInterestBar() {
        points = new ArrayList<>();
        applyTheme();
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        orientation = BoxLayout.PAGE_AXIS;
        setupNoPOIPanel();
    }

    @Override
    public void remove(Component component) {
        super.remove(component);
        if(points.size() == 0) add(NO_POI_SAVED);
        revalidate();
        repaint();
    }

    @Override
    public Component add(Component component) {
        if(points.size() > 0) remove(NO_POI_SAVED);
        super.add(component);
        revalidate();
        repaint();
        return component;
    }

    public void setupNoPOIPanel() {
        NO_POI_SAVED.setBorder(BorderFactory.createLineBorder(ThemeHelper.color("toolbar")));
        NO_POI_SAVED.remove(NO_POI_SAVED.getDeleteButton());
        for(MouseListener listener : NO_POI_SAVED.getMouseListeners()) {
            NO_POI_SAVED.removeMouseListener(listener);
        }
    }

    public void specifyLayout(int layout) {
        orientation = layout;
        setLayout(new BoxLayout(this, layout));
    }

    public void addPlaceToVerticaList(PointProfile place) {
        add(place);
        createVerticalSpace(SPACE_BETWEEN_PROFILES);
        addHorizontalGlue();
    }

    public void addPlaceToHorizontalList(PointProfile place) {
        add(place);
        createHorizontalSpace(SPACE_BETWEEN_PROFILES);
        addVerticalGlue();
    }

    public void createVerticalSpace(int size) {
        add(Box.createRigidArea(new Dimension(0,size)));
    }

    public void createHorizontalSpace(int size) { add(Box.createRigidArea(new Dimension(size, 0)));}

    public void addVerticalGlue() {add(Box.createVerticalGlue());}

    public void addHorizontalGlue() {
        add(Box.createHorizontalGlue());
    }

    public void applyTheme() {
        setBackground(ThemeHelper.color("toolbar"));
        for(PointProfile panel : points) {
           panel.applyTheme();
        }
    }

    public void setPointProfilesVertically(List<PointProfile> pointProfiles) {
        points = pointProfiles;
        for (PointProfile point : points) {
            add(point);
            createVerticalSpace(SPACE_BETWEEN_PROFILES);
            addHorizontalGlue();
        }
    }

    public void setPointProfilesHorizontally(List<PointProfile> pointProfiles) {
        points = pointProfiles;
        for(PointProfile point : points) {
            add(point);
            createHorizontalSpace(SPACE_BETWEEN_PROFILES);
            addVerticalGlue();
        }
    }

    public void addNoPoiPanel() {
        if(points.size() == 0) {
            if(orientation == BoxLayout.LINE_AXIS){
                SpringLayout spl = (SpringLayout) NO_POI_SAVED.getLayout();
                spl.removeLayoutComponent(NO_POI_SAVED.getDescription());
                spl.putConstraint(SpringLayout.NORTH, NO_POI_SAVED.getDescription(), HORIZONTAL_NOPOI_LABEL_NORTH_OFFSET, SpringLayout.NORTH, NO_POI_SAVED);
                spl.putConstraint(SpringLayout.HORIZONTAL_CENTER, NO_POI_SAVED.getDescription(), NOPOI_SMALL_OFFSET, SpringLayout.HORIZONTAL_CENTER, NO_POI_SAVED);
                add(NO_POI_SAVED);
            } else {
                SpringLayout spl = (SpringLayout) NO_POI_SAVED.getLayout();
                spl.removeLayoutComponent(NO_POI_SAVED.getDescription());
                spl.putConstraint(SpringLayout.HORIZONTAL_CENTER, NO_POI_SAVED.getDescription(), NOPOI_SMALL_OFFSET, SpringLayout.HORIZONTAL_CENTER, NO_POI_SAVED);
                spl.putConstraint(SpringLayout.VERTICAL_CENTER, NO_POI_SAVED.getDescription(), NOPOI_LARGE_OFFSET, SpringLayout.VERTICAL_CENTER, NO_POI_SAVED);
                add(NO_POI_SAVED);
            }
        }
    }
}
