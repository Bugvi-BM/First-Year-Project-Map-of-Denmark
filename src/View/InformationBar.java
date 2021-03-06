package View;

import Helpers.GlobalValue;
import Helpers.ThemeHelper;

import javax.swing.*;
import java.awt.*;


public class InformationBar extends View {

    private Dimension dimension;

    public InformationBar() {
        setPreferredSize(dimension = new Dimension(GlobalValue.getLargeInformationBarWidth(), 1000));
        setLayout(new SpringLayout());
        setBorder(BorderFactory.createLineBorder(ThemeHelper.color("border")));
        applyTheme();
    }

    public void applyTheme() {
        setBorder(BorderFactory.createLineBorder(ThemeHelper.color("border")));
        setBackground(ThemeHelper.color("toolbar"));
    }

}
