import Controller.CanvasController;
import Controller.ToolbarController;
import Enums.OSMEnums.WayType;
import Helpers.FileHandler;
import View.Toolbar;
import View.Window;
import junit.framework.TestCase;
import Model.*;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class TestModel extends TestCase {

    @Test
    public void testSingleton() {
        Window window = new Window();
        Model model = Model.getInstance();
        Model model2 = Model.getInstance();
        CanvasController canvasController = CanvasController.getInstance(window);
        try {
            FileHandler.loadDefault("/testRoad.osm");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        assertTrue(model.equals(model2));
        try {
            Method method = Model.class.getDeclaredMethod("resetInstance", new Class[] {});
            method.setAccessible(true);
            method.invoke(model, new Object[] {});
            System.gc();
            method.setAccessible(false);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testClear() {
        Window window = new Window();
        Model model = Model.getInstance();
        CanvasController canvasController = CanvasController.getInstance(window);
        try {
            FileHandler.loadDefault("/testRoad.osm");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        assertEquals(false, model.getWayElements().isEmpty());
        model.clear();
        assertEquals(true, model.getWayElements().get(WayType.ROAD).isEmpty());
        assertEquals(true, model.getWayElements().get(WayType.UNKNOWN).isEmpty());
        try {
            Method method = Model.class.getDeclaredMethod("resetInstance", new Class[] {});
            method.setAccessible(true);
            method.invoke(model, new Object[] {});
            System.gc();
            method.setAccessible(false);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetBounds() {
        Model model = Model.getInstance();
        assertTrue(model.getMinLatitude() == 0.0f);
        assertTrue(model.getMaxLatitude() == 0.0f);
        assertTrue(model.getMinLongitude() == 0.0f);
        assertTrue(model.getMaxLongitude() == 0.0f);
        try {
            Method method = Model.class.getDeclaredMethod("resetInstance", new Class[] {});
            method.setAccessible(true);
            method.invoke(model, new Object[] {});
            System.gc();
            method.setAccessible(false);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSetBounds() {
        Model model = Model.getInstance();
        model.setBounds(2.3f,2.4f,2.5f, 2.6f);
        assertEquals(2.3f, model.getMinLatitude());
        assertEquals(2.4f, model.getMaxLatitude());
        assertEquals(2.5f, model.getMinLongitude());
        assertEquals(2.6f, model.getMaxLongitude());
        try {
            Method method = Model.class.getDeclaredMethod("resetInstance", new Class[] {});
            method.setAccessible(true);
            method.invoke(model, new Object[] {});
            System.gc();
            method.setAccessible(false);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
