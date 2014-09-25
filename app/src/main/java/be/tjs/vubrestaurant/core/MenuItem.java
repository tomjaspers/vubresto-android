package be.tjs.vubrestaurant.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MenuItem {
    private String menuName;
    private String dishName;
    private String color;

    private static final Map<String, String> COLOR_MAP;
    private static final String[] EXTRA_COLORS = new String[]{
            "#f0eb93", // licht yellow
            "#c6ac86" // brown-orange-yellow-ish?
    };
    private static int extraColorCounter = 0;

    static {
        Map<String, String> map = new HashMap<String, String>(16);
        map.put("soep", "#fdb85b");      // yellow
        map.put("menu 1", "#68b6f3");    // blue
        map.put("dag menu", "#68b6f3");  // blue
        map.put("health", "#ff9861");    // orange
        map.put("menu 2", "#cc93d5");    // purple
        map.put("veggie", "#87b164");    // green
        map.put("pasta", "#de694a");     // red
        map.put("pasta bar", "#de694a"); // red
        map.put("wok", "#6c4c42");       // brown
        COLOR_MAP = Collections.unmodifiableMap(map);
    }

    public MenuItem(String menuName, String dishName) {
        super();
        this.menuName = menuName;
        this.dishName = dishName;

        this.assignColor();
    }

    private void assignColor() {
        // Select the appropriate color based on the name
        this.color = COLOR_MAP.get(this.menuName.toLowerCase());

        // If the menu has a different name, select one of the extra random colors
        if (this.color == null) {
            this.color = EXTRA_COLORS[extraColorCounter++];
            if (extraColorCounter > EXTRA_COLORS.length) {
                extraColorCounter = 0;
            }
        }
    }

    public String getMenuName() {
        return menuName;
    }

    public String getDishName() {
        return dishName;
    }

    public String getColor() {
        return color;
    }

    public boolean isEmpty() {
        return (this.dishName == null || this.dishName.isEmpty() || this.dishName.equals(""));
    }
}
