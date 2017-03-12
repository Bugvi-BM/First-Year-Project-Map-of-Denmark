package Controller;

import Enums.ToolType;
import Helpers.FileHandler;
import Helpers.GlobalConstant;
import Model.Model;
import View.PopupWindow;
import View.ToolFeature;
import View.Toolbar;
import View.Window;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Class details:
 *
 * @author Andreas Blanke, blan@itu.dk
 * @version 06-03-2017.
 * @project BFST
 */
public final class ToolbarController extends Controller {
    private Window window;
    private Toolbar toolbar;
    private static ToolbarController instance;

    private ToolbarController(Window window) {
        toolbar = new Toolbar();
        this.window = window;
        this.window.addComponent(BorderLayout.PAGE_START, toolbar);
        addInteractorsToTools();
    }

    public static ToolbarController getInstance(Window window) {
        if (instance == null) {
            instance = new ToolbarController(window);
        }
        return instance;
    }

    private void addInteractorsToTools() {
        addInteractorToLoadTool();
        addInteractorToSaveTool();
    }

    private void addInteractorToSaveTool() {
        toolbar.addInteractorToTool(ToolType.SAVE, new ToolInteractor(ToolType.SAVE, KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
    }

    private void addInteractorToLoadTool() {
        toolbar.addInteractorToTool(ToolType.LOAD, new ToolInteractor(ToolType.LOAD, KeyEvent.VK_L, KeyEvent.CTRL_DOWN_MASK));
    }

    private void toolEvent(ToolType type) {
        switch (type) {
            case LOAD:
                loadEvent();
                break;
            case SAVE:
                saveEvent();
                break;
        }
    }

    private void loadEvent() {
        toolbar.toggleWellOnTool(ToolType.LOAD);
        FileNameExtensionFilter[] filters = new FileNameExtensionFilter[]{
                new FileNameExtensionFilter("OSM Files", GlobalConstant.osmFilter),
                new FileNameExtensionFilter("ZIP Files", GlobalConstant.zipFilter)
        };
        JFileChooser chooser = PopupWindow.fileLoader(false, filters);
        if (chooser != null) {
            Model.getInstance().clear();
            CanvasController.resetBounds();
            FileHandler.load(chooser.getSelectedFile().toString());
            Model.getInstance().modelHasChanged();
            CanvasController.adjustToBounds();
        }
        toolbar.toggleWellOnTool(ToolType.LOAD);
    }

    private void saveEvent() {
        toolbar.toggleWellOnTool(ToolType.SAVE);
        PopupWindow.infoBox(null, "You activated save tool");
        toolbar.toggleWellOnTool(ToolType.SAVE);
    }

   public class ToolInteractor extends MouseAdapter {

        private ToolType type;
        private ToolFeature tool;
        private int keyEvent;
        private int activationKey;

        public ToolInteractor(ToolType type, int keyEvent, int activationKey) {
            this.type = type;
            this.keyEvent = keyEvent;
            this.activationKey = activationKey;
            tool = (ToolFeature) toolbar.getTool(type);
            setKeyShortCuts();
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            toolEvent(type);
        }


        private void setKeyShortCuts() {
            String event = type.toString().toLowerCase();
            tool.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                    .put(KeyStroke.getKeyStroke(keyEvent, activationKey), event);
            tool.getActionMap().put(event, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    toolEvent(type);
                }
            });
        }

    }

}
