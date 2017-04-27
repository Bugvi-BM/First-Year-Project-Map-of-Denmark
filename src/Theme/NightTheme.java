package Theme;

import java.awt.*;

/**
 * Created by  on .
 *
 * @author bugvimagnussen
 * @version 24/03/2017
 */
public class NightTheme implements Theme {

    private String name = "Night";

    public Color rail()
    {
        return new Color(0x999999);
    }
    public Color background()
    {
        return new Color(0x242f3e);
    }
    public Color toolbar()
    {
        return new Color(0x3f3f3f);
    }
    public Color icon()
    {
        return new Color(0xFFFFFF);
    }
    public Color boundary()
    {
        return new Color(0xD31BC6);
    }
    public Color border()
    {
        return new Color(0x000000);
    }
    public Color searchfield()
    {
        return new Color(0x323236);
    }
    public Color defaulttext() { return new Color(0xD1D1D1); }
    public Color toolTipBackground() { return new Color(0x555258); }
    public Color toolTipForeground() { return new Color(0xFEFDFF); }
    public Color canvasPopupBackground() { return new Color(0x7562A9); }
    public Color canvasPopupForeground() { return new Color(0xFFFFFF); }
    public Color toolActivated() { return new Color(0xB2ACB1); }

    public Color cityName() { return new Color(0xCCC9C7); }
    public Color roadName() { return new Color(0x000000); }
    public Color barName() { return new Color(0xF9FF17); }
    public Color nightClubName() { return new Color(0xF9FF17); }
    public Color fastFoodName() { return new Color(0xF9FF17); }

    public Color hospital() { return new Color(0xD30408); }
    public Color placeOfWorship() { return new Color(0x000000); }
    public Color parkingAmenity() { return new Color(0x000000); }

    public Color water()
    {
        return new Color(0x515c6d);
    }
    public Color park() { return new Color(0x044512); }
    public Color forest()
    {
        return new Color(0x033A11);
    }
    public Color grassland()
    {
        return new Color(0x043A0F);
    }
    public Color grass()
    {
        return new Color(0x02370E);
    }
    public Color heath()
    {
        return new Color(0x616F37);
    }
    public Color meadow()
    {
        return new Color(0x033A10);
    }
    public Color farmland() { return new Color(0x616F37); }
    public Color beach()
    {
        return new Color(0x5F5C47);
    }
    public Color building()
    {
        return new Color(0x34445A);
    }
    public Color bridge(){
        return new Color(0x000000);
    }
    public Color sportspitch(){
        return new Color(0x044512);
    }
    public Color wetland(){
        return new Color(0x000000);
    }
    public Color commonland(){
        return new Color(0x044512);
    }
    public Color playground(){
        return new Color(0x044512);
    }
    public Color parking(){
        return new Color(0x000000);
    }
    public Color sportstrack(){ return new Color(0x044512); }

    // Roads
    public Color motorway() { return new Color(0xD47487); }
    public Color trunkRoad() { return new Color(0xC06678); }
    public Color primaryRoad() { return new Color(0xAE6202); }
    public Color secondaryRoad() { return new Color(0x998206); }
    public Color tertiaryRoad() { return new Color(0x787878); }
    public Color unclassifiedRoad() { return new Color(0x787878); }
    public Color residentialRoad() { return new Color(0x787878); }
    public Color livingStreet() { return new Color(0x787878); }
    public Color serviceRoad() { return new Color(0x787878); }
    public Color busGuideway() { return new Color(0x787878); }
    public Color escape() { return new Color(0x787878); }
    public Color raceway() { return new Color(0x787878); }
    public Color pedestrianStreet() { return new Color(0x787878); }
    public Color track() { return new Color(0x787878); }
    public Color steps() { return new Color(0x787878); }
    public Color footway() { return new Color(0x787878); }
    public Color footwayArea() { return new Color(0x45444E); }
    public Color bridleway() { return new Color(0x787878); }
    public Color cycleway() { return new Color(0x787878); }
    public Color path() { return new Color(0x787878); }
    public Color road() { return new Color(0x787878); }
    public Color roadBorder() { return new Color(0000000); }

    @Override
    public String getName()
    {
        return name;
    }
}
