package be.tjs.vubrestaurant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import be.tjs.vubrestaurant.core.Constants;

/**
 * The DropdownNavigationAdapter is used to allow the user to choose
 * which restaurant to display by selecting the spinner in the actionbar.
 * <p/>
 * Created by tjs on 09/10/14.
 */
class DropdownNavigationAdapter extends BaseAdapter {

    private final Context context;
    private final String[] data;
    private final LayoutInflater inflater;

    public DropdownNavigationAdapter(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.data = Constants.RESTAURANTS;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View actionBarView = inflater.inflate(R.layout.spinner_dropdown_result, null);
        TextView title = (TextView) actionBarView.findViewById(R.id.title);
        TextView subtitle = (TextView) actionBarView.findViewById(R.id.subtitle);
        title.setText(context.getResources().getString(R.string.app_name));
        subtitle.setText(data[position]);
        return actionBarView;

    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View actionBarDropDownView = inflater.inflate(R.layout.spinner_dropdown_item, null);
        TextView dropDownTitle = (TextView) actionBarDropDownView.findViewById(R.id.spinner_dropdown_item_text);
        dropDownTitle.setText(data[position]);
        return actionBarDropDownView;
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


}
