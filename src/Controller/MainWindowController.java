package Controller;

import View.PopupWindow;
import View.Window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import Enums.ToolType;
import Controller.ToolbarController;
import View.ToolComponent;
import View.SearchTool;

/**
 * Created by Búgvi Magnussen on 14-03-2017.
 */
public final class MainWindowController extends WindowController {

    private static final String MAIN_TITLE = "OSM Map Viewer v0.2";

    private static MainWindowController instance;

    private MainWindowController(Window window) {
        super(window);
    }

    public static MainWindowController getInstance() {
        if(instance == null) {
            Window mainWindow = setupWindow();
            instance = new MainWindowController(mainWindow);
        }
        return instance;
    }

    private static Window setupWindow() {
       Window mainWindow = new Window().title(MAIN_TITLE)
                .closeOperation(WindowConstants.EXIT_ON_CLOSE)
                .dimension(new Dimension(1200, 1000))
                .extendedState(JFrame.MAXIMIZED_BOTH)
                .layout(new BorderLayout())
                .relativeTo(null)
                .hide();
        return mainWindow;
    }

    @Override
    protected void specifyKeyBindings() {
        handler.addKeyBinding(KeyEvent.VK_ESCAPE, KeyEvent.VK_UNDEFINED, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Toolkit.getDefaultToolkit().beep();
                if(PopupWindow.confirmBox(null, "Do You Wish to Quit OSM Visualiser?",
                        "PLease Confirm!", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
    }

    public void resetInstance() {
        instance = null;
    }

    private class MainWindowInteractionHandler extends MainWindowController.WindowInteractionHandler {

        @Override
        public void componentResized(ComponentEvent e) {
            super.componentResized(e);
            //Todo implement changes
            System.out.println("Resize");
            SearchTool tool = (SearchTool)ToolbarController.getInstance(window).getToolbar().getTool(ToolType.SEARCH);
            double scaleFactor = window.getDimension().getWidth() / tool.getSize().width;
            tool.setWidth((int)(window.getDimension().width / scaleFactor));
        }

    }

}
