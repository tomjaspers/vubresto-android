package be.tjs.vubrestaurant;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import be.tjs.vubrestaurant.core.Constants;
import be.tjs.vubrestaurant.core.Menu;

/**
 * The DropdownNavigationAdapter is used to allow the user to choose
 * which restaurant to display by selecting the spinner in the actionbar.
 * <p/>
 * Created by tjs on 09/10/14.
 */
class DropdownNavigationAdapter extends BaseAdapter {

    private final String[] data;
    private final LayoutInflater layoutInflater;

    public DropdownNavigationAdapter(Context context) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.data = Constants.RESTAURANTS;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Use ViewHolder pattern
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.spinner_dropdown_result, parent, false);
            holder = new ViewHolder();
            holder.tvRestaurant = (TextView) convertView.findViewById(R.id.subtitle);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvRestaurant.setText(data[position]);
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        // Use ViewHolder pattern
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.spinner_dropdown_item, parent, false);
            holder = new ViewHolder();
            holder.tvRestaurant = (TextView) convertView.findViewById(R.id.spinner_dropdown_item_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvRestaurant.setText(data[position]);
        return convertView;
    }

    @Override
    public int getCount() {
        return data.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    static class ViewHolder {
        TextView tvRestaurant;
    }

}
