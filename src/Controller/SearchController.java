package Controller;


import Helpers.StringSorter;
import Model.Addresses.Value;
import Model.Model;
import View.PopupWindow;
import View.SearchTool;

import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The superclass of the program's multiple searchtools.
 * The primary idea with this class is to decrease code duplication.
 * The functionality of the searchtools which is common, is gathered in this class.
 * Especially the functionality of the auto completion feature.
 */
public abstract class SearchController extends Controller {

        protected SearchTool searchTool;
        protected boolean allowSearch;
        private boolean validSearch;
        protected String currentQuery;
        protected final int[] prohibitedKeys = new int[] {KeyEvent.VK_CONTROL, KeyEvent.VK_SHIFT, KeyEvent.VK_ALT, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT,
                KeyEvent.VK_META, KeyEvent.VK_WINDOWS, KeyEvent.VK_CAPS_LOCK, KeyEvent.VK_UNDEFINED};
        protected javax.swing.Timer queryTimer;
        protected final int QUERY_DELAY = 600;

    /**
     * Creates a new controller.
     */
    protected SearchController() {
            super();
        }

        protected abstract void setupSearchTool();

        protected abstract void themeHasChanged();

        public abstract void closeSearchToolList();

        public void setToolTip(String tip) {
            searchTool.getField().setToolTipText(tip);
        }

    /**
     * Search activated is called when the user presses enter
     * or clicks on the search button after entering an address.
     * The method will return a point if the tst contains a direct match,
     * if this is not the case the method will create a popupwindow and come
     * with suggestions. (Strings that match the input)
     * @return the Point associated with the address.
     */
    protected Point2D.Float searchActivatedEvent() {
            validSearch = false;
            if(!allowSearch) {
                searchTool.getField().requestFocus();
            }
            else if(allowSearch && searchTool.getText().isEmpty()) {
                searchTool.getField().requestFocus();
            }
                Point2D.Float point = null;
                ArrayList<Value> list = Model.getInstance().getTst().get(searchTool.getText());
                //if there exists a value
                if(list != null) {
                    validSearch = true;
                    //if the address exists multiple times
                    if (list.size() > 1) {
                        String result = selectCity(list);
                        String[] unsortedCities = buildCityNameList(list);
                        if(result != null) {
                            for(int i = 0; i < unsortedCities.length; i++){
                                if(unsortedCities[i].equals(result)){
                                    point = new Point2D.Float(list.get(i).getX(), list.get(i).getY());
                                    break;
                                }
                            }
                        }
                    } else {
                        point = new Point2D.Float(list.get(0).getX(), list.get(0).getY());
                    }
                }else{
                    //finding strings that could match the input
                    String[] matches = manageSearchResults();
                    if(matches.length > 0) {
                        selectAddress(matches);
                        ArrayList<Value> selectedStrings = Model.getInstance().getTst().get(currentQuery);
                        if(selectedStrings != null && selectedStrings.size() > 0) {
                            point = new Point2D.Float(selectedStrings.get(0).getX(), selectedStrings.get(0).getY());
                        }
                    }else{
                        PopupWindow.infoBox(null, "Could not find a matching address", "Try again");
                    }
                }
                allowSearch = true;
                return point;
        }

    /**
     * Converts the values of the match to an array of strings.
     * @param list the list of values that match the pattern.
     * @return
     */

