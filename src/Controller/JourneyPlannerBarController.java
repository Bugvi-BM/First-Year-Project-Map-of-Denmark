package Controller;

import Helpers.GlobalValue;
import Helpers.ThemeHelper;
import View.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by  on .
 *
 * @author bugvimagnussen
 * @version 03/05/2017
 */
public final class JourneyPlannerBarController extends Controller {

    private static JourneyPlannerBarController instance;

    private final int DISTANCE_BETWEEN_TOOLBAR_AND_SCROLLPANE = GlobalValue.getToolbarHeight() + 10;
    private final int TRANSPORT_BUTTONS_WIDTH = GlobalValue.getLargeInformationBarWidth() - 72;
    private final int CLEAR_SEARCH_BUTTONS_WIDTH = TRANSPORT_BUTTONS_WIDTH - 3;
    private final int TRANSPORT_BUTTONS_HEIGHT = 90;
    private final int CLEAR_SEARCH_BUTTONS_HEIGHT = 60;
    private final int DISTANCE_BETWEEN_BUTTONS_AND_FROMBAR = 10;
    private final int SEARCHBAR_WIDTH = 312;
    private final int SEARCHBAR_HEIGHT = 30;
    private final int JOURNEY_PLANNERBAR_WIDTH = 325;
    private final int JOURNEY_PLANNERBAR_HEIGHT = 650;
    private final int SCROLL_SPEED = 18;
    private final int TITLE_FONT_SIZE = 15;
    private final int DISTANCE_BETWEEN_SEARCHBARS = 4;
    private final int DISTANCE_BETWEEN_TOBAR_TO_CLEARSEARCH_BUTTONS = 10;

    private InformationBar informationBar;
    private SpringLayout journeyPlannerBarLayout;
    private SpringLayout informationBarLayout;
    private JourneyPlannerTransportTypeButtons journeyPlannerTransportTypeButtons;
    private JourneyPlannerBar journeyPlannerBar;
    private SearchTool fromBar;
    private SearchTool toBar;
    private JourneyPlannerSearchClearButtons journeyPlannerSearchClearButtons;
    private JScrollPane largeScroll;

    private boolean isLargeJourneyPlannerVisible;
    private boolean isSmallJourneyPlannerVisible;

    private JourneyPlannerBarController() {
        super();
    }

    public static JourneyPlannerBarController getInstance() {
        if(instance == null) {
            instance = new JourneyPlannerBarController();
        }
        return instance;
    }


    public void setupInformationBar() {
        informationBar = new InformationBar();
        informationBarLayout = (SpringLayout) informationBar.getLayout();
        InformationBarInteractionHandler handler = new InformationBarInteractionHandler();
        informationBar.addMouseListener(handler);
        isLargeJourneyPlannerVisible = false;
        isSmallJourneyPlannerVisible = false;
    }

    public void setupBaseJourneyPlannerBar() {
        journeyPlannerTransportTypeButtons = new JourneyPlannerTransportTypeButtons();
        journeyPlannerTransportTypeButtons.setOpaque(true);
        journeyPlannerBar = new JourneyPlannerBar();
        journeyPlannerBar.setOpaque(true);
        journeyPlannerBarLayout = (SpringLayout) journeyPlannerBar.getLayout();
        fromBar = new SearchTool();
        fromBar.setOpaque(true);
        toBar = new SearchTool();
        toBar.setOpaque(true);
        journeyPlannerSearchClearButtons = new JourneyPlannerSearchClearButtons();
        journeyPlannerSearchClearButtons.setOpaque(true);
        addInteractionHandlersToJourneyPlannerTransportButtons();
        addInteractionHandlerToClearSearchButtons();
    }

