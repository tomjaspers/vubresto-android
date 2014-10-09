package be.tjs.vubrestaurant.core;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class RestaurantContainer {
    @SuppressWarnings("unused")
    private static final String TAG = "RestaurantContainer";

    /**
     * We will always show 7 days in the app (today + 6 following),
     * regardless of how the menus are online.
     */
    private static final int NUM_DAYS = 7;

    private final LocalDate[] dates;
    private final int language;
    // The actual containers for menus of Etterbeek / Jette
    private Map<LocalDate, List<Menu>> menusPerDateEtterbeek;
    private Map<LocalDate, List<Menu>> menusPerDateJette;
    // The menus of the current active restaurant
    private Map<LocalDate, List<Menu>> menusPerDate;
    private int activeRestaurant;
    private boolean loading = true;


    public RestaurantContainer(int activeRestaurant, int language) {
        this.menusPerDateEtterbeek = new TreeMap<LocalDate, List<Menu>>();
        this.menusPerDateJette = new TreeMap<LocalDate, List<Menu>>();
        this.dates = generateDates();
        this.language = language;
        setActiveRestaurant(activeRestaurant);
    }

    private static LocalDate[] generateDates() {
        LocalDate todayDate = new LocalDate();
        LocalDate[] dates = new LocalDate[NUM_DAYS];
        for (int i = 0; i < NUM_DAYS; i++) {
            final LocalDate date = new LocalDate(todayDate).plusDays(i);
            dates[i] = date;
        }
        return dates;
    }

    public void setActiveRestaurant(int restaurant) {
        this.activeRestaurant = restaurant;
        this.menusPerDate = getMenusPerDateForActiveRestaurant();
    }

    public List<Menu> getMenus(LocalDate date) {
        if (menusPerDate.containsKey(date)) {
            return menusPerDate.get(date);
        }
        return new ArrayList<Menu>();
    }

    private Map<LocalDate, List<Menu>> getMenusPerDateForActiveRestaurant() {
        if (activeRestaurant == Constants.RESTO_ETTERBEEK) {
            return menusPerDateEtterbeek;
        } else if (activeRestaurant == Constants.RESTO_JETTE) {
            return menusPerDateJette;
        }
        return null;
    }

    public void retrieveMenuItemsAndFill() throws Exception {
        try {
            menusPerDate.clear();
            for (LocalDate date : dates) {
                menusPerDate.put(date, new ArrayList<Menu>());
            }
            RestaurantParser.parseRestaurant(menusPerDate, activeRestaurant, language);
        } catch (Exception e) {
            menusPerDate.clear();
            throw e;
        }
    }

    public boolean hasMenus(LocalDate date) {
        return menusPerDate.containsKey(date) && menusPerDate.get(date) != null;
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public boolean isEmpty() {
        return menusPerDate.isEmpty();
    }

    public LocalDate[] getDates() {
        return dates;
    }

}
