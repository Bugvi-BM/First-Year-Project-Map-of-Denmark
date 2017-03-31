package View;

import Helpers.ThemeHelper;

import javax.swing.*;
import java.awt.*;
import java.util.List;


/**
 * Created by  on .
 *
 * @author bugvimagnussen
 * @version 29/03/2017
 */
public class SearchTool extends ToolComponent {

    private JComboBox<String> field;
    private java.util.List<String> addresses;
    private DefaultComboBoxModel<String> model;

    private Dimension searchFieldDimension;
    private JLabel searchLabel;



    public SearchTool(int width) {
        field = new JComboBox<>();
        searchFieldDimension = new Dimension(width, 40);
        setupLayout();
        addSearchLabel();
        add(field);
    }

    @Override
    public void setupLayout() {
        field.setPreferredSize(searchFieldDimension);
        field.setEditable(true);
        field.setFont(new Font(field.getFont().getName(), field.getFont().getStyle(), 20));
        field.setRequestFocusEnabled(true);
        for(Component component : field.getComponents()) if(component instanceof JButton) field.remove(component);

        //Todo force focus loss
        //Todo add implementation for theme change
    }

    public void addListToSearchField(List<String> list) {
        addresses = list;
        model = new DefaultComboBoxModel(list.toArray());
    }

    private void addSearchLabel() {
        searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font(getFont().getName(), getFont().getStyle(), 20));
        add(searchLabel);
    }

    public void changeTheme() {
        setBackground(ThemeHelper.color("toolbar"));
        //setForeground(ThemeHelper.color());
        //field.setBackground(ThemeHelper.color("searchbar"));
        //field.setForeground(ThemeHelper.color("searchtext"));
    }
}
