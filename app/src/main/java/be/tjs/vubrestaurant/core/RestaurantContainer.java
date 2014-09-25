package be.tjs.vubrestaurant.core;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class RestaurantContainer {
    @SuppressWarnings("unused")
    private static final String TAG = "RestaurantContainer";

    private Map<LocalDate, List<MenuItem>> menuItemsPerDate;
    private boolean isLoading;

    private static RestaurantContainer restaurantContainerInstance = null;

    public RestaurantContainer() {
        this.menuItemsPerDate = new TreeMap<LocalDate, List<MenuItem>>();
        this.isLoading = false;
    }

    public static RestaurantContainer getInstance() {
        if (restaurantContainerInstance == null)
            restaurantContainerInstance = new RestaurantContainer();
        return restaurantContainerInstance;
    }

    public List<MenuItem> getMenuItemsPerDate(LocalDate date) {
        if (menuItemsPerDate.containsKey(date)) {
            return menuItemsPerDate.get(date);
        }
        return new ArrayList<MenuItem>();
    }

    public void retrieveMenuItemsAndFill() throws Exception {
        if (!this.isLoading) {
            this.isLoading = true;
            try {
                menuItemsPerDate.putAll(RestaurantParser.parseRestaurantEtterbeek());
            } catch (Exception e) {
                clearContainers();
                throw e;
            } finally {
                this.isLoading = false;
            }
        }
    }

    public boolean hasMenuForDate(LocalDate date) {
        return menuItemsPerDate.containsKey(date);
    }

    public void clearContainers() {
        menuItemsPerDate.clear();
        isLoading = false;
    }

    public boolean isCleared() {
        return menuItemsPerDate.isEmpty();
    }


}
