package Controller.ToolbarControllers;

import Controller.Controller;
import Enums.ToolType;
import Enums.ToolbarType;
import Helpers.OSDetector;
import View.MenuTool;
import View.ToolComponent;
import View.Toolbar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import static javax.swing.SpringLayout.NORTH;
import static javax.swing.SpringLayout.WEST;

/**
 * This class controls the menu tool. It determines the visual dimensions of the menu,
 * calculates its position, specifies input controls and handles events related to the menu tool.
 */
public final class MenuToolController extends Controller {

    private final int POPUP_MARGIN_LEFT = 10;
    private final int POPUP_MARGIN_HEIGHT = 340;
    private final int POPUP_MARGIN_WIDTH = 60;
    private final int POPUP_MARGIN_TOP = 10;
    private final int POPUP_MARGIN_BETWEEN_TOOLS = 63;
    private final int POPUP_MARGIN_LARGER_BETWEEN_TOOLS = 70;
    private final int POPUPMENU_LEFT_OFFSET = 10;
    private final int POPUPMENU_YAXIS_OFFSET = 15;

    private static MenuToolController instance;
    private MenuTool popupMenu;
    private Toolbar toolbar;

    /**
     * private constructor, called by getInstance
     */
    private MenuToolController() { super(); }


    /**
     * Returns the singleton instance of the MenuToolController.
     * @return the singleton
     */
    public static MenuToolController getInstance()
    {
        if (instance == null) {
            instance = new MenuToolController();
        }
        return instance;
    }

    /**
     * Sets up the menu tool.
     */
    protected void setupMenuTool()
    {
        toolbar = ToolbarController.getInstance().getToolbar();
        popupMenu = new MenuTool();
        addActionsToToolsMenu();
    }

    /**
     * Hides the popup menu.
     */
    protected void hidePopupMenu()
    {
        if (popupMenu != null && popupMenu.isVisible()) {
            popupMenu.hidePopupMenu();
            if (ToolbarController.getInstance().getType() == ToolbarType.SMALL)
                ToolbarController.getInstance()
                        .getToolbar()
                        .getTool(ToolType.MENU)
                        .toggleActivate(false);
        }
    }

    /**
     * Lets a client know whether the popup menu is visible.
     * @return is the popup menu visible
     */
    protected boolean isPopupVisible() {
        if(popupMenu != null) return popupMenu.isVisible();
        else return false;
    }

    /**
     * Determines the layout of the popup menu, and adds all tools to the popup menu.
     */
    protected void setupLayoutForMenuTool()
    {
        ToolComponent load = toolbar.getTool(ToolType.LOAD);
        popupMenu.getLayout().putConstraint(WEST, load, POPUP_MARGIN_LEFT, WEST,
            popupMenu.getPopupMenu());
        popupMenu.getLayout().putConstraint(NORTH, load, POPUP_MARGIN_TOP, NORTH,
            popupMenu.getPopupMenu());
        popupMenu.addTool(load);
        ToolComponent save = toolbar.getTool(ToolType.SAVE);
        popupMenu.getLayout().putConstraint(WEST, save, POPUP_MARGIN_LEFT, WEST,
            popupMenu.getPopupMenu());
        popupMenu.getLayout().putConstraint(NORTH, save, POPUP_MARGIN_BETWEEN_TOOLS,
            NORTH, load);
        popupMenu.addTool(toolbar.getTool(ToolType.SAVE));
        ToolComponent poi = toolbar.getTool(ToolType.POI);
        popupMenu.getLayout().putConstraint(WEST, poi, POPUP_MARGIN_LEFT, WEST,
            popupMenu.getPopupMenu());
        popupMenu.getLayout().putConstraint(NORTH, poi, POPUP_MARGIN_BETWEEN_TOOLS,
            NORTH, save);
        popupMenu.addTool(toolbar.getTool(ToolType.POI));
        ToolComponent routes = toolbar.getTool(ToolType.ROUTES);
        popupMenu.getLayout().putConstraint(WEST, routes, POPUP_MARGIN_LEFT, WEST,
                popupMenu.getPopupMenu());
        popupMenu.getLayout().putConstraint(NORTH, routes, POPUP_MARGIN_BETWEEN_TOOLS,
                NORTH, poi);
        popupMenu.addTool(routes);
        ToolComponent settings = toolbar.getTool(ToolType.SETTINGS);
        popupMenu.getLayout().putConstraint(WEST, settings, POPUP_MARGIN_LEFT, WEST,
            popupMenu.getPopupMenu());
        popupMenu.getLayout().putConstraint(
            NORTH, settings, POPUP_MARGIN_LARGER_BETWEEN_TOOLS, NORTH, routes);
        popupMenu.addTool(toolbar.getTool(ToolType.SETTINGS));
        toolbar.getTool(ToolType.MENU).add(popupMenu.getPopupMenu());
        popupMenu.getPopupMenu().setPopupSize(POPUP_MARGIN_WIDTH,
            POPUP_MARGIN_HEIGHT);
        popupMenu.showPopupMenu();
        popupMenu.hidePopupMenu();
        popupMenu.getPopupMenu().repaint();
    }

