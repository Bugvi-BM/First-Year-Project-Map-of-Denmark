package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Class details:
 *
 * @author Niclas Hedam, nhed@itu.dk
 * @version 18-03-2017
 */
public class ThemeSetting extends View {


    private JLabel themeLabel;
    private JComboBox<String> themeList;

    /** Constructor of the Infobar */
    public ThemeSetting() {
        setPreferredSize(new Dimension(400,500));

        themeLabel = new JLabel("Theme:");

        String[] themes = new String[] {"Standard"};
        themeList = new JComboBox<>(themes);
        add(themeLabel);
        add(themeList);


        // get the selected item:
        //String theme = (String) themeList.getSelectedItem();
        //System.out.println("You seleted the theme: " + theme);

    }


}