    public void setupLargeJourneyPlannerBar() {
        isLargeJourneyPlannerVisible = true;
        informationBar.setPreferredSize(new Dimension(GlobalValue.getLargeInformationBarWidth(), window.getFrame().getHeight()));
        journeyPlannerBar.setMinimumSize(new Dimension(TRANSPORT_BUTTONS_WIDTH, TRANSPORT_BUTTONS_HEIGHT));
        journeyPlannerTransportTypeButtons.setPreferredSize(new Dimension(TRANSPORT_BUTTONS_WIDTH, TRANSPORT_BUTTONS_HEIGHT));
        journeyPlannerSearchClearButtons.setPreferredSize(new Dimension(CLEAR_SEARCH_BUTTONS_WIDTH, CLEAR_SEARCH_BUTTONS_HEIGHT));
        fromBar.getField().setPreferredSize(new Dimension(SEARCHBAR_WIDTH,SEARCHBAR_HEIGHT));
        fromBar.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "From:", TitledBorder.LEFT, TitledBorder.ABOVE_TOP, new Font(fromBar.getFont().getName(), fromBar.getFont().getStyle(), TITLE_FONT_SIZE), ThemeHelper.color("icon")));
        toBar.getField().setPreferredSize(new Dimension(SEARCHBAR_WIDTH, SEARCHBAR_HEIGHT));
        toBar.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "To:", TitledBorder.LEFT, TitledBorder.ABOVE_TOP, new Font(toBar.getFont().getName(), toBar.getFont().getStyle(), TITLE_FONT_SIZE), ThemeHelper.color("icon")));
        setupLargeScroll();
        informationBarLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, largeScroll, 0, SpringLayout.HORIZONTAL_CENTER, informationBar);
        informationBarLayout.putConstraint(SpringLayout.NORTH, largeScroll, DISTANCE_BETWEEN_TOOLBAR_AND_SCROLLPANE, SpringLayout.NORTH, informationBar);
        journeyPlannerBarLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, journeyPlannerTransportTypeButtons, 0, SpringLayout.HORIZONTAL_CENTER, journeyPlannerBar);
        journeyPlannerBarLayout.putConstraint(SpringLayout.NORTH, journeyPlannerTransportTypeButtons, 0, SpringLayout.NORTH, journeyPlannerBar);
        journeyPlannerBarLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, fromBar, 0, SpringLayout.HORIZONTAL_CENTER, journeyPlannerBar);
        journeyPlannerBarLayout.putConstraint(SpringLayout.NORTH, fromBar, DISTANCE_BETWEEN_BUTTONS_AND_FROMBAR, SpringLayout.SOUTH, journeyPlannerTransportTypeButtons);
        journeyPlannerBarLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, toBar, 0, SpringLayout.HORIZONTAL_CENTER, journeyPlannerBar);
        journeyPlannerBarLayout.putConstraint(SpringLayout.NORTH, toBar, DISTANCE_BETWEEN_SEARCHBARS, SpringLayout.SOUTH, fromBar);
        journeyPlannerBarLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, journeyPlannerSearchClearButtons, 0, SpringLayout.HORIZONTAL_CENTER, journeyPlannerBar);
        journeyPlannerBarLayout.putConstraint(SpringLayout.NORTH, journeyPlannerSearchClearButtons, DISTANCE_BETWEEN_TOBAR_TO_CLEARSEARCH_BUTTONS, SpringLayout.SOUTH, toBar);
        themeHasChanged();
        journeyPlannerBar.add(journeyPlannerTransportTypeButtons);
        journeyPlannerBar.add(fromBar);
        journeyPlannerBar.add(toBar);
        journeyPlannerBar.add(journeyPlannerSearchClearButtons);
        informationBar.add(largeScroll);
    }

    private void setupLargeScroll() {
        largeScroll = new JScrollPane(journeyPlannerBar);
        largeScroll.setOpaque(true);
        largeScroll.setPreferredSize(new Dimension(JOURNEY_PLANNERBAR_WIDTH, JOURNEY_PLANNERBAR_HEIGHT));
        largeScroll.setMinimumSize(new Dimension(JOURNEY_PLANNERBAR_WIDTH, JOURNEY_PLANNERBAR_HEIGHT));
        largeScroll.setMaximumSize(new Dimension(JOURNEY_PLANNERBAR_WIDTH, JOURNEY_PLANNERBAR_HEIGHT));
        largeScroll.setBorder(BorderFactory.createEmptyBorder());
        largeScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        largeScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        largeScroll.getVerticalScrollBar().setUI(new CustomScrollbarUI());
        largeScroll.getVerticalScrollBar().setUnitIncrement(SCROLL_SPEED);
    }

    public void setupSmallJourneyPlannerBar() {
        isSmallJourneyPlannerVisible = true;
    }

    public InformationBar getInformationBar() {
        return informationBar;
    }

    public void themeHasChanged() {
        informationBar.applyTheme();
        journeyPlannerTransportTypeButtons.applyTheme();
        fromBar.applyTheme();
        journeyPlannerBar.applyTheme();
        fromBar.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "From:", TitledBorder.LEFT, TitledBorder.ABOVE_TOP, new Font(fromBar.getFont().getName(), fromBar.getFont().getStyle(), TITLE_FONT_SIZE), ThemeHelper.color("icon")));
        fromBar.getField().getEditor().getEditorComponent().setForeground(ThemeHelper.color("icon"));
        toBar.getField().getEditor().getEditorComponent().setForeground(ThemeHelper.color("icon"));
        toBar.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "To:", TitledBorder.LEFT, TitledBorder.ABOVE_TOP, new Font(fromBar.getFont().getName(), fromBar.getFont().getStyle(), TITLE_FONT_SIZE), ThemeHelper.color("icon")));
        journeyPlannerSearchClearButtons.applyTheme();
    }

    private void addInteractionHandlerToClearSearchButtons() {
        journeyPlannerSearchClearButtons.getClearButton().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                journeyPlannerSearchClearButtons.getClearButton().setForeground(ThemeHelper.color("toolHover"));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                journeyPlannerSearchClearButtons.getClearButton().setForeground(ThemeHelper.color("icon"));
            }
        });
        journeyPlannerSearchClearButtons.getSearchButton().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                journeyPlannerSearchClearButtons.getSearchButton().setForeground(ThemeHelper.color("icon"));
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseMoved(e);
                journeyPlannerSearchClearButtons.getSearchButton().setForeground(ThemeHelper.color("toolHover"));
            }
        });
    }

    private void addInteractionHandlersToJourneyPlannerTransportButtons() {
        journeyPlannerTransportTypeButtons.getOnFootButton().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                journeyPlannerTransportTypeButtons.getOnFootButton().setForeground(ThemeHelper.color("toolHover"));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                journeyPlannerTransportTypeButtons.getOnFootButton().setForeground(ThemeHelper.color("icon"));
            }
        });
        journeyPlannerTransportTypeButtons.getBicycleButton().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                journeyPlannerTransportTypeButtons.getBicycleButton().setForeground(ThemeHelper.color("toolHover"));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                journeyPlannerTransportTypeButtons.getBicycleButton().setForeground(ThemeHelper.color("icon"));
            }
        });
        journeyPlannerTransportTypeButtons.getCarButton().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                journeyPlannerTransportTypeButtons.getCarButton().setForeground(ThemeHelper.color("toolHover"));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                journeyPlannerTransportTypeButtons.getCarButton().setForeground(ThemeHelper.color("icon"));
            }
        });
    }

    public void clearJourneyPlannerBar() {
        isLargeJourneyPlannerVisible = false;
        isSmallJourneyPlannerVisible = false;
        journeyPlannerBarLayout.removeLayoutComponent(journeyPlannerTransportTypeButtons);
        journeyPlannerBarLayout.removeLayoutComponent(fromBar);
        journeyPlannerBarLayout.removeLayoutComponent(toBar);
        journeyPlannerBar.removeAll();
        if(largeScroll != null) informationBarLayout.removeLayoutComponent(largeScroll);
        informationBar.removeAll();
    }

    private class InformationBarInteractionHandler extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            if(MainWindowController.getInstance().isMenuToolPopupVisible()) MainWindowController.getInstance().requestMenuToolHidePopup();
            MainWindowController.getInstance().requestSearchToolCloseList();
        }
    }
}