    /**
     * Specifies key bindings for the tools in the popup menu.
     */
    private void addActionsToToolsMenu()
    {
        addAction(KeyEvent.VK_L, OSDetector.getActivationKey(),
            new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    if (popupMenu.isVisible()) {
                        ToolbarController.getInstance().toolEvent(ToolType.LOAD);
                        hidePopupMenu();
                    }
                }
            });
        addAction(KeyEvent.VK_S, OSDetector.getActivationKey(),
            new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (popupMenu.isVisible()) {
                        ToolbarController.getInstance().toolEvent(ToolType.SAVE);
                        hidePopupMenu();
                    }
                }
            });
        addAction(
            KeyEvent.VK_COMMA, OSDetector.getActivationKey(), new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    if (popupMenu.isVisible()) {
                        ToolbarController.getInstance().toolEvent(ToolType.SETTINGS);
                        hidePopupMenu();
                    }
                }
            });
        addAction(KeyEvent.VK_P, OSDetector.getActivationKey(), new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(popupMenu.isVisible()) {
                    ToolbarController.getInstance().toolEvent(ToolType.POI);
                    hidePopupMenu();
                }
            }
        });
        addAction(KeyEvent.VK_R, OSDetector.getActivationKey(), new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(popupMenu.isVisible()) {
                    ToolbarController.getInstance().toolEvent(ToolType.ROUTES);
                    hidePopupMenu();
                }
            }
        });
    }

    /**
     * Adds an action to an action map of the Menu Tool.
     * @param key the key to activate the action
     * @param activationKey which key to be held down in order to activate the action (if any).
     * @param action the action to be triggered when the key is pressed.
     */
    private void addAction(int key, int activationKey, AbstractAction action)
    {
        toolbar.getTool(ToolType.MENU)
            .getInputMap(JComponent.WHEN_FOCUSED)
            .put(KeyStroke.getKeyStroke(key, activationKey), action.toString());
        toolbar.getTool(ToolType.MENU)
            .getActionMap()
            .put(action.toString(), action);
    }

    /**
     * Activates or deactivates the popup menu, depending on if the popup menu is already
     * visible.
     */
    protected void menuToolActivated()
    {
        if (!popupMenu.isVisible()) {
            popupMenu.showPopupMenu();
            toolbar.getTool(ToolType.MENU).grabFocus();
        } else {
            popupMenu.hidePopupMenu();
            ToolbarController.getInstance()
                .getToolbar()
                .getTool(ToolType.MENU)
                .toggleActivate(false);
        }
        popupMenu.setLocation(calculatePosition());
    }

    /**
     * Recalculates the position of the popup menu if it is visible
     * and the window is resized.
     */
    protected void windowResizedEvent()
    {
        if (popupMenu.isVisible())
            popupMenu.setLocation(calculatePosition());
    }

    /**
     * Recalculates the position of the popup menu if it is visible
     * and the window is moved.
     */
    protected void windowMovedEvent()
    {
        if (popupMenu.isVisible())
            popupMenu.setLocation(calculatePosition());
    }

    /**
     * Calculates the position of the popup menu.
     * @return the new position of the popup menu.
     */
    private Point calculatePosition()
    {
        return new Point(toolbar.getLocationOnScreen().x + POPUPMENU_LEFT_OFFSET,
            (toolbar.getLocationOnScreen().y + toolbar.getHeight()) - POPUPMENU_YAXIS_OFFSET);
    }

}
