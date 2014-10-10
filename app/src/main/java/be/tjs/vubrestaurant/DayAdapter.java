package be.tjs.vubrestaurant;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.joda.time.LocalDate;

import java.util.List;

import be.tjs.vubrestaurant.core.Menu;
import be.tjs.vubrestaurant.core.RestaurantContainer;

class DayAdapter extends BaseAdapter {
    @SuppressWarnings("unused")
    private static final String TAG = "DayAdapter";
    private final LayoutInflater layoutInflater;
    private final LocalDate date;
    private final RestaurantContainer restaurantContainer;
    private List<Menu> menus;

    public DayAdapter(Context context, LocalDate date, RestaurantContainer restaurantContainer) {
        // Cache the LayoutInflate to avoid asking for a new one each time.
        this.layoutInflater = LayoutInflater.from(context);
        this.date = date;
        this.restaurantContainer = restaurantContainer;
        this.menus = null;
    }

    public void notifyDataSetChanged() {
        this.menus = this.restaurantContainer.getMenus(this.date);
        super.notifyDataSetChanged();
    }

    public int getCount() {
        if (menus == null) {
            return 0;
        }
        return menus.size();
    }

    public Object getItem(int position) {
        return menus.get(position);
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
            holder.tvMenu = (TextView) convertView.findViewById(R.id.txt_menu_naam);
            holder.tvDish = (TextView) convertView.findViewById(R.id.txt_gerecht);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Menu menu = (Menu) getItem(position);
        int color;
        try{
            color = Color.parseColor(menu.getColor());
        }catch(Exception e){
            color = Color.LTGRAY;
            Log.d(TAG, "Failed parsing menu color: " + e.toString());
        }
        holder.tvMenu.setText(menu.getName());
        holder.tvMenu.setBackgroundColor(color);
        holder.tvDish.setText(menu.getDish());

        return convertView;
    }

    static class ViewHolder {
        TextView tvMenu;
        TextView tvDish;
    }
}