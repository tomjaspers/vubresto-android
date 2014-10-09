package be.tjs.vubrestaurant;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.analytics.tracking.android.EasyTracker;

import org.joda.time.LocalDate;

import java.util.Locale;

import be.tjs.vubrestaurant.core.Constants;
import be.tjs.vubrestaurant.core.RestaurantContainer;


public class MainActivity extends ActionBarActivity {
    @SuppressWarnings("unused")
    private static final String TAG = "MainActivity";

    private static final String PREFS_FILENAME = "PreferencesVUBResto";
    private static final String PREFS_ACTIVE_RESTAURANT = "ActiveRestaurant";

    RestaurantContainer restaurantContainer;
    private DatesAdapter datesAdapter;
    private ViewPager viewPager;

    private int activeRestaurant;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Teal);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Restore preferences
        SharedPreferences preferences = getSharedPreferences(PREFS_FILENAME, MODE_PRIVATE);
        activeRestaurant = preferences.getInt(PREFS_ACTIVE_RESTAURANT, Constants.RESTO_ETTERBEEK);

        // Setup actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setLogo(R.drawable.ic_vubrestaurant);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        actionBar.setListNavigationCallbacks(
                new DropdownNavigationAdapter(this),
                new ActionBar.OnNavigationListener() {
                    @Override
                    public boolean onNavigationItemSelected(int position, long itemId) {
                        switchActiveRestaurant(position);
                        return true;
                    }
        });
        actionBar.setSelectedNavigationItem(activeRestaurant);

        // Select the correct language based on locale
        int language = Constants.LANG_EN;
        if (Locale.getDefault().getLanguage().equals("nl")) {
            language = Constants.LANG_NL;
        }
        restaurantContainer = new RestaurantContainer(this.activeRestaurant, language);
        datesAdapter = new DatesAdapter(getSupportFragmentManager(),restaurantContainer.getDates());

        // Initialize visual elements

        // Customize pagerTabStrip
        PagerTabStrip pagerTabStrip = (PagerTabStrip) findViewById(R.id.pagerTabStrip);
        pagerTabStrip.setTextColor(getResources().getColor(R.color.l_white));
        pagerTabStrip.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18.0f);
        pagerTabStrip.setTabIndicatorColor(getResources().getColor(R.color.l_dark_gray));

        // Set up the viewPager for first load (toggle visibility to hide the scroll to Today)
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(datesAdapter);
        viewPager.setVisibility(View.GONE);
        viewPager.setCurrentItem(0); // Today is always the first date (since 1.3.2)
        viewPager.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (restaurantContainer.isEmpty()) {
            datesAdapter.notifyDataSetChanged();
            loadContent();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle menu selection
        switch (item.getItemId()) {
            case R.id.action_today:
                scrollToToday();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Analytics
        EasyTracker.getInstance().activityStart(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Make sure we save the current active restaurant for next time
        SharedPreferences preferences = getSharedPreferences(PREFS_FILENAME, MODE_PRIVATE);
        preferences.edit().putInt(PREFS_ACTIVE_RESTAURANT, this.activeRestaurant).apply();
    }

    @Override
    public void onStop() {
        super.onStop();
        // Analytics
        EasyTracker.getInstance().activityStop(this);
    }

    private void switchActiveRestaurant(int restaurant){
        this.activeRestaurant = restaurant;
        restaurantContainer.setActiveRestaurant(restaurant);
        if(restaurantContainer.isEmpty()){
            loadContent();
        } else {
            datesAdapter.notifyDataSetChanged();
        }
    }

    private void scrollToToday() {
        // Today is always the first date (since 1.3.2)
        viewPager.setCurrentItem(0, true);
    }

    void loadContent(){
        new LoadContentTask().execute(this.activeRestaurant);
    }

    private class LoadContentTask extends AsyncTask<Object, Object, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            restaurantContainer.setLoading(true);
            datesAdapter.notifyDataSetChanged();
        }

        @Override
        protected Boolean doInBackground(Object... args) {
            try {
                restaurantContainer.retrieveMenuItemsAndFill();
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        @Override
        protected void onPostExecute(Boolean result) {
            restaurantContainer.setLoading(false);
            datesAdapter.notifyDataSetChanged();
            super.onPostExecute(result);
        }
    }

    private class DatesAdapter extends FragmentStatePagerAdapter {
        private final LocalDate[] dates;

        public DatesAdapter(FragmentManager fragmentManager, LocalDate[] dates) {
            super(fragmentManager);
            this.dates = dates;
        }

        @Override
        public int getCount() {
            return dates.length;
        }

        @Override
        public Fragment getItem(int position) {
            return (DayFragment.newInstance(position, dates[position]));
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public String getPageTitle(int position) {
            return dates[position].toString("E d MMM");
        }
    }
}
