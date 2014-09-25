package be.tjs.vubrestaurant.core;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class RestaurantParser {
    @SuppressWarnings("unused")
    private static final String TAG = "TimetableParser";

    private static final int NUM_DAYS = 7;

    private static final String URL_ETTERBEEK = "http://my.vub.ac.be/resto/etterbeek";
    private static final String URL_JETTE = "http://my.vub.ac.be/resto/jette";

    public static final String NBSP_IN_UTF8 = "\u00a0";

    public static LocalDate[] getDates() {
        LocalDate todayDate = new LocalDate();
        LocalDate[] dates = new LocalDate[NUM_DAYS];
        for (int i = 0; i < NUM_DAYS; i++) {
            LocalDate date = new LocalDate(todayDate).plusDays(i);
            dates[i] = date;
        }
        return dates;
    }

    public static Map<LocalDate, List<MenuItem>> parseRestaurantEtterbeek() throws Exception {
        LocalDate[] dates = getDates();

        // Init the local container for the menus
        Map<LocalDate, List<MenuItem>> menuItemsPerDate = new TreeMap<LocalDate, List<MenuItem>>();
        for (LocalDate date : dates) {
            menuItemsPerDate.put(date, new ArrayList<MenuItem>());
        }

        // Retrieve the document (i.e., the website)
        Document doc = Jsoup.connect(URL_ETTERBEEK).get();

        // Get the div's: contains the DATE and the TABLE with menu info
        Elements divs = doc.select("#content .views-row");

        // There is some inconsistency with how many dates are posted on the site
        // Code below is hacky but is "safe" against the inconsistencies
        int i = 0;
        int j = 0;
        while (i < dates.length && j < divs.size()) {
            LocalDate date = dates[i++];
            if (date.getDayOfWeek() == DateTimeConstants.SATURDAY ||
                    date.getDayOfWeek() == DateTimeConstants.SUNDAY) {
                continue;
            }
            Element div = divs.get(j++);

            // dateElement could be used in future for a better way to connect menus to a date
            // Element dateElement = div.select(".date-display-single").first();
            Element table = div.select("table").first();
            Elements tableRows = table.select("tr");

            // Loop through the <tr>, each containing 2 <td> with menu name and dish name, resp.
            List<MenuItem> menuItemsOfThisDate = new ArrayList<MenuItem>();
            for (Element tableRow : tableRows) {
                Elements rowTds = tableRow.select("td");

                Element tdMenuName = rowTds.get(0);
                Element tdDishName = rowTds.get(1);

                // Sometimes they add some &nbsp; on the menus for no real reason
                String menuName = tdMenuName.text().replaceAll(NBSP_IN_UTF8, " ").trim();
                String dishName = tdDishName.text().replaceAll(NBSP_IN_UTF8, " ").trim();

                MenuItem menuItem = new MenuItem(menuName, dishName);
                if (!menuItem.isEmpty()) {
                    menuItemsOfThisDate.add(menuItem);
                }
            }
            menuItemsPerDate.put(date, menuItemsOfThisDate);
        }
        return menuItemsPerDate;
    }
}

