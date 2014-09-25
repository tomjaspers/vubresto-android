package be.tjs.vubrestaurant;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import java.util.ArrayList;

import be.tjs.vubrestaurant.core.MenuItem;
import be.tjs.vubrestaurant.core.RestaurantContainer;
import be.tjs.vubrestaurant.utils.AsyncTaskResult;

public class DayFragment extends Fragment {
    @SuppressWarnings("unused")
    private static final String TAG = "DayFragment";

    private static final String KEY_POSITION = "position";
    private static final String KEY_DATE = "date";

    private static RestaurantContainer restaurantContainer = RestaurantContainer.getInstance();

    private ProgressBar progressBar;
    private ListView contentListView;
    private View emptyView;
    private View errorView;
    private View weekendView;
    private DayAdapter dayAdapter;
    private LocalDate date;
    private Button btnTryAgain;

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
        weekendView = view.findViewById(R.id.weekendView);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        errorView = view.findViewById(R.id.errorView);
        emptyView = view.findViewById(R.id.emptyView);
        contentListView = (ListView) view.findViewById(R.id.content_listview);
        btnTryAgain = (Button) view.findViewById(R.id.try_again_button);

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
            dayAdapter = new DayAdapter(view.getContext(), new ArrayList<MenuItem>(), date);
            contentListView.setEmptyView(emptyView);
            contentListView.setAdapter(dayAdapter);

            btnTryAgain.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    showContent();
                }
            });

            // Call showContent to fill the listviews etc (if possible)
            showContent();
        }

        return view;
    }

    private void showContent() {
        // Hide the errorview
        errorView.setVisibility(View.GONE);
        if (restaurantContainer.hasMenuForDate(date)) {
            // We have the data, so let's show it
            // Hide the progress bar
            progressBar.setVisibility(View.GONE);
            // Show the list view
            emptyView.setVisibility(View.VISIBLE);
            contentListView.setVisibility(View.VISIBLE);
            // Notify the course adapter that the dataset has changed
            try {
                // Call the unsafe version to get an Exception in case something goes wrong, that we can handle with error content
                dayAdapter.notifyDataSetChangedUnsafe();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                showErrorContent();
            }
        } else {
            // We don't have data, so do a background call for it
            // Hide the list view
            emptyView.setVisibility(View.GONE);
            contentListView.setVisibility(View.GONE);
            // Show the progress bar
            progressBar.setVisibility(View.VISIBLE);

            // Make a new call for background task
            new LoadContentTask().execute(date);
        }
    }

    private void showErrorContent() {
        // Hide content stuff and show error stuff
        emptyView.setVisibility(View.GONE);
        contentListView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);
    }


    private class LoadContentTask extends AsyncTask<Object, Object, AsyncTaskResult<Boolean>> {

        @Override
        protected AsyncTaskResult<Boolean> doInBackground(Object... arg) {
            try {
                restaurantContainer.retrieveMenuItemsAndFill();
            } catch (Exception anyException) {
                return new AsyncTaskResult<Boolean>(anyException);
            }
            return new AsyncTaskResult<Boolean>(true);
        }

        protected void onPostExecute(AsyncTaskResult<Boolean> result) {
            if (result.getError() != null) {
                showErrorContent();
            } else if (isCancelled()) {
                // Request is cancelled: we (try) to show the content; if necessary this will
                // cause a new request (avoid asking user to try again or something)
                showContent();
            } else {
                // Successfull result
                showContent();
            }
        }
    }
}