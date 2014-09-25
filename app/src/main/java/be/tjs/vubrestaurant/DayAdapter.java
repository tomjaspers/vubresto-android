package be.tjs.vubrestaurant;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import be.tjs.vubrestaurant.core.MenuItem;
import be.tjs.vubrestaurant.core.RestaurantContainer;

public class DayAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<MenuItem> menuItems;
    private LocalDate selectedDate;
    private RestaurantContainer restaurantContainer;

    public DayAdapter(Context context, ArrayList<MenuItem> menuItems, LocalDate selectedDate) {
        // Cache the LayoutInflate to avoid asking for a new one each time.
        this.layoutInflater = LayoutInflater.from(context);
        this.menuItems = menuItems;
        this.selectedDate = selectedDate;
        this.restaurantContainer = RestaurantContainer.getInstance();
    }

    public void notifyDataSetChangedUnsafe() throws Exception {
        try {
            this.menuItems = this.restaurantContainer.getMenuItemsPerDate(this.selectedDate);
        } catch (Exception e) {
            this.menuItems = new ArrayList<MenuItem>();
            throw new Exception("Exception retrieving menusPerDate", e);
        }
        super.notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        try {
            this.menuItems = this.restaurantContainer.getMenuItemsPerDate(this.selectedDate);
        } catch (Exception e) {
            this.menuItems = new ArrayList<MenuItem>();
        }
        super.notifyDataSetChanged();
    }

    public int getCount() {
        if (menuItems == null) {
            return 0;
        }
        return menuItems.size();
    }

    public Object getItem(int position) {
        return menuItems.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // Use ViewHolder pattern
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.menu_item, parent, false);
            holder = new ViewHolder();
            holder.tvMenuName = (TextView) convertView.findViewById(R.id.txt_menu_naam);
            holder.tvDish = (TextView) convertView.findViewById(R.id.txt_gerecht);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final MenuItem menuItem = (MenuItem) getItem(position);
        holder.tvMenuName.setText(menuItem.getMenuName());
        holder.tvMenuName.setBackgroundColor(Color.parseColor(menuItem.getColor()));
        holder.tvDish.setText(menuItem.getDishName());

        return convertView;
    }

    static class ViewHolder {
        TextView tvMenuName;
        TextView tvDish;
    }
}