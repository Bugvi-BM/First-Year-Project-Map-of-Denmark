package View;

import Helpers.ThemeHelper;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * The JourneyDescriptionField is a visual component that displays more
 * information about the currently found route.
 */
public class JourneyDescriptionField extends View {

    private JTextArea field;
    private JScrollPane scroll;

    private final int TITLE_FONT_SIZE = 15;
    private final int SCROLL_SPEED = 18;


    public JourneyDescriptionField() {
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.RED), "Travel Description:", TitledBorder.LEFT, TitledBorder.ABOVE_TOP, new Font(getFont().getName(), getFont().getStyle(), TITLE_FONT_SIZE), ThemeHelper.color("icon")));
        field = new JTextArea();
        scroll = new JScrollPane(field);
        setLayout(new BorderLayout());
        field.setOpaque(true);
        field.setEditable(false);
        scroll.setOpaque(true);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setBorder(BorderFactory.createLineBorder(ThemeHelper.color("toolbar")));
        scroll.getVerticalScrollBar().setUI(new CustomScrollbarUI());
        scroll.getVerticalScrollBar().setUnitIncrement(SCROLL_SPEED);
        scroll.getHorizontalScrollBar().setUI(new CustomScrollbarUI());
        scroll.getHorizontalScrollBar().setUnitIncrement(SCROLL_SPEED);
        add(scroll, BorderLayout.CENTER);
        field.setBackground(ThemeHelper.color("searchfield"));

    }

    /**
     * Adds a description line to the box
     */
    public void addLine(String line) {
        field.append(line + "\n");
    }

    /**
     * Returns the textarea or field with the description
     */
    public JTextArea getField() {
        return field;
    }

    /**
     * Updates and applies the currently selected theme
     */
    public void applyTheme() {
        setBackground(ThemeHelper.color("toolbar"));
        field.setBackground(ThemeHelper.color("searchfield"));
        field.setForeground(ThemeHelper.color("icon"));
        scroll.setBorder(BorderFactory.createLineBorder(ThemeHelper.color("toolbar")));
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Travel Description:", TitledBorder.LEFT, TitledBorder.ABOVE_TOP, new Font(getFont().getName(), getFont().getStyle(), TITLE_FONT_SIZE), ThemeHelper.color("icon")));
        scroll.setBackground(ThemeHelper.color("toolbar"));
    }
}