    protected String[] buildCityNameList(ArrayList<Value> list){
            String[] cities = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getCitynameindex() != 0) {
                    cities[i] = Model.getInstance().getIndexToCity(list.get(i).getCitynameindex());
                } else {
                    cities[i] = searchTool.getText() + " " + i;
                }
            }
            return cities;
        }

    /**
     * Creates a sorted list of cities to choose from.
     * @param list The list of values that match the pattern.
     * @return
     */

        protected String selectCity(ArrayList<Value> list){
            String[] cities = buildCityNameList(list);
            StringSorter.sort(cities);
            String result = PopupWindow.confirmBox(null, "Select a City:", "Multiple Search Results!", cities);
            if(result != null) {
                return result;
            }else return null;
        }

    /**
     * Sorts the addresses that match the pattern and opens a confirmbox that lets the user choose
     * between one of the addresses.
     * @param matches All strings that match the pattern (unsorted)
     */

    protected void selectAddress(String[] matches){
            StringSorter.sort(matches);
            System.out.println(matches.length);
            String result = PopupWindow.confirmBox(null, "Select an Address:", "Multiple Search Results!", matches);
            if(result != null) {
                validSearch = true;
                currentQuery = result;
                searchTool.setText(currentQuery);
            }
        }

    /**
     * Shows all keys in the TST that could match the user input in a popup.
     */

    protected void showMatchingResults() {
            if (searchTool.getField().isPopupVisible() && searchTool.getField().getItemCount() == 0)
                searchTool.getField().hidePopup();
            searchTool.getField().removeAllItems();
            if (currentQuery == null || currentQuery.equals("")) {
                searchTool.getField().removeAllItems();
                return;
            } else {
                String[] listToShow = manageSearchResults();
                if (listToShow == null) return;
                listToShow = sortByBestMatch(listToShow);
                for (String s : listToShow) {
                    searchTool.getField().addItem(s);
                }
            }
            if(searchTool.getField().getModel().getSize() != 0)
            {
                if(searchTool.getField().getModel().getSize() <= 8) {
                    searchTool.getField().setMaximumRowCount(searchTool.getField().getModel().getSize());
                } else if (searchTool.getField().getModel().getSize() > 8){
                    searchTool.getField().setMaximumRowCount(8);
                }
                searchTool.getField().showPopup();
            }
            else searchTool.getField().hidePopup();
        }

    /**
     * Builds a string array of keys that possibly match the user input.
     * Sorts the array by significance of keys.
     * @return
     */

    private String[] manageSearchResults(){
        if(currentQuery == null) return null;
        HashMap<Boolean, ArrayList<String>> map = Model.getInstance().getTst().keysThatMatch(currentQuery.toLowerCase());
        ArrayList<String> listToShow = new ArrayList<>();
        for (String s : map.get(true)) {
            listToShow.add(s);
        }
        int i = 0;
        if(currentQuery.length() < 4) {
            while (listToShow.size() <= 10 && i < map.get(false).size()) {
                listToShow.add(map.get(false).get(i));
                i++;
            }
        }else{
            while (i < map.get(false).size()) {
                listToShow.add(map.get(false).get(i));
                i++;
            }
        }
        String[] matchesArray = new String[listToShow.size()];
        for (int j = 0; j < matchesArray.length ; j++) {
            matchesArray[j] = listToShow.get(j);
        }
        return matchesArray;
    }

    /**
     * Sorts the string array by giving strings that start with the user input
     * a higher priority than strings that only contain the user input.
     * @param matches
     * @return
     */

        protected String[] sortByBestMatch(String[] matches){
            ArrayList<String> goodMatch = new ArrayList<>();
            ArrayList<String> badMatch = new ArrayList<>();
            for(String s : matches){
                if(s.startsWith(currentQuery)){
                    goodMatch.add(s);
                }else badMatch.add(s);
            }
            String[] sortedMatches = new String[matches.length];
            int currentIndex = 0;
            for(int i = 0; i < goodMatch.size(); i++){
                    sortedMatches[currentIndex] = goodMatch.get(i);
                currentIndex++;
            }
            for(int i = 0; i < badMatch.size(); i++){
                    sortedMatches[currentIndex] = badMatch.get(i);

                currentIndex++;
            }

            return sortedMatches;
        }

    /**
     * this method checks if the component has focus.
     * @return does the component have focus (true/false)
     */
    public boolean doesSearchbarHaveFocus() {
            return searchTool.getField().getEditor().getEditorComponent().hasFocus();
        }

    /**
     * Checks if a key is valid.
     * @param e the key
     * @return true if the key is valid, false if not.
     */
    protected boolean checkForProhibitedKey(KeyEvent e) {
            for (int key : prohibitedKeys) {
                if (e.getKeyCode() == key) return true;
            }
            return false;
        }

    protected abstract void specifyKeyBindings();

    protected boolean isValidSearch() {
        return validSearch;
    }

    /**
     * Sets the current query.
     *(The current query is the the text in the searchtool field.)
     * @param query
     */
    protected void setCurrentQuery(String query) {
        currentQuery = query;
    }

}
