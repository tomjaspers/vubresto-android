package be.tjs.vubrestaurant;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

public class DayFragment extends Fragment {
    @SuppressWarnings("unused")
    private static final String TAG = "DayFragment";

    private static final String KEY_POSITION = "position";
    private static final String KEY_DATE = "date";

    private ProgressBar progressBar;
    private ListView contentListView;
    private TextView emptyView;
    private View errorView;
    private DayAdapter dayAdapter;
    private LocalDate date;

    public static DayFragment newInstance(int position, LocalDate date) {
        DayFragment frag = new DayFragment();
        Bundle args = new Bundle();

        args.putInt(KEY_POSITION, position);
        args.putString(KEY_DATE, date.toString());
        frag.setArguments(args);

        return (frag);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.day_view, container, false);

        String dateString = getArguments().getString(KEY_DATE);

        // Initialize elements
        date = new LocalDate(dateString);
        int dayOfWeek = date.getDayOfWeek();

        // Initialize
        View weekendView = view.findViewById(R.id.weekendView);
        Button btnTryAgain = (Button) view.findViewById(R.id.try_again_button);

        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        errorView = view.findViewById(R.id.errorView);
        emptyView = (TextView) view.findViewById(R.id.emptyView);
        contentListView = (ListView) view.findViewById(R.id.content_listview);

        // Show static view during the weekend
        if (dayOfWeek == DateTimeConstants.SATURDAY || dayOfWeek == DateTimeConstants.SUNDAY) {
            // Show weekendView
            weekendView.setVisibility(View.VISIBLE);
            // Hide all the rest
            progressBar.setVisibility(View.GONE);
            errorView.setVisibility(View.GONE);
            emptyView.setVisibility(View.GONE);
            contentListView.setVisibility(View.GONE);
        } else {
            dayAdapter = new DayAdapter(view.getContext(), date, ((MainActivity) getActivity()).restaurantContainer);
            contentListView.setEmptyView(emptyView);
            contentListView.setAdapter(dayAdapter);

            btnTryAgain.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    ((MainActivity) getActivity()).loadContent();
                }
            });

            // Call showContent to fill the listviews etc (if possible)
            showContent();
        }

        return view;
    }

    private void showContent() {
        if (((MainActivity) getActivity()).isLoading()) {
            // Hide the errorview
            errorView.setVisibility(View.GONE);
            // Hide the list view
            emptyView.setVisibility(View.GONE);
            contentListView.setVisibility(View.GONE);
            // Show the progress bar
            progressBar.setVisibility(View.VISIBLE);
        } else {
            if (((MainActivity) getActivity()).restaurantContainer.hasDate(date)) {
                // Hide the errorview
                errorView.setVisibility(View.GONE);
                // Hide the progress bar
                progressBar.setVisibility(View.GONE);
                if(((MainActivity) getActivity()).restaurantContainer.hasMenus(date)){
                    emptyView.setText(getString(R.string.no_meals));
                } else {
                    emptyView.setText(getString(R.string.no_information));
                }
                // Show the list view
                emptyView.setVisibility(View.VISIBLE);
                contentListView.setVisibility(View.VISIBLE);
                // Notify the course adapter that the dataset has changed
                dayAdapter.notifyDataSetChanged();
            } else {
                // Hide content stuff and show error stuff
                emptyView.setVisibility(View.GONE);
                contentListView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                errorView.setVisibility(View.VISIBLE);
            }
        }
    }